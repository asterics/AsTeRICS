
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

package eu.asterics.component.processor.datetodouble;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
 * 
 * Converts a date string to a Unix timestamp [ms], using the default date zone
 * and a property-defined date format.
 * 
 * @author Barbara Wakolbinger Date: 2018/04/01
 */
public class DateToDoubleInstance extends AbstractRuntimeComponentInstance {
	
	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opTimestampUnixMs = new DefaultRuntimeOutputPort();
	

	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpDateFormatted = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpConversionFailed = new DefaultRuntimeEventTriggererPort();
	

	/* ***************************************************************************
	 * Constants 
	 * ***************************************************************************/
	private final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy-HH:mm:ss.SSS";
	

	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	private String propDateFormat = DEFAULT_DATE_FORMAT;
	

	/* ***************************************************************************
	 * Member Variables and Constructors
	 * ***************************************************************************/
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(propDateFormat);
	private String dateStringInput = "";

	/**
	 * The class constructor.
	 */
	public DateToDoubleInstance() {
		dateTimeFormatter.setTimeZone(TimeZone.getDefault());
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
		if ("dateString".equalsIgnoreCase(portID)) {
			return ipDateString;
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

		if ("timestampUnixMs".equalsIgnoreCase(portID)) {
			return opTimestampUnixMs;
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
		if ("dateFormatted".equalsIgnoreCase(eventPortID)) {
			return etpDateFormatted;
		}
		if ("conversionFailed".equalsIgnoreCase(eventPortID)) {
			return etpConversionFailed;
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
		if ("formatInput".equalsIgnoreCase(eventPortID)) {
			return elpFormatInput;
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
		if ("dateFormat".equalsIgnoreCase(propertyName)) {
			return propDateFormat;
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
		if ("dateFormat".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propDateFormat;
			propDateFormat = (String) newValue;
			try {
				dateTimeFormatter = new SimpleDateFormat(propDateFormat);
			} catch (IllegalArgumentException | NullPointerException e) {
				dateTimeFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
				propDateFormat = DEFAULT_DATE_FORMAT;
				AstericsErrorHandling.instance.getLogger().info(
						"invalid output format: " + newValue + " ! Reset to default format: " + DEFAULT_DATE_FORMAT);
			}
			dateTimeFormatter.setTimeZone(TimeZone.getDefault());
			return oldValue;
		}

		return null;
	}
	
	
	/* ***************************************************************************
	 * Input Ports for receiving values
	 * ***************************************************************************/
	private final IRuntimeInputPort ipDateString = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			dateStringInput = ConversionUtils.stringFromBytes(data);
			elpFormatInput.receiveEvent(null);
		}
	};
	
	
	/* *****************************************************************************
	 * Event Listener Ports
	 *******************************************************************************/
	final IRuntimeEventListenerPort elpFormatInput = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("try to convert date " + dateStringInput);

			if (dateStringInput == null || dateStringInput.length() == 0) {
				AstericsErrorHandling.instance.getLogger().info("date string is null/empty - no conversion!");
				etpConversionFailed.raiseEvent();
			} else {
				try {
					dateTimeFormatter.setLenient(false);// insist on strict format!
					double unixMillis = dateTimeFormatter.parse(dateStringInput).getTime();
					opTimestampUnixMs.sendData(ConversionUtils.doubleToBytes((unixMillis)));
					AstericsErrorHandling.instance.getLogger().info("converted to: " + unixMillis);
					etpDateFormatted.raiseEvent();
				} catch (Exception e) {
					etpConversionFailed.raiseEvent();
					AstericsErrorHandling.instance.getLogger()
							.info("conversion failed with format " + dateTimeFormatter.toPattern());
				}
			}
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