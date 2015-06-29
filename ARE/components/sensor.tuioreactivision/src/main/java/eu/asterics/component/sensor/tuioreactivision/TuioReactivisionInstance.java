

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

package eu.asterics.component.sensor.tuioreactivision;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
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
import TUIO.*;
/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author <your name> [<your email address>]
 *         Date: 
 *         Time: 
 */
public class TuioReactivisionInstance extends AbstractRuntimeComponentInstance implements TuioListener
{
	final IRuntimeOutputPort opMarkerID = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opSessionID = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opXpos = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opYpos = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opAngle = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMotionSpeed = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opRotationSpeed = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opMotionAccel = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opRotationAccel = new DefaultRuntimeOutputPort();
	final IRuntimeOutputPort opText = new DefaultRuntimeOutputPort();
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpEvent1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpEvent6 = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	boolean propTextOutput = false;
	boolean propMarkerAllocation = false;
	String propReactivisionPath = "";
	String propTextFile = "";


	// declare member variables here
	double _xpos;
	double _ypos;
	double _angle;
	int _markerID;
	double _sessionID;
	double _motionSpeed;
	double _motionAccel;
	double _rotationSpeed;
	double _rotationAccel;
	String _text;
	HashMap<Integer, String> dictString = new HashMap<>();
	HashMap<Integer,Integer> dictInteger = new HashMap<>();
	int _case;
	 Process p;
	 TuioClient client;
    
   /**
    * The class constructor.
    */
    public TuioReactivisionInstance()
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
		if ("markerID".equalsIgnoreCase(portID))
		{
			return opMarkerID;
		}
		if ("sessionID".equalsIgnoreCase(portID))
		{
			return opSessionID;
		}
		if ("xpos".equalsIgnoreCase(portID))
		{
			return opXpos;
		}
		if ("ypos".equalsIgnoreCase(portID))
		{
			return opYpos;
		}
		if ("angle".equalsIgnoreCase(portID))
		{
			return opAngle;
		}
		if ("motionSpeed".equalsIgnoreCase(portID))
		{
			return opMotionSpeed;
		}
		if ("rotationSpeed".equalsIgnoreCase(portID))
		{
			return opRotationSpeed;
		}
		if ("motionAccel".equalsIgnoreCase(portID))
		{
			return opMotionAccel;
		}
		if ("rotationAccel".equalsIgnoreCase(portID))
		{
			return opRotationAccel;
		}
		if ("text".equalsIgnoreCase(portID))
		{
			return opText;
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
		if ("event1".equalsIgnoreCase(eventPortID))
		{
			return etpEvent1;
		}
		if ("event2".equalsIgnoreCase(eventPortID))
		{
			return etpEvent2;
		}
		if ("event3".equalsIgnoreCase(eventPortID))
		{
			return etpEvent3;
		}
		if ("event4".equalsIgnoreCase(eventPortID))
		{
			return etpEvent4;
		}
		if ("event5".equalsIgnoreCase(eventPortID))
		{
			return etpEvent5;
		}
		if ("event6".equalsIgnoreCase(eventPortID))
		{
			return etpEvent6;
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
		if ("textOutput".equalsIgnoreCase(propertyName))
		{
			return propTextOutput;
		}
		if ("markerAllocation".equalsIgnoreCase(propertyName))
		{
			return propMarkerAllocation;
		}
		if ("reactivisionPath".equalsIgnoreCase(propertyName))
		{
			return propReactivisionPath;
		}
		if ("textFile".equalsIgnoreCase(propertyName))
		{
			return propTextFile;
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
		if ("textOutput".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTextOutput;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propTextOutput = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propTextOutput = false;
			}
			return oldValue;
		}
		if ("markerAllocation".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propMarkerAllocation;
			if("true".equalsIgnoreCase((String)newValue))
			{
				propMarkerAllocation = true;
			}
			else if("false".equalsIgnoreCase((String)newValue))
			{
				propMarkerAllocation = false;
			}
			return oldValue;
		}
		if ("reactivisionPath".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propReactivisionPath;
			propReactivisionPath = (String)newValue;
			return oldValue;
		}
		if ("textFile".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propTextFile;
			propTextFile = (String)newValue;
			return oldValue;
		}

        return null;
    }

     

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  try{
    		  //starts reactivision.exe
    		  if(propReactivisionPath!=null && !"".equals(propReactivisionPath)){
    			  this.startReact();    		 
    		  }

    		  int port = 3333;

    		  //starts Tuio Client
    		  client = new TuioClient(port);


    		  client.addTuioListener(this);
    		  client.connect();

    		  if(propTextOutput){
    			  propMarkerAllocation = false;
    			  this.readText();

    		  }
    		  else if (propMarkerAllocation){
    			  propTextOutput = false;
    			  this.readInt();
    		  }

    		  super.start();
    	  }catch(Exception e) {
    		  throw new RuntimeException(e);
    	  }          
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
    	  //p.destroy();
    	  client.disconnect();
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
    	  
    	//starts reactivision.exe
/*     	 if(propReactivisionPath!=null){
     		 this.startReact();
     		 
     	 }
     	  
     	  int port = 3333;

     	  	//starts Tuio Client
     		TuioClient client = new TuioClient(port);

     		
     		client.addTuioListener(this);
     		*/
     		client.connect();
   		
          super.resume();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
    	  if(client!=null) {client.disconnect();}
    	  if(p!=null) {p.destroy();}
          super.stop();
      }

