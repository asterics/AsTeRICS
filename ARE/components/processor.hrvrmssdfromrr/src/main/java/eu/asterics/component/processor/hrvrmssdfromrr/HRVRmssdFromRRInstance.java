
/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.hrvrmssdfromrr;

import java.nio.ByteBuffer;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * @author Barbara Wakolbinger Date: 01/2019
 */
public class HRVRmssdFromRRInstance extends AbstractRuntimeComponentInstance {
	
	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opRmssd = new DefaultRuntimeOutputPort();
	

	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpRmssdCalculated = new DefaultRuntimeEventTriggererPort();
	

	/* ***************************************************************************
	 * Constants 
	 * ***************************************************************************/
	final static int DEFAULT_WINDOW_SIZE = 100;
	
	
	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	private int propRmssdWindowSize = DEFAULT_WINDOW_SIZE;	

	
	/* ***************************************************************************
	 * Member Variables and Constructors
	 * ***************************************************************************/
	
	/* changed at reset */

	private double sumRRIntervalSquaredDiffs = 0;
	private double[] rrIntervalsMs = new double[propRmssdWindowSize];
	private double[] rrIntervalsSquaredDiffs = new double[propRmssdWindowSize - 1];
	private int intervalsSinceLastReset = 0;

	/* unchanged at reset */

	private boolean calculationPaused = false;
	private boolean componentPaused = false;

	/**
	 * The class constructor.
	 */
	public HRVRmssdFromRRInstance() {
		AstericsErrorHandling.instance.getLogger().info("constructor RMSSD called");
	}

	
	/* ***************************************************************************
	 * Getters and Setters for Ports and Properties
	 * ***************************************************************************/
	/**
	 * Returns an Input Port.
	 * 
	 * @param portID the name of the port
	 * @return the input port or null if not found
	 */
	public IRuntimeInputPort getInputPort(String portID) {
		AstericsErrorHandling.instance.getLogger().info("getInputPort: " + portID);
		if ("rrInterval".equalsIgnoreCase(portID)) {
			return ipRrInterval;
		}

		return null;
	}

	/**
	 * Returns an Output Port.
	 * 
	 * @param portID the name of the port
	 * @return the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID) {
		AstericsErrorHandling.instance.getLogger().info("getOutputPort: " + portID);
		if ("rmssd".equalsIgnoreCase(portID)) {
			return opRmssd;
		}

		return null;
	}

	/**
	 * Returns an Event Listener Port.
	 * 
	 * @param eventPortID the name of the port
	 * @return the EventListener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
		AstericsErrorHandling.instance.getLogger().info("getEventListenerPort: " + eventPortID);
		if ("resetCalculation".equalsIgnoreCase(eventPortID)) {
			return elpResetCalculation;
		}
		if ("pauseCalculation".equalsIgnoreCase(eventPortID)) {
			return elpPauseCalculation;
		}
		if ("pauseComponent".equalsIgnoreCase(eventPortID)) {
			return elpPauseComponent;
		}
		if ("continueCalculation".equalsIgnoreCase(eventPortID)) {
			return elpContinueCalculation;
		}
		if ("continueComponent".equalsIgnoreCase(eventPortID)) {
			return elpContinueComponent;
		}

		return null;
	}

	/**
	 * Returns an Event Triggerer Port.
	 * 
	 * @param eventPortID the name of the port
	 * @return the EventTriggerer port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
		if ("rmssdRecalculated".equalsIgnoreCase(eventPortID)) {
			return etpRmssdCalculated;
		}

		return null;
	}

	/**
	 * Returns the value of the given property.
	 * 
	 * @param propertyName the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("rmssdWindowSize".equalsIgnoreCase(propertyName)) {
			return propRmssdWindowSize;
		}

		return null;
	}

	/**
	 * Sets a new value for the given property.
	 * 
	 * @param propertyName the name of the property
	 * @param newValue the desired property value or null if not found
	 * @return the old property value or null if property name was not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		// setter only callable before starting the model thus only needs to reset
		// rrIntervalMs and rrIntervalsSquaredDiffs arrays additionally
		if ("rmssdWindowSize".equalsIgnoreCase(propertyName)) {
			final Object oldValue = (Integer) propRmssdWindowSize;
			int parsedNewValue;
			if (newValue instanceof Integer) {
				parsedNewValue = (Integer) newValue;
			} else if (newValue instanceof String) {
				try {
					parsedNewValue = Integer.parseInt((String) newValue);
				} catch (NumberFormatException e) {
					parsedNewValue = -1;
				}
			} else {
				parsedNewValue = -1;
				AstericsErrorHandling.instance.getLogger()
						.info("RMSSD Window Size expects integer but found: " + newValue + " of type "
								+ newValue.getClass() + "! Keeping default value: " + propRmssdWindowSize);
			}

			if (((Integer) parsedNewValue) > 1) {
				propRmssdWindowSize = (Integer) parsedNewValue;
			} else {
				AstericsErrorHandling.instance.getLogger()
						.info("ERROR: propRmssdWindowSize must be > 1! It is now set to " + DEFAULT_WINDOW_SIZE);
				propRmssdWindowSize = DEFAULT_WINDOW_SIZE;
			}
			rrIntervalsMs = new double[propRmssdWindowSize];
			rrIntervalsSquaredDiffs = new double[propRmssdWindowSize - 1];
			return oldValue;

		}
		return null;
	}

	
	/* ***************************************************************************
	 * Input Ports for receiving values.
	 * ***************************************************************************/
	private final IRuntimeInputPort ipRrInterval = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			AstericsErrorHandling.instance.getLogger().info("how to receiveData()");

