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

import eu.asterics.mw.services.AstericsThreadPool;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.TooManyListenersException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

class CIMIdentifyPortController extends CIMPortController implements Runnable {

    // local definitions
    enum SearchState {
        SEARCH_START, DETECTING_CIM, FOUND_CIM, DETECTING_ZIGBEE, FOUND_ZIGBEE
    }

    private static final long PACKET_DETECT_TIMEOUT = 4000;
    private static final String TASK_ID_CIM_IDENTIFIER = CIMPortManager.TASK_ID_CIM_TEMP + "IDENTIFIER_";
    private static final String TASK_ID_CIM_INJECTOR = CIMPortManager.TASK_ID_CIM_TEMP + "INJECTOR_";

    SerialPort port;
    long timeOfCreation;
    final int BAUD_RATE = 115200;
    private volatile boolean threadRunning = true;
    private volatile boolean injectorThreadEnded = false;
    private boolean connectionLost;

    private String name;
    private final String taskIdIdentifierTask;
    private final String taskIdInjectorTask;
    private CIMPortEventListener eventListener;

    public String getName() {
        return name;
    }

    public String getTaskIdIdentifierTask() {
        return taskIdIdentifierTask;
    }

    /**
     * Creates the port controller from the COM port identifier for an available
     * COM port.
     * 
     * @param portIdentifier
     * @throws CIMException
     */
    CIMIdentifyPortController(CommPortIdentifier portIdentifier) throws CIMException {
        super(portIdentifier.getName(), null);
        name = "Identify" + portIdentifier.getName();
        long currentTime = System.currentTimeMillis();
        taskIdIdentifierTask = MessageFormat.format("{0}{1}_{2}", TASK_ID_CIM_IDENTIFIER, currentTime, comPortName);
        taskIdInjectorTask = MessageFormat.format("{0}{1}_{2}", TASK_ID_CIM_INJECTOR, currentTime, comPortName);

        try {
            port = (SerialPort) portIdentifier.open(this.getClass().getName() + comPortName, 2000);

            logger.fine("Opened serial port " + comPortName);
            port.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // bug fix high cpu load on Win10:
            // https://github.com/asterics/AsTeRICS/issues/116
            port.enableReceiveTimeout(RXTX_PORT_ENABLE_RECEIVE_TIMEOUT);

            eventListener = new CIMPortEventListener(port.getInputStream());
            port.addEventListener(eventListener);
            port.notifyOnDataAvailable(true);
        } catch (UnsupportedCommOperationException ucoe) {
            logger.severe("Could not set serial port parameters -> " + ucoe.getMessage());
            closePort();
            throw new CIMException();
        } catch (PortInUseException piue) {
            logger.warning(MessageFormat.format("Port {0} already in use -> {1}", comPortName, piue.getMessage()));
            closePort();
            throw new CIMException();
        } catch (IOException ioe) {
            logger.severe(this.getClass().getName() + "." + "CIMSerialPortController: Could not get input stream"
                    + " -> \n" + ioe.getMessage());
            closePort();
            throw new CIMException();
        } catch (TooManyListenersException tmle) {
            logger.warning(MessageFormat.format("Too many listeners on port {0} -> {1}", comPortName, tmle.getMessage()));
            closePort();
            throw new CIMException();
        }
    }

