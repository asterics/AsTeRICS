

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

package com.starlab.component.processor.ssvepdetect;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;

/**
 * 
 * This plugin is in charge of evaluating the SSVEP response (up to 4 different frequencies)
 *  among the frequencies defined by the user. It also calculates the config file based on previously 
 *  recorded training file that will be used to evaluate the detection, and is in charge of updating
 *  its parameters according to the config file. 
 *  
 * @author David Ibanez [david.ibanez@starlab.es]
 *         Date: 30/08/2012
 *         Time: 16:32
 */
public class SSVEPDetectInstance extends AbstractRuntimeComponentInstance
{
	public native int nativeSSVEPDetection();
	
	public native void nativeSSVEPDetectionInit (double propSF1GO1, double propSF1GOz, double propSF1GO2,
			double propSF2GO1, double propSF2GOz, double propSF2GO2,
			double propSF3GO1, double propSF3GOz, double propSF3GO2,
			double propSF4GO1, double propSF4GOz, double propSF4GO2,
			int propStimFreq1,int propStimFreq2,int propStimFreq3,int propStimFreq4, 
			int propBestHarm1,int propBestHarm2,int propBestHarm3,int propBestHarm4);
	
	public native void nativesignalBuffering(double S1, double S2, double S3);
	

	static {
		System.loadLibrary("SSVEPDetection");
	}

	final IRuntimeOutputPort opFreqP1 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreqP2 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreqP3 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreqP4 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opTrainProcessReport  = new DefaultRuntimeOutputPort();
	
	final IRuntimeEventTriggererPort etpUpdatePanelsConfig = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpNonStimFreqD = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStimFreq1D = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStimFreq2D = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStimFreq3D = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStimFreq4D = new DefaultRuntimeEventTriggererPort();
	
	
	
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent()
	double propSF1GO1 = 1.0;
	double propSF1GOz = 1.0;
	double propSF1GO2 = 1.0;
	double propSF2GO1 = 1.0;
	double propSF2GOz = 1.0;
	double propSF2GO2 = 1.0;
	double propSF3GO1 = 1.0;
	double propSF3GOz = 1.0;
	double propSF3GO2 = 1.0;
	double propSF4GO1 = 1.0;
	double propSF4GOz = 1.0;
	double propSF4GO2 = 1.0;
	int propStimFreq1 = 20;
	int propStimFreq2 = 20;
	int propStimFreq3 = 20;
	int propStimFreq4 = 20;
	int propBestHarm1 = 20;
	int propBestHarm2 = 20;
	int propBestHarm3 = 20;
	int propBestHarm4 = 20;

	// declare member variables here
   int onBuffering;
   private double s1;
   private double s2;
   private double s3;
   int numR = 0;
   
   String UserName = "";
   String NumPanels = "";
   
