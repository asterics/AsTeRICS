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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.stringdispatcher;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.services.AstericsErrorHandling;

import java.util.*;
import java.util.logging.*;


/**
 *   Implements the String Dispatcher plugin, which stores and sends string values
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Feb 15, 2011
 *         Time: 11:41:08 AM
 */
public class StringDispatcherInstance  extends AbstractRuntimeComponentInstance
{
  
  private final int NUMBER_OF_SLOTS = 20;
  private final String PROP_SLOT="slot";
  private final String PROP_DELAY="delay";
  private final String ELP_DISPATCH_SLOT="dispatchSlot";
  private final String ELP_DISPATCH_NEXT_SLOT="dispatchNextSlot";
  private final String ELP_DISPATCH_CURRENT_SLOT="dispatchCurrentSlot";
  private final String ELP_DISPATCH_PREVIOUS_SLOT="dispatchPreviousSlot";
  private final String ELP_DISPATCH_SLOT_SERIES="dispatchSlotSeries";
  private final String ELP_RESET_TO_FIRST_SLOT="resetToFirstSlot";
  private final String OP_OUTPUT="output";
  private final String IP_SLOT_DISPATCH="slotDispatch";
 
  private final OutputPort opOutput = new OutputPort();
  private IRuntimeInputPort ipSlotDispatch= new SlotDispatchPort();
  
  private DispatchSlot[] elpDispatchSlotArray = new DispatchSlot[NUMBER_OF_SLOTS];
  private String [] propSlotArray = new String[NUMBER_OF_SLOTS];
  
  private int propDelay=300;
  boolean block = false;
  private int currentIndex=0;
	
  StringDispatcherTimer timer = new StringDispatcherTimer(this);

  /**
   * The class constructor.
   */	
  public StringDispatcherInstance()
  {
    for(int i=0;i<NUMBER_OF_SLOTS;i++)
	{
	  propSlotArray[i]="";
	  elpDispatchSlotArray[i]=new DispatchSlot();
	  elpDispatchSlotArray[i].number=i+1;
	}
  }
  
  /**
   * Called when model is started.
   */
  public void start()
  {
	  
    super.start();
  }

  /**
   * Called when model is paused.
   */
  public void pause()
  {
    block=false;
    timer.finishNow();
    super.pause();
  }

  /**
   * Called when model is resumed.
   */
  public void resume()
  {
    super.resume();
  }

  /**
   * Called when model is stopped
   */
  public void stop()
  {
    block=false;
    timer.finishNow();
    super.stop();
  }

  /**
   * Returns an Input Port.
   * @param portID   the name of the port
   * @return         the input port or null if not found
   */
  public IRuntimeInputPort getInputPort(String portID)
  {
    if(IP_SLOT_DISPATCH.equalsIgnoreCase(portID))
    {
      return ipSlotDispatch;
    }
    else
    {
      return null;
    }
  }
    
  /**
   * Returns an Output Port.
   * @param portID   the name of the port
   * @return         the output port
   */    
  public IRuntimeOutputPort getOutputPort(String portID)
  {
    if(OP_OUTPUT.equalsIgnoreCase(portID))
    {
      return opOutput;
    }
    else
    {
      return null;
    }
  }
    
  /**
   * Counts the no empty slots.
   */  
  private int CountNoEmptySlots()
  {
    int counter=0;
    for(int i=0;i<NUMBER_OF_SLOTS;i++)
    {
      if(propSlotArray[i].length()>0)
      {
        counter++;
      }
    }
    	
    return counter;
  }
 
  /**
   * Unsets the block on slot sending.
   */  
  void resetBlock()
  {
    block=false;
  }
  
  /**
   * Sends the string value from the selected slot.
   * @param slotNumber   the nuber of the slot to send
   */  
  private void sendText(int slotNumber)
  {
    if(block==false)
    {
      if(slotNumber>0 && slotNumber<=NUMBER_OF_SLOTS)
      {
        currentIndex=slotNumber;
    	opOutput.sendData(propSlotArray[slotNumber-1]);
      }
    }
  }
    
