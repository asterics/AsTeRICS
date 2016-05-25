

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
//import java.util.logging.Logger;

import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
//import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
//import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
//import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
//import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
//import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
//import eu.asterics.mw.services.AREServices;

/**
 * This plugin records the incoming data into an EDF file
 * 
 * Have a look at "http://www.edfplus.info/specs/edf.html" for full file specification
 *  
 * @author Christian Strasak [christian.strasak@technikum-wien.at]
 *         Date: 15.07.2014
 */
public class WriteEDFInstance extends AbstractRuntimeComponentInstance implements Runnable
{
	//===================================================================================================
  	// constants
  	//=================================================================================================== 
	static final int RECORD_DURATION = 1;
	static final int NUMBER_OF_CHANNELS = 8;
	static final int NUMBER_OF_STREAM_BUFFERS = 10;
	
	static final String FILE_EXTENSION = ".edf";
	static final String FILE_PATH = "data/edf/";
	//===================================================================================================
  	// variables
  	//=================================================================================================== 
	boolean recordingShouldRun = false;
	boolean samplingShouldPause = false;
	boolean recordingRuns = true;
	boolean propReSampling = false;
	
	int numberOfSignals = NUMBER_OF_CHANNELS;
	
	long startTimeOfRecording = 0;
	long numberOfDataRecords = 0;
	long pauseTime = 0;
	long lastPauseTimePoint = 0;
	
	String propPatientID = "unknown";
	String propFileName = "record";
	String filePathNameExtension = "";
	//===================================================================================================
  	// arrays
  	//=================================================================================================== 
	boolean signalIsActivated[] = new boolean[NUMBER_OF_CHANNELS];
	boolean channelIsActivated[] = new boolean[NUMBER_OF_CHANNELS];

	int streamBufferWritePointer[] = new int[NUMBER_OF_CHANNELS];
	int signalOfInputChannel[] = new int[NUMBER_OF_CHANNELS]; // for assigning the channels IDs to signal IDs
	int propSamplingRateCH[] = new int[NUMBER_OF_CHANNELS];
	int samplingRateOfSignal[] = new int[NUMBER_OF_CHANNELS];

	int propPhysicalMinimumCH[] = new int[NUMBER_OF_CHANNELS];
	int propPhysicalMaximumCH[] = new int[NUMBER_OF_CHANNELS];
	int propDigitalMinimumCH[]  = new int[NUMBER_OF_CHANNELS];
	int propDigitalMaximumCH[]  = new int[NUMBER_OF_CHANNELS];
	
	int physicalMinimumOfSignal[] = new int[NUMBER_OF_CHANNELS];
	int physicalMaximumOfSignal[] = new int[NUMBER_OF_CHANNELS];
	int digitalMinimumOfSignal[] = new int[NUMBER_OF_CHANNELS];
	int digitalMaximumOfSignal[] = new int[NUMBER_OF_CHANNELS];

	long nextSampleTime[] = new long[NUMBER_OF_CHANNELS];
	
	double samplesBuffer[] = new double[NUMBER_OF_CHANNELS];

	ByteArrayOutputStream [][] samplesOfSignal = new ByteArrayOutputStream[NUMBER_OF_CHANNELS][NUMBER_OF_STREAM_BUFFERS]; //streams for the samples data
	BufferSampling[] BufferSamplingInstance = new BufferSampling[NUMBER_OF_CHANNELS];
	WriteStreamsToFile WriteStreamsToFileInstance;

