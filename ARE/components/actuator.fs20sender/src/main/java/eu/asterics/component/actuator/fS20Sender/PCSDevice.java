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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import eu.asterics.mw.services.AstericsErrorHandling;

public class PCSDevice {

    private static final String REGISTRY_PATH_DEVICE_PARAMS = "Device Parameters";
    private static final String REGISTRY_PATH_FS20 = "SYSTEM\\CurrentControlSet\\Enum\\USB\\VID_18EF&PID_E015";
    private static final String REGISTRY_KEY_POWERMANAGEMENT = "EnhancedPowerManagementEnabled";
    private static final String FILENAME_REGPATCH = "regpatchfs20.vbs";
    private static final String FILENAME_REGPATCH2 = "regpatchfs20.cmd";
    private static final String PATH_SEPARATOR = "\\";

    private Logger logger = AstericsErrorHandling.instance.getLogger();
    private ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();
    private int vid = 0x18EF;
    private int pid = 0xE015;

    private HidDevice dev = null;

    public PCSDevice() {
    }

    public boolean open() {

        if (dev != null) {
            dev.close();
        }
        if (isWindowsOS()) {
            logger.info("trying to patch registry for FS20 to disable power-save-mode...");
            boolean patched = patchRegistryDisablePowerSaveMode();
            if (patched) {
                logger.info("successfully patched registry for FS20.");
            } else {
                logger.info("Registry for FS20 not patched (maybe not needed).");
            }
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
        timerExecutor.shutdownNow();
        if (dev != null) {
            dev.close();
        }
        return true;
    }

    public boolean send(int houseCode, int addr, int command) {
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
        sendToDevice(buf);
        return true;
    }

    /**
     * sends bytes to the fs20-device before sending it is tried to reopen the
     * device, if it is null. if sending fails (returns -1) it is tried to
     * reopen the device and send the bytes again
     * 
     * @param bytes
     * @return number of bytes sent, -1 if sending failed
     */
    private int sendToDevice(byte[] bytes) {
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

    private boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private boolean patchRegistryDisablePowerSaveMode() {
        boolean patched = false;
        try {
            if (Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20)) {
                String[] subkeys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20);
                if (!allSubkeysPatched(subkeys)) {
                    copyBatchFromJar(FILENAME_REGPATCH);
                    copyBatchFromJar(FILENAME_REGPATCH2);
                    ProcessBuilder builder = new ProcessBuilder("cscript", "/E:JScript", "/nologo", FILENAME_REGPATCH,
                            getArgString(subkeys));
                    builder.directory(new File(getHomePath()));
                    Process process = builder.start();
                    patched = process.waitFor() == 0;
                    deleteBatchFile(FILENAME_REGPATCH);
                    // second patch-script cannot be removed immediately,
                    // because maybe it is still executing, so wait 500ms
                    Runnable cleanupTask = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                deleteBatchFile(FILENAME_REGPATCH2);
                            } catch (IOException e) {
                                logger.log(Level.INFO,
                                        "could not remove patch-script after patching registry for FS20.", e);
                            }
                        }
                    };
                    timerExecutor.schedule(cleanupTask, 500, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "error patching registry.", e);
        }
        return patched;
    }

    private String getArgString(String[] strings) {
        String args = "";
        for (String key : strings) {
            args += " \"" + key + "\"";
        }
        return args.trim();
    }

    private void copyBatchFromJar(String batchFilename) throws IOException {
        ClassLoader loader = PCSDevice.class.getClassLoader();
        InputStream resource = loader.getResourceAsStream(batchFilename);
        Files.copy(resource, Paths.get(getHomePath() + "\\" + batchFilename), StandardCopyOption.REPLACE_EXISTING);
        resource.close();
    }

    private void deleteBatchFile(String batchFilename) throws IOException {
        new File(getHomePath() + PATH_SEPARATOR + batchFilename).delete();
    }

    private String getHomePath() {
        return System.getProperty("user.dir");
    }

    private boolean allSubkeysPatched(String[] subkeys) {
        for (String subkey : subkeys) {
            int value = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE,
                    REGISTRY_PATH_FS20 + "\\" + subkey + "\\" + REGISTRY_PATH_DEVICE_PARAMS,
                    REGISTRY_KEY_POWERMANAGEMENT);
            if (value == 1) {
                return false;
            }
        }
        return true;
    }
}
