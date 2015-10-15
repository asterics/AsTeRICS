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
 *    Dual License: MIT or GPL v3.0 (GNU General Public License Version 3.0)
 *                 (please refer to the LICENSE_dual.txt file)
 * 
 */

package eu.asterics.component.actuator.analogout;

import java.util.*;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.*;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * AnalogOutInstance communicates with the DAC outputs of the ADC-DAC CIM of 
 * the AsTeRICS platform. It provides a very simplistic interface which only
 * translates the digital input to the component to analog values on outputs 
 * of the hardware at the moment. 
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class AnalogOutInstance extends AbstractRuntimeComponentInstance
{
	private CIMPortController port = null;
	
	
	private final String NAME_INPUT_1 = "out1";
	private final String NAME_INPUT_2 = "out2";
	private final String NAME_INPUT_3 = "out3";
	private final String NAME_INPUT_4 = "out4";
	
	
	private static final short ADC_CIM_ID 			= 0x0401;
	
	private static final short ADC_FEATURE_DAC_OUTPUT_VALUE = 0x50;
	private static final int NUMBER_OF_OUTPUTS = 4;
	
	private InputPort [] ipInputPorts = new InputPort[4];
	
	/**
	 * Basic constructor creates the input ports
	 */
    public AnalogOutInstance()
    {
        // empty constructor - needed for OSGi service factory operations
		for (int i = 0; i < NUMBER_OF_OUTPUTS; i++)
		{
			ipInputPorts[i] = new InputPort(this, i);
		}
    }

    /**
     * Returns the desired input port of the component
     * @param portID the name of the port
     * @return the instance of the port
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if(NAME_INPUT_1.equalsIgnoreCase(portID))
        {
            return ipInputPorts[0];
        }
        else if(NAME_INPUT_2.equalsIgnoreCase(portID))
        {
            return ipInputPorts[1];
        }
        else if(NAME_INPUT_3.equalsIgnoreCase(portID))
        {
            return ipInputPorts[2];
        }
        else if(NAME_INPUT_4.equalsIgnoreCase(portID))
        {
            return ipInputPorts[3];
        }
        return null;
    }

    
    public Object getRuntimePropertyValue(String propertyName)
    {
        return null;
    }

    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        return null;
    }
    
    /**
     * Starts the component. Detects ADC and will report error if this fails
     */
    public void start()
    {
    	port = CIMPortManager.getInstance().getConnection(ADC_CIM_ID);
    	if (port != null)
    	{
	    	super.start();
	    	AstericsErrorHandling.instance.reportInfo(this, "AnalogOutInstance started");
    	}
    	else
    	{
       		AstericsErrorHandling.instance.reportError(this, String.format("Could not find AnalogOut CIM (0x%x) in PortManager. Please verify that the CIM Module is connected to an USB Port and that the driver is installed.", ADC_CIM_ID ));
    	}
    }

    /**
     * Stops the component
     */
    public void stop()
    {
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "AnalogOutInstance stopped");
    }
    
    private byte [] dacValues = { 0, 0, 0, 0};
    
    /**
     * Clips the input to the available range and transfers it to the CIM
     * @param value the voltage value (0 to 240 representing 100mV steps)
     * @param index the index of the output to set the value on
     */
    public void processInput(int value, int index)
    {
    	int val = value;
    	if (val > 240)
    		val = 240;
    	else if (val < 0)
    		val = 0;
    	
    	dacValues[index] = (byte) val;
   	
    	if (port != null)
    	{
    		CIMPortManager.getInstance().sendPacket(port, dacValues, ADC_FEATURE_DAC_OUTPUT_VALUE, 
				      CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    	}
    }
  
    /**
     * This input port implementation knows its index and will transfer input
     * data to the corresponding output port on the CIM 
     * @author weissch
     *
     */
    class InputPort extends DefaultRuntimeInputPort 
    {
    	int index;
    	AnalogOutInstance owner;
    	
    	/**
    	 * Sets the relation between the port and the CIM output port
    	 * @param owner the owning instance
    	 * @param index the index of the port 
    	 */
    	public InputPort(AnalogOutInstance owner, int index)
    	{
    		this.owner = owner;
    		this.index = index;
    	}
    	
    	/**
    	 * Called with input data, transfer it to the corresponding output
    	 */
		public void receiveData(byte[] data)
		{
			int value = ConversionUtils.byteArrayToInt(data);
			owner.processInput(value, index);
		}


	}
}