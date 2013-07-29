

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

package eu.asterics.component.processor.fs20commandinterpreter;


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
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * This processor receive a FS20 input string, containing housecode, sender address
 * and FS20 command. If the housecode and the sender address fits to the setted properties, an 
 * event regarding to the command will be fired. This functionality is also included in the FS20Receiver
 * plugin, but because of the fact, that the FS20Receiver must be a singelton, this plugin is needed to work with
 * several FS20 inputs.
 *  
 *  
 * @author Roland Ossmann [ro@ki-i.at]
 *         Date: 16.05.2012
 *         Time: 10:06
 */
public class FS20CommandInterpreterInstance extends AbstractRuntimeComponentInstance
{
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	final IRuntimeEventTriggererPort etpOff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel1 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel2 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel3 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel4 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel5 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel6 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel7 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel8 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel9 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel10 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel11 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel12 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel13 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel14 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel15 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnLevel16 = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnOldLevel = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpToggle = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDimUp = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDimDown = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpDimUpAndDown = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpProgramTimer = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOffForTimerThenOldLevel = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnForTimerThenOff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnOldLevelForTimerThenOff = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpReset = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnForTimerThenOldLevel = new DefaultRuntimeEventTriggererPort();
	final IRuntimeEventTriggererPort etpOnOldLevelForTimerThenPreviousState = new DefaultRuntimeEventTriggererPort();
	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propHousecode = 11111111;
	int propSendaddress = 1111;

	// declare member variables here

  
    
   /**
    * The class constructor.
    */
    public FS20CommandInterpreterInstance()
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
		if ("command".equalsIgnoreCase(portID))
		{
			return ipCommand;
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
		if ("off".equalsIgnoreCase(eventPortID))
		{
			return etpOff;
		}
		if ("onLevel1".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel1;
		}
		if ("onLevel2".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel2;
		}
		if ("onLevel3".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel3;
		}
		if ("onLevel4".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel4;
		}
		if ("onLevel5".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel5;
		}
		if ("onLevel6".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel6;
		}
		if ("onLevel7".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel7;
		}
		if ("onLevel8".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel8;
		}
		if ("onLevel9".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel9;
		}
		if ("onLevel10".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel10;
		}
		if ("onLevel11".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel11;
		}
		if ("onLevel12".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel12;
		}
		if ("onLevel13".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel13;
		}
		if ("onLevel14".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel14;
		}
		if ("onLevel15".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel15;
		}
		if ("onLevel16".equalsIgnoreCase(eventPortID))
		{
			return etpOnLevel16;
		}
		if ("onOldLevel".equalsIgnoreCase(eventPortID))
		{
			return etpOnOldLevel;
		}
		if ("toggle".equalsIgnoreCase(eventPortID))
		{
			return etpToggle;
		}
		if ("dimUp".equalsIgnoreCase(eventPortID))
		{
			return etpDimUp;
		}
		if ("dimDown".equalsIgnoreCase(eventPortID))
		{
			return etpDimDown;
		}
		if ("dimUpAndDown".equalsIgnoreCase(eventPortID))
		{
			return etpDimUpAndDown;
		}
		if ("programTimer".equalsIgnoreCase(eventPortID))
		{
			return etpProgramTimer;
		}
		if ("offForTimerThenOldLevel".equalsIgnoreCase(eventPortID))
		{
			return etpOffForTimerThenOldLevel;
		}
		if ("onForTimerThenOff".equalsIgnoreCase(eventPortID))
		{
			return etpOnForTimerThenOff;
		}
		if ("onOldLevelForTimerThenOff".equalsIgnoreCase(eventPortID))
		{
			return etpOnOldLevelForTimerThenOff;
		}
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return etpReset;
		}
		if ("onForTimerThenOldLevel".equalsIgnoreCase(eventPortID))
		{
			return etpOnForTimerThenOldLevel;
		}
		if ("onOldLevelForTimerThenPreviousState".equalsIgnoreCase(eventPortID))
		{
			return etpOnOldLevelForTimerThenPreviousState;
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
		if ("housecode".equalsIgnoreCase(propertyName))
		{
			return propHousecode;
		}
		if ("sendaddress".equalsIgnoreCase(propertyName))
		{
			return propSendaddress;
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
		if ("housecode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propHousecode;
			propHousecode = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("sendaddress".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propSendaddress;
			propSendaddress = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipCommand  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{

			String receivedCommand = ConversionUtils.stringFromBytes(data); 
			String[] values = receivedCommand.split("_");
			if (values.length != 3) {
				AstericsErrorHandling.instance.reportError(new FS20CommandInterpreterInstance(), "Parameter mismatch for action string "+receivedCommand+"! Format is: hc_addr_cmd! example: 11111111_1111_18 for toggle");		
				return;
			}
			try {
				int recHousecode = Integer.parseInt(values[0]);
				int recSendaddress = Integer.parseInt(values[1]);
				int recCommand = Integer.parseInt(values[2]);
				if (recHousecode == propHousecode && recSendaddress == propSendaddress) {
					findEvent(recCommand);
				}
			} catch (NumberFormatException ne) {
				AstericsErrorHandling.instance.reportError(new FS20CommandInterpreterInstance(), "Data mismatch in action string "+receivedCommand+"! Numbers could not be converted. Format is: hc_addr_cmd! example: 11111111_1111_18 for toggle");
			}
		}
	};


	/**
	 * Find and fire the event regarding to the command
	 * @param The FS20 command
	 */
    private void findEvent(int command) {
    	
		switch (command) {
			case 0: etpOff.raiseEvent(); 
			break;
			case 1: etpOnLevel1.raiseEvent();
			break;
			case 2: etpOnLevel2.raiseEvent();    					
			break;
			case 3: etpOnLevel3.raiseEvent();    					
			break;
			case 4: etpOnLevel4.raiseEvent();    					
			break;
			case 5: etpOnLevel5.raiseEvent();    					
			break;
			case 6: etpOnLevel6.raiseEvent();    					
			break;
			case 7: etpOnLevel7.raiseEvent();    					
			break;
			case 8: etpOnLevel8.raiseEvent();    					
			break;
			case 9: etpOnLevel9.raiseEvent();    					
			break;
			case 10: etpOnLevel10.raiseEvent();    					
			break;
			case 11: etpOnLevel11.raiseEvent();    					
			break;
			case 12: etpOnLevel12.raiseEvent();    					
			break;
			case 13: etpOnLevel13.raiseEvent();    					
			break;
			case 14: etpOnLevel14.raiseEvent();    					
			break;
			case 15: etpOnLevel15.raiseEvent();    					
			break;
			case 16: etpOnLevel16.raiseEvent();    					
			break;
			case 17: etpOnOldLevel.raiseEvent();  
			break;
			case 18: etpToggle.raiseEvent();    					
			break;
			case 19: etpDimUp.raiseEvent();    					
			break;
			case 20: etpDimDown.raiseEvent();    					
			break;
			case 21: etpDimUpAndDown.raiseEvent();    					
			break;
			case 22: etpProgramTimer.raiseEvent();    					
			break;
			case 24: etpOffForTimerThenOldLevel.raiseEvent();    					
			break;
			case 25: etpOnForTimerThenOff.raiseEvent();    					
			break;
			case 26: etpOnOldLevelForTimerThenOff.raiseEvent();    					
			break;
			case 27: etpReset.raiseEvent();    					
			break;
			case 30: etpOnForTimerThenOldLevel.raiseEvent();    					
			break;
			case 31: etpOnOldLevelForTimerThenPreviousState.raiseEvent();    					
			break;    			
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