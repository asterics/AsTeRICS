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

package eu.asterics.component.actuator.platformlcd;

import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.displayguimanagement.DisplayGuiManager;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * PlatformLCDInstance represents the display found in the core CIM's hardware.
 * It allows to activate and deactivate the display and to control the
 * brightness level. Moreover it encapsulates the menu handling abilities for
 * the display in it interacting with the navigation buttons found on the core
 * CIM. The class is implemented as an AbstractRuntimeComponentInstance as
 * although it does not have any ports or event ports because the use the
 * components are started after the CIM service which could not be guaranteed if
 * the handling of the display were implemented at the bundle level like CIM
 * communication.
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:55:05 AM
 */
public class PlatformLCDInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler {
    DisplayGuiManager gui = DisplayGuiManager.instance;

    final short CORE_CIM_ID = DisplayGuiManager.CORE_CIM_ID;

    final short CORE_FEATURE_DISPLAY_CLEAR = 0x70;
    final short CORE_FEATURE_DISPLAY_SET_TEXT_WINDOW = 0x71;
    final short CORE_FEATURE_DISPLAY_SET_TEXT_FONT = 0x73;
    final short CORE_FEATURE_DISPLAY_SET_TEXT_POSITION = 0x72;
    final short CORE_FEATURE_DISPLAY_PRINT = 0x74;
    final short CORE_FEATURE_DISPLAY_BITMAP = 0x75;
    final short CORE_FEATURE_DISPLAY_LIGHT = 0x76;

    final short CORE_FEATURE_BUTTON_STATE = 0x80;
    final short CORE_FEATURE_BUTTON_EVENT_MASK = 0x81;

    final long DISPLAY_ACTIVITY_TIMEOUT = 30000;

    private final byte[] textWindowData = { 0, 0, 8, 0, (byte) 132, 0, 32, 0 };
    CIMPortController port = null;
    int display_buffer;

    boolean[] buttonState = { false, false, false, false, false };
    boolean[] incomingButtonState = { false, false, false, false, false };

    MenuHandler menuHandler = null;

    final char[] astericsLogoCharStream = { 50, 0, 0, 0, 32, 0, 32, 0, 0xF8, 0x7, 0xFF, 0xFF, 0xF8, 0x3, 0xFF, 0xD7,
            0xFC, 0x1, 0xFF, 0xBB, 0xFC, 0x0, 0xFF, 0x55, 0xFE, 0x0, 0x7F, 0xAB, 0xFF, 0x0, 0x7F, 0x55, 0xFF, 0x80,
            0x3F, 0xAA, 0xFF, 0xC0, 0x1D, 0x55, 0xFF, 0xE0, 0xE, 0xAA, 0xFF, 0xE0, 0x15, 0x55, 0xFF, 0xF0, 0xB, 0xBA,
            0xFF, 0xF0, 0x15, 0x55, 0xFF, 0xF8, 0x2E, 0xEB, 0xFF, 0xFC, 0x55, 0x57, 0xFF, 0xFE, 0xBB, 0xBF, 0xFF, 0xFD,
            0x55, 0x57, 0xFF, 0xFE, 0xFE, 0xFF, 0xFF, 0xFD, 0x55, 0x5F, 0xFF, 0xFB, 0xBB, 0xBF, 0xFF, 0xF5, 0x55, 0x5F,
            0xFF, 0xEA, 0xEE, 0x8F, 0xFF, 0xD5, 0x55, 0x7, 0xFF, 0xAB, 0xBA, 0x3, 0xFF, 0x55, 0x55, 0x1, 0xFE, 0xAA,
            0xAE, 0x0, 0xFD, 0x55, 0x57, 0x0, 0xFA, 0xAA, 0xBF, 0x80, 0xF5, 0x55, 0x5F, 0x80, 0xEA, 0xAA, 0xBF, 0xC0,
            0xD5, 0x55, 0x7F, 0xC0, 0xAA, 0xAA, 0xFF, 0xE0, 0x55, 0x55, 0xFF, 0xF1 };

