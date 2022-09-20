
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

package eu.asterics.component.processor.oscgesturefollower;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.sciss.net.OSCBundle;
import de.sciss.net.OSCChannel;
import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author Luka Samardzija [luka.samardzija@technikum-wien.at] Date: Sep 10,2012
 *         Time: 09:44:55 AM
 */
public class OscGestureFollowerInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opLikeliest = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propInPort = 8000;
    int propOutPort = 9000;
    // declare member variables here

    float outchannel1;

    static boolean oscgesturefollowerStarted = false;

    static OscGestureFollowerInstance instance;

    Object sync = new Object();
    OSCClient c = null;
    OSCServer s;

    OSCBundle bndl1, bndl2, bndl3, bndl4;
    OSCBundle bndlData;

    float outdataA, outdataB, outdataC, outdataD;
    String propFilename = "nodding.mubu";

    private double inA = 0, inB = 0, inC = 0, inD = 0;

    /**
     * The class constructor.
     */
    public OscGestureFollowerInstance() {
        instance = this;
        // empty constructor
    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("cH1".equalsIgnoreCase(portID)) {
            return ipCH1;
        }
        if ("cH2".equalsIgnoreCase(portID)) {
            return ipCH2;
        }
        if ("cH3".equalsIgnoreCase(portID)) {
            return ipCH3;
        }
        if ("cH4".equalsIgnoreCase(portID)) {
            return ipCH4;
        }

        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("likeliest".equalsIgnoreCase(portID)) {
            return opLikeliest;
        }

        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("stop".equalsIgnoreCase(eventPortID)) {
            return elpStop;
        }
        if ("stoplearn".equalsIgnoreCase(eventPortID)) {
            return elpStoplearn;
        }
        if ("learn1".equalsIgnoreCase(eventPortID)) {
            return elpLearn1;
        }
        if ("learn2".equalsIgnoreCase(eventPortID)) {
            return elpLearn2;
        }
        if ("learn3".equalsIgnoreCase(eventPortID)) {
            return elpLearn3;
        }
        if ("learn4".equalsIgnoreCase(eventPortID)) {
            return elpLearn4;
        }
        if ("learn5".equalsIgnoreCase(eventPortID)) {
            return elpLearn5;
        }
        if ("clear".equalsIgnoreCase(eventPortID)) {
            return elpClear;
        }
        if ("follow".equalsIgnoreCase(eventPortID)) {
            return elpFollow;
        }
        if ("load".equalsIgnoreCase(eventPortID)) {
            return elpLoad;
        }
        if ("save".equalsIgnoreCase(eventPortID)) {
            return elpSave;
        }

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {

        return null;
    }

    /**
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {

        if ("inport".equalsIgnoreCase(propertyName)) {
            return propInPort;
        }

        if ("outport".equalsIgnoreCase(propertyName)) {
            return propOutPort;
        }

        if ("filename".equalsIgnoreCase(propertyName)) {
            return propFilename;
        }

        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {

        if ("inport".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInPort;
            propInPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        if ("outport".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOutPort;
            propOutPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        if ("filename".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFilename;
            propFilename = newValue.toString();
            return oldValue;
        }
        return null;
    }

    /**
     * send data to gfOSC
     */

    synchronized private void sendOscMsg(String address, String command) {

        // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
        // "SendOscMsg1");
        bndlData = new OSCBundle(System.currentTimeMillis());
        bndlData.addPacket(new OSCMessage(address, new Object[] { command }));
        try {
            OscGestureFollowerInstance.instance.c.send(bndlData);
            synchronized (OscGestureFollowerInstance.instance.sync) {
                OscGestureFollowerInstance.instance.sync.notifyAll();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    synchronized private void sendOscMsg(String address, String command, int value) {

        // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
        // "SendOscMsg2");
        bndlData = new OSCBundle(System.currentTimeMillis());
        bndlData.addPacket(new OSCMessage(address, new Object[] { command, new Integer(value) }));
        try {
            OscGestureFollowerInstance.instance.c.send(bndlData);
            synchronized (OscGestureFollowerInstance.instance.sync) {
                OscGestureFollowerInstance.instance.sync.notifyAll();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    synchronized private void sendOscMsg(String address, String command, String value) {

        // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
        // "SendOscMsg3");
        bndlData = new OSCBundle(System.currentTimeMillis());
        bndlData.addPacket(new OSCMessage(address, new Object[] { command, value }));
        try {
            OscGestureFollowerInstance.instance.c.send(bndlData);
            synchronized (OscGestureFollowerInstance.instance.sync) {
                OscGestureFollowerInstance.instance.sync.notifyAll();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    /**
     * send data to gfOSC
     */
    synchronized private void sendOutput() {
        if (OscGestureFollowerInstance.instance.c == null) {
            return;
        }

        if (OscGestureFollowerInstance.instance.c.isConnected()) {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "Send_output");
            bndlData = new OSCBundle(System.currentTimeMillis());
            bndlData.addPacket(new OSCMessage("/data", new Object[] { new Float(outdataA), new Float(outdataB),
                    new Float(outdataC), new Float(outdataD) }));
            try {
                OscGestureFollowerInstance.instance.c.send(bndlData);
                synchronized (OscGestureFollowerInstance.instance.sync) {
                    OscGestureFollowerInstance.instance.sync.notifyAll();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipCH1 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inA = ConversionUtils.doubleFromBytes(data);
            outdataA = (float) inA;
            sendOutput();
        }
    };
    private final IRuntimeInputPort ipCH2 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inB = ConversionUtils.doubleFromBytes(data);
            outdataB = (float) inB;
            sendOutput();
        }
    };
    private final IRuntimeInputPort ipCH3 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inC = ConversionUtils.doubleFromBytes(data);
            outdataC = (float) inC;
            sendOutput();
        }
    };
    private final IRuntimeInputPort ipCH4 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inD = ConversionUtils.doubleFromBytes(data);
            outdataD = (float) inD;
            sendOutput();
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpLoad = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "Stop");
            sendOscMsg("/parameters", "readall", propFilename);
        }
    };
    final IRuntimeEventListenerPort elpSave = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "Stop");
            sendOscMsg("/parameters", "writeall", propFilename);
        }
    };

    final IRuntimeEventListenerPort elpStop = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "Stop");
            sendOscMsg("/parameters", "stop");
        }
    };
    final IRuntimeEventListenerPort elpStoplearn = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "Stop_learn");
            sendOscMsg("/parameters", "learn", 0);
        }
    };
    final IRuntimeEventListenerPort elpLearn1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "learn", 1);
        }
    };
    final IRuntimeEventListenerPort elpLearn2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "learn", 2);
        }
    };
    final IRuntimeEventListenerPort elpLearn3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "learn", 3);
        }
    };
    final IRuntimeEventListenerPort elpLearn4 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "learn", 4);
        }
    };
    final IRuntimeEventListenerPort elpLearn5 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "learn", 5);
        }
    };
    final IRuntimeEventListenerPort elpClear = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "clear", 0);
            sendOscMsg("/parameters", "clear", 1);
            sendOscMsg("/parameters", "clear", 2);
            sendOscMsg("/parameters", "clear", 3);
            sendOscMsg("/parameters", "clear", 4);
            sendOscMsg("/parameters", "clear", 5);
        }
    };
    final IRuntimeEventListenerPort elpFollow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendOscMsg("/parameters", "follow");
        }
    };

    /**
     * Returns the filenames inside the path folder data/music and data/sounds
     */
    @Override
    public List<String> getRuntimePropertyList(String key) {

        List<String> res = new ArrayList<String>();
        if (key.compareToIgnoreCase("filename") == 0) {
            List<String> nextDir = new ArrayList<String>(); // Directories
            nextDir.add("tools/GestureFollower");
            while (nextDir.size() > 0) {
                File pathName = new File(nextDir.get(0));
                String[] fileNames = pathName.list(); // lists all files in the
                                                      // directory

                for (int i = 0; i < fileNames.length; i++) {
                    if (fileNames[i].contains(".mubu")) {
                        res.add(fileNames[i]);
                        /*
                         * File f = new File(pathName.getPath(), fileNames[i]);
                         * // getPath converts abstract path to path in String,
                         * // constructor creates new File object with fileName
                         * name if (f.isDirectory()) { //
                         * nextDir.add(f.getPath()); } else {
                         * res.add(f.getPath()); }
                         */
                    }
                }
                nextDir.remove(0);
            }
        }
        return res;

    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        launchNow();
        client(OSCChannel.UDP);
        server(OSCChannel.UDP);
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        try {
            OscGestureFollowerInstance.instance.c.stop();
        } catch (IOException e1) {
        }
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        try {
            OscGestureFollowerInstance.instance.c.start();
        } catch (IOException e1) {
        }
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        closeNow();
        if (OscGestureFollowerInstance.instance.c != null) {
            try {
                OscGestureFollowerInstance.instance.c.stop();
            } catch (IOException e1) {
            }
            OscGestureFollowerInstance.instance.c.dispose();
            OscGestureFollowerInstance.instance.c = null;
        }
        super.stop();
    }

    public static void client(String protocol) {
        // postln( "NetUtilTest.client( \"" + protocol + "\" )\n" );
        // postln( "talking to localhost port 57110" );

        try {
            OscGestureFollowerInstance.instance.c = OSCClient.newUsing(protocol);
            OscGestureFollowerInstance.instance.c.setTarget(
                    new InetSocketAddress(InetAddress.getLocalHost(), OscGestureFollowerInstance.instance.propOutPort));
            // OscGestureFollowerInstance.instance.c.setTarget( new
            // InetSocketAddress( InetAddress.getLocalHost(), 9000 ));
            // postln( " start()" );
            OscGestureFollowerInstance.instance.c.start();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        OscGestureFollowerInstance.instance.c.addOSCListener(new OSCListener() {
            @Override
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
                if (m.getName().equals("/likeliest")) {

                    AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance, "Likeliest_juhu");
                    // OscGestureFollowerInstance.instance.opLikeliest.sendData(
                    // ConversionUtils.doubleToBytes((float) m.getArg(0)));
                    // OscGestureFollowerInstance.instance.opLikeliest.sendData(ConversionUtils.doubleToBytes(OscGestureFollowerInstance.instance.testput));

                    synchronized (OscGestureFollowerInstance.instance.sync) {
                        OscGestureFollowerInstance.instance.sync.notifyAll();
                    }
                }
            }
        });

    }

    public static void server(String protocol) {

        // postln( "NetUtilTest.server( \"" + protocol + "\" )\n" );
        // postln( "listening at port 57110. recognized commands: /pause, /quit,
        // /dumpOSC" );

        try {
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // "start server");
            OscGestureFollowerInstance.instance.s = OSCServer.newUsing(protocol,
                    OscGestureFollowerInstance.instance.propInPort);
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // String.format( "server addr "+localAddress ));
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // String.valueOf(InetAddress.getLocalHost().getHostAddress()));
            // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
            // String.valueOf(localAddress);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
        // "add Listener");
        OscGestureFollowerInstance.instance.s.addOSCListener(new OSCListener() {
            @Override
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {

                // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
                // String.format("get from" +addr));

                // if(m.getName().contains("foo"))
                // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
                // "get_foo");
                // else

                if (m.getName().equals("/likeliest")) {
                    OscGestureFollowerInstance.instance.opLikeliest
                            .sendData(ConversionUtils.doubleToBytes((float) m.getArg(0)));
                    // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
                    // "likeliest");

                    synchronized (OscGestureFollowerInstance.instance.sync) {
                        OscGestureFollowerInstance.instance.sync.notifyAll();
                    }
                }

            }

        });
        try {
            OscGestureFollowerInstance.instance.s.start();
        } catch (IOException e3) {
        }
        // AstericsErrorHandling.instance.reportInfo(OscGestureFollowerInstance.instance,
        // "foo");

    }

    @Override
    public void syncedValuesReceived(HashMap<String, byte[]> dataRow) {

        for (String s : dataRow.keySet()) {

            byte[] data = dataRow.get(s);
            if (s.equals("CH1")) {

                outdataA = (float) ConversionUtils.doubleFromBytes(data);
            } else if (s.equals("CH2")) {
                outdataB = (float) ConversionUtils.doubleFromBytes(data);
            } else if (s.equals("CH3")) {
                outdataC = (float) ConversionUtils.doubleFromBytes(data);
            } else if (s.equals("CH4")) {
                outdataD = (float) ConversionUtils.doubleFromBytes(data);
            }
            // AstericsErrorHandling.instance.reportInfo(OscClientInstance.instance,
            // "syncronized");

        }
        sendOutput();
    }

    String gfApplication = "tools\\GestureFollower\\gfOSC_v1.4.exe";
    String propWorkingDirectory = ".";
    boolean propAutoLaunch = false;
    boolean propAutoClose = true;
    boolean propOnlyByEvent = false;

    // declare member variables here
    Process process = null;
    boolean processStarted = false;

    private final void launchNow() {
        if (processStarted == true) {
            closeNow();
        }
        try {
            List<String> command = new ArrayList<String>();

            command.add(gfApplication);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.environment();
            builder.directory(new File(propWorkingDirectory));

            process = builder.start();
            processStarted = true;
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this, "IOException: problem starting " + gfApplication);
        } catch (IllegalArgumentException e) {
            AstericsErrorHandling.instance.reportError(this, "IllegalArgument: problem starting " + gfApplication);
        }
    }

    private final void closeNow() {
        if (process != null) {
            // System.out.println("closing Process");

            process.destroy();
            process = null;
        }
        processStarted = false;
    }

}