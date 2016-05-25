
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

package eu.asterics.component.processor.pathmultiplexer;

import eu.asterics.mw.data.*;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import java.util.*;

/**
 *   Implements the plugin which passes selected input to the output.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jul 05, 2011
 *         Time: 11:51:00 AM
 */
public class PathmultiplexerInstance extends AbstractRuntimeComponentInstance
{
  final private String OP_OUTPUT = "output";
  final private String IP_INPUT_1 = "input1";
  final private String IP_INPUT_2 = "input2";
  final private String IP_INPUT_3 = "input3";
  final private String IP_INPUT_4 = "input4";
  final private String ELP_PASS_PORT_1 = "passPort1";
  final private String ELP_PASS_PORT_2 = "passPort2";
  final private String ELP_PASS_PORT_3 = "passPort3";
  final private String ELP_PASS_PORT_4 = "passPort4";
  final private String ELP_PASS_NEXT_PORT = "passNextPort";
  final private String ELP_PASS_PREVIOUS_PORT = "passPreviousPort";
  final private String PROP_NUMBER = "number";
	
  final IRuntimeOutputPort outputPort = new DefaultRuntimeOutputPort();        
  
  int propNumber = 2;
  int selected_port = 1;
	
  /**
   * The class constructor.
   */
  public PathmultiplexerInstance()
  {
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
   * Called when model is stopped.
   */
  public void stop()
  {
    super.stop();
  }

  /**
   * Returns an Input Port.
   * @param portID   the name of the port
   * @return         the input port or null if not found
   */
  public IRuntimeInputPort getInputPort(String portID)
  {
    if(IP_INPUT_1.equalsIgnoreCase(portID))
    {
      return ipInput1;
    }
    else if(IP_INPUT_2.equalsIgnoreCase(portID))
    {
      return ipInput2;
    }
    else if(IP_INPUT_3.equalsIgnoreCase(portID))
    {
      return ipInput3;
    }
    else if(IP_INPUT_4.equalsIgnoreCase(portID))
    {
      return ipInput4;
    }
    
    return null;
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
      return outputPort;
    }
    return null;
  }
  
  /**
   * Returns an Event Listener Port.
   * @param eventPortID   the name of the port
   * @return         the event listener port or null if not found
   */
  public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
  {
    if(ELP_PASS_PORT_1.equalsIgnoreCase(eventPortID))
    {
      return elpPassPort1;
    }
    else if(ELP_PASS_PORT_2.equalsIgnoreCase(eventPortID))
    {
      return elpPassPort2;
    }
    else if(ELP_PASS_PORT_3.equalsIgnoreCase(eventPortID))
    {
      return elpPassPort3;
    }
    else if(ELP_PASS_PORT_4.equalsIgnoreCase(eventPortID))
    {
      return elpPassPort4;
    }
    else if(ELP_PASS_NEXT_PORT.equalsIgnoreCase(eventPortID))
    {
      return elpPassNextPort;
    }
    else if(ELP_PASS_PREVIOUS_PORT.equalsIgnoreCase(eventPortID))
    {
      return elpPassPreviousPort;
    }
    return null;
  }
    

  /**
   * Returns the value of the given property.
   * @param propertyName   the name of the property
   * @return               the property value or null if not found
   */
  public Object getRuntimePropertyValue(String propertyName)
  {
    if(PROP_NUMBER.equalsIgnoreCase(propertyName))
    {
      return propNumber-1;
    }
    return null;
  }

  /**
   * Sets a new value for the given property.
   * @param propertyName   the name of the property
   * @param newValue       the desired property value
   * @return old propety value
   */
  public Object setRuntimePropertyValue(String propertyName, Object newValue)
  {
    try
    {
	  if(PROP_NUMBER.equalsIgnoreCase(propertyName))
	  {
	    final Object oldValue = propNumber-1;
        propNumber = Integer.parseInt(newValue.toString())+1;
        if ( (propNumber < 1) || (propNumber > 4) )
        {
          AstericsErrorHandling.instance.reportInfo(this, "Property value out of range for " + propertyName + ": " + newValue);
        }
	      return propNumber;
      }
    }
    catch (NumberFormatException nfe)
    {
      AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
    }
    return null;
  }

  /**
   * Input port 1
   */
  private final IRuntimeInputPort ipInput1 = new DefaultRuntimeInputPort()
  {
    public void receiveData(byte[] data)
    {
      double in = ConversionUtils.doubleFromBytes(data);
	  if(selected_port==1)
	  {
	    outputPort.sendData(ConversionUtils.doubleToBytes(in));
	  }
	}

	
  };
	
  /**
   * Input port 2
   */
  private final IRuntimeInputPort ipInput2 = new DefaultRuntimeInputPort()
  {
    public void receiveData(byte[] data)
	{
	  double in = ConversionUtils.doubleFromBytes(data);
	  if(selected_port==2)
	  {
	    outputPort.sendData(ConversionUtils.doubleToBytes(in));
	  }
    }

	
  };
		
  /**
   * Input port 3
   */
  private final IRuntimeInputPort ipInput3 = new DefaultRuntimeInputPort()
  {
    public void receiveData(byte[] data)
	{
	  double in = ConversionUtils.doubleFromBytes(data);
	  if(selected_port==3)
	  {
	    outputPort.sendData(ConversionUtils.doubleToBytes(in));
	  }
    }

	
  };
		
  /**
   * Input port 4
   */
  private final IRuntimeInputPort ipInput4  = new DefaultRuntimeInputPort()
  {
    public void receiveData(byte[] data)
	{
	  double in = ConversionUtils.doubleFromBytes(data);
	  if(selected_port==4)
	  {
	    outputPort.sendData(ConversionUtils.doubleToBytes(in));
	  }
	}

	
  };
		
  /**
   * Event Listener Port for pass the port 1.
   */
  final IRuntimeEventListenerPort elpPassPort1 	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
      selected_port=1;
    }
  };
  
  /**
   * Event Listener Port for pass the port 2.
   */
  final IRuntimeEventListenerPort elpPassPort2 	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
	  selected_port=2;
    }
  };
  
  /**
   * Event Listener Port for pass the port 3.
   */
  final IRuntimeEventListenerPort elpPassPort3 	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
      selected_port=3;
    }
  }; 
    
  /**
   * Event Listener Port for pass the port 4.
   */
  final IRuntimeEventListenerPort elpPassPort4 	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
	  selected_port=4;
    }
  }; 
  
  /**
   * Event Listener Port for pass the next port.
   */
  final IRuntimeEventListenerPort elpPassNextPort 	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
	  selected_port++; 
	  if (selected_port>propNumber)
	  {
		  selected_port=1;
	  }
    }
  }; 
  
  /**
   * Event Listener Port for pass the previous port.
   */
  final IRuntimeEventListenerPort elpPassPreviousPort	= new IRuntimeEventListenerPort()
  {
    public void receiveEvent(final String data)
    {
	  selected_port--; 
	  if (selected_port<1)
	  {
        selected_port=propNumber;
	  }
    }
  }; 

}