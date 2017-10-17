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

package eu.asterics.component.actuator.fS20Sender;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;

import eu.asterics.mw.services.AstericsErrorHandling;

public class PCSDevice {

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private ScheduledExecutorService timerExecutorSend = null;
    private int vid = 0x18EF;
    private int pid = 0xE015;

    private HidDevice dev = null;

    public PCSDevice() {
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

    public int send(int houseCode, int addr, int command) {
        byte[] buf = new byte[11];
        buf[0] = 0x01; // hid report id
        buf[1] = 0x06; // byte anzahl
        buf[2] = (byte) 0xF1; // Befehl ID
        byte[] hc = FS20Utils.houseCodeToHex(houseCode);
        buf[3] = hc[0]; // 1111 HC
        buf[4] = hc[1]; // 1111 HC
        buf[5] = FS20Utils.addressToHex(addr); // 1111 Adresse
        buf[6] = (byte) command; // Befehl
        buf[7] = 0x00; // Erweiterung
        return sendToDevice(buf);
    }

    /**
     * sends bytes to the fs20-device. before sending, it is tried to reopen the
     * device, if it is null. if sending fails (returns -1) it is tried to
     * reopen the device and send the bytes again. if sending needs more than 1
     * second it is aborted and -1 is returned
     * 
     * @param bytes
     * @return number of bytes sent, -1 if sending failed
     */
    private int sendToDevice(byte[] bytes) {
        int result = -1;
        Callable<Integer> sendTask = new SendCallable(bytes);
        Future<Integer> handler = timerExecutorSend.submit(sendTask);
        try {
            result = handler.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            timerExecutorSend.shutdownNow();
            timerExecutorSend = Executors.newSingleThreadScheduledExecutor();
            logger.log(Level.WARNING, "sending to FS20 timed out!");
        }

        return result;
    }

    private class SendCallable implements Callable<Integer> {

        private byte[] bytes;

        public SendCallable(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public Integer call() throws Exception {
            int result = -1;
            if (dev == null) {
                open();
            }
            if (dev != null) {
                result = dev.write(bytes, bytes.length, (byte) 0);
                if (result < 0) {
                    open();
                    if (dev != null) {
                        result = dev.write(bytes, bytes.length, (byte) 0);
                    }
                }
            }
            return result;
        }
    }
}