    byte[] astericsLogoByteStream = null;
    long lastActivity = 0;
    boolean displayActive = false;

    /**
     * Base constructor of the display instance will set up the display and set
     * it to show the text "Framework started".
     */
    public PlatformLCDInstance() {
        /*
         * // port = CIMPortManager.getInstance().getConnection(CORE_CIM_ID);
         * try { Thread.sleep(500); } catch (InterruptedException e) {
         * 
         * } // port = CIMPortManager.getInstance().getConnection(CORE_CIM_ID);
         * if (port != null) { AstericsErrorHandling.instance.reportInfo(this,
         * "Core CIM found"); port.addEventListener(this);
         * 
         * astericsLogoByteStream = new byte[astericsLogoCharStream.length]; for
         * (int i = 0; i < astericsLogoByteStream.length; i++) {
         * astericsLogoByteStream[i] = (byte) astericsLogoCharStream[i]; }
         * 
         * CIMPortManager.getInstance().sendPacket(port, buttonEventMask,
         * CORE_FEATURE_BUTTON_EVENT_MASK,
         * CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
         * 
         * CIMPortManager.getInstance().sendPacket(port, null,
         * CORE_FEATURE_DISPLAY_CLEAR,
         * CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
         * 
         * CIMPortManager.getInstance().sendPacket(port, textWindowData,
         * CORE_FEATURE_DISPLAY_SET_TEXT_WINDOW,
         * CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
         * 
         * CIMPortManager.getInstance().sendPacket(port, textFontData,
         * CORE_FEATURE_DISPLAY_SET_TEXT_FONT,
         * CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
         * 
         * writeToDisplay("Framework started", true); displayActivity();
         * 
         * menuHandler = new MenuHandler(this);
         * AstericsThreadPool.instance.execute(new Runnable() {
         * 
         * @Override public void run() { while (true) { try { if
         * (System.currentTimeMillis() - lastActivity >
         * DISPLAY_ACTIVITY_TIMEOUT) { displayInactive();
         * menuHandler.inactivity(); } Thread.sleep(1000); } catch
         * (InterruptedException e) { } } } }); } else
         * AstericsErrorHandling.instance.reportInfo(this,
         * "Could not find core CIM");
         */
    }

