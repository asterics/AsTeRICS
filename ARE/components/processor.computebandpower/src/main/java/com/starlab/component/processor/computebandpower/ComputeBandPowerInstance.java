
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

package com.starlab.component.processor.computebandpower;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 *   Implements the computation of the power in a given band of a time-domain
 *   input signal
 * 
 * @author Javier Acedo [javier.acedo@starlab.es]
 *         Date: May 1, 2011
 *         Time 20:28:57 PM
 */
public class ComputeBandPowerInstance extends AbstractRuntimeComponentInstance
{
    private InputPort ipInputPort = new InputPort();
    private OutputPort opOutputPort = new OutputPort();

    private ComputeBandPower computeBandPower = new ComputeBandPower();

    private double [] bufferIn;
	private double [] bufferInAux;
    private int counter;   
    private int iBuffer;

    /**
     * The class constructor.
     */
    public ComputeBandPowerInstance ()
    {
        bufferIn = new double[computeBandPower.getDataLen()];
		bufferInAux = new double[computeBandPower.getDataLen()];
        counter = 0;
        iBuffer = 0;   
		for (int i=0; i<computeBandPower.getDataLen(); i++)
			bufferIn[i]=0;
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
        if("DataLen".equalsIgnoreCase(propertyName))
        {
            return computeBandPower.getDataLen();
        }
        else if("PsdComputingRate".equalsIgnoreCase(propertyName))
        {
            return computeBandPower.getPsdComputingRate();
        }        
        else if("SampleRate".equalsIgnoreCase(propertyName))
        {
            return computeBandPower.getSampleRate();
        }
        else if("StartBandFrequency".equalsIgnoreCase(propertyName))
        {
            return computeBandPower.getStartBand();
        }
        else if("EndBandFrequency".equalsIgnoreCase(propertyName))
        {
            return computeBandPower.getEndBand();
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
        if ("DataLen".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = computeBandPower.getDataLen();
            if (newValue != null)
            {
                try
                {
                    int dataLen = Integer.parseInt(newValue.toString());
                    // only power of two values permitted
                    if (dataLen > 1 && (dataLen & (dataLen - 1)) == 0)
                    {
                        if (dataLen != computeBandPower.getDataLen())
                        {
                            bufferIn = new double[dataLen];
                            bufferInAux = new double[dataLen];
                            iBuffer = 0;
                            computeBandPower.setDataLen(dataLen);
                        }
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
        else if ("PsdComputingRate".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = computeBandPower.getPsdComputingRate();

            if (newValue != null)
            {
                try
                {
                    int psdComputingRate = Integer.parseInt(newValue.toString());
                    if ((psdComputingRate > 0)&&(psdComputingRate <= 100))
                    {
                        computeBandPower.setPsdComputingRate(psdComputingRate);
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
        else if ("SampleRate".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = computeBandPower.getSampleRate();

            if (newValue != null)
            {
                try
                {
                    int sampleRate = Integer.parseInt(newValue.toString());
                    if (sampleRate > 0)
                    {
                        computeBandPower.setSampleRate(sampleRate);
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
        else if ("StartBandFrequency".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = computeBandPower.getStartBand();

            if (newValue != null)
            {
                try
                {
                    int startBand = Integer.parseInt(newValue.toString());
                    if (startBand >= 0 && startBand < (computeBandPower.getSampleRate() / 2))
                    {
                        computeBandPower.setStartBand(startBand);
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
        else if ("EndBandFrequency".equalsIgnoreCase(propertyName))
        {
            final Object oldValue = computeBandPower.getEndBand();

            if (newValue != null)
            {
                try
                {
                    int endBand = Integer.parseInt(newValue.toString());
                    if (endBand > 0 && endBand <= (computeBandPower.getSampleRate() / 2))
                    {
                        computeBandPower.setEndBand(endBand);
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
     * Input Port for receiving values.
     */
    private class InputPort extends DefaultRuntimeInputPort
    {
        public void receiveData(byte[] data)
        {
        	int i;
            // convert input to int
        	try
        	{
        		if (iBuffer >= computeBandPower.getDataLen())
        		{
	        		for (i=0; i<computeBandPower.getDataLen()-1; i++)
	        			bufferIn[i]=bufferIn[i+1];
	        		
	        		bufferIn[computeBandPower.getDataLen()-1] = ConversionUtils.doubleFromBytes(data);
        		}
        		else
        			bufferIn[iBuffer++]= ConversionUtils.doubleFromBytes(data);        	
        		
        		counter++;
        	}
        	catch(ArrayIndexOutOfBoundsException aioobe)
        	{
        		counter = 0;
        		iBuffer = 0;
        		for (i=0; i<computeBandPower.getDataLen(); i++)
        			bufferIn[i]=0;
        		
        		bufferIn[iBuffer++] = ConversionUtils.doubleFromBytes(data);
        	}
            if ((counter >= computeBandPower.getPsdComputingRate())&&(iBuffer >= computeBandPower.getDataLen()))
            {
                counter = 0; // reset bufferIn
                for (i=0; i<computeBandPower.getDataLen(); i++)
                	bufferInAux[i] = bufferIn[i];
				//double [] ptrAux = bufferIn;
				//bufferIn = bufferInAux;
				//bufferInAux = ptrAux;
                double psd;
                psd = computeBandPower.compute(bufferInAux);
                if (psd >= 0)
                {
                	opOutputPort.sendData(ConversionUtils.doubleToBytes(psd));
                }
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
