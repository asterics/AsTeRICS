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

//import eu.asterics.mw.services.AstericsLogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * A class to communicate with a device connected to a serial port which does
 * not adhere to the CIM protocol specification. This class mainly wraps the
 * RXTX implementation to something that can be used more easily in the
 * framework of the AsTeRICS project.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
class CIMRawPortController extends CIMPortController {
    private int baudRate = 115200;

    InputStream inputStream;
    OutputStream outputStream;
    CIMPortEventListener eventListener;

    private Logger logger = AstericsErrorHandling.instance.getLogger();

    // Java and communication related
    boolean threadRunning = true;
    CIMEventHandler eventHandler;

    // serial port handling
    SerialPort port;

    /**
     * Creates the raw port controller from a port identifier and a given
     * baudrate.
     * 
     * @param portIdentifier
     *            identifier for the request COM port
     * @param baudRate
     *            the requested baud rate for the port
     * @throws CIMException
     */
    CIMRawPortController(CommPortIdentifier portIdentifier, int baudRate) throws CIMException {
        super(portIdentifier.getName());

        try {
            port = (SerialPort) portIdentifier.open(this.getClass().getName() + comPortName, 2000);

            port.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // bug fix high cpu load on Win10:
            // https://github.com/asterics/AsTeRICS/issues/116
            port.enableReceiveTimeout(RXTX_PORT_ENABLE_RECEIVE_TIMEOUT);

            inputStream = port.getInputStream();
            outputStream = port.getOutputStream();
            eventListener = new CIMPortEventListener(inputStream);
            port.addEventListener(eventListener);
            port.notifyOnDataAvailable(true);

        } catch (UnsupportedCommOperationException ucoe) {
            logger.severe(this.getClass().getName() + "." + "CIMSerialPortController: Could not set serial port "
                    + "parameters -> \n" + ucoe.getMessage());
            port.close();
            throw new CIMException();
        } catch (PortInUseException piue) {
            logger.warning(this.getClass().getName() + "." + "CIMSerialPortController: "
                    + String.format("Port %s already in use", comPortName) + " -> \n" + piue.getMessage());
            throw new CIMException();
        } catch (IOException ioe) {
            logger.severe(this.getClass().getName() + "." + "CIMSerialPortController: Could not get input stream"
                    + " -> \n" + ioe.getMessage());
            port.close();
            throw new CIMException();
        } catch (TooManyListenersException tmle) {
            logger.warning(this.getClass().getName() + "." + "CIMSerialPortController: "
                    + String.format("Too many listeners on port %s", comPortName) + " -> \n" + tmle.getMessage());
            throw new CIMException();
        }

    }

    /**
     * Switchtes the baud rate of the serial port
     * 
     * @param baudRate
     *            selected baudrate
     */
    void setBaudRate(int baudRate) {
        if (this.baudRate != baudRate) {
            try {
                this.baudRate = baudRate;
                port.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
                logger.fine(this.getClass().getName() + ".setBaudRate:" + " Baudrate set to: " + baudRate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closePort() {
        if (port != null) {
            try {
                port.removeEventListener();
                port.getOutputStream().close();
                port.getInputStream().close();
                port.close();
                port = null;
                logger.fine(this.getClass().getName() + ".run: Port " + comPortName + " closed \n");
            } catch (Exception e) {
                logger.log(Level.WARNING, MessageFormat.format("error on closing port {0}.", comPortName), e);
            }
        }
        threadRunning = false;
    }

    @Override
    byte sendPacket(byte[] data, short featureAddress, short requestCode, boolean crc) {
        try {
            // for (byte b : data)
            // {
            // System.out.println(String.format("Sent: 0x%2x ('%c')", b, b));
            // }
            port.getOutputStream().write(data);
            port.getOutputStream().flush();
            port.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public Byte poll() throws IOException {
        return poll(1000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Byte poll(long timeout, TimeUnit unit) throws IOException {
        try {
            return eventListener.poll(timeout, unit);
        } catch (InterruptedException e) {
            return null;
        }
    }
}
