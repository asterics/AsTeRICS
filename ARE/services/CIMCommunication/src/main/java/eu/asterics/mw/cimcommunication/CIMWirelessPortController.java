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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.cimcommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CIMWirelessPortController extends CIMPortController {

    CIMWirelessHubPortController hubCtrl;
    CIMUniqueIdentifier cuid;

    public CIMWirelessPortController(CIMUniqueIdentifier cuid, CIMWirelessHubPortController hubCtrl) {
        super(cuid.toIdentifierString());
        this.cuid = cuid;
        this.hubCtrl = hubCtrl;
        logger.fine("WirelessPortCtrl " + comPortName + " constructed");
    }

    @Override
    public void closePort() {
    }

    void receivePacket(byte[] d) {
        // System.out.println("WirelessPortCtrl "+ comPortName + " received:");
        // for (int i = 0; i < d.length; i++)
        // {
        // d[i] = din.readByte();
        // System.out.print(String.format("\tdata[%d]: %x ('%c')", i, d[i],
        // d[i]));
        // }

        for (CIMEventHandler e : eventHandlers) {
            e.handlePacketReceived(new CIMWirelessDataEvent(this, d));
        }
    }

    @Override
    byte sendPacket(byte[] data, short featureAddress, short requestCode, boolean crc) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        LEDataOutputStream dos = new LEDataOutputStream(bos);

        try {
            dos.writeInt((int) cuid.CIMUniqueNumber);
            dos.writeShort(cuid.CIMId);
            dos.writeShort(data.length);
            dos.write(data);
            dos.flush();
            dos.close();
            bos.close();
            return hubCtrl.sendPacket(bos.toByteArray(), featureAddress, requestCode, crc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // byte [] data = new byte[8 + d.length];
        return 0;
    }

}
