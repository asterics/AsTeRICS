
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

package com.starlab.component.actuator.enobiodisplay;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AREServices;

/**
 * Implements the Enobiodisplay actuator plugin, which can show the Enobio
 * signals in different colors depending on their calibration status: RED:
 * channel not being calibrated, YELLOW: channel being calibrated, GREEN:
 * channel calibrated
 * 
 * @author Javier Acedo [javier.acedo@starlab.es] Date: Apr 29, 2011 Time
 *         04:51:02 PM
 */
public class EnobioDisplayInstance extends AbstractRuntimeComponentInstance {
    private int dataCh1 = 0;
    private int dataCh2 = 0;
    private int dataCh3 = 0;
    private int dataCh4 = 0;
    private int dataStatus = 0;
    private final PlotPanel gui = new PlotPanel(2000);

    /**
     * The class constructor. initializes the GUI
     */
    public EnobioDisplayInstance() {
        // empty constructor - needed for OSGi service factory operations
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
        if ("Channel1".equalsIgnoreCase(portID)) {
            return ipEnobioChannel1;
        } else if ("Channel2".equalsIgnoreCase(portID)) {
            return ipEnobioChannel2;
        } else if ("Channel3".equalsIgnoreCase(portID)) {
            return ipEnobioChannel3;
        } else if ("Channel4".equalsIgnoreCase(portID)) {
            return ipEnobioChannel4;
        } else if ("Status".equalsIgnoreCase(portID)) {
            return ipEnobioStatus;
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
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
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
        return null;
    }

    /**
     * Input Port for receiving Enobio channel 1 values.
     */
    private final IRuntimeInputPort ipEnobioChannel1 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            dataCh1 = ConversionUtils.intFromBytes(data);

            gui.addValue(0, dataCh1);
        }

    };

    /**
     * Input Port for receiving Enobio channel 2 values.
     */
    private final IRuntimeInputPort ipEnobioChannel2 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            dataCh2 = ConversionUtils.intFromBytes(data);

            gui.addValue(1, dataCh2);
        }

    };

    /**
     * Input Port for receiving Enobio channel 3 values.
     */
    private final IRuntimeInputPort ipEnobioChannel3 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            dataCh3 = ConversionUtils.intFromBytes(data);

            gui.addValue(2, dataCh3);
        }

    };

    /**
     * Input Port for receiving Enobio channel 4 values.
     */
    private final IRuntimeInputPort ipEnobioChannel4 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            dataCh4 = ConversionUtils.intFromBytes(data);

            gui.addValue(3, dataCh4);
        }

    };

    /**
     * Input Port for receiving Enobio status values.
     */
    private final IRuntimeInputPort ipEnobioStatus = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            dataStatus = ConversionUtils.intFromBytes(data);

            gui.updateStatus(dataStatus);
            // gui.updateCoordinates(0, status);
        }

    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        // gui.setVisible(true);
        AREServices.instance.displayPanel(gui, this, true);

        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        // gui.setVisible(false);
        AREServices.instance.displayPanel(gui, this, false);
        dataCh1 = 0;
        dataCh2 = 0;
        dataCh3 = 0;
        dataCh4 = 0;
        dataStatus = 0;

        gui.addValues(dataCh1, dataCh2, dataCh3, dataCh4, dataStatus);

        super.stop();
    }
}