   /**
    * The class constructor.
    */
    public WriteEDFInstance()
    {
    	for(int channelNumber = 0; channelNumber < NUMBER_OF_CHANNELS; channelNumber++)
    	{
    		for(int streamBufferNumber = 0; streamBufferNumber < NUMBER_OF_STREAM_BUFFERS; streamBufferNumber++)
    			samplesOfSignal[channelNumber][streamBufferNumber] = new ByteArrayOutputStream();

    		channelIsActivated[channelNumber] = false;
    		signalIsActivated[channelNumber] = false;
    		samplesBuffer[channelNumber] = 0;
    		nextSampleTime[channelNumber] = 0;
    		signalOfInputChannel[channelNumber] = channelNumber;
    	}
    }
	
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
			final int oldValue = propPhysicalMinimumCH[0];
			propPhysicalMinimumCH[0] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH2".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[1];
			propPhysicalMinimumCH[1] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[2];
			propPhysicalMinimumCH[2] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[3];
			propPhysicalMinimumCH[3] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[4];
			propPhysicalMinimumCH[4] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[5];
			propPhysicalMinimumCH[5] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[6];
			propPhysicalMinimumCH[6] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMinimumCH[7];
			propPhysicalMinimumCH[7] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[0];
			propPhysicalMaximumCH[0] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[1];
			propPhysicalMaximumCH[1] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[2];
			propPhysicalMaximumCH[2] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[3];
			propPhysicalMaximumCH[3] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[4];
			propPhysicalMaximumCH[4] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[5];
			propPhysicalMaximumCH[5] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[6];
			propPhysicalMaximumCH[6] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("physicalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propPhysicalMaximumCH[7];
			propPhysicalMaximumCH[7] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH1".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[0];
			propDigitalMinimumCH[0] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH2".equalsIgnoreCase(propertyName))
		{  
			final int oldValue = propDigitalMinimumCH[1];
			propDigitalMinimumCH[1] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH3".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[2];
			propDigitalMinimumCH[2] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH4".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[3];
			propDigitalMinimumCH[3] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH5".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[4];
			propDigitalMinimumCH[4] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH6".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[5];
			propDigitalMinimumCH[5] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH7".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[6];
			propDigitalMinimumCH[6] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMinimumCH8".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMinimumCH[7];
			propDigitalMinimumCH[7] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH1".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[0];
			propDigitalMaximumCH[0] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH2".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[1];
			propDigitalMaximumCH[1] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH3".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[2];
			propDigitalMaximumCH[2] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH4".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[3];
			propDigitalMaximumCH[3] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH5".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[4];
			propDigitalMaximumCH[4] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH6".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[5];
			propDigitalMaximumCH[5] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH7".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[6];
			propDigitalMaximumCH[6] = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("digitalMaximumCH8".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propDigitalMaximumCH[7];
			propDigitalMaximumCH[7] = Integer.parseInt((String)newValue);
			return oldValue;
		}
        return null;
    }


     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipCH1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[0] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[0]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[0]);
			}
		}
	};
	private final IRuntimeInputPort ipCH2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[1] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[1]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[1]);
			}
		}
	};
	private final IRuntimeInputPort ipCH3  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[2] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[2]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[2]);
			}
		}
	};
	private final IRuntimeInputPort ipCH4  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[3] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[3]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[3]);
			}
		}
	};
	private final IRuntimeInputPort ipCH5  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[4] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[4]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[4]);
			}
		}
	};
	private final IRuntimeInputPort ipCH6  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[5] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[5]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[5]);
			}
		}
	};
	private final IRuntimeInputPort ipCH7  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[6] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[6]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[6]);
			}
		}
	};
	private final IRuntimeInputPort ipCH8  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			if( channelIsActivated[7] )
			{
				if(propReSampling == true)
					samplesBuffer[signalOfInputChannel[7]] = ConversionUtils.doubleFromBytes(data);
				else
					storeValueIntoStreams(data,signalOfInputChannel[7]);
			}
		}
	};
	
	/**
     * converts parameter doubleData into bytes and appends it to the stream of parameter signalNumber
     * @author 				Christian Strasak
     * @param doubleData	value, which should be appended to a stream
     * @param signalNumber	number of the signal stream, in which doubleData should be appended 
     * @return              void
     */
	public void appendSampleToStream (double doubleData, int signalNumber)
	{
    	int dataInt = (int)((doubleData - physicalMinimumOfSignal[signalNumber]) / (physicalMaximumOfSignal[signalNumber] - physicalMinimumOfSignal[signalNumber]) * (digitalMaximumOfSignal[signalNumber] - digitalMinimumOfSignal[signalNumber]) + digitalMinimumOfSignal[signalNumber]);

    	//===================================================================================================
		// splitting the data into two bytes
		//===================================================================================================
    	byte[] value = new byte [2];
        value[0] = (byte) (dataInt & 0xFF);
        value[1] = (byte) ((dataInt >> 8) & 0xFF);
        
		//===================================================================================================
		// writing the two bytes into a stream buffer
		//===================================================================================================
        samplesOfSignal[signalNumber][streamBufferWritePointer[signalNumber]].write(value[0]);
        samplesOfSignal[signalNumber][streamBufferWritePointer[signalNumber]].write(value[1]);

	}

	/**
     * writes parameter data into a stream, manages the stream buffers and deactivates a signal, if a buffer-overflow occurs
     * @author 				Christian Strasak
     * @param data			value which should be stored
     * @param signalNumber	number of the signal stream, in which data should be appended 
     * @return              void
     */
    public void storeValueIntoStreams(byte[] data, int signalNumber)
    {
    	if(!samplingShouldPause && signalIsActivated[signalNumber])
    	{
    		appendSampleToStream (ConversionUtils.doubleFromBytes(data), signalNumber);
    		
	        if( (samplesOfSignal[signalNumber][streamBufferWritePointer[signalNumber]].size() / 2) >= samplingRateOfSignal[signalNumber] )
	        {
				//===================================================================================================
				// incrementing the stream buffer, if the current is full
		        // the buffer is realized as a ring buffer, so it will be set to zero after each cycle
				//===================================================================================================
	        	if(streamBufferWritePointer[signalNumber] < (NUMBER_OF_STREAM_BUFFERS - 1) )
	        		streamBufferWritePointer[signalNumber] ++;
	        	else streamBufferWritePointer[signalNumber] = 0;
	        	
				//===================================================================================================
				// checking if the next stream buffer is empty
	        	// if it is not empty, the file storage of this buffer was not completed yet
		        // this may be caused by a too slow sampling rate property
	        	// hence this signal will be deactivated
				//===================================================================================================
	        	if( samplesOfSignal[signalNumber][streamBufferWritePointer[signalNumber]].size() != 0 )
	        	{
	        		AstericsErrorHandling.instance.getLogger().warning("writeData: signal " + signalNumber + " has been deactivated, because the buffer has been overflowed");
	        		signalIsActivated[signalNumber] = false;
					//	        		channelIsActivated[signalNumber] = false;
					//===================================================================================================
					// reseting all stream buffers of this signal
			        // and filling them with zeros
					//===================================================================================================
	        		for(int streamBufferNumber = 0; streamBufferNumber < NUMBER_OF_STREAM_BUFFERS; streamBufferNumber++)
	        		{
	        			samplesOfSignal[signalNumber][streamBufferNumber].reset(); // reset all stream buffers for this signal
        				for( int sampleNumber = 0; sampleNumber < (samplingRateOfSignal[signalNumber] * 2); sampleNumber++ )
        					samplesOfSignal[signalNumber][streamBufferNumber].write(0);  // fill each stream with zeros
	        		}
	        	}
	        }
    	}
    }

	/**
	* called when model is started.
	*/
	@Override
	public void start()
	{

		int signalNumber = 0;
		//===================================================================================================
		// Mapping the channels to the signals
		// If the frequency of a channel is 0, it will be deactivated
		//===================================================================================================
		for(int channelNumber = 0; channelNumber < NUMBER_OF_CHANNELS; channelNumber ++ )
		{
			if( propSamplingRateCH[channelNumber] > 0 )
			{
				signalIsActivated[signalNumber] = true;

				samplingRateOfSignal[signalNumber] = propSamplingRateCH[channelNumber];
				physicalMinimumOfSignal[signalNumber] = propPhysicalMinimumCH[channelNumber];
				physicalMaximumOfSignal[signalNumber] = propPhysicalMaximumCH[channelNumber];
				digitalMinimumOfSignal[signalNumber] = propDigitalMinimumCH[channelNumber];
				digitalMaximumOfSignal[signalNumber] = propDigitalMaximumCH[channelNumber];
				
				signalOfInputChannel[channelNumber] = signalNumber;
				
				signalNumber++;
				channelIsActivated[channelNumber] = true;
			}
			else
			{
				channelIsActivated[channelNumber] = false;
			}
		}	
		numberOfSignals = signalNumber;
		
		if ( numberOfSignals == 0 )
		{
			AstericsErrorHandling.instance.reportError(this, "number of signals is 0. No data will be recorded");
			return;
		}

		filePathNameExtension = FILE_PATH + propFileName + FILE_EXTENSION;
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		DateFormat timeFormat = new SimpleDateFormat("HH.mm.ss");
		Date date = new Date();
		//===================================================================================================
		// Setting the global header information for the EDF file
		//===================================================================================================
		String formatVersion = appendSpaceToString("0",8,false); // 0 indicates that it is written in EDF-format
		String patientID = appendSpaceToString(propPatientID,80,false);
		String recordingID = appendSpaceToString("AsTeRICS",80,false);
		String startDate = dateFormat.format(date);
		String startTime = timeFormat.format(date);
		String headerBytes = appendSpaceToString(String.valueOf(256 * (1 + numberOfSignals)),8,false);//Size of header in bytes (to find the recording start)
		String reserved= appendSpaceToString("",44,false);
		String numberOfRecords= appendSpaceToString("-1",8,false);
		String durationOfRecords= appendSpaceToString(String.valueOf(RECORD_DURATION),8,false);
		String signals = appendSpaceToString(String.valueOf(numberOfSignals),4,false);
		//===================================================================================================
		// Setting the signal-specific header information for the EDF file
		//=================================================================================================== 
		String label = appendSpaceToString("label",16,true); //(e.g. EEG Fpz-Cz or Body temp)
		String transducerType = appendSpaceToString("transducerType",80,true); // (e.g. AgAgCl electrode) 
		String physicalDimension = appendSpaceToString("units",8,true); // (e.g. uV or degreeC)  
		
		String physicalMinimum = "";
		for(signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
				physicalMinimum += appendSpaceToString(String.valueOf(physicalMinimumOfSignal[signalNumber]),8,false); 

		String physicalMaximum = "";
		for(signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
				physicalMaximum += appendSpaceToString(String.valueOf(physicalMaximumOfSignal[signalNumber]),8,false); 

		String digitalMinimum = "";
		for(signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
				digitalMinimum += appendSpaceToString(String.valueOf(digitalMinimumOfSignal[signalNumber]),8,false); 

		String digitalMaximum = "";
		for(signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
				digitalMaximum += appendSpaceToString(String.valueOf(digitalMaximumOfSignal[signalNumber]),8,false);  

		String prefiltering = appendSpaceToString("preFiltering",80,true); // (e.g. HP:0.1Hz LP:75Hz)
		
		String numberOfSamples = "";
		for(signalNumber = 0; signalNumber < numberOfSignals; signalNumber ++)
				numberOfSamples += appendSpaceToString(String.valueOf(samplingRateOfSignal[signalNumber]),8,false); 
		
		String reserved2 = appendSpaceToString("",32,true); // reserved due to EDF specification     
		//===================================================================================================
		// Writing the header data, defined above, into file
		//===================================================================================================
		try { new File(FILE_PATH).mkdirs(); } 
		catch (Exception e) {AstericsErrorHandling.instance.getLogger().severe("Error: could not create subfolders");}
		
		BufferedWriter out = null;
		try 
		{
			out = new BufferedWriter(new FileWriter(filePathNameExtension));
			out.write(formatVersion + "" 
				+ patientID 
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
		} 
		catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error writing header to file");}
    	  
		try {out.close();} catch (IOException e) {AstericsErrorHandling.instance.reportError(this, "Error closing file");}
		//===================================================================================================
		// Starting the recording procedure
		//===================================================================================================    	  
		recordingShouldRun = true; // sends the recording threads the start signal
		numberOfDataRecords = 0;
    	
    	startTimeOfRecording = System.nanoTime();
    	pauseTime = 0;

    	if(propReSampling == true)
			AstericsThreadPool.instance.execute(this);
		else
    	{
			WriteStreamsToFileInstance = new WriteStreamsToFile();
    		WriteStreamsToFileInstance.start();
    	}
		
    	while(!recordingRuns) // waits until the threads are started before calling the "super.start()"
    	{	 	
			try {TimeUnit.MILLISECONDS.sleep(1);}
    		catch (InterruptedException e) {};
		}

    	
    	super.start();
    }
	
	/**
	* This class checks every record time, if the buffers for the signals are full
	* if full: the store method will be called
	* if not full: the corresponding signal will be deactivated
	**/
    public class WriteStreamsToFile extends Thread
    {
    	/**Constructor
    	 * 
    	 * resets the streams every time (when the plugin has been restarted)
    	 */

    	public WriteStreamsToFile ()
    	{
    		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
    		{
        		for(int streamBuffer = 0; streamBuffer < NUMBER_OF_STREAM_BUFFERS; streamBuffer ++)
        			samplesOfSignal[signalNumber][streamBuffer].reset();
        		streamBufferWritePointer[signalNumber] = 0;
    		}
    	}
    	public void run ()
    	{
    		int streamBufferReadPointer = 0;
    		long recordDurationInNanoSeconds = RECORD_DURATION * 1000 * 1000 * 1000;
    		long nextRecordTime = startTimeOfRecording + recordDurationInNanoSeconds;
    		long recordingTimeBuffer = 100 * 1000 * 1000; // 1 ms buffer
    		long timeUntilNextStorage = 0;
    		
    		while(recordingShouldRun)
    		{

    			recordingRuns = true;
    			
			    while(samplingShouldPause)
    	        {
    	        	try{TimeUnit.MILLISECONDS.sleep(1);} catch (InterruptedException e){};
    	        }
				//===================================================================================================
				// Waiting until the next record time (when the stream buffers should be full) + recording time buffer
				//===================================================================================================
    			nextRecordTime += recordDurationInNanoSeconds;
    			timeUntilNextStorage = nextRecordTime + pauseTime - System.nanoTime() + recordingTimeBuffer;

        		if( timeUntilNextStorage > 0)
        		{
	        		try{ TimeUnit.NANOSECONDS.sleep(timeUntilNextStorage); }
					catch (Exception e) {AstericsErrorHandling.instance.getLogger().warning("could not sleep until next record");};
        		}	
        		else AstericsErrorHandling.instance.getLogger().warning("time until next record: " + timeUntilNextStorage);
        		
//        		AstericsErrorHandling.instance.getLogger().info("read buffer pointer: " + streamBufferReadPointer);
        		
        		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
        		{
        			
//        			AstericsErrorHandling.instance.getLogger().info("write buffer pointer of signal " + signalNumber + ": " + streamBufferWritePointer[signalNumber]);
        			//===================================================================================================
        			// checking if the buffers of the current cycle of all signals are already full
        			// if not, than the corresponding signal will be deactivated
        			// and all stream buffers of this signal will be reseted and filled with zeors
        			// this could be caused by a too fast frequency property
        			//===================================================================================================
					if( signalIsActivated[signalNumber] && (samplesOfSignal[signalNumber][streamBufferReadPointer].size() / 2) < samplingRateOfSignal[signalNumber] )
					{
						signalIsActivated[signalNumber] = false;
						
						if( samplesOfSignal[signalNumber][streamBufferReadPointer].size() == 0 )
							AstericsErrorHandling.instance.getLogger().warning("signal " + signalNumber + " has been deactivated, because the buffer was empty at the storage timepoint");
						else AstericsErrorHandling.instance.getLogger().warning("signal " + signalNumber + " has been deactivated, because the buffer was not full at the storage timepoint");
						
						for(int streamBufferNumber = 0; streamBufferNumber < NUMBER_OF_STREAM_BUFFERS; streamBufferNumber++)
		        		{
		        			samplesOfSignal[signalNumber][streamBufferNumber].reset(); // reset all stream buffers for this signal
	        				for( int sampleNumber = 0; sampleNumber < (samplingRateOfSignal[signalNumber] * 2); sampleNumber++ )
	        					samplesOfSignal[signalNumber][streamBufferNumber].write(0);  // fill each stream with zeros
		        		}
					}
        		}

        		if(recordingShouldRun)
	     			writeDataStreamsToFile(streamBufferReadPointer);
	    		
				//===================================================================================================
				// Incrementing the read-buffer-pointer
				//===================================================================================================
	    		if(streamBufferReadPointer < (NUMBER_OF_STREAM_BUFFERS - 1) )
	        		streamBufferReadPointer ++;
	        	else streamBufferReadPointer = 0;
    		}
    		recordingRuns = false;
        }
    }
	
	/**
	 * thread for the reSampling method
	 * this method calls threads for every signal, which will handle the incoming data of the input ports
	 * the called threads will run for the defined record time and then quit
	 * it waits until all called threads have quit and restarts them until the stop button has been pressed 
	*/
	@Override
    public void run()
    {
    	int streamBufferWritePointer = 0;
    	recordingRuns = true;
 		
    	for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
			nextSampleTime[signalNumber] = 0;

		//===================================================================================================
	    // Looping every record time
	    //===================================================================================================
		while(recordingShouldRun)
    	{
    		//===================================================================================================
	    	// Starting a sampling thread for each signal
	    	//===================================================================================================
	   		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
	   		{
				samplesOfSignal[signalNumber][streamBufferWritePointer].reset();
				BufferSamplingInstance[signalNumber] = new BufferSampling(signalNumber);
	  			BufferSamplingInstance[signalNumber].start();
	   		}
    		//===================================================================================================
	    	// Waiting until all sampling threads are quit
	    	//=================================================================================================== 
	  		try
			{
				for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
					BufferSamplingInstance[signalNumber].join();
			} catch(Exception e){}

	  		if(recordingShouldRun)
				writeDataStreamsToFile(streamBufferWritePointer);
    	  }
    	  recordingRuns = false;
      }
	
	/**
	 * this class reads the buffer of a input port periodically (defined by the sampling rate)
	 * after the defined record time it calls the store method and quits
	 * {@value signalNumberParameter number of the signal, which will be stored}
	*/
    public class BufferSampling extends Thread
    {
    	
    	int signalNumber = 0;

    	/**Constructor
    	 * 
    	 * @param signalNumberParameter = number of the signal to process
    	 */
    	public BufferSampling (int signalNumberParameter)
    	{
    		this.signalNumber = signalNumberParameter;
    	}
    	
    	public void run()
    	{
        	long timeOffset = 0;
        	long timeUntilNextSample = 0;
        	long periodDuration = (samplingRateOfSignal[signalNumber] < 1) ? 0 : (1000 * 1000 * 1000 / samplingRateOfSignal[signalNumber]);
        	
    		for(int sampleNumber = 1; sampleNumber <= samplingRateOfSignal[signalNumber]; sampleNumber ++)
    		{
	    		//===================================================================================================
		    	// Calculating the next Sampling timepoint
		    	//=================================================================================================== 
    			timeOffset = System.nanoTime() - startTimeOfRecording;
				nextSampleTime[signalNumber] += periodDuration;
 	    		timeUntilNextSample = nextSampleTime[signalNumber] - timeOffset;
	    		//===================================================================================================
		    	// Waiting until the next Sampling timepoint has been reached
		    	//=================================================================================================== 
 	    		if(timeUntilNextSample > 0)
 	    		{
	 	    		try {TimeUnit.NANOSECONDS.sleep(timeUntilNextSample);} 	catch (InterruptedException e) {};
 	    		}
 	    		
 	    		appendSampleToStream ( samplesBuffer[signalNumber] , signalNumber );
 	    		
	    		//===================================================================================================
		    	// Waiting if the pause button of the ACS/ARE has been pressed
		    	//=================================================================================================== 
    	        if(samplingShouldPause)
    	        {
	    	        while(samplingShouldPause)
	    	        {
	    	        	try{TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e){};
	    	        }
		    		//===================================================================================================
			    	// Correcting the time and sampling offset after the pause button has been pressed
			    	//=================================================================================================== 
	    	        startTimeOfRecording = System.nanoTime();
	    	        nextSampleTime[signalNumber] = 0;
    	        }
    		}
    	}
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
    		  lastPauseTimePoint = System.nanoTime();
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
    		  pauseTime += (System.nanoTime() - lastPauseTimePoint); 
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
	  
      /**
       * this method appends the values of the current stream buffer pointer, of all signals, to the file
       * @param bufferPointer pointer to the stream buffers
       */
	public void writeDataStreamsToFile (int bufferPointer)
	{
		
		//===================================================================================================
		// Writing all sampling streams into the file body
		//===================================================================================================
		try 
		{
			OutputStream outputStream = new FileOutputStream (filePathNameExtension, true);
			
			for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
			{
				//===================================================================================================
				// Checking if the buffers of the current read-pointer of each signal are full
				// when not: overwriting them with zeros, before writing to file
				// because the number of samples of each record must be the same
				//===================================================================================================
				if( (samplesOfSignal[signalNumber][bufferPointer].size() / 2) < samplingRateOfSignal[signalNumber] )
				{
					samplesOfSignal[signalNumber][bufferPointer].reset();

					for( int sampleNumber = 0; sampleNumber < (samplingRateOfSignal[signalNumber] * 2); sampleNumber++ )
    					samplesOfSignal[signalNumber][bufferPointer].write(0);  
				}

				samplesOfSignal[signalNumber][bufferPointer].writeTo(outputStream); 
				samplesOfSignal[signalNumber][bufferPointer].reset();
			}

			outputStream.close();
		} catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error appending data streams to file");}
		//===================================================================================================
		// Writing the correct number of records into the file header
		//=================================================================================================== 
		try
		{
			RandomAccessFile file = new RandomAccessFile(filePathNameExtension, "rw");
			file.seek(236); // go to position of "number of data records" in file
			file.writeBytes(appendSpaceToString(String.valueOf(++numberOfDataRecords),8,false));
			file.close();
		}catch (IOException e) {AstericsErrorHandling.instance.getLogger().info("Error writing value for number of data records in file: " + propFileName);}
	}
	  
	/**
	 * this method appends several " " (spaces) to [value], so they can be stored in the correct length into the header of an edf file
	 * @param value value to which the spaces will be appended
	 * @param length the correct length (defined by the EDF specification) 
	 * @param multi if true: this will be done [numberOfSignals] times, else: only once
	 * @return value appended by " ", if it was shorter than [length]
	 */
	public String appendSpaceToString(String value, final int length, boolean multi)
	{
		int signals = multi ? numberOfSignals : 1;
		String newString = "";
		
		for(int signalNumber = 0; signalNumber < signals; signalNumber++)
		{
			if( value.length() > length )
				AstericsErrorHandling.instance.getLogger().warning("value " + value + " will be cut on the right side, because it is too big to write it in the file");
			
			try { newString += value.substring(0, length); }
			catch ( IndexOutOfBoundsException e) { newString += value; }
			
			for (int character = value.length() ; character < length ; character ++)
				newString += " ";
		}
			
		return newString;
	}
}
