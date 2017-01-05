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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import gnu.io.SerialPort;

/**
 * The CIMSerialPortController communicates with one CIM attached to the
 * platform. The controller is running in its own thread and permanently
 * listening for new data from the serial port.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
class CIMSerialPortController extends CIMPortController implements Runnable {
    private final int PACKET_TIMEOUT = 3000;
    private final int PACKET_DETECT_SEND_OVERHEAD = 1000;

    // local definitions
    enum PacketState {
        PACKET_SEARCH_1, PACKET_SEARCH_2, PACKET_FOUND
    }

    final short areVersion = 0x1; // TODO replace with utility function

    // Java and communication related
    boolean threadRunning = true;
    boolean threadEnded = false;
    BlockingQueue<Byte> dataSource;

    // serial port handling
    SerialPort port;
    int baudRate = 115200;

    // packet handling
    long lastPacketSent = 0;

    InputStream inputStream = null;
    CIMUniqueIdentifier cuid = null;
    short cimId = 0;
    boolean constructionSuccess = false;
    boolean connectionLost = false;

    long timeOfCreation;

    /**
     * Creates the port controller from the COM port identifier for an available
     * COM port.
     * 
     * @param portIdentifier
     */
    CIMSerialPortController(String comPortName, SerialPort port, CIMPortEventListener listener) {
        super(comPortName);
        this.port = port;

        dataSource = listener.dataSink;
        inputStream = listener.in;
        timeOfCreation = System.currentTimeMillis();

        logger.fine(comPortName + " controller thread created");
    }

    /**
     * The main packet receive loop for a COM port. This permanently reads data
     * from the blocking queue between the serial port and the serial port
     * controller which detaches the serial receiver thread from the packet
     * parsing task. Also performs the auto detection of the CIM.
     */
    @Override
    public void run() {
        PacketState searchState = PacketState.PACKET_SEARCH_1;
        CIMProtocolPacket packet = null;
        connectionLost = false;

        // near endless loop
        while (threadRunning) {
            try {
                // wait for next byte in queue
                Byte b = dataSource.poll(1000L, TimeUnit.MILLISECONDS);
                if (b != null) {
                    System.currentTimeMillis();
                    switch (searchState) {
                    case PACKET_SEARCH_1:
                        if (b == '@') {
                            searchState = PacketState.PACKET_SEARCH_2;
                        }
                        break;
                    case PACKET_SEARCH_2:
                        if (b == 'T') {
                            // found the packet header, start parsing the packet
                            // content
                            searchState = PacketState.PACKET_FOUND;
                            packet = new CIMProtocolPacket();
                        } else {
                            searchState = PacketState.PACKET_SEARCH_1;
                        }

                        break;
                    case PACKET_FOUND:
                        synchronized (this) {
                            if (packet.parsePacket(b)) {
                                // finished parsing the packet
                                int errorCode = packet.receivedWithoutErrors();
                                if (errorCode != CIMProtocolPacket.ERRORSTATE_DROP_PACKET
                                        && errorCode != CIMProtocolPacket.ERRORSTATE_NOT_READY) {
                                    byte receivedSerialNumber = packet.getSerialNumber();

                                    if ((receivedSerialNumber & 0x80) != 0) {
                                        // this is a CIM generated packet
                                        if (nextExpectedCIMIssuedSerialNumber == 0) {
                                            nextExpectedCIMIssuedSerialNumber = receivedSerialNumber;
                                        }

                                        if (nextExpectedCIMIssuedSerialNumber != receivedSerialNumber) {
                                            // Logger.getAnonymousLogger().warning("Did
                                            // not receive correct packet serial
                                            // number on CIM generated packet,
                                            // potentially lost packets, " +
                                            // packet.toString());
                                            logger.warning(this.getClass().getName()
                                                    + ".run: Did not receive correct packet serial number on CIM generated packet, potentially lost packets, "
                                                    + packet.toString());
                                            nextExpectedCIMIssuedSerialNumber = receivedSerialNumber;
                                        }

                                        if (nextExpectedCIMIssuedSerialNumber == -1) {
                                            nextExpectedCIMIssuedSerialNumber = -128;
                                        } else {
                                            nextExpectedCIMIssuedSerialNumber++;
                                        }

                                        synchronized (eventHandlers) {
                                            for (CIMEventHandler eventHandler : eventHandlers) {
                                                eventHandler
                                                        .handlePacketReceived(new CIMEventPacketReceived(this, packet));
                                            }
                                        }
                                    } else {
                                        // this is a reply to an ARE generated
                                        // packet
                                        if (nextExpectedIncomingSerialNumber != receivedSerialNumber) {
                                            // some packets have been lost
                                            // Logger.getAnonymousLogger().severe("Lost
                                            // one or more packets: expected: #"
                                            // +
                                            // nextExpectedIncomingSerialNumber
                                            // + ", received: #" +
                                            // receivedSerialNumber);
                                            logger.severe(this.getClass().getName()
                                                    + ".run: Lost one or more packets: expected: #"
                                                    + nextExpectedIncomingSerialNumber + ", received: #"
                                                    + receivedSerialNumber);
                                            if (!eventHandlers.isEmpty()) {
                                                // notify the caller about all
                                                // missing packets
                                                while (nextExpectedIncomingSerialNumber != receivedSerialNumber) {
                                                    synchronized (eventHandlers) {
                                                        for (CIMEventHandler eventHandler : eventHandlers) {
                                                            eventHandler.handlePacketError(new CIMEventErrorPacketLost(
                                                                    this, nextExpectedIncomingSerialNumber));
                                                        }
                                                    }
                                                    nextExpectedIncomingSerialNumber = (byte) ((nextExpectedIncomingSerialNumber
                                                            + 1) % 128);
                                                }
                                            } else {
                                                // no need to notify just skip
                                                // the missing numbers
                                                nextExpectedIncomingSerialNumber = receivedSerialNumber;
                                                nextExpectedIncomingSerialNumber++;
                                            }
                                        } else {
                                            nextExpectedIncomingSerialNumber = (byte) ((nextExpectedIncomingSerialNumber
                                                    + 1) % 128);

                                            synchronized (eventHandlers) {
                                                for (CIMEventHandler eventHandler : eventHandlers) {
                                                    eventHandler.handlePacketReceived(
                                                            new CIMEventPacketReceived(this, packet));
                                                }
                                            }
                                        }
                                    }
                                } else // received with errors
                                {
                                    synchronized (eventHandlers) {
                                        for (CIMEventHandler eventHandler : eventHandlers) {
                                            eventHandler.handlePacketError(new CIMEventErrorPacketFault(this, packet));
                                        }
                                    }

                                    // Logger.getAnonymousLogger().severe("Error
                                    // in packet #" + packet.getSerialNumber() +
                                    // "on port " + comPortName);
                                    logger.severe(this.getClass().getName() + ".run: Error in packet #"
                                            + packet.getSerialNumber() + "on port " + comPortName);

                                    // TODO what if serial number got distorted
                                    nextExpectedIncomingSerialNumber = (byte) ((packet.getSerialNumber() + 1) % 128);
                                }
                                searchState = PacketState.PACKET_SEARCH_1;
                            }
                            break;
                        }
                    }
                } else {
                    // timeout happened
                    if (lastPacketSent != 0) {
                        if ((nextExpectedIncomingSerialNumber != serialNumber)
                                && (lastPacketSent + PACKET_TIMEOUT < System.currentTimeMillis())) {
                            synchronized (eventHandlers) {
                                for (CIMEventHandler eventHandler : eventHandlers) {
                                    eventHandler.handlePacketError(
                                            new CIMEventErrorPacketLost(this, nextExpectedIncomingSerialNumber));
                                }
                            }
                            logger.warning(this.getClass().getName() + ".run: Did not receive reply to packet #"
                                    + nextExpectedIncomingSerialNumber + " since " + PACKET_TIMEOUT + "ms on "
                                    + comPortName);
                            nextExpectedIncomingSerialNumber = (byte) ((nextExpectedIncomingSerialNumber + 1) % 128);
                            lastPacketSent = 0;
                        }
                    }
                }
                Thread.yield();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.fine(this.getClass().getName() + ".run: Thread " + comPortName + " main loop ended, cleaning up \n");

        // thread ends, clean up
        port.notifyOnDataAvailable(false);
        port.removeEventListener();
        synchronized (eventHandlers) {
            eventHandlers.clear();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            port.close();
        }
        CIMPortManager.getInstance().removeConnection(cimId);

        logger.fine(this.getClass().getName() + ".run: Thread on serial port " + comPortName + " ended \n");
        threadEnded = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.mw.cimcommunication.CIMPortController#closePort()
     */
    @Override
    public void closePort() {
        logger.fine(this.getClass().getName() + ".closePort on " + comPortName
                + ": This method currently waits until the port thread ends, "
                + "which might result in a deadlock but should not through " + "the timeouts of blocking \n");
        threadRunning = false;
        try {
            while (!threadEnded) {
                Thread.yield();
            }
        } finally {
            port.close();
            logger.fine("Port: " + port.getName() + " closed");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.asterics.mw.cimcommunication.CIMPortController#sendPacket(byte[],
     * short, short, boolean)
     */
    @Override
    synchronized byte sendPacket(byte[] data, short featureAddress, short requestCode, boolean crc) {
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
                port.getOutputStream().close();
                // for (byte b : packet.toBytes())
                // {
                // System.out.println(String.format("Sent: 0x%2x ('%c')", b,
                // b));
                // }
            } catch (IOException ioe) {
                if (connectionLost == false) {
                    logger.severe(this.getClass().getName() + ".sendPacket:could" + " not send packet #" + serialNumber
                            + ", " + packet.toString() + " -> \n" + ioe.getMessage());
                    connectionLost = true;
                }
                return -1;
            } catch (NullPointerException npe) {
                logger.severe(this.getClass().getName() + ".sendPacket: "
                        + "NullPointerException trying to send packet #" + serialNumber + " -> \n" + npe.getMessage());
                return -1;
            }

            lastPacketSent = System.currentTimeMillis();

            ret = serialNumber;
            if (serialNumber == 127) {
                serialNumber = 0;
            } else {
                serialNumber++;
            }
        } else {
            logger.warning(
                    this.getClass().getName() + ".sendPacket: " + "sendPacket called while thread was set to end \n");
        }
        return ret;
    }

    public void setNextExpectedIncomingSerialNumber(byte nextExpectedIncomingSerialNumber) {

        this.nextExpectedIncomingSerialNumber = nextExpectedIncomingSerialNumber;
        this.serialNumber = nextExpectedIncomingSerialNumber;
    }

}
