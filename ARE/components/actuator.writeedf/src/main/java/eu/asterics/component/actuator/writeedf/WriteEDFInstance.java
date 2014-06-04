

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

package eu.asterics.component.actuator.writeedf;


import java.io.BufferedWriter;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//import WriteEDFInstance.BufferSampling;
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
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * This plugin records the incoming data into an EDF file
 * 
 * Have a look at "http://www.edfplus.info/specs/edf.html" for full file specification
 *  
 * @author Christian Strasak [christian.strasak@technikum-wien.at]
 *         Date: 24.05.2014
 */
public class WriteEDFInstance extends AbstractRuntimeComponentInstance implements Runnable
{
	//===================================================================================================
  	// final variables
  	//=================================================================================================== 
	final int numberOfSignals = 8; // number of signals
	final int recordDuration = 1;
	final String propFileExtension = ".edf";
	//===================================================================================================
  	// variables
  	//=================================================================================================== 
	boolean recordingShouldRun = false;
	boolean samplingShouldPause = false;
	boolean recordingRuns = true;
	boolean propReSampling = false;
	long startTimeOfSampling = 0;
	long numberOfDataRecords = 0;
	String propPatientID = "undefined";
	String propFileName = "record";
	int numberOfBuffers = 5;
//	int propRecordingMode = 0;
	//===================================================================================================
  	// arrays
  	//=================================================================================================== 
	double samplesBuffer[] = new double[numberOfSignals];
	long nextSampleTime[] = new long[numberOfSignals];
	int buffer[] = new int[numberOfSignals];
	ByteArrayOutputStream [][] dataSamples = new ByteArrayOutputStream[numberOfSignals][numberOfBuffers]; //streams for the samples data
	//===================================================================================================
  	// enumerations
	// RESAMPLING: the incoming data will be sampled independently from there original sampling rate
	// PASSTHROUGH: incoming data will be written directly to the file, without new sampling
  	//=================================================================================================== 
//	Method recordingMethod = Method.PASSTHROUGH;
	//===================================================================================================
  	// objects
  	//=================================================================================================== 
	BufferSampling[] IBufferSampling = new BufferSampling[numberOfSignals];
	WriteStreamsToFile IWriteStreamsToFile;
	

