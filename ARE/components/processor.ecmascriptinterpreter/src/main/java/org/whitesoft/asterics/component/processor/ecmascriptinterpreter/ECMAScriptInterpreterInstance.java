

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

package org.whitesoft.asterics.component.processor.ecmascriptinterpreter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * This component is a general purpose processor that can relays the input and
 * incoming events to a script compatible to the ECMA script specification (e.g.
 * JavaScript). The script is specified by the property scriptname. If the property
 * is left empty, the component will load the file "script.js" from local storage.
 * If this file does not exist, the component will generate the file in local storage
 * and fill it with a default "pass-through" script. 
 * 
 *  There are certain constraints for the script:
 *  - the script has to contain an object named scriptclass.
 *  - the object has to implement a method dataInput(input_index, input_data)
 *  - the object has to implement a method eventInput(event_index)
 *  
 *  The script is provided with the following external variables:
 *  - output:   an array of size 8 representing 8 IRuntimeOutputPorts
 *  - eventout: an array of size 8 representing 8 IRuntimeEventTriggererPorts
 *  - property: an array of size 8 holding strings with the property inputs from the components property fields 
 * 
 *  The sendData method of the output variables has to be called with a string.
 *  If necessary this needs to be converted into a Java string, this can be done like this:
 *  
 *  	str = new java.lang.String(in_data);
 *	    output[in_nb].sendData(str.getBytes());
 *  
 * @author Christoph Weiss [christoph.weiss@gmail.com]
 *         Date: 
 *         Time: 
 */
public class ECMAScriptInterpreterInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeInputPort [] ipInputPorts  = new IRuntimeInputPort[NUMBER_OF_INPUTS];
	final IRuntimeOutputPort [] opOutputPorts = new IRuntimeOutputPort[NUMBER_OF_OUTPUTS];
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventListenerPort  [] elpEventListenerPorts = new IRuntimeEventListenerPort[NUMBER_OF_EVENT_INPUTS]; 
	final IRuntimeEventTriggererPort [] etpEventTriggerPorts = new IRuntimeEventTriggererPort[NUMBER_OF_EVENT_OUTPUTS];
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propScriptname = "";
	String [] propValue = new String[NUMBER_OF_PROPS];

	// declare member variables here

	ScriptEngine engine;
	CompiledScript cs = null;
	Object scriptclass = null;	
    
    static final int NUMBER_OF_INPUTS = 8;
    static final int NUMBER_OF_OUTPUTS = 8;
    static final int NUMBER_OF_EVENT_INPUTS = 8;
    static final int NUMBER_OF_EVENT_OUTPUTS = 8;
    static final int NUMBER_OF_PROPS = 8;
    
    static final String defaultscriptcontent =
    		"// the script is provided with the following external vars:\n" + 
    		"// output:   an array of size 8 representing 8 IRuntimeOutputPorts\n" + 
    		"// eventout: an array of size 8 representing 8 IRuntimeEventTriggererPorts\n" +
    		"// property: an array of size 8 holding strings with the property inputs from the components property fields\n\n" + 
    		"function clazz(dataout, evout) {\n\n"+ 
    		 
    		"	this.dataInput = function(in_nb, in_data) {\n" +
    		"// the next line shows how to access the properties\n" +
    		"//		in_data = in_data.concat(property[in_nb]);\n" +
    		"		str = new java.lang.String(in_data);\n" +
    		"		output[in_nb].sendData(str.getBytes());\n" +
    		"	};\n\n" + 
    			
    		"	this.eventInput = function(ev_nb) {\n" +
    		"		eventout[ev_nb].raiseEvent();\n" +
    		"	};\n" +
    		"};\n\n" +

    		"var scriptclass = new clazz();\n\n";

   /**
    * The class constructor.
    */
    public ECMAScriptInterpreterInstance()
    {

    	for (int i = 0; i < ipInputPorts.length; i++)
    	{
    		ipInputPorts[i] = new InputPort(i);
    	}

    	for (int i = 0; i < opOutputPorts.length; i++)
    	{
    		opOutputPorts[i] = new DefaultRuntimeOutputPort();
    	}
    	

    	for (int i = 0; i < elpEventListenerPorts.length; i++)
    	{
    		elpEventListenerPorts[i] = new EventListenerPort(i);
    	}
    	
    	for (int i = 0; i < etpEventTriggerPorts.length; i++)
    	{
    		etpEventTriggerPorts[i] = new DefaultRuntimeEventTriggererPort();
    	}
    	
    	for (int i = 0; i < propValue.length; i++)
    	{
    		propValue[i] = "";
    	}
    	
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if (portID.startsWith("inputPort"))
		{
			String strstr = portID.replace("inputPort", "");
			int idx = Integer.parseInt(strstr);
			return ipInputPorts[idx - 1];
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
		if (portID.startsWith("outputPort"))
		{
			String strstr = portID.replace("outputPort", "");
			int idx = Integer.parseInt(strstr);
			return opOutputPorts[idx - 1];
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
		if (eventPortID.startsWith("elpPort"))
		{
			String strstr = eventPortID.replace("elpPort", "");
			int idx = Integer.parseInt(strstr);
			return elpEventListenerPorts[idx - 1];
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
		if (eventPortID.startsWith("etpPort"))
		{
			String strstr = eventPortID.replace("etpPort", "");
			int idx = Integer.parseInt(strstr);
			return etpEventTriggerPorts[idx - 1];
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
		if ("scriptname".equalsIgnoreCase(propertyName))
		{
			return propScriptname;
		}

		if (propertyName.startsWith("value"))
		{
			String strstr = propertyName.replace("value", "");
			int idx = Integer.parseInt(strstr);
			return propValue[idx - 1];
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
		if ("scriptname".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propScriptname;
			propScriptname = (String) newValue;
			return oldValue;
		}
 
		if (propertyName.startsWith("value"))
		{
			String strstr = propertyName.replace("value", "");
			int idx = Integer.parseInt(strstr);
			Object oldValue =  propValue[idx - 1];
			propValue[idx - 1] = (String) newValue;
			return oldValue;
		}

		return null;
    }

     /**
      * Input Ports for receiving values.
      */
    class InputPort implements IRuntimeInputPort
    {
    	int index; 
    	
    	public InputPort(int index)
    	{
    		this.index = index;
    	}

		@Override
		public void receiveData(byte[] data) 
		{
			if (scriptclass != null)
			{
				Invocable inv = (Invocable) (cs == null ? engine : cs.getEngine());

				try {
					inv.invokeMethod(scriptclass, "dataInput", index, new String(data));
				} catch (NoSuchMethodException | ScriptException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void startBuffering(AbstractRuntimeComponentInstance c,
				String portID) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stopBuffering(AbstractRuntimeComponentInstance c,
				String portID) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isBuffered() {
			return false;
		}
    };
 
     /**
      * Event Listerner Ports.
      */
	class EventListenerPort implements IRuntimeEventListenerPort
	{
		int index; 
		
		public EventListenerPort(int idx)
		{
			this.index = idx;
		}
		
		public void receiveEvent(final String data)
		{
			if (scriptclass != null)
			{
				Invocable inv = (Invocable) (cs == null ? engine : cs.getEngine());
				try {
					inv.invokeMethod(scriptclass, "eventInput", index);
				} catch (NoSuchMethodException | ScriptException e) {
					e.printStackTrace();
				}
			}
		}
	};


	// the script is provided with the vars:
	// output:   an array of size 8 representing 8 IRuntimeOutputPorts
	// eventout: an array of size 8 representing 8 IRuntimeEventTriggererPorts
	// property: an array of size 8 holding strings with the property inputs from the components property fields 

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          ScriptEngineManager factory = new ScriptEngineManager();
          // create JavaScript engine
          engine = factory.getEngineByName("JavaScript");
          engine.put("output", opOutputPorts);
          engine.put("eventout", etpEventTriggerPorts);
          engine.put("property", propValue);

          try {

        	  File file = null;
        	  if (propScriptname.isEmpty())
        	  {
        		  file = AREServices.instance.getLocalStorageFile(this, "script.js");
        		  boolean fillScript = false;
        		  BufferedReader br = new BufferedReader(new FileReader(file));     
        		  try {
        			  if (br.readLine() == null) 
        			  {
        				  fillScript = true;
					  }
            		  br.close();
        		  } catch (IOException e) {
					e.printStackTrace();
        		  }
        		  
        		  if (fillScript)
        		  {
        			  try {
						Writer writer = new FileWriter(file);
						writer.write(defaultscriptcontent);
						writer.flush();
						writer.close();
        			  } catch (IOException e) {
        				  e.printStackTrace();
        			  }
        		  }
        	  }

        	  System.out.println("Opening " + 
        			  (propScriptname.isEmpty() ? "default script from local storage" : propScriptname)
        			  + " ...");
        	  
        	  FileReader reader;
        	  if (propScriptname.isEmpty())
        		  reader = new FileReader(file);
        	  else
        		  reader = new FileReader(propScriptname);
        	  
              if (engine instanceof Compilable)
              {
                  System.out.println("Compiling " + 
                		  	(propScriptname.isEmpty() ? "default script from local storage" : propScriptname) + "...");
                  
                  Compilable compEngine = (Compilable)engine;
                  cs = compEngine.compile(reader);
                  cs.eval();
              }
              else
                  engine.eval(reader);

              if (cs != null)
            	  scriptclass = cs.getEngine().get("scriptclass");
              else
            	  scriptclass = engine.get("scriptclass");

          } catch (FileNotFoundException | ScriptException e) {
        	  e.printStackTrace();
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
          super.stop();
  		  engine = null;
  		  cs = null;
      }
}