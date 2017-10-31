
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

package eu.asterics.component.actuator.keyboard;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.KeyStroke;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the Keyboard plugin, which creates local keystrokes via a JNI
 * interface to a function which performs the windows API calls A list of
 * keycodes is imported from the file "keycodes.txt" Note: not all keyboard
 * function / keycodes are supported right now.
 * 
 * @author Christoph Veigl [christoph.veigl@technikum-wien.at] Date: Apr 24,
 *         2011 Time: 04:45:08 PM 
 */
public class KeyboardInstance extends AbstractRuntimeComponentInstance {
    /**
     * Statically load the native library
     */
    static {
        try {
            System.loadLibrary("kbdevent");
            AstericsErrorHandling.instance.getLogger().fine("Loading \"kbdevent.dll\" for Keystrike generation... ok!");
        } catch (UnsatisfiedLinkError e) {
            AstericsErrorHandling.instance.getLogger().fine("could not load kbdevent.dll (only applies for Windows)");
        }
    }

    private final String ELP_SENDKEYS_NAME = "sendKeys";
    private final String ELP_PRESSKEY_NAME = "pressKey";
    private final String ELP_HOLDKEY_NAME = "holdKey";
    private final String ELP_RELEASEKEY_NAME = "releaseKey";

    private final int MODE_PRESS = 0;
    private final int MODE_HOLD = 1;
    private final int MODE_RELEASE = 2;

    private String propKeyCodeString = "";
    private int propInputMethod = 1;
    private int propWaitTime = 1000;

    private int actSendPos = 0;
    private Vector<Integer> keyCodeArray;
    private long lastUpdate = 0;
    private Hashtable<String, Integer> keyCodeMap;
    private Hashtable<Integer, String> vKeyCodeMap;

    native public int keyPress(int keycode);

    native public int keyRelease(int keycode);

    native public int keyPressSi(int keycode); // using SendInput

    native public int keyReleaseSi(int keycode); // using SendInput

