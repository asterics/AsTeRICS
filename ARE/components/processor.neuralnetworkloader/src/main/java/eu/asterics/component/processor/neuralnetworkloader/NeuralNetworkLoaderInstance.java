

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

package eu.asterics.component.processor.neuralnetworkloader;


import java.util.logging.Logger;

//import eu.asterics.component.processor.constantdispatcher.ConstantDispatcherInstance.SlotDispatchPort;
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
import org.encog.Encog;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 
 * This component uses encog framework.
 * @see <a href="http://www.heatonresearch.com/encog">encog</a> 
 * 
 * It can load the neural network from the EG file and use it for regression or classification.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jul 05, 2011
 *         Time: 11:51:00 AM
 */
public class NeuralNetworkLoaderInstance extends AbstractRuntimeComponentInstance
{
	private final int NUMBER_OF_INPUTS = 32;
	private final int NUMBER_OF_OUTPUTS = 32;
	private final String IP_INPUT="input";
	private final String OP_OUTPUT="output";
	private final int BufferSize=10;
	
	final IRuntimeOutputPort[] opOutputArray = new DefaultRuntimeOutputPort[NUMBER_OF_OUTPUTS];
	private InputPort[] ipInpuArray= new InputPort[NUMBER_OF_INPUTS];
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
	