    /**
     * Should be called to indicate activity on the display. This is used if the
     * display will reduce its brightness after a certain amount of inactivity
     */
    private void displayActivity() {
        lastActivity = System.currentTimeMillis();
        if (!displayActive) {
            byte[] data = { 100 };
            CIMPortManager.getInstance().sendPacket(port, data, CORE_FEATURE_DISPLAY_LIGHT,
                    CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
            displayActive = true;
        }
    }

    /**
     * Reduces the back light of the display to zero and clears the display
     */
    private void displayInactive() {
        if (displayActive) {
            byte[] data = { 0 };
            // port.sendPacket(astericsLogoByteStream,
            // CORE_FEATURE_DISPLAY_BITMAP,
            // CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
            CIMPortManager.getInstance().sendPacket(port, data, CORE_FEATURE_DISPLAY_LIGHT,
                    CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
            CIMPortManager.getInstance().sendPacket(port, null, CORE_FEATURE_DISPLAY_CLEAR,
                    CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
            displayActive = false;
        }
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
        if ("input".equalsIgnoreCase(portID)) {
            return displayStringInputPort;
        }
        return null;
    }

    /**
     * Method stub as this component does not have properties
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method stub as this component does not have properties
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This port is not needed for the component however it is necessary to
     * allow ACS to accept the component as it would have absolutely no port
     * otherwise.
     */
    private final IRuntimeInputPort displayStringInputPort = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
        }

    };

    /**
     * Starts the component, does not do any actual work
     */
    @Override
    public void start() {
        AstericsErrorHandling.instance.reportInfo(this, "CoreCimDisplayInstance started");
        super.start();
    }

    /**
     * Stops the component, does not do any actual work
     */
    @Override
    public void stop() {
        AstericsErrorHandling.instance.reportInfo(this, "CoreCimDisplayInstance stopped");
        super.stop();
    }

    int charsPerLine = 22;

    /**
     * Writes a specified text to the display
     * 
     * @param text
     *            text to be displayed
     * @param justified
     *            if true text will be centered on the display
     */
    public void writeToDisplay(String text, boolean justified) {
        StringBuffer buf = new StringBuffer("\f");
        int length = text.length();
        if (justified) {
            int wsLength = charsPerLine - length;
            if (wsLength > 0) {
                for (int i = 0; i < (wsLength / 2); i++) {
                    buf.append(" ");
                }
            }
        }
        buf.append(text.substring(0, (text.length() > charsPerLine) ? charsPerLine : text.length()));
        CIMPortManager.getInstance().sendPacket(port, null, CORE_FEATURE_DISPLAY_CLEAR,
                CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
        CIMPortManager.getInstance().sendPacket(port, textWindowData, CORE_FEATURE_DISPLAY_SET_TEXT_WINDOW,
                CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);

        AstericsErrorHandling.instance.reportInfo(this, "Writing to display \"" + buf.toString() + "\"");

        CIMPortManager.getInstance().sendPacket(port, buf.toString().getBytes(), CORE_FEATURE_DISPLAY_PRINT,
                CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
    }

    /**
     * Reacts to pressing the navigate left button
     */
    public void handleLeftPress() {

        AstericsErrorHandling.instance.getLogger().fine("Left button press");
        menuHandler.handleLeft();
    }

    /**
     * Reacts to pressing the navigate right button
     */
    public void handleRightPress() {
        AstericsErrorHandling.instance.getLogger().fine("Right button press");
        menuHandler.handleRight();
    }

    /**
     * Reacts to pressing the navigate down button
     */
    public void handleDownPress() {
        AstericsErrorHandling.instance.getLogger().fine("Down button press");
        menuHandler.handleDown();
    }

    /**
     * Reacts to pressing the navigate up button
     */
    public void handleUpPress() {
        AstericsErrorHandling.instance.getLogger().fine("Up button press");
        menuHandler.handleUp();
    }

    /**
     * Reacts to pressing the ok button
     */
    public void handleOkPress() {
        AstericsErrorHandling.instance.getLogger().fine("Ok button press");
        menuHandler.handleOk();
    }

    /**
     * Reacts to incoming button presses
     */
    public void handleButton(int i) {
        displayActivity();
        switch (i) {
        case 0:
            handleLeftPress();
            break;
        case 1:
            handleRightPress();
            break;
        case 2:
            handleDownPress();
            break;
        case 3:
            handleUpPress();
            break;
        case 4:
            handleOkPress();
            break;
        default:
            break;
        }
    }

    /**
     * Implemented packet handler from CIM communication. Receives packets on
     * button input on the core CIM
     */
    @Override
    public void handlePacketReceived(CIMEvent e) {
        CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
        CIMProtocolPacket packet = ev.packet;

        if ((packet.getSerialNumber() & 0x80) != 0) {
            if (packet.getFeatureAddress() == 0x80) {
                byte b = packet.getData()[0];

                for (int i = 0; i < 5; i++) {
                    incomingButtonState[i] = ((b & (1 << i)) > 0) ? true : false;
                }

                for (int i = 0; i < 5; i++) {
                    if (buttonState[i] != incomingButtonState[i]) {
                        buttonState[i] = incomingButtonState[i];
                        if (buttonState[i]) {
                            handleButton(i);
                        }
                    }
                }
            }
        }
    }

    /**
     * Receives error notification messages on faulty packets
     */
    @Override
    public void handlePacketError(CIMEvent e) {
        AstericsErrorHandling.instance.reportDebugInfo(this, "Faulty packet received");
    }
}