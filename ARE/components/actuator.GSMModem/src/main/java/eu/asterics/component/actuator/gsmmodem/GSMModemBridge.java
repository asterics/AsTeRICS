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

package eu.asterics.component.actuator.gsmmodem;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Interfaces the GSM modem library for the GSM modem plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 11, 2011 Time: 4:27:47
 *         PM
 */
public class GSMModemBridge implements Runnable {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("GSMModemLibrary");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"GSMModemLibrary.dll\" ... ok!");

        System.loadLibrary("GSMModemBridge");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"GSMModemBridge.dll\" ... ok!");

    }

    final IRuntimeComponentInstance componentInstance;
    final IRuntimeOutputPort opRemotePhoneID;
    final IRuntimeOutputPort opReceivedSMS;
    final IRuntimeOutputPort opErrorNumber;
    final IRuntimeEventTriggererPort etpNewSMS;
    final IRuntimeEventTriggererPort etpError;

    String propSerialPort = "";
    String propPin = "";
    String propSmsCenterID = "";
    String phoneID = "";
    String messageContent = "";

    /**
     * The class constructor.
     * 
     * @param componentInstance
     *            owner of this object.
     * @param opRemotePhoneID
     *            SMS sender out port
     * @param opReceivedSMS
     *            SMS content out port
     * @param opErrorNumber
     *            Error out port
     * @param etpNewSMS
     *            new SMS available output event port
     * @param etpError
     *            error output event port
     */
    public GSMModemBridge(final IRuntimeComponentInstance componentInstance, final IRuntimeOutputPort opRemotePhoneID,
            final IRuntimeOutputPort opReceivedSMS, final IRuntimeOutputPort opErrorNumber,
            final IRuntimeEventTriggererPort etpNewSMS, final IRuntimeEventTriggererPort etpError) {

        this.componentInstance = componentInstance;
        this.opRemotePhoneID = opRemotePhoneID;
        this.opReceivedSMS = opReceivedSMS;
        this.opErrorNumber = opErrorNumber;
        this.etpNewSMS = etpNewSMS;
        this.etpError = etpError;
    }

    boolean paused = true;

    /**
     * Starts the class.
     */
    public void start() {
        int result = activate(propSerialPort, propPin, propSmsCenterID);

        if (result < 0) {
            AstericsErrorHandling.instance.getLogger().warning("Modem activate error: " + Integer.toString(result));
            opErrorNumber.sendData(ConversionUtils.intToBytes(result));
            etpError.raiseEvent();
        }

        paused = false;
    }

    /**
     * Stop the class.
     */
    public void stop() {
        int result = close();
        if (result < 0) {
            AstericsErrorHandling.instance.getLogger().warning("Modem close error: " + Integer.toString(result));
            opErrorNumber.sendData(ConversionUtils.intToBytes(result));
            etpError.raiseEvent();
        }
        paused = true;
    }

    /**
     * Pause the class.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Resume the class.
     */
    public void resume() {
        paused = false;
    }

    /**
     * returns the serial port
     * 
     * @return serial port
     */
    String getPropSerialPort() {
        return propSerialPort;
    }

    /**
     * returns the SIM card PIN
     * 
     * @return SIM card PIN
     */
    String getPropPin() {
        return propPin;
    }

    /**
     * returns the SMS Center ID
     * 
     * @return SMS Center ID
     */
    String getPropSmsCenterID() {
        return propSmsCenterID;
    }

    /**
     * returns the phone ID
     * 
     * @return phone ID
     */
    String getPhoneID() {
        return phoneID;
    }

    /**
     * returns the message content
     * 
     * @return message content
     */
    String getMessageContent() {
        return messageContent;
    }

    /**
     * sets the serial port
     * 
     * @param propSerialPort
     *            serial port
     */
    void setPropSerialPort(String propSerialPort) {
        this.propSerialPort = propSerialPort;
    }

    /**
     * sets the SIM card PIN
     * 
     * @param propPin
     *            SIM card PIN
     */
    void setPropPin(String propPin) {
        this.propPin = propPin;
    }

    /**
     * sets the SMS Center ID
     * 
     * @param propSmsCenterID
     *            SMS Center ID
     */
    void setPropSmsCenterID(String propSmsCenterID) {
        this.propSmsCenterID = propSmsCenterID;
    }

    /**
     * sets the phone ID
     * 
     * @param phoneID
     *            phone ID
     */
    void setPhoneID(String phoneID) {
        this.phoneID = phoneID;
    }

    /**
     * sets the message content
     * 
     * @param messageContent
     *            message content
     */
    void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    private boolean sendSMSAction = false;
    private boolean busy = false;

    /**
     * Sends the SMS.
     */
    public void sendSMS() {
        if (paused == true) {
            AstericsErrorHandling.instance.getLogger().warning("Plugin is paused");
            return;
        }
        if (busy) {
            AstericsErrorHandling.instance.getLogger().warning("Plugin is busy");
            return;
        }
        busy = true;

        sendSMSAction = true;
        AstericsThreadPool.instance.execute(this);
    }

    /**
     * Thread function used for send SMS.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        if (sendSMSAction) {
            sendSMSAction = false;
            if ((phoneID.length() > 0) && (messageContent.length() > 0)) {
                AstericsErrorHandling.instance.getLogger().fine("Sending SMS: " + phoneID + " " + messageContent);
                int result = sendSMS(phoneID, messageContent);
                if (result < 0) {
                    if (result == -2013 || result == -5) {
                        AstericsErrorHandling.instance.getLogger()
                                .warning("Modem Send SMS error: " + Integer.toString(result) + " Modem not ready !!!");
                    } else {
                        AstericsErrorHandling.instance.getLogger()
                                .warning("Modem Send SMS error: " + Integer.toString(result));
                    }

                    opErrorNumber.sendData(ConversionUtils.intToBytes(result));
                    etpError.raiseEvent();
                }
            } else {
                AstericsErrorHandling.instance.getLogger().warning("Phone ID or message content is empty");
            }

        }
        busy = false;
    }

    /**
     * Activates the underlying native code/hardware.
     *
     * @param port
     *            modem serial port
     * @param pin
     *            SIM card PIN
     * @param smsCenterID
     *            SMS center ID
     * @return error code
     */
    native private int activate(String port, String pin, String smsCenterID);

    /**
     * Send SMS.
     *
     * @param phoneID
     *            is the recipient phone ID
     * @param content
     *            is the message content
     * @return error code
     */
    native private int sendSMS(String phoneID, String content);

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return error code
     */
    native private int close();

    /**
     * This method is called back from the native code when there is a new SMS.
     *
     * @param phoneID
     *            is the sender phone ID
     * @param content
     *            is the message content
     */
    private void newSMS_callback(String phoneID, String content) {
        if (paused == false) {
            AstericsErrorHandling.instance.getLogger().fine("Received SMS: " + phoneID + " " + content);
            opRemotePhoneID.sendData(ConversionUtils.stringToBytes(phoneID));
            opReceivedSMS.sendData(ConversionUtils.stringToBytes(content));
            etpNewSMS.raiseEvent();
        }

    }

    /**
     * This method is called back from the native code when there is found a new
     * error.
     *
     * @param code
     *            code of the error
     */
    private void error_callback(int code) {
        if (code < 0) {
            if (code == -4 || code == -10 || code == -11 || code == -12 || code == -13) {
                AstericsErrorHandling.instance.getLogger()
                        .warning("Error:  " + Integer.toString(code) + " Modem is not ready !!!");
            } else {
                AstericsErrorHandling.instance.getLogger().warning("Error:  " + Integer.toString(code));
            }
        } else {
            if (code == 2) {
                AstericsErrorHandling.instance.getLogger().fine("Modem is ready");
            }
        }
        if (paused == false) {
            if (code != 2) {
                opErrorNumber.sendData(ConversionUtils.intToBytes(code));
                etpError.raiseEvent();
            }
        }

    }
}