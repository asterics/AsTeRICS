
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

package eu.asterics.component.processor.timestampwriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
 * Stores the time (in milliseconds) that passed since the last reset (or
 * start). Outputs are the passed milliseconds (also formatted) and UNIX epoch
 * timestamp in milliseconds and formatted.
 * 
 * @author Barbara Wakolbinger Date: 2018/03/26
 */
public class TimestampWriterInstance extends AbstractRuntimeComponentInstance {
	
	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opTimePassedMs = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opTimePassedFormatted = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opTimestampUnixMs = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opTimestampUnixFormatted = new DefaultRuntimeOutputPort();

	
	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpTimestampFormatted = new DefaultRuntimeEventTriggererPort();

	
	/* ***************************************************************************
	 * Constants 
	 * ***************************************************************************/
	private final String DEFAULT_TIMESTAMP_FORMAT = "dd.MM.yyyy-HH:mm:ss.SSS";
	private final String DEFAULT_TIME_DIFF_FORMAT = "HH:mm:ss.SSS";

	
	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	private String propTimestampFormat = DEFAULT_TIMESTAMP_FORMAT;
	private String propDiffTimeFormat = DEFAULT_TIME_DIFF_FORMAT;
	

	/* ***************************************************************************
	 * Member Variables and Constructors
	 * ***************************************************************************/
	private double timestampLastReset = 0;
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(propTimestampFormat);
	private SimpleDateFormat timeFormatter = new SimpleDateFormat(propDiffTimeFormat); // UTC!

	/**
	 * The class constructor.
	 */
	public TimestampWriterInstance() {
		dateTimeFormatter.setTimeZone(TimeZone.getDefault());
		// System.getCurrentTimeMillis() is always UTC. incorrect hour component when
		// formatted via SimpleDateFormat and default time zone!
		timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
		if ("timePassedMs".equalsIgnoreCase(portID)) {
			return opTimePassedMs;
		}
		if ("timePassedFormatted".equalsIgnoreCase(portID)) {
			return opTimePassedFormatted;
		}
		if ("timestampUnixMs".equalsIgnoreCase(portID)) {
			return opTimestampUnixMs;
		}
		if ("timestampUnixFormatted".equalsIgnoreCase(portID)) {
			return opTimestampUnixFormatted;
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
		if ("timestampFormatted".equalsIgnoreCase(eventPortID)) {
			return etpTimestampFormatted;
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
		if ("resetStartTimestamp".equalsIgnoreCase(eventPortID)) {
			return elpResetStartTimestamp;
		}
		if ("sendOutputs".equalsIgnoreCase(eventPortID)) {
			return elpSendOutputs;
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
		if ("timestampFormat".equalsIgnoreCase(propertyName)) {
			return propTimestampFormat;
		}
		if ("diffTimeFormat".equalsIgnoreCase(propertyName)) {
			return propDiffTimeFormat;
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
		if ("timestampFormat".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propTimestampFormat;
			propTimestampFormat = (String) newValue;
			try {
				dateTimeFormatter = new SimpleDateFormat(propTimestampFormat);
			} catch (IllegalArgumentException | NullPointerException e) {
				dateTimeFormatter = new SimpleDateFormat(DEFAULT_TIMESTAMP_FORMAT);
				propTimestampFormat = DEFAULT_TIMESTAMP_FORMAT;
				AstericsErrorHandling.instance.getLogger().info("invalid output format: " + newValue
						+ " ! Reset to default format: " + DEFAULT_TIMESTAMP_FORMAT);
			}
			dateTimeFormatter.setTimeZone(TimeZone.getDefault());
			return oldValue;
		}

		if ("diffTimeFormat".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propDiffTimeFormat;
			propDiffTimeFormat = (String) newValue;
			try {
				timeFormatter = new SimpleDateFormat(propDiffTimeFormat);
			} catch (IllegalArgumentException | NullPointerException e) {
				timeFormatter = new SimpleDateFormat(DEFAULT_TIME_DIFF_FORMAT);
				propDiffTimeFormat = DEFAULT_TIME_DIFF_FORMAT;
				AstericsErrorHandling.instance.getLogger().info("invalid output format: " + newValue
						+ " ! Reset to default format: " + DEFAULT_TIME_DIFF_FORMAT);
			}
			timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			return oldValue;
		}

		return null;
	}

	
	/* *****************************************************************************
	 * Event Listener Ports
	 * *****************************************************************************/
	final IRuntimeEventListenerPort elpResetStartTimestamp = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			timestampLastReset = System.currentTimeMillis();
		}
	};
	
	final IRuntimeEventListenerPort elpSendOutputs = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			double now = System.currentTimeMillis();

			AstericsErrorHandling.instance.getLogger().info("now [ms]: " + now);
			AstericsErrorHandling.instance.getLogger().info("last reset [ms]: " + timestampLastReset);
			
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTimeInMillis((long) now);

			Calendar calStart = Calendar.getInstance();
			calStart.setTimeInMillis((long) timestampLastReset);

			double differenceSinceReset = now - timestampLastReset;
			AstericsErrorHandling.instance.getLogger().info("now - lastReset (subtract): " + differenceSinceReset);

			Calendar firstDateEver = Calendar.getInstance();
			firstDateEver.setTimeInMillis(0);
			AstericsErrorHandling.instance.getLogger().info("1.1.1970: ");
			AstericsErrorHandling.instance.getLogger().info(dateTimeFormatter.format(firstDateEver.getTime()));

			Calendar dateDiff = Calendar.getInstance();
			dateDiff.setTimeInMillis((long) differenceSinceReset); // 0+differenceSinceReset

			AstericsErrorHandling.instance.getLogger()
					.info("time diff since comp. reset " + timeFormatter.format(dateDiff.getTime()));

			String formattedTimeDiff = timeFormatter.format(dateDiff.getTime());
			opTimePassedFormatted.sendData((ConversionUtils.stringToBytes(formattedTimeDiff)));
			// careful with stringToBytes(<doubleValue>+"") as toString (+"") might make it unconvertible
			opTimePassedMs.sendData((ConversionUtils.doubleToBytes(differenceSinceReset))); 

			String formattedUnixTime = dateTimeFormatter.format(calEnd.getTime());
			opTimestampUnixFormatted.sendData((ConversionUtils.stringToBytes(formattedUnixTime)));
			opTimestampUnixMs.sendData((ConversionUtils.doubleToBytes(now))); // NOT: stringToBytes(now+"")!!!
			etpTimestampFormatted.raiseEvent();
		}
	};
	

	/* ***************************************************************************
	 *  Plugin Lifecycle methods
	 * ***************************************************************************/
	/**
	 * Called when model is started.
	 */
	@Override
	public void start() {
		timestampLastReset = System.currentTimeMillis();
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