    /**
     * The class constructor, loads the keycode map
     */
    public KeyboardInstance() {

        try {
            FileReader fin = new FileReader("data/actuator.keyboard/keycodes.txt");
            LineNumberReader in = new LineNumberReader(fin);
            keyCodeMap = new Hashtable<String, Integer>();
            vKeyCodeMap = new Hashtable<Integer, String>();
            String actLine = null;
            String actKey = null;
            String actVKey = null;
            String actCode = null;
            Integer code = 0;
            Integer count = 0;

            try {
                while ((actLine = in.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(actLine, " ,;\r\n");
                    if (st.hasMoreTokens()) {
                        actCode = st.nextToken();
                    } else {
                        actCode = null;
                    }
                    if (st.hasMoreTokens()) {
                        actKey = st.nextToken();
                    } else {
                        actKey = null;
                    }
                    if (st.hasMoreTokens()) {
                        actVKey = st.nextToken();
                    } else {
                        actVKey = null;
                    }
                    if ((actKey != null) && (actCode != null)) {
                        try {
                            code = Integer.parseInt(actCode);
                            keyCodeMap.put(actKey, code);
                            count++;
                        } catch (NumberFormatException e) {
                            AstericsErrorHandling.instance.reportInfo(this, "Wrong key Code " + actCode);
                        }
                    }
                    if ((actVKey != null) && (actCode != null)) {
                        try {
                            code = Integer.parseInt(actCode);
                            vKeyCodeMap.put(code, actVKey);
                        } catch (NumberFormatException e) {
                            AstericsErrorHandling.instance.reportInfo(this, "Wrong virtual key Code " + actCode);
                        }
                    }
                }
            } catch (IOException e) {
                AstericsErrorHandling.instance.reportInfo(this, "I/O Error reading KeyCode File. ");

            }
            AstericsErrorHandling.instance.reportInfo(this, "KeyCode file parsed, " + count + " Codes found.");
        } catch (FileNotFoundException e) {
            AstericsErrorHandling.instance.reportInfo(this, "KeyCode File not found ");
        }
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
        if ("keyCodes".equalsIgnoreCase(portID)) {
            return ipKeyCodes;
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
     * @param portID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (ELP_SENDKEYS_NAME.equalsIgnoreCase(eventPortID)) {
            return elpSendKeys;
        } else if (ELP_PRESSKEY_NAME.equalsIgnoreCase(eventPortID)) {
            return elpPressKey;
        } else if (ELP_HOLDKEY_NAME.equalsIgnoreCase(eventPortID)) {
            return elpHoldKey;
        } else if (ELP_RELEASEKEY_NAME.equalsIgnoreCase(eventPortID)) {
            return elpReleaseKey;
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
        if ("keyCodeString".equalsIgnoreCase(propertyName)) {
            return propKeyCodeString;
        }
        if ("inputMethod".equalsIgnoreCase(propertyName)) {
            return propInputMethod;
        }
        if ("waitTime".equalsIgnoreCase(propertyName)) {
            return propWaitTime;
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("keyCodeString".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propKeyCodeString;

            propKeyCodeString = (String) newValue;
            keyCodeTranslate();
            return oldValue;
        } else if ("inputMethod".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = this.propInputMethod;
            propInputMethod = Integer.parseInt((String) newValue);
            return oldValue;
        } else if ("waitTime".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propWaitTime;
            propWaitTime = Integer.parseInt((String) newValue);
            if (propWaitTime < 1) {
                propWaitTime = 1;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * translates the keycode-string (e.g. "{SHIFT}A") into a Keycode. the
     * keycode string is expected in property variable propKeyCodeString, the
     * Keycodes are returned in vector keyCodeArray
     */
    public void keyCodeTranslate() // TBD: Improve ! special characters might
                                   // not work for non-german keyboard layouts
    {
        Integer keyCode = 0;
        String actToken;
        int index = 0;
        char character;
        keyCodeArray = new Vector<Integer>();

        while (index < propKeyCodeString.length()) {
            try {
                character = propKeyCodeString.charAt(index);                
            } catch (Exception e) {
                character = ' ';
            }
            ;

            if (character == '{') // process a masked character specified in
                                  // keyCode-File
            {
                try {
                    actToken = propKeyCodeString.substring(index, propKeyCodeString.indexOf("}", index) + 1);
                    // Logger.getAnonymousLogger().info("next Token: " +
                    // actToken);
                } catch (Exception e) {
                    actToken = "invalid";
                }
                ;

                index += actToken.length();
                Integer n = keyCodeMap.get(actToken);
                if (n != null) {
                    // System.out.println("Token "+actToken+" found, value = " +
                    // n);
                    keyCode = n;
                }
            } else {
                KeyStroke key = KeyStroke.getKeyStroke("pressed " + Character.toUpperCase(character));
                if (null != key) {
                    if ((character >= 'A') && (character <= 'Z')) {
                        keyCodeArray.add(16); // SHIFT
                    }
                    keyCode = key.getKeyCode();
                    index++;
                } else {
                    switch (character) {
                    case ' ':
                        keyCode = 32;
                        break;
                    case '-':
                        keyCode = 189;
                        break;
                    case '+':
                        keyCode = 187;
                        break;
                    case '.':
                        keyCode = 190;
                        break;
                    case ',':
                        keyCode = 188;
                        break;
                    case '*':
                        keyCode = 106;
                        break;
                    case '<':
                        keyCode = 226;
                        break;
                    case '#':
                        keyCode = 191;
                        break;
                    case 'ä':
                        keyCode = 222;
                        break;
                    case 'ß':
                        keyCode = 219;
                        break;
                    case 'ö':
                        keyCode = 192;
                        break;
                    case 'ü':
                        keyCode = 186;
                        break;
                    case ':':
                        keyCodeArray.add(16);
                        keyCode = 190;
                        break;
                    case '_':
                        keyCodeArray.add(16);
                        keyCode = 189;
                        break;
                    case '(':
                        keyCodeArray.add(16);
                        keyCode = 56;
                        break;
                    case ')':
                        keyCodeArray.add(16);
                        keyCode = 57;
                        break;
                    case '[':
                        keyCodeArray.add(18);
                        keyCode = 56;
                        break;
                    case ']':
                        keyCodeArray.add(18);
                        keyCode = 57;
                        break;
                    case '{':
                        keyCodeArray.add(18);
                        keyCode = 55;
                        break;
                    case '}':
                        keyCodeArray.add(18);
                        keyCode = 58;
                        break;
                    case ';':
                        keyCodeArray.add(16);
                        keyCode = 188;
                        break;
                    case '/':
                        keyCodeArray.add(16);
                        keyCode = 55;
                        break;
                    case '>':
                        keyCodeArray.add(16);
                        keyCode = 226;
                        break;
                    case '?':
                        keyCodeArray.add(16);
                        keyCode = 219;
                        break;
                    case '!':
                        keyCodeArray.add(16);
                        keyCode = 49;
                        break;
                    case '&':
                        keyCodeArray.add(16);
                        keyCode = 54;
                        break;
                    case '$':
                        keyCodeArray.add(16);
                        keyCode = 52;
                        break;
                    case '\"':
                        keyCodeArray.add(16);
                        keyCode = 50;
                        break;
                    case '\'':
                        keyCodeArray.add(16);
                        keyCode = 191;
                        break;
                    case '\\':
                        keyCodeArray.add(18);
                        keyCode = 219;
                        break;
                    case '@':
                        keyCodeArray.add(18);
                        keyCode = 81;
                        break;
                    default:
                        keyCode = 32;
                        break;
                    }
                    index++;

                }
            }
            // System.out.println("Adding Code "+keyCode+" for character
            // "+character);
            keyCodeArray.add(keyCode);
            keyCode = 0;
        }
        actSendPos = 0;
    }

    public String getVirtualKeycode(int actcode) // TBD: improve!
                                                 // jNativehook virtual keycodes
                                                 // do not fit german keyboard
                                                 // layout !
    {
        String val = vKeyCodeMap.get(actcode);
        if (val != null) {
            // System.out.println("Vitual Keycode for "+actcode+" found, value =
            // " + val);
        } else {
            switch (actcode) {
            case 189:
                val = "VC_MINUS";
                break;
            case 187:
                val = "VC_PLUS";
                break;
            case 190:
                val = "VC_PERIOD";
                break;
            case 188:
                val = "VC_COMMA";
                break;
            case 106:
                val = "VC_MULTIPLY";
                break;
            // case '<': keyCode=226; val="VC_LESSER";break;
            // case '#': keyCode=191; val="VC_NUMBER";break;
            // case ':': keyCodeArray.add(16);keyCode=190; val="VC_";break;
            // case 189: val="VC_UNDERSCORE";break;
            case 56:
                val = "VC_OPEN_BRACKET";
                break;
            case 57:
                val = "VC_CLOSE_BRACKET";
                break;
            // case '[': keyCodeArray.add(18);keyCode=56; val="VC_";break;
            // case ']': keyCodeArray.add(18);keyCode=57; val="VC_";break;
            // case '{': keyCodeArray.add(18);keyCode=55; val="VC_";break;
            // case '}': keyCodeArray.add(18);keyCode=58; val="VC_";break;
            // case 188: val="VC_SEMICOLON";break;
            case 55:
                val = "VC_SLASH";
                break;
            // case '>': keyCodeArray.add(16);keyCode=226;
            // val="VC_GREATER";break;
            // case '?': keyCodeArray.add(16);keyCode=219; val="VC_";break;
            // case '!': keyCodeArray.add(16);keyCode=49; val="VC_";break;
            // case '&': keyCodeArray.add(16);keyCode=54; val="VC_";break;
            // case '$': keyCodeArray.add(16);keyCode=52; val="VC_";break;
            // case '\"': keyCodeArray.add(16);keyCode=50; val="VC_QUOTE";break;
            case 191:
                val = "VC_QUOTE";
                break;
            case 219:
                val = "VC_BACK_SLASH";
                break;
            // case '@': keyCodeArray.add(18);keyCode=81; val="VC_";break;

            default:
                char character = (char) actcode;
                val = "VC_" + Character.toUpperCase(character);
                // System.out.println("assuming Vitual Keycode for "+actcode+" =
                // " + val);
            }
        }
        return val;
    }

    public void sendKeyPress(int actcode) {
        switch (propInputMethod) {
        case 0:
            keyPress(actcode);
            break;
        case 1:
            keyPressSi(actcode);
            break;
        case 2:

            int virtualKeycode = 0;
            Field f;
            try {

                String val = getVirtualKeycode(actcode);
                f = NativeKeyEvent.class.getField(val);
                Class<?> t = f.getType();
                if (t == int.class) {
                    try {
                        virtualKeycode = f.getInt(null);
                        // AstericsErrorHandling.instance.getLogger().fine("press
                        // virtual keycode="+virtualKeycode);

                        NativeKeyEvent keyEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED,
                                System.currentTimeMillis(), 0x00, // Modifiers
                                0x00, // Raw Code
                                virtualKeycode, // NativeKeyEvent.VC_UNDEFINED,
                                NativeKeyEvent.CHAR_UNDEFINED, NativeKeyEvent.KEY_LOCATION_STANDARD);

                        GlobalScreen.postNativeEvent(keyEvent);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        AstericsErrorHandling.instance.getLogger()
                                .fine("JnativeHook key press Exception: " + e.getMessage());
                    }
                }
            } catch (NoSuchFieldException | SecurityException e1) {
                AstericsErrorHandling.instance.getLogger().fine("JnativeHook Reflection Exception: " + e1.getMessage());
            }

            break;
        }
    }

    public void sendKeyRelease(int actcode) {

        switch (propInputMethod) {
        case 0:
            keyRelease(actcode);
            break;
        case 1:
            keyReleaseSi(actcode);
            break;
        case 2: // TDB

            int virtualKeycode = 0;
            Field f;
            try {

                String val = getVirtualKeycode(actcode);
                f = NativeKeyEvent.class.getField(val);
                Class<?> t = f.getType();
                if (t == int.class) {
                    try {
                        virtualKeycode = f.getInt(null);
                        // AstericsErrorHandling.instance.getLogger().fine("release
                        // virtual keycode="+virtualKeycode);

                        NativeKeyEvent keyEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED,
                                System.currentTimeMillis(), 0x00, // Modifiers
                                0x00, // Raw Code
                                virtualKeycode, // NativeKeyEvent.VC_UNDEFINED
                                NativeKeyEvent.CHAR_UNDEFINED, NativeKeyEvent.KEY_LOCATION_UNKNOWN);

                        GlobalScreen.postNativeEvent(keyEvent);

                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        AstericsErrorHandling.instance.getLogger()
                                .fine("JnativeHook key release Exception: " + e.getMessage());

                    }
                }
            } catch (NoSuchFieldException | SecurityException e1) {
                AstericsErrorHandling.instance.getLogger().fine("JnativeHook Reflection Exception: " + e1.getMessage());

            }

            break;
        }
    }

    /**
     * sends a keycode of given index (in the keycode vector) and mode
     */
    public int sendKeyCode(int index, int mode) {
        int i = 0;
        int actcode = -1;
        int modifier[] = new int[20];
        int mcount = 0;
        boolean waitForKey = true;

        /*
         * if (mode==MODE_HOLD) System.out.println("holding Key "); else if
         * (mode==MODE_PRESS) System.out.println("press Key "); else if
         * (mode==MODE_RELEASE) System.out.println("release Key ");
         */

        while ((index < keyCodeArray.size()) && (waitForKey)) // iterate until
                                                              // regular key
                                                              // (accumulate
                                                              // modifiers)
        {
            actcode = keyCodeArray.get(index);
            if ((mode == MODE_PRESS) || (mode == MODE_HOLD)) {
                switch (actcode) {
                case 1: // System.out.println("waiting: "+propWaitTime);
                    try {
                        Thread.sleep(propWaitTime);
                    } catch (Exception e) {
                    }
                    ;
                    break;
                case 11: // handle modifier keys !
                case 16:
                case 17:
                case 18:
                case 91:
                case 92:
                case 157:
                case 164:
                    if (mcount < 20) {
                        // System.out.println("modifier: "+actcode);
                        modifier[mcount++] = actcode; // add the modifiers
                    }
                    break;
                default:
                    waitForKey = false; // if a regular key appears !
                }
                if (actcode != 1) {
                    // System.out.println("press "+actcode);
                    sendKeyPress(actcode); // press modifiers or regular keys
                }
            }

            index++;
            i++;
        }

        if (mode != MODE_HOLD) // release immediately
        {
            if (actcode != -1) {
                // System.out.println("release "+actcode);
                sendKeyRelease(actcode);
            }

            while (mcount > 0) {
                // System.out.println("release modifier "+modifier[mcount-1]);
                sendKeyRelease(modifier[--mcount]);
            }
        }
        return (i);
    }

    /**
     * sends all keycodes of the keycode vector
     */
    public void sendAllCodes() {
        int i = 0;

        while (i < keyCodeArray.size()) // type (press and release) all keys
                                        // sequentially
        {
            // Logger.getAnonymousLogger().fine("sending keycode");
            i += sendKeyCode(i, MODE_PRESS);
        }
    }

    /**
     * Input Port for receiving keyCode strings.
     */
    private final IRuntimeInputPort ipKeyCodes = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            // Logger.getAnonymousLogger().info("received keycodes: " +
            // keycodes);
            propKeyCodeString = ConversionUtils.stringFromBytes(data);
            keyCodeTranslate();
            sendAllCodes();

        }

    };

    /**
     * Event Listener Port for sending all keycodes.
     */
    final IRuntimeEventListenerPort elpSendKeys = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {

            // Logger.getAnonymousLogger().info("received SendKeys event ");
            sendAllCodes();
        }
    };

    /**
     * Event Listener Port for pressing next key.
     */
    final IRuntimeEventListenerPort elpPressKey = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKeyCode(actSendPos, MODE_PRESS);
        }
    };

    /**
     * Event Listener Port for holding next key.
     */
    final IRuntimeEventListenerPort elpHoldKey = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.getLogger().fine(String.format("received
            // keyhold event, sending: %04x",keyCodeArray.get(actSendPos)));
            sendKeyCode(actSendPos, MODE_HOLD);
        }
    };

    /**
     * Event Listener Port for releasing current key.
     */
    final IRuntimeEventListenerPort elpReleaseKey = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // AstericsErrorHandling.instance.getLogger().fine("received
            // keyrelease event, sending.");
            sendKeyCode(actSendPos, MODE_RELEASE);
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        // NativeHookServices.init();
        AstericsErrorHandling.instance.reportInfo(this, "KeyboardInstance started");
        
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        sendKeyCode(actSendPos, MODE_RELEASE);
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
        sendKeyCode(actSendPos, MODE_RELEASE);
        super.stop();
        AstericsErrorHandling.instance.reportInfo(this, "Keyboard Instance stopped");
    }
}