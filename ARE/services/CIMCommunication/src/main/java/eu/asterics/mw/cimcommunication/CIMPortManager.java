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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IAREEventListener;
import eu.asterics.mw.systemstatechange.SystemChangeListener;
import eu.asterics.mw.systemstatechange.SystemChangeNotifier;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

/**
 * The CIMPortManager controls all the connections to CIMs on the AsTeRICS
 * platform. The class is implemented as a singleton and is the only way to
 * access the connected CIMs.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class CIMPortManager implements IAREEventListener, SystemChangeListener {

    final int AUTODETECT_WAIT_TIME = 3500;
    final int RESCAN_WAIT_TIME = 2500;

    static CIMPortManager instance = new CIMPortManager();
    Hashtable<CIMUniqueIdentifier, CIMPortController> comPorts;
    HashMap<String, CIMPortController> comRawPorts;
    CIMWirelessHubPortController wirelessHub = null;

    private final String IGNORE_PORT_FILNAME = "data/cimcommunication/ignore_ports.txt";
    private final String CIMID_FILENAME = "data/cimcommunication/cimids.txt";
    private List<String> ignoredComPorts = new ArrayList<String>();
    private HashMap<Long, String> cimIdToName = new HashMap<Long, String>();
    private Logger logger = null;
    // Indicate that new devices have been attached since the last rescan
    private boolean usbDevicesAttached = false;
    // Indicate that devices have been detached since the last rescan
    private boolean usbDevicesRemoved = false;
    private boolean modelRunning = false;

    /**
     * Sets up the port manager instance and scans for attached CIMs
     */
    private CIMPortManager() {
        logger = AstericsErrorHandling.instance.getLogger();
        // logger.fine("Constructing CIM Port Manager");

        comPorts = new Hashtable<CIMUniqueIdentifier, CIMPortController>();
        comRawPorts = new HashMap<String, CIMPortController>();
        generateIgnoredPortList();
        generateCIMDescriptionMap();

        rescan(AUTODETECT_WAIT_TIME);

        logger.fine("Scanning of ports successful, so register for ARE events, and systemstate changes");
        AREServices.instance.registerAREEventListener(this);
        SystemChangeNotifier.instance.addListener(this);

    }

    /**
     * Method to access the CIMPortManager singleton
     * 
     * @return the common port manager instance
     */
    public synchronized static CIMPortManager getInstance() {
        if (instance == null) {
            instance = new CIMPortManager();
        }
        return instance;
    }

    private void rescan() {
        rescan(RESCAN_WAIT_TIME);
    }

    /**
     * Scans the serial ports for newly attached CIMs
     */
    private void rescan(int timeout) {
        logger.info("Starting rescan of COM ports");
        boolean waitForResponses = false;

        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            boolean ignorePort = false;
            CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum.nextElement();
            if (!portIdentifier.isCurrentlyOwned()
                    && (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)) {
                logger.fine(this.getClass().getName()+".rescan: CIM scanning on port <" + portIdentifier.getName()+">");
                for (String s : ignoredComPorts) {
                    if (portIdentifier.getName().trim().equals(s.trim())) {
                        // this port be ignored, nothing has been opened so far
                        logger.fine("Ignoring COM port " + portIdentifier.getName());
                        ignorePort = true;
                    }
                }

                if (ignorePort) {
                    continue;
                }

                try {
                    CIMIdentifyPortController ctrl = new CIMIdentifyPortController(portIdentifier);
                    AstericsThreadPool.instance.execute(ctrl);

                    // logger.fine(this.getClass().getName()+".rescan: " +
                    // "CIM Port " + portIdentifier.getName() +
                    // " identifier thread started \n");
                    waitForResponses = true;

                    // added sleep before request of ID packet, for detection of
                    // HID CIM
                } catch (CIMException e) {
                    logger.warning(this.getClass().getName() + "." + "CIMPortManager: Could not create port controller "
                            + "on CIM Port " + portIdentifier.getName() + " thread started \n");
                }
            }
        }

        if (waitForResponses) {
            try {
                logger.fine(this.getClass().getName() + "." + "CIMPortManager: waiting for auto detect responses \n");
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        usbDevicesAttached = false;
        logger.info("Finished rescan of COM ports");
    }

    private void printActiveCimControllers() {
        StringBuffer buf = new StringBuffer();
        buf.append("Currently active COM ports:\n");
        for (CIMUniqueIdentifier cuid : comPorts.keySet()) {
            CIMPortController ctrl = comPorts.get(cuid);
            String descr = cimIdToName.get(((long) cuid.CIMId) & 0x0000ffff);
            if (descr != null) {
                buf.append("\t" + ctrl.comPortName + ":\t" + descr + ",\tUniqueNumber: "
                        + String.format("0x%x", cuid.CIMUniqueNumber) + "\n");
            }
        }

        if (wirelessHub != null) {
            buf.append(wirelessHub.getAvailableWirelessCIMsAsString());
        }
        logger.info(buf.toString());
    }

    String getDescriptionForCIMId(long l) {
        return cimIdToName.get(l);
    }

    private void stopCIMThreads() {
        logger.info("Begin: Stopping currently active COM ports:\n");
        printActiveCimControllers();
        Iterator<CIMUniqueIdentifier> it = comPorts.keySet().iterator();
        while (it.hasNext()) {
            CIMUniqueIdentifier cuid = it.next();
            if (cuid.CIMId != (short) 0x0602) {
                CIMPortController ctrl = comPorts.get(cuid);
                ctrl.closePort();
                it.remove();
            }
        }
        /*
         * for ( CIMUniqueIdentifier cuid : comPorts.keySet()) {
         * CIMPortController ctrl = comPorts.get(cuid); ctrl.closePort();
         * buf.append("  Stopped: " + ctrl.comPortName + "\n"); }
         */

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        usbDevicesRemoved = false;
        logger.info("End: Stopping currently active COM ports");
    }

    private void generateCIMDescriptionMap() {
        String lineInput = null;
        cimIdToName = new HashMap<Long, String>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(CIMID_FILENAME));

            while ((lineInput = in.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(lineInput, ",");
                if (tok.countTokens() == 2) {
                    Long cimId = Long.decode(tok.nextToken());
                    String descr = tok.nextToken();
                    cimIdToName.put(cimId, descr);
                }
            }
        } catch (IOException ioe) {
            logger.severe(this.getClass().getName() + "." + "generateIgnoredPortList: IO Exception while handling "
                    + "ignore ports file");
        }

    }

    /**
     * Reads the ignored ports file and sets up the internal list of ignored
     * components. The file is name "ignore_ports.txt" and should contain one
     * port name "COMx" per line. Ignoring ports is necessary as some computers
     * may have a port which echos all outputs and the user might not know how
     * to disable this feature.
     */
    private void generateIgnoredPortList() {
        // logger.fine(this.getClass().getName()+".generateIgnoredPortList: " +
        // "Reading port ignore list \n");
        ignoredComPorts = new ArrayList<String>();
        String lineInput = null;

        try {
            BufferedReader in = new BufferedReader(new FileReader(IGNORE_PORT_FILNAME));

            while ((lineInput = in.readLine()) != null) {
                int idx = lineInput.indexOf("COM") + 3;
                if (idx < 3) {
                    continue;
                }

                StringBuffer buf = new StringBuffer();
                buf.append("COM");
                for (; idx < lineInput.length(); idx++) {
                    char c = lineInput.charAt(idx);
                    if (Character.isDigit(c)) {
                        buf.append(c);
                    } else {
                        break;
                    }
                }

                if (buf.length() > 3) {
                    String s = buf.toString();
                    ignoredComPorts.add(s);
                    logger.fine(String.format("Adding port %s to ignored ports", s));
                }
            }

        } catch (IOException ioe) {
            logger.severe(this.getClass().getName() + "." + "generateIgnoredPortList: IO Exception while handling "
                    + "ignore ports file");
        }
    }

    /**
     * Adds a serial port controller for a certain CIM id to the list
     * 
     * @param cimId
     *            ID of the CIM
     * @param ctrl
     *            instance of the controller
     */
    synchronized void addConnection(CIMUniqueIdentifier cuid, CIMPortController ctrl) {
        if ((ctrl != null) && (comPorts.get(cuid) == null)) {
            comPorts.put(cuid, ctrl);
        }
    }

    /**
     * Removes a specified CIM from the list
     * 
     * @param cimId
     *            the ID of the CIM to be removed
     */
    synchronized void removeConnection(short cimId) {
        comPorts.remove(cimId);
    }

    /**
     * Removes a specified CIM from the list
     * 
     * @param cimId
     *            the ID of the CIM to be removed
     */
    synchronized void removeConnection(CIMUniqueIdentifier cuid) {
        comPorts.remove(cuid);
    }

    /**
     * Returns a connection to a specified CIM. This works for only one CIM of
     * one ID, as there is no way to identify the CIM's unique number.
     * 
     * @param cimId
     *            the ID of the requested CIM
     * @return the instance of the controller for the requested CIM
     */
    public CIMPortController getConnection(short cimId) {
        Set<CIMUniqueIdentifier> keys = comPorts.keySet();
        for (CIMUniqueIdentifier key : keys) {
            if (key.CIMId == cimId) {
                return comPorts.get(key);
            }
        }
        return null;
    }

    /**
     * Returns a connection to a specified CIM using the combination of the CIM
     * Id and the unique number of the CIM . At the moment this works for only
     * one CIM of one ID, as there is no way to identify the CIM's unique
     * number.
     * 
     * @param cimId
     *            the ID of the requested CIM
     * @param uniqueNumber
     *            the unique number of the CIM
     * @return the instance of the controller for the requested CIM
     */
    public CIMPortController getConnection(short cimId, long uniqueNumber) {
        return comPorts.get(new CIMUniqueIdentifier(cimId, uniqueNumber));
    }

    public CIMPortController getWirelessConnection(short cimId, long uniqueNumber) {
        if (wirelessHub != null) {
            return wirelessHub.getConnection(cimId, uniqueNumber);
        }
        return null;
    }

    /**
     * Opens a serial port at a certain baud rate. This is used to connect to
     * devices that use serial communication but do not adhere to the CIM
     * protocol
     * 
     * @param portName
     *            the name of the port the device is connected to
     * @param baudRate
     *            the baud rate of the connection
     * @return a controller instance of the opened connection, null on error
     */
    public CIMPortController getRawConnection(String portName, int baudRate) {
        return getRawConnection(portName, baudRate, false);
    }

    public CIMPortController getRawConnection(String portName, int baudRate, boolean highSpeed) {
        CIMPortController ret = comRawPorts.get(portName);
        if (ret == null) {
            CommPortIdentifier portIdentifier = null;
            try {
                portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
                if (portIdentifier.isCurrentlyOwned()) {
                    logger.severe(this.getClass().getName() + "." + "getRawConnection: Could not open raw port, "
                            + "port currently owned by " + portIdentifier.getCurrentOwner());
                    return null;
                }
            } catch (NoSuchPortException e) {
                logger.severe(this.getClass().getName() + "." + "getRawConnection: Could not open raw port, "
                        + "no port by the name of " + portName);
                return null;
            }

            if ((portIdentifier != null) && (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)) {
                try {
                    if (highSpeed) {
                        ret = new CIMHighSpeedRawPortController(portIdentifier, baudRate);
                    } else {
                        ret = new CIMRawPortController(portIdentifier, baudRate);
                        AstericsThreadPool.instance.execute((CIMRawPortController) ret);
                    }
                    comRawPorts.put(portName, ret);
                } catch (CIMException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return ret;
    }

    /**
     * Closes the serial connection on the specified port, if it exists. The
     * connection listeners have to be removed.
     * 
     * @param portName
     *            name of the port to be closed
     */
    public void closeRawConnection(String portName) {
        CIMPortController rpc = comRawPorts.remove(portName);
        if (rpc != null) {
            rpc.closePort();
        }
    }

    public int sendPacket(short cimId, byte[] data, short featureAddress, short requestCode, boolean crc) {
        Set<CIMUniqueIdentifier> keys = comPorts.keySet();
        for (CIMUniqueIdentifier key : keys) {
            if (key.CIMId == cimId) {
                CIMPortController ctrl = comPorts.get(key);
                return sendPacket(ctrl, data, featureAddress, requestCode, crc);
            }
        }
        return 0;
    }

    public int sendPacket(CIMUniqueIdentifier cuid, byte[] data, short featureAddress, short requestCode, boolean crc) {
        CIMPortController ctrl = comPorts.get(cuid);
        if (ctrl != null) {
            return sendPacket(ctrl, data, featureAddress, requestCode, crc);
        }
        return 0;
    }

    public int sendPacket(CIMPortController ctrl, byte[] data, short featureAddress, short requestCode, boolean crc) {
        if (ctrl != null) {
            return ctrl.sendPacket(data, featureAddress, requestCode, crc);
        }
        logger.severe(this.getClass().getName() + "."
                + "sendPacket: Could not find requested port, method called with ctrl=null");
        return -1;
    }

    public boolean hasMultipleCIMsWithSameIdConnected() {
        Vector<Short> cimIds = new Vector<Short>();
        Set<CIMUniqueIdentifier> keys = comPorts.keySet();
        for (CIMUniqueIdentifier key : keys) {
            if (cimIds.contains(key.CIMId)) {
                return true;
            }
            cimIds.add(key.CIMId);
        }
        return false;

    }

    /**
     * Checks whether there are multiple CIMs of one specified ID. Once called
     * method will search through all detected CIMs.
     * 
     * @param cimId
     *            the cimId to be checked for multiple CIMs
     * @return true if two or more CIMs of the specified ID are found, false
     *         otherwise.
     */
    public boolean hasMultipleCIMSofIdConnected(short cimId) {
        boolean alreadyFoundId = false;
        Set<CIMUniqueIdentifier> keys = comPorts.keySet();

        for (CIMUniqueIdentifier key : keys) {
            if (key.CIMId == cimId) {
                if (alreadyFoundId) {
                    return true;
                } else {
                    alreadyFoundId = true;
                }
            }
        }
        return false;
    }

    /**
     * Generates a Vector of all the unique identifier numbers of a certain CIM
     * id.
     * 
     * @param cimId
     *            the CIM Id of the desired CIM
     * @return a Vector instance holding the unique Ids of the CIMs or null if
     *         no CIM of that ID has been found.
     */
    public Vector<Long> getUniqueIdentifiersofCIMs(short cimId) {
        Vector<Long> vector = new Vector<Long>();
        Set<CIMUniqueIdentifier> keys = comPorts.keySet();

        for (CIMUniqueIdentifier key : keys) {
            if (key.CIMId == cimId) {
                vector.add(key.CIMUniqueNumber);
            }
        }

        if (vector.isEmpty()) {
            return null;
        }
        return vector;
    }

    @Override
    public void preDeployModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postDeployModel() {
        // TODO Auto-generated method stub

    }

    /**
     * Performs a CIM rescan if USB devices have been altered. Called before
     * start of model
     */
    @Override
    public void preStartModel() {
        logger.fine("Begin preStartModel: usbDevicesRemoved: " + usbDevicesRemoved + ", usbDevicesAttached: "
                + usbDevicesAttached);
        if (usbDevicesRemoved || usbDevicesAttached) {
            stopCIMThreads();
            // usbDevicesRemoved = false;
            rescan();
        }
        modelRunning = true;
        printActiveCimControllers();
        logger.fine("End preStartModel: usbDevicesRemoved: " + usbDevicesRemoved + ", usbDevicesAttached: "
                + usbDevicesAttached);
    }

    @Override
    public void postStartModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void preStopModel() {
        // TODO Auto-generated method stub

    }

    /**
     * Performs a CIM rescan if USB devices have been altered. Called after stop
     * of model
     */
    @Override
    public void postStopModel() {
        logger.fine("Begin postStopModel: usbDevicesRemoved: " + usbDevicesRemoved);
        if (usbDevicesRemoved) {
            stopCIMThreads();
            // usbDevicesRemoved = false;
            // rescan();
        }
        modelRunning = false;
        logger.fine("End postStopModel: usbDevicesRemoved: " + usbDevicesRemoved);
    }

    /**
     * Performs a rescan of the CIM ports. Called when USB devices are attached.
     */
    @Override
    public void usbDevicesAttached() {
        logger.fine("usbDevicesAttached callback");
        usbDevicesAttached = true;
        // rescan();
    }

    /**
     * Stops CIM threads if ARE is currently not running. Called when USB
     * devices are removed.
     */
    @Override
    public void usbDevicesRemoved() {
        // logger.fine("usbDevicesRemoved callback");
        usbDevicesRemoved = true;

        if (!modelRunning) {
            stopCIMThreads();
            // usbDevicesRemoved = false;
            // rescan();
        }

    }

    @Override
    public void prePauseModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postPauseModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void preResumeModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postResumeModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void preBundlesInstalled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postBundlesInstalled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAreError(String msg) {
        // TODO Auto-generated method stub

    }

    @Override
    public void systemSleepRequested() {
    }

    @Override
    public void systemSleep() {

    }

    @Override
    public void systemResume() {

    }

    public boolean addWirelessConnection(CIMWirelessHubPortController ctrl) {
        if (wirelessHub == null) {
            this.wirelessHub = ctrl;
            return true;
        }
        return false;
    }

    public Vector<Long> getUniqueIdentifiersofWirelessCIMs(short cimId) {
        if (wirelessHub != null) {
            return wirelessHub.getUniqueIdentifiersofWirelessCIMs(cimId);
        }
        return null;
    }

    public CIMPortController getWirelessConnection(short cimId) {
        if (wirelessHub != null) {
            return wirelessHub.getConnection(cimId);
        }
        return null;
    }

    static void uninitialize() {
        uninitialize();
        instance = null;
    }

}
