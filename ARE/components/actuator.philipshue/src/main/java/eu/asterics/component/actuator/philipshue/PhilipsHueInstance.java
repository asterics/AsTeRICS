
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
 * One instance of this plugin can control one specific Philips Hue Light.
 * <p>
 * The cmd input port expects JSON (see https://developers.meethue.com/develop/hue-api/lights-api/)
 * and sends the command to the specified device.
 * This can be achieved by using a StringDispatcher and a ButtonGrid.
 * The cmdResponse output port returns the anwser from the bridge (in JSON).
 * <p>
 * The currentState output port periodically returns the state the defined light.
 * The polling rate can be set via the updateRate property (in milliseconds).
 * <p>
 * Additionally it is possible to control all connected Philips Hue devices
 * (group 0) or only the specified device via event triggers.
 * <p>
 * The tick event is fired once for each update cycle,
 * the statusChanged event every time the state of any light changes
 * (be it power state, hue, saturation or brightness).
 * <p>
 * When the defined light changes its power state (is turned on or off)
 * one of two events is fires: turnedOn or turnedOff.
 */

package eu.asterics.component.actuator.philipshue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;

/**
 * This plugin allows the control of Philips Hue Devices via the official Philips Hue hardware bridge.
 *
 * @author Benjamin Medicke [bmedicke@gmail.com]
 */
public class PhilipsHueInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opCmd = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opCurrentState = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort etpStatusChanged = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTick = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTurnedOn = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTurnedOff = new DefaultRuntimeEventTriggererPort();

    // target constants:
    final static String DEFAULT_LIGHT = "/lights/2";
    final static String ALL_LIGHTS = "/groups/0";

    // The Philips Hue API expects different endpoints for talking to lights and groups.
    // endpoint constants:
    final static String ENDPOINT_GROUP = "/action";
    final static String ENDPOINT_LIGHT = "/state";

    // payload constants:
    final static String LIGHTS_ON = "{\"on\":true}";
    final static String LIGHTS_OFF = "{\"on\":false}";
    final static String LIGHTS_WHITE = "{\"on\":true, \"sat\": 121, \"hue\": 8597}";
    final static String LIGHTS_RED = "{\"on\":true, \"sat\": 121, \"hue\": 0}";
    final static String LIGHTS_GREEN = "{\"on\":true, \"sat\": 121, \"hue\": 18962}";
    final static String LIGHTS_BLUE = "{\"on\":true, \"sat\": 121, \"hue\": 45840}";
    final static String LIGHTS_COLORLOOP = "{\"on\":true, \"effect\": \"colorloop\"}"; // loop through hue.
    final static String LIGHTS_BRI_LOW = "{\"bri\": 25}"; // 10% brightness.
    final static String LIGHTS_BRI_HALF = "{\"bri\": 128}"; // 50% brightness.
    final static String LIGHTS_BRI_FULL = "{\"bri\": 255}"; // 100% brightness.
    final static String LIGHTS_SATURATE = "{\"sat\": 255}"; // 100% saturation.
    final static String LIGHTS_DESATURATE = "{\"sat\": 0}"; // 0% saturation.

    // properties that can be changed from the UI:
    /**
     * API Key for to authenticate with the Philips Hue Bridge
     */
    String propApiKey = "0UqD9KYkjxiFzxJsQnqXyhllxXQ0-KEw4Ifbl5i2";

    /**
     * IP address of the Philips Hue Bridge
     */
    String propIp = "192.168.0.115";

    /**
     * default target device to be controlled (if not configured otherwise)
     */
    String propTarget = DEFAULT_LIGHT;

    /**
     * update rate to fetch all necessary items [ms] once per second (1000) is a good starting point. any more than that and the Hue Bridge might start lagging.
     */
    int propUpdateRate = 1000;

    int updateRate = propUpdateRate; // this variable is read by the tick generator.

    String lastState = ""; // store previous state of Hue device to allow detection of state changes.

    boolean lastPowerState; // same thing for the power state on its own.

    // allows to periodically call a function that fetches the current state of the Hue devices:
    private ScheduledExecutorService ses;
    private Runnable fetchStateRunnable = new Runnable() {
        public void run() {
            fetchState();
        }
    };

    /**
     * The class constructor.
     */
    public PhilipsHueInstance() {
    }

    /**
     * returns an Input Port.
     *
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    public IRuntimeInputPort getInputPort(String portID) {
        if ("cmd".equalsIgnoreCase(portID)) {
            return ipCmd;
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
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("cmdResponse".equalsIgnoreCase(portID)) {
            return opCmd;
        }
        if ("currentState".equalsIgnoreCase(portID)) {
            return opCurrentState;
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
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        // predefined light:
        if ("lightOn".equalsIgnoreCase(eventPortID)) {
            return elpLightOn;
        }
        if ("lightOff".equalsIgnoreCase(eventPortID)) {
            return elpLightOff;
        }
        if ("lightWhite".equalsIgnoreCase(eventPortID)) {
            return elpLightWhite;
        }
        if ("lightRed".equalsIgnoreCase(eventPortID)) {
            return elpLightRed;
        }
        if ("lightGreen".equalsIgnoreCase(eventPortID)) {
            return elpLightGreen;
        }
        if ("lightBlue".equalsIgnoreCase(eventPortID)) {
            return elpLightBlue;
        }
        if ("lightBriLow".equalsIgnoreCase(eventPortID)) {
            return elpLightBriLow;
        }
        if ("lightBriHalf".equalsIgnoreCase(eventPortID)) {
            return elpLightBriHalf;
        }
        if ("lightBriFull".equalsIgnoreCase(eventPortID)) {
            return elpLightBriFull;
        }
        if ("lightSaturate".equalsIgnoreCase(eventPortID)) {
            return elpLightSaturate;
        }
        if ("lightDesaturate".equalsIgnoreCase(eventPortID)) {
            return elpLightDesaturate;
        }

        // all lights connected to the bridge:
        if ("allLightsOn".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsOn;
        }
        if ("allLightsOff".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsOff;
        }
        if ("allLightsWhite".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsWhite;
        }
        if ("allLightsRed".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsRed;
        }
        if ("allLightsGreen".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsGreen;
        }
        if ("allLightsBlue".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsBlue;
        }
        if ("allLightsColorloop".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsColorloop;
        }
        if ("allLightsBriLow".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsBriLow;
        }
        if ("allLightsBriHalf".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsBriHalf;
        }
        if ("allLightsBriFull".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsBriFull;
        }
        if ("allLightsSaturate".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsSaturate;
        }
        if ("allLightsDesaturate".equalsIgnoreCase(eventPortID)) {
            return elpAllLightsDesaturate;
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
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("statusChanged".equalsIgnoreCase(eventPortID)) {
            return etpStatusChanged;
        }
        if ("tick".equalsIgnoreCase(eventPortID)) {
            return etpTick;
        }
        if ("turnedOn".equalsIgnoreCase(eventPortID)) {
            return etpTurnedOn;
        }
        if ("turnedOff".equalsIgnoreCase(eventPortID)) {
            return etpTurnedOff;
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
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
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
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipCmd = new DefaultRuntimeInputPort() {
        public void receiveData(byte[] data) {
            String cmd = "";
            cmd = ConversionUtils.stringFromBytes(data);
            String cmdResponse = sendPayload(propTarget, ENDPOINT_LIGHT, cmd);
            opCmd.sendData(ConversionUtils.stringToBytes(cmdResponse));
            try {
                opCurrentState.sendData(ConversionUtils.stringToBytes(getCurrentState()));
            } catch (IOException e) {
                AstericsErrorHandling.instance.reportDebugInfo(PhilipsHueInstance.this, "HueConnectionError: Could not fetch current state: " + e.getMessage());
            }
        }
    };

    /**
     * Event Listerner Ports.
     */

    // event listener for predefined light:

    final IRuntimeEventListenerPort elpLightOn = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_ON);
        }
    };

    final IRuntimeEventListenerPort elpLightOff = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_OFF);
        }
    };

    final IRuntimeEventListenerPort elpLightWhite = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_WHITE);
        }
    };

    final IRuntimeEventListenerPort elpLightRed = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_RED);
        }
    };

    final IRuntimeEventListenerPort elpLightGreen = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_GREEN);
        }
    };

    final IRuntimeEventListenerPort elpLightBlue = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_BLUE);
        }
    };

    final IRuntimeEventListenerPort elpLightBriLow = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_BRI_LOW);
        }
    };

    final IRuntimeEventListenerPort elpLightBriHalf = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_BRI_HALF);
        }
    };

    final IRuntimeEventListenerPort elpLightBriFull = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_BRI_FULL);
        }
    };

    final IRuntimeEventListenerPort elpLightSaturate = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_SATURATE);
        }
    };

    final IRuntimeEventListenerPort elpLightDesaturate = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            sendPayload(propTarget, ENDPOINT_LIGHT, LIGHTS_DESATURATE);
        }
    };

    // event listener for controlling all lights connected to the bridge at once:
    final IRuntimeEventListenerPort elpAllLightsOn = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsOn();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsOff = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsOff();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsWhite = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsWhite();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsRed = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsRed();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsGreen = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsGreen();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsBlue = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsBlue();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsColorloop = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsColorloop();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsBriLow = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsBriLow();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsBriHalf = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsBriHalf();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsBriFull = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsBriFull();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsSaturate = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsSaturate();
        }
    };

    final IRuntimeEventListenerPort elpAllLightsDesaturate = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            lightsDesaturate();
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        // log loading of the plugin:
        AstericsErrorHandling.instance.reportDebugInfo(this, "start()");

        super.start();
        ses = Executors.newScheduledThreadPool(1);
        ses.schedule(fetchStateRunnable, updateRate, TimeUnit.MILLISECONDS);
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
        ses.shutdownNow();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        ses = Executors.newScheduledThreadPool(1);
        ses.schedule(fetchStateRunnable, updateRate, TimeUnit.MILLISECONDS);
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        // log stopping of the plugin:
        AstericsErrorHandling.instance.reportDebugInfo(this, "stop()");

        super.stop();
        ses.shutdownNow();
    }

    // functions for controlling all lights at the same time (group 0):

    private String lightsOn() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_ON);
    }

    private String lightsOff() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_OFF);
    }

    private String lightsWhite() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_WHITE);
    }

    private String lightsRed() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_RED);
    }

    private String lightsGreen() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_GREEN);
    }

    private String lightsBlue() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_BLUE);
    }

    private String lightsColorloop() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_COLORLOOP);
    }

    private String lightsBriLow() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_BRI_LOW);
    }

    private String lightsBriHalf() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_BRI_HALF);
    }

    private String lightsBriFull() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_BRI_FULL);
    }

    private String lightsSaturate() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_SATURATE);
    }

    private String lightsDesaturate() {
        return sendPayload(ALL_LIGHTS, ENDPOINT_GROUP, LIGHTS_DESATURATE);
    }

    /**
     * Fetch current state of the configured (via property) light (power state, hue, saturation, brightness, effects, etc.)
     * 
     * @return state as JSON
     */
    private String getCurrentState() throws IOException {
        return getResponse(propTarget);
    }

    /**
     * getResponse() uses the GET method and returns JSON.
     *
     * @param target
     *            URL of the Hue bridge
     * @return JSON response from the Hue bridge
     */
    private String getResponse(String target) throws IOException {
        StringBuffer response = new StringBuffer();

        URL url = new URL("http://" + propIp + "/api/" + propApiKey + target);
        AstericsErrorHandling.instance.reportDebugInfo(this, "calling: " + url.toString());

        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            con = (HttpURLConnection) url.openConnection();

            // setup connection:
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            // read body of response:
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // log return code and response body:
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "response: " + response.toString() + " with status code " + String.valueOf(con.getResponseCode()));
        } finally {
            // clean up:
            if (in != null) in.close();
            if (con != null) con.disconnect();
        }

        return response.toString(); // return string representation of response.
    }

    /**
     * sendPayload() uses the PUT method and expects JSON as a payload. It returns JSON.
     * 
     * @param target
     *            URL of the Hue Bridge
     * @param endpoint
     *            should be "/state" for lights and "/action" for groups
     * @param payload
     *            JSON that will be sent to the bridge (https://developers.meethue.com/develop/hue-api)
     * @return response of HTTP call (usually JSON)
     */
    private String sendPayload(String target, String endpoint, String payload) {
        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL("http://" + propIp + "/api/" + propApiKey + target + endpoint);
            AstericsErrorHandling.instance.reportDebugInfo(this, "sendPayload: calling: " + url.toString() + "with payload: " + payload);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // setup connection:
            con.setRequestMethod("PUT");
            con.setDoOutput(true); // to add parameters to the request.
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            // write payload:
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();

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
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "response: " + response.toString() + " with status code " + String.valueOf(con.getResponseCode()));
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this, "HueConnectionError: " + e.getMessage());
        }

        return response.toString(); // return string representation of response.
    }

    /**
     * This function is periodically called by the TickGenerator instance. See updateRate variable.
     */
    public void fetchState() {

        try {
            etpTick.raiseEvent(); // fire tick event.

            AstericsErrorHandling.instance.reportDebugInfo(this, "fetchState: Fetching data, updateRate: " + updateRate);

            // fetch the current settings of the light (as JSON):
            String currentState = getCurrentState();

            // strip possible whitespaces and extract power state from JSON response:
            boolean currentPowerState = currentState.replaceAll(" ", "").contains("\"on\":true");

            // detect changed state of Hue devices:
            if (!lastState.equals(currentState)) {
                opCurrentState.sendData(ConversionUtils.stringToBytes(currentState));
                etpStatusChanged.raiseEvent(); // fire statusChanged event.
            }

            // send events (turnedOn/turnedOff) if the power state has changed:
            if (lastPowerState != currentPowerState) {
                // strip possible whitespaces and check JSON:
                if (currentState.replaceAll(" ", "").contains("\"on\":true")) {
                    etpTurnedOn.raiseEvent();
                } else {
                    etpTurnedOff.raiseEvent();
                }
            }

            // update state variables for next tick.
            lastPowerState = currentPowerState;
            lastState = currentState;

            AstericsErrorHandling.instance.reportDebugInfo(this, "Scheduling next bridge polling, updateRate: " + updateRate);
            ses.schedule(fetchStateRunnable, updateRate, TimeUnit.MILLISECONDS);
        } catch (IOException e) {
            // AstericsErrorHandling.instance.reportError(this, "HueConnectionError: " + e.getMessage());
            AstericsErrorHandling.instance.reportDebugInfo(this, "HueConnectionError: polling failed: " + e.getMessage());
            // In case of an error slow down polling to not flood the user with error messages.
            ses.schedule(fetchStateRunnable, updateRate * 10, TimeUnit.MILLISECONDS);
        }
    }
}