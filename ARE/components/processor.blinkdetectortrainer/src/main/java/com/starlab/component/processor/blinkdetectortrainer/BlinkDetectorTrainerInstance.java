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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package com.starlab.component.processor.blinkdetectortrainer;

import java.util.logging.Logger;
import java.util.ResourceBundle;
import java.util.Locale;
import java.lang.String;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * This module implements the training of the blink detector plug-in by measuring the
 * maximum and minimum thresholds, the blink length and the separation
 * between double blinks for each subject.
 * 
 * 
 * 
 * @author Laura Dubreuil Vall [laura.dubreuil@starlab.es] 
 * Date: 03/09/2012
 * Time: 13:41
 */
public class BlinkDetectorTrainerInstance extends  AbstractRuntimeComponentInstance {
	final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort resultsOutput = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.:
	// opMyOutPort.sendData(ConversionUtils.intToBytes(10));

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propLanguage = 0;
	Timer timer = null;

	// declare member variables here
	Locale currentLocale;
	ResourceBundle messages;
	BufferedWriter out = null;
	
	int propSampleRate = 250;

	//Booleans to indicate whether we are looking for simple or double blinks
	int lookForSimple = 0;
	int lookForDouble = 0;
	
	//First received sample after the trigger 
	int firstSample = 0;	
		
	//Index for total blinks (each blink of double blinks are also considered simple blinks)
	int iMinTotal = 0;
	int iMaxTotal = 0;
	
	//Index for simple blinks
	int index = 0;
	

	static {
		System.loadLibrary("blinkdetectortrainer");
		AstericsErrorHandling.instance.getLogger().fine("Loading \"blinkdetectortrainer.dll\" ... ok!");
	}
	
	//DLL
	public native long nativeCreateBlinkDetectorTrainer(int sampleRate);
	public native boolean nativeRemoveBlinkDetectorTrainer (long handle);
	public native int nativeComputeSampleBlinkDetectorTrainer(long handle, double sample,int firstSample, int lookForSimple, 
				int lookForDouble, int index, int iMinTotal, int iMaxTotal);
	public native double nativeComputeMax(long handle,int size); //Compute minimum max
	public native double nativeComputeMin(long handle, int size); //Compute maximum min
	public native int nativeComputeLength(long handle, int size); //Simple blink length
	public native int nativeComputeSeparation(long handle, int size); //Double blink separation
	public native int nativeSetParametersDoubleBlink(long handle, double minThreshold,double maxThreshold, int interval1,int interval2);
	public native int nativeStartProtocol(long handle); //Initialize variables

	final Object lock = new Object();
	
	long blinkDetectorHandle = 0;
	