   Thread thread = new SimpleThread("dummy");
 
  
   /**
    * The class constructor.
    */
    public SSVEPDetectInstance()
    {

    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("o1".equalsIgnoreCase(portID))
		{
			return ipO1;
		}
		if ("oz".equalsIgnoreCase(portID))
		{
			return ipOz;
		}
		if ("o2".equalsIgnoreCase(portID))
		{
			return ipO2;
		}
		if ("UserName".equalsIgnoreCase(portID))
		{
			return ipUserName;
		}
		if ("NumberOfPanels".equalsIgnoreCase(portID))
		{
			return ipNumOfPanels;
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
		if ("FreqP1".equalsIgnoreCase(portID))
		{
			return opFreqP1;
		}

		if ("FreqP2".equalsIgnoreCase(portID))
		{
			return opFreqP2;
		}
		
		if ("FreqP3".equalsIgnoreCase(portID))
		{
			return opFreqP3;
		}
		
		if ("FreqP4".equalsIgnoreCase(portID))
		{
			return opFreqP4;
		}

		if ("TrainProcessReport".equalsIgnoreCase(portID))
		{
			return opTrainProcessReport;
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
		if ("startStim".equalsIgnoreCase(eventPortID))
		{
			return elpStartStim;
		}
		if ("stopStim".equalsIgnoreCase(eventPortID))
		{
			return elpStopStim;
		}
		if ("UpdateFromConfigFile".equalsIgnoreCase(eventPortID))
		{
			return elpUpdateFromConfigFile;
		}		
		if ("CalculateConfigFile".equalsIgnoreCase(eventPortID))
		{
			return elpCalculateConfigFile;
		}			
		
        return null;
    }

    
    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
    	
    	
    	
    	if ("UpdatePanelsConfig".equalsIgnoreCase(eventPortID))
		{
			return etpUpdatePanelsConfig;
		}
		if ("nonStimFreqD".equalsIgnoreCase(eventPortID))
		{
			return etpNonStimFreqD;
		}
		if ("stimFreq1D".equalsIgnoreCase(eventPortID))
		{
			return etpStimFreq1D;
		}
		if ("stimFreq2D".equalsIgnoreCase(eventPortID))
		{
			return etpStimFreq2D;
		}
		if ("stimFreq3D".equalsIgnoreCase(eventPortID))
		{
			return etpStimFreq3D;
		}
		if ("stimFreq4D".equalsIgnoreCase(eventPortID))
		{
			return etpStimFreq4D;
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
		if ("sF1GO1".equalsIgnoreCase(propertyName))
		{
			return propSF1GO1;
		}
		if ("sF1GOz".equalsIgnoreCase(propertyName))
		{
			return propSF1GOz;
		}
		if ("sF1GO2".equalsIgnoreCase(propertyName))
		{
			return propSF1GO2;
		}
		if ("sF2GO1".equalsIgnoreCase(propertyName))
		{
			return propSF2GO1;
		}
		if ("sF2GOz".equalsIgnoreCase(propertyName))
		{
			return propSF2GOz;
		}
		if ("sF2GO2".equalsIgnoreCase(propertyName))
		{
			return propSF2GO2;
		}
		if ("sF3GO1".equalsIgnoreCase(propertyName))
		{
			return propSF3GO1;
		}
		if ("sF3GOz".equalsIgnoreCase(propertyName))
		{
			return propSF3GOz;
		}
		if ("sF3GO2".equalsIgnoreCase(propertyName))
		{
			return propSF3GO2;
		}
		if ("sF4GO1".equalsIgnoreCase(propertyName))
		{
			return propSF4GO1;
		}
		if ("sF4GOz".equalsIgnoreCase(propertyName))
		{
			return propSF4GOz;
		}
		if ("sF4GO2".equalsIgnoreCase(propertyName))
		{
			return propSF4GO2;
		}
		if ("stimFreq1".equalsIgnoreCase(propertyName))
		{
			return propStimFreq1;
		}
		if ("stimFreq2".equalsIgnoreCase(propertyName))
		{
			return propStimFreq2;
		}
		if ("stimFreq3".equalsIgnoreCase(propertyName))
		{
			return propStimFreq3;
		}
		if ("stimFreq4".equalsIgnoreCase(propertyName))
		{
			return propStimFreq4;
		}
		if ("bestHarm1".equalsIgnoreCase(propertyName))
		{
			return propBestHarm1;
		}
		if ("bestHarm2".equalsIgnoreCase(propertyName))
		{
			return propBestHarm2;
		}
		if ("bestHarm3".equalsIgnoreCase(propertyName))
		{
			return propBestHarm3;
		}
		if ("bestHarm4".equalsIgnoreCase(propertyName))
		{
			return propBestHarm4;
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
		if ("sF1GO1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF1GO1;
			propSF1GO1 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF1GOz".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF1GOz;
			propSF1GOz = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF1GO2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF1GO2;
			propSF1GO2 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF2GO1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF2GO1;
			propSF2GO1 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF2GOz".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF2GOz;
			propSF2GOz = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF2GO2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF2GO2;
			propSF2GO2 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF3GO1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF3GO1;
			propSF3GO1 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF3GOz".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF3GOz;
			propSF3GOz = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF3GO2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF3GO2;
			propSF3GO2 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF4GO1".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF4GO1;
			propSF4GO1 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF4GOz".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF4GOz;
			propSF4GOz = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("sF4GO2".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propSF4GO2;
			propSF4GO2 = Double.parseDouble((String)newValue);
			return oldValue;
		}
		if ("stimFreq1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStimFreq1;
			propStimFreq1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("stimFreq2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStimFreq2;
			propStimFreq2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("stimFreq3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStimFreq3;
			propStimFreq3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("stimFreq4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propStimFreq4;
			propStimFreq4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("bestHarm1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBestHarm1;
			propBestHarm1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("bestHarm2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBestHarm2;
			propBestHarm2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("bestHarm3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBestHarm3;
			propBestHarm3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("bestHarm4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBestHarm4;
			propBestHarm4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipUserName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			
			UserName = ConversionUtils.stringFromBytes(data);
		}
	};
	
	private final IRuntimeInputPort ipNumOfPanels  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			
			NumPanels= ConversionUtils.stringFromBytes(data);
		}
	};
     
	private final IRuntimeInputPort ipO1  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{			

		}
	};
	private final IRuntimeInputPort ipOz  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{

		}
	};
	private final IRuntimeInputPort ipO2  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				/* // insert data reception handling here, e.g.: 
				s3 = ConversionUtils.doubleFromBytes(data); 
				
			    numR=numR+1;
				
				if (numR>=3)
				{
					numR=0;
					
			  		if (onBuffering==1)
			  		{
			  			nativesignalBuffering(s1, s2, s3);
			  		}
				}
				
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); */
		}
	};


     /**
      * Event Listerner Ports.
      */
	
	final IRuntimeEventListenerPort elpUpdateFromConfigFile = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{ 

			getFreqFromFile();
			
		}
	};
	
	
	final IRuntimeEventListenerPort elpCalculateConfigFile = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{ 
			thread = new SimpleThread("Thread");
			thread.start();
			
		}
	};
	
	
	final IRuntimeEventListenerPort elpStartStim = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{ 
	         //Initialize the params and buffer				
		    nativeSSVEPDetectionInit (propSF1GO1, propSF1GOz, propSF1GO2,
	    				propSF2GO1, propSF2GOz, propSF2GO2,
	    				propSF3GO1, propSF3GOz, propSF3GO2,
	    				propSF4GO1, propSF4GOz, propSF4GO2,
	    				propStimFreq1,propStimFreq2,propStimFreq3,propStimFreq4, 
	    				propBestHarm1,propBestHarm2,propBestHarm4, propBestHarm4);
		    
		    onBuffering = 1;
		    
		}
	};
	
	
	final IRuntimeEventListenerPort elpStopStim = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			int out; 
			
			onBuffering=0;	  
			
			out = nativeSSVEPDetection();
							 
			switch(out) 
			{
			case 0:
				etpNonStimFreqD.raiseEvent();
				break;
				
			case 1: 
				etpStimFreq1D.raiseEvent();
				break;
				 
			case 2:
				etpStimFreq2D.raiseEvent();			
				break;				
			case 3:
				etpStimFreq3D.raiseEvent();			
				break;				
			case 4:
				etpStimFreq4D.raiseEvent();
				break;
			}
		}
	};
 
	   
 
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          //Initialize the params and buffer
    		nativeSSVEPDetectionInit (propSF1GO1, propSF1GOz, propSF1GO2,
    				propSF2GO1, propSF2GOz, propSF2GO2,
    				propSF3GO1, propSF3GOz, propSF3GO2,
    				propSF4GO1, propSF4GOz, propSF4GO2,
    				propStimFreq1,propStimFreq2,propStimFreq3,propStimFreq4, 
    				propBestHarm1,propBestHarm2,propBestHarm3, propBestHarm4);
    		

