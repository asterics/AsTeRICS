
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

package eu.asterics.component.processor.jsonparser;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
 * Parses a string in JSON format from an input port in order to read and send
 * JSON field values to an output port.
 * 
 * @author Barbara Wakolbinger Date: 13.02.2019
 */
public class JsonParserInstance extends AbstractRuntimeComponentInstance {
	
	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opJsonFieldValue = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opError = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opLatestReadFieldName = new DefaultRuntimeOutputPort();
	

	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpInputParsed = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpFieldValueAvailable = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpFieldValueNotFound = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpFieldValueInvalidFormat = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpInvalidInputFormat = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpMissingJsonInput = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpMissingFieldName = new DefaultRuntimeEventTriggererPort();
	

	/* ***************************************************************************
	 * Constants (e.g. for Error Messages)
	 * ***************************************************************************/
	final static String MISSING_JSON_INPUT = "No input at port jsonInputString.";
	final static String MISSING_FIELD_NAME = "No input at port jsonFieldName.";

	/**
	 * To avoid this error: trigger parseInput at least once (do so again with a valid
	 * JSON input string, if property keepParsedObject is false)!
	 */
	final static String MISSING_JSON_OBJECT = "No parsed json object available.";
	final static String INVALID_FIELD_NAME = "Field with name <> could not be found in parsed json object.";
	final static String INVALID_FIELD_VALUE = "Field with name <> found but has an invalid value.";
	final static String INVALID_JSON_INPUT = "jsonInputString has an invalid format and cannot be parsed.";
	
	
	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	boolean propKeepParsedObject = true;

	
	/* ***************************************************************************
	 * Member Variables and Constructors
	 * ***************************************************************************/
	private String jsonStringIn = "";
	private String jsonFieldNameIn = "";

	/**
	 * Even if propKeepParsedObject is true, there is NO unset of latestParsedObject
	 * in case readNextField failed (for any reason!), i.e. a parsed JSON object is
	 * only rejected after successful usage, after being replaced by a new JSON
	 * input string or when forced via rejectParsedObject event.
	 */
	private JSONObject latestParsedObject = null;

	/**
	 * Set whenever elpReadNextField receives an event (i.e. attempt of reading a
	 * JSON field). set to "" if jsonFieldNameIn was missing at that time, otherwise
	 * to jsonFieldNameIn (also if jsonStringIn was missing or field not found).
	 * i.e. it always represents the name of the field from the last reading attempt
	 * - usable for error messages.
	 */
	private String latestReadFieldName = "";

