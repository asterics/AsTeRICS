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

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public abstract class CIMPortController {
    // This timeout must be set to avoid a high CPU load on Win10
    // Use @see RXTXPort#enableReceiveTimeout
    public static final int RXTX_PORT_ENABLE_RECEIVE_TIMEOUT = 500;
    public static final int RXTX_PORT_DEFAULT_SEND_TIMEOUT = 2000;
    private static final String TASK_ID_CIM_SENDPACKET = CIMPortManager.TASK_ID_CIM_TEMP + "SENDPACKET_";

    List<CIMEventHandler> eventHandlers = new LinkedList<CIMEventHandler>();
    protected Logger logger = null;
    String comPortName;
    CIMUniqueIdentifier cuid = null;

    final short areVersion = 0x1; // TODO replace with utility function

    byte serialNumber = 0;
    byte nextExpectedIncomingSerialNumber = 0;
    byte nextExpectedCIMIssuedSerialNumber = 0;

    private boolean hasClosed = false;

    public CIMPortController(String comPortName, CIMUniqueIdentifier cuid) {
        logger = AstericsErrorHandling.instance.getLogger();
        this.comPortName = comPortName;
        this.cuid = cuid;
    }

    public String getComPortName() {
        return comPortName;
    }

    /**
     * Adds a CIM event handler to the list of event handlers
     * 
     * @param handler
     *            the implementation of the event handler interface
     */
    public void addEventListener(CIMEventHandler handler) {
        synchronized (eventHandlers) {
            if (!eventHandlers.contains(handler)) {
                logger.fine(this.getClass().getName() + ".addEventListener: adding listener:" + handler + "on port "
                        + comPortName);
                eventHandlers.add(handler);
            }
        }
    }

    /**
     * Removes a specified CIM event handler from the list of handlers
     * 
     * @param handler
     *            the handler implementation to be removed
     * 
     */
    public void removeEventListener(CIMEventHandler handler) {
        synchronized (eventHandlers) {
            if (eventHandlers.contains(handler)) {
                logger.fine(this.getClass().getName() + ".removeEventListener: removing listener:" + handler
                        + "on port " + comPortName);
                eventHandlers.remove(handler);
            }
        }
    }

    /**
     * gets the input stream of the implementing CIMPortController
     * @return
     * @RuntimeException if the subclass does not implement this method
     */
    public InputStream getInputStream() {
        throw new RuntimeException("method eu.asterics.mw.cimcommunication.CIMPortController.getInputStream() not implemented");
    }

    /**
     * gets the output stream of the implementing CIMPortController
     * @return
     * @RuntimeException if the subclass does not implement this method
     */
    public OutputStream getOutputStream() {
        throw new RuntimeException("method eu.asterics.mw.cimcommunication.CIMPortController.getOutputStream() not implemented");
    }

    /**
     * tries to read from the input stream for 1 second and returns the read byte
     * @return the read byte or null, if no byte was read within 1 second
     * @RuntimeException if the subclass does not implement this method
     * @IOException if IOException occured on the polling data source
     */
    public Byte poll() throws IOException {
        throw new RuntimeException("method eu.asterics.mw.cimcommunication.CIMPortController.poll() not implemented");
    }

    /**
     * tries to read from the input stream with the given timout and returns the read byte
     * @param timeout the timeout, unit specified by unit
     * @param unit the TimeUnit of the given timeout
     * @return the read byte or null, if no byte was read within the timeout
     * @RuntimeException if the subclass does not implement this method
     * @IOException if IOException occured on the polling data source
     */
    public Byte poll(long timeout, TimeUnit unit) throws IOException {
        throw new RuntimeException("method eu.asterics.mw.cimcommunication.CIMPortController.poll(long timeout, TimeUnit unit) not implemented");
    }

    /**
     * Closes the port. Tells the thread to run out and returns only after the
     * thread has ended.
     */
    abstract void closePortInternal();

    public final void closePort() {
        synchronized (this) {
            if (hasClosed) {
                return; // do not close again, if already closing or closed
            }
            hasClosed = true;
        }
        eventHandlers.clear();
        closePortInternal();
        boolean removedConnection = CIMPortManager.getInstance().removeConnection(cuid);
        if(removedConnection) {
            CIMPortManager.getInstance().printActiveCimControllers();
        }
    }

    /**
     * Sends a packet to the connected device.
     * 
     * @param data
     *            a byte array of data to be transferred
     * @param featureAddress
     *            the feature address to send the data to
     * @param requestCode
     *            the request code for the transfer
     * @param crc
     *            true if crc should be attached to packet
     * @return the serial number of the packet or -1 on error
     */
    abstract byte sendPacketInternal(byte[] data, short featureAddress, short requestCode, boolean crc) throws Exception;

    /**
     * Sends a packet to the connected device with a timeout of 2000ms
     *
     * @param data
     * @param featureAddress
     * @param requestCode
     * @param crc
     * @return the serial number of the packet or -1 on error
     */
    final byte sendPacket(byte[] data, short featureAddress, short requestCode, boolean crc) {
        return sendPacket(data, featureAddress, requestCode, crc, RXTX_PORT_DEFAULT_SEND_TIMEOUT);
    }

    /**
     * Sends a packet to the connected device.
     *
     * @param data
     *            a byte array of data to be transferred
     * @param featureAddress
     *            the feature address to send the data to
     * @param requestCode
     *            the request code for the transfer
     * @param crc
     *            true if crc should be attached to packet
     * @param timeoutMillis the maximum time in milliseconds to wait for successful sending
     * @return the serial number of the packet or -1 on error (e.g. if timeout exceeded)
     */
    final byte sendPacket(final byte[] data, final short featureAddress, final short requestCode, final boolean crc, final long timeoutMillis) {
        synchronized (this) {
            if(hasClosed) {
                logger.warning("called sendPacket, but port is already closed or in closing: " + comPortName);
                return 0;
            }
        }
        String taskIdSendPacket = MessageFormat.format("{0}{1}_{2}", TASK_ID_CIM_SENDPACKET, System.currentTimeMillis(), comPortName);
        Future<Byte> future;
        future = AstericsThreadPool.getInstance().execute(new Callable<Byte>() {
            @Override
            public Byte call() throws Exception {
                try {
                    return sendPacketInternal(data, featureAddress, requestCode, crc);
                } catch (Exception e) {
                    logger.warning(MessageFormat.format("error on send packet for port {0}: {1} -> closing port...", comPortName, e.getClass().getName()));
                    closePort();
                }
                return -1;
            }
        }, taskIdSendPacket);

        byte returnValue = -1;
        try {
            returnValue = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warning("error in getting return value of sendPacket: " + e.getClass().getName());
        }
        return returnValue;
    }
}