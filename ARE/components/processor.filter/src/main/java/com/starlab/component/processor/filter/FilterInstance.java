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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package com.starlab.component.processor.filter;

import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * This module implements a filter that can be configured to work as either low
 * pass, band pass and high pass filter.
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: 08/06/2012 Time:
 *         10:42:23
 */
public class FilterInstance extends AbstractRuntimeComponentInstance {

	public native long nativeCreateFilter(int order, int type,
			double sampleRate, double cutoffFreq1, double cutoffFreq2, int lengthFilteredSignal);

	public native boolean nativeRemoveFilter(long handle);

	public native double nativeComputeSample(long handle, double sample);

	public native double nativeGetSignalPower(long handle);

	static {
		System.loadLibrary("filter");
	}

	final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opPassBandMagnitude = new DefaultRuntimeOutputPort();
	final Object lock = new Object();

	int propOrder = 2;
	double propCutoffFreq1 = 0;
	double propCutoffFreq2 = 0;
	int propSignalPowerUpdateRate = 1;
	int propSignalPowerBufferSize = 125;
	int counterUpdateRate = 0;
	int propSamplingRate = 500;
	int propType = 0;
	long filterHandle = 0;

	// declare member variables here

	/**
	 * The class constructor.
	 */
	public FilterInstance() {
	}

	/**
	 * returns an Input Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the input port or null if not found
	 */
	public IRuntimeInputPort getInputPort(String portID) {
		if ("input".equalsIgnoreCase(portID)) {
			return ipInput;
		}

		return null;
	}

	/**
	 * returns an Output Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID) {
		if ("output".equalsIgnoreCase(portID)) {
			return opOutput;
		}
		if ("signalpower".equalsIgnoreCase(portID)) {
			return opPassBandMagnitude;
		}

		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventListener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

		return null;
	}

	/**
	 * returns an Event Triggerer Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventTriggerer port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {

		return null;
	}

	/**
	 * returns the value of the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("order".equalsIgnoreCase(propertyName)) {
			return propOrder;
		}
		if ("cutoffFreq1".equalsIgnoreCase(propertyName)) {
			return propCutoffFreq1;
		}
		if ("cutoffFreq2".equalsIgnoreCase(propertyName)) {
			return propCutoffFreq2;
		}
		if ("signalPowerUpdateRate".equalsIgnoreCase(propertyName)) {
			return propSignalPowerUpdateRate;
		}
		if ("signalPowerBufferSize".equalsIgnoreCase(propertyName)) {
			return propSignalPowerBufferSize;
		}
		if ("samplingRate".equalsIgnoreCase(propertyName)) {
			return propSamplingRate;
		}
		if ("type".equalsIgnoreCase(propertyName)) {
			return propType;
		}

		return null;
	}

	/**
	 * sets a new value for the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param newValue
	 *            the desired property value or null if not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		if ("order".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propOrder;
			int oldPropOrder = propOrder;
			propOrder = Integer.parseInt(newValue.toString());
			if (propOrder != oldPropOrder) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("cutoffFreq1".equalsIgnoreCase(propertyName)) {
			final double oldValue = propCutoffFreq1;
			double oldPropCutoffFreq1 = propCutoffFreq1;
			propCutoffFreq1 = Double.parseDouble((String) newValue);
			if (propCutoffFreq1 != oldPropCutoffFreq1) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("cutoffFreq2".equalsIgnoreCase(propertyName)) {
			final double oldValue = propCutoffFreq2;
			double oldPropCutoffFreq2 = propCutoffFreq2;
			propCutoffFreq2 = Double.parseDouble((String) newValue);
			if (propCutoffFreq2 != oldPropCutoffFreq2) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("samplingRate".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSamplingRate;
			int oldPropSamplingRate = propSamplingRate;
			propSamplingRate = Integer.parseInt(newValue.toString());
			if (propSamplingRate != oldPropSamplingRate) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("type".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propType;
			int oldPropType = propType;
			propType = Integer.parseInt(newValue.toString());
			if (propType != oldPropType) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("signalPowerUpdateRate".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSignalPowerUpdateRate;
			int oldPropSignalPowerUpdateRate = propSignalPowerUpdateRate;
			propSignalPowerUpdateRate = Integer.parseInt(newValue.toString());
			if (propSignalPowerUpdateRate != oldPropSignalPowerUpdateRate) {
				reconfigureFilter();
			}
			return oldValue;
		}
		if ("signalPowerBufferSize".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSignalPowerBufferSize;
			int oldPropSignalPowerBufferSize = propSignalPowerBufferSize;
			propSignalPowerBufferSize = Integer.parseInt(newValue.toString());
			if (propSignalPowerBufferSize != oldPropSignalPowerBufferSize) {
				reconfigureFilter();
			}
			return oldValue;
		}

		return null;
	}

	/**
	 * Input Ports for receiving values.
	 */
	private final DefaultRuntimeInputPort ipInput = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			double sample = ConversionUtils.doubleFromBytes(data);
			double output;
			synchronized (lock) {
				output = nativeComputeSample(filterHandle, sample);
			}
			opOutput.sendData(ConversionUtils.doubleToBytes(output));
			if (++counterUpdateRate >= propSignalPowerUpdateRate) {
				counterUpdateRate = 0;
				synchronized (lock) {
					output = nativeGetSignalPower(filterHandle);
				}
				opPassBandMagnitude.sendData(ConversionUtils
						.doubleToBytes(output));
			}
		}
	};

	/**
	 * Event Listerner Ports.
	 */

	/**
	 * called when model is started.
	 */
	@Override
	public void start() {
		reconfigureFilter();
		super.start();
	}

	/**
	 * called when model is paused.
	 */
	@Override
	public void pause() {
		super.pause();
	}

	/**
	 * called when model is resumed.
	 */
	@Override
	public void resume() {
		super.resume();
	}

	/**
	 * called when model is stopped.
	 */
	@Override
	public void stop() {
		super.stop();
	}

	private void reconfigureFilter() {
		synchronized (lock) {
			if (filterHandle != 0) {
				nativeRemoveFilter(filterHandle);
			}
			counterUpdateRate = 0;
			filterHandle = nativeCreateFilter(propOrder, propType,
					propSamplingRate, propCutoffFreq1, propCutoffFreq2, propSignalPowerBufferSize);
		}
	}

	protected void finalize() throws Throwable {
		if (filterHandle != 0) {
			nativeRemoveFilter(filterHandle);
		}
		super.finalize();
	}
}