	int propSamplingRateCH[] = new int[numberOfSignals];
	double propPhysicalMinimumCH[] = new double[numberOfSignals];
	double propPhysicalMaximumCH[] = new double[numberOfSignals];
	double propDigitalMinimumCH[]  = new double[numberOfSignals];
	double propDigitalMaximumCH[]  = new double[numberOfSignals];
	
    
   /**
    * The class constructor.
    */
    public WriteEDFInstance()
    {
    	for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
    	{
    		for(int buffer = 0; buffer < numberOfBuffers; buffer++)
    			dataSamples[signalNumber][buffer] = new ByteArrayOutputStream();
//    		buffer[signalNumber] = 0;
    		samplesBuffer[signalNumber] = 0;
    		propSamplingRateCH[signalNumber] = 100;
    		propPhysicalMinimumCH[signalNumber] = -32768;
    		propPhysicalMaximumCH[signalNumber] = 32767;
    		propDigitalMinimumCH[signalNumber] = -32768;
    		propDigitalMaximumCH[signalNumber] = 32767;
    		nextSampleTime[signalNumber] = 0;
    	}
    }

//    public enum Method 
//	{
//		PASSTHROUGH(0), RESAMPLING(1);
//		private int value;
//	
//		private Method (int value){ this.value = value;}
//	};
	
	
   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("cH1".equalsIgnoreCase(portID))
		{
			return ipCH1;
		}
		if ("cH2".equalsIgnoreCase(portID))
		{
			return ipCH2;
		}
		if ("cH3".equalsIgnoreCase(portID))
		{
			return ipCH3;
		}
		if ("cH4".equalsIgnoreCase(portID))
		{
			return ipCH4;
		}
		if ("cH5".equalsIgnoreCase(portID))
		{
			return ipCH5;
		}
		if ("cH6".equalsIgnoreCase(portID))
		{
			return ipCH6;
		}
		if ("cH7".equalsIgnoreCase(portID))
		{
			return ipCH7;
		}
		if ("cH8".equalsIgnoreCase(portID))
		{
			return ipCH8;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("patientID".equalsIgnoreCase(propertyName))
		{
			return propPatientID;
		}
		if ("fileName".equalsIgnoreCase(propertyName))
		{
			return propFileName;
		}
		if ("reSampling".equalsIgnoreCase(propertyName))
		{
			return propReSampling;
		}
		if ("samplingRateCH1".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[0];
		}
		if ("samplingRateCH2".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[1];
		}
		if ("samplingRateCH3".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[2];
		}
		if ("samplingRateCH4".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[3];
		}
		if ("samplingRateCH5".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[4];
		}
		if ("samplingRateCH6".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[5];
		}
		if ("samplingRateCH7".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[6];
		}
		if ("samplingRateCH8".equalsIgnoreCase(propertyName))
		{
			return propSamplingRateCH[7];
		}
		if ("physicalMinimumCH1".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[0];
		}
		if ("physicalMinimumCH2".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[1];
		}
		if ("physicalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[2];
		}
		if ("physicalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[3];
		}
		if ("physicalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[4];
		}
		if ("physicalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[5];
		}
		if ("physicalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[6];
		}
		if ("physicalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMinimumCH[7];
		}
		if ("physicalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[0];
		}
		if ("physicalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[1];
		}
		if ("physicalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[2];
		}
		if ("physicalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[3];
		}
		if ("physicalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[4];
		}
		if ("physicalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[5];
		}
		if ("physicalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[6];
		}
		if ("physicalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			return propPhysicalMaximumCH[7];
		}
		if ("digitalMinimumCH1".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[0];
		}
		if ("digitalMinimumCH2".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[1];
		}
		if ("digitalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[2];
		}
		if ("digitalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[3];
		}
		if ("digitalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[4];
		}
		if ("digitalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[5];
		}
		if ("digitalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[6];
		}
		if ("digitalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			return propDigitalMinimumCH[7];
		}
		if ("digitalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[0];
		}
		if ("digitalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[1];
		}
		if ("digitalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[2];
		}
		if ("digitalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[3];
		}
		if ("digitalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[4];
		}
		if ("digitalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[5];
		}
		if ("digitalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[6];
		}
		if ("digitalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			return propDigitalMaximumCH[7];
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("patientID".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propPatientID;
			propPatientID = (String)newValue;
			return oldValue;
		}
		if ("fileName".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFileName;
			propFileName = (String)newValue;
			return oldValue;
		}
		if ("reSampling".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propReSampling;
			propReSampling = Boolean.parseBoolean((String)newValue);
			return oldValue;
			
		}
		if ("samplingRateCH1".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[0];
			propSamplingRateCH[0] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH2".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[1];
			propSamplingRateCH[1] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH3".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[2];
			propSamplingRateCH[2] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH4".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[3];
			propSamplingRateCH[3] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH5".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[4];
			propSamplingRateCH[4] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH6".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[5];
			propSamplingRateCH[5] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH7".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[6];
			propSamplingRateCH[6] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("samplingRateCH8".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propSamplingRateCH[7];
			propSamplingRateCH[7] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[0];
			propPhysicalMinimumCH[0] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[1];
			propPhysicalMinimumCH[1] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[2];
			propPhysicalMinimumCH[2] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[3];
			propPhysicalMinimumCH[3] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[4];
			propPhysicalMinimumCH[4] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[5];
			propPhysicalMinimumCH[5] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[6];
			propPhysicalMinimumCH[6] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMinimumCH[7];
			propPhysicalMinimumCH[7] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[0];
			propPhysicalMaximumCH[0] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[1];
			propPhysicalMaximumCH[1] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[2];
			propPhysicalMaximumCH[2] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[3];
			propPhysicalMaximumCH[3] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[4];
			propPhysicalMaximumCH[4] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[5];
			propPhysicalMaximumCH[5] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[6];
			propPhysicalMaximumCH[6] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propPhysicalMaximumCH[7];
			propPhysicalMaximumCH[7] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[0];
			propDigitalMinimumCH[0] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[1];
			propDigitalMinimumCH[1] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[2];
			propDigitalMinimumCH[2] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[3];
			propDigitalMinimumCH[3] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[4];
			propDigitalMinimumCH[4] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[5];
			propDigitalMinimumCH[5] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[6];
			propDigitalMinimumCH[6] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMinimumCH[7];
			propDigitalMinimumCH[7] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[0];
			propDigitalMaximumCH[0] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[1];
			propDigitalMaximumCH[1] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[2];
			propDigitalMaximumCH[2] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[3];
			propDigitalMaximumCH[3] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[4];
			propDigitalMaximumCH[4] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[5];
			propDigitalMaximumCH[5] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[6];
			propDigitalMaximumCH[6] = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDigitalMaximumCH[7];
			propDigitalMaximumCH[7] = Double.parseDouble((String)newValue);
			return oldValue;
		}

        return null;
    }
    public class BufferSampling extends Thread
    {
    	
    	int signalNumber = 0;

    	/**Constructor
    	 * 
    	 * @param signalNumberParameter = number of the signal to process
    	 */
    	public BufferSampling (int signalNumberParameter)
    	{
    		this.signalNumber = signalNumberParameter; // to declare for which signal this object is used for
    	}
    	
    	public void run()
    	{
    		int buffer = 0;
        	long timeOffset = 0;
        	long timeUntilNextSample = 0;
        	long periodDuration = 1000 * 1000 * 1000 / propSamplingRateCH[signalNumber];
        	
    		for(int sampleNumber = 1; sampleNumber <= propSamplingRateCH[signalNumber]; sampleNumber++)
    		{
	    		//===================================================================================================
		    	// Calculate the next Sampling Timepoint
		    	//=================================================================================================== 
    			timeOffset = System.nanoTime() - startTimeOfSampling;
				nextSampleTime[signalNumber] += periodDuration;
 	    		timeUntilNextSample = nextSampleTime[signalNumber] - timeOffset;
	    		//===================================================================================================
		    	// Wait until the next Sampling Timepoint has been reached
		    	//=================================================================================================== 
 	    		if(timeUntilNextSample > 0)
 	    		{
	 	    		try {TimeUnit.NANOSECONDS.sleep(timeUntilNextSample);} 	catch (InterruptedException e) {};
 	    		}
	    		//===================================================================================================
		    	// Convert the Sample into an 2 byte integer value
		    	//=================================================================================================== 
 	    		int dataInt = (int)((samplesBuffer[signalNumber] - propPhysicalMinimumCH[signalNumber]) / (propPhysicalMaximumCH[signalNumber] - propPhysicalMinimumCH[signalNumber]) * (propDigitalMaximumCH[signalNumber] - propDigitalMinimumCH[signalNumber]) + propDigitalMinimumCH[signalNumber]);
    	    	byte[] value = new byte [2];
    	    	value[0] = (byte) (dataInt & 0xFF);
    	        value[1] = (byte) ((dataInt >> 8) & 0xFF);
	    		//===================================================================================================
		    	// Write the 2 Bytes of the integer value into its coresponding dataStream
		    	//=================================================================================================== 
    	        dataSamples[signalNumber][buffer].write(value[0]);
    	        dataSamples[signalNumber][buffer].write(value[1]);
	    		//===================================================================================================
		    	// wait if the pause button of the ACS/ARE has been pressed
		    	//=================================================================================================== 
    	        if(samplingShouldPause)
    	        {
	    	        while(samplingShouldPause)
	    	        {
	    	        	try{TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e){};
	    	        }
		    		//===================================================================================================
			    	// correct the time and sampling offset after the pause button has been pressed
			    	//=================================================================================================== 
	    	        startTimeOfSampling = System.nanoTime();
	    	        nextSampleTime[signalNumber] = 0;
    	        }
    		}
    	}
    }

    public void writeData(byte[] data, int signalNumber)
    {
    	if(!samplingShouldPause)
    	{
	    	double DoubleData = ConversionUtils.doubleFromBytes(data);
	    	int dataInt = (int)((DoubleData - propPhysicalMinimumCH[signalNumber]) / (propPhysicalMaximumCH[signalNumber] - propPhysicalMinimumCH[signalNumber]) * (propDigitalMaximumCH[signalNumber] - propDigitalMinimumCH[signalNumber]) + propDigitalMinimumCH[signalNumber]);
//	    	AstericsErrorHandling.instance.getLogger().info("incoming dat int: " + dataInt);
	        byte[] value = new byte [2];
	        value[0] = (byte) (dataInt & 0xFF);
	        value[1] = (byte) ((dataInt >> 8) & 0xFF);
	        
	        dataSamples[signalNumber][buffer[signalNumber]].write(value[0]);
	        dataSamples[signalNumber][buffer[signalNumber]].write(value[1]);
	        if( (dataSamples[signalNumber][buffer[signalNumber]].size() / 2) >= propSamplingRateCH[signalNumber] )
	        {
	        	if(buffer[signalNumber] < (numberOfBuffers - 1) )
	        		buffer[signalNumber]++;
	        	else buffer[signalNumber] = 0;
	        }
    	}
    }
    public class WriteStreamsToFile extends Thread
    {
    	/**Constructor
    	 * 
    	 * resets the streams everytime (when the plugin has been restarted)
    	 */

    	public WriteStreamsToFile ()
    	{
    		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
    		{
        		for(int buffer = 0; buffer < numberOfBuffers; buffer++)
        			dataSamples[signalNumber][buffer].reset();
        		buffer[signalNumber] = 0;
    		}
    	}
    	public void run ()
    	{
    		int buffer = 0;
    		long numberOfDataRecords = 0;
    		
    		while(recordingShouldRun)
    		{
    			recordingRuns = true;
	    		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
	    		{
	    			if( (dataSamples[signalNumber][buffer].size() / 2) < propSamplingRateCH[signalNumber] )
	    			{
	    				if(recordingShouldRun)
	    				{
	    					try{ TimeUnit.MILLISECONDS.sleep(1); } catch (Exception e) {AstericsErrorHandling.instance.getLogger().warning("could not sleep");};
	    					signalNumber--;
	    				}
	    				else
	    				{
	    					recordingRuns = false;
	    					return;
	    				}
	    			}
	    		}
	     		if(recordingShouldRun)
		  		{
		    		//===================================================================================================
			    	// write all sampling streams into the file body
			    	//===================================================================================================
		      		try 
		    		{
		      			OutputStream outputStream = new FileOutputStream ("data/edf/" + propFileName + propFileExtension, true);
		    			
		    			for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
		    				dataSamples[signalNumber][buffer].writeTo(outputStream); // write each sample of each signal into the file
		
		    			outputStream.close();
		    		} catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error appending data streams to file");}
		    		//===================================================================================================
			    	// write the correct number of records into the file header
			    	//=================================================================================================== 
		      		try
		        	{
		    	    	RandomAccessFile file = new RandomAccessFile("data/edf/" + propFileName + propFileExtension, "rw");
		    	    	file.seek(236); // go to position of "number of data records" in file
		    	    	file.writeBytes(spaceFilledString(String.valueOf(++numberOfDataRecords),8,false));
		    	    	file.close();
		        	}catch (IOException e) {AstericsErrorHandling.instance.getLogger().info("Error writing value for number of data records in file: " + propFileName);}
		  		}

	    		
	    		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
	    			dataSamples[signalNumber][buffer].reset();
	    		if(buffer < (numberOfBuffers - 1) )
	        		buffer ++;
	        	else buffer = 0;
    		}
    		recordingRuns = false;
        }
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipCH1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[0] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,0);
		}
	};
	private final IRuntimeInputPort ipCH2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[1] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,1);
		}
	};
	private final IRuntimeInputPort ipCH3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[2] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,2);
		}
	};
	private final IRuntimeInputPort ipCH4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[3] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,3);
		}
	};
	private final IRuntimeInputPort ipCH5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[4] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,4);
		}
	};
	private final IRuntimeInputPort ipCH6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[5] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,5);
		}
	};
	private final IRuntimeInputPort ipCH7  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[6] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,6);
		}
	};
	private final IRuntimeInputPort ipCH8  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if(propReSampling == true)
				samplesBuffer[7] = ConversionUtils.doubleFromBytes(data);
			else
				writeData(data,7);
		}
	};

	public String spaceFilledString(String value, final int len, boolean multi)
	{
		String newString = "";
		int signals = 1;
		
		if(multi == true)
			signals = numberOfSignals;
		for(int signalNumber = 0; signalNumber < signals; signalNumber++)
		{
			newString += value; 
			for (int i = value.length(); i < len; i++)
	            newString += " ";
		}
		return newString;
	}

     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    	  DateFormat timeFormat = new SimpleDateFormat("HH.mm.ss");
    	  Date date = new Date();
    	  //===================================================================================================
    	  // set the global header information for the EDF file
    	  //===================================================================================================
    	  String formatVersion = spaceFilledString("0",8,false); // 0 indicates that it is written in EDF-format
    	  String patientID = spaceFilledString(propPatientID,80,false);
    	  String recordingID = spaceFilledString("AsTeRICS",80,false);
    	  String startDate = dateFormat.format(date);
    	  String startTime = timeFormat.format(date);
    	  String headerBytes = spaceFilledString(String.valueOf(256 * (1 + numberOfSignals)),8,false);//Size of header in bytes (to find the recording start)
    	  String reserved= spaceFilledString("",44,false);
    	  String numberOfRecords= spaceFilledString("-1",8,false);
    	  String durationOfRecords= spaceFilledString(String.valueOf(recordDuration),8,false);
    	  String signals = spaceFilledString(String.valueOf(numberOfSignals),4,false);
    	  //===================================================================================================
    	  // Set the signal-specific header information for the EDF file
    	  //=================================================================================================== 
    	  String label = spaceFilledString("label",16,true); //(e.g. EEG Fpz-Cz or Body temp)
    	  String transducerType = spaceFilledString("transducerType",80,true); // (e.g. AgAgCl electrode) 
    	  String physicalDimension = spaceFilledString("units",8,true); // (e.g. uV or degreeC)  
    	  
    	  String physicalMinimum = "";
    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
    		  physicalMinimum += spaceFilledString(String.valueOf((int)propPhysicalMinimumCH[signalNumber]),8,false); // number of samples in each data record (must be set to "-1" before recording due to EDF specifications)    

    	  String physicalMaximum = "";
    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
    		  physicalMaximum += spaceFilledString(String.valueOf((int)propPhysicalMaximumCH[signalNumber]),8,false); // number of samples in each data record (must be set to "-1" before recording due to EDF specifications)    

    	  String digitalMinimum = "";
    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
    		  digitalMinimum += spaceFilledString(String.valueOf((int)propDigitalMinimumCH[signalNumber]),8,false); // number of samples in each data record (must be set to "-1" before recording due to EDF specifications)    

    	  String digitalMaximum = "";
    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
    		  digitalMaximum += spaceFilledString(String.valueOf((int)propDigitalMaximumCH[signalNumber]),8,false); // number of samples in each data record (must be set to "-1" before recording due to EDF specifications)    

    	  String prefiltering = spaceFilledString("preFiltering",80,true); // (e.g. HP:0.1Hz LP:75Hz)
    	  String numberOfSamples = "";
    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
    		  numberOfSamples += spaceFilledString(String.valueOf(propSamplingRateCH[signalNumber]),8,false); // number of samples in each data record (must be set to "-1" before recording due to EDF specifications)    
    	  String reserved2 = spaceFilledString("",32,true); // reserved due to EDF specification     
    	  //===================================================================================================
    	  // write the header data, defined above, into file
    	  //===================================================================================================
    	  try {  new File("data/edf").mkdirs(); } catch (Exception e) {AstericsErrorHandling.instance.getLogger().severe("Error: could not create subfolders");}
    	  BufferedWriter out = null;
    	  try 
    	  {
    		  out = new BufferedWriter(new FileWriter("data/edf/" + propFileName + propFileExtension));
    		  out.write(formatVersion 
						+ "" + patientID 
						+ recordingID 
						+ startDate
						+ startTime
						+ headerBytes
						+ reserved
						+ numberOfRecords
						+ durationOfRecords
						+ signals
						+ label
						+ transducerType
						+ physicalDimension
						+ physicalMinimum
						+ physicalMaximum
						+ digitalMinimum
						+ digitalMaximum
						+ prefiltering
						+ numberOfSamples
						+ reserved2);
    	  } catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error writing header to file");}
    	  try {out.close();} catch (IOException e) {AstericsErrorHandling.instance.reportError(this, "Error closing file");}
    	  
		  recordingShouldRun = true;
    	  if(propReSampling == true)
    		  AstericsThreadPool.instance.execute(this);
    	  else
    	  {
    		  IWriteStreamsToFile = new WriteStreamsToFile();
    		  IWriteStreamsToFile.start();
    	  }
    	  while(!recordingRuns)
    	  {	 	
    		  try {TimeUnit.MILLISECONDS.sleep(10);} // suspend the thread, for the right sampling frequency, and allow the other threads to send their data
    		  catch (InterruptedException e) {};
    	  }
    	  super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  if(recordingShouldRun)
    	  {
    		  samplingShouldPause = true;
    		  super.pause();
    	  }
     }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  if(samplingShouldPause)
    	  {
    		  samplingShouldPause = false;
    		  super.resume();
    	  }
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  recordingShouldRun = false;
    	  samplingShouldPause = false;
    	  
    	  while(recordingRuns == true)
		  {	 	
			  try {TimeUnit.MILLISECONDS.sleep(10);} 
			  catch (InterruptedException e) {};
		  }
		  super.stop();
      }
      @Override
      public void run()
      {
    	  int buffer = 0;
    	  numberOfDataRecords = 0;
    	  startTimeOfSampling = System.nanoTime();
    	  recordingRuns = true;

    	  for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
    		  nextSampleTime[signalNumber] = 0;
    	  
    	  while(recordingShouldRun)
    	  {
    		//===================================================================================================
	    	// start a sampling thread for each signal
	    	//===================================================================================================
	   		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
	   		{
				dataSamples[signalNumber][buffer].reset();
				IBufferSampling[signalNumber] = new BufferSampling(signalNumber);
	  			IBufferSampling[signalNumber].start();
	   		}
    		//===================================================================================================
	    	// wait until all sampling threads are quit
	    	//=================================================================================================== 
	  		try
			{
				for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
					IBufferSampling[signalNumber].join();
			}catch(Exception e){}

	  		if(recordingShouldRun)
	  		{
	    		//===================================================================================================
		    	// write all sampling streams into the file body
		    	//===================================================================================================
	      		try 
	    		{
	      			OutputStream outputStream = new FileOutputStream ("data/edf/" + propFileName + propFileExtension, true);
	    			
	    			for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
	    				dataSamples[signalNumber][buffer].writeTo(outputStream); // write each sample of each signal into the file
	
	    			outputStream.close();
	    		} catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error appending data streams to file");}
	    		//===================================================================================================
		    	// write the correct number of records into the file header
		    	//=================================================================================================== 
	      		try
	        	{
	    	    	RandomAccessFile file = new RandomAccessFile("data/edf/" + propFileName + propFileExtension, "rw");
	    	    	file.seek(236); // go to position of "number of data records" in file
	    	    	file.writeBytes(spaceFilledString(String.valueOf(++numberOfDataRecords),8,false));
	    	    	file.close();
	        	}catch (IOException e) {AstericsErrorHandling.instance.reportInfo(this, "Error writing value for number of data records in file: " + propFileName);}
	  		}
    	  }
    	  recordingRuns = false;
      }

}