	/**
	 * The class constructor.
	 */
	public BlinkDetectorTrainerInstance() {
		// empty constructor
		setUpMessages();

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
			return ipInput;		}

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
		if ("protocol".equalsIgnoreCase(portID)) {
			return opOutput;
		}
		if ("results".equalsIgnoreCase(portID)) {
			return resultsOutput;
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
		if ("startProtocol".equalsIgnoreCase(eventPortID)) {
			return elpStartProtocol;
		}
		if ("stopProtocol".equalsIgnoreCase(eventPortID)) {
			return elpStopProtocol;
		}

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
		if ("language".equalsIgnoreCase(propertyName))
		{
			return propLanguage;
		}
		if ("sampleRate".equalsIgnoreCase(propertyName))
		{
			return propSampleRate;
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
		if ("language".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propLanguage;
			int newPropLanguage = Integer.parseInt(newValue.toString());
			if (newPropLanguage != propLanguage) {
				propLanguage = newPropLanguage;
				setUpMessages();
			}
			return oldValue;
		}
		if ("sampleRate".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSampleRate;
			int oldPropSampleRate = propSampleRate;
			propSampleRate = Integer.parseInt(newValue.toString());
			if (propSampleRate != oldPropSampleRate) {
				reconfigure();
			}
			return oldValue;
		}

		return null;
	}

	/**
	 * Input Ports for receiving values.
	 */
	private final IRuntimeInputPort ipInput = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			
			double in = ConversionUtils.doubleFromBytes(data);
           synchronized (lock) {
        	   firstSample = nativeComputeSampleBlinkDetectorTrainer(blinkDetectorHandle, in, firstSample, lookForSimple, lookForDouble, index, iMinTotal, iMaxTotal);

            }
		}
	};
	
	/**
	 * Event Listerner Ports.
	 */
	final IRuntimeEventListenerPort elpStopProtocol = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			if (timer != null)
			{
				timer.cancel();
			}
			opOutput.sendData(ConversionUtils.stringToBytes(""));
			resultsOutput.sendData(ConversionUtils.stringToBytes(""));
			lookForSimple = 0;
			lookForDouble = 0;
			firstSample = 0;
		}
	};

	/**
	 * Event Listerner Ports.
	 */
	final IRuntimeEventListenerPort elpStartProtocol = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {
			// insert event handling here
			if (timer != null)
			{
				timer.cancel();
			}
			timer = new Timer();
			index = 0;
			iMaxTotal = 0;
			iMinTotal = 0;
			
			lookForSimple = 0;
			lookForDouble = 0;
			firstSample = 0;
			
			nativeStartProtocol(blinkDetectorHandle); //Initialize variables
			
			opOutput.sendData(ConversionUtils.stringToBytes(""));
			resultsOutput.sendData(ConversionUtils.stringToBytes(""));
			timer.scheduleAtFixedRate(new TimerTask()
				{
					int counter = 0;
					int protocolStatus = 0;

					public void run() 
					{
						switch (protocolStatus)
						{
						case 0:
							protocolStatus++;
							counter = 0;
							opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("ini_protocol")));
							lookForSimple = 0;
							lookForDouble = 0;
							break;
						case 1:
							if (counter >= 5)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 2:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("blink")));
								lookForSimple = 1;
								lookForDouble = 0;
								firstSample = 1;
							}
							break;
						case 3:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 4:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 1;
								lookForDouble = 0;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("blink")));
							}
							break;
						case 5:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 6:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 1;
								lookForDouble = 0;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("blink")));
							}
							break;
						case 7:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 8:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 1;
								lookForDouble = 0;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("blink")));
							}
							break;
						case 9:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 10:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 1;
								lookForDouble = 0;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("blink")));
							}
							break;
						case 11:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 12:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 0;
								lookForDouble = 1;
								firstSample = 1;
								/*double maximumSimple = 0.3*nativeComputeMax(blinkDetectorHandle,5);
								double minimumSimple = 0.3*nativeComputeMin(blinkDetectorHandle,5);
								int interval1 = 5*nativeComputeLength(blinkDetectorHandle,5);
								int interval2 = (int)(interval1*3);
								System.out.println("Parameters for double blink: ");
								System.out.println(Double.toString(minimumSimple));
								System.out.println("\n");
								System.out.println(Double.toString(maximumSimple));
								System.out.println("\n");
								System.out.println(Integer.toString(interval1));
								System.out.println("\n");
								System.out.println(Integer.toString(interval2));*/
								//nativeSetParametersDoubleBlink(blinkDetectorHandle,minimumSimple,maximumSimple,interval1,interval2);
								index=0;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("double_blink")));
							}
							break;
						case 13:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 14:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 0;
								lookForDouble = 1;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("double_blink")));
							}
							break;
						case 15:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 16:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 0;
								lookForDouble = 1;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("double_blink")));
							}
							break;
						case 17:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 18:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 0;
								lookForDouble = 1;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("double_blink")));
							}
							break;
						case 19:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 20:
							if (counter >= 1)
							{
								protocolStatus++;
								counter = 0;
								lookForSimple = 0;
								lookForDouble = 1;
								firstSample = 1;
								index++;
								iMinTotal++;
								iMaxTotal++;
								iMinTotal++;
								iMaxTotal++;
								opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("double_blink")));
							}
							break;
						case 21:
							if (counter >= 2)
							{
								protocolStatus++;
								counter = 0;
								opOutput.sendData(ConversionUtils.stringToBytes(" "));
							}
							break;
						case 22:
							timer.cancel();
							lookForSimple = 0;
							lookForDouble = 0;
							firstSample = 0;
												
							opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("end_protocol")));
							
							int maximum = (int)nativeComputeMax(blinkDetectorHandle,15);
							System.out.println("Maximum threshold: ");
							System.out.println(Double.toString(maximum));
							
							int minimum = (int)nativeComputeMin(blinkDetectorHandle,15);
							System.out.println("Minimum threshold: ");
							System.out.println(Double.toString(minimum));
							
							int	length = nativeComputeLength(blinkDetectorHandle,15);
							System.out.println("Blink length: ");							
							System.out.println(Integer.toString(length));
							
							int separation = nativeComputeSeparation(blinkDetectorHandle,5);
							System.out.println("Double blink separation: ");
							System.out.println(Integer.toString(separation));
							
							String results;
							if ((maximum != 0)&&(minimum!=0)&&(length!=0)&&(separation!=0))
							{
								results = "Maximum threshold: " + Integer.toString(maximum)
										+ "        Minimum threshold: " + Integer.toString(minimum)
										+ "        Blink length: " + Integer.toString(length) + " ms"
										+ "        Double blink separation: " + Integer.toString(separation) + " ms";
							}
							else
								results = "Error calculating parameters. Please, stop and start the protocol again";
							
							opOutput.sendData(ConversionUtils.stringToBytes(messages.getString("results")));
								
							resultsOutput.sendData(ConversionUtils.stringToBytes(results));
							//System.out.print(results);
							
							/*for (int i=0;i<15;i++)
							{
								System.out.println(Double.toString(maxTotal[i]));
								System.out.println(Double.toString(minTotal[i]));
								System.out.println(Double.toString(posMaxTotal[i]));
								System.out.println(Double.toString(posMinTotal[i]));
								System.out.println("\n");
							}*/
							break;
						}

							
						System.out.println("Timer " + counter + " " + propLanguage);
						counter++;
					}
				}, 0, 1000);
			
		}
	};

	/**
	 * called when model is started.
	 */
	@Override
	public void start() {
  	  if (blinkDetectorHandle == 0)
  	  {
  		  blinkDetectorHandle = nativeCreateBlinkDetectorTrainer(propSampleRate);
  	  }
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
  	  if (blinkDetectorHandle != 0)
  	  {
  		  nativeRemoveBlinkDetectorTrainer(blinkDetectorHandle);
  		  blinkDetectorHandle = 0;
  	  }
		if (timer != null)
		{
			timer.cancel();
		}
		super.stop();
	}
	
    private void reconfigure() {
  	  synchronized (lock) {
  		  if (blinkDetectorHandle != 0) {
  			  nativeRemoveBlinkDetectorTrainer(blinkDetectorHandle);
			  }
  		  blinkDetectorHandle = nativeCreateBlinkDetectorTrainer(propSampleRate);
		  }
	  }

    
    protected void finalize() throws Throwable
    {
  	  if (blinkDetectorHandle != 0)
  	  {
  		  nativeRemoveBlinkDetectorTrainer(blinkDetectorHandle);
  	  }
  	  super.finalize();
    }

	private String getLanguage(int id) {
		switch (id) {
		case 0:
			return "english";
		case 1:
			return "spanish";
		}
		return "english";
	}

	private void setUpMessages() {
		currentLocale = new Locale(getLanguage(propLanguage), "");
		try {
			messages = ResourceBundle
					.getBundle("MessagesBundle", currentLocale);
			/*
			 * System.out.println(messages.getString("ini_protocol"));
			 * System.out.println(messages.getString("blink"));
			 * System.out.println(messages.getString("double_blink"));
			 * System.out.println(messages.getString("end_protocol"));
			 * System.out.println(messages.getString("results"));
			 */
		} catch (java.util.MissingResourceException ex) {
			System.out.println("Resource not found");
		}
	}
}