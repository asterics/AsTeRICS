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
 *    Additional terms:
 *    1.ENOBIO License Agreement
 *    Read the following terms and conditions carefully. The use of this system
 *    and software acknowledges that you have read this agreement, understood
 *    it and that you agree to be bound by its terms and conditions.
 *
 *    1.1.Copyright
 *    - All associated title and copyrights of the formerly mentioned plugins
 *    accompanying printed materials are owned by Starlab Barcelona S.L. You
 *    may not copy the printed materials accompanying ENOBIO without the
 *    express written permission of Starlab Barcelona S.L.
 *    - You may not transfer this software to another party without the
 *    permission of Starlab Barcelona S.L. You may not rent, lease, or lend
 *    this software. This license is effective until terminated. You may
 *    terminate it at any time by destroying the software together with all
 *    copies. This license also terminates if you fail to comply with the terms
 *    and conditions of this agreement.
 *
 *    1.2.Limited Warranty
 *    - This Enobio accompanying software plugins can be downloaded "as is" and
 *    without warranties as to performance or merchantability or any other
 *    warranties whether expressed or implied.
 *    - This software is designed to work with a particular hardware
 *    configuration: the ENOBIO device, which must be acquired separately,
 *    transmitting to a PC via the provided USB receiver, no warranty of
 *    fitness for any other configuration is offered.
 *    - The user must assume the entire risk of using the program. Any
 *    liability of Starlab Barcelona S.L. will be limited exclusively to
 *    product replacement or refund of the purchase price at the discretion of
 *    Starlab S.L.
 *
 *    1.3.Support
 *    - Email support shall be provided free for the lifetime of the product as
 *    long as the product has not been discontinued : enobio@starlab.es
 *
 */

package com.starlab.component.sensor.enobio;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the Enobio sensor plugin, which communicates with the Enobio
 * reciever and implements the protocol to retrieve the raw data from the four
 * Enobio channels
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Jun 21, 2011 Time:
 *         02:29:56 PM
 */
public class EnobioInstance extends AbstractRuntimeComponentInstance implements IEnobio {
    public boolean propHighPassFilterInChannel1 = false;
    public boolean propHighPassFilterInChannel2 = false;
    public boolean propHighPassFilterInChannel3 = false;
    public boolean propHighPassFilterInChannel4 = false;
    public boolean propIsChannel1Activated = false;
    public boolean propIsChannel2Activated = false;
    public boolean propIsChannel3Activated = false;
    public boolean propIsChannel4Activated = false;
    public boolean propLineNoiseFilter = false;

    private final OutputPort opEnobioChannel1 = new OutputPort();
    private final OutputPort opEnobioChannel2 = new OutputPort();
    private final OutputPort opEnobioChannel3 = new OutputPort();
    private final OutputPort opEnobioChannel4 = new OutputPort();
    private final OutputPort opEnobioStatus = new OutputPort();
    private final IRuntimeEventTriggererPort runtimePosEdgeEventTriggererPort = new DefaultRuntimeEventTriggererPort();
    private final IRuntimeEventTriggererPort runtimeNegEdgeEventTriggererPort = new DefaultRuntimeEventTriggererPort();

    private Enobio enobio;
    private int externalSignalLastValue = 0xff; // unknown value;

    /**
     * The class constructor.
     */
    public EnobioInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    /**
     * called when model is started. It initializes the Enobio sensor and asks
     * for starting the streaming
     */
    @Override
    public void start() {
        if (enobio == null) {
            enobio = new Enobio(this);
            propHighPassFilterInChannel1 = enobio.getHpfc1();
            propHighPassFilterInChannel2 = enobio.getHpfc2();
            propHighPassFilterInChannel3 = enobio.getHpfc3();
            propHighPassFilterInChannel4 = enobio.getHpfc4();
            propIsChannel1Activated = enobio.getChk1();
            propIsChannel2Activated = enobio.getChk2();
            propIsChannel3Activated = enobio.getChk3();
            propIsChannel4Activated = enobio.getChk4();
            propLineNoiseFilter = enobio.getFilter();
        }
        externalSignalLastValue = 0xff; // unkown value
        enobio.onAction();
        enobio.startAction();
        super.start();
    }