    @Override
    public void closePortInternal() {
        threadRunning = false;
        if(port != null) {
            try {
                port.disableReceiveFraming();
                port.disableReceiveThreshold();
                port.disableReceiveTimeout();
                port.removeEventListener();
                port.notifyOnDataAvailable(false); //sometimes throws NPE?!
                port.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "error preparing port for closing: " + e.getClass().getName());
            }
        }
        port = null;
        eventListener = null;
        logger.fine("Port: " + comPortName + " closed.");
    }

    /**
     * The main packet receive loop for a COM port. This permanently reads data
     * from the blocking queue between the serial port and the serial port
     * controller which detaches the serial receiver thread from the packet
     * parsing task. Also performs the auto detection of the CIM.
     */
    @Override
    public void run() {
        timeOfCreation = System.currentTimeMillis();
        SearchState searchState = SearchState.SEARCH_START;
        CIMProtocolPacket packet = null;

        connectionLost = false;
        boolean closeResources = true;

        Future injectorThreadFuture = AstericsThreadPool.instance.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    logger.fine("Identify packet injector thread started for port " + comPortName);
                    while (threadRunning) {
                        sendPacketInternal(null, CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER,
                                CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    logger.log(Level.INFO,"InterruptedException on waiting in IdentifyPortController " + comPortName);
                }
                injectorThreadEnded = true;
            }
        }, taskIdInjectorTask);

        // near endless loop
        try {
            while (threadRunning) {
                // wait for next byte in queue
                Byte b = eventListener.poll(1000L, TimeUnit.MILLISECONDS);
                if (System.currentTimeMillis() - timeOfCreation > PACKET_DETECT_TIMEOUT) {
                    logger.fine("Did not receive identifiable CIM protocol packet during identification phase on port " + comPortName);
                    threadRunning = false;
                    closeResources = true;
                }

                if (b != null) {
                    // System.out.println(String.format("Port: " + comPortName +
                    // " Recv: 0x%2x ('%c')", b, b));
                    switch (searchState) {
                        case SEARCH_START:
                            if (b == '@') {
                                searchState = SearchState.DETECTING_CIM;
                            }
                            break;
                        case DETECTING_CIM:
                            if (b == 'T') {
                                // found the packet header, start parsing the packet
                                // content
                                searchState = SearchState.FOUND_CIM;
                                packet = new CIMProtocolPacket();
                                // System.out.println("Created new packet");
                            } else {
                                searchState = SearchState.SEARCH_START;
                            }

                            break;
                        case FOUND_CIM:
                            synchronized (this) {
                                if (packet.parsePacket(b)) {
                                    if (packet.getFeatureAddress() == CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER
                                            && (packet.getData() != null)) {
                                        // finished parsing the packet
                                        short cimId = packet.getAreCimID();
                                        long uid = 0;
                                        byte[] data = packet.getData();
                                        for (int i = 3; i >= 0; i--) {
                                            uid = (uid << 8) | (((int) data[i]) & 0xff);
                                        }

                                        CIMUniqueIdentifier cuid = new CIMUniqueIdentifier(cimId, uid);

                                        logger.fine(MessageFormat.format("{0}: CIM identified as {1}", comPortName, cuid.toString()));

                                        byte nextSerialNumber = (byte) (packet.getSerialNumber() + 1);
                                        closeResources = CIMPortManager.getInstance().reportFoundCim(cuid, comPortName, port, eventListener, nextSerialNumber);
                                        threadRunning = false;
                                    }
                                    searchState = SearchState.SEARCH_START;
                                }
                                break;
                            }
                    }
                } // if (b != null)
                Thread.yield();
            }
        } catch (InterruptedException | IOException e) {
            logger.log(Level.WARNING, "error in CIMIdentity port controller: " + comPortName, e);
            closeResources = true;
        }
        logger.info(MessageFormat.format("waiting on injector thread to stop ({0})...", comPortName));
        threadRunning = false;
        injectorThreadFuture.cancel(true);
        for(int i=0; i<7 && !injectorThreadEnded; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        if(!injectorThreadEnded) {
            logger.warning("Injector Thread did not end after 700ms: " + comPortName);
        }

        logger.fine(MessageFormat.format("Injector thread ended. Cleaning up resources and stopping Identifier thread ({0})", comPortName));
        if (closeResources) {
            closePort();
        }
        logger.fine(MessageFormat.format("Identifier thread {0} ended", comPortName));
    }

    @Override
    synchronized byte sendPacketInternal(byte[] data, short featureAddress, short requestCode, boolean crc) {
        byte ret = -1;
        if (threadRunning) {
            CIMProtocolPacket packet = new CIMProtocolPacket();
            packet.useCrc(crc);
            packet.setAreCimID(areVersion);
            packet.setSerialNumber(serialNumber);
            packet.setFeatureAddress(featureAddress);
            packet.setRequestReplyCode(requestCode);
            packet.setData(data);

            try {
                port.getOutputStream().write(packet.toBytes());
                port.getOutputStream().flush();
            } catch (IOException ioe) {
                if (connectionLost == false) {
                    logger.severe(MessageFormat.format("Could not send packet #{0}, {1} on port {2}. (if port related to Windows Bluetooth stack, ignore error -> {3}",
                            serialNumber, packet.toString(), comPortName, ioe.getMessage()));
                    connectionLost = true;
                }
                return -1;
            } catch (NullPointerException npe) {
                logger.severe(MessageFormat.format("NullPointerException trying to send packet #{0} -> \n {1}", serialNumber, npe.getMessage()));
                return -1;
            }

            ret = serialNumber;
            if (serialNumber == 127) {
                serialNumber = 0;
            } else {
                serialNumber++;
            }
        } else {
            logger.warning(MessageFormat.format("called sendPacket while thread was set to end ({0})", this.comPortName));
        }
        return ret;
    }

}
