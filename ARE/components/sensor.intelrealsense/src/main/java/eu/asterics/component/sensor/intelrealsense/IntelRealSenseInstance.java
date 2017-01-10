
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

package eu.asterics.component.sensor.intelrealsense;

import java.util.concurrent.Callable;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * This plugin interfaces the Intel Real Sense 3D cameras and provide head
 * tracking data and triggers events in case of facial expressions.
 * 
 * 
 * 
 * @author Christoph Acker [acker_christoph@yahoo.de] Date: 30/06/2016
 */
public class IntelRealSenseInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opH = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opW = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opX = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opY = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opRoll = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opYaw = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opPitch = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpBrowRaiserLeft = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpBrowRaiserRight = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpBrowLowererLeft = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpBrowLowererRight = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpSmile = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpKiss = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpMouthOpen = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpThongueOut = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesClosedLeft = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesClosedRight = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesTurnLeft = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesTurnRight = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesUp = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpEyesDown = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpPuffLeft = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpPuffRight = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    int propDeviceModel = 0;
    boolean propEnableExpressions = true;
    boolean propdisplayGUI = true;
    int propScoreBrowRaiserLeft = 50;
    int propScoreBrowRaiserRight = 50;
    int propScoreBrowLowererLeft = 50;
    int propScoreBrowLowererRight = 50;
    int propScoreSmile = 50;
    int propScoreKiss = 50;
    int propScoreMouthOpen = 50;
    int propScoreThongueOut = 50;
    int propScoreEyesClosedLeft = 50;
    int propScoreEyesClosedRight = 50;
    int propScoreEyesTurnLeft = 50;
    int propScoreEyesTurnRight = 50;
    int propScoreEyesUp = 50;
    int propScoreEyesDown = 50;
    int propScorePuffLeft = 50;
    int propScorePuffRight = 50;

    // declare member variables here

    int deviceModels[] = new int[] { 2097166, 2097167, 2097183, 2097168 };
    int isExpressionON = 0;
    int showDisplay = 0;
    private final BridgeIntelRealSense bridge = new BridgeIntelRealSense(this);

    /**
     * The class constructor.
     */
    public IntelRealSenseInstance() {
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
        if ("h".equalsIgnoreCase(portID)) {
            return opH;
        }
        if ("w".equalsIgnoreCase(portID)) {
            return opW;
        }
        if ("x".equalsIgnoreCase(portID)) {
            return opX;
        }
        if ("y".equalsIgnoreCase(portID)) {
            return opY;
        }
        if ("roll".equalsIgnoreCase(portID)) {
            return opRoll;
        }
        if ("yaw".equalsIgnoreCase(portID)) {
            return opYaw;
        }
        if ("pitch".equalsIgnoreCase(portID)) {
            return opPitch;
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
        if ("browRaiserLeft".equalsIgnoreCase(eventPortID)) {
            return etpBrowRaiserLeft;
        }
        if ("browRaiserRight".equalsIgnoreCase(eventPortID)) {
            return etpBrowRaiserRight;
        }
        if ("browLowererLeft".equalsIgnoreCase(eventPortID)) {
            return etpBrowLowererLeft;
        }
        if ("browLowererRight".equalsIgnoreCase(eventPortID)) {
            return etpBrowLowererRight;
        }
        if ("smile".equalsIgnoreCase(eventPortID)) {
            return etpSmile;
        }
        if ("kiss".equalsIgnoreCase(eventPortID)) {
            return etpKiss;
        }
        if ("mouthOpen".equalsIgnoreCase(eventPortID)) {
            return etpMouthOpen;
        }
        if ("thongueOut".equalsIgnoreCase(eventPortID)) {
            return etpThongueOut;
        }
        if ("eyesClosedLeft".equalsIgnoreCase(eventPortID)) {
            return etpEyesClosedLeft;
        }
        if ("eyesClosedRight".equalsIgnoreCase(eventPortID)) {
            return etpEyesClosedRight;
        }
        if ("eyesTurnRight".equalsIgnoreCase(eventPortID)) {
            return etpEyesTurnRight;
        }
        if ("eyesTurnLeft".equalsIgnoreCase(eventPortID)) {
            return etpEyesTurnLeft;
        }
        if ("eyesUp".equalsIgnoreCase(eventPortID)) {
            return etpEyesUp;
        }
        if ("eyesDown".equalsIgnoreCase(eventPortID)) {
            return etpEyesDown;
        }
        if ("puffLeft".equalsIgnoreCase(eventPortID)) {
            return etpPuffLeft;
        }
        if ("puffRight".equalsIgnoreCase(eventPortID)) {
            return etpPuffRight;
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
        if ("deviceModel".equalsIgnoreCase(propertyName)) {
            return propDeviceModel;
        }
        if ("enableExpressions".equalsIgnoreCase(propertyName)) {
            return propEnableExpressions;
        }
        if ("displayGUI".equalsIgnoreCase(propertyName)) {
            return propdisplayGUI;
        }
        if ("scoreBrowRaiserLeft".equalsIgnoreCase(propertyName)) {
            return propScoreBrowRaiserLeft;
        }
        if ("scoreBrowRaiserRight".equalsIgnoreCase(propertyName)) {
            return propScoreBrowRaiserRight;
        }
        if ("scoreBrowLowererLeft".equalsIgnoreCase(propertyName)) {
            return propScoreBrowLowererLeft;
        }
        if ("scoreBrowLowererRight".equalsIgnoreCase(propertyName)) {
            return propScoreBrowLowererRight;
        }
        if ("scoreSmile".equalsIgnoreCase(propertyName)) {
            return propScoreSmile;
        }
        if ("scoreKiss".equalsIgnoreCase(propertyName)) {
            return propScoreKiss;
        }
        if ("scoreMouthOpen".equalsIgnoreCase(propertyName)) {
            return propScoreMouthOpen;
        }
        if ("scoreThongueOut".equalsIgnoreCase(propertyName)) {
            return propScoreThongueOut;
        }
        if ("scoreEyesClosedLeft".equalsIgnoreCase(propertyName)) {
            return propScoreEyesClosedLeft;
        }
        if ("scoreEyesClosedRight".equalsIgnoreCase(propertyName)) {
            return propScoreEyesClosedRight;
        }
        if ("scoreEyesTurnLeft".equalsIgnoreCase(propertyName)) {
            return propScoreEyesTurnLeft;
        }
        if ("scoreEyesTurnRight".equalsIgnoreCase(propertyName)) {
            return propScoreEyesTurnRight;
        }
        if ("scoreEyesUp".equalsIgnoreCase(propertyName)) {
            return propScoreEyesUp;
        }
        if ("scoreEyesDown".equalsIgnoreCase(propertyName)) {
            return propScoreEyesDown;
        }
        if ("scorePuffLeft".equalsIgnoreCase(propertyName)) {
            return propScorePuffLeft;
        }
        if ("scorePuffRight".equalsIgnoreCase(propertyName)) {
            return propScorePuffRight;
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
        if ("deviceModel".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDeviceModel;
            propDeviceModel = Integer.parseInt((String) newValue);
            return oldValue;
        }
        if ("enableExpressions".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propEnableExpressions;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propEnableExpressions = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propEnableExpressions = false;
            }
            return oldValue;
        }
        if ("displayGUI".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propdisplayGUI;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propdisplayGUI = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propdisplayGUI = false;
            }
            return oldValue;
        }
        if ("scoreBrowRaiserLeft".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreBrowRaiserLeft;
            propScoreBrowRaiserLeft = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreBrowRaiserRight".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreBrowRaiserRight;
            propScoreBrowRaiserRight = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreBrowLowererLeft".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreBrowLowererLeft;
            propScoreBrowLowererLeft = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreBrowLowererRight".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreBrowLowererRight;
            propScoreBrowLowererRight = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreSmile".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreSmile;
            propScoreSmile = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreKiss".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreKiss;
            propScoreKiss = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreMouthOpen".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreMouthOpen;
            propScoreMouthOpen = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreThongueOut".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreThongueOut;
            propScoreThongueOut = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesClosedLeft".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesClosedLeft;
            propScoreEyesClosedLeft = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesClosedRight".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesClosedRight;
            propScoreEyesClosedRight = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesTurnLeft".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesTurnLeft;
            propScoreEyesTurnLeft = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesTurnRight".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesTurnRight;
            propScoreEyesTurnRight = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesUp".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesUp;
            propScoreEyesUp = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scoreEyesDown".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScoreEyesDown;
            propScoreEyesDown = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scorePuffLeft".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScorePuffLeft;
            propScorePuffLeft = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("scorePuffRight".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propScorePuffRight;
            propScorePuffRight = Integer.parseInt(newValue.toString());
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
        AstericsErrorHandling.instance.getLogger().fine("\n\nIntel Real Sense selected model: " + propDeviceModel);

        if (propEnableExpressions) {
            isExpressionON = 1;
        } else {
            isExpressionON = 0;
        }

        if (propdisplayGUI) {
            showDisplay = 1;
        } else {
            showDisplay = 0;
        }

        AstericsThreadPool.instance.execute(new Callable() {

            @Override
            public Object call() throws Exception {
                if (bridge.init(deviceModels[propDeviceModel], isExpressionON, showDisplay) == -1) {
                    throw new RuntimeException("Init of Intel Real Sense camera failed.");
                }
                AstericsErrorHandling.instance.getLogger()
                        .fine("Intel Real Sense camera initialized, starting tracking");
                bridge.startTracking();
                AstericsErrorHandling.instance.getLogger().fine("Intel Real Sense camera stopped tracking");

                return null;
            }

        });
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        bridge.pause();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        bridge.resume();
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        bridge.deactivate();
        super.stop();
    }
}