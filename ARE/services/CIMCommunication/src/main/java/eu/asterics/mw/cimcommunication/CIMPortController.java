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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.cimcommunication;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

public abstract class CIMPortController {

	List<CIMEventHandler> eventHandlers = new LinkedList<CIMEventHandler>();
	protected Logger logger = null;
	String comPortName;
	CIMUniqueIdentifier cuid = null;
	
	final short areVersion = 0x1; //TODO replace with utility function
	
	byte serialNumber = 0;
	byte nextExpectedIncomingSerialNumber = 0;
	byte nextExpectedCIMIssuedSerialNumber = 0;
	

	public CIMPortController(String comPortName)
	{
		logger = AstericsErrorHandling.instance.getLogger();
		this.comPortName = comPortName;
	}
	
	/**
	 * Adds a CIM event handler to the list of event handlers
	 * @param handler the implementation of the event handler interface
	 */
	public void addEventListener(CIMEventHandler handler)
	{
		synchronized (eventHandlers)
		{
			if (!eventHandlers.contains(handler))
			{
				logger.fine(this.getClass().getName()+".addEventListener: adding listener:" + handler + "on port " + comPortName);
				eventHandlers.add(handler);
			}
		}
	}
	
	/**
	 * Removes a specified CIM event handler from the list of handlers 
	 * @param handler the handler implementation to be removed
	 * 
	 */
	public void removeEventListener(CIMEventHandler handler)
	{
		synchronized (eventHandlers)
		{
			if (eventHandlers.contains(handler))
			{
				logger.fine(this.getClass().getName()+
						".removeEventListener: removing listener:" + handler + 
						"on port " + comPortName);
				eventHandlers.remove(handler);
			}
		}
	}
	
	public InputStream getInputStream()
	{
		return null;
	}

	public OutputStream getOutputStream()
	{
		return null;
	}

	/**
	 * Closes the port. Tells the thread to run out and returns only after the
	 * thread has ended.
	 */
	abstract void closePort();

	/**
	 * Sends a packet to the connected device.
	 * @param data a byte array of data to be transferred 
	 * @param featureAddress the feature address to send the data to
	 * @param requestCode the request code for the transfer
	 * @param crc true if crc should be attached to packet
	 * @return the serial number of the packet or -1 on error
	 */
	abstract byte sendPacket(byte[] data, short featureAddress,
			short requestCode, boolean crc);

}