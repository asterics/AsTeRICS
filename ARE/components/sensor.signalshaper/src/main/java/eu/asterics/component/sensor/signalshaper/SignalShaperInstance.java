

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

package eu.asterics.component.sensor.signalshaper;


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
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * This component allow the user to compose the signal from the linear signals.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: May 30, 2012
 *         Time: 12:56:32 AM
 */
public class SignalShaperInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opOutput = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	private final String BEGIN_VALUE="beginValue";
	private final String END_VALUE="endValue";
	private final String TIME="time";
	private final int NumberOfLines=5;
	
	int propInterval = 20;
	int propNumberOfLines = 1;
	int propBehaviourAfterFinish = 0;
	
	/*
	double propBeginValue1 = 0;
	double propEndValue1 = 0;
	double propTime1 = 0;
	*/
	
	private double [] propBeginValueArray = new double[NumberOfLines];
	private double [] propEndValueArray = new double[NumberOfLines];
	private int [] propTimeArray = new int[NumberOfLines];

	// declare member variables here

	private boolean finish=false;
	private boolean restart=false;
	private boolean working=false;
    
   /**
    * The class constructor.
    */
    public SignalShaperInstance()
    {
        // empty constructor
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
		if ("output".equalsIgnoreCase(portID))
		{
			return opOutput;
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
		if ("start".equalsIgnoreCase(eventPortID))
		{
			return elpStart;
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

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("interval".equalsIgnoreCase(propertyName))
		{
			return propInterval;
		}
		if ("numberOfLines".equalsIgnoreCase(propertyName))
		{
			return propNumberOfLines-1;
		}
		if ("behaviourAfterFinish".equalsIgnoreCase(propertyName))
		{
			return propBehaviourAfterFinish;
		}
		
		int textSize=BEGIN_VALUE.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(BEGIN_VALUE))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  return propBeginValueArray[number-1];
	    		  }
	    	  }
	       }
	    }
	    
	    textSize=END_VALUE.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(END_VALUE))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  return propEndValueArray[number-1];
	    		  }
	    	  }
	       }
	    }
	    
	    textSize=TIME.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(TIME))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  return propTimeArray[number-1];
	    		  }
	    	  }
	       }
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
		if ("interval".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propInterval;
			propInterval = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("numberOfLines".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propNumberOfLines-1;
			propNumberOfLines = Integer.parseInt(newValue.toString())+1;
			return oldValue;
		}
		if ("behaviourAfterFinish".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propBehaviourAfterFinish;
			propBehaviourAfterFinish = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		
		
		
		int textSize=BEGIN_VALUE.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(BEGIN_VALUE))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  //return propBeginValueArray[number-1];
	    			  final double oldValue = propBeginValueArray[number-1];
	    			  propBeginValueArray[number-1] = Double.parseDouble((String)newValue);
	    			  return oldValue;
	    		  }
	    	  }
	       }
	    }
	    
	    textSize=END_VALUE.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(END_VALUE))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  //return propEndValueArray[number-1];
	    			  final double oldValue =  propEndValueArray[number-1];
	    			  propEndValueArray[number-1] = Double.parseDouble((String)newValue);
	    			  return oldValue;
	    		  }
	    	  }
	       }
	    }
	    
	    textSize=TIME.length();
	    if(propertyName.length()>textSize)
	    {
	    	String testName=propertyName.substring(0,textSize);
	    	if(testName.equalsIgnoreCase(TIME))
	    	{
	    	  String numberText=propertyName.substring(textSize);
	    	  int number=0;
	    	  boolean finish=false;
	    	    	  
	    	  try
	    	  {
	    		  number = Integer.parseInt(numberText);
	    	  }
	    	  catch(NumberFormatException ex)
	    	  {
	    		  finish=true;
	    	  }
	    	  
	    	  if(finish==false)
	    	  {
	    	    			
	    		  if(number>0 && number<=NumberOfLines)
	    		  {
	    			  //return propTimeArray[number-1];
	    			  final double oldValue = propTimeArray[number-1];
	    			  propTimeArray[number-1] = Integer.parseInt((String)newValue);
	    			  return oldValue;
	    		  }
	    	  }
	       }
	    }
	    
		

        return null;
    }

    


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpStart = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			startTimer();
		}
	};

	private double [] signal = null;
	int numberOfIntervals=0;
	
	 /**
     * Prepares the signal to send.
     */
	private void prepareSignal()
	{
		int signalTime=0;
		
		int [] timeSteps = new int[propNumberOfLines+1];
		timeSteps[0]=0;
		
		for(int i=0;i<propNumberOfLines;i++)
		{
			signalTime=signalTime+propTimeArray[i];
			timeSteps[i+1]=signalTime;
		}
		
		
		numberOfIntervals=signalTime/propInterval+1;
		if(signalTime%propInterval>0)
		{
			numberOfIntervals++;
		}
		signal=new double[numberOfIntervals];
		
		int line =0;
		//countedTime=0;
		
		boolean prepareTheLastPoint=false;
		
		for(int i=0;i<numberOfIntervals;i++)
		{
			if(prepareTheLastPoint)
			{
				signal[i]=propEndValueArray[propNumberOfLines-1];
			}
			else
			{
				int countedTime=i*propInterval;
				signal[i]=countValue(countedTime,line,timeSteps);
				if(i<numberOfIntervals-1)
				{
					int nextTime=countedTime+propInterval;
					int newLine=0;
					int j=propNumberOfLines;
					do
					{
						if(nextTime>timeSteps[j-1])
						{
							newLine=j-1;
							break;
						}
						j--;
					}
					while(j>line+1);
				
					if(newLine>0)
					{
						line=newLine;
					}
					
				}
			
				if(i==numberOfIntervals-2)
				{
					int nextTime=countedTime+propInterval;
					prepareTheLastPoint=true;
				}
			}
		}
		
		
		
		
		
	}
	
	 /**
     * Counts the value for the sample.
     * @param time the time of current sample
     * @param line the current line
     * @param timeSteps the time steps of the lines
     * @return the value for the sample
     */
	private double countValue(int time,int line,int[] timeSteps)
	{
		double v1= propBeginValueArray[line];
		double v2= propEndValueArray[line];
		double t1 = (double) timeSteps[line];
		double t2 = (double) timeSteps[line+1];
		
		double a = (v2-v1)/(t2-t1);
		double b = (v1*t2-v2*t1)/(t2-t1);
		
		double tx=(double)time;
		
		return a*tx+b;
	}
	
	 /**
     * Starts the signal generation.
     */
	private void startTimer()
	{
		sampleIndex=0;
		finish=false;
		restart=false;
		if(working)
		{
			restart=true;
		}
		else
		{
			AstericsThreadPool.instance.execute(timer);
		}
	}
	
	int sampleIndex=0;
	
	/**
     * Implements the timer for the component.
     */
	private final Runnable timer = new Runnable(){
  		
		/**
	     * The timer method.
	     */
		@Override
  		public void run() {
  			working=true;
  			do
  			{
  				if(restart)
  				{
  					restart =false;
  					sampleIndex=0;
  				}
  				if(sampleIndex>=numberOfIntervals)
  				{
  					if(propBehaviourAfterFinish==1)
  					{
  						sampleIndex=0;
  						opOutput.sendData(ConversionUtils.doubleToBytes(signal[sampleIndex]));
  	  					sampleIndex++;
  					}
  					else if(propBehaviourAfterFinish==2)
  					{
  						opOutput.sendData(ConversionUtils.doubleToBytes(signal[numberOfIntervals-1]));
  					}
  				}
  				else
  				{
  					opOutput.sendData(ConversionUtils.doubleToBytes(signal[sampleIndex]));
  					sampleIndex++;
  				}
  				
  				try{
  					Thread.sleep(propInterval);
  				}catch (InterruptedException e) {}
  			
  			}while(!finish);
  			working=false;
  		}
  	};

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          super.start();
          prepareSignal();
          //startTimer();
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  super.pause();
    	  finish=true;
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          prepareSignal();
          //startTimer();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  super.stop();
    	  finish=true;
      }
}