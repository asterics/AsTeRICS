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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.mw.cimcommunication;

import eu.asterics.mw.services.AstericsErrorHandling;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;


/**
 * Implementation of RXTX listener interface to transfer data from the serial
 * port to the CIM communication implementation. Reads bytes from serial port
 * and writes them to a blocking queue to allow serial port controller to read
 * the data.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
class CIMPortEventListener implements SerialPortEventListener {
	
	BlockingQueue<Byte> dataSink;
	InputStream in;

	/**
	 * Constructs the listener
	 * @param controller the controller that data should be sent to
	 * @param in the input stream from the RXTX serial port 
	 * @param dataSink the blocking queue the serial port controller reads from
	 */
	public CIMPortEventListener(InputStream in, BlockingQueue<Byte> dataSink) 
	{
		this.in = in;
		this.dataSink = dataSink;
	}

	/**
	 * Implementation of the handling of events on the serial port. Only handles
	 * DATA_AVAILABLE and puts the received bytes into the queue.
	 */
	@Override
	public void serialEvent(SerialPortEvent ev) {
		
		int data;		
		
		switch (ev.getEventType())
		{
		case SerialPortEvent.DATA_AVAILABLE:
            try
            {
                while ( ( data = in.read()) > -1 )
                {
//
//                	System.out.println(String.format("Recv: 0x%2x ('%c')", data, data));
//
                   	dataSink.add((byte) data);
                 }
            }
            catch ( IOException e )
            {
            	AstericsErrorHandling.instance.getLogger()
            		.warning("Exception on serial monitor thread");
                e.printStackTrace();
            }
			
			break;
		}
	}

}
