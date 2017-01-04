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

package eu.asterics.mw.cimcommunication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import gnu.io.SerialPort;

/**
 * The CIMSerialPortController communicates with one CIM attached to the
 * platform. The controller is running in its own thread and permanently
 * listening for new data from the serial port.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
class CIMWirelessHubPortController extends CIMSerialPortController implements Runnable, CIMEventHandler {
    List<CIMEventHandler> zigbeeEventHandlers = new LinkedList<CIMEventHandler>();

    CIMWirelessHubPortController(String comPortName, SerialPort port, CIMPortEventListener listener) {
        super(comPortName, port, listener);
        super.addEventListener(this);

        logger.finest("CIMWirelessHubPortController constructed");
    }

    @Override
    public void addEventListener(CIMEventHandler handler) {
        synchronized (zigbeeEventHandlers) {
            if (!zigbeeEventHandlers.contains(handler)) {
                // logger.fine(this.getClass().getName()+".addEventListener:
                // adding listener:" + handler + "on port " + comPortName);
                zigbeeEventHandlers.add(handler);
            }
        }
    }

    HashMap<CIMUniqueIdentifier, CIMWirelessPortController> zigbeeControllers = new HashMap<CIMUniqueIdentifier, CIMWirelessPortController>();
    List<CIMUniqueIdentifier> activeZigbeeControllers = new ArrayList<CIMUniqueIdentifier>();
    // Hashtable<CIMUniqueIdentifier, CIMPortController> comPorts;

    @Override
    public void handlePacketReceived(CIMEvent e) {

        CIMProtocolPacket packet = ((CIMEventPacketReceived) e).packet;
        byte[] data = packet.getData();

        switch (packet.featureAddress) {
        case 0x92:
            try {
                LEDataInputStream din = new LEDataInputStream(new ByteArrayInputStream(data));
                int nb = din.readShort();
                int uid = -1;
                short id = -1;
                StringBuffer buf = new StringBuffer();
                buf.append("Paired Zigbee CIMs:\n");
                buf.append("\tnb: ").append(nb).append("\n");

                for (int i = 0; i < nb; i++) {
                    uid = din.readInt();
                    id = din.readShort();
                    CIMUniqueIdentifier cuid = new CIMUniqueIdentifier(id, uid);
                    if (!zigbeeControllers.containsKey(cuid)) {
                        zigbeeControllers.put(cuid, new CIMWirelessPortController(cuid, this));
                    }
                    buf.append("\t\tUniqueNb: ").append(String.format("%x", uid)).append("\tCIMId: ")
                            .append(String.format("%x\n", id));
                }
                logger.info(buf.toString());

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            break;
        case 0x93:
            try {
                LEDataInputStream din = new LEDataInputStream(new ByteArrayInputStream(data));
                int nb = din.readShort();
                int uid = -1;
                short id = -1;
                StringBuffer buf = new StringBuffer();
                buf.append("Active Zigbee CIMs:\n");
                buf.append("\tnb: ").append(nb);

                for (int i = 0; i < nb; i++) {
                    uid = din.readInt();
                    id = din.readShort();
                    CIMUniqueIdentifier cuid = new CIMUniqueIdentifier(id, uid);
                    if (!activeZigbeeControllers.contains(cuid)) {
                        activeZigbeeControllers.add(cuid);
                    }
                    buf.append("\t\tUniqueNb: ").append(String.format("%x", uid)).append("\tCIMId: ")
                            .append(String.format("%x\n", id));
                }
                // logger.info(buf.toString());

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            break;
        case 0x95:
            LEDataInputStream din = new LEDataInputStream(new ByteArrayInputStream(data));
            try {
                int uid = din.readInt();
                short id = din.readShort();
                int lg = din.readShort();
                byte[] d = new byte[lg];
                // System.out.println(String.format("uid: %x, id: %x, lg: %x",
                // uid, id, lg));
                for (int i = 0; i < lg; i++) {
                    d[i] = din.readByte();
                }

                CIMUniqueIdentifier cuid = new CIMUniqueIdentifier(id, uid);
                CIMWirelessPortController ctrl = zigbeeControllers.get(cuid);
                if (ctrl != null) {
                    ctrl.receivePacket(d);
                } else {
                    logger.warning("Addressed controller is not pair with wireless CIM");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            break;
        }
    }

    @Override
    public void handlePacketError(CIMEvent e) {
        // TODO Auto-generated method stub
    }

    CIMPortController getConnection(short cimId, long uniqueNumber) {
        return zigbeeControllers.get(new CIMUniqueIdentifier(cimId, uniqueNumber));
    }

    public Vector<Long> getUniqueIdentifiersofWirelessCIMs(short gpioCimId) {
        Vector<Long> vector = new Vector<Long>();
        Set<CIMUniqueIdentifier> keys = zigbeeControllers.keySet();

        for (CIMUniqueIdentifier key : keys) {
            // System.out.println(String.format("key.CIMId: 0x%x, cimId: 0x%x",
            // key.CIMId, gpioCimId));
            if (key.CIMId == gpioCimId) {
                vector.add(key.CIMUniqueNumber);
            }
        }

        if (vector.isEmpty()) {
            return null;
        }
        return vector;
    }

    @Override
    public void run() {
        sendPacket(null, (short) 0x92, CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
        // sendPacket(null, (short) 0x90,
        // CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
        super.run();
    }

    public CIMPortController getConnection(short cimId) {
        // TODO Auto-generated method stub

        Set<CIMUniqueIdentifier> keys = zigbeeControllers.keySet();
        for (CIMUniqueIdentifier key : keys) {
            if (key.CIMId == cimId) {
                return zigbeeControllers.get(key);
            }
        }
        return null;
    }

    public String getAvailableWirelessCIMsAsString() {
        StringBuffer buf = new StringBuffer();
        String descr = CIMPortManager.getInstance().getDescriptionForCIMId(((long) cuid.CIMId) & 0x0000ffff);
        // String descr = cimIdToName.get(((long) cuid.CIMId) & 0x0000ffff);
        if (descr != null) {
            buf.append("\t" + comPortName + ":\t" + descr + ",\tUniqueNumber: "
                    + String.format("0x%x", cuid.CIMUniqueNumber) + "\n");

            buf.append("\t").append(zigbeeControllers.size()).append(" paired CIMs:\n");
            for (CIMUniqueIdentifier key : zigbeeControllers.keySet()) {
                descr = CIMPortManager.getInstance().getDescriptionForCIMId(((long) key.CIMId) & 0x0000ffff);
                if (descr != null) {
                    buf.append(
                            "\t\t" + descr + ",\tUniqueNumber: " + String.format("0x%x", key.CIMUniqueNumber) + "\n");
                }
            }
        }
        return buf.toString();
    }
}
