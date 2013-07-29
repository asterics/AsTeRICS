

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

package com.starlab.component.processor.protocolssveptrain;


import java.util.logging.Logger;
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
import java.util.Vector;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 
 * This module implements the SSVEP training and 
 * is in charge of running the training protocol 
 * defined by the user within its properties consisting 
 * of N stimulation trials at the frequencies defined.
 *  
 * @author David Ibanez [david.ibanez@starlab.es]
 *         Date: 30/08/2012
 *         Time: 15:00
 */


public class ProtocolSSVEPTrainInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opStimFrequency = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreq2 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreq3 = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opFreq4 = new DefaultRuntimeOutputPort();
	
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpStartTrial = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStopTrial = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStartStim = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpStopStim = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpReadyStim = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpUpdatePanelConfig = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propNumRepetitions = 0;
	int propRepStimDuration = 0;
	int propRepNonStimDuration = 0;
	int propFreqStim1 = 0;
	int propFreqStim2 = 0;
	int propFreqStim3 = 0;
	int propFreqStim4 = 0;
	int propFreqStim5 = 0;
	int propFreqStim6 = 0;
	int propFreqStim7 = 0;
	int propFreqStim8 = 0;
	int propFreqStim9 = 0;
	int propFreqStim10 = 0;
	boolean propRandomizeFreq = true;

	// declare member variables here
    int numStimFrequencies=0;
    Vector StimulationFrequencies = new Vector(); 
    Vector NonStimulationFrequencies = new Vector(); 
  
    // Timer for the protocol
    int delay=1000;
    Timer StopWatch = new Timer();
    
    int RemainingStimP=0;
    int onStimulating=0;
    boolean onProtocol = false;
    boolean onStim = false;
    boolean onRest = false;
    int currentFreqIndex = 0;
    int currentCounter = 0;
    int currentStimPer = 0;

    
   /**
    * The class constructor.
    */
    public ProtocolSSVEPTrainInstance()
    {
    	StopWatch.schedule(new Task("stopwatch") , 0, delay);
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
		if ("stimFrequency".equalsIgnoreCase(portID))
		{
			return opStimFrequency;
		}

		if ("Freq2".equalsIgnoreCase(portID))
		{
			return opFreq2;
		}
		
		if ("Freq3".equalsIgnoreCase(portID))
		{
			return opFreq3;
		}
		
		if ("Freq4".equalsIgnoreCase(portID))
		{
			return opFreq4;
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
		if ("startProt".equalsIgnoreCase(eventPortID))
		{
			return elpStartProt;
		}
		if ("stopProt".equalsIgnoreCase(eventPortID))
		{
			return elpStopProt;
		}
		if ("continue".equalsIgnoreCase(eventPortID))
		{
			return elpContinue;
		}
		if ("repeat".equalsIgnoreCase(eventPortID))
		{
			return elpRepeat;
		}
		if ("stop".equalsIgnoreCase(eventPortID))
		{
			return elpStop;
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
		if ("startTrial".equalsIgnoreCase(eventPortID))
		{
			return etpStartTrial;
		}
		if ("stopTrial".equalsIgnoreCase(eventPortID))
		{
			return etpStopTrial;
		}
		if ("startStim".equalsIgnoreCase(eventPortID))
		{
			return etpStartStim;
		}
		if ("stopStim".equalsIgnoreCase(eventPortID))
		{
			return etpStopStim;
		}
		if ("readyStim".equalsIgnoreCase(eventPortID))
		{
			return etpReadyStim;
		}
		if ("UpdatePanelConfig".equalsIgnoreCase(eventPortID))
		{
			return etpUpdatePanelConfig;
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
		if ("numRepetitions".equalsIgnoreCase(propertyName))
		{
			return propNumRepetitions;
		}
		if ("repStimDuration".equalsIgnoreCase(propertyName))
		{
			return propRepStimDuration;
		}
		if ("repNonStimDuration".equalsIgnoreCase(propertyName))
		{
			return propRepNonStimDuration;
		}
		if ("freqStim1".equalsIgnoreCase(propertyName))
		{
			return propFreqStim1;
		}
		if ("freqStim2".equalsIgnoreCase(propertyName))
		{
			return propFreqStim2;
		}
		if ("freqStim3".equalsIgnoreCase(propertyName))
		{
			return propFreqStim3;
		}
		if ("freqStim4".equalsIgnoreCase(propertyName))
		{
			return propFreqStim4;
		}
		if ("freqStim5".equalsIgnoreCase(propertyName))
		{
			return propFreqStim5;
		}
		if ("freqStim6".equalsIgnoreCase(propertyName))
		{
			return propFreqStim6;
		}
		if ("freqStim7".equalsIgnoreCase(propertyName))
		{
			return propFreqStim7;
		}
		if ("freqStim8".equalsIgnoreCase(propertyName))
		{
			return propFreqStim8;
		}
		if ("freqStim9".equalsIgnoreCase(propertyName))
		{
			return propFreqStim9;
		}
		if ("freqStim10".equalsIgnoreCase(propertyName))
		{
			return propFreqStim10;
		}
		if ("randomizeFreq".equalsIgnoreCase(propertyName))
		{
			return propRandomizeFreq;
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
		if ("numRepetitions".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumRepetitions;
			propNumRepetitions = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("repStimDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propRepStimDuration;
			propRepStimDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("repNonStimDuration".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propRepNonStimDuration;
			propRepNonStimDuration = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim1".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim1;
			propFreqStim1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim2".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim2;
			propFreqStim2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim3".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim3;
			propFreqStim3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim4".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim4;
			propFreqStim4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim5".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim5;
			propFreqStim5 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim6".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim6;
			propFreqStim6 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim7".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim7;
			propFreqStim7 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim8".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim8;
			propFreqStim8 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim9".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim9;
			propFreqStim9 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("freqStim10".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFreqStim10;
			propFreqStim10 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("randomizeFreq".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propRandomizeFreq;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propRandomizeFreq = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propRandomizeFreq = false;
			}
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStartProt = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			int StimFreq=0;
		    RemainingStimP=propNumRepetitions;	
		    
		    onProtocol = true;
		    onStim = false;
		    onRest = true;
		    
		    currentFreqIndex = 0;
		    currentCounter = propRepNonStimDuration;	
		    
		    currentStimPer=0; 
		    
		    //Send Stim Frequency to configurate the stimulator	    
		    StimFreq=(int)StimulationFrequencies.get(currentFreqIndex);	    
		    
		    NonStimulationFrequencies.clear();
		    
		    
		    for (int i=0 ;i < StimulationFrequencies.size();i++)
		    {
		    	
		    	NonStimulationFrequencies.add(i, StimulationFrequencies.get(i));	
	    	
		    }
		    
		    
		   	NonStimulationFrequencies.remove(currentFreqIndex);    
		   	Collections.shuffle(NonStimulationFrequencies); 
		   	
		   	
		    opStimFrequency.sendData(ConversionUtils.intToBytes(StimFreq));	  
			opFreq2.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(0)));
			opFreq3.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(1)));
			opFreq4.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(2)));
		    
		    
		    etpUpdatePanelConfig.raiseEvent();
		    
		    try{
		    	Thread.sleep(250);
		    }
		    
	    	catch(InterruptedException e){
	    		
	    		
	    	}
		    etpUpdatePanelConfig.raiseEvent();
		    etpStartTrial.raiseEvent();	
		    
		    
		    
		}
	};
	final IRuntimeEventListenerPort elpStopProt = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    onProtocol = false;
		    onStim = false; 
		    currentStimPer=0;
		    
		    
		}
	};
	final IRuntimeEventListenerPort elpContinue = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			
			if(currentFreqIndex<numStimFrequencies-1)
			{
				int StimFreq=0;
			    RemainingStimP=propNumRepetitions;	
			    
			    onProtocol = true;
			    onStim = false;
			    onRest = true;
			    
			    currentCounter = propRepNonStimDuration;	
			    currentStimPer=0;
			    
			    
			    currentFreqIndex++;
			    
			   //Send Stim Frequency to configurate the stimulator
			   StimFreq=(int)StimulationFrequencies.get(currentFreqIndex);
			   
			   NonStimulationFrequencies.clear();
			   
			    for (int i=0 ;i < StimulationFrequencies.size();i++)
			    {
			    	
			    	NonStimulationFrequencies.add(i, StimulationFrequencies.get(i));	
		    	
			    }
			    
			    
			   	NonStimulationFrequencies.remove(currentFreqIndex);    
			   	Collections.shuffle(NonStimulationFrequencies); 
			   	
			   opStimFrequency.sendData(ConversionUtils.intToBytes(StimFreq));		  
			   opFreq2.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(0)));
			   opFreq3.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(1)));
			   opFreq4.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(2)));
			   
			    try{
			    	Thread.sleep(250);
			    }
			    
		    	catch(InterruptedException e){
		    		
		    		
		    	}
			    etpUpdatePanelConfig.raiseEvent();
			   etpStartTrial.raiseEvent();		
			}
			else
			{
				etpStopTrial.raiseEvent();
				//System.out.println("Protocol Finished");
			}
		   
			
  
		}
	};
	final IRuntimeEventListenerPort elpRepeat = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			int StimFreq=0;
		    RemainingStimP=propNumRepetitions;	
		    
		    onProtocol = true;
		    onStim = false;
		    onRest = true;
		    
		    currentCounter = propRepNonStimDuration;
		    currentStimPer=0;
		    
		   //Send Stim Frequency to configurate the stimulator
		    StimFreq=(int)StimulationFrequencies.get(currentFreqIndex);
		    
		    NonStimulationFrequencies = StimulationFrequencies; 
			NonStimulationFrequencies.remove(currentFreqIndex);  
			Collections.shuffle(NonStimulationFrequencies); 
			
		    
		    opStimFrequency.sendData(ConversionUtils.intToBytes(StimFreq));		  
			opFreq2.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(0)));
			opFreq3.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(1)));
			opFreq4.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(2)));
		    
		    try{
		    	Thread.sleep(250);
		    }
		    
	    	catch(InterruptedException e){
	    		
	    		
	    	}
		    etpUpdatePanelConfig.raiseEvent();
		    etpStartTrial.raiseEvent();	
		}
	};
	final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
		    onProtocol = false;
		    onStim = false; 
			etpStopTrial.raiseEvent();	
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

    	  
    	  StimulationFrequencies = new Vector(); 
    	  numStimFrequencies = 0;
    	  
    	  
    	  if (propFreqStim1!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim1);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim2!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim2);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim3!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim3);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim4!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim4);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim5!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim5);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim6!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim6);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim7!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim7);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim8!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim8);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim9!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim9);
    		  numStimFrequencies++;
    	  }
    	  if (propFreqStim10!=0)
    	  {
    		  StimulationFrequencies.addElement(propFreqStim10);
    		  numStimFrequencies++;
    	  }
 	  
    	  
    	  if (propRandomizeFreq)
    	  {
    	    Collections.shuffle(StimulationFrequencies);  	   
    	    
    	  }
    	    
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
       * Timer that performs the different protocol actions
       */
      public class Task extends TimerTask 
      {

    	  private String _objectName;                 // A string to output

    	  /**
    	  * Constructs the object, sets the string to be output in function run()
    	  * @param str
    	  */
    	  Task(String objectName) 
    	  {
    	  this._objectName = objectName;
    	  }

      public void run()
      {
    	  if(onProtocol)
    	  {
    		  if (onStim)
    		  {
    			  
    			  if(currentCounter==0)
    			  {
    				  currentStimPer++;
    				  //Start Non Stim Period   				      			   				  
    				  etpStopStim.raiseEvent();
    				  onStim=false;
    				  currentCounter = propRepNonStimDuration;
    				  
    				  //Only shuffle background panels each five stim periods
    				  if (currentStimPer%5==0)
    				  {
	    				  Collections.shuffle(NonStimulationFrequencies);     				  
	    				  opFreq2.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(0)));
	    				  opFreq3.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(1)));
	    				  opFreq4.sendData(ConversionUtils.intToBytes((int)NonStimulationFrequencies.get(2)));
    				  }
    				  
    			  }
    			  else
    			  {
    				  currentCounter--;
    			  }
    			  
    		  }
    		  
    		  else
    		  {
    			  if(currentCounter==0)
    			  {
    				  //Start Stim Period
    				  if (currentStimPer < propNumRepetitions)
    				  {
    				  onStim=true;
    				  currentCounter = propRepStimDuration;
    				  etpStartStim.raiseEvent();
    				  }
    				  //Stop Protocol
    				  else
    				  {
    					  onStim=false;
    					  onProtocol=false;
    					  etpStopTrial.raiseEvent();
    				  }
    				  
    				  
    			  }
    			  
    			  else if(currentCounter==1)
    			  {
    				  if (currentStimPer < propNumRepetitions)
    				  {
	    				  etpReadyStim.raiseEvent();
	    				  currentCounter--;
    				  }
    				  else
    				  {
    					  currentCounter--;
    				  }
    			  }
    			  
    			  else
    			  {
    				  currentCounter--;
    			  }
    		  }
    		  
    	  
    	  }
      }

    }  
    	  

}