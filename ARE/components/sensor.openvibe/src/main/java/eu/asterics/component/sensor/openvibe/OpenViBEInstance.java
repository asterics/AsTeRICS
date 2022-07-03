
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

package eu.asterics.component.sensor.openvibe;

import java.io.IOException;
import java.net.SocketAddress;

import de.sciss.net.OSCChannel;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author Rene Hirtl [rene.hirtl@technikum-wien.at] Date: Apr 25, 2012 Time:
 *         4:36:00 PM
 */
public class OpenViBEInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opCH1 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH2 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH3 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH4 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH5 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH6 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH7 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH8 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH9 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH10 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH11 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH12 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH13 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH14 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH15 = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCH16 = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpTriggerOut = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_00 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_01 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_02 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_03 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_04 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_05 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_06 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_07 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_08 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_09 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_0A = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_0B = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Label_0C = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Target = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_NonTarget = new DefaultRuntimeEventTriggererPort();

    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_0 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_1 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_2 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_3 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_4 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_5 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_6 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_7 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_8 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_9 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_A = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_B = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_C = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_D = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_E = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_F = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_G = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_H = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_I = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_J = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_K = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_L = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_M = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_N = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_O = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_P = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_Q = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_R = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_S = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_T = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_U = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_V = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_W = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_X = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_Y = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpOVTK_StimulationId_Letter_Z = new DefaultRuntimeEventTriggererPort();

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propPort = 57110;

    // declare member variables here
    static boolean openvibeStarted = false;
    static OpenViBEInstance instance;

    int SamplingFrequency = 128;
    int ChannelCount = 0;

    // float ReceiveBuffer[][] = new float[2][8192];
    // int ReceiveBufferCount = 0;

    Object sync = new Object();
    OSCServer c;

    /**
     * The class constructor.
     */
    public OpenViBEInstance() {
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
        if ("cH1".equalsIgnoreCase(portID)) {
            return opCH1;
        }
        if ("cH2".equalsIgnoreCase(portID)) {
            return opCH2;
        }
        if ("cH3".equalsIgnoreCase(portID)) {
            return opCH3;
        }
        if ("cH4".equalsIgnoreCase(portID)) {
            return opCH4;
        }
        if ("cH5".equalsIgnoreCase(portID)) {
            return opCH5;
        }
        if ("cH6".equalsIgnoreCase(portID)) {
            return opCH6;
        }
        if ("cH7".equalsIgnoreCase(portID)) {
            return opCH7;
        }
        if ("cH8".equalsIgnoreCase(portID)) {
            return opCH8;
        }
        if ("cH9".equalsIgnoreCase(portID)) {
            return opCH9;
        }
        if ("cH10".equalsIgnoreCase(portID)) {
            return opCH10;
        }
        if ("cH11".equalsIgnoreCase(portID)) {
            return opCH11;
        }
        if ("cH12".equalsIgnoreCase(portID)) {
            return opCH12;
        }
        if ("cH13".equalsIgnoreCase(portID)) {
            return opCH13;
        }
        if ("cH14".equalsIgnoreCase(portID)) {
            return opCH14;
        }
        if ("cH15".equalsIgnoreCase(portID)) {
            return opCH15;
        }
        if ("cH16".equalsIgnoreCase(portID)) {
            return opCH16;
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
        if ("triggerOut".equalsIgnoreCase(eventPortID)) {
            return etpTriggerOut;
        } else if ("OVTK_StimulationId_Label_00".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_00;
        } else if ("OVTK_StimulationId_Label_01".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_01;
        } else if ("OVTK_StimulationId_Label_02".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_02;
        } else if ("OVTK_StimulationId_Label_03".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_03;
        } else if ("OVTK_StimulationId_Label_04".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_04;
        } else if ("OVTK_StimulationId_Label_05".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_05;
        } else if ("OVTK_StimulationId_Label_06".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_06;
        } else if ("OVTK_StimulationId_Label_07".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_07;
        } else if ("OVTK_StimulationId_Label_08".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_08;
        } else if ("OVTK_StimulationId_Label_09".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_09;
        } else if ("OVTK_StimulationId_Label_0A".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_0A;
        } else if ("OVTK_StimulationId_Label_0B".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_0B;
        } else if ("OVTK_StimulationId_Label_0C".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Label_0C;
        } else if ("OVTK_StimulationId_Target".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Target;
        } else if ("OVTK_StimulationId_NonTarget".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_NonTarget;
        } else if ("OVTK_StimulationId_Letter_0".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_0;
        } else if ("OVTK_StimulationId_Letter_1".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_1;
        } else if ("OVTK_StimulationId_Letter_2".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_2;
        } else if ("OVTK_StimulationId_Letter_3".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_3;
        } else if ("OVTK_StimulationId_Letter_4".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_4;
        } else if ("OVTK_StimulationId_Letter_5".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_5;
        } else if ("OVTK_StimulationId_Letter_6".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_6;
        } else if ("OVTK_StimulationId_Letter_7".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_7;
        } else if ("OVTK_StimulationId_Letter_8".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_8;
        } else if ("OVTK_StimulationId_Letter_9".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_9;
        } else if ("OVTK_StimulationId_Letter_A".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_A;
        } else if ("OVTK_StimulationId_Letter_B".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_B;
        } else if ("OVTK_StimulationId_Letter_C".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_C;
        } else if ("OVTK_StimulationId_Letter_D".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_D;
        } else if ("OVTK_StimulationId_Letter_E".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_E;
        } else if ("OVTK_StimulationId_Letter_F".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_F;
        } else if ("OVTK_StimulationId_Letter_G".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_G;
        } else if ("OVTK_StimulationId_Letter_H".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_H;
        } else if ("OVTK_StimulationId_Letter_I".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_I;
        } else if ("OVTK_StimulationId_Letter_J".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_J;
        } else if ("OVTK_StimulationId_Letter_K".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_K;
        } else if ("OVTK_StimulationId_Letter_L".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_L;
        } else if ("OVTK_StimulationId_Letter_M".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_M;
        } else if ("OVTK_StimulationId_Letter_N".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_N;
        } else if ("OVTK_StimulationId_Letter_O".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_O;
        } else if ("OVTK_StimulationId_Letter_P".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_P;
        } else if ("OVTK_StimulationId_Letter_Q".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_Q;
        } else if ("OVTK_StimulationId_Letter_R".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_R;
        } else if ("OVTK_StimulationId_Letter_S".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_S;
        } else if ("OVTK_StimulationId_Letter_T".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_T;
        } else if ("OVTK_StimulationId_Letter_U".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_U;
        } else if ("OVTK_StimulationId_Letter_V".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_V;
        } else if ("OVTK_StimulationId_Letter_W".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_W;
        } else if ("OVTK_StimulationId_Letter_X".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_X;
        } else if ("OVTK_StimulationId_Letter_Y".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_Y;
        } else if ("OVTK_StimulationId_Letter_Z".equalsIgnoreCase(eventPortID)) {
            return etpOVTK_StimulationId_Letter_Z;
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

        return null;
    }

    /**
     * Input Ports for receiving values.
     */

    /**
     * Event Listerner Ports.
     */

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
        // String.format("Start"));
        server(OSCChannel.UDP);
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        try {
            OpenViBEInstance.instance.c.stop();
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
            OpenViBEInstance.instance.c.start();
        } catch (IOException e1) {
        }
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        OpenViBEInstance.instance.c.dispose();
        super.stop();
    }

    /**
     * Tests the server functionality on a given protocol. This opens a server
     * listening at port 0x5454. Recognized messages are <code>/pause</code>,
     * <code>/quit</code>, <code>/dumpOSC</code>. See
     * <code>NetUtil_Tests.rtf</code> for a way to check the server.
     *
     * @param protocol
     *            <code>UDP</code> or <code>TCP</code>
     */
    public static void server(String protocol) {
        // final Object sync = new Object();
        // final OSCServer c;
        // final OSCMessage[] msgBuffer = new OSCMessage[16];
        // final int msgCount = 0;
        // final long timeStamp = 0;

        try {
            OpenViBEInstance.instance.c = OSCServer.newUsing(protocol, OpenViBEInstance.instance.propPort);
            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
            // "BufferSize");
            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
            // String.valueOf(OpenViBEInstance.instance.c.getBufferSize()));
            // OpenViBEInstance.instance.c.setBufferSize(256);
            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
            // String.valueOf(OpenViBEInstance.instance.c.getBufferSize()));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        OpenViBEInstance.instance.c.addOSCListener(new OSCListener() {
            /*
             * public void messageReceived( OSCBundle bndl, SocketAddress addr,
             * long time ) {
             * AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.
             * instance, "Bundle"); }
             */

            /*
             * int msgCount = 0; long timeStamp = 0; long timeStampStim = 0;
             * long blubvar = 0;
             */

            String StimulationID1 = "";// new String();
            String StimulationID2 = "";// new String();

            @Override
            public void messageReceived(OSCMessage m, SocketAddress addr, long time) {
                // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                // String.valueOf(blubvar));

                // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                // String.valueOf(time));
                // bndl = new OSCBundle();

                /*
                 * if(timeStamp < OpenViBEInstance.instance.ChannelCount) {
                 * msgBuffer[msgCount] = m; msgCount++; timeStamp++; } else {
                 * msgCount = 0; timeStamp = 0; //Call plot function }
                 */

                // OpenViBEInstance.instance.opCH13.sendData(ConversionUtils.doubleToBytes((double)
                // time));

                int count = 0;
                int size = 0;
                int sizeAddress = 0;

                /*
                 * try {
                 * //AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.
                 * instance, String.format( "send "+addr ));
                 * //OpenViBEInstance.instance.c.send( new OSCMessage( "/done",
                 * new Object[] { m.getName(), m.getArg(0), m.getArg(1),
                 * m.getArg(2), m.getArg(3) }), addr );
                 * OpenViBEInstance.instance.c.send( new OSCMessage( "/done",
                 * new Object[] { m.getName() }), addr ); } catch( IOException
                 * e1 ) { e1.printStackTrace(); }
                 */

                try {
                    size = m.getSize();
                    sizeAddress = m.getName().length();
                    sizeAddress += (4 - sizeAddress % 4) + 4;
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(size));
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(sizeAddress));
                } catch (IOException e4) {
                    size = 0;
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(size));
                }

                /*
                 * for(count = 0; count < ((size-sizeAddress)/5); count++) {
                 * OpenViBEInstance.instance.opCH1.sendData(ConversionUtils.
                 * doubleToBytes((float) m.getArg(count))); }
                 */

                /*
                 * for(count = 0; count < ((size-sizeAddress)/5); count++) {
                 * OpenViBEInstance.instance.ReceiveBuffer[0][OpenViBEInstance.
                 * instance.ReceiveBufferCount] = (float) m.getArg(count);
                 * OpenViBEInstance.instance.ReceiveBuffer[1][OpenViBEInstance.
                 * instance.ReceiveBufferCount] = (float) time;
                 * OpenViBEInstance.instance.ReceiveBufferCount++;
                 * 
                 * if(OpenViBEInstance.instance.ReceiveBufferCount >= 1024) {
                 * for(count = 0; count < 1024; count++) {
                 * OpenViBEInstance.instance.opCH13.sendData(ConversionUtils.
                 * doubleToBytes(OpenViBEInstance.instance.ReceiveBuffer[0][
                 * OpenViBEInstance.instance.ReceiveBufferCount]));
                 * OpenViBEInstance.instance.opCH14.sendData(ConversionUtils.
                 * doubleToBytes(OpenViBEInstance.instance.ReceiveBuffer[1][
                 * OpenViBEInstance.instance.ReceiveBufferCount])); } } }
                 */

                // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                // m.getName());

                if (m.getName().contains("bundle")) {
                    AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance, "Bundle");
                }

                else if (m.getName().equals("/SamplingFrequency")) {
                    OpenViBEInstance.instance.SamplingFrequency = (int) m.getArg(0);
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(OpenViBEInstance.instance.SamplingFrequency));
                    // OpenViBEInstance.instance.opCH2.sendData(ConversionUtils.intToBytes((int)
                    // m.getArg(0)));

                    // Set defaul value, if necessary
                    if (OpenViBEInstance.instance.SamplingFrequency == 0) {
                        OpenViBEInstance.instance.SamplingFrequency = 128;
                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.valueOf(OpenViBEInstance.instance.SamplingFrequency));
                    }
                } else if (m.getName().equals("/ChannelCount")) {
                    OpenViBEInstance.instance.ChannelCount = (int) m.getArg(0);
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(OpenViBEInstance.instance.ChannelCount));
                } else if (m.getName().startsWith("/Signal")) {

                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // "Signal");
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // m.getName());

                    if (m.getName().contains(String.valueOf(1))) {
                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // "CH1");
                        // System.out.println("Ch1 data size:"+
                        // (size-sizeAddress)/5);
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH1
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                            System.out.print("*");
                            // try {

                            /*
                             * final long end = System.nanoTime() + 500000; long
                             * timeLeft; do { Thread.yield(); timeLeft = end -
                             * System.nanoTime(); } while (timeLeft > 0);
                             */

                            // Thread.yield();
                            // } catch (InterruptedException e) {}

                            // OpenViBEInstance.instance.opCH14.sendData(ConversionUtils.doubleToBytes((double)
                            // time));
                        }
                    } else if (m.getName().contains(String.valueOf(2))) {
                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // "CH2");
                        // System.out.println("Ch2 data size:"+
                        // (size-sizeAddress)/5);

                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH2
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                            /*
                             * final long end = System.nanoTime() + 500000; long
                             * timeLeft; do { Thread.yield(); timeLeft = end -
                             * System.nanoTime(); } while (timeLeft > 0);
                             */
                        }
                    } else if (m.getName().contains(String.valueOf(3))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH3
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(4))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH4
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(5))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH5
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(6))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH6
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(7))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH7
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(8))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH8
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(9))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH9
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(10))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH10
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(11))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH11
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(12))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH12
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(13))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH13
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(14))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH14
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(15))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH15
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    } else if (m.getName().contains(String.valueOf(16))) {
                        for (count = 0; count < ((size - sizeAddress) / 5); count++) {
                            OpenViBEInstance.instance.opCH16
                                    .sendData(ConversionUtils.doubleToBytes((float) m.getArg(count)));
                        }
                    }

                    synchronized (OpenViBEInstance.instance.sync) {
                        OpenViBEInstance.instance.sync.notifyAll();
                    }

                } else if (m.getName().contains("/Stimulation")) {
                    String StimulationID = new String();
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.format( "Stimulation" ));
                    // OpenViBEInstance.instance.etpTriggerOut.raiseEvent();
                    // OpenViBEInstance.instance.etpOVTK_StimulationId_Label_00.raiseEvent();
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.valueOf(time));
                    // if(timeStampStim == time)
                    StimulationID = (String) m.getArg(0);
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.format(m.getName()));
                    // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                    // String.format(StimulationID));

                    if (m.getName().contains("P300")) {

                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format("P300"));

                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format(StimulationID.substring(26)));

                        int value = 0;

                        if (StimulationID.substring(26).contains("C")) {
                            value = 12;
                        } else if (StimulationID.substring(26).contains("B")) {
                            value = 11;
                        } else if (StimulationID.substring(26).contains("A")) {
                            value = 10;
                        } else if (StimulationID.contains("OVTK_StimulationId_Label")) {
                            value = Integer.parseInt(StimulationID.substring(26));
                        }

                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format(Integer.toString(value)));

                        if ((value >= 1) && (value < 7)) {
                            StimulationID1 = StimulationID;
                        } else if ((value >= 7) && (value < 13)) {
                            StimulationID2 = StimulationID;
                        }

                        // if((ConversionUtils.intFromBytes(ConversionUtils.stringToBytes(StimulationID.substring(26,
                        // 27))) >= 1) &&
                        // (ConversionUtils.intFromBytes(ConversionUtils.stringToBytes(StimulationID.substring(26,
                        // 27))) < 7)) StimulationID1 = StimulationID;
                        // else
                        // if((ConversionUtils.intFromBytes(ConversionUtils.stringToBytes(StimulationID.substring(26,
                        // 27))) >= 7) &&
                        // (ConversionUtils.intFromBytes(ConversionUtils.stringToBytes(StimulationID.substring(26,
                        // 27))) < 13)) StimulationID2 = StimulationID;
                        // else StimulationID2 = StimulationID;

                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format(StimulationID.substring(25, 27)));

                        // if second stimulation was sent
                        if (m.getName().contains("/Stimulation") && StimulationID1 != "" && StimulationID2 != "") {
                            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                            // String.format("blub2"));

                            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                            // String.format("Second stimulation!"));
                            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                            // String.format(StimulationID1));
                            // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                            // String.format(StimulationID2));

                            switch (StimulationID1) {
                            case "OVTK_StimulationId_Label_01": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_A.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_B.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_C.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_D.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_E.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_F.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            case "OVTK_StimulationId_Label_02": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_G.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_H.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_I.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_J.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_K.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_L.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            case "OVTK_StimulationId_Label_03": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_M.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_N.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_O.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_P.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_Q.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_R.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            case "OVTK_StimulationId_Label_04": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_S.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_T.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_U.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_V.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_W.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_X.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            case "OVTK_StimulationId_Label_05": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_Y.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_Z.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_1.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_2.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_3.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_4.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            case "OVTK_StimulationId_Label_06": {
                                switch (StimulationID2) {
                                case "OVTK_StimulationId_Label_07": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_5.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_08": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_6.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_09": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_7.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0A": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_8.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0B": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_9.raiseEvent();
                                    break;
                                }
                                case "OVTK_StimulationId_Label_0C": {
                                    OpenViBEInstance.instance.etpOVTK_StimulationId_Letter_0.raiseEvent();
                                    break;
                                }
                                }
                                break;
                            }
                            }
                            StimulationID1 = "";
                            StimulationID2 = "";
                        }
                    } else if (m.getName().contains("SSVEP")) {
                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format("SSVEP"));
                        if (StimulationID.contains("OVTK_StimulationId_Label_00")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_00.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_01")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_01.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_02")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_02.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_03")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_03.raiseEvent();
                        }
                    } else {
                        // AstericsErrorHandling.instance.reportInfo(OpenViBEInstance.instance,
                        // String.format("Misc"));

                        if (StimulationID.contains("OVTK_StimulationId_Label_00")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_00.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_01")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_01.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_02")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_02.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_03")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_03.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_04")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_04.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_05")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_05.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_06")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_06.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_07")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_07.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_08")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_08.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_09")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_09.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_0A")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_0A.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_0B")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_0B.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Label_0C")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Label_0C.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_Target")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_Target.raiseEvent();
                        }
                        if (StimulationID.contains("OVTK_StimulationId_NonTarget")) {
                            OpenViBEInstance.instance.etpOVTK_StimulationId_NonTarget.raiseEvent();
                        }
                    }

                }
            }
        });

        try {
            OpenViBEInstance.instance.c.start();
        } catch (IOException e3) {
        }
    }

    /*
     * protected static void postln( String s ) { System.err.println( s ); }
     */
}
