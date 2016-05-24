package eu.asterics.component.actuator.fS20Sender;

import java.io.IOException;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

import eu.asterics.mw.services.AstericsErrorHandling;

public class PCSDevice {

	private int vid = 0x18EF;
	private int pid = 0xE015;
	
	private HIDDevice dev = null;
	
	public PCSDevice() {
	}
	
	public boolean open() {
		try {			
			dev = HIDManager.getInstance().openById(0x18EF, 0xE015, null);			
		}catch (HIDDeviceNotFoundException e) {
			return false;
		} catch (IOException ioe) {
			return false;
		}
		return true;
	}

	
	public boolean close() {
		try {
			dev.close();
		} catch (IOException ioe) {
			return false;
		}
		return true;
	}
	
	public boolean send(int houseCode, int addr, int command) {
		byte [] buf = new byte[11];
		buf[0] = 0x01; // hid report id
		buf[1] = 0x06; // byte anzahl
		buf[2] = (byte) 0xF1; // Befehl ID
		byte [] hc = FS20Utils.houseCodeToHex(houseCode);
		buf[3] = hc[0]; // 1111 HC
		buf[4] = hc[1]; // 1111 HC
		buf[5] = FS20Utils.addressToHex(addr); // 1111 Adresse
		buf[6] = (byte) command; // Befehl
		buf[7] = 0x00; // Erweiterung
		try {
			if (dev != null) dev.write(buf);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return true;
	}
}