	@Override
	public void addTuioBlob(TuioBlob arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTuioObject(TuioObject obj) {
		// TODO Auto-generated method stub
				_markerID = obj.getSymbolID();
				_sessionID = obj.getSessionID();
				_xpos = obj.getX();
				_ypos = obj.getY();
				_angle = obj.getAngle();
				
				
				opMarkerID.sendData(ConversionUtils.intToBytes(_markerID));
				opSessionID.sendData(ConversionUtils.doubleToBytes(_sessionID));
				opXpos.sendData(ConversionUtils.doubleToBytes(_xpos));
				opYpos.sendData(ConversionUtils.doubleToBytes(_ypos));
				opAngle.sendData(ConversionUtils.doubleToBytes(_angle));
				
				if(propTextOutput){
					_text = dictString.get(_markerID);
					if (_text != null) {
						opText.sendData(ConversionUtils.stringToBytes(_text));
					} else {
						opText.sendData(ConversionUtils.stringToBytes(" "));
					}					
				}
				
				
				if(propMarkerAllocation == true){
					if(dictInteger.get(_markerID)!=null){
					_case = dictInteger.get(_markerID);
					switch(_case){
					case 1:
						etpEvent1.raiseEvent();
						break;
					case 2:
						etpEvent2.raiseEvent();
						break;
					case 3:
						etpEvent3.raiseEvent();
						break;
					case 4:
						etpEvent4.raiseEvent();
						break;
					case 5:
						etpEvent5.raiseEvent();
						break;
					case 6:
						etpEvent6.raiseEvent();
						break;
					default:
						break;
					}
				}
				}
	}			

	@Override
	public void refresh(TuioTime arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTuioBlob(TuioBlob arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTuioBlob(TuioBlob arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTuioObject(TuioObject obj) {
		// TODO Auto-generated method stub
		_sessionID = obj.getSessionID();
		_xpos = obj.getX();
		_ypos = obj.getY();
		_angle = obj.getAngle();
		_motionSpeed = obj.getMotionSpeed();
		_motionAccel = obj.getMotionAccel();
		_rotationSpeed = obj.getRotationSpeed();
		_rotationAccel = obj.getRotationAccel();
		
		
		
		opSessionID.sendData(ConversionUtils.doubleToBytes(_sessionID));
		opXpos.sendData(ConversionUtils.doubleToBytes(_xpos));
		opYpos.sendData(ConversionUtils.doubleToBytes(_ypos));
		opAngle.sendData(ConversionUtils.doubleToBytes(_angle));
		opMotionSpeed.sendData(ConversionUtils.doubleToBytes(_motionSpeed));
		opMotionAccel.sendData(ConversionUtils.doubleToBytes(_motionAccel));
		opRotationSpeed.sendData(ConversionUtils.doubleToBytes(_rotationSpeed));
		opAngle.sendData(ConversionUtils.doubleToBytes(_rotationAccel));
//		
//		if(propTextOutput){
//			_text = dictString.get(_markerID);
//			opText.sendData(ConversionUtils.stringToBytes(_text));
//			}
//		
//		
//		if(propMarkerAllocation == true){
//			_case = dictInteger.get(_markerID);
//			switch(_case){
//			case 1:
//				etpEvent1.raiseEvent();
//				break;
//			case 2:
//				etpEvent2.raiseEvent();
//				break;
//			case 3:
//				etpEvent3.raiseEvent();
//				break;
//			case 4:
//				etpEvent4.raiseEvent();
//				break;
//			case 5:
//				etpEvent5.raiseEvent();
//				break;
//			case 6:
//				etpEvent6.raiseEvent();
//				break;
//			default:
//				break;
//			}
//		}
		
		
	}
	
	
	//reads text file including objects
	public void readText() throws FileNotFoundException {
		Scanner x = null;
		List<String> text = new ArrayList<String>();
		
		try{
			x = new Scanner(new File(propTextFile));
		}
		catch(FileNotFoundException e){
			//System.out.println("Could not find file");
			AstericsErrorHandling.instance.reportError(this, "Cannot read file <"+propTextFile+">\nPlease enter a meaningful file path into the 'TextFile' property");
			throw e;
		}
		
		while(x.hasNext()){
			text.add(x.next());
			
		}
		
		x.close();
		
		for(int j = 0; j < text.size(); j++){
			
			String string = text.get(j);
			String[] parts = string.split("-");
			Integer part1 = Integer.parseInt(parts[0]); // 004
			String part2 = parts[1];
			dictString.put(part1, part2);
			
			
		}
		
	}
	
	// reads file with marker information
	public void readInt() throws FileNotFoundException {
		Scanner x = null;
		List<String> textInt = new ArrayList<String>();
		try{
			x = new Scanner(new File(propTextFile));
		}
		catch(FileNotFoundException e){
			//System.out.println("Could not find file");
			AstericsErrorHandling.instance.reportError(this, "Cannot read file <"+propTextFile+">\nPlease enter a meaningful file path into the 'TextFile' property");
			throw e;
		}
		
		while(x.hasNext()){
			textInt.add(x.next());
			
		}
		
		x.close();
		
		for(int j = 0; j < textInt.size(); j++){ // six event trigger
			
			String string = textInt.get(j);
			String[] parts = string.split("-");
			Integer part1 = Integer.parseInt(parts[1]); // 004
			Integer part2 = Integer.parseInt(parts[0]);
			dictInteger.put(part1, part2);
			
			
		}
		
	}
	
	public void startReact(){
		try {
			List<String> command=new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(propReactivisionPath);
			while (st.hasMoreTokens()) {
				String act=st.nextToken();
				command.add(act);
	    		System.out.println("adding argument :" + act);
			}

		    
		    ProcessBuilder builder = new ProcessBuilder(command);
		    
		    Map<String, String> env = builder.environment();
		    String propWorkingDirectory=new File(propReactivisionPath).getParent();
		    builder.directory(new File(propWorkingDirectory));
		    
		    p = builder.start();    		    

            //p = Runtime.getRuntime().exec(propReactivisionPath);
            Thread.sleep(5000);
         } catch (IOException e) {
             e.printStackTrace();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
	}
}