    /**
     * called when model is paused. The streaming shall be stopped
     */
    @Override
    public void pause() {
        enobio.stopAction();
        super.pause();
    }

    /**
     * called when model is resumed. The streaming shall be resumed
     */
    @Override
    public void resume() {
        enobio.startAction();
        super.resume();
    }

    /**
     * called when model is stopped. It performs the action for closing and
     * stopping the Enobio sensor
     */
    @Override
    public void stop() {
        if (enobio != null) {
            enobio.stopAction();
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            enobio.offAction();
            enobio = null;
        }
        super.stop();
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
        if ("Channel1".equalsIgnoreCase(portID)) {
            return opEnobioChannel1;
        } else if ("Channel2".equalsIgnoreCase(portID)) {
            return opEnobioChannel2;
        } else if ("Channel3".equalsIgnoreCase(portID)) {
            return opEnobioChannel3;
        } else if ("Channel4".equalsIgnoreCase(portID)) {
            return opEnobioChannel4;
        } else if ("Status".equalsIgnoreCase(portID)) {
            return opEnobioStatus;
        }

        return null;
    }

    /**
     * returns an event triggerer Port.
     * 
     * @param eventPortID
     *            the name of the event port
     * @return the event port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("externalSignalPosEdgeEvent".equalsIgnoreCase(eventPortID)) {
            return runtimePosEdgeEventTriggererPort;
        } else if ("externalSignalNegEdgeEvent".equalsIgnoreCase(eventPortID)) {
            return runtimeNegEdgeEventTriggererPort;
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
        if ("HighPassFilterInChannel1".equalsIgnoreCase(propertyName)) {
            propHighPassFilterInChannel1 = enobio.getHpfc1();
            return propHighPassFilterInChannel1;
        } else if ("HighPassFilterInChannel2".equalsIgnoreCase(propertyName)) {
            propHighPassFilterInChannel2 = enobio.getHpfc2();
            return propHighPassFilterInChannel2;
        } else if ("HighPassFilterInChannel3".equalsIgnoreCase(propertyName)) {
            propHighPassFilterInChannel3 = enobio.getHpfc3();
            return propHighPassFilterInChannel3;
        } else if ("HighPassFilterInChannel4".equalsIgnoreCase(propertyName)) {
            propHighPassFilterInChannel4 = enobio.getHpfc4();
            return propHighPassFilterInChannel4;
        } else if ("IsChannel1Activated".equalsIgnoreCase(propertyName)) {
            propIsChannel1Activated = enobio.getChk1();
            return propIsChannel1Activated;
        } else if ("IsChannel2Activated".equalsIgnoreCase(propertyName)) {
            propIsChannel2Activated = enobio.getChk2();
            return propIsChannel2Activated;
        } else if ("IsChannel3Activated".equalsIgnoreCase(propertyName)) {
            propIsChannel3Activated = enobio.getChk3();
            return propIsChannel3Activated;
        } else if ("IsChannel4Activated".equalsIgnoreCase(propertyName)) {
            propIsChannel4Activated = enobio.getChk4();
            return propIsChannel4Activated;
        } else if ("LineNoiseFilter".equalsIgnoreCase(propertyName)) {
            propLineNoiseFilter = enobio.getFilter();
            return propLineNoiseFilter;
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
        if (enobio == null) {
            enobio = new Enobio(this);
        }

        if ("HighPassFilterInChannel1".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getHpfc1();

            if (newValue != null) {
                propHighPassFilterInChannel1 = Boolean.parseBoolean((newValue.toString()));
                enobio.setHpfc1(propHighPassFilterInChannel1);
            }

            return oldValue;
        } else if ("HighPassFilterInChannel2".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getHpfc2();

            if (newValue != null) {
                propHighPassFilterInChannel2 = Boolean.parseBoolean((newValue.toString()));
                enobio.setHpfc2(propHighPassFilterInChannel2);
            }

            return oldValue;
        } else if ("HighPassFilterInChannel3".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getHpfc3();

            if (newValue != null) {
                propHighPassFilterInChannel3 = Boolean.parseBoolean((newValue.toString()));
                enobio.setHpfc3(propHighPassFilterInChannel3);
            }

            return oldValue;
        } else if ("HighPassFilterInChannel4".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getHpfc4();

            if (newValue != null) {
                propHighPassFilterInChannel4 = Boolean.parseBoolean((newValue.toString()));
                enobio.setHpfc4(propHighPassFilterInChannel4);
            }

            return oldValue;
        } else if ("IsChannel1Activated".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getChk1();

            if (newValue != null) {
                propIsChannel1Activated = Boolean.parseBoolean((newValue.toString()));
                enobio.setChk1(propIsChannel1Activated);
            }

            return oldValue;
        } else if ("IsChannel2Activated".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getChk2();

            if (newValue != null) {
                propIsChannel2Activated = Boolean.parseBoolean((newValue.toString()));
                enobio.setChk2(propIsChannel2Activated);
            }

            return oldValue;
        } else if ("IsChannel3Activated".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getChk3();

            if (newValue != null) {
                propIsChannel3Activated = Boolean.parseBoolean((newValue.toString()));
                enobio.setChk3(propIsChannel3Activated);
            }

            return oldValue;
        } else if ("IsChannel4Activated".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getChk4();

            if (newValue != null) {
                propIsChannel4Activated = Boolean.parseBoolean((newValue.toString()));
                enobio.setChk4(propIsChannel4Activated);
            }

            return oldValue;
        } else if ("LineNoiseFilter".equalsIgnoreCase(propertyName)) {
            final Object oldValue = enobio.getFilter();

            if (newValue != null) {
                propLineNoiseFilter = Boolean.parseBoolean((newValue.toString()));
                enobio.setFilter(propLineNoiseFilter);
            }

            return oldValue;
        }

        return null; // To change body of implemented methods use File |
                     // Settings | File Templates.

    }

    /**
     * It is called every time there is a new sample of data from Enobio ready
     * to be sent
     * 
     * @param v1
     *            Channel 1 value
     * @param v2
     *            Channel 2 value
     * @param v3
     *            Channel 3 value
     * @param v4
     *            Channel 4 value
     * @param vStatus
     *            Status value
     */
    @Override
    public void newValues(int v1, int v2, int v3, int v4, int vStatus) {
        int externalSignalValue = ((vStatus & 0x00ff0000) > 0) ? 1 : 0; // Bit
                                                                        // of
                                                                        // vStatus
                                                                        // carries
                                                                        // the
                                                                        // external
                                                                        // signal
                                                                        // level
        if (externalSignalValue != externalSignalLastValue) {
            if (externalSignalLastValue == 0 && externalSignalValue == 1) {
                reportInfo("positive edge event");
                runtimePosEdgeEventTriggererPort.raiseEvent();
            } else if (externalSignalLastValue == 1 && externalSignalValue == 0) {
                reportInfo("negative edge event");
                runtimeNegEdgeEventTriggererPort.raiseEvent();
            }
            externalSignalLastValue = externalSignalValue;
        }
        opEnobioStatus.sendData(vStatus);
        opEnobioChannel1.sendData(v1);
        opEnobioChannel2.sendData(v2);
        opEnobioChannel3.sendData(v3);
        opEnobioChannel4.sendData(v4);
    }

    /**
     * called for reporting info to the ARE
     */
    @Override
    public void reportInfo(String strInfo) {
        AstericsErrorHandling.instance.reportInfo(this, strInfo);
    }

    /**
     * called for reporting some error to the ARE
     */
    @Override
    public void reportError(String strError) {
        AstericsErrorHandling.instance.reportError(this, strError);
    }

    /**
     * implementation of the default output port for sending integers
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToBytes(data));
        }
    }

}
