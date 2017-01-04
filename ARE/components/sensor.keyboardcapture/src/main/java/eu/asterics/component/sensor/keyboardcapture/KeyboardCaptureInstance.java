
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

package eu.asterics.component.sensor.keyboardcapture;

import java.util.StringTokenizer;
import java.util.Vector;

import eu.asterics.component.sensor.keyboardcapture.jni.Bridge;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * KeyboardCaptureInstance intercepts local keyboard input and detects up to
 * seven command strings which were typed
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 1, 2011 Time: 3:35:00
 *         PM
 */
public class KeyboardCaptureInstance extends AbstractRuntimeComponentInstance {
    private final int NUMBER_OF_COMMANDS = 10;
    private final int MODE_WORDS = 0;
    private final int MODE_KEYS = 1;

    private final String KEY_PROPERTY_COMMAND = "command";
    private final String KEY_PROPERTY_EVENT = "recognizedCommand";

    private final OutportKeys opKeyCode = new OutportKeys();
    private final OutportWords opWords = new OutportWords();
    final IRuntimeEventTriggererPort[] etpCmdRecognized = new DefaultRuntimeEventTriggererPort[NUMBER_OF_COMMANDS];

    private int propMode = 0;

    private final Bridge bridge = new Bridge(this);
    private String[] commands = { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten" };
    private int wordCountOld = 0;
    private String actString = "";

    private Vector<Integer> keyCodeArray;

    /**
     * The class constructor, creates the event ports
     */
    public KeyboardCaptureInstance() {
        keyCodeArray = new Vector<Integer>();

        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            etpCmdRecognized[i] = new DefaultRuntimeEventTriggererPort();
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
        if ("keyCode".equalsIgnoreCase(portID)) {
            return opKeyCode;
        } else if ("words".equalsIgnoreCase(portID)) {
            return opWords;
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
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            return bridge.getProperty(propertyName);
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            return (propMode);
        } else {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                String s = KEY_PROPERTY_COMMAND + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    return (commands[i]);
                }
            }
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
        if ("blockEvents".equalsIgnoreCase(propertyName)) {
            final String oldValue = bridge.getProperty(propertyName);
            bridge.setProperty(propertyName, newValue.toString());

            return oldValue;
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = propMode;
            propMode = Integer.parseInt((String) newValue);
            return oldValue;
        } else {
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                String s = KEY_PROPERTY_COMMAND + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    commands[i] = (String) newValue;
                    // Logger.getAnonymousLogger().info(String.format("Setting
                    // command %d to %s", i+1, newValue));
                }
            }
        }
        return null;
    }

    /**
     * returns an Event Triggerer Port
     * 
     * @param enventPortID
     *            the name of the event trigger port
     * @return the event trigger port or null if not found
     */
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            String s = KEY_PROPERTY_EVENT + (i + 1);
            if (s.equalsIgnoreCase(eventPortID)) {
                return etpCmdRecognized[i];
            }
        }
        return null;
    }

    /**
     * performs comparison of types string with command strings and raises event
     * on positive match.
     */
    public void processKeyboardInput(boolean press, int keycode) {
        String lastWord = "";

        if (press == false) {
            // System.out.println("release ! keycode array
            // size="+keyCodeArray.size());

            if (keyCodeArray.contains(keycode)) {
                keyCodeArray.remove(keyCodeArray.indexOf(keycode));
            }

            if (keyCodeArray.size() == 0) {
                // System.out.println("send 0 !");
                opKeyCode.sendData(0);
            } else {
                // System.out.println("send last key");
                opKeyCode.sendData(keyCodeArray.lastElement());
            }
        }
        // System.out.println("released "+keycode);

        if (press == true) {
            if (keyCodeArray.size() == 0) {
                keyCodeArray.add(keycode);
                opKeyCode.sendData(keycode);
                // System.out.println("first key");
            } else {

                // System.out.println("send, keycode array
                // size="+keyCodeArray.size()+" actkey="+keycode+"
                // lastkey="+keyCodeArray.get(keyCodeArray.size()-1));
                opKeyCode.sendData(keycode);
                if (keycode != keyCodeArray.get(keyCodeArray.size() - 1)) {
                    keyCodeArray.add(keycode);
                    // System.out.println("new key, keycode array
                    // size="+keyCodeArray.size());
                }
            }

            /* convert UTF-16 special chars if necessary ... */

            actString += (char) keycode;
            // Logger.getAnonymousLogger().info(String.format("keybuffer: %s",
            // act_string));

            if (propMode == MODE_WORDS) {
                if ((char) keycode == ' ') {
                    int wordCount = 0;
                    StringTokenizer st = new StringTokenizer(actString);
                    while (st.hasMoreTokens()) {
                        wordCount++;
                        lastWord = st.nextToken();
                    }

                    if (wordCount != wordCountOld) {
                        opWords.sendData(lastWord);
                        wordCountOld = wordCount;
                        AstericsErrorHandling.instance.reportInfo(this, String.format("word sent: %s", lastWord));
                    }
                }
                for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                    if (commands[i].equalsIgnoreCase(actString.substring(actString.length() - commands[i].length()))) {
                        etpCmdRecognized[i].raiseEvent();
                        AstericsErrorHandling.instance.reportInfo(this,
                                String.format("command: %d found, event raised!", i + 1));
                    }
                }

                if (actString.length() > 80) {
                    actString = actString.substring(40, actString.length());
                    // wordcnt_old=0;
                }
            }
            if (propMode == MODE_KEYS) {
                for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                    if (commands[i].equalsIgnoreCase(actString)) {
                        etpCmdRecognized[i].raiseEvent();
                        AstericsErrorHandling.instance.reportInfo(this,
                                String.format("command: %d found, event raised!", i + 1));
                    }
                }
                actString = "";
            }

        }
    }

    /**
     * Output Port for current key.
     */
    public class OutportKeys extends DefaultRuntimeOutputPort {
        public void sendData(int data) {
            super.sendData(ConversionUtils.intToByteArray(data));
        }
    }

    /**
     * Output Port for last typed word.
     */
    public class OutportWords extends DefaultRuntimeOutputPort {
        public void sendData(String data) {
            super.sendData(ConversionUtils.stringToBytes(data));
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        bridge.activate();
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        bridge.deactivate();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        bridge.activate();
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