	String propFilePath = "";
	BasicNetwork neuralNetwork=null;
	int inputCount=0;
	int outputCount=0;
	int usedInputs=0;
	boolean networkLoaded=false;
	// declare member variables here
	ArrayList<Double>[] buffers = (ArrayList<Double>[])new ArrayList[32];
	private Lock lock = new ReentrantLock();
  
    
   /**
    * The class constructor.
    */
    public NeuralNetworkLoaderInstance()
    {
        for(int i=0;i<NUMBER_OF_OUTPUTS;i++)
        {
        	opOutputArray[i]=new DefaultRuntimeOutputPort();
        	ipInpuArray[i]= new InputPort();
        	ipInpuArray[i].portID=i;
        }
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		
    	int ipInputSize=IP_INPUT.length();
    	if(portID.length()>ipInputSize)
        {
    		String testName=portID.substring(0,ipInputSize);
    		if(testName.equalsIgnoreCase(IP_INPUT))
        	{
    			String portNumberText=portID.substring(ipInputSize);
    	    	int portNumberValue; 
    	    	try
    	    	{
    	    		 portNumberValue = Integer.parseInt(portNumberText);
    	    	}
    	    	catch(NumberFormatException ex)
    	    	{
    	    		return null;
    	    	}
    	    	
    	    	if(portNumberValue>0 && portNumberValue<=NUMBER_OF_INPUTS)
    	    	{
    	    	    return ipInpuArray[portNumberValue-1];
    	    	}
    	    	else
    	    	{
    	    	    return null;
    	    	}
        	}
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
		/*
    	if ("output1".equalsIgnoreCase(portID))
		{
			return opOutput1;
		}*/
    	
    	int ipOutputSize=OP_OUTPUT.length();
    	if(portID.length()>ipOutputSize)
        {
    		String testName=portID.substring(0,ipOutputSize);
    		if(testName.equalsIgnoreCase(OP_OUTPUT))
        	{
    			String portNumberText=portID.substring(ipOutputSize);
    	    	int portNumberValue; 
    	    	try
    	    	{
    	    		 portNumberValue = Integer.parseInt(portNumberText);
    	    	}
    	    	catch(NumberFormatException ex)
    	    	{
    	    		return null;
    	    	}
    	    	
    	    	if(portNumberValue>0 && portNumberValue<=NUMBER_OF_OUTPUTS)
    	    	{
    	    	    return opOutputArray[portNumberValue-1];
    	    	}
    	    	else
    	    	{
    	    	    return null;
    	    	}
        	}
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
		if ("filePath".equalsIgnoreCase(propertyName))
		{
			return propFilePath;
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
		if ("filePath".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propFilePath;
			propFilePath = (String)newValue;
			return oldValue;
		}
		
        return null;
    }
    
    
    /**
     * Loads a neural network from a the EG file.
     * @param fileName   the file path.
     */
    private void loadEGFile(String fileName)
    {
    	if(fileName==null)
    	{
    		networkLoaded=false;
    		return;
    	}
    	
    	try
    	{
    		File file = new File(fileName);
    		File directory = new File(file.getParent());
    		
    		EncogDirectoryPersistence edp = new EncogDirectoryPersistence(directory);
    		
    		neuralNetwork=(BasicNetwork)EncogDirectoryPersistence.loadObject(file);
    		
    		
    		if(neuralNetwork==null)
        	{
        		networkLoaded=false;
        		return;
        	}
        	inputCount=neuralNetwork.getInputCount();
        	outputCount=neuralNetwork.getOutputCount();
    	}
    	catch(Throwable t)
    	{
    		neuralNetwork=null;
    		networkLoaded=false;
    		AstericsErrorHandling.instance.getLogger().warning("Neural Network is not loaded!!!");
    	}
    	
  
    	
    	if(inputCount>NUMBER_OF_INPUTS)
    	{
    		usedInputs=NUMBER_OF_INPUTS;
    	}
    	else
    	{
    		usedInputs=inputCount;
    	}
    	
    	for(int i=0;i<usedInputs;i++)
    	{
    		buffers[i]=new ArrayList();
    	}
    	networkLoaded=true;
    }
    
    /**
     * Executes the neural network.
     */
    private void networkCompute()
    {
    	double inputData[]=new double[inputCount];
    	if(inputCount>usedInputs)
    	{
    		for(int i=0;i<inputCount;i++)
    		{
    			inputData[i]=0;
    		}
    	}
    	
    	for(int i=0;i<usedInputs;i++)
    	{
    		inputData[i]=buffers[i].get(0);
    		buffers[i].remove(0);
    	}
    	
    	double outputData[]  =new double[outputCount];
    	
    	if(neuralNetwork!=null)
    	{
    		if(networkLoaded)
    		{
    			neuralNetwork.compute(inputData,outputData);
    			int usedOutputs=NUMBER_OF_OUTPUTS;
    			if(outputCount<usedOutputs)
    			{
    				usedOutputs=outputCount;
    			}
    			
    			for(int i=0;i<usedOutputs;i++)
    			{
    				opOutputArray[i].sendData(ConversionUtils.doubleToBytes(outputData[i]));
    			}
    		}
    	}
    }

     
   // /**
   //   * Input Ports for receiving values.
   //   */
	/*
    private final IRuntimeInputPort ipInput1  = new IRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 // insert data reception handling here, e.g.: 
				 // myVar = ConversionUtils.doubleFromBytes(data); 
				 // myVar = ConversionUtils.stringFromBytes(data); 
				 // myVar = ConversionUtils.intFromBytes(data); 
		}
	};*/
    
    /**
     * Input port for receive slot number to dispatch.
     */
    private class InputPort extends DefaultRuntimeInputPort
    {
      
    	int portID=-1;
    	public void receiveData(byte[] data)
    	{
    		double inputData = ConversionUtils.doubleFromBytes(data);
    		if(portID<usedInputs)
    		{
    			try
    			{
    				lock.lock();
    				if(buffers[portID].size()<BufferSize)
    				{
    					buffers[portID].add(inputData);
    				}
    				else
    				{
    					buffers[portID].remove(0);
    					buffers[portID].add(inputData);
    				}
    				
    				boolean empty=false;
    				for(int i =0;i<usedInputs;i++)
    				{
    					if(buffers[i].size()==0)
    					{
    						empty=true;
    						break;
    						
    					}
    				}
    				if(empty==false)
    				{
    					networkCompute();
    				}
    			}
    			finally
    			{
    				lock.unlock();
    				
    			}
    		}
        }
    	
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
    	  try
    	  {
    		  loadEGFile(propFilePath);
    	  }
    	  catch(Exception e)
    	  {
    		  AstericsErrorHandling.instance.getLogger().warning("Neural Network is not loaded!!!");
    	  }
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
    	  networkLoaded=false;
          super.stop();
      }
}