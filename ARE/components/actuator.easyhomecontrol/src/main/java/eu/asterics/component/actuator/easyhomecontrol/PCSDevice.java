package eu.asterics.component.actuator.easyhomecontrol;

import java.io.IOException;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

import eu.asterics.mw.services.AstericsErrorHandling;

public class PCSDevice {

	private int vid = 0x04D9;
	private int pid = 0x1357;
	
	private HIDDevice dev = null;
	
	public PCSDevice() {
	}
	
	public boolean open() {
		try {
			System.out.println("try vid pid");
			dev = HIDManager.openById(vid, pid, null);
			System.out.println("vid pid");
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
	
	public boolean send(int housecode, int addr, int command) {
		/*
		byte [] buf = new byte[11];
		buf[0] = 0x01; // hid report id
		buf[1] = 0x06; // byte anzahl
		buf[2] = (byte) 0xF1; // Befehl ID
		byte [] hc = FS20Utils.housecodeToHex(housecode);
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
		*/
		
		byte[] rfCmdBuf = new byte[32];
        byte i = 0;
        char[] buf = new char[8];

        for (i = 0; i < 8; i++) {
	        buf[i] = (char)(housecode % 3);
	        housecode /= 3;

	        switch (buf[i]) {
		        case 0:
			        buf[i] = 0;
			        break;
		        case 1:
			        buf[i] = 3;
			        break;
	        case 2:
            		        buf[i] = 1;
	                        break;
	        }
        }

        char addr2 = 0;//(char)(addr & 0xFF);
        for (i = 7; i >= 1; i--) {
            addr2 = (char)((addr2 << 2) | buf[i]);
        }

        addr2 = (char)((addr2 << 2) | buf[0]);

        byte sbuf = 0x14;
        if (command != 0)
	        sbuf |= 0x01;

        rfCmdBuf[0*8+0] = 0x01;
        // StartBit_HTime
        rfCmdBuf[0*8+1] = (byte) ((320 / 10) >> 8);
        rfCmdBuf[0*8+2] = (byte) (320 / 10);
        // StartBit_LTime
        rfCmdBuf[0*8+3] = (byte) ((9700 / 10) >> 8);
        rfCmdBuf[0*8+4] = (byte) ((9700 / 10) & (0xFF));
        // EndBit_HTime
        rfCmdBuf[0*8+5] = 0x00;
        rfCmdBuf[0*8+6] = 0x00;
        // EndBit_LTime
        rfCmdBuf[0*8+7] = 0x00;

        rfCmdBuf[1*8+0] = 0x02;
        // EndBit_LTime
        rfCmdBuf[1*8+1] = 0x00;
        // DataBit0_HTime
        rfCmdBuf[1*8+2] = (byte) (320 / 10);
        // DataBit0_LTime
        rfCmdBuf[1*8+3] = (byte) (960 / 10);
        // DataBit1_HTime
        rfCmdBuf[1*8+4] = (byte) (960 / 10);
        // DataBit1_LTime
        rfCmdBuf[1*8+5] = (byte) (320 / 10);
        // DataBit_Count
        rfCmdBuf[1*8+6] = (byte) 24;
        // Frame_Count
        rfCmdBuf[1*8+7] = (byte) 18;

        rfCmdBuf[2*8+0] = 0x03;
        rfCmdBuf[2*8+1] = (byte) (addr >> 8);
        rfCmdBuf[2*8+2] = (byte) addr;
        rfCmdBuf[2*8+3] = sbuf;
        rfCmdBuf[2*8+4] = 0x00;
        rfCmdBuf[2*8+5] = 0x00;
        rfCmdBuf[2*8+6] = 0x00;
        rfCmdBuf[2*8+7] = 0x00;

        rfCmdBuf[3*8+0] = 0x04;
        rfCmdBuf[3*8+1] = 0x00;
        rfCmdBuf[3*8+2] = 0x00;
        rfCmdBuf[3*8+3] = 0x00;
        rfCmdBuf[3*8+4] = 0x00;
        rfCmdBuf[3*8+5] = 0x00;
        rfCmdBuf[3*8+6] = 0x00;
        rfCmdBuf[3*8+7] = 0x00;
        
        byte [][] buffer = new byte[6][9];
        buffer[0][0]=0x01;
        buffer[0][1]=0x06; 
        buffer[0][2]=0x01;
        
        for (int j = 1; j <= 4; j++)
        {
        	buffer[j][0] = 0x01;
        	for (int k=1; k<=8; k++)
        	{
        		buffer[j][k] = rfCmdBuf[k-1+((j-1)*8)];
        	}
        }
        
        
        buffer[5][0]=0x01;
        buffer[5][1]=0x05;

			try {
				/*
				byte[] buffer = new byte[36];
				int countera=0;
				for(int k = 0;k<4;k++){
					buffer[countera]=0;
					for(int j = 1;j<9;j++)
					{
						buffer[j+countera]=rfCmdBuf[j+countera-(k+1)];
					}
					countera = countera + 9;
				}

			
				

				//buffer[0]= {0x06, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		
		
				//System.out.println(buffer);
				*/
				if (dev != null) {
					for (int l = 0;l<6;l++)
					{
						dev.write(buffer[l]);
					}
					
				}
				
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		
		return true;
	}
	
}
