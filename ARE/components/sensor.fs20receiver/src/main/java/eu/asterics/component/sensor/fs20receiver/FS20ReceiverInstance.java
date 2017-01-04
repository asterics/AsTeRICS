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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.fs20receiver;

import java.util.logging.Logger;

import com.codeminders.hidapi.ClassPathLibraryLoader;

//import java.util.logging.Logger;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * This plugin is the receiver system for the FS20 home automatisation system.
 * The plugin opens a connection to the USB Receiver "FS20 PCE" and fire an
 * event if the received command fits to the set housecode and sender address.
 * 
 * 
 * 
 * @author Roland Ossmann [ro@ki-i.at] Date: 15.05.2012 Time: 11:13
 */
public class FS20ReceiverInstance extends AbstractRuntimeComponentInstance {

    private IRuntimeOutputPort opFs20command = new DefaultRuntimeOutputPort();

    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

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

    // declare member variables here

    int propHousecode = 11111111;
    int propSendaddress = 1111;

    FS20Reader runnable;
    Thread readerThread;
    private Logger logger = AstericsErrorHandling.instance.getLogger();

    /**
     * The class constructor.
     */
    public FS20ReceiverInstance() {
        logger.fine("Trying to load library for FS20...");
        boolean successLoading = ClassPathLibraryLoader.loadNativeHIDLibrary();
        if (successLoading == false) {
            throw new RuntimeException("Could not load native lib for FS20 device.");
        }
        logger.fine("Success loading native lib for FS20: " + successLoading);
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
     * Returns the output port of the component
     * 
     * @param portID
     *            the name of the port
     * @return the instance of the port, null otherwise
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("fs20command".equalsIgnoreCase(portID)) {
            return opFs20command;
        } else {
            return null;
        }
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
        if ("off".equalsIgnoreCase(eventPortID)) {
            return etpOff;
        }
        if ("onLevel1".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel1;
        }
        if ("onLevel2".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel2;
        }
        if ("onLevel3".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel3;
        }
        if ("onLevel4".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel4;
        }
        if ("onLevel5".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel5;
        }
        if ("onLevel6".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel6;
        }
        if ("onLevel7".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel7;
        }
        if ("onLevel8".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel8;
        }
        if ("onLevel9".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel9;
        }
        if ("onLevel10".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel10;
        }
        if ("onLevel11".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel11;
        }
        if ("onLevel12".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel12;
        }
        if ("onLevel13".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel13;
        }
        if ("onLevel14".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel14;
        }
        if ("onLevel15".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel15;
        }
        if ("onLevel16".equalsIgnoreCase(eventPortID)) {
            return etpOnLevel16;
        }
        if ("onOldLevel".equalsIgnoreCase(eventPortID)) {
            return etpOnOldLevel;
        }
        if ("toggle".equalsIgnoreCase(eventPortID)) {
            return etpToggle;
        }
        if ("dimUp".equalsIgnoreCase(eventPortID)) {
            return etpDimUp;
        }
        if ("dimDown".equalsIgnoreCase(eventPortID)) {
            return etpDimDown;
        }
        if ("dimUpAndDown".equalsIgnoreCase(eventPortID)) {
            return etpDimUpAndDown;
        }
        if ("programTimer".equalsIgnoreCase(eventPortID)) {
            return etpProgramTimer;
        }
        if ("offForTimerThenOldLevel".equalsIgnoreCase(eventPortID)) {
            return etpOffForTimerThenOldLevel;
        }
        if ("onForTimerThenOff".equalsIgnoreCase(eventPortID)) {
            return etpOnForTimerThenOff;
        }
        if ("onOldLevelForTimerThenOff".equalsIgnoreCase(eventPortID)) {
            return etpOnOldLevelForTimerThenOff;
        }
        if ("reset".equalsIgnoreCase(eventPortID)) {
            return etpReset;
        }
        if ("onForTimerThenOldLevel".equalsIgnoreCase(eventPortID)) {
            return etpOnForTimerThenOldLevel;
        }
        if ("onOldLevelForTimerThenPreviousState".equalsIgnoreCase(eventPortID)) {
            return etpOnOldLevelForTimerThenPreviousState;
        }

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
        if ("housecode".equalsIgnoreCase(propertyName)) {
            return propHousecode;
        }
        if ("sendaddress".equalsIgnoreCase(propertyName)) {
            return propSendaddress;
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
        if ("housecode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propHousecode;
            propHousecode = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("sendaddress".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSendaddress;
            propSendaddress = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        return null;
    }

    /**
     * parsing the input message from the receiver firstly, the housecode and
     * the senderaddress will be checked, then the event regarding to the
     * command will be fired
     */
    private void findEvent(byte[] inputMsg) {
        String housecode = "";
        for (int i = 2; i <= 5; i++) {
            housecode += (Integer.toHexString((int) inputMsg[i]));
        }
        String address = "";
        for (int i = 6; i <= 7; i++) {
            address += (Integer.toHexString((int) inputMsg[i]));
        }

        String commandStr = housecode + "_" + address + "_" + (Integer.toHexString((int) inputMsg[8]));
        opFs20command.sendData(ConversionUtils.stringToBytes(commandStr));

        try {
            if (Integer.parseInt(housecode) == propHousecode && Integer.parseInt(address) == propSendaddress) {
                int command = Integer.parseInt(Integer.toHexString((int) inputMsg[8]));
                AstericsErrorHandling.instance.reportInfo(this, String.format("Received FS20 Command: %s", command));
                switch (command) {
                case 0:
                    etpOff.raiseEvent();
                    break;
                case 1:
                    etpOnLevel1.raiseEvent();
                    break;
                case 2:
                    etpOnLevel2.raiseEvent();
                    break;
                case 3:
                    etpOnLevel3.raiseEvent();
                    break;
                case 4:
                    etpOnLevel4.raiseEvent();
                    break;
                case 5:
                    etpOnLevel5.raiseEvent();
                    break;
                case 6:
                    etpOnLevel6.raiseEvent();
                    break;
                case 7:
                    etpOnLevel7.raiseEvent();
                    break;
                case 8:
                    etpOnLevel8.raiseEvent();
                    break;
                case 9:
                    etpOnLevel9.raiseEvent();
                    break;
                case 10:
                    etpOnLevel10.raiseEvent();
                    break;
                case 11:
                    etpOnLevel11.raiseEvent();
                    break;
                case 12:
                    etpOnLevel12.raiseEvent();
                    break;
                case 13:
                    etpOnLevel13.raiseEvent();
                    break;
                case 14:
                    etpOnLevel14.raiseEvent();
                    break;
                case 15:
                    etpOnLevel15.raiseEvent();
                    break;
                case 16:
                    etpOnLevel16.raiseEvent();
                    break;
                case 17:
                    etpOnOldLevel.raiseEvent();
                    break;
                case 18:
                    etpToggle.raiseEvent();
                    break;
                case 19:
                    etpDimUp.raiseEvent();
                    break;
                case 20:
                    etpDimDown.raiseEvent();
                    break;
                case 21:
                    etpDimUpAndDown.raiseEvent();
                    break;
                case 22:
                    etpProgramTimer.raiseEvent();
                    break;
                case 24:
                    etpOffForTimerThenOldLevel.raiseEvent();
                    break;
                case 25:
                    etpOnForTimerThenOff.raiseEvent();
                    break;
                case 26:
                    etpOnOldLevelForTimerThenOff.raiseEvent();
                    break;
                case 27:
                    etpReset.raiseEvent();
                    break;
                case 30:
                    etpOnForTimerThenOldLevel.raiseEvent();
                    break;
                case 31:
                    etpOnOldLevelForTimerThenPreviousState.raiseEvent();
                    break;

                }
            }
        } catch (NumberFormatException ne) {
            AstericsErrorHandling.instance.reportError(this, "Error converting FS20 received codes");
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        super.start();
        // AstericsErrorHandling.instance.reportError(this, "Could not find FS20
        // PCE Device");
        AstericsErrorHandling.instance.reportInfo(this, "Starting FS20 Receiver");
        runnable = new FS20Reader();

        runnable.addEventListener(new FS20EventListener() {
            @Override
            public void fs20EventOccurred(FS20ReaderEvent evt) {

                // int i =0;
                // for (byte b : evt.getMsg())
                // System.out.println("Byte "+(i++) + ": " + b);
                findEvent(evt.getMsg());
            }
        });

        // Create the thread supplying it with the runnable object
        readerThread = new Thread(runnable);

        // Start the thread
        readerThread.start();

    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        readerThread.interrupt();
        runnable.done();
        readerThread.interrupt();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();

        readerThread = new Thread(runnable);
        readerThread.start();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        readerThread.interrupt();
        runnable.done();
        readerThread.interrupt();
        super.stop();

    }
}