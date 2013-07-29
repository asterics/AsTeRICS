
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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.processor.amplifier;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

import java.util.*;

/**
 * The amplifier is a simple processor component which increases or decreases 
 * the input by a factor set as a property
  * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class AmplifierInstance extends AbstractRuntimeComponentInstance
{
    public static final double DEFAULT_FACTOR = 1;

    private double propFactor = DEFAULT_FACTOR;

    /**
     * The input port instance of this component. 
     */
    private IRuntimeInputPort ipSigIn = new InputPort1();

    private IRuntimeOutputPort opSigOut = new DefaultRuntimeOutputPort();

	
    public AmplifierInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * Returns the input port of the component
     * @param portID the name of the port
     * @return the instance of the port, null otherwise
     */
    public IRuntimeInputPort getInputPort(String portID)
    {
        if("sigIn".equalsIgnoreCase(portID))
        {
            return ipSigIn;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the output port of the component
     * @param portID the name of the port
     * @return the instance of the port, null otherwise
     */
    public IRuntimeOutputPort getOutputPort(String portID)
    {
        if("sigOut".equalsIgnoreCase(portID))
        {
            return opSigOut;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the values of properties of the component
     * @param propertyName name of the property
     * @return  the value of the property as an Object
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
        if("factor".equalsIgnoreCase(propertyName))
        {
            return propFactor;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the values of properties of the component
     * @param propertyName name of the property
     * @param newValue the new value as an Object
     * @return  the old value of the property as an Object
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if("factor".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propFactor;

            if(newValue != null)
            {
                try
                {
                	propFactor = Double.parseDouble(newValue.toString());
                }
                catch (NumberFormatException nfe)
                {
                	AstericsErrorHandling.instance.reportError(this, "Invalid property value for " + propertyName + ": " + newValue);
                }
            }
            return oldValue;
        }
        else
        {
            return null;
        }
    }

    /**
     * The input port of this component. Performs all amplification 
     * calculations.
     * 
     * @author weissch
     *
     */
    private class InputPort1 extends DefaultRuntimeInputPort
    {
    	/**
    	 * Converts and amplifies the input data
    	 */
        public void receiveData(byte[] data)
        {
            // convert input to int
            double in = ConversionUtils.doubleFromBytes(data);
            
            // send output
            opSigOut.sendData(ConversionUtils.doubleToBytes(in * propFactor));
        }

    }
}