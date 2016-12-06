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

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import eu.asterics.mw.services.AstericsErrorHandling;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PCSDevice {

  private static final String REGISTRY_PATH_DEVICE_PARAMS = "Device Parameters";
  private static final String REGISTRY_PATH_FS20 = "SYSTEM\\CurrentControlSet\\Enum\\USB\\VID_18EF&PID_E015";
  private static final String REGISTRY_KEY_POWERMANAGEMENT = "EnhancedPowerManagementEnabled";

  private Logger logger = AstericsErrorHandling.instance.getLogger();
  private int vid = 0x18EF;
  private int pid = 0xE015;

  private HidDevice dev = null;

  public PCSDevice() {
  }

  public boolean open() {

    if (isWindowsOS()) {
      logger.info("trying to patch registry for FS20 to disable power-save-mode...");
      patchRegistryDisablePowerSaveMode();
    }
    List<HidDevice> list = HidManager.getHidServices().getAttachedHidDevices();
    for (HidDevice device : list) {
      if (device.getVendorId() == (short) vid && device.getProductId() == (short) pid) {
        dev = device;
      }
    }
    dev.open();
    return dev.isOpen();
  }


  public boolean close() {
    HidManager.getHidServices().shutdown();
    dev.close();
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
    if (dev != null) dev.write(buf, buf.length, (byte) 0);
    return true;
  }

  private boolean isWindowsOS() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  private boolean patchRegistryDisablePowerSaveMode() {
    boolean patched = false;
    try {
      if (Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20)) {
        String[] subkeys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20);
        for (String subkey : subkeys) {
          Advapi32Util.registrySetIntValue(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20 + "\\" + subkey + "\\" + REGISTRY_PATH_DEVICE_PARAMS, REGISTRY_KEY_POWERMANAGEMENT, 0);
          patched = true;
        }
      }
    } catch (Exception e) {
      logger.log(Level.WARNING, "error patching registry.", e);
    }
    return patched;
  }
}