  /**
   * Sends the string value from the previous slot. 
   * If the current current slot is the first,
   * tries to send value from the last slot.
   */  
  
  void sendPrevious()
  {
    int index = currentIndex-1;
    for(int i=0;i<NUMBER_OF_SLOTS;i++)
    {
      if(index<1)
      {
        index=NUMBER_OF_SLOTS;
      }
	
      if(propSlotArray[index-1].length()>0)
      {
        sendText(index);
    	break;
      }
      index=index-1;
    }
  }

  void sendCurrent()
  {
    if (currentIndex==0) currentIndex=1;
    if(propSlotArray[currentIndex].length()>0)
    {
        sendText(currentIndex);
    }
  }

  
  /**
   * Sends the string value from the next slot. 
   * If the current current slot is the last,
   * tries to send value from the first slot.
   */  
  void sendNext()
  {
    int index = currentIndex+1;
    for(int i=0;i<NUMBER_OF_SLOTS;i++)
    {
      if(index>NUMBER_OF_SLOTS)
      {
        index=1;
      }
	
      if(propSlotArray[index-1].length()>0)
      {
        sendText(index);
    	break;
      }
      
      index=index+1;
    }
  }
  
  /**
   * Sends the next slot from series.
   */  
  void sendNextSlotFromSeries()
  {
    int index = currentIndex+1;
	for(int i=0;i<NUMBER_OF_SLOTS;i++)
	{
	  if(index>NUMBER_OF_SLOTS)
	  {
	    index=1;
	  }
	  
	  if(propSlotArray[index-1].length()>0)
	  {			    
	     currentIndex=index;
	     opOutput.sendData(propSlotArray[index-1]);			    
	     break;
      }
	      
	  index=index+1;
    }
  }
    
  /**
   * Returns the value of the given property.
   * @param propertyName   the name of the property
   * @return               the property value or null if not found
   */  
  public Object getRuntimePropertyValue(String propertyName)
  {
    if(PROP_DELAY.equalsIgnoreCase(propertyName))
    {
      return propDelay;
    }
    else
    {
    	
      int slotPropSize=PROP_SLOT.length();
      if(propertyName.length()>slotPropSize)
      {
        String testName=propertyName.substring(0,slotPropSize);
    	if(testName.equalsIgnoreCase(PROP_SLOT))
    	{
    	  String slotNumberText=propertyName.substring(slotPropSize);
    	  int slotNumberValue;
    	  
    	  try
    	  {
    	    slotNumberValue = Integer.parseInt(slotNumberText);
    	  }
    	  catch(NumberFormatException ex)
    	  {
    	    return null;
    	  }
    			
    	  if(slotNumberValue>0 && slotNumberValue<=NUMBER_OF_SLOTS)
    	  {
    	    return propSlotArray[slotNumberValue-1];
    	  }
    	  else
    	  {
    	    return null;
    	  }
    	}	
      }
    }
    
    return null;
    	
  }

  /**
   * Sets a new value for the given property.
   * @param propertyName   the name of the property
   * @param newValue       the desired property value
   * @return old property value
   */
  public Object setRuntimePropertyValue(String propertyName, Object newValue)
  {
    if(PROP_DELAY.equalsIgnoreCase(propertyName))
    {
      int oldValue =propDelay;
      propDelay = Integer.parseInt((String) newValue);;
      return oldValue;
    }
    else
    {
      int slotPropSize=PROP_SLOT.length();
      if(propertyName.length()>slotPropSize)
      {
        String testName=propertyName.substring(0,slotPropSize);
        if(testName.equalsIgnoreCase(PROP_SLOT))
        {
          String slotNumberText=propertyName.substring(slotPropSize);
          int slotNumberValue;
    			
          try
          {
            slotNumberValue = Integer.parseInt(slotNumberText);
          }
          catch(NumberFormatException ex)
          {
            return null;
          }
 	
          if(slotNumberValue>0 && slotNumberValue<=NUMBER_OF_SLOTS)
          {
            final String oldValue=propSlotArray[slotNumberValue-1];
        	propSlotArray[slotNumberValue-1]=(String)newValue;
        	return oldValue;
          }
          else
          {
            return null;
          }
        }
      }
    }
    
    return null;
  }
  
