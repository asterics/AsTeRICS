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
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */

package eu.asterics.component.actuator.serialSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
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
import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventRawPacket;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.cimcommunication.CIMPortController;


import java.util.*;
import java.util.logging.*;



/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class SerialSenderInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler

{
	private CIMPortController portController = null;
	private static final short HID_ACTUATOR_CIM_ID = 0x0101;
	private static final short HID_FEATURE_JOYSTICK_UPDATE = 0x20;

	// Usage of an output port e.g.:
	// opMyOutPort.sendData(ConversionUtils.intToBytes(10));
	int inSlot0, inSlot1, inSlot2, inSlot3, inSlot4, inSlot5, inSlot6, inSlot7,
			inSlot8, inSlot9, inSlot10, inSlot11, inSlot12, inSlot13, inSlot14,
			inSlot15;// Usage of an event trigger port e.g.:
						// etpMyEtPort.raiseEvent();
	byte[] b = new byte[16];
	int cnt = 0;

	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	// private static final int DATA_RATE = 600;
	int propBaudRate = 9600;
	boolean paused = false;
	boolean running = false;
	Thread t;
	String propCOMPort = "COM8";
	int propSlot0 = 0;
	int propSlot1 = 0;
	int propSlot2 = 0;
	int propSlot3 = 0;
	int propSlot4 = 0;
	int propSlot5 = 0;
	int propSlot6 = 0;
	int propSlot7 = 0;
	int propSlot8 = 0;
	int propSlot9 = 0;
	int propSlot10 = 0;
	int propSlot11 = 0;
	int propSlot12 = 0;
	int propSlot13 = 0;
	int propSlot14 = 0;
	int propSlot15 = 0;
	boolean propSlot0Active = false;
	boolean propSlot1Active = false;
	boolean propSlot2Active = false;
	boolean propSlot3Active = false;
	boolean propSlot4Active = false;
	boolean propSlot5Active = false;
	boolean propSlot6Active = false;
	boolean propSlot7Active = false;
	boolean propSlot8Active = false;
	boolean propSlot9Active = false;
	boolean propSlot10Active = false;
	boolean propSlot11Active = false;
	boolean propSlot12Active = false;
	boolean propSlot13Active = false;
	boolean propSlot14Active = false;
	boolean propSlot15Active = false;
	boolean propDebug = false;
	int propSlot0Delay = 0;
	int propSlot1Delay = 0;
	int propSlot2Delay = 0;
	int propSlot3Delay = 0;
	int propSlot4Delay = 0;
	int propSlot5Delay = 0;
	int propSlot6Delay = 0;
	int propSlot7Delay = 0;
	int propSlot8Delay = 0;
	int propSlot9Delay = 0;
	int propSlot10Delay = 0;
	int propSlot11Delay = 0;
	int propSlot12Delay = 0;
	int propSlot13Delay = 0;
	int propSlot14Delay = 0;
	int propSlot15Delay = 0;

	// declare member variables here

	/*
	 * OutputStream output; InputStream input; SerialPort serialPort;
	 * CommPortIdentifier portId = null;
	 */
	/**
	 * The class constructor.
	 */
	public SerialSenderInstance() {
		// empty constructor
	}

	/**
	 * returns an Input Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the input port or null if not found
	 */
	public IRuntimeInputPort getInputPort(String portID) {
		if ("slot0".equalsIgnoreCase(portID)) {
			return ipSlot0;
		}
		if ("slot1".equalsIgnoreCase(portID)) {
			return ipSlot1;
		}
		if ("slot2".equalsIgnoreCase(portID)) {
			return ipSlot2;
		}
		if ("slot3".equalsIgnoreCase(portID)) {
			return ipSlot3;
		}
		if ("slot4".equalsIgnoreCase(portID)) {
			return ipSlot4;
		}
		if ("slot5".equalsIgnoreCase(portID)) {
			return ipSlot5;
		}
		if ("slot6".equalsIgnoreCase(portID)) {
			return ipSlot6;
		}
		if ("slot7".equalsIgnoreCase(portID)) {
			return ipSlot7;
		}
		if ("slot8".equalsIgnoreCase(portID)) {
			return ipSlot8;
		}
		if ("slot9".equalsIgnoreCase(portID)) {
			return ipSlot9;
		}
		if ("slot10".equalsIgnoreCase(portID)) {
			return ipSlot10;
		}
		if ("slot11".equalsIgnoreCase(portID)) {
			return ipSlot11;
		}
		if ("slot12".equalsIgnoreCase(portID)) {
			return ipSlot12;
		}
		if ("slot13".equalsIgnoreCase(portID)) {
			return ipSlot13;
		}
		if ("slot14".equalsIgnoreCase(portID)) {
			return ipSlot14;
		}
		if ("slot15".equalsIgnoreCase(portID)) {
			return ipSlot15;
		}

		return null;
	}

	/**
	 * returns an Output Port.
	 * 
	 * @param portID
	 *            the name of the port
	 * @return the output port or null if not found
	 */
	public IRuntimeOutputPort getOutputPort(String portID) {

		return null;
	}

	/**
	 * returns an Event Listener Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventListener port or null if not found
	 */
	public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
		if ("send".equalsIgnoreCase(eventPortID)) {
			return elpSend;
		}

		return null;
	}

	/**
	 * returns an Event Triggerer Port.
	 * 
	 * @param eventPortID
	 *            the name of the port
	 * @return the EventTriggerer port or null if not found
	 */
	public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {

		return null;
	}

	/**
	 * returns the value of the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return the property value or null if not found
	 */
	public Object getRuntimePropertyValue(String propertyName) {
		if ("debug".equalsIgnoreCase(propertyName)) {
			return propDebug;
		}
		if ("cOMPort".equalsIgnoreCase(propertyName)) {
			return propCOMPort;
		}
		if ("baudRate".equalsIgnoreCase(propertyName)) {
			return propBaudRate;
		}
		if ("slot0".equalsIgnoreCase(propertyName)) {
			return propSlot0;
		}
		if ("slot1".equalsIgnoreCase(propertyName)) {
			return propSlot1;
		}
		if ("slot2".equalsIgnoreCase(propertyName)) {
			return propSlot2;
		}
		if ("slot3".equalsIgnoreCase(propertyName)) {
			return propSlot3;
		}
		if ("slot4".equalsIgnoreCase(propertyName)) {
			return propSlot4;
		}
		if ("slot5".equalsIgnoreCase(propertyName)) {
			return propSlot5;
		}
		if ("slot6".equalsIgnoreCase(propertyName)) {
			return propSlot6;
		}
		if ("slot7".equalsIgnoreCase(propertyName)) {
			return propSlot7;
		}
		if ("slot8".equalsIgnoreCase(propertyName)) {
			return propSlot8;
		}
		if ("slot9".equalsIgnoreCase(propertyName)) {
			return propSlot9;
		}
		if ("slot10".equalsIgnoreCase(propertyName)) {
			return propSlot10;
		}
		if ("slot11".equalsIgnoreCase(propertyName)) {
			return propSlot11;
		}
		if ("slot12".equalsIgnoreCase(propertyName)) {
			return propSlot12;
		}
		if ("slot13".equalsIgnoreCase(propertyName)) {
			return propSlot13;
		}
		if ("slot14".equalsIgnoreCase(propertyName)) {
			return propSlot14;
		}
		if ("slot15".equalsIgnoreCase(propertyName)) {
			return propSlot15;
		}
		if ("slot0Active".equalsIgnoreCase(propertyName)) {
			return propSlot0Active;
		}
		if ("slot1Active".equalsIgnoreCase(propertyName)) {
			return propSlot1Active;
		}
		if ("slot2Active".equalsIgnoreCase(propertyName)) {
			return propSlot2Active;
		}
		if ("slot3Active".equalsIgnoreCase(propertyName)) {
			return propSlot3Active;
		}
		if ("slot4Active".equalsIgnoreCase(propertyName)) {
			return propSlot4Active;
		}
		if ("slot5Active".equalsIgnoreCase(propertyName)) {
			return propSlot5Active;
		}
		if ("slot6Active".equalsIgnoreCase(propertyName)) {
			return propSlot6Active;
		}
		if ("slot7Active".equalsIgnoreCase(propertyName)) {
			return propSlot7Active;
		}
		if ("slot8Active".equalsIgnoreCase(propertyName)) {
			return propSlot8Active;
		}
		if ("slot9Active".equalsIgnoreCase(propertyName)) {
			return propSlot9Active;
		}
		if ("slot10Active".equalsIgnoreCase(propertyName)) {
			return propSlot10Active;
		}
		if ("slot11Active".equalsIgnoreCase(propertyName)) {
			return propSlot11Active;
		}
		if ("slot12Active".equalsIgnoreCase(propertyName)) {
			return propSlot12Active;
		}
		if ("slot13Active".equalsIgnoreCase(propertyName)) {
			return propSlot13Active;
		}
		if ("slot14Active".equalsIgnoreCase(propertyName)) {
			return propSlot14Active;
		}
		if ("slot15Active".equalsIgnoreCase(propertyName)) {
			return propSlot15Active;
		}
		if ("slot0Delay".equalsIgnoreCase(propertyName)) {
			return propSlot0Delay;
		}
		if ("slot1Delay".equalsIgnoreCase(propertyName)) {
			return propSlot1Delay;
		}
		if ("slot2Delay".equalsIgnoreCase(propertyName)) {
			return propSlot2Delay;
		}
		if ("slot3Delay".equalsIgnoreCase(propertyName)) {
			return propSlot3Delay;
		}
		if ("slot4Delay".equalsIgnoreCase(propertyName)) {
			return propSlot4Delay;
		}
		if ("slot5Delay".equalsIgnoreCase(propertyName)) {
			return propSlot5Delay;
		}
		if ("slot6Delay".equalsIgnoreCase(propertyName)) {
			return propSlot6Delay;
		}
		if ("slot7Delay".equalsIgnoreCase(propertyName)) {
			return propSlot7Delay;
		}
		if ("slot8Delay".equalsIgnoreCase(propertyName)) {
			return propSlot8Delay;
		}
		if ("slot9Delay".equalsIgnoreCase(propertyName)) {
			return propSlot9Delay;
		}
		if ("slot10Delay".equalsIgnoreCase(propertyName)) {
			return propSlot10Delay;
		}
		if ("slot11Delay".equalsIgnoreCase(propertyName)) {
			return propSlot11Delay;
		}
		if ("slot12Delay".equalsIgnoreCase(propertyName)) {
			return propSlot12Delay;
		}
		if ("slot13Delay".equalsIgnoreCase(propertyName)) {
			return propSlot13Delay;
		}
		if ("slot14Delay".equalsIgnoreCase(propertyName)) {
			return propSlot14Delay;
		}
		if ("slot15Delay".equalsIgnoreCase(propertyName)) {
			return propSlot15Delay;
		}

		return null;
	}

	/**
	 * sets a new value for the given property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param newValue
	 *            the desired property value or null if not found
	 */
	public Object setRuntimePropertyValue(String propertyName, Object newValue) {
		if (("debug").equalsIgnoreCase(propertyName)) {
			final Object oldValue = propDebug;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propDebug = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propDebug = false;
			}
			return oldValue;
		}
		if ("cOMPort".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propCOMPort;
			propCOMPort = (String) newValue;
			return oldValue;
		}
		if ("baudRate".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propBaudRate;
			propBaudRate = Integer.parseInt(newValue.toString());

			return oldValue;
		}
		if ("slot0".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot0;
			propSlot0 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot1".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot1;
			propSlot1 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot2".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot2;
			propSlot2 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot3".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot3;
			propSlot3 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot4".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot4;
			propSlot4 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot5".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot5;
			propSlot5 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot6".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot6;
			propSlot6 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot7".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot7;
			propSlot7 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot8".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot8;
			propSlot8 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot9".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot9;
			propSlot9 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot10".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot10;
			propSlot10 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot11".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot11;
			propSlot11 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot12".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot12;
			propSlot12 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot13".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot13;
			propSlot13 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot14".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot14;
			propSlot14 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot15".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot15;
			propSlot15 = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot0Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot0Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot0Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot0Active = false;
			}
			return oldValue;
		}
		if ("slot1Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot1Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot1Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot1Active = false;
			}
			return oldValue;
		}
		if ("slot2Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot2Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot2Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot2Active = false;
			}
			return oldValue;
		}
		if ("slot3Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot3Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot3Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot3Active = false;
			}
			return oldValue;
		}
		if ("slot4Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot4Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot4Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot4Active = false;
			}
			return oldValue;
		}
		if ("slot5Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot5Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot5Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot5Active = false;
			}
			return oldValue;
		}
		if ("slot6Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot6Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot6Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot6Active = false;
			}
			return oldValue;
		}
		if ("slot7Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot7Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot7Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot7Active = false;
			}
			return oldValue;
		}
		if ("slot8Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot8Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot8Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot8Active = false;
			}
			return oldValue;
		}
		if ("slot9Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot9Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot9Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot9Active = false;
			}
			return oldValue;
		}
		if ("slot10Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot10Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot10Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot10Active = false;
			}
			return oldValue;
		}
		if ("slot11Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot11Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot11Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot11Active = false;
			}
			return oldValue;
		}
		if ("slot12Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot12Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot12Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot12Active = false;
			}
			return oldValue;
		}
		if ("slot13Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot13Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot13Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot13Active = false;
			}
			return oldValue;
		}
		if ("slot14Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot14Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot14Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot14Active = false;
			}
			return oldValue;
		}
		if ("slot15Active".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot15Active;
			if ("true".equalsIgnoreCase((String) newValue)) {
				propSlot15Active = true;
			} else if ("false".equalsIgnoreCase((String) newValue)) {
				propSlot15Active = false;
			}
			return oldValue;
		}
		if ("slot0Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot0Delay;
			propSlot0Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot1Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot1Delay;
			propSlot1Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot2Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot2Delay;
			propSlot2Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot3Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot3Delay;
			propSlot3Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot4Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot4Delay;
			propSlot4Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot5Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot5Delay;
			propSlot5Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot6Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot6Delay;
			propSlot6Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot7Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot7Delay;
			propSlot7Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot8Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot8Delay;
			propSlot8Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot9Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot9Delay;
			propSlot9Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot10Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot10Delay;
			propSlot10Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot11Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot11Delay;
			propSlot11Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot12Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot12Delay;
			propSlot12Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot13Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot13Delay;
			propSlot13Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot14Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot14Delay;
			propSlot14Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("slot15Delay".equalsIgnoreCase(propertyName)) {
			final Object oldValue = propSlot15Delay;
			propSlot15Delay = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		return null;
	}

	/**
	 * Input Ports for receiving values.
	 */
	private final IRuntimeInputPort ipSlot0 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot0 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot1 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot1 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot2 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot2 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot3 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot3 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot4 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot4 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot5 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot5 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot6 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot6 =(int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot7 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot7 =(int) ConversionUtils.doubleFromBytes(data);
			System.out.println("Slot7 changed: " + inSlot7);
		}
	};
	private final IRuntimeInputPort ipSlot8 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot8 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot9 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot9 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot10 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot10 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot11 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot11 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot12 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot12 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot13 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot13 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot14 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot14 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};
	private final IRuntimeInputPort ipSlot15 = new DefaultRuntimeInputPort() {
		public void receiveData(byte[] data) {
			inSlot15 = (int) ConversionUtils.doubleFromBytes(data);
		}
	};

	synchronized private final void sendSlot(byte b) {
		byte[] data = { b };
		CIMPortManager.getInstance().sendPacket(portController, data,
				HID_FEATURE_JOYSTICK_UPDATE,
				CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
	}

	/**
	 * Event Listerner Ports.
	 */
	final IRuntimeEventListenerPort elpSend = new IRuntimeEventListenerPort() {
		public void receiveEvent(final String data) {

			if ((running) && (paused == false))  {
				try {
					// Send Slot 0
					if (propSlot0Active == true) {
						inSlot0 = ((int) inSlot0 & 255);
						Thread.sleep(propSlot0Delay);
						sendSlot((byte) (inSlot0 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 0: " + (inSlot0 & 0xff));
					}

					// Send Slot 1
					if (propSlot1Active == true) {
						inSlot1 = ((int) inSlot1 & 255);
						Thread.sleep(propSlot1Delay);
						sendSlot((byte) (inSlot1 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 1: " + (inSlot1 & 0xff));
					}

					// Send Slot 2
					if (propSlot2Active == true) {
						inSlot2 = (int) (((int) inSlot2) & 255);
						Thread.sleep(propSlot2Delay);
						sendSlot((byte) (inSlot2 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 2: " + (inSlot2 & 0xff));
					}

					// Send Slot 3
					if (propSlot3Active == true) {
						inSlot3 = ((int) inSlot3 & 255);
						Thread.sleep(propSlot3Delay);
						sendSlot((byte) (inSlot3 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 3:  " + (inSlot3 & 0xff));
					}

					// Send Slot 4
					if (propSlot4Active == true) {
						inSlot4 = ((int) inSlot4 & 255);
						Thread.sleep(propSlot4Delay);
						sendSlot((byte) (inSlot4 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 4: " + (inSlot4 & 0xff));
					}

					// Send Slot 5
					if (propSlot5Active == true) {
						inSlot5 = ((int) inSlot5 & 255);
						Thread.sleep(propSlot5Delay);
						sendSlot((byte) (inSlot5 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 5: " + (inSlot5 & 0xff));
					}

					// Send Slot 6
					if (propSlot6Active == true) {
						inSlot6 = ((int) inSlot6 & 255);
						Thread.sleep(propSlot6Delay);
						sendSlot((byte) (inSlot6 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 6: " + (inSlot6 & 0xff));
					}
					// Send Slot 7
					if (propSlot7Active == true) {
						inSlot7 = ((int) inSlot7 & 255);
						Thread.sleep(propSlot7Delay);
						sendSlot((byte) (inSlot7 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 7: " + (inSlot7 & 0xff));
					}

					// Send Slot 8
					if (propSlot8Active == true) {
						inSlot8 = ((int) inSlot8 & 255);
						Thread.sleep(propSlot8Delay);
						sendSlot((byte) (inSlot8 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 8: " + (inSlot8 & 0xff));
					}

					// Send Slot 9
					if (propSlot9Active == true) {
						inSlot9 = ((int) inSlot9 & 255);
						Thread.sleep(propSlot9Delay);
						sendSlot((byte) (inSlot9 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 9: " + (inSlot9 & 0xff));
					}

					// Send Slot 10
					if (propSlot10Active == true) {
						inSlot10 = ((int) inSlot10 & 255);
						Thread.sleep(propSlot10Delay);
						sendSlot((byte) (inSlot10 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 10: " + (inSlot10 & 0xff));
					}

					// Send Slot 11
					if (propSlot11Active == true) {
						inSlot11 = ((int) inSlot11 & 255);
						Thread.sleep(propSlot11Delay);
						sendSlot((byte) (inSlot11 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 11: " + (inSlot11 & 0xff));
					}

					// Send Slot 12
					if (propSlot12Active == true) {
						inSlot12 = ((int) inSlot12 & 255);
						Thread.sleep(propSlot12Delay);
						sendSlot((byte) (inSlot12 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 12: " + (inSlot12 & 0xff));
					}

					// Send Slot 13
					if (propSlot13Active == true) {
						inSlot13 = ((int) inSlot13 & 255);
						Thread.sleep(propSlot13Delay);
						sendSlot((byte) (inSlot13 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 13: " + (inSlot13 & 0xff));
					}

					// Send Slot 14
					if (propSlot14Active == true) {
						inSlot14 = ((int) inSlot14 & 255);
						Thread.sleep(propSlot14Delay);
						sendSlot((byte) (inSlot14 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 14: " + (inSlot14 & 0xff));
					}

					// Send Slot 15
					if (propSlot15Active == true) {
						inSlot15 = ((int) inSlot15 & 255);
						Thread.sleep(propSlot15Delay);
						sendSlot((byte) (inSlot15 & 0xff));
						if (propDebug)
							System.out.println("Sending Slot 15: " + (inSlot15 & 0xff));
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * called when model is started.
	 */
	@Override
	public void start() {
		super.start();
		inSlot0 = propSlot0;
		inSlot1 = propSlot1;
		inSlot2 = propSlot2;
		inSlot3 = propSlot3;
		inSlot4 = propSlot4;
		inSlot5 = propSlot5;
		inSlot6 = propSlot6;
		inSlot7 = propSlot7;
		inSlot8 = propSlot8;
		inSlot9 = propSlot9;
		inSlot10 = propSlot10;
		inSlot11 = propSlot11;
		inSlot12 = propSlot12;
		inSlot13 = propSlot13;
		inSlot14 = propSlot14;
		inSlot15 = propSlot15;

		portController = CIMPortManager.getInstance().getRawConnection(
				propCOMPort, propBaudRate);
		if (portController == null) {
			AstericsErrorHandling.instance.reportError(this,"Could not construct raw port controller");
		} else {
			if (propDebug) {
				portController.addEventListener(this);
			}
			running = true;
		}
	}

	/**
	 * called when model is paused.
	 */
	@Override
	public void pause() {

		super.pause();
		if (portController != null) {

			CIMPortManager.getInstance().closeRawConnection(propCOMPort);
			portController = null;
			AstericsErrorHandling.instance.reportInfo(this,
					"Raw port controller closed");
		}
	};

	/**
	 * called when model is resumed.
	 */
	@Override
	public void resume() {
		super.resume();
		portController = CIMPortManager.getInstance().getRawConnection(
				propCOMPort, propBaudRate);
		if (portController == null) {
			AstericsErrorHandling.instance.reportError(this,
					"Could not construct raw port controller");
		} else {
			if (propDebug) {
				portController.addEventListener(this);
			}
		}
		
	};

	/**
	 * called when model is stopped.
	 */
	@Override
	public void stop() {
		super.stop();
		
		if (portController != null) {

			CIMPortManager.getInstance().closeRawConnection(propCOMPort);
			portController = null;
			AstericsErrorHandling.instance.reportInfo(this,
					"RazorImu connection closed");
		}
		inSlot0 = propSlot0;
		inSlot1 = propSlot1;
		inSlot2 = propSlot2;
		inSlot3 = propSlot3;
		inSlot4 = propSlot4;
		inSlot5 = propSlot5;
		inSlot6 = propSlot6;
		inSlot7 = propSlot7;
		inSlot8 = propSlot8;
		inSlot9 = propSlot9;
		inSlot10 = propSlot10;
		inSlot11 = propSlot11;
		inSlot12 = propSlot12;
		inSlot13 = propSlot13;
		inSlot14 = propSlot14;
		inSlot15 = propSlot15;
		running = false;
	}

	@Override
	public void handlePacketReceived(CIMEvent e) {
		CIMEventRawPacket rp = (CIMEventRawPacket ) e;
		System.out.println((rp.b & 0x000000ff));
	}

	@Override
	public void handlePacketError(CIMEvent e) {
		
	}
}