			// extra-safety as normally an inputPort's receiveData() is not called if
			// nothing is actually there as input
			if (data.length > 0) {
				double dataValue = byteArrayToDouble(data);
				AstericsErrorHandling.instance.getLogger().info("received: " + dataValue);

				if (!componentPaused) {
					rrIntervalsMs[intervalsSinceLastReset % propRmssdWindowSize] = dataValue;
					recalcRmssd();
					intervalsSinceLastReset++;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.info("component paused! no RR samples collection and no calculation!");
				}
			}
		}
	};


	/* *****************************************************************************
	 * Event Listener Ports.
	 * *****************************************************************************/
	final IRuntimeEventListenerPort elpResetCalculation = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("receiveEvent reset " + data);
			resetFields();

			// don't change started/running && paused states
		}
	};

	final IRuntimeEventListenerPort elpPauseCalculation = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("receiveEvent pause calc " + data);
			calculationPaused = true;
		}
	};

	final IRuntimeEventListenerPort elpPauseComponent = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("receiveEvent pause component " + data);
			componentPaused = true;
		}
	};

	final IRuntimeEventListenerPort elpContinueCalculation = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("receiveEvent continue calc " + data);
			calculationPaused = false;
		}
	};

	final IRuntimeEventListenerPort elpContinueComponent = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("receiveEvent continue component " + data);
			componentPaused = false;
		}
	};

	private static double byteArrayToDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	
	/* *****************************************************************************
	 * Private Logic implementation and helper methods.
	 * *****************************************************************************/
	/**
	 * Uses the formula explained on
	 * https://hrv-herzratenvariabilität.de/2017/10/berechnung-des-hrv-werts-rmssd/
	 * for calculating the RMSSD, considering the R-R intervals currently in the sliding window. 
	 */
	private void recalcRmssd() {
		AstericsErrorHandling.instance.getLogger().info("recalcRmssd() is called");
		AstericsErrorHandling.instance.getLogger().info("# of rr intervals in total: " + intervalsSinceLastReset);
		if (componentPaused) {
			AstericsErrorHandling.instance.getLogger().info("RMSSD NOT recalculated. Paused component ...");
		} else if (intervalsSinceLastReset >= propRmssdWindowSize) {
			// intervalsSinceLastReset count continues while still collecting samples thus
			// also update sum and sum array for paused calculation (not for paused
			// component)

			double squaredDiff = 0;
			int currentIntervalIndex = (intervalsSinceLastReset - 1) % propRmssdWindowSize;
			int currentSquareSumIndex;
			if (currentIntervalIndex > 0) {
				squaredDiff = rrIntervalsMs[currentIntervalIndex] - rrIntervalsMs[currentIntervalIndex - 1];
				currentSquareSumIndex = currentIntervalIndex - 1;
				AstericsErrorHandling.instance.getLogger()
						.info("rr[mostRecent-1]: " + rrIntervalsMs[currentIntervalIndex - 1]);
			} else {
				// due to sliding window, the most recent RR interval has array index 0, the one
				// before is the last element of the array
				squaredDiff = rrIntervalsMs[0] - rrIntervalsMs[propRmssdWindowSize - 1];
				currentSquareSumIndex = propRmssdWindowSize - 2; // = sumRRIntervalSquaredDiffs.length()-1
				AstericsErrorHandling.instance.getLogger()
						.info("rr[mostRecent-1]: " + rrIntervalsMs[propRmssdWindowSize - 1]);
			}

			AstericsErrorHandling.instance.getLogger().info("rr[mostRecent]: " + rrIntervalsMs[currentIntervalIndex]);

			squaredDiff = squaredDiff * squaredDiff;

			// replace the squaredDifference no longer in the sliding window and update the
			// sum of all squared diffs still in the window
			sumRRIntervalSquaredDiffs -= rrIntervalsSquaredDiffs[currentSquareSumIndex];
			sumRRIntervalSquaredDiffs += squaredDiff;
			rrIntervalsSquaredDiffs[currentSquareSumIndex] = squaredDiff;

			if (calculationPaused) {
				AstericsErrorHandling.instance.getLogger().info(
						"RMSSD NOT recalculated. Paused calculation ... (only updated squareSum and squareSumArray)");
			} else {
				AstericsErrorHandling.instance.getLogger().info("RMSSD is recalculated");

				// = div by: sumRRIntervalSquaredDiffs.length(), i.e. # of squaredDiffs
				double squaredRmssd = sumRRIntervalSquaredDiffs / (propRmssdWindowSize - 1);
				double rmssd = Math.sqrt(squaredRmssd);
				opRmssd.sendData(ConversionUtils.doubleToBytes(rmssd));
				AstericsErrorHandling.instance.getLogger()
						.info("sum of squared rr interval diffs up to now: " + sumRRIntervalSquaredDiffs);
				AstericsErrorHandling.instance.getLogger().info("rmssd: " + rmssd);
			}
		} else {
			AstericsErrorHandling.instance.getLogger().info("RMSSD cannot be recalculated. Now # = "
					+ intervalsSinceLastReset + "Waiting for more RR interval samples ...");
		}
	}

	private void resetFields() {
		rrIntervalsMs = new double[propRmssdWindowSize];
		rrIntervalsSquaredDiffs = new double[propRmssdWindowSize - 1];
		intervalsSinceLastReset = 0;
		sumRRIntervalSquaredDiffs = 0;
	}

	
	/* ***************************************************************************
	 *  Plugin Lifecycle methods
	 * ***************************************************************************/
	/**
	 * Called when model is started.
	 */
	@Override
	public void start() {

		super.start();
	}

	/**
	 * Called when model is paused.
	 */
	@Override
	public void pause() {
		super.pause();
	}

	/**
	 * Called when model is resumed.
	 */
	@Override
	public void resume() {
		super.resume();
	}

	/**
	 * Called when model is stopped.
	 */
	@Override
	public void stop() {
		super.stop();
	}
}