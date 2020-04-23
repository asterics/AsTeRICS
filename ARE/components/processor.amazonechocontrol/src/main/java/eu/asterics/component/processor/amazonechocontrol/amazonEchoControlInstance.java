
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

package eu.asterics.component.processor.amazonechocontrol;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * openHAB-based AsTeRICS Plugin to control Amazon Echo Dot
 * (see:
 * https://github.com/openhab/openhab/wiki/REST-API
 * and
 * https://www.openhab.org/addons/bindings/amazonechocontrol/#amazon-echo-control-binding)
 * <p>
 * based on Benjamin Aigners openHAB Plugin, which i updated
 *
 * @author Manuel Nagel [sa17b004@technikum-wien.at Date: 11.12.2019 Time: 4:23 PM
 */
public class amazonEchoControlInstance extends AbstractRuntimeComponentInstance {
    /**
     * Using this hostname to connect to openHAB e.g.: http://localhost:8080
     * <p>
     * REST API: http://localhost:8080/rest
     */
    static private String hostname;

    /**
     * This port will be used to access openHAB (default: 8080 for non-HTTPS,
     * 8443 for HTTPS)
     */
    String port = "8080";

    /**
     * If a authentication is configured, use this username for HTTP
     * authentication WARNING: if the username is given, this component WILL
     * authenticate, even if no username is necessary!
     */
    String username = "";

    /**
     * Password, corresponding to username
     */
    String password = "";

    /**
     * Use lazy certificate check for HTTPS If this is set to true, the SSL
     * certificate check will be bypassed
     */
    boolean lazyCertificate = true;

    /**
     * protocol to be connected with, either http or https
     */
    int propProtocol = 0;
    String protocols[] = new String[] { "http", "https" };

    /**
     * update rate to fetch all necessary items [ms]
     */
    int updateRate = 1000;

    /**
     * item name for fetching data, output port item1
     */
    String item1out = "";
    /**
     * item name for fetching data, output port item2
     */
    String item2out = "";
    /**
     * item name for fetching data, song title output
     */
    String item2outtitle = "";

    /**
     * get json formatted string
     */
    String json_input = "";

    /**
     * get itemsuffix from json
     */
    String itemSuffix = "";

    /**
     * response from command
     */
    String response_output = "OK";

    /**
     * all available items of the selected openHAB instance
     */
    List<String> items;


    // output ports
    public final IRuntimeOutputPort opCurrentState = new DefaultRuntimeOutputPort();
    public final IRuntimeOutputPort opResponse = new DefaultRuntimeOutputPort();
    public final IRuntimeOutputPort opCurrentTitle = new DefaultRuntimeOutputPort();

    // event trigger
    final IRuntimeEventTriggererPort etpTurnedOn = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpTurnedOff = new DefaultRuntimeEventTriggererPort();

    // tick generator for fetching item state
    private final TickGenerator tg = new TickGenerator(this);

    /**
     * The class constructor.
     */
    public amazonEchoControlInstance() {
    }

    /**
     * returns an Input Port.
     *
     * @param portID the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("jsonCommand".equalsIgnoreCase(portID)) {
            return ipItem1;
        }
        return null;
    }

    /**
     * returns an Output Port.
     *
     * @param portID the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("currentState".equalsIgnoreCase(portID)) {
            return opCurrentState;
        }
        if ("cmdResponse".equalsIgnoreCase(portID)) {
            return opResponse;
        }
        if("currentTitle".equalsIgnoreCase(portID)) {
            return opCurrentTitle;
        }
        return null;
    }

    /**
     * returns an Event Listener Port.
     *
     * @param eventPortID the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("playerPlay".equalsIgnoreCase(eventPortID)) {
            return elPlayerPlay;
        }
        if ("playerPause".equalsIgnoreCase(eventPortID)) {
            return elPlayerPause;
        }
        if ("playerNext".equalsIgnoreCase(eventPortID)) {
            return elPlayerNext;
        }
        if ("playerPrevious".equalsIgnoreCase(eventPortID)) {
            return elPlayerPrevious;
        }
        if ("volumeMute".equalsIgnoreCase(eventPortID)) {
            return elVolumeMute;
        }
        if ("volume30".equalsIgnoreCase(eventPortID)) {
            return elVolume30;
        }
        if ("weather".equalsIgnoreCase(eventPortID)) {
            return elWeather;
        }
        if ("tellStory".equalsIgnoreCase(eventPortID)) {
            return elTellStory;
        }
        if ("traffic".equalsIgnoreCase(eventPortID)) {
            return elTraffic;
        }
        if ("singASong".equalsIgnoreCase(eventPortID)) {
            return elSingASong;
        }
        if ("flashBriefing".equalsIgnoreCase(eventPortID)) {
            return elFlashBriefing;
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
     * @param propertyName the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        // general properties
        if ("updaterate".equalsIgnoreCase(propertyName)) {
            return updateRate;
        }
        if ("hostname".equalsIgnoreCase(propertyName)) {
            return hostname;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            return port;
        }
        if ("protocol".equalsIgnoreCase(propertyName)) {
            return propProtocol;
        }
        if ("lazyCertificates".equalsIgnoreCase(propertyName)) {
            return lazyCertificate;
        }
        if ("username".equalsIgnoreCase(propertyName)) {
            return username;
        }
        if ("password".equalsIgnoreCase(propertyName)) {
            return password;
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     *
     * @param[in] propertyName the name of the property
     * @param[in] newValue the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        AstericsErrorHandling.instance.reportDebugInfo(null, "SetRuntimeProperty: " + propertyName + " " + newValue);
        // general properties
        if ("updaterate".equalsIgnoreCase(propertyName)) {
            final Object oldValue = updateRate;
            updateRate = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("hostname".equalsIgnoreCase(propertyName)) {
            final Object oldValue = hostname;
            hostname = newValue.toString();
            return oldValue;
        }
        if ("port".equalsIgnoreCase(propertyName)) {
            final Object oldValue = port;
            port = newValue.toString();
            return oldValue;
        }
        if ("username".equalsIgnoreCase(propertyName)) {
            final Object oldValue = username;
            username = newValue.toString();
            return oldValue;
        }
        if ("password".equalsIgnoreCase(propertyName)) {
            final Object oldValue = password;
            password = newValue.toString();
            return oldValue;
        }
        if ("protocol".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propProtocol;
            propProtocol = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("lazyCertificates".equalsIgnoreCase(propertyName)) {
            final Object oldValue = lazyCertificate;
            if ("true".equalsIgnoreCase((String) newValue)) {
                lazyCertificate = true;
            } else {
                lazyCertificate = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipItem1 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            String s, s1;
            String value = "";
            json_input = ConversionUtils.stringFromBytes(data);

            AstericsErrorHandling.instance.reportDebugInfo(null, "Input:" + json_input);

            JSONObject jsonObject = new JSONObject(json_input);

            try {

                itemSuffix = jsonObject.getString("ItemSuffix");
                if (jsonObject.getString("value") != "") {
                    value = jsonObject.getString("value");
                }

                AstericsErrorHandling.instance.reportDebugInfo(null, "Input: " + itemSuffix + " state: " + value);

               if (itemSuffix != "") {
                    response_output = "OK";
                   s = getItemState(itemSuffix);
                    opCurrentState.sendData(s.getBytes());
                   AstericsErrorHandling.instance.reportDebugInfo(null, itemSuffix + " state: " + s);

                    opResponse.sendData(response_output.getBytes());
                   AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");

                }
               if (value != "") {
                    setItemState(itemSuffix, value);
               }
                //opItem1.sendData(ConversionUtils.stringToBytes(getCurrentState()));
            } catch (Exception e) {
                response_output = "ERROR";
                opResponse.sendData(response_output.getBytes());
                AstericsErrorHandling.instance.reportDebugInfo(null,
                        "Input Error,\n message: "
                                + e.getMessage());
            }


        }

    };

    /**
     * Event Listerner Ports.
     */

    // event listener for Echo Dot:

    final IRuntimeEventListenerPort elPlayerPlay = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("player", "PLAY");
            itemSuffix = "player";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "Player state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elPlayerPause = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("player", "PAUSE");
            itemSuffix = "player";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "Player state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elPlayerNext = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("player", "NEXT");
            itemSuffix = "player";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "Player state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elPlayerPrevious = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("player", "PREVIOUS");
            itemSuffix = "player";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "Player state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elVolumeMute = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("volume", "0");
            itemSuffix = "volume";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "volume state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elVolume30 = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("volume", "30");
            itemSuffix = "volume";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "volume state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elWeather = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("startCommand", "Weather");
            itemSuffix = "startCommand";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "startCommand state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elTellStory = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("startCommand", "TellStory");
            itemSuffix = "startCommand";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "startCommand state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elTraffic = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("startCommand", "Traffic");
            itemSuffix = "startCommand";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "startCommand state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elSingASong = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("startCommand", "SingASong");
            itemSuffix = "startCommand";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "startCommand state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };
    final IRuntimeEventListenerPort elFlashBriefing = new IRuntimeEventListenerPort() {
        public void receiveEvent(final String data) {
            setItemState("startCommand", "FlashBriefing");
            itemSuffix = "startCommand";
            String s;
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null,  "startCommand state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        String protocol = protocols[propProtocol];

        AstericsErrorHandling.instance.reportDebugInfo(this, "Connecting to openHAB:");
        AstericsErrorHandling.instance.reportDebugInfo(this, "Host: " + hostname + ":" + port);
        AstericsErrorHandling.instance.reportDebugInfo(this, "Username: " + username);
        AstericsErrorHandling.instance.reportDebugInfo(this, "Password: " + password);
        AstericsErrorHandling.instance.reportDebugInfo(this, "Protocol: " + protocol);
        AstericsErrorHandling.instance.reportDebugInfo(this, "Using lazyCertificates: " + lazyCertificate);
        // get all available items
        try {
            this.items = getItems();
            super.start();
            tg.start();    
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportError(this,
            "Can't connect to openHAB at "+protocol + "://" + hostname + ":" + port+". Verify the running instance in the browser (port?, username/password?),\n message: "
                            + e.getMessage());
        }    }

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
        super.stop();
        tg.stop();
    }


    public List<String> getItems() throws IOException {
        String protocol = protocols[propProtocol];

        return getList(protocol + "://" + hostname + ":" + port, "item");
    }


    public String getItemState(String item) {

        String protocol = protocols[propProtocol];
        
        try {
            String itemstate = "";
            for (String searchItem : items) {

                String[] data = searchItem.split("_", 5);

                if (data[data.length-1].equals(item)) {

                    //url = new URL("http://localhost:8080/rest/items/" + searchItem);
                    itemstate = searchItem;
                }

            }


            AstericsErrorHandling.instance.reportDebugInfo(this, "Get item (name: " + itemstate + ": " + protocol + "://"
                    + hostname + ":" + port + "/rest/items/" + itemstate + "/state");


            return httpGet(protocol + "://" + hostname + ":" + port + "/rest/items/" + itemstate + "/state");
        } catch (KeyManagementException e) {
            tg.stop();
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "KeyManagement exception, try to use lazyCertificate option (property)");
        } catch (NoSuchAlgorithmException e) {
            tg.stop();
            AstericsErrorHandling.instance.reportDebugInfo(this, "Algortihm exception, please contact the AsTeRICS team");
        } catch (IOException e) {
            // catch a wrong item name
            if (e.getMessage().equalsIgnoreCase("Not Found")) {
                AstericsErrorHandling.instance.reportDebugInfo(this,
                        "Item name '" + item + "' not found, please update your model (HTTP 404)");
            } else {
                AstericsErrorHandling.instance.reportDebugInfo(this,
                "Can't connect to openHAB at "+protocol + "://" + hostname + ":" + port+". Verify the running instance in the browser (port?, username/password?),\n message: "
                                + e.getMessage());
                response_output = "ERROR";
                opResponse.sendData(response_output.getBytes());
            }
        }
        return "";
    }

    public String setItemState(String item, String state) {

        String protocol = protocols[propProtocol];
        //List<String> itemList = new ArrayList();

        String urlParameters = state;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        try {


            // AstericsErrorHandling.instance.reportDebugInfo(this, "Set item (name: " + item + ",state: " + state + "):"
            //      + protocol + "://" + hostname + ":" + port + "/rest/items/"+item + " HTTP POST");

            URL url = new URL(protocol + "://" + hostname + ":" + port + "/rest/items/" + item);

            for (String searchItem : items) {

                String[] data = searchItem.split("_", 5);

                if (data[data.length-1].equals(item)) {
                    url = new URL("http://localhost:8080/rest/items/" + searchItem);
                }

            }

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "text/plain");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {

                wr.write(postData);
            }

            StringBuilder content;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }


            AstericsErrorHandling.instance.reportDebugInfo(this, "change to :" + state);
            return content.toString();
            //return httpGet(protocol + "://" + hostname + ":" + port + "/CMD?" + item + "=" + state);

        } catch (IOException e) {
            if (e.getMessage().equalsIgnoreCase("Not Found")) {
                AstericsErrorHandling.instance.reportDebugInfo(this,
                        "Item name '" + item + "' not found, please update your model (HTTP 404)");
            } else {
                AstericsErrorHandling.instance.reportDebugInfo(this,
                "Can't connect to openHAB at "+protocol + "://" + hostname + ":" + port+". Verify the running instance in the browser (port?, username/password?),\n message: "
                                + e.getMessage());
            }
        }
        return "";
    }

    public List<String> getList(String hostname, String type) throws IOException {
        List<String> response = new ArrayList<String>();

        try {
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "Get list (type: " + type + ": " + hostname + "/rest/" + type + "s)");

            //create JSON Array
            JSONArray jsonArray = new JSONArray(httpGet(hostname + "/rest/" + type + "s"));

            // parse all objects, and extract name
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                response.add(name);
            }
        } catch (KeyManagementException e) {
            tg.stop();
            AstericsErrorHandling.instance.reportDebugInfo(this,
                    "KeyManagement exception, try to use lazyCertificate option (property)");
        } catch (NoSuchAlgorithmException e) {
            tg.stop();
            AstericsErrorHandling.instance.reportDebugInfo(this, "Algortihm exception, please contact the AsTeRICS team");
        }

        return response;
    }

    public String httpGet(String urlStr) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        // if we wan't to ignore any certificate errors (not recommended!!!!),
        // we need to do additional stuff here
        // Based on
        // http://www.rgagnon.com/javadetails/java-fix-certificate-problem-in-HTTPS.html
        if (lazyCertificate == true) {
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameValid);

            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // check for an username, if given, authenticate via HTTP BASIC
        if (this.username.length() != 0) {
            String userPassword = username + ":" + password;
            String passphraseEncoded = MyBase64.encode(userPassword.getBytes());

            conn.setRequestProperty("Authorization", "Basic " + passphraseEncoded);
            conn.connect();
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    /**
     * From http://stackoverflow.com/questions/469695/decode-base64-data-in-java
     * There is no open base64 class in openJDK 7 (it will be in 8)
     *
     * @author GeorgeK
     */
    public static class MyBase64 {

        private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                .toCharArray();

        private static int[] toInt = new int[128];

        static {
            for (int i = 0; i < ALPHABET.length; i++) {
                toInt[ALPHABET[i]] = i;
            }
        }

        /**
         * Translates the specified byte array into Base64 string.
         *
         * @param buf the byte array (not null)
         * @return the translated Base64 string (not null)
         */
        public static String encode(byte[] buf) {
            int size = buf.length;
            char[] ar = new char[((size + 2) / 3) * 4];
            int a = 0;
            int i = 0;
            while (i < size) {
                byte b0 = buf[i++];
                byte b1 = (i < size) ? buf[i++] : 0;
                byte b2 = (i < size) ? buf[i++] : 0;

                int mask = 0x3F;
                ar[a++] = ALPHABET[(b0 >> 2) & mask];
                ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
                ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
                ar[a++] = ALPHABET[b2 & mask];
            }
            switch (size % 3) {
                case 1:
                    ar[--a] = '=';
                case 2:
                    ar[--a] = '=';
            }
            return new String(ar);
        }

        /**
         * Translates the specified Base64 string into a byte array.
         *
         * @param s the Base64 string (not null)
         * @return the byte array (not null)
         */
        public static byte[] decode(String s) {
            int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
            byte[] buffer = new byte[s.length() * 3 / 4 - delta];
            int mask = 0xFF;
            int index = 0;
            for (int i = 0; i < s.length(); i += 4) {
                int c0 = toInt[s.charAt(i)];
                int c1 = toInt[s.charAt(i + 1)];
                buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c2 = toInt[s.charAt(i + 2)];
                buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
                if (index >= buffer.length) {
                    return buffer;
                }
                int c3 = toInt[s.charAt(i + 3)];
                buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
            }
            return buffer;
        }

    }

    // Create a verifier for our hostname
    static HostnameVerifier hostnameValid = new HostnameVerifier() {
        @Override
        public boolean verify(String hostnameToVerify, SSLSession session) {
            if (hostnameToVerify.equals(hostname)) {
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * callback method to fetch the item state in a regular period (defined by
     * updateRate)
     */
    public void fetchState() {
        String s, s_title, s_event;
        AstericsErrorHandling.instance.reportDebugInfo(this, "Fetching data, updateRate: " + updateRate);
        AstericsErrorHandling.instance.reportDebugInfo(this, "UPDATE:" + itemSuffix);

        s_title = getItemState("title");
        opCurrentTitle.sendData(s_title.getBytes());
        AstericsErrorHandling.instance.reportDebugInfo(null, "current title: " + s_title);

        if (itemSuffix != "") {
            s = getItemState(itemSuffix);
            opCurrentState.sendData(s.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, itemSuffix + " state: " + s);

            opResponse.sendData(response_output.getBytes());
            AstericsErrorHandling.instance.reportDebugInfo(null, "Command complete");



        }

        s_event = getItemState("player");
        if(s_event.equals("PLAY")){
            etpTurnedOn.raiseEvent();
            AstericsErrorHandling.instance.reportDebugInfo(null, "current event trigger: " + s_event);
        }else if(s_event.equals("PAUSE")){
            etpTurnedOff.raiseEvent();
            AstericsErrorHandling.instance.reportDebugInfo(null, "current event trigger: " + s_event);
        }



    }
}