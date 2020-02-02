

/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   666  d88P  Y88b d88P  Y88b
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
 *         This project has been funded by the European Commission,
 *                      Grant Agreement Number 247730
 *
 *
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * One instance of this plugin can fire events for one specified Philips Hue sensor.
 * <p>
 * The currentState output port periodically returns the state of the defined sensor.
 * The polling rate can be set via the updateRate property (in milliseconds).
 * <p>
 * The tick event is fired once for each update cycle,
 * the statusChanged event every time the status of the sensor changes.
 * <p>
 * There are two supported sensor types:
 * <p>
 * Motion Sensors that fire the event
 * - motionDetected
 * <p>
 * and Dimmer Switches that send events corresponding to the button pressed:
 * - dimmerButtonOn
 * - dimmerButtonBrighter
 * - dimmerButtonDarker
 * - dimmerButtonOff
 */


package eu.asterics.component.sensor.philipshuesensor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * This plugin allows the use of Philips Hue sensor devices as input via the official Philips Hue hardware bridge.
 *
 * @author Benjamin Medicke [bmedicke@gmail.com]
 */
public class PhilipsHueSensorInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opCurrentState = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort etpStatusChanged = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTick = new DefaultRuntimeEventTriggererPort();

    final IRuntimeEventTriggererPort etpDimmerButtonOn = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpDimmerButtonBrighter = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpDimmerButtonDarker = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpDimmerButtonOff = new DefaultRuntimeEventTriggererPort();

    final IRuntimeEventTriggererPort etpMotionDetected = new DefaultRuntimeEventTriggererPort();

    // target constants:
    final static String DEFAULT_SENSOR = "/sensors/11";
    final static String ALL_SENSORS = "/sensors";

    // properties that can be changed from the UI:
    /**
     * API Key for to authenticate with the Philips Hue Bridge
     */
    String propApiKey = "";

    /**
     * IP address of the Philips Hue Bridge
     */
    String propIp = "";

    /**
     * default target device to be controlled
     * (if not configured otherwise)
     */
    String propTarget = DEFAULT_SENSOR;

    /**
     * update rate to fetch all necessary items [ms]
     * once per second (1000) is a good starting point. any more than that and the
     * Hue Bridge might start lagging.
     */
    int propUpdateRate = 1000;

    int updateRate = propUpdateRate; // this variable is read by the tick generator.

    String lastState = ""; // store previous state of Hue device to allow detection of state changes.


    // allows to periodically call a function that fetches the current state of the Hue devices:
    private final TickGenerator tg = new TickGenerator(this);

    /**
     * The class constructor.
     */
    public PhilipsHueSensorInstance() {
    }

    /**
     * returns an Output Port.
     *
     * @param portID the name of the port
     * @return the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("currentState".equalsIgnoreCase(portID)) {
            return opCurrentState;
        }

        return null;
    }


    /**
     * returns an Event Triggerer Port.
     *
     * @param eventPortID the name of the port
     * @return the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("statusChanged".equalsIgnoreCase(eventPortID)) {
            return etpStatusChanged;
        }
        if ("tick".equalsIgnoreCase(eventPortID)) {
            return etpTick;
        }
        if ("dimmerButtonOn".equalsIgnoreCase(eventPortID)) {
            return etpDimmerButtonOn;
        }
        if ("dimmerButtonBrighter".equalsIgnoreCase(eventPortID)) {
            return etpDimmerButtonBrighter;
        }
        if ("dimmerButtonDarker".equalsIgnoreCase(eventPortID)) {
            return etpDimmerButtonDarker;
        }
        if ("dimmerButtonOff".equalsIgnoreCase(eventPortID)) {
            return etpDimmerButtonOff;
        }
        if ("motionDetected".equalsIgnoreCase(eventPortID)) {
            return etpMotionDetected;
        }

        return null;
    }

    /**
     * returns the value of the given property.
     *
     * @param propertyName the name of the property
     * @return the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName) {
        if ("apiKey".equalsIgnoreCase(propertyName)) {
            return propApiKey;
        }
        if ("ip".equalsIgnoreCase(propertyName)) {
            return propIp;
        }
        if ("target".equalsIgnoreCase(propertyName)) {
            return propTarget;
        }
        if ("updateRate".equalsIgnoreCase(propertyName)) {
            return propUpdateRate;
        }

        return null;
    }

    /**
     * sets a new value for the given property.
     *
     * @param propertyName the name of the property
     * @param newValue     the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("apiKey".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propApiKey;
            propApiKey = (String) newValue;
            return oldValue;
        }
        if ("ip".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propIp;
            propIp = (String) newValue;
            return oldValue;
        }
        if ("target".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propTarget;
            propTarget = (String) newValue;
            return oldValue;
        }
        if ("updateRate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUpdateRate;
            propUpdateRate = Integer.parseInt(newValue.toString());
            updateRate = propUpdateRate;
            return oldValue;
        }

        return null;
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        // log loading of the plugin:
        AstericsErrorHandling.instance.reportDebugInfo(this, "start()");

        super.start();
        tg.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        tg.stop();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        tg.start();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        // log stopping of the plugin:
        AstericsErrorHandling.instance.reportDebugInfo(this, "stop()");

        super.stop();
        tg.stop();
    }

    /**
     * Fetch current state of the configured (via property) sensor
     * (power state, hue, saturation, brightness, effects, etc.)
     *
     * @return state as JSON
     */
    private String getCurrentState() {
        return getResponse(propTarget);
    }

    /**
     * getResponse() uses the GET method and returns JSON.
     *
     * @param target URL of the Hue bridge
     * @return JSON response from the Hue bridge
     */
    private String getResponse(String target) {
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL("http://" + propIp + "/api/" + propApiKey + target);
            AstericsErrorHandling.instance.reportDebugInfo(this, "calling: " + url.toString());

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // setup connection:
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            // read body of response:
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // clean up:
            in.close();
            con.disconnect();

            // log return code and response body:
            AstericsErrorHandling.instance.reportDebugInfo(this, "response: " + response.toString()
                    + " with status code " + String.valueOf(con.getResponseCode()));
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this, "HueConnectionError: " + e.getMessage());
        }

        return response.toString(); // return string representation of response.
    }

    /**
     * This function is periodically called by the TickGenerator instance.
     * See updateRate variable.
     */
    public void fetchState() {
        etpTick.raiseEvent(); // fire tick event.

        //AstericsErrorHandling.instance.reportDebugInfo(this, "Fetching data, updateRate: " + updateRate);

        // fetch the current settings of the light (as JSON):
        String currentState = getCurrentState();

        // strip possible whitespaces and extract sensor type from JSON response:
        boolean isDimmerSwitch = currentState.replaceAll(" ", "").contains("\"productname\":\"Huedimmerswitch\"");
        boolean isMotionSensor = currentState.replaceAll(" ", "").contains("\"productname\":\"Huemotionsensor\"");

        // detect changed state of Hue device:
        if (!lastState.equals(currentState)) {

            // on button pressed:
            if (isDimmerSwitch && currentState.replaceAll(" ", "").contains("\"state\":{\"buttonevent\":1002"))
                etpDimmerButtonOn.raiseEvent();

            // brightness up button pressed:
            else if (isDimmerSwitch && currentState.replaceAll(" ", "").contains("\"state\":{\"buttonevent\":2002"))
                etpDimmerButtonBrighter.raiseEvent();

            // brightness down button pressed:
            else if (isDimmerSwitch && currentState.replaceAll(" ", "").contains("\"state\":{\"buttonevent\":3002"))
                etpDimmerButtonDarker.raiseEvent();

            // off button pressed:
            else if (isDimmerSwitch && currentState.replaceAll(" ", "").contains("\"state\":{\"buttonevent\":4002"))
                etpDimmerButtonOff.raiseEvent();

            // presence detected by motion sensor:
            else if (isMotionSensor && currentState.replaceAll(" ", "").contains("\"presence\":true")) {
                etpMotionDetected.raiseEvent();
            }

            opCurrentState.sendData(ConversionUtils.stringToBytes(currentState));
            etpStatusChanged.raiseEvent(); // fire statusChanged event.
        }

        // update state variables for next tick.
        lastState = currentState;
    }
}