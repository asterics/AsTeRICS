
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

package eu.asterics.component.processor.blinkchangedetector;

import java.util.LinkedList;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Detects eyes' blink starts and ends, based on events whether the eyes are
 * currently open or closed. It calculates blink duration and blink rate and
 * outputs them based on eyes' open/closed states, automatically in certain
 * temporal intervals or triggered via forwarding events. It does rate and
 * duration increase/decrease trend detection based on upper and lower
 * thresholds and additional properties.
 * 
 * @author Barbara Wakolbinger Date: 06/2018
 */
public class BlinkChangeDetectorInstance extends AbstractRuntimeComponentInstance {
	

	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opBlinkDurationMs = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opBlinkRateHz = new DefaultRuntimeOutputPort();
	

	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpBlinkRateIncreased = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBlinkRateDecreased = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBlinkDurationLonger = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBlinkDurationShorter = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBlinkStarts = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBlinkEnds = new DefaultRuntimeEventTriggererPort();
	
	
	/* ***************************************************************************
	 * Constants 
	 * ***************************************************************************/
	/**
	 * Default size for trend detection buffers (calculated rates and durations), 
	 * not the blinks/timestamps buffer.
	 */
	final int DEFAULT_BUFFER_SIZE = 10; 
	
	/**
	 * Maximum size for memory reasons. It is very unlikely that someone wants to use the blinks and period
	 * of several hours to detect a new blink rate.
	 * Anyway this is only relevant, if the timestamp buffer is not reduced by propRateObservationPeriodMinutes anyway.
	 * https://www.huffpost.com/entry/why-do-we-blink-so-much-mental-rest_n_2377720: avg. # of blinks during 1 hour = about 1200
	 * https://stackoverflow.com/questions/7632126/maximum-size-of-hashset-vector-linkedlist:  
	 * no actual maximum size of a linked list but maximum size measure: INTEGER_MAX; 
	 * 
	 * Meaningful size for this application: 3600 (3 hours)
	 */
	final int TIMESTAMPS_BUFFER_SIZE = 3600; // for rate trend detection
	
	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	
	/**
	 * Model developer / plugin user must set it to 0 to disable the rate and duration buffer which internally
	 * sets it back to 1 (there is actually a 1-element-list only storing the most recent calculation result).
	 * If the user sets it to a negative or non-integer value, this causes that the default DEFAULT_BUFFER_SIZE is used.
	 */
	int propTrendsBufferSize;
	int propRateObservationPeriodMinutes;
	int propOutputIntervalMs;
	int propThreshDurationHighMs;
	int propThreshDurationLowMs;
	double propThreshRateLowHz;
	double propThreshRateHighHz;
	int propHowManyDurationOutliers;
	int propHowManyRateOutliers;

	
	/* ***************************************************************************
	 * Member Variables and Constructors
	 * ***************************************************************************/
	private double timestampCloseStartedMs = 0;
	private LinkedList<Double> recentBlinkDurations = new LinkedList<>();
	
	/** 
	 * For calculating the current blink rate. Restricted by TIMESTAMPS_BUFFER_SIZE, if propRateObservationPeriodMinutes is not set. 
	 */
	private LinkedList<Double> recentBlinkStartTimestamps = new LinkedList<>(); 
	private LinkedList<Double> recentBlinkRates = new LinkedList<>(); // for calculating blink rate trends
	private double modelStartTimestampMs;
	private double countBlinksTotal = 0;
	private double lastDurationOutputTimestamp = 0;
	private double lastRateOutputTimestamp = 0;

	/**
	 * The class constructor.
	 */
	public BlinkChangeDetectorInstance() {
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
		return null;
	}

