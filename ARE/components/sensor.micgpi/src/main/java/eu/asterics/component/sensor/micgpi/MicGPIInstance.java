

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

package eu.asterics.component.sensor.micgpi;


import java.util.*;
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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import eu.asterics.mw.services.AstericsThreadPool;
/**
 * 
 * <Describe purpose of this module>
 * 
 *  
 * @author David Thaller [dt@ki-i.at]
 */
public class MicGPIInstance extends AbstractRuntimeComponentInstance implements Runnable
{
	final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpInLow = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpInHigh = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	
	int propUpdateInterval = 10;
	int propThresholdHigh = 30;
	int propThresholdLow = -30;
	String propCaptureDevice = "System Default";
	// declare member variables here
	private RecordDevice recDev;
    private TargetDataLine targetDataLine;
    private boolean running = true;
	private boolean highState, lowState;
   /**
    * The class constructor.
    */
    public MicGPIInstance()
    {
        recDev = new RecordDevice();
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("thresholdLow".equalsIgnoreCase(portID))
		{
			return ipThresholdLow;
		}
		if ("thresholdHigh".equalsIgnoreCase(portID))
		{
			return ipThresholdHigh;
		}
		return null;
	}

	private final IRuntimeInputPort ipThresholdLow  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			propThresholdLow = ConversionUtils.intFromBytes(data);
		}
		
	};

	private final IRuntimeInputPort ipThresholdHigh = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
			propThresholdHigh = ConversionUtils.intFromBytes(data);
		}
		
	};
	
    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{
		if ("out".equalsIgnoreCase(portID))
		{
			return opOut;
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
		if ("inLow".equalsIgnoreCase(eventPortID))
		{
			return etpInLow;
		}
		if ("inHigh".equalsIgnoreCase(eventPortID))
		{
			return etpInHigh;
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
		if ("updateInterval".equalsIgnoreCase(propertyName))
		{
			return propUpdateInterval;
		}
	
		if ("captureDevice".equalsIgnoreCase(propertyName))
		{
			return propCaptureDevice;
		}
		if ("thresholdLow".equalsIgnoreCase(propertyName))
		{
			return propThresholdLow;
		}
		if ("thresholdHigh".equalsIgnoreCase(propertyName))
		{
			return propThresholdHigh;
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
		if ("updateInterval".equalsIgnoreCase(propertyName))
		{
			final int oldValue = propUpdateInterval;
			propUpdateInterval = Integer.parseInt((String)newValue);
			return oldValue;
		}
		if ("captureDevice".equalsIgnoreCase(propertyName)) {
			final String oldValue = propCaptureDevice;
			propCaptureDevice = (String) newValue;
			return oldValue;
		}
		if ("thresholdLow".equalsIgnoreCase(propertyName)) {
			final int oldValue = propThresholdLow;
			propThresholdLow = Integer.parseInt((String) newValue);
			return oldValue;
		}
		if ("thresholdHigh".equalsIgnoreCase(propertyName)) {
			final int oldValue = propThresholdHigh;
			propThresholdHigh = Integer.parseInt((String) newValue);
			return oldValue;
		}
        return null;
    }
	
	/**
	 * Returns a List of available CIM unique IDs
	 * @return list of string with CIM IDs
	 */
	public List<String> getRuntimePropertyList(String key) 
	{
		List<String> res = new ArrayList<String>(); 
		if (key.compareToIgnoreCase("captureDevice")==0)
		{
			res.add("System Default");
			List<String> list = recDev.getCaptureDeviceNames();
			for (String s : list)
				System.out.println(s);
			res.addAll(recDev.getCaptureDeviceNames());
		}
		return res;
	} 
    

     /**
      * Input Ports for receiving values.
      */


     /**
      * Event Listerner Ports.
      */


	@Override
    public void run() {
            if (this.targetDataLine == null) {
				AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
				return;
            }
			running = true;
			try {
				this.targetDataLine.open();
				this.targetDataLine.start();
			} catch (LineUnavailableException lue) {
				AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
				return;
			}
			byte [] data = new byte[1024];
			while (running == true) {
				try {
					int readBytes = targetDataLine.read(data, 0, 1024);
					if (readBytes == 0)
						continue;
					else
						opOut.sendData(ConversionUtils.intToByteArray(data[0]));
					for (int i = 0; i< readBytes; i++) {
						int value = data[i];
						
						if (value < propThresholdLow) {
							if (lowState == false) {
								etpInLow.raiseEvent();
								lowState = true;
							}
						} 
						if (value > propThresholdHigh) {
							if (highState == false) {
								etpInHigh.raiseEvent();
								highState = true;
							}
						} 
						if (value > -10 && value < 10) {
							highState = false;
							lowState = false;
						}
					}
					Thread.sleep(propUpdateInterval);
				} catch (InterruptedException ie) {}   	
        }
		this.targetDataLine.stop();
		this.targetDataLine.close();
    }
	 
	private TargetDataLine getTargetDataLine() {
	
		if (propCaptureDevice.equals("System Default")) {
			System.out.println("Getting Default Device");
			return recDev.getDefaultTargetDataLine();
		} else {
			System.out.println("Getting Device: " + propCaptureDevice);
			return recDev.getTargetDataLine(propCaptureDevice);
		}
	}
	  
	  
     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
		   
          super.start();
		  lowState = false;
		  highState = false;
		  targetDataLine = getTargetDataLine();
		  if (targetDataLine == null) {
			AstericsErrorHandling.instance.reportInfo(this, "Error opening capture device: " + propCaptureDevice);
			return;
		  }
		  AstericsThreadPool.instance.execute(this);
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
		running = false;
        super.stop();
      }
}