
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

package eu.asterics.component.processor.stringextractor;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Extracts a subtext from an input text and immediately forwards it to the
 * output port, as soon extraction is finished due to both start and end
 * delimiter detected (in the correct order and not overlapping each other).
 * 
 * @author Barbara Wakolbinger Date: 2018/01/16
 */
public class StringExtractorInstance extends AbstractRuntimeComponentInstance {
	
	/* ***************************************************************************
	 *  Output ports 
	 * ***************************************************************************/
	final IRuntimeOutputPort opExtractedText = new DefaultRuntimeOutputPort();

	
	/* ***************************************************************************
	 *  Event Triggerer Ports
	 * ***************************************************************************/
	final IRuntimeEventTriggererPort etpTextExtracted = new DefaultRuntimeEventTriggererPort();
	

	/* ***************************************************************************
	 *  Properties
	 * ***************************************************************************/
	String propStartDelimiter = "START";
	String propEndDelimiter = "END";

	
	/* ***************************************************************************
	 * Constructors
	 * ***************************************************************************/
	/**
	 * The class constructor.
	 */
	public StringExtractorInstance() {
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
		if ("inText".equalsIgnoreCase(portID)) {
			return ipInText;
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
		if ("extractedText".equalsIgnoreCase(portID)) {
			return opExtractedText;
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
		if ("textExtracted".equalsIgnoreCase(eventPortID)) {
			return etpTextExtracted;
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
		if ("startDelimiter".equalsIgnoreCase(propertyName)) {
			return propStartDelimiter;
		}
		if ("endDelimiter".equalsIgnoreCase(propertyName)) {
			return propEndDelimiter;
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
		if ("startDelimiter".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propStartDelimiter;
			propStartDelimiter = (String) newValue;
			return oldValue;
		}
		if ("endDelimiter".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propEndDelimiter;
			propEndDelimiter = (String) newValue;
			return oldValue;
		}

		return null;
	}
	

	/* ***************************************************************************
	 * Input Ports for receiving values
	 * ***************************************************************************/
	private final IRuntimeInputPort ipInText = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			String dataValue = ConversionUtils.stringFromBytes(data);
			AstericsErrorHandling.instance.getLogger().info("StringExtractor received: " + dataValue);

			if (dataValue.contains(propStartDelimiter) && dataValue.contains(propEndDelimiter)) {
				// if delimiters appear more often, always the next appearances are used (end
				// delimiter always behind start delimiter otherwise ignored)
				int endIndexOfStartDelim = dataValue.indexOf(propStartDelimiter) + propStartDelimiter.length();
				int startIndexOfEndDelim = dataValue.indexOf(propEndDelimiter, endIndexOfStartDelim);

				if (startIndexOfEndDelim > -1) {
					String extracted = dataValue.substring(endIndexOfStartDelim, startIndexOfEndDelim);
					opExtractedText.sendData(ConversionUtils.stringToBytes(extracted));
					etpTextExtracted.raiseEvent();
					AstericsErrorHandling.instance.getLogger().info("StringExtractor raised event");
				} else {
					AstericsErrorHandling.instance.getLogger().info(
							"StringExtractor DID NOT rais event as end delimiter was found in front or within start delimiter!");
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