	/**
	 * Returns an Output Port.
	 * 
	 * @param portID the name of the port
	 * @return the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID) {
		if ("blinkDurationMs".equalsIgnoreCase(portID)) {
			return opBlinkDurationMs;
		}
		if ("blinkRateHz".equalsIgnoreCase(portID)) {
			return opBlinkRateHz;
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

		if ("eyesAreOrBecomeClosed".equalsIgnoreCase(eventPortID)) {
			return elpEyesAreOrBecomeClosed;
		}
		if ("eyesAreOrBecomeOpen".equalsIgnoreCase(eventPortID)) {
			return elpEyesAreOrBecomeOpen;
		}	
		if ("forwardCurrentBlinkDuration".equalsIgnoreCase(eventPortID)) {
			return elpForwardCurrentBlinkDuration;
		}		
		if ("forwardCurrentBlinkRate".equalsIgnoreCase(eventPortID)) {
			return elpForwardCurrentBlinkRate;
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
		if ("blinkRateIncreased".equalsIgnoreCase(eventPortID)) {
			return etpBlinkRateIncreased;
		}
		if ("blinkRateDecreased".equalsIgnoreCase(eventPortID)) {
			return etpBlinkRateDecreased;
		}
		if ("blinkDurationLonger".equalsIgnoreCase(eventPortID)) {
			return etpBlinkDurationLonger;
		}
		if ("blinkDurationShorter".equalsIgnoreCase(eventPortID)) {
			return etpBlinkDurationShorter;
		}
		if ("blinkStarts".equalsIgnoreCase(eventPortID)) {
			return etpBlinkStarts;
		}
		if ("blinkEnds".equalsIgnoreCase(eventPortID)) {
			return etpBlinkEnds;
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
		if ("trendsBufferSize".equalsIgnoreCase(propertyName)) {
			return propTrendsBufferSize;
		}
		if ("threshDurationHighMs".equalsIgnoreCase(propertyName)) {
			return propThreshDurationHighMs;
		}
		if ("threshDurationLowMs".equalsIgnoreCase(propertyName)) {
			return propThreshDurationLowMs;
		}
		if ("threshRateLowHz".equalsIgnoreCase(propertyName)) {
			return propThreshRateLowHz;
		}
		if ("threshRateHighHz".equalsIgnoreCase(propertyName)) {
			return propThreshRateHighHz;
		}
		if ("howManyDurationOutliers".equalsIgnoreCase(propertyName)) {
			return propHowManyDurationOutliers;
		}
		if ("howManyRateOutliers".equalsIgnoreCase(propertyName)) {
			return propHowManyRateOutliers;
		}
		if ("outputIntervalMs".equals(propertyName)) {
			return propOutputIntervalMs;
		}
		if ("rateObservationPeriodMinutes".equals(propertyName)) {
			return propRateObservationPeriodMinutes;
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
		Object oldValue = null;
		try {
			if ("trendsBufferSize".equalsIgnoreCase(propertyName)) {
				oldValue = propTrendsBufferSize;

				int newBufferSize = 1;
				try {
					newBufferSize = Integer.parseInt(newValue.toString());
				}catch(Exception e) {
					newBufferSize = DEFAULT_BUFFER_SIZE;
					AstericsErrorHandling.instance.getLogger().warning(
							"invalid property value for BufferSize! Must be an integer. Default "+ DEFAULT_BUFFER_SIZE+" is kept.");
				}
				
				if (newBufferSize > 0) {
					propTrendsBufferSize = newBufferSize;
				} else if (newBufferSize == 0) { // "disable" buffer which means there is exactly one value saved (1-element-list)
					propTrendsBufferSize = 1;
					AstericsErrorHandling.instance.getLogger().warning(
							"BufferSize is 0, i.e. buffer disabled! Size is set to 1 to allow detecting the current rate/duration as outlier/trend.");
				} else {
					propTrendsBufferSize = DEFAULT_BUFFER_SIZE;
					AstericsErrorHandling.instance.getLogger().warning(
							"invalid property value for BufferSize! Must be >=0. Default "+ DEFAULT_BUFFER_SIZE+" is kept.");
				}
			} else if ("threshDurationHighMs".equalsIgnoreCase(propertyName)) {
				oldValue = propThreshDurationHighMs;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propThreshDurationHighMs = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("High threshold for blink duration is 0 or negative and thus disabled!");
					propThreshDurationHighMs = -1;
				}
			} else if ("threshDurationLowMs".equalsIgnoreCase(propertyName)) {
				oldValue = propThreshDurationLowMs;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propThreshDurationLowMs = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("Low threshold for blink duration is 0 or negative and thus disabled!");
					propThreshDurationLowMs = -1;
				}
			} else if ("threshRateLowHz".equalsIgnoreCase(propertyName)) {
				oldValue = propThreshRateLowHz;
				double newValParsed = Double.parseDouble(newValue.toString());
				if (newValParsed > 0) {
					propThreshRateLowHz = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("Low threshold for blink rate is 0 or negative and thus disabled!");
					propThreshRateLowHz = -1;
				}
			} else if ("threshRateHighHz".equalsIgnoreCase(propertyName)) {
				oldValue = propThreshRateHighHz;
				double newValParsed = Double.parseDouble(newValue.toString());
				if (newValParsed > 0) {
					propThreshRateHighHz = Math.abs(newValParsed);
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("High threshold for blink rate is 0 or negative and thus disabled!");
					propThreshRateHighHz = -1;
				}
			} else if ("howManyDurationOutliers".equalsIgnoreCase(propertyName)) {
				oldValue = propHowManyDurationOutliers;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propHowManyDurationOutliers = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("property howManyDurationOutliers is 0 or negative and thus disabled!");
					propHowManyDurationOutliers = -1;
				}
			} else if ("howManyRateOutliers".equalsIgnoreCase(propertyName)) {
				oldValue = propHowManyRateOutliers;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propHowManyRateOutliers = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("property howManyRateOutliers is 0 ore negative and thus disabled!");
					propHowManyRateOutliers = -1;
				}
			} else if ("outputIntervalMs".equalsIgnoreCase(propertyName)) {
				oldValue = propOutputIntervalMs;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propOutputIntervalMs = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("property outputIntervalMs is 0 ore negative and thus disabled!");
					propOutputIntervalMs = -1;
				}
			} else if ("rateObservationPeriodMinutes".equalsIgnoreCase(propertyName)) {
				oldValue = propRateObservationPeriodMinutes;
				int newValParsed = Integer.parseInt(newValue.toString());
				if (newValParsed > 0) {
					propRateObservationPeriodMinutes = newValParsed;
				} else {
					AstericsErrorHandling.instance.getLogger()
							.warning("property rateObservationPeriod is 0 ore negative and thus disabled!");
					propRateObservationPeriodMinutes = -1;
				}
			}
			else {
				AstericsErrorHandling.instance.getLogger().warning("invalid property name: " + propertyName);
			}
		} catch (Exception e) {
			AstericsErrorHandling.instance.getLogger().warning("invalid property value! Check the types!");
		}
		
		return oldValue;
	}

	
	/* *****************************************************************************
	 * Event Listener Ports
	 * *****************************************************************************/
	/**
	 * Responsible for detecting blink starts, sending rate and duration outputs 
	 * (depending on intervals but also eyes' state) and initiating trend detection, if meaningful.
	 * 0ms-duration outputs are possible.
	 */
	final IRuntimeEventListenerPort elpEyesAreOrBecomeClosed = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			// Note: blink RATE is continuously changing as time progresses but only 
			// recalculated in certain conditions

