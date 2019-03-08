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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.myocontroller;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Interfaces with the Myocontroller data acquisition device via bluetooth / Com port
 * 
 * 
 * 
 * @author Chris
 */
public class MyocontrollerInstance extends AbstractRuntimeComponentInstance {
    public final int NUMBER_OF_CHANNELS = 6;
    public final String OP_CH_PREFIX = "Channel";
    public final IRuntimeOutputPort[] opChannels = new DefaultRuntimeOutputPort[NUMBER_OF_CHANNELS];

    private InputStream in = null;
    private Thread readerThread = null;
    private boolean running = false;
    String propComPort = "COM4";
    int propBaudRate = 57600;

    // declare member variables here
    CIMPortController portController = null;

    String ARCHIVEFILE = "output_2019-03-06_16-55-22.log";
    RandomAccessFile filePointerInstance = null;
    
    /**
     * The class constructor.
     */
    public MyocontrollerInstance() {
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++) 
            opChannels[i] = new DefaultRuntimeOutputPort();
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {

        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) { 
        String s;
        for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
            s = OP_CH_PREFIX + (i+1);
            if (s.equalsIgnoreCase(portID)) {
                return opChannels[i];
            }
        }
        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */
    @Override
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
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("comPort".equalsIgnoreCase(propertyName)) {
            return propComPort;
        }
        if ("baudRate".equalsIgnoreCase(propertyName)) {
            return propBaudRate;
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
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("comPort".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propComPort;
            propComPort = newValue.toString();
            return oldValue;
        }
        if ("baudRate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBaudRate;
            propBaudRate = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * Called by the raw port controller if data is available
     * 
     * @param ev
     *            a CIMEvent which can be ignored as it is only needed due to
     *            the interface specification
     */

    int state = 0;
    int chn = 0;
    int actvalue=0;

    public void handlePacketReceived(byte data) {

        switch (state) {
            case 0: if (data == 'S') state++;
                    break;
            case 1: if (data == 'T') state++; else state = 0;
                    break;
            case 2: if (data == 'A') state++; else state = 0;
                    break;
            case 3: if (data == 0x0c) state++; else state = 0;   // discard the shorter (8) packages!
                    break;
            case 13: chn=(int)data;  state++;
                     break;
            case 15: actvalue = ((int) data) & 0xff; state++;
                     break;
            case 16: actvalue |= ((((int) data) & 0xff) << 8);
                     state++;
                     break;
            case 18: if (chn < NUMBER_OF_CHANNELS)
                         opChannels[chn].sendData(ConversionUtils.doubleToBytes((float)actvalue));
                     state = 0; // done ! look for next packet !
                     break;
            default:
                   state++;
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);

        if (portController == null) {
            AstericsErrorHandling.instance.reportInfo(this,
                    "Myocontroller: Could not construct raw port controller, please verify that the COM port is valid. Trying Archive File.");
            
            try {
                filePointerInstance = new RandomAccessFile(ARCHIVEFILE, "r");
            } catch (IOException e) {
                AstericsErrorHandling.instance.getLogger().info("Error reading archive file  " + ARCHIVEFILE);
            }
            
        } else in = portController.getInputStream();
    
        readerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                running = true;
                while (running) {
                    
                    if (portController != null) {
                         try {
                            if (in.available() > 0) {
                                handlePacketReceived((byte) in.read());
                            } else {
                                Thread.sleep(10);
                            }
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    }
                    else  {  // Archive File
                    
                        try {
                            for (int z=0;z<20;z++)
                               handlePacketReceived((byte) filePointerInstance.read());
                        } 
                        catch (IOException e) {
                            AstericsErrorHandling.instance.getLogger().info("Could not read from file");
                            // filePointerInstance.seek(1); // go to the first position in the file
                        }; 
                        try { Thread.sleep(2); }  // gives about 500 packets / second
                        catch (InterruptedException e) {};
                    }
                }
            }
        });
        readerThread.start();
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        if (portController != null) {
            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController = null;
            AstericsErrorHandling.instance.reportInfo(this, "Myocontroller raw port controller closed");
        }
        running = false;
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() { 
        portController = CIMPortManager.getInstance().getRawConnection(propComPort, propBaudRate, true);
        if (portController == null) {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not construct Myocontroller raw port controller, please make sure that the COM port is valid.");
        } 
        readerThread.start();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        super.stop();
        running = false;

        if (portController != null) {
            CIMPortManager.getInstance().closeRawConnection(propComPort);
            portController = null;
            AstericsErrorHandling.instance.reportInfo(this, "Myocontroller connection closed");
        }
        else {
            try {
                filePointerInstance.close();
            } catch (IOException e) {
                AstericsErrorHandling.instance.getLogger().severe("Error closing file: " + ARCHIVEFILE);
            }
        }
    }
}