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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.cimcommunication;

import eu.asterics.mw.services.AstericsErrorHandling;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Implementation of RXTX listener interface to transfer data from the serial
 * port to the CIM communication implementation. Reads bytes from serial port
 * and writes them to a blocking queue to allow serial port controller to read
 * the data.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
class CIMPortEventListener implements SerialPortEventListener {

    private final static int MAX_CAPACITY = 4096;
    BlockingQueue<Byte> cachingQueue;
    InputStream in;
    private boolean hadI0Exception = false;

    /**
     * Constructs the listener
     *
     * @param in
     *            the input stream from the RXTX serial port
     */
    public CIMPortEventListener(InputStream in) {
        this.in = in;
        this.cachingQueue = new LinkedBlockingQueue<>(MAX_CAPACITY);
    }

    /**
     * gets the input stream
     * @return
     */
    public InputStream getInputStream() {
        return this.in;
    }

    /**
     * polls the data sink up to a given maximum time
     *
     * @param timeout the amount of time in unit given by param unit
     * @param unit the time unit of the timeout
     * @return the next byte
     * @throws InterruptedException
     * @throws IOException if IOException occured on the last reading on the class member input stream
     */
    public Byte poll(long timeout, TimeUnit unit) throws InterruptedException, IOException {
        if(cachingQueue.isEmpty() && hadI0Exception) {
            hadI0Exception = false;
            throw new IOException("IOException on input stream of CIMPortEventListener");
        }
        return cachingQueue.poll(timeout, unit);
    }

    /**
     * Implementation of the handling of events on the serial port. Only handles
     * DATA_AVAILABLE and puts the received bytes into the queue.
     */
    @Override
    public void serialEvent(SerialPortEvent ev) {
        int data;
        switch (ev.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    while (!hadI0Exception && (data = in.read()) > -1) {
                        cachingQueue.add((byte) data);
                    }
                } catch (IOException e) {
                    AstericsErrorHandling.instance.getLogger().log(Level.WARNING, "Exception on serial monitor thread", e);
                    hadI0Exception = true;
                } catch(IllegalStateException e) {
                    AstericsErrorHandling.instance.getLogger().log(Level.WARNING, "Could not add received data to cachingQueue, because queue is full. Clearing queue...");
                    cachingQueue.clear();
                }
                break;
        }
    }
}