  /**
   * Returns an Event Listener Port.
   * @param eventPortID   the name of the port
   * @return         the event listener port or null if not found
   */
  @Override
  public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
  {
    if(ELP_DISPATCH_NEXT_SLOT.equalsIgnoreCase(eventPortID))
    {
      return sendNext;
    }
    else if(ELP_DISPATCH_CURRENT_SLOT.equalsIgnoreCase(eventPortID))
    {
      return sendCurrent;
    }
    else if(ELP_DISPATCH_PREVIOUS_SLOT.equalsIgnoreCase(eventPortID))
    {
      return sendPrevious;
    }
    else if(ELP_DISPATCH_SLOT_SERIES.equalsIgnoreCase(eventPortID))
    {
      return sendAll ;
    }
    else if(ELP_RESET_TO_FIRST_SLOT.equalsIgnoreCase(eventPortID))
    {
      return resetToFirst;
    }
    else
    {
      int sendEventSize=ELP_DISPATCH_SLOT.length();
      if(eventPortID.length()>sendEventSize)
      {
        String testName=eventPortID.substring(0,sendEventSize);
    	if(testName.equalsIgnoreCase(ELP_DISPATCH_SLOT))
    	{
    	  String eventNumberText=eventPortID.substring(sendEventSize);
    	  int eventNumberValue;
    	  try
    	  {
    	    eventNumberValue = Integer.parseInt(eventNumberText);
    	  }
    	  catch(NumberFormatException ex)
    	  {
    	    return null;
    	  }
    			
    	  if(eventNumberValue>0 && eventNumberValue<=NUMBER_OF_SLOTS)
    	  {
    	    return elpDispatchSlotArray[eventNumberValue-1];
    	  }
    	  else
    	  {
    	    return null;
    	  }
    	}
      }
    }
        
    return null;
       
  }
  
  /**
   * Event Listener Port for the Dispatch Next Slot event.
   */
  final IRuntimeEventListenerPort sendNext 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      sendNext();
    }
  };
  
  /**
   * Event Listener Port for the Dispatch Current Slot event.
   */
  final IRuntimeEventListenerPort sendCurrent 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      sendCurrent();
    }
  };

  /**
   * Event Listener Port for the Dispatch Previous Slot event.
   */
  final IRuntimeEventListenerPort sendPrevious 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      sendPrevious();
    }
  };
 
  /**
   * Event Listener Port for the Reset To First Slot event.
   */
  final IRuntimeEventListenerPort resetToFirst 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
    	if (block==false)
    	{
    		currentIndex=0;
    	}
    }
  };
  /**
   * Event Listener Port for the Dispatch Slot Series event.
   */
  final IRuntimeEventListenerPort sendAll 	= new IRuntimeEventListenerPort()
  {
    @Override 
    public void receiveEvent(String data)
    {
      if(block==false)
      {
        int numberSlotsToSend=CountNoEmptySlots();
        currentIndex=0;
    	block=true;
    	timer.sendAll(numberSlotsToSend,propDelay);
      }
    }
  };
  
  /**
   * Event Listener Port for the Dispatch Slot event.
   */
  private class DispatchSlot implements IRuntimeEventListenerPort
  {
    @Override 
    public void receiveEvent(String data)
    {
      sendText(number);
    }
    	
    public int number=0;
  }
    
  /**
   * Input port for receive slot number to dispatch.
   */
  private class SlotDispatchPort extends DefaultRuntimeInputPort
  {
    public void receiveData(byte[] data)
    {
      int number = ConversionUtils.intFromBytes(data);
      if(number>0 && number<=NUMBER_OF_SLOTS)
      {
        sendText(number);
      }
        	
    }

  }
  
  /**
   * Plugin output port.
   */
  public class OutputPort extends DefaultRuntimeOutputPort
  {
    public void sendData(String data)
    {
          super.sendData(ConversionUtils.stringToBytes(data));  
    }
  }
}