          super.start();
           
          
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          super.pause();
      }

     /** 
      * called when model is resumed.
      */ 
      @Override  
      public void resume()
      { 
          super.resume(); 
      }   
      
     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {          
  
          super.stop();
      }
      
      /**
       * Synced port reception
       */
      @Override  
	public void syncedValuesReceived(HashMap<String, byte[]> dataRow) 
    {
  		
  		
  		for (String s: dataRow.keySet())
  		{

  			byte [] data = dataRow.get(s);
  			
  			if (s.equals("O1"))
  			{
  				s1 = ConversionUtils.doubleFromBytes(data);
  			}
  			if (s.equals("Oz"))
  			{
  				s2 = ConversionUtils.doubleFromBytes(data);
  			}
  			if (s.equals("O2"))
  			{
  				s3 = ConversionUtils.doubleFromBytes(data);
  			}
  			
  		}
  		
  		
	   if (onBuffering==1)
	   {
		   nativesignalBuffering(s1, s2, s3);		   
	   }
  		 
  	} 
      
      
      /**
       * Method to update current plugin configuration according to the configuration file
       */
      public void getFreqFromFile()
      {
    	  Scanner scan;
          int currentColumn = 6;
          int currentLine = 0;
          
          try
          {
          	
        	  
          BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/data/SSVEPTrainFiles/ConfigFile.txt"));
          String line;

          propStimFreq1=0;
          propStimFreq2=0;
          propStimFreq3=0;
          propStimFreq4=0;
          
          
  	        while ((line = in.readLine()) != null)	//file reading
  	        {        	
  	        	currentLine++;
  	        	currentColumn = 0;
  	        	String[] values = line.split(" ");
  	        
  		        for (String str : values)
  		        {
  		            currentColumn ++;
  		            
  		        	double str_double = Double.parseDouble(str);
  		        	
  		        	switch (currentLine)
  		        	{
  		        	
  		        	case 1:		        		
  		        		
  		        		if (currentColumn==1)
  		        		{
  		        			propStimFreq1 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==3)
  		        		{
  		        			propBestHarm1 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==4)
  		        		{
  		        			propSF1GO1 = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==5)
  		        		{
  		        			propSF1GOz = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==6)
  		        		{
  		        			propSF1GO2 = str_double;
  		        		}
  		        		
  		        		break;
  		        		
  		        	case 2:
  		        		
  		        		if (currentColumn==1)
  		        		{
  		        			propStimFreq2 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==3)
  		        		{
  		        			propBestHarm2 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==4)
  		        		{
  		        			propSF2GO1 = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==5)
  		        		{
  		        			propSF2GOz = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==6)
  		        		{
  		        			propSF2GO2 = str_double;
  		        		}
  		        		
  		        		break;
  		        		
  		        	case 3:
  		        		
  		        		if (currentColumn==1)
  		        		{
  		        			propStimFreq3 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==3)
  		        		{
  		        			propBestHarm3 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==4)
  		        		{
  		        			propSF3GO1 = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==5)
  		        		{
  		        			propSF3GOz = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==6)
  		        		{
  		        			propSF3GO2 = str_double;
  		        		}
  		        		
  		        		break;
  		        		
  		        	case 4:
  		        		
  		        		if (currentColumn==1)
  		        		{
  		        			propStimFreq4 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==3)
  		        		{
  		        			propBestHarm4 = (int)str_double;
  		        		}
  		        		
  		        		else if (currentColumn==4)
  		        		{
  		        			propSF4GO1 = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==5)
  		        		{
  		        			propSF4GOz = str_double;
  		        		}
  		        		
  		        		else if (currentColumn==6)
  		        		{
  		        			propSF4GO2 = str_double;
  		        		}
  		        		
  		        		break;
  		        		
  		        	
  		        	}
  		                    

  		        }
  		          	               
  	        }
  	        
          	in.close();
          	
          	
		    opFreqP1.sendData(ConversionUtils.intToBytes(propStimFreq1));
		    opFreqP2.sendData(ConversionUtils.intToBytes(propStimFreq2));
		    opFreqP3.sendData(ConversionUtils.intToBytes(propStimFreq3));
		    opFreqP4.sendData(ConversionUtils.intToBytes(propStimFreq4));
          	
		    etpUpdatePanelsConfig.raiseEvent();
		    
		    
		    nativeSSVEPDetectionInit (propSF1GO1, propSF1GOz, propSF1GO2,
    				propSF2GO1, propSF2GOz, propSF2GO2,
    				propSF3GO1, propSF3GOz, propSF3GO2,
    				propSF4GO1, propSF4GOz, propSF4GO2,
    				propStimFreq1,propStimFreq2,propStimFreq3,propStimFreq4, 
    				propBestHarm1,propBestHarm2,propBestHarm3, propBestHarm4);
		    
		    opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Succesfull Configuration"));
  	        
          }
          catch( FileNotFoundException e ) 
          {
        	  opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Config File Not Found"));
          }
  	  	
          catch( IOException e)
          {
        	  opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Impossible to run the configuration process"));
          }
      }
      
         
    /**
    * Method to calculate and generate the configuration file
    */  
  	class SimpleThread extends Thread 
  	{
  	    public SimpleThread(String str) 
  	    {
  	    	super(str);
  	    }
  	    
  	    public void run() 
  	    {
 
			try
			{
				char backslide = 92;
				int exitV;
				
				//Clean the Training Report
				opTrainProcessReport.sendData(ConversionUtils.stringToBytes(" "));
				
				String execCommand = "tools" +  backslide + "SSVEPTrainFunction " + UserName + " " + NumPanels;
				
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec(execCommand);	
				
			    exitV = p.waitFor();
				
			   //Training Report		
				if(exitV==-1)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Wrong Number of Arguments"));
				
				else if(exitV==-2)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Wrong Number of Panels"));
				
				else if(exitV==-3)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("No Valid Files Found"));
				
				else if(exitV==-4)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Some error reading files"));
				
				else if(exitV==-5)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Some error writing the results to the output file"));
				
				else if(exitV==-6)
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Some error creating the output file"));
				
				else
					opTrainProcessReport.sendData(ConversionUtils.stringToBytes("Config file succesfully calculated"));

			}

			catch(Exception e)
			{
				System.out.println("Training Program could not be executed");
				System.out.println(e.toString());
			}
			
  	    }
  	}
} 