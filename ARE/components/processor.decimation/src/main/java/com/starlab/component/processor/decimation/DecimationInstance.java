
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package com.starlab.component.processor.decimation;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the decimation of an input signal
 * 
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: Apr 29, 2011
 *         Time 04:51:02 PM
 */
public class DecimationInstance extends AbstractRuntimeComponentInstance
{
    
    private InputPort ipInputPort = new InputPort();
    private OutputPort opOutputPort = new OutputPort();

    private Decimation decimation = new Decimation();

    public static final int DEFAULT_DOWNSAMPLING_VALUE = 2;
    public static final int MAXIMUM_DOWNSAMPLING_VALUE = 11;
    private int propDownSampling = DEFAULT_DOWNSAMPLING_VALUE;

    /**
     * The class constructor
     */
    public DecimationInstance ()
    {
        decimation.setDownSampling(propDownSampling);
    }

    /**
     * called when model is started
     */
    @Override
    public void start ()
    {
        super.start();
    }

    /**
     * called when model is paused
     */
    @Override
    public void pause ()
    {
        super.pause();
    }

    /**
     * called when model is resumed
     */
    @Override
    public void resume ()
    {
        super.resume();
    }

    /**
     * called when model is stopped
     */
    @Override
    public void stop ()
    {
        super.stop();
    }

    /**
     * returns an Input Port.
     * @param portID   the name of the port
     * @return         the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort (String portID)
    {
        if ("input".equalsIgnoreCase(portID))
        {
            return ipInputPort;
        }

        return null;
    }

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort (String portID)
    {
        if ("output".equalsIgnoreCase(portID))
        {
            return opOutputPort;
        }

        return null;
    }

    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue (String propertyName)
    {
        if("DownSamplingRatio".equalsIgnoreCase(propertyName))
        {
            return propDownSampling;
        }

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue (String propertyName, Object newValue)
    {
        if ("DownSamplingRatio".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = propDownSampling;

            if (newValue != null)
            {
                try
                {
                	propDownSampling = Integer.parseInt(newValue.toString());
                    if (propDownSampling <= MAXIMUM_DOWNSAMPLING_VALUE)
                    {
                        decimation.setDownSampling(propDownSampling);
                    }
                    else AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
                }
                catch (NumberFormatException nfe)
                {
                    AstericsErrorHandling.instance.reportInfo(this, "Invalid property value for " + propertyName + ": " + newValue);
                }
            }

            return oldValue;
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Input Port for receiving the input signal
     */
    private class InputPort extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
            // convert input to int
            double in = ConversionUtils.doubleFromBytes(data);
            if (decimation.decimate(in))
            {
                opOutputPort.sendData(ConversionUtils.doubleToBytes(decimation.getLastDecimated()));
            }
        }

		
    }

    /**
     * Default Output Port for sending the values
     */
    private class OutputPort extends DefaultRuntimeOutputPort
    {
        // empty
    }
}
