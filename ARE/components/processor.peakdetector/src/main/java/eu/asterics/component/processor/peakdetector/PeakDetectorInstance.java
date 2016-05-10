

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

package eu.asterics.component.processor.peakdetector;


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

/**
 * 
 * detects peaks (tops, valleys or both) in an incoming signal
 * outputs events and peak-to-peak timing
 * 
 *  
 * @author <Chris Veigl> [veigl@technikum-wien.at]
 *         Date: 02-09-2013
 *         Time: 
 */
public class PeakDetectorInstance extends AbstractRuntimeComponentInstance
{
	
	final IRuntimeOutputPort opTop = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opBottom = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opTime = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpTopDetected = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpBottomDetected = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propMode = 0;
	int propComparePeaks = 0;
	int propValidTopPercentage = 0;
	int propValidBottomPercentage = 0;
	int propTimeMode = 0;

	// declare member variables here

	final int MODE_TOPS=0;
	final int MODE_BOTTOMS=1;
	final int MODE_BOTH=2;

	final int TIMEMODE_MILLIS=0;
	final int TIMEMODE_BPM=1;

	final int SLOPE_IDLE=0;
	final int SLOPE_RISING=1;
	final int SLOPE_FALLING=2;
	
    private int oldSlope = SLOPE_IDLE; 
    private int actSlope = SLOPE_IDLE; 
    
    private double oldVal;
    private double actVal;
    
	private long lastTimestamp=0;
	private long actTimestamp;
	
	double[] topHistory;
	double[] bottomHistory;
	
	int topCount=0;
	int bottomCount=0;

    
   /**
    * The class constructor.
    */
    public PeakDetectorInstance()
    {
       topHistory=new double[100];
       bottomHistory=new double[100];
       for (int i=0;i<100;i++)
       {
    	   topHistory[i]=0;
    	   bottomHistory[i]=0;
       }
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("in".equalsIgnoreCase(portID))
		{
			return ipIn;
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
		if ("top".equalsIgnoreCase(portID))
		{
			return opTop;
		}
		if ("bottom".equalsIgnoreCase(portID))
		{
			return opBottom;
		}
		if ("time".equalsIgnoreCase(portID))
		{
			return opTime;
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
		if ("topDetected".equalsIgnoreCase(eventPortID))
		{
			return etpTopDetected;
		}
		if ("bottomDetected".equalsIgnoreCase(eventPortID))
		{
			return etpBottomDetected;
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
		if ("mode".equalsIgnoreCase(propertyName))
		{
			return propMode;
		}
		if ("comparePeaks".equalsIgnoreCase(propertyName))
		{
			return propComparePeaks;
		}
		if ("validTopPercentage".equalsIgnoreCase(propertyName))
		{
			return propValidTopPercentage;
		}
		if ("validBottomPercentage".equalsIgnoreCase(propertyName))
		{
			return propValidBottomPercentage;
		}
		if ("timeMode".equalsIgnoreCase(propertyName))
		{
			return propTimeMode;
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
		if ("mode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMode;
			propMode = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("comparePeaks".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComparePeaks;
			propComparePeaks = Integer.parseInt(newValue.toString());
			if (propComparePeaks<0) propComparePeaks=0;
			if (propComparePeaks>99) propComparePeaks=99;
			return oldValue;
		}
		if ("validTopPercentage".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propValidTopPercentage;
			propValidTopPercentage = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("validBottomPercentage".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propValidBottomPercentage;
			propValidBottomPercentage = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("timeMode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTimeMode;
			propTimeMode = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipIn  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				actVal = ConversionUtils.doubleFromBytes(data); 
				
				if (actVal > oldVal) actSlope=SLOPE_RISING;
				if (actVal < oldVal) actSlope=SLOPE_FALLING;

				
				if (oldSlope!=SLOPE_IDLE)
				{
					  if ((propMode==MODE_TOPS)||(propMode==MODE_BOTH)) 
					  {
							if ((actSlope==SLOPE_FALLING) && (oldSlope==SLOPE_RISING))
							{
								if (validTop(oldVal))
								{
									etpTopDetected.raiseEvent();
									opTop.sendData(ConversionUtils.doubleToBytes(oldVal));
									sendTimestamp();
								}
							}
					  }
					  if ((propMode==MODE_BOTTOMS)||(propMode==MODE_BOTH))
					  {
							if ((actSlope==SLOPE_RISING) && (oldSlope==SLOPE_FALLING))
							{
								if (validBottom(oldVal))
								{
									etpBottomDetected.raiseEvent();
									opBottom.sendData(ConversionUtils.doubleToBytes(oldVal));
									sendTimestamp();
								}
							}
					  }
				}
				oldVal=actVal;
				oldSlope=actSlope;
		}
	};


	void sendTimestamp()
	{
		actTimestamp=System.currentTimeMillis();
		if (lastTimestamp!=0)
		{
			if (propTimeMode==TIMEMODE_MILLIS)
				opTime.sendData(ConversionUtils.doubleToBytes(actTimestamp-lastTimestamp));
			else 
				opTime.sendData(ConversionUtils.doubleToBytes(60000/(actTimestamp-lastTimestamp)));
		}
		lastTimestamp=actTimestamp;
	}

	
	boolean validTop (double actTop)
	{
		double avg=0;
		
		if (propComparePeaks==0) return true;

		topHistory[topCount]=actTop;
		topCount=(topCount+1)%propComparePeaks;

		for (int i=0;i<propComparePeaks;i++)
			avg=avg+topHistory[i];
		
		avg/=propComparePeaks;
		if (actTop > avg*(double)propValidTopPercentage/100.0)
		{
			return(true);
		}
		return false;
	}
	
	boolean validBottom (double actBottom)
	{
		double avg=0;
		
		if (propComparePeaks==0) return true;
		bottomHistory[bottomCount]=actBottom;
		bottomCount=(bottomCount+1)%propComparePeaks; 
		for (int i=0;i<propComparePeaks;i++)
			avg=avg+bottomHistory[i];
		
		avg/=propComparePeaks;
		if (actBottom < avg*propValidBottomPercentage/100.0) return(true);
		return false;
	}
	
     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {

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
}