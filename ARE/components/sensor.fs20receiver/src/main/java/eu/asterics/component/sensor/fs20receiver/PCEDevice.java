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

package eu.asterics.component.sensor.fs20receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.services.AstericsErrorHandling;

public class PCEDevice extends AbstractRuntimeComponentInstance implements Runnable{

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private ScheduledExecutorService timerExecutorSend = null;
    private int vid = 0x18EF;
    private int pid = 0xE014;
    public ArrayList<FS20EventListener> listeners;
    private boolean threadDone = false;

    private HidDevice dev = null;

    public PCEDevice() {
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

    public boolean open() {

        timerExecutorSend = Executors.newSingleThreadScheduledExecutor();
        if (dev != null) {
            dev.close();
        }
        List<HidDevice> list = HidManager.getHidServices().getAttachedHidDevices();
        for (HidDevice device : list) {
            if (device.getVendorId() == (short) vid && device.getProductId() == (short) pid) {
                dev = device;
            }
        }
        if (dev == null) {
            return false;
        }
        dev.open();
        return dev.isOpen();
    }

    public boolean close() {
        HidManager.getHidServices().shutdown();
        timerExecutorSend.shutdownNow();
        if (dev != null) {
            dev.close();
        }
        return true;
    }
    

    @Override
    public void run() {

        threadDone = false;
        try {
        	if(open() == false) {
        		AstericsErrorHandling.instance.reportError(this, "Could not find FS20 PCE Device");
        		return;
        	}
        	
            AstericsErrorHandling.instance.reportInfo(PCEDevice.this, "FS20 PCE Receiver device successfully opened");
            dev.setNonBlocking(true);
            
            byte[] rcvBuffer = new byte[14];
            while (!threadDone) {
                int readed = dev.read(rcvBuffer);
                Thread.sleep(300);
                // int i = 0;
                // for (byte b : rcvBuffer)
                // System.out.println("Byte "+(i++) + ": " + b);
                if (readed > 0 && rcvBuffer[0] == 0x02 && rcvBuffer[1] == 0x0b) {
                    fireFS20Event(new FS20ReaderEvent(this, rcvBuffer));
                }
            }
        } catch (Exception ex) {
            AstericsErrorHandling.instance.reportError(this, "Could not find FS20 PCE Device");
        } finally {
            try {
                dev.close();
            } catch (Exception e) {
                // AstericsErrorHandling.instance.reportError(this, "Error
                // closing FS20 PCE Device");
            }
        }

    }
}