			if (timestampCloseStartedMs == 0) { // actually a new blink starts; otherwise eyes simply continue to be
												// closed (not opened in between, triggerer/cause is unknown!)
				AstericsErrorHandling.instance.getLogger().info("NEXT BLINK STARTS");
				etpBlinkStarts.raiseEvent();
				countBlinksTotal++;
				timestampCloseStartedMs = System.currentTimeMillis();

				lastDurationOutputTimestamp = timestampCloseStartedMs;
				opBlinkDurationMs.sendData((ConversionUtils.doubleToBytes(0))); // always output here!

				recentBlinkStartTimestamps.add(timestampCloseStartedMs); 
				if(propRateObservationPeriodMinutes == -1) {
					if(recentBlinkStartTimestamps.size() > TIMESTAMPS_BUFFER_SIZE) {
						recentBlinkStartTimestamps.removeFirst();
					}
				} // otherwise, the size is checked in terms of recency (temporal validity) before calculating blink rate
				
				recalcAndResendBlinkRate(timestampCloseStartedMs); // updates lastRateOutputTimestamp
			} else { // eyes stay closed -> don't do checks, don't trigger blink start event but send current
					 // duration to port (if interval since last output was long enough)
				double timestampNow = System.currentTimeMillis();

				// continuous output enabled and interval reached or very first output
				if (propOutputIntervalMs > -1 && (lastDurationOutputTimestamp == 0
						|| (timestampNow - lastDurationOutputTimestamp) >= propOutputIntervalMs)) { 
					recalcAndResendBlinkDuration(timestampNow); // updates lastDurationOutputTimestamp
				}
				
				if (propOutputIntervalMs > -1 && (lastRateOutputTimestamp == 0
						|| (timestampNow - lastRateOutputTimestamp) >= propOutputIntervalMs)) {
					recalcAndResendBlinkRate(timestampNow); // updates lastRateOutputTimestamp
				}
			}
		}
	};

	/**
	 * Responsible for detecting blink ends, sending rate and duration outputs 
	 * (depending on intervals but also eyes' state) and initiating trend detection, if meaningful.
	 * 0ms-duration outputs are possible.
	 */
	final IRuntimeEventListenerPort elpEyesAreOrBecomeOpen = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			double timestampNow = System.currentTimeMillis();

			if (timestampCloseStartedMs > 0) { // = eyes have just opened. otherwise: eyes have been open already and
											   // still are (event triggerer/cause unknown)
				AstericsErrorHandling.instance.getLogger().info("BLINK just ENDED");
				etpBlinkEnds.raiseEvent();

				double timestampNowBlinkEndMs = System.currentTimeMillis();
				double newDuration = recalcAndResendBlinkDuration(timestampNowBlinkEndMs);
				timestampCloseStartedMs = 0; // only in this branch!

				detectBlinkDurationChange(newDuration); // can only change when a blink ends! call nowhere else! only
														// this method adds the duration to the list!
			} else {
				// continuous output enabled and interval passed by or the very first output
				if (propOutputIntervalMs > -1 && (lastDurationOutputTimestamp == 0
						|| (timestampNow - lastDurationOutputTimestamp) >= propOutputIntervalMs)) { 
					opBlinkDurationMs.sendData((ConversionUtils.doubleToBytes(0))); // no ongoing blink but nevertheless
																					// output
					lastDurationOutputTimestamp = timestampNow;
				}
			}

			// blink RATE conditions are independently from new ongoing or just finished blink
			// as rate anyway changes continuously (as milliseconds pass by)
			if (propOutputIntervalMs > -1 && (lastRateOutputTimestamp == 0
					|| (timestampNow - lastRateOutputTimestamp) >= propOutputIntervalMs)) {
				recalcAndResendBlinkRate(timestampNow); // updates lastRateOutputTimestamp
				AstericsErrorHandling.instance.getLogger().info("recalc blink rate");
			}
		}
	};

	/**
	 * Recalculates the blink duration (0 ms, if no ongoing blink) and sends it to the output port.
	 * 
	 * Does NOT do a trend analysis / detection and not update the
	 * lastDurationOutputTimestamp as this would interfere with regular output.
	 */
	final IRuntimeEventListenerPort elpForwardCurrentBlinkDuration = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			double now = System.currentTimeMillis();
			
			// currently eyes are closed (i.e. ongoing blink) BECAUSE there is a blink start timestamp available
			if (timestampCloseStartedMs > 0) { 
				double blinkDuration = now - timestampCloseStartedMs;
				opBlinkDurationMs.sendData((ConversionUtils.doubleToBytes(blinkDuration))); 
				// NOT: stringToBytes(blinkDuration+"")!!!
			} else {
				opBlinkDurationMs.sendData((ConversionUtils.doubleToBytes(0)));
				AstericsErrorHandling.instance.getLogger()
						.warning("currently no ongoing blink (eyes are open!) - sent duration 0 to output port");
			}
		}
	};

	/**
	 * Recalculates the blink rate via calling calculateBlinkRate(), which also 
	 * controls buffer size, and sends it to the output port.
	 * Outdated blinks are removed from the buffer with adaptBlinksToRateObservationPeriod(double) before calculation.
	 * 
	 * Does NOT do a trend analysis / detection and not update the
	 * lastRateOutputTimestamp as this would interfere with regular output.
	 */
	final IRuntimeEventListenerPort elpForwardCurrentBlinkRate = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().warning("forwarding blink rate ...");
			
			adaptBlinksToRateObservationPeriod(System.currentTimeMillis());
			double blinkRateRecalc = calculateBlinkRate();
			opBlinkRateHz.sendData(ConversionUtils.doubleToBytes(blinkRateRecalc));
		}
	};
	
	/* *****************************************************************************
	 * Private/Protected Logic implementation and helper methods.
	 * *****************************************************************************/
	/**
	 * Recalculates the blink duration but does NOT add the newly calculated duration to recentBlinkDurations. 
	 * Does not unset (to 0) timestampCloseStartedMs as this method can also be called during 
	 * an active blink (eyes still closed).
	 * Caller must take care of these 2 tasks!
	 * 
	 * @param timestampNow current timestamp in milliseconds, i.e. the current duration's end 
	 */
	private double recalcAndResendBlinkDuration(double timestampNow) {
		double blinkDurationMs = timestampNow - timestampCloseStartedMs; // difference between eyes closed timestamp and
																		 // eyes re-open timestamp

		lastDurationOutputTimestamp = timestampNow;
		opBlinkDurationMs.sendData((ConversionUtils.doubleToBytes(blinkDurationMs)));
		return blinkDurationMs;
	}

	/**
	 * Recalculates the blink rate and DOES add it to recentBlinkRates via calling calculateBlinkRate(), which also 
	 * controls buffer size.
	 * Outdated blinks are removed from the buffer with adaptBlinksToRateObservationPeriod(double) before calculation.
	 * Sends the rate to the output port and initiates trend detection.
	 * 
	 * @param timestampNow reference timestamp (now in milliseconds) to remove outdated blinks and for rate calculation
	 */
	private void recalcAndResendBlinkRate(double timestampNow) {
		// remove blinks that appeared before the observation period (if any is set)
		adaptBlinksToRateObservationPeriod(timestampNow);

		double currentBlinkRate = calculateBlinkRate(); // also adds the rate to the buffer (and controls buffer size)

		detectBlinkRateChange();
		lastRateOutputTimestamp = timestampNow;
		opBlinkRateHz.sendData((ConversionUtils.doubleToBytes(currentBlinkRate)));
	}

	/**
	 * Only affects blink rate calculation. Removes blinks from
	 * recentBlinkStartTimetamps that appeared before the observation period (if any
	 * is set, otherwise nothing happens). 
	 * 
	 * Updates countBlinksTotal (= number of blinks in observation period) and 
	 * modelStartTimestampMs (= start of the observation period, rather than the actual model start).
	 * 
	 * @param timestampNow the time (ms) from which propRateObservationPeriodMinutes minutes are subtracted 
	 * to get the oldest valid blink start when removing outdated blinks from the buffer
	 */
	private void adaptBlinksToRateObservationPeriod(double timestampNow) {
		if (propRateObservationPeriodMinutes > -1) {
			double rateObservationPeriodMs = propRateObservationPeriodMinutes * 60 * 1000;
			double oldestIncludedTimestamp = timestampNow - rateObservationPeriodMs;
			
			while (recentBlinkStartTimestamps.size() > 0) {
				if (recentBlinkStartTimestamps.getFirst() < oldestIncludedTimestamp) {
					recentBlinkStartTimestamps.removeFirst();
				} else {
					break; // chronologically ordered timestamps thus all following timestamps are
						   // definitely within the observation period
				}
			}
			
			countBlinksTotal = recentBlinkStartTimestamps.size();
			modelStartTimestampMs = oldestIncludedTimestamp;
		}
	}	

	/**
	 * Calculates the blink Rate since model start (or within the observation period), 
	 * updating as time passes by. No matter what the reason for this calculation is, 
	 * the new rate is stored into the buffer and the first one is removed, in case 
	 * the buffer size is exceeded. 
	 * Saving the rate into a buffer only when a new blink is detected does not make sense,
	 * as it updates with time passing, independently from eyes open/close events.
	 * 
	 * Attention:
	 * No rate is calculated and added to the list, and 0 is returned, if
	 * there has not been a blink yet or time since modelStartTimestampMs is 0.
	 * 
	 * Note: 
	 * A rate calculated at a certain point in time is always based on older but still valid (in terms of recency) blinks,
	 * older rates in the rate buffer can be based on no longer valid/saved blinks but for comparing rate changes this is perfectly ok.
	 * propRateObservationPeriodMinutes only refers to the timestamps and period used for the current rate calculation
	 * and shrinking the recentBlinkStartTimestamps.
	 * 
	 * @return the calculated blink rate, 0 if no blink so far (within observation period)
	 */
	protected double calculateBlinkRate() { //
		double now = System.currentTimeMillis();
		double deltaT = now - modelStartTimestampMs; // in ms
		double blinkRate = 0;
		if (countBlinksTotal < 1) {
			AstericsErrorHandling.instance.getLogger()
					.warning("could not calc blink rate as there was no blink up to now.");
		} else if (deltaT > 0) { // time range == 0 can happen at the very beginning! e.g. if eyes are closed at
							     // start or if there is a recalc of rate without eyes state changing -> could
								 // lead to infinitely rate
			double avgDurationPerBlink = deltaT / countBlinksTotal;
			blinkRate = (((double) 1) / avgDurationPerBlink) * 1000; // 1/ms * 1000 = 1Hz

			recentBlinkRates.add(blinkRate);

			if (recentBlinkRates.size() > propTrendsBufferSize) {
				recentBlinkRates.removeFirst();
			}
		} else {
			AstericsErrorHandling.instance.getLogger()
					.warning("could not calc blink rate due to time range between now and model start is 0.");
		}
		
		return blinkRate; // if 0: no blink within observation period
	}

	/**
	 * Does Rate trend detection based on threshold frequencies by which a rate can be seen as 
	 * (too) high or low.
	 * Usage Note: Does not re-calculate a rate or manipulate recentBlinkRates! Do this in advance, 
	 * BEFORE calling this method!
	 * 
	 * Increasing Trend = the most recent rate was above the upper rate threshold
	 * and x rates before are also above, and none below the low threshold
	 * frequency. 
	 * Decreasing Trend = the most recent rate was below the lower rate threshold 
	 * and x rates before are also below, and none above the low threshold
	 * frequency.
	 * 
	 * Neutral rates (i.e. those in the range between the thresholds) do not influence
	 * an ongoing trend but cannot create a new one - no detection is started,
	 * immediately returned without event being triggered. 
	 *  
	 * Trend detection starts at the most recent rate and goes through all rates in
	 * the buffer towards the oldest, but it is interrupted (as seen completed!) and
	 * a trend event is triggered, as soon as a number of propHowManyRateOutliers rates 
	 * in one direction (the outlier direction of the most recent rate) are detected.
	 * 
	 * A buffered previous rate below low threshold interrupts a rising trend detection 
	 * and no event is triggered.
	 * A buffered previous rate above high threshold interrupts a falling trend detection
	 * and no event is triggered.
	 * 
	 * There is a pseudo trend, which means propHowManyRateOutliers is 1 which means a trend
	 * event is triggered as soon as there was one outlier. If propHowManyRateOutliers is >1 no
	 * trend detection is started, if there are fewer than 2 buffered rates.
	 */
	protected void detectBlinkRateChange() {
		if (propHowManyRateOutliers <= 0) { // disabled
			AstericsErrorHandling.instance.getLogger()
					.warning("No trend detection executed as disabled via propHowManyRateOutliers set to "
							+ propHowManyRateOutliers + ".");
			return;
		}

		// special case, pseudo trends - not using more than one ("buffered") value
		if (propHowManyRateOutliers == 1 && recentBlinkRates.size() >= 1) { 
			double mostRecentRate = recentBlinkRates.get(recentBlinkRates.size() - 1); // == getFirst() == get(0)

			// == check enabled and threshold exceeded
			if (propThreshRateHighHz > -1 && mostRecentRate > propThreshRateHighHz) { 
				etpBlinkRateIncreased.raiseEvent();
				return;
			}

			// == check enabled and threshold undercut
			if (propThreshRateLowHz > -1 && mostRecentRate < propThreshRateLowHz) { 
				etpBlinkRateDecreased.raiseEvent();
				return;
			}
		}
		else if (recentBlinkRates.size() >= 2) { // no rate change / trend possible, if fewer than 2 measurements
			double mostRecentRate = recentBlinkRates.get(recentBlinkRates.size() - 1);

			if (propThreshRateHighHz > -1) { // == check enabled!
				int countRateOutliersAbove = 0;

				// most recent rate was above high thres, so there can only be an upwards trend
				if (mostRecentRate > propThreshRateHighHz) { 
					countRateOutliersAbove = 1;

					double rate;
					for (int i = recentBlinkRates.size() - 2; i >= 0; i--) {
						rate = recentBlinkRates.get(i);
						if (rate > propThreshRateHighHz) {
							countRateOutliersAbove++;
							if (countRateOutliersAbove >= propHowManyRateOutliers) {
								etpBlinkRateIncreased.raiseEvent();
								return;
							}
						} else if (propThreshRateLowHz > -1 && rate < propThreshRateLowHz) { 
							// trend interrupted, if detecting preceding low freq outlier 
							return;
						} // if rate is within range: continue with checking the rate before
					}
				}
			}

			if (propThreshRateLowHz > -1) { // == check enabled!
				int countRateOutliersBelow = 0;

				// most recent rate was below low thres, so there can only be a downwards trend
				if (mostRecentRate < propThreshDurationLowMs) { 
					countRateOutliersBelow = 1;

					double rate;
					for (int i = recentBlinkRates.size() - 2; i >= 0; i--) {
						rate = recentBlinkRates.get(i);
						if (rate < propThreshRateLowHz) {
							countRateOutliersBelow++;
							if (countRateOutliersBelow >= propHowManyRateOutliers) {
								etpBlinkRateDecreased.raiseEvent();
								return;
							}
						} else if (propThreshRateHighHz > -1 && rate > propThreshRateLowHz) { 
							// trend interrupted
							return;
						} // if rate is within range: continue with checking the rate before
					}
				}
			}
		}
		
		return; // without trend detection
	}

	/**
	 * Does duration trend detection. Adds the new blink duration to recentBlinkDurations and controls its size. 
	 * Usage Note: Don't manipulate recentBlinkDurations outside of this method!!! 
	 * Algorithm would not work any more! This difference is necessary as rate updates to non-0-values as time passes by, 
	 * whereas duration is only >0 for ongoing blinks and only those shall be put into the buffer but outputting 0ms-durations
	 * is meaningful and sometimes desired elsewhere, too.
	 * 
	 * Except from this detail, the algorithm works analogous to rate trend detection
	 * (using the corresponding properties for duration trend detection).
	 * 
	 * Pseudo trend also supported.
	 * 
	 * @param newBlinkDuration the duration to be put into the buffer and is the basis for trend direction
	 */
	protected void detectBlinkDurationChange(double newBlinkDuration) { 
		recentBlinkDurations.add(newBlinkDuration);
		if(recentBlinkDurations.size() > propTrendsBufferSize) {
			recentBlinkDurations.removeFirst();
		}

		if (propHowManyDurationOutliers <= 0) {
			AstericsErrorHandling.instance.getLogger()
					.warning("No trend detection executed as disabled via propHowManyDurationOutliers set to "
							+ propHowManyDurationOutliers + ".");
			return;
		}

		// special case, pseudo trends - not using more than one ("buffered") value
		if (propHowManyDurationOutliers == 1 && recentBlinkDurations.size() >= 1) { 
			double mostRecentDuration = recentBlinkDurations.get(recentBlinkDurations.size() - 1); // == getFirst()

			// == check enabled and threshold exceeded
			if (propThreshDurationHighMs > -1 && mostRecentDuration > propThreshDurationHighMs) { 
				etpBlinkDurationLonger.raiseEvent();
				return;
			}

			// == check enabled and threshold undercut
			if (propThreshDurationLowMs > -1 && mostRecentDuration < propThreshDurationLowMs) { 
				etpBlinkDurationShorter.raiseEvent();
				return;
			}
		}
		// no rate change / trend possible, if fewer than 2 measurements
		else if (recentBlinkDurations.size() >= 2) { 
			double mostRecentDuration = recentBlinkDurations.get(recentBlinkDurations.size() - 1);

			if (propThreshDurationHighMs > -1) { // == check enabled!
				int countDurationOutliersAbove = 0;

				// most recent duration was above upper threshold, so there can only be an upwards trend
				if (mostRecentDuration > propThreshDurationHighMs) { 
					countDurationOutliersAbove = 1;

					double duration;
					for (int i = recentBlinkDurations.size() - 2; i >= 0; i--) {
						duration = recentBlinkDurations.get(i);
						if (duration > propThreshDurationHighMs) {
							countDurationOutliersAbove++;
							if (countDurationOutliersAbove >= propHowManyDurationOutliers) {
								etpBlinkDurationLonger.raiseEvent();
								return;
							}
						} else if (propThreshDurationLowMs > -1 && duration < propThreshDurationLowMs) { 
							// trend interrupted
							return;							
						} // if duration is within range: continue with checking the duration before
					}
				}
			}

			if (propThreshDurationLowMs > -1) { // == check enabled!
				int countDurationOutliersBelow = 0;

				// most recent duration was below lower threshold, so there can only be a downwards trend
				if (mostRecentDuration < propThreshDurationLowMs) { 
					countDurationOutliersBelow = 1;

					double duration;
					for (int i = recentBlinkDurations.size() - 2; i >= 0; i--) {
						duration = recentBlinkDurations.get(i);
						if (duration < propThreshDurationLowMs) {
							countDurationOutliersBelow++;
							if (countDurationOutliersBelow >= propHowManyDurationOutliers) {
								etpBlinkDurationShorter.raiseEvent();
								return;
							}
						} else if (propThreshDurationHighMs > -1 && duration > propThreshDurationHighMs) { 
							// trend interrupted
							return;
						} // if duration is within range: continue with checking the duration before
					}
				}
			}
		}
		
		return; // without trend detection
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
		AstericsErrorHandling.instance.getLogger()
				.warning("Plugin start: Set modelStartTimestampMs to plugin start timestamp.");

		modelStartTimestampMs = System.currentTimeMillis();
		countBlinksTotal = 0;
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