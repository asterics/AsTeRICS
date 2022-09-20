

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.irmicro;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
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
 * The IRMicro plugin connects to a Microcontroller via a COM port 
 * in oreder to receive and sent inraret remote control commands (raw timing values)
 * the values are stored in (or loded from) a CSV file in the ARE subfolder ./data/processor.IRMicro
 * 
 *  
 * @author Chris [veigl@technikum-wien.at]
 *         Date: 2017-12-14
 */
public class IRMicroInstance extends AbstractRuntimeComponentInstance
{
	final IRuntimeOutputPort opReceivedName = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opReceivedHex = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpRecordFinish = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpRecordTimeout = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	String propComPort = "COM2";
	int propTimeout = 10000;

	// declare member variables here

    String dataFilePath = "./data/processor.IRMicro/";

    CIMPortController portController = null;
    private boolean running = false;
    private static boolean messageReceived = false;
    private static int timeout = 0;
    String incomingData = "";
    String receivedMessage = "";

    private InputStream in = null;
    private OutputStream out = null;
    Thread readThread = null;
    
    boolean recordNext=false;
	String recordCmdName="";

    
   /**
    * The class constructor.
    */
    public IRMicroInstance()
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
		if ("sendName".equalsIgnoreCase(portID))
		{
			return ipSendName;
		}
		if ("recordName".equalsIgnoreCase(portID))
		{
			return ipRecordName;
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
		if ("receivedName".equalsIgnoreCase(portID))
		{
			return opReceivedName;
		}
		if ("receivedHex".equalsIgnoreCase(portID))
		{
			return opReceivedHex;
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
		if ("clearAll".equalsIgnoreCase(eventPortID))
		{
			return elpClearAll;
		}
		if ("clearLast".equalsIgnoreCase(eventPortID))
		{
			return elpClearLast;
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
		if ("recordFinish".equalsIgnoreCase(eventPortID))
		{
			return etpRecordFinish;
		}
		if ("recordTimeout".equalsIgnoreCase(eventPortID))
		{
			return etpRecordTimeout;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			return propComPort;
		}
		if ("timeout".equalsIgnoreCase(propertyName))
		{
			return propTimeout;
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
		if ("comPort".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propComPort;
			propComPort = (String)newValue;
			return oldValue;
		}
		if ("timeout".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTimeout;
			propTimeout = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipSendName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			String sendCmdName=ConversionUtils.stringFromBytes(data);
	    	System.out.println("send "+sendCmdName+" !");
		    String timings=loadCsvTimings(sendCmdName); 
		    if (timings==null) { System.out.println("No timings found!"); }
		    else {
		    	sendToIRMicro (timings);
		    }
		}
	};
	
	private final IRuntimeInputPort ipRecordName  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
			recordCmdName=ConversionUtils.stringFromBytes(data);
	    	System.out.println("recording next IR code as "+recordCmdName+" !");
	    	recordNext=true;
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpClearAll = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};
	final IRuntimeEventListenerPort elpClearLast = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 // insert event handling here 
		}
	};


     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
          if (!openCOMPort()) {
              System.out.println("IRMicro: open COM Port failed");
              AstericsErrorHandling.instance.reportError(this, "Could not open IRMicro Module at COM" + propComPort
                      + ". Please verify that the Module is connected to an USB Port, the driver is installed and the COM Port number is correct.");
              return;
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
      }



      private boolean openCOMPort() {
          portController = CIMPortManager.getInstance().getRawConnection(propComPort, 9600, true);

          if (portController == null) {
              System.out.println("IRMicro: Could not construct raw port controller, please verify that the COM port is valid.");
              return false;
          } else {
              System.out.println("IRMicro: " + propComPort + " opened!");
              System.out.println("IRMicro: starting reader thread !");
              in = portController.getInputStream();
              out = portController.getOutputStream();
              readThread = new Thread(new Runnable() {
                  @Override
                  public void run() {
                      running = true;
                      while (running) {
                          try {
                              while (in.available() > 0) {
                                  handlePacketReceived((byte) in.read());
                              }
                              Thread.sleep(10);
                          } catch (IOException | InterruptedException io) {
                              io.printStackTrace();
                          }
                      }
                      System.out.println("IRMicro: Thread end reached !");
                  }
              });

              readThread.start();
          }
          return true;
      }

      public void handlePacketReceived(byte data) {
    	  receivedMessage += (char) data;

          if ((char) data == '\n') {
              System.out.print("IRMicro: --->" + receivedMessage);
              if (receivedMessage.startsWith("T:")) {
            	  // check timings of known codes!
            	  if (recordNext == true) {
            		  recordNext=false;
                      System.out.println("IRMicro: now storing timing codes to " + recordCmdName +".csv");
            		  storeCsvTimings(recordCmdName,receivedMessage);             		  
            	  }
              }
              else if ((receivedMessage.startsWith("0x")) && (!receivedMessage.startsWith("0xFFFFFFFF"))) {
            	  // lets assume we have a hex code !
            	  opReceivedHex.sendData(ConversionUtils.stringToBytes(receivedMessage.replace("\n", "")));
              }
              receivedMessage = "";            	  
          }

      }

      public void sendToIRMicro(String text) {
          try {
              System.out.println("IRMicro: sending -->" + text);
              out.write(ConversionUtils.stringToBytes(text+"\n"));
          } catch (Exception e) {
              System.out.println("IRMicros: send failed!");
          }
      }

      private String loadCsvTimings(String csvTimingsFileName) { //throws FileNotFoundException, IOException {
 /*   	  try {
    		  ResourceRegistry.getInstance().getResource("data/processor.IRMicro/test.txt",RES_TYPE.DATA);
    	  }
    	  catch (Exception e) {e.printStackTrace();}
*/
    	  File fIn = null;
          BufferedReader br=null;
    	  String line="";

    	  try {
              fIn = new File(dataFilePath + csvTimingsFileName+".csv");
              if (!fIn.exists()) return(null);

              try { 
            	  br = new BufferedReader(new FileReader(fIn));
            	  
            	  while ((line = br.readLine()) != null) {
            	       return (line);  // just handling one line by now !
            	  }
              }
              finally {
            	  br.close();
              }
          } catch (Exception e) {
              AstericsErrorHandling.instance.getLogger()
                      .fine("Could not load timings from file " + dataFilePath + csvTimingsFileName);
          }
          return (null);
      }

      
      private void storeCsvTimings(String cmdName, String csvTimingsString) { //throws FileNotFoundException, IOException {
    /*	  try {
    	  ResourceRegistry.getInstance().storeResource("data content as string","data/processor.IRMicro/test.txt",RES_TYPE.DATA);
    	  }
    	  catch (Exception e) {e.printStackTrace();}
    	  }
*/

    	  BufferedWriter writer = null;
    	  try
    	  {
    		  File fOut;
    		  fOut = new File (dataFilePath + cmdName + ".csv");
    		  if (fOut.exists()) { 
    			  fOut.delete();
        		  fOut = new File (dataFilePath + cmdName + ".csv");
    		  }
   
    	      writer = new BufferedWriter( new FileWriter(fOut, false));
    	      writer.write(csvTimingsString);

    	  }
    	  catch ( Exception e)
    	  {
              AstericsErrorHandling.instance.getLogger()
              .fine("Could not write timings to file "+ dataFilePath + cmdName + ".csv" );
    	  }
    	  finally
    	  {
    	      try
    	      {
    	          if ( writer != null)
    	          writer.close( );
    	      }
    	      catch ( IOException e)
    	      {
    	    	  e.printStackTrace();
    	      }
    	  }
      }



}