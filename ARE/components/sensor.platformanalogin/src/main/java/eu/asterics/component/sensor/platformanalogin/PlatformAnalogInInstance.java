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

package eu.asterics.component.sensor.platformanalogin;

import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortController;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * AnalogInInstance is an AsTeRICS component handling inputs from the ADC inputs
 * of the ADC CIM (ID: 0x0401). It will provide the values sampled on the analog
 * inputs of the CIM on its outputs
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class PlatformAnalogInInstance extends AbstractRuntimeComponentInstance implements CIMEventHandler, Runnable {
    private CIMPortController port = null;

    private final String KEY_PROPERTY_INPUTACTIVE = "activateInput";
    private final String KEY_PROPERTY_INPUTACTIVE_1 = "activateInput1";
    private final String KEY_PROPERTY_INPUTACTIVE_2 = "activateInput2";
    private final String KEY_PROPERTY_PERIODIC_UPDATE = "periodicUpdate";

    private final int NUMBER_OF_INPUTS = 2;
    private final short PLATFORM_CORE_CIM_V2_ID = 0x0602;

    final OutputPort[] opIn = new OutputPort[NUMBER_OF_INPUTS];

    private boolean[] propActivateInput = new boolean[NUMBER_OF_INPUTS];
    private short propPeriodicUpdate = 0;

    private static final short ADC_FEATURE_INPUT_VALUE = 0x40;

    boolean threadActive = false;

    /**
     * Constructs component and initiates output port arrays
     */
    public PlatformAnalogInInstance() {
        // empty constructor - needed for OSGi service factory operations
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            opIn[i] = new OutputPort();
            propActivateInput[i] = false;
        }
    }

    /**
     * Returns the requested output port instance
     * 
     * @param portID
     *            the ID of the requested port
     * @return the the output port instance, null if non existant
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            String s = "in" + (i + 1);
            if (s.equalsIgnoreCase(portID)) {
                return opIn[i];
            }
        }
        return null;
    }

    /**
     * Returns the requested event port
     * 
     * @param eventPortID
     *            the ID of the requested event listener port
     * @return the requested port, null if non existant
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("adcSampleTrigger".equalsIgnoreCase(eventPortID)) {
            return elpAdcSampleTrigger;
        }
        return null;
    }

    /**
     * Returns the value of a requested property
     * 
     * @param propertyName
     *            the name of the requested property
     * @return the value of the property as an Object
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if (KEY_PROPERTY_INPUTACTIVE_1.equalsIgnoreCase(propertyName)) {
            return propActivateInput[0];
        } else if (KEY_PROPERTY_INPUTACTIVE_2.equalsIgnoreCase(propertyName)) {
            return propActivateInput[1];
        } else if (KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName)) {
            return propPeriodicUpdate;
        }
        return null;
    }

    /**
     * Sets the value of a requested property
     * 
     * @param propertyName
     *            the name of the requested property
     * @param newValue
     *            the new value for the property
     * @return null
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if (KEY_PROPERTY_PERIODIC_UPDATE.equalsIgnoreCase(propertyName)) {
            try {
                propPeriodicUpdate = Short.parseShort(newValue.toString());
                AstericsErrorHandling.instance.reportInfo(this,
                        String.format("Setting periodic updates to %d ms", propPeriodicUpdate));
            } catch (NumberFormatException nfe) {
                AstericsErrorHandling.instance.reportInfo(this,
                        "Invalid property value for " + propertyName + ": " + newValue);
            }
        } else {
            // only the input active properties available anymore
            boolean propertyValue = false;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propertyValue = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propertyValue = false;
            } else {
                AstericsErrorHandling.instance.reportInfo(this,
                        "Invalid property value for " + propertyName + ": " + newValue);
            }

            for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
                String s = KEY_PROPERTY_INPUTACTIVE + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    propActivateInput[i] = propertyValue;
                    AstericsErrorHandling.instance.reportInfo(this,
                            String.format("Setting input %d to %s", i, newValue));
                }
            }
        }
        return null;
    }

    /**
     * Starts the component, retrieves ADC CIM connection and adds a listener to
     * the connection. Starts thread for periodic updates.
     */
    @Override
    public void start() {
        port = CIMPortManager.getInstance().getConnection(PLATFORM_CORE_CIM_V2_ID);
        if (port != null) {
            port.addEventListener(this);
        } else {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not find AsTeRICS Personal platform - needed by the PlatformAnalogIn plugin");
        }
        threadActive = true;
        AstericsThreadPool.instance.execute(this);
        super.start();
        // AstericsErrorHandling.instance.reportInfo(this,
        // "PlatformAnalogInInstance started");
    }

    /**
     * Stops the component, removes the listener, halts the thread
     */
    @Override
    public void stop() {
        super.stop();
        threadActive = false;
        if (port != null) {
            port.removeEventListener(this);
        }
        // AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance
        // stopped");
    }

    /**
     * Pauses the component, removes the listener, halts the thread
     */
    @Override
    public void pause() {
        super.pause();
        threadActive = false;
        if (port != null) {
            port.removeEventListener(this);
        }
        // AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance
        // paused");
    }

    /**
     * Resumes the component, retrieves ADC CIM connection and adds a listener
     * to the connection. Starts thread for periodic updates.
     */
    @Override
    public void resume() {
        port = CIMPortManager.getInstance().getConnection(PLATFORM_CORE_CIM_V2_ID);
        if (port != null) {
            port.addEventListener(this);
        } else {
            AstericsErrorHandling.instance.reportError(this,
                    "Could not find AsTeRICS Personal platform - needed by the PlatformAnalogIn plugin");
        }
        threadActive = true;
        AstericsThreadPool.instance.execute(this);
        super.resume();
        // AstericsErrorHandling.instance.reportInfo(this, "AnalogInInstance
        // resumed");
    }

    /**
     * Runs a loop which periodically sends a sample request to the ADC CIM if
     * periodic update is activated
     */
    @Override
    public void run() {
        while (threadActive) {
            if ((propPeriodicUpdate != 0) && (port != null)) {
                CIMPortManager.getInstance().sendPacket(port, null, ADC_FEATURE_INPUT_VALUE,
                        CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
                // Logger.getAnonymousLogger().info("Sent ADC input value read
                // message");
            }

            try {
                Thread.sleep(propPeriodicUpdate != 0 ? propPeriodicUpdate : 1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Handles an input packet from the ADC CIM. Reads the values on all active
     * inputs and sends the data on the corresponding output ports
     * 
     * @param packet
     *            the incoming packet
     */
    private void handleAdcInputValuePacket(CIMProtocolPacket packet) {
        byte[] b = packet.getData();

        for (int i = 0; i < 2; i++) {
            if (propActivateInput[i]) {
                int output = 0;
                for (int j = 0; j < 3; j++) {
                    output |= (((int) b[j + i * 3]) & 0xff) << 8 * j;
                }
                opIn[i].sendData(output);
            }
        }
    }

    /**
     * Called by port controller if new packet has been received
     */
    @Override
    public void handlePacketReceived(CIMEvent e) {
        // Logger.getAnonymousLogger().info("handlePacketReceived start");

        CIMEventPacketReceived ev = (CIMEventPacketReceived) e;
        CIMProtocolPacket packet = ev.packet;

        if (packet.getFeatureAddress() == ADC_FEATURE_INPUT_VALUE) {
            handleAdcInputValuePacket(packet);
        }
    }

    /**
     * Called upon faulty packet reception
     */
    @Override
    public void handlePacketError(CIMEvent e) {
        AstericsErrorHandling.instance.reportInfo(this, "Faulty packet received");
    }

    /**
     * An output port implementation that allows sending of integers
     * 
     * @author weissch
     *
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToBytes(data));
        }
    }

    /**
     * An event listener port implementation which will cause ADC sampling upon
     * an incoming event.
     */
    final IRuntimeEventListenerPort elpAdcSampleTrigger = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if ((propPeriodicUpdate == 0) && (port != null)) {
                CIMPortManager.getInstance().sendPacket(port, null, ADC_FEATURE_INPUT_VALUE,
                        CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
            } else {
                AstericsErrorHandling.instance.getLogger()
                        .fine("Trigger event received while periodic update was" + " set, ignoring event");
            }
        }
    };

}