	/**
	 * The class constructor.
	 */
	public JsonParserInstance() {
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
		if ("jsonInputString".equalsIgnoreCase(portID)) {
			return ipJsonInputString;
		}
		if ("jsonFieldName".equalsIgnoreCase(portID)) {
			return ipJsonFieldName;
		}
		if ("keepParsedObject".equalsIgnoreCase(portID)) {
			return ipKeepParsedObject;
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
		if ("jsonFieldValue".equalsIgnoreCase(portID)) {
			return opJsonFieldValue;
		}
		if ("error".equalsIgnoreCase(portID)) {
			return opError;
		}
		if ("latestReadFieldName".equals(portID)) {
			return opLatestReadFieldName;
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
		if ("parseInput".equalsIgnoreCase(eventPortID)) {
			return elpParseInput;
		}
		if ("readNextField".equalsIgnoreCase(eventPortID)) {
			return elpReadNextField;
		}
		if ("rejectParsedObject".equalsIgnoreCase(eventPortID)) {
			return elpRejectParsedObject;
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
		if ("inputParsed".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'input parsed'");
			return etpInputParsed;
		}
		if ("fieldValueAvailable".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'field value available'");
			return etpFieldValueAvailable;
		}
		if ("fieldValueNotFound".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'field value not found'");
			return etpFieldValueNotFound;
		}
		if ("fieldValueInvalidFormat".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'invalid field value format'");
			return etpFieldValueInvalidFormat;
		}
		if ("invalidInputFormat".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'invalid input format'");
			return etpInvalidInputFormat;
		}
		if ("missingJsonInput".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'missing json input'");
			return etpMissingJsonInput;
		}
		if ("missingFieldName".equalsIgnoreCase(eventPortID)) {
			AstericsErrorHandling.instance.getLogger().info("init/get trigger 'missing field name'");
			return etpMissingFieldName;
		}

		AstericsErrorHandling.instance.getLogger().info("init/get trigger unknown event trigger");
		return null;
	}

	/**
	 * Returns the value of the given property.
	 * 
	 * @param propertyName the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("keepParsedObject".equalsIgnoreCase(propertyName)) {
			return propKeepParsedObject;
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
		if ("keepParsedObject".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propKeepParsedObject;
			AstericsErrorHandling.instance.getLogger()
					.info("new value for keepParsedObject: " + newValue + " of type " + newValue.getClass());
			if ("true".equalsIgnoreCase((String) newValue)) {
				AstericsErrorHandling.instance.getLogger().info("set property keepParsedObject to true");
				propKeepParsedObject = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				AstericsErrorHandling.instance.getLogger().info("set property keepParsedObject to false");
				propKeepParsedObject = false;
			}
			AstericsErrorHandling.instance.getLogger().info("cannot set property keepParsedObject - unknown value");
			return oldValue;
		}

		return null;
	}

	
	/* ***************************************************************************
	 * Input Ports for receiving values
	 * ***************************************************************************/
	private final IRuntimeInputPort ipJsonInputString = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			jsonStringIn = ConversionUtils.stringFromBytes(data);
		}
	};

	private final IRuntimeInputPort ipJsonFieldName = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			jsonFieldNameIn = ConversionUtils.stringFromBytes(data);
		}
	};

	private final IRuntimeInputPort ipKeepParsedObject = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			if (data.length > 1) {
				AstericsErrorHandling.instance.getLogger()
						.info("invalid value for input port keepParsedObject! Use 1 byte input!");
			} else if (data.length > 0) {
				propKeepParsedObject = ConversionUtils.booleanFromByte(data[0]);
				AstericsErrorHandling.instance.getLogger()
						.info("set property keepParsedObject via input port to " + propKeepParsedObject);
			} else {
				AstericsErrorHandling.instance.getLogger()
						.info("property keepParsedObject NOT set via input port due to missing value.");
			}
		}
	};

	
	/* *****************************************************************************
	 * Event Listener Ports
	 * *****************************************************************************/
	final IRuntimeEventListenerPort elpRejectParsedObject = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("FORCE reject parsed json object via event listener port!");
			latestParsedObject = null;
		}
	};

	final IRuntimeEventListenerPort elpParseInput = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			if (jsonStringIn == null || jsonStringIn.length() == 0) {
				opError.sendData(ConversionUtils.stringToBytes(MISSING_JSON_INPUT));
				etpMissingJsonInput.raiseEvent();
			} else {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) parser.parse(jsonStringIn);
				} catch (ParseException | ClassCastException e) {
					opError.sendData(ConversionUtils.stringToBytes(INVALID_JSON_INPUT));
					etpInvalidInputFormat.raiseEvent();
				}
				latestParsedObject = jsonObject;
				opError.sendData(ConversionUtils.stringToBytes("")); // "unset" error message
				etpInputParsed.raiseEvent();
			}
		}
	};

	/**
	 * Implementation Details: 
	 * null (as a valid value of a JSON field) can't be sent to an output port of type String. options: 
	 * 1) send "null" (will potentially need extra conversion/handling afterwards but valid null values are detected) 
	 * and trigger etpFieldValueAvailable
	 * 2) don't send anything to output port and don't trigger etpFieldValueAvailable
	 * (problem: not clear if field/key was non-existent or value was a valid null)!
	 * -> choice of option 1) as easier to detect the null-value with following plugins
	 * 
	 * "" (as a valid JSON field value) does not need special handling. "" is sent to output port and etpFieldValueAvailable triggered
	 */
	final IRuntimeEventListenerPort elpReadNextField = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			AstericsErrorHandling.instance.getLogger().info("received readNextField for field name " + jsonFieldNameIn);
			if (jsonFieldNameIn == null || jsonFieldNameIn.length() == 0) {
				latestReadFieldName = "";
				opError.sendData(ConversionUtils.stringToBytes(MISSING_FIELD_NAME));
				opLatestReadFieldName.sendData(ConversionUtils.stringToBytes(latestReadFieldName));
				opJsonFieldValue.sendData(ConversionUtils.stringToBytes(""));
				etpMissingFieldName.raiseEvent();
			} else if (latestParsedObject == null) {
				AstericsErrorHandling.instance.getLogger()
						.info("json object not found (not yet parsed) " + latestParsedObject);
				latestReadFieldName = jsonFieldNameIn;
				opError.sendData(ConversionUtils.stringToBytes(MISSING_JSON_OBJECT));
				opLatestReadFieldName.sendData(ConversionUtils.stringToBytes(latestReadFieldName));
				opJsonFieldValue.sendData(ConversionUtils.stringToBytes(""));
				etpMissingJsonInput.raiseEvent();
			} else {
				latestReadFieldName = jsonFieldNameIn;
				opLatestReadFieldName.sendData(ConversionUtils.stringToBytes(latestReadFieldName));
				if (!latestParsedObject.containsKey(latestReadFieldName)) {
					AstericsErrorHandling.instance.getLogger().info("field name not found: " + latestReadFieldName);
					String customErrorMsg = INVALID_FIELD_NAME.replace("<>", latestReadFieldName);
					opError.sendData(ConversionUtils.stringToBytes(customErrorMsg));
					opJsonFieldValue.sendData(ConversionUtils.stringToBytes(""));
					etpFieldValueNotFound.raiseEvent();
				} else {
					String fieldValue = "";
					try {
						// null is a valid key value (key name existent like: {"fieldName": null} but
						// toString() would fail!
						fieldValue = latestParsedObject.get(latestReadFieldName) != null
								? latestParsedObject.get(latestReadFieldName).toString()
								: "null"; // = imp. option 1: send "null" to output port, NOT null! 

						if (fieldValue == null) {
							AstericsErrorHandling.instance.getLogger()
									.info("field value for field " + latestReadFieldName + " is null.");
						} else if (fieldValue.length() == 0) {
							AstericsErrorHandling.instance.getLogger()
									.info("field value for field " + latestReadFieldName + " is empty (\"\").");
						}
						// no special handling needed for empty values e.g. {"fieldName": ""} (valid and
						// sends "" to output port)
						AstericsErrorHandling.instance.getLogger()
								.info("field value found for field " + latestReadFieldName + ": " + fieldValue);
					} catch (Exception e) { // parsed JSON object and key being existent was previously checked -> only
											// errors with the format of field
						String customErrorMsg = INVALID_FIELD_VALUE.replace("<>", latestReadFieldName);
						opError.sendData(ConversionUtils.stringToBytes(customErrorMsg));
						opJsonFieldValue.sendData(ConversionUtils.stringToBytes(""));
						etpFieldValueInvalidFormat.raiseEvent(); // can only appear, if the value of a field cannot be
																 // converted to a string
						AstericsErrorHandling.instance.getLogger()
								.info("field value NOT found for field " + latestReadFieldName);
						return;
					}

					if (!propKeepParsedObject) {
						AstericsErrorHandling.instance.getLogger()
								.info("reject parsed json object after first successful field reading");
						latestParsedObject = null;
					}

					opJsonFieldValue.sendData(ConversionUtils.stringToBytes(fieldValue));

					opError.sendData(ConversionUtils.stringToBytes("")); // "unset" error message
					etpFieldValueAvailable.raiseEvent();	// also for the values "" or "null"
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