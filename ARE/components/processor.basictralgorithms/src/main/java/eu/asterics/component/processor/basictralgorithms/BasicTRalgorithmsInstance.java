

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

package eu.asterics.component.processor.basictralgorithms;

import java.util.ArrayList;

import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Implements basic algorithms for reduction of the user hands tremor.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 15, 2012
 *         Time: 10:06:15 AM
 */
public class BasicTRalgorithmsInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutputX = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opOutputY = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propAlgorithm = 0;
	int propEventsType = 0;
	int propBufferSize = 20;
	double propMaxDistance = 10;
	double propFactor = 0.5;
	int propDegree = 1;
	// declare member variables here
    private final int BufferSize=10;
    private final int AM_Algorithm=0;
    private final int OR_Algorithm =1;
    private final int ES_Algorithm =2;
    private final int E_Absolute=0;
    private final int E_Relative=1;
    
    private ArrayList<Integer> bufferX=new ArrayList();
    private ArrayList<Integer> bufferY=new ArrayList();
    
    private ArithmeticMeanAlgorithm AMalgorithm;
    private OutlierReductionAlgorithm ORalgorithm;
    private OutlierReductionAlgorithmRelative ORRalgorithm;
    private ExponentialSmoothingAlgorithm ESalgorithm;
    private TremorReductionBasicAlgorithm currentAlgorithm;
    
    int currentBufferSize=propBufferSize;
    double currentMaxDistance=propMaxDistance;
    double currentFactor=propFactor;
    private Lock lock = new ReentrantLock();
    
   /**
    * The class constructor.
    */
    public BasicTRalgorithmsInstance()
    {
        AMalgorithm=new ArithmeticMeanAlgorithm(propBufferSize);
        ORalgorithm=new OutlierReductionAlgorithm(propMaxDistance);
        ORRalgorithm =new OutlierReductionAlgorithmRelative (propMaxDistance);
        ESalgorithm = new ExponentialSmoothingAlgorithm(propFactor,propDegree);
        currentAlgorithm=AMalgorithm;
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("inputX".equalsIgnoreCase(portID))
		{
			return ipInputX;
		}
		if ("inputY".equalsIgnoreCase(portID))
		{
			return ipInputY;
		}
		if ("bufferSize".equalsIgnoreCase(portID))
		{
			return ipBufferSize;
		}
		if ("maxDistance".equalsIgnoreCase(portID))
		{
			return ipMaxDistance;
		}
		if ("factor".equalsIgnoreCase(portID))
		{
			return ipFactor;
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
		if ("outputX".equalsIgnoreCase(portID))
		{
			return opOutputX;
		}
		if ("outputY".equalsIgnoreCase(portID))
		{
			return opOutputY;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("algorithm".equalsIgnoreCase(propertyName))
		{
			return propAlgorithm;
		}
		if ("eventsType".equalsIgnoreCase(propertyName))
		{
			return propEventsType;
		}
		if ("bufferSize".equalsIgnoreCase(propertyName))
		{
			return propBufferSize;
		}
		if ("maxDistance".equalsIgnoreCase(propertyName))
		{
			return propMaxDistance;
		}
		if ("factor".equalsIgnoreCase(propertyName))
		{
			return propFactor;
		}
		if ("degree".equalsIgnoreCase(propertyName))
		{
			return propDegree;
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
		if ("algorithm".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAlgorithm;
			propAlgorithm = Integer.parseInt(newValue.toString());
			if((propAlgorithm<0)||(propAlgorithm>2))
			{
				propAlgorithm=0;
			}
			
			switch(propAlgorithm)
			{
			case AM_Algorithm:
				currentAlgorithm=AMalgorithm;
				currentAlgorithm.clean();
				break;
			case OR_Algorithm:
				if(propEventsType==E_Absolute)
				{
					currentAlgorithm=ORalgorithm;
				}
				else
				{
					currentAlgorithm=ORRalgorithm;
				}
				currentAlgorithm.clean();
				break;
			case ES_Algorithm:
				currentAlgorithm=ESalgorithm;
				currentAlgorithm.clean();
				break;
			}
			
			return oldValue;
		}
		if ("eventsType".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propEventsType;
			propEventsType = Integer.parseInt(newValue.toString());
			if((propEventsType<0)||(propEventsType>1))
			{
				propEventsType=0;
			}
			
			if(propAlgorithm==OR_Algorithm)
			{
				if(propEventsType==E_Absolute)
				{
					currentAlgorithm=ORalgorithm;
				}
				else
				{
					currentAlgorithm=ORRalgorithm;
				}
			}
			currentAlgorithm.clean();
			return oldValue;
		}
		if ("bufferSize".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBufferSize;
			propBufferSize = Integer.parseInt(newValue.toString());
			if(propBufferSize<1)
			{
				propBufferSize =20;
			}
			
			currentBufferSize=propBufferSize;
			
			AMalgorithm.setBufferSize(propBufferSize);
			
			return oldValue;
		}
		if ("maxDistance".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propMaxDistance;
			propMaxDistance = Double.parseDouble((String)newValue);
			if(propMaxDistance<1)
			{
				propMaxDistance=10.0;
			}
			
			currentMaxDistance=propMaxDistance;
			
			ORalgorithm.SetDistance(propMaxDistance);
			ORRalgorithm.SetDistance(propMaxDistance);
			return oldValue;
		}
		if ("factor".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propFactor;
			propFactor = Double.parseDouble((String)newValue);
			
			if((propFactor<=0)||(propFactor>=1))
			{
				propFactor=0.5;
			}
			
			currentFactor=propFactor;
			
			ESalgorithm.setFactor(propFactor);
			
			return oldValue;
		}
		if ("degree".equalsIgnoreCase(propertyName))
		{
			final double oldValue = propDegree;
			propDegree = Integer.parseInt((String)newValue);
			
			int degree=propDegree+1;
			if((degree<1)||(degree>4))
			{
				degree=1;
			}
			
			ESalgorithm.setDegree(degree);
			
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipInputX  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int x=0;
			
			x = ConversionUtils.intFromBytes(data);
			
			try
			{
				lock.lock();
			
				if (bufferY.size()>0)
				{
				
					int y=bufferY.get(0);
					bufferY.remove(0);
				
					AlgorithmPoint inputPoint=new AlgorithmPoint(x,y);
					AlgorithmPoint outputPoint = currentAlgorithm.calcualteNewPoint(inputPoint);
				
					opOutputX.sendData(ConversionUtils.intToBytes(outputPoint.getX()));
					opOutputY.sendData(ConversionUtils.intToBytes(outputPoint.getY()));
				
				}
				else
				{
				
					if(bufferX.size()>BufferSize)
					{
						bufferX.remove(0);
						bufferX.add(x);
					}
					else
					{
						bufferX.add(x);
					}
				
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
	};
	private final IRuntimeInputPort ipInputY  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int y=0;
			
			y = ConversionUtils.intFromBytes(data);
			
			try
			{
				lock.lock();
				
				if (bufferX.size()>0)
				{
				
					int x=bufferX.get(0);
					bufferX.remove(0);
				
					AlgorithmPoint inputPoint=new AlgorithmPoint(x,y);
					AlgorithmPoint outputPoint = currentAlgorithm.calcualteNewPoint(inputPoint);
				
					opOutputX.sendData(ConversionUtils.intToBytes(outputPoint.getX()));
					opOutputY.sendData(ConversionUtils.intToBytes(outputPoint.getY()));
				
				}
				else
				{
				
					if(bufferY.size()>BufferSize)
					{
						bufferY.remove(0);
						bufferY.add(y);
					}
					else
					{
						bufferY.add(y);
					}
				
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
	};
	private final IRuntimeInputPort ipBufferSize  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			int tmpCurrentBufferSize=ConversionUtils.intFromBytes(data);
			
			try
			{
			
				lock.lock();
				if(tmpCurrentBufferSize<1)
				{
				
				}
				else
				{
					currentBufferSize=tmpCurrentBufferSize;
					AMalgorithm.setBufferSize(currentBufferSize);
				}
			}
			finally
			{
				lock.unlock();
			}
			
		}
		
	};
	private final IRuntimeInputPort ipMaxDistance  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			double tmpCurrentMaxDistance=ConversionUtils.doubleFromBytes(data);
			
			try
			{
			
				lock.lock();
				if(tmpCurrentMaxDistance<1)
				{
				}
				else
				{
					currentMaxDistance=tmpCurrentMaxDistance;
					ORalgorithm.SetDistance(currentMaxDistance);
					ORRalgorithm.SetDistance(currentMaxDistance);
				}
			}
			finally
			{
				lock.unlock();
			}
			
			
		}
		
	};
	private final IRuntimeInputPort ipFactor  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			
			double tmpCurrentFactor=ConversionUtils.doubleFromBytes(data);
			
			try
			{
			
				lock.lock();
				if((tmpCurrentFactor<=0)||(tmpCurrentFactor>=1))
				{
				
				}
				else
				{
					currentFactor=tmpCurrentFactor;
					ESalgorithm.setFactor(currentFactor);
				}
			}
			finally
			{
				lock.unlock();
			}
		}
		
	};


     /**
      * Event Listerner Ports.
      */

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  System.out.println("start TR");
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