

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

package eu.asterics.component.sensor.readedf;


import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.gui.ErrorLogPane;
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
 * This plugin reads data from an edf-file and sends it to its output ports
 * 
 * Have a look at "http://www.edfplus.info/specs/edf.html" for full edf-file specification
 *  
 * @author Christian Strasak [christian.strasak@technikum-wien.at]
 *         Date: 15.05.2014
 */

public class ReadEDFInstance extends AbstractRuntimeComponentInstance 
{
	String propFileName = "";
	String propFileExtension = ".edf";
	
	RandomAccessFile filePointer = null;
	
	boolean samplingRuns = false;
	boolean samplingShouldRun = false;
	boolean samplingShouldPause = false;
	long startTime = 0; 
	
	int numberOfSignals = 32; // number of recorded signals in the file (number will be replaced after the file has been read)
	int numberOfDataRecords = 0; 
	int headerSize = 0;
	int recordSize = 0; // = number of samples of each signal per record

	int samplesPerRecord[] = new int[numberOfSignals];
	float periodDuration[] = new float[numberOfSignals]; // period duration for the samples of each signal
	final OutputPort[] opCH = new OutputPort[numberOfSignals]; 
	signalTransmitClass[] signalTransmitInstance = new signalTransmitClass[numberOfSignals];

	float physicalMin[] = new float[numberOfSignals];
	float physicalMax[] = new float[numberOfSignals];
	float digitalMin[] = new float[numberOfSignals];
	float digitalMax[] = new float[numberOfSignals];
	
	/**
    * The class constructor.
    */
	 public ReadEDFInstance()
	 {
    	for(int i = 0; i < numberOfSignals; i++)
    	{
//    		signalTransmitInstance[i] = new signalTransmitClass(i);
    		opCH[i] = new OutputPort();
    		physicalMin[i] = 0;
    		physicalMax[i] = 0;
    		digitalMin[i] = 0;
    		digitalMax[i] = 0;
    	}
	 }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("cH1".equalsIgnoreCase(portID))
		{
			return opCH[0];
		}
		if ("cH2".equalsIgnoreCase(portID))
		{
			return opCH[1];
		}
		if ("cH3".equalsIgnoreCase(portID))
		{
			return opCH[2];
		}
		if ("cH4".equalsIgnoreCase(portID))
		{
			return opCH[3];
		}
		if ("cH5".equalsIgnoreCase(portID))
		{
			return opCH[4];
		}
		if ("cH6".equalsIgnoreCase(portID))
		{
			return opCH[5];
		}
		if ("cH7".equalsIgnoreCase(portID))
		{
			return opCH[6];
		}
		if ("cH8".equalsIgnoreCase(portID))
		{
			return opCH[7];
		}

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
		if ("filename".equalsIgnoreCase(propertyName))
		{
			return propFileName;
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
		if ("filename".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFileName;
			propFileName = (String)newValue;
			return oldValue;
		}

        return null;
    }
    
    public class OutputPort extends DefaultRuntimeOutputPort

  	{
  		public void sendData(double data)
  		{     			
  			super.sendData(ConversionUtils.doubleToBytes(data));
  		}
  	}
    
    /**
     * reads the samples from a stream, converts it and sends it to the output ports
     * @param number of the output port (parameter realised through the constructor)
     */
    public class signalTransmitClass extends Thread 
    {
    	int signalNumber = 0; 
    	long sampleCounter = 0;
    	long nextSampleTime = 0;
    	long timeOffset = 0;
    	long pauseTime = 0;
    	long timeUntilNextSample = 0;
    	
    	/**Constructor
    	 * 
    	 * @param signalNumberParameter = number of the signal to process
    	 */
    	public signalTransmitClass (int signalNumberParameter)
    	{
    		this.signalNumber = signalNumberParameter; // to declare for which signal this object is used for
    	}
    	
    	public void run()
    	{
			int sample = 0;
			int signalSamplesOffset = 0;
			samplingRuns = true;
			RandomAccessFile filePointerInstance = null;
			
			try	{ filePointerInstance = new RandomAccessFile(propFileName , "r");}
	    	catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error reading file: " + propFileName);}

			for(int i = 0; i < signalNumber; i++)
				signalSamplesOffset += (samplesPerRecord[i] * 2); // to determine the offset of each signal-start in a record

			for(long recordNumber = 0;(recordNumber < numberOfDataRecords) && samplingShouldRun; recordNumber++) 
			{		        	
				for(long sampleNumber = 0;(sampleNumber < samplesPerRecord[signalNumber]) && samplingShouldRun; sampleNumber++)
	    		{
					try { filePointerInstance.seek(headerSize + (recordSize * recordNumber) + signalSamplesOffset + (2 * sampleNumber));} //go to the first position of the signal records in the file
					catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Could not read from file for signal number: " + signalNumber);};
					//===================================================================================================
			    	//read the bytes from file, corresponding to a sample, and merge them
					//===================================================================================================
					try 
					{
						sample = filePointerInstance.read() ;
						sample += (filePointerInstance.read() * 256); // read the next byte and insert it, in front of the last byte
					} catch (IOException e){};
	    			//===================================================================================================
	    			// undo two's complement if value is negative
	    			//===================================================================================================
	 	    		if(sample > 32767) 
	 	    		{ 
	 	    			sample -= 1;
	 	    		 	sample = sample ^ 0xFFFF;
	 	    		 	sample *= -1;
	 	    		}
	 	    		double physicalSample = (((double)sample - digitalMin[signalNumber]) / (digitalMax[signalNumber] - digitalMin[signalNumber]) * (physicalMax[signalNumber] - physicalMin[signalNumber]) + physicalMin[signalNumber]);
	 	    		timeOffset = System.nanoTime() - (startTime + pauseTime);
					nextSampleTime = (long)(periodDuration[signalNumber] * sampleCounter * 1000 * 1000);
	 	    		timeUntilNextSample = nextSampleTime - timeOffset;
	 	    		if(timeUntilNextSample > 0)
	 	    		{
		 	    		try {TimeUnit.NANOSECONDS.sleep(timeUntilNextSample);} // suspend the thread, for the right sampling frequency, and allow the other threads to send their data
		 	    		catch (InterruptedException e) {};
	 	    		}
	 	    		opCH[signalNumber].sendData(physicalSample); // send the sample to the corresponding output port
	 	    		sampleCounter++;

	 	    		if(samplingShouldPause)
	    	        {
	 	    			long timePoint = System.nanoTime();
		    	        while(samplingShouldPause)
		    	        {
		    	        	try{TimeUnit.MILLISECONDS.sleep(10);} catch (InterruptedException e){};
		    	        }
		    	        pauseTime += System.nanoTime() - timePoint;
	    	        }

	    		}
			}
     	  if (filePointer != null)
    	  {
    		  try {filePointer.close();}
    		  catch (IOException e) {AstericsErrorHandling.instance.getLogger().severe("Error closing file: " + propFileName);}
    	  }
     	  samplingRuns = false;
    	}
    }
    /**
     * Reads ASCII values from file and returns the corresponding int value
     * @param position = position of the data in the file
     * @param numberOfByts = number of bytes to be read
     * @return int-value of the read bytes
     */
    float readFromFile(int position, int numberOfBytes)
    {
    	
    	float value = 0;
    	int stateMachine = 0; // 0: no "." or "E" where found so far / 1: "." was found / 2: "E" was found
    	int decimalPosition = 1;
    	int power = 0;
    	byte[] asciiValue = new byte[numberOfBytes]; // for the characters in the file representing a number
		try
		{
			filePointer.seek(position); 
			filePointer.read(asciiValue); // read "numberOfBytes" Bytes
		}	
		catch (IOException e) {AstericsErrorHandling.instance.reportInfo(this, "Error reading file: " + propFileName);}
		//===================================================================================================
		// convert each of the ASCII-chars to a digit and merge them into a number
		//===================================================================================================
		for(int j = 0; j < numberOfBytes; j++)
		{
			switch (stateMachine)
			{
				case 0: 
					if((asciiValue[j] >= '0') && (asciiValue[j] <= '9')) 
					{
						asciiValue[j] -= '0';
						value *= 10;
						value += asciiValue[j];
					}
					else if (asciiValue[j] == '.')
						stateMachine = 1;
					else if((asciiValue[j] == 'e') || (asciiValue[j] == 'E'))
						stateMachine = 2;
					break;
				case 1:
					if((asciiValue[j] >= '0') && (asciiValue[j] <= '9')) 
					{
						asciiValue[j] -= '0';
						decimalPosition *= 10;
						float temp = (float)asciiValue[j];
						float temp2 = temp / decimalPosition;
						value += temp2; 
					}
					else if((asciiValue[j] == 'e') || (asciiValue[j] == 'E'))
						stateMachine = 2;
					break;
				case 2:
					if((asciiValue[j] >= '0') && (asciiValue[j] <= '9')) 
					{
						asciiValue[j] -= '0';
						power *= 10;
						power += asciiValue[j];
					}
					break;
				default:
					value = -1;
					break;
			}
		}
		if(power > 0)
			value *= (float)Math.pow(10, power);
		if(asciiValue[0] == '-' ) 
			value *= -1;
		return value;
    }
    
	public List<String> getRuntimePropertyList(String key) 
	{

		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("filename")==0)
		{
			List<String> nextDir = new ArrayList<String>(); //Directories
			nextDir.add("data/edf");	
//			nextDir.add("data/sounds");	
			while(nextDir.size() > 0) 
			{
				File pathName = new File(nextDir.get(0)); 
				String[] fileNames = pathName.list();  // lists all files in the directory

				for(int i = 0; i < fileNames.length; i++) 
				{ 
					File f = new File(pathName.getPath(), fileNames[i]); // getPath converts abstract path to path in String, 
					// constructor creates new File object with fileName name   
					if (f.isDirectory()) 
					{  
						nextDir.add(f.getPath()); 
					} 
					else 
					{
						res.add(f.getPath());
					}
				} 
				nextDir.remove(0); 
			}  
		}
		return res;

	} 

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  recordSize = 0;
		  
    	try	{ filePointer = new RandomAccessFile(propFileName , "r");}
    	catch (IOException e) 
    	{ 
    		AstericsErrorHandling.instance.reportError(this, "Error reading file: " + propFileName);
    		return ;
    	}
    	//===================================================================================================
    	//read the header from the file
    	//===================================================================================================
    	numberOfDataRecords = (int)readFromFile(236, 8); //get the number of records, that have been caputured sequentially
    	numberOfSignals = (int)readFromFile(252, 4); // get the number of signals 
    	headerSize = 256 * (1 + numberOfSignals); // get the header size
    	final int numOfSamplesPosition = headerSize - numberOfSignals * (32 + 8); // get the position, where the number of samples is located
    	final float durationOfDataRecord = readFromFile(244, 8); //get the duration of a data record
      	//===================================================================================================
    	//get the data of the second header for the signal depending values (each signal has different attributes)
    	//===================================================================================================
    	for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
    	{
    		samplesPerRecord[signalNumber] = (int)readFromFile(numOfSamplesPosition + signalNumber * 8, 8); // get the "number of samples" of each signal
    		recordSize += (samplesPerRecord[signalNumber] * 2); // get the number of samples of all signals in one record
    		periodDuration[signalNumber] = durationOfDataRecord / samplesPerRecord[signalNumber]* 1000; // calculate the period duration [in ms] of each sample
    		physicalMin[signalNumber] = readFromFile(256 + numberOfSignals * 104 + signalNumber * 8,8);
    		physicalMax[signalNumber] = readFromFile(256 + numberOfSignals * 112 + signalNumber * 8,8);
    		digitalMin[signalNumber] = readFromFile(256 + numberOfSignals * 120 + signalNumber * 8,8);
    		digitalMax[signalNumber] = readFromFile(256 + numberOfSignals * 128 + signalNumber * 8,8);
   		}
    	//===================================================================================================
    	//Start a thread for each signal. It will read the corresponding samples and send it to its output port
    	//===================================================================================================
    	startTime = System.currentTimeMillis();
    	byte[] allRecordBytes = new byte[numberOfDataRecords * numberOfSignals * recordSize];
    	try {
    		filePointer.seek(headerSize);
    		filePointer.read(allRecordBytes); 	}
    	catch (IOException e) {	AstericsErrorHandling.instance.reportError(this, "Error reading file: " + propFileName);  	}
   		if (filePointer != null)
		{
			try {filePointer.close();}
			catch (IOException e) {AstericsErrorHandling.instance.reportError(this, "Error closing file");}
		}

//    	ErrorLogPane.appendLog("Datei eingelesen");
    	AstericsErrorHandling.instance.reportInfo(this, allRecordBytes.length + " samples have been read in " + (System.currentTimeMillis() - startTime) + " Milliseconds");
    	
    	startTime = System.nanoTime();
    	samplingShouldRun = true;

   		for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++)
   		{
   			signalTransmitInstance[signalNumber] = new signalTransmitClass(signalNumber);
   			signalTransmitInstance[signalNumber].start();
   		}
   		
   		while(!samplingRuns)
	    {	 	
   			try {TimeUnit.MILLISECONDS.sleep(10);} // suspend the thread, for the right sampling frequency, and allow the other threads to send their data
	  		catch (InterruptedException e) {};
	    }
//	  	super.start();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  if(samplingShouldRun)
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
    	  samplingShouldRun = false;
    	  samplingShouldPause = false;
    	  
    	  while(samplingRuns == true)
		  {	 	
			  try {TimeUnit.MILLISECONDS.sleep(10);} 
			  catch (InterruptedException e) {};
		  }
     	 
     	  if (filePointer != null)
    	  {
    		  try {filePointer.close();}
    		  catch (IOException e) {AstericsErrorHandling.instance.reportError(this, "Error closing file: " + propFileName);}
    	  }
	      super.stop();
      }
}