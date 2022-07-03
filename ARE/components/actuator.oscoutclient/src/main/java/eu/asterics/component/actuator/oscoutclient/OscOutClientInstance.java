
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

package eu.asterics.component.actuator.oscoutclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import de.sciss.net.OSCBundle;
import de.sciss.net.OSCChannel;
import de.sciss.net.OSCClient;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class OscOutClientInstance extends AbstractRuntimeComponentInstance {
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpTriggerOne = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propPort = 57110;
    String propPeerAddress = "127.0.0.1";
    String propAddressCh1 = "/channel1";
    String propAddressStringCh = "/AddressStringCh";
    // String propAddressCh3 = "/channel3";
    // String propAddressCh4 = "/channel4";

    // declare member variables here

    float outchannel1;

    static boolean oscoutclientStarted = false;

    static OscOutClientInstance instance;

    Object sync = new Object();
    OSCClient c;

    OSCBundle bndlData;

    float outCH1, outCH2, outCH3, outCH4;
    String outCHstring;

    private double inCH1 = 0, inCH2 = 0, inCH3 = 0, inCH4 = 0;;

    /**
     * The class constructor.
     */
    public OscOutClientInstance() {
        // empty constructor
        instance = this;
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
        if ("stringIN".equalsIgnoreCase(portID)) {
            return ipStringIN;
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
        if ("triggerOne".equalsIgnoreCase(eventPortID)) {
            return etpTriggerOne;
        }

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
        if ("port".equalsIgnoreCase(propertyName)) {
            return propPort;
        }
        if ("peerAddress".equalsIgnoreCase(propertyName)) {
            return propPeerAddress;
        }
        if ("addressCh1".equalsIgnoreCase(propertyName)) {
            return propAddressCh1;
        }
        if ("addressStringCh".equalsIgnoreCase(propertyName)) {
            return propAddressStringCh;
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
        if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPort;
            propPort = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("peerAddress".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propPeerAddress;
            propPeerAddress = (String) newValue;
            return oldValue;
        }
        if ("addressCh1".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAddressCh1;
            propAddressCh1 = (String) newValue;
            return oldValue;
        }
        if ("addressStringCh".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAddressStringCh;
            propAddressStringCh = (String) newValue;
            return oldValue;
        }

        return null;
    }

    /**
     * send data to gfOSC
     */
    synchronized private void sendOutput() {
        outCH1 = (float) inCH1;
        outCH2 = (float) inCH2;
        outCH3 = (float) inCH3;
        outCH4 = (float) inCH4;

        // AstericsErrorHandling.instance.reportInfo(OscGestureInstance.instance,
        // "Send_output");

        bndlData = new OSCBundle(System.currentTimeMillis());
        bndlData.addPacket(new OSCMessage(OscOutClientInstance.instance.propAddressCh1,
                new Object[] { new Float(outCH1), new Float(outCH2), new Float(outCH3), new Float(outCH4) }));
        // bndlData.addPacket(new
        // OSCMessage(OscOutClientInstance.instance.propAddressCh1, new Object[]
        // { new Integer (5)}));
        try {
            OscOutClientInstance.instance.c.send(bndlData);
        } catch (IOException e2) {
            e2.printStackTrace();
        }

    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipCH1 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inCH1 = ConversionUtils.doubleFromBytes(data);
            sendOutput();

            /*
             * double value = ConversionUtils.doubleFromBytes(data); outchannel1
             * = (float)value; //float floatValue;
             * 
             * //AstericsErrorHandling.instance.reportInfo(OscOutClientInstance.
             * instance,
             * String.valueOf(OscOutClientInstance.instance.outchannel1)); try {
             * //floatValue= (float)value; OscOutClientInstance.instance.c.send(
             * new OSCMessage( "/channel1", new Object[] { new
             * Float(outchannel1) })); } catch( IOException e3 ) {
             * e3.printStackTrace(); }
             */
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
        }
    };
    private final IRuntimeInputPort ipCH2 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inCH2 = ConversionUtils.doubleFromBytes(data);
            sendOutput();
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
        }
    };
    private final IRuntimeInputPort ipCH3 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inCH3 = ConversionUtils.doubleFromBytes(data);
            sendOutput();
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
        }
    };
    private final IRuntimeInputPort ipCH4 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            inCH4 = ConversionUtils.doubleFromBytes(data);
            sendOutput();

            //

            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
        }
    };
    private final IRuntimeInputPort ipStringIN = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {

            String text = ConversionUtils.stringFromBytes(data);

            bndlData = new OSCBundle(System.currentTimeMillis());
            bndlData.addPacket(new OSCMessage(OscOutClientInstance.instance.propAddressStringCh,
                    new Object[] { new String(text) }));
            try {
                OscOutClientInstance.instance.c.send(bndlData);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            // insert data reception handling here, e.g.:
            // myVar = ConversionUtils.doubleFromBytes(data);
            // myVar = ConversionUtils.stringFromBytes(data);
            // myVar = ConversionUtils.intFromBytes(data);
        }
    };

    /**
     * Event Listerner Ports.
     */

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        client(OSCChannel.UDP);
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        try {
            OscOutClientInstance.instance.c.stop();
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
            OscOutClientInstance.instance.c.start();
        } catch (IOException e1) {
        }
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        OscOutClientInstance.instance.c.dispose();
        super.stop();
    }

    public static void client(String protocol) {
        // postln( "NetUtilTest.client( \"" + protocol + "\" )\n" );
        // postln( "talking to localhost port 57110" );

        try {
            OscOutClientInstance.instance.c = OSCClient.newUsing(protocol);
            OscOutClientInstance.instance.c.setTarget(
                    new InetSocketAddress(InetAddress.getByName(OscOutClientInstance.instance.propPeerAddress),
                            OscOutClientInstance.instance.propPort));
            // postln( " start()" );
            OscOutClientInstance.instance.c.start();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        OscOutClientInstance.instance.c.addOSCListener(new OSCListener() {
            @Override
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
                if (m.getName().equals("/n_end")) {
                    synchronized (OscOutClientInstance.instance.sync) {
                        OscOutClientInstance.instance.sync.notifyAll();
                    }
                }
            }
        });
        // OscOutClientInstance.instance.c.dumpOSC( OSCChannel.kDumpBoth,
        // System.err );

    }

}