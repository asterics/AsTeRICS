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

import java.io.IOException;
import java.util.ArrayList;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * ReaderThread: opening a connection to the FS20 Receiver and read the
 * messages. Fire an event, if a message has been received
 */

public class FS20Reader extends AbstractRuntimeComponentInstance implements Runnable {
    public ArrayList<FS20EventListener> listeners;

    private boolean threadDone = false;

    HIDDevice pce;

    public FS20Reader() {
        listeners = new ArrayList<FS20EventListener>();
    }

    public void addEventListener(FS20EventListener l) {
        listeners.add(l);
    }

    private void fireFS20Event(FS20ReaderEvent evt) {
        for (FS20EventListener l : listeners) {
            l.fs20EventOccurred(evt);
        }
    }

    public void done() {
        threadDone = true;
    }

    @Override
    public void run() {

        threadDone = false;
        try {
            pce = HIDManager.getInstance().openById(0x18EF, 0xE014, null);
            AstericsErrorHandling.instance.reportInfo(FS20Reader.this, "FS20 PCE Receiver device successfully opened");
            pce.disableBlocking();
            byte[] rcvBuffer = new byte[14];
            while (!threadDone) {
                int readed = pce.read(rcvBuffer);
                Thread.sleep(300);
                // int i = 0;
                // for (byte b : rcvBuffer)
                // System.out.println("Byte "+(i++) + ": " + b);
                if (readed > 0 && rcvBuffer[0] == 0x02 && rcvBuffer[1] == 0x0b) {
                    fireFS20Event(new FS20ReaderEvent(this, rcvBuffer));
                }
            }
        } catch (HIDDeviceNotFoundException nfe) {
            AstericsErrorHandling.instance.reportError(this, "Could not find FS20 PCE Device");
        } catch (IOException ioe) {
            AstericsErrorHandling.instance.reportError(this, "Error reading data from FS20 PCE Device");
        } catch (InterruptedException ie) {

        } catch (Exception ex) {
            AstericsErrorHandling.instance.reportError(this, "Could not find FS20 PCE Device");
        } finally {
            try {
                pce.close();
            } catch (Exception e) {
                // AstericsErrorHandling.instance.reportError(this, "Error
                // closing FS20 PCE Device");
            }
        }

    }

}
