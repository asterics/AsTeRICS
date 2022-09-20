
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

package eu.asterics.component.processor.speechprocessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IRemoteConnectionListener;
import eu.asterics.mw.services.RemoteConnectionManager;

/**
 * 
 * The SpeechProcessor plugin builds a brigde to the SpeechProcessor.exe program
 * (path can be given a s plugin property, default =
 * ./tools/SpeechProcessor.exe) which provides an interface to the Microsoft
 * Speech Platfrom (version 11) for speech recognition and generation.
 * 
 * The plugin builds a recognition grammar for up to n speech commands given as
 * properties and allows to trigger events upon recogintion. Additionally the
 * speech synthesizer can be used via an input port for strings.
 * 
 * A language (culture) can be selected also via a property, the corresponging
 * laguage packs for TTS and SR for 26 languages can be downloaded via the
 * following links:
 * 
 * http://www.microsoft.com/en-us/download/details.aspx?id=27225 and
 * http://www.microsoft.com/en-us/download/details.aspx?id=27224
 *
 * To work with the SDK, download:
 * http://www.microsoft.com/en-us/download/details.aspx?id=27226
 *
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: 2012-05-03
 */
public class SpeechProcessorInstance extends AbstractRuntimeComponentInstance {

    public final int NUMBER_OF_COMMANDS = 25;

    public final int MODE_ALWAYSACTIVE = 0;
    public final int MODE_VOICETRIGGERED = 1;
    public final int MODE_AUTO = 2;
    public final int MODE_TTSONLY = 3;

    public final int CONNECTION_TIMEOUT = 7000;
    public final String speechProcessorExePath = ".\\tools\\SpeechProcessor.exe";

    final IRuntimeOutputPort opCommand = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    public final IRuntimeEventTriggererPort etpActivated = new DefaultRuntimeEventTriggererPort();
    public final IRuntimeEventTriggererPort etpDeActivated = new DefaultRuntimeEventTriggererPort();
    public final IRuntimeEventTriggererPort[] etpCommandRecognized = new DefaultRuntimeEventTriggererPort[NUMBER_OF_COMMANDS];

    int propLanguage = 0;
    int propMode = 0;

    private double propRecognitionConfidence = 0.5;
    private String propActivationCommand = "";
    private String propDeActivationCommand = "";
    private String propHelpCommand = "";
    public String[] propCommand = new String[NUMBER_OF_COMMANDS];
    private int propSpeechLoopDelay = 1500;

    public Process p = null;
    // declare member variables here

    private boolean recoActive = false;

    private int tcpPort = 8221;
    private boolean connectionEstablished = false;
    private boolean bypassSpeechOutput = false;
    private boolean playback = false;

    private IRemoteConnectionListener connectionListener = null;

    private SpeechProcessorInstance instance = this;

    /**
     * The class constructor.
     */
    public SpeechProcessorInstance() {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            etpCommandRecognized[i] = new DefaultRuntimeEventTriggererPort();
            propCommand[i] = "";
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
        if ("speak".equalsIgnoreCase(portID)) {
            return ipSpeak;
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
        if ("command".equalsIgnoreCase(portID)) {
            return opCommand;
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
        if ("help".equalsIgnoreCase(eventPortID)) {
            return elpHelp;
        } else if ("activation".equalsIgnoreCase(eventPortID)) {
            return elpActivate;
        } else if ("deactivation".equalsIgnoreCase(eventPortID)) {
            return elpDeActivate;
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
    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("activated".equalsIgnoreCase(eventPortID)) {
            return etpActivated;
        } else if ("deActivated".equalsIgnoreCase(eventPortID)) {
            return etpDeActivated;
        } else {
            String s;
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                s = "recognizedCommand" + (i + 1);
                if (s.equalsIgnoreCase(eventPortID)) {
                    return etpCommandRecognized[i];
                }
            }
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
        if ("language".equalsIgnoreCase(propertyName)) {
            return propLanguage;
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            return propMode;
        } else if ("recognitionConfidence".equalsIgnoreCase(propertyName)) {
            return propRecognitionConfidence;
        } else if ("activationCommand".equalsIgnoreCase(propertyName)) {
            return propActivationCommand;
        } else if ("deActivationCommand".equalsIgnoreCase(propertyName)) {
            return propDeActivationCommand;
        } else if ("helpCommand".equalsIgnoreCase(propertyName)) {
            return propHelpCommand;
        } else if ("speechLoopDelay".equalsIgnoreCase(propertyName)) {
            return propSpeechLoopDelay;
        } else {
            String s;

            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                s = "command" + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    return propCommand[i];
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
        if ("language".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propLanguage;
            propLanguage = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("mode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMode;
            propMode = Integer.parseInt(newValue.toString());

            if (propMode == MODE_ALWAYSACTIVE) {
                recoActive = true;
            } else {
                recoActive = false;
            }

            return oldValue;
        } else if ("recognitionConfidence".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propRecognitionConfidence;
            propRecognitionConfidence = Double.parseDouble((String) newValue);
            return oldValue;
        } else if ("activationCommand".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propActivationCommand;
            propActivationCommand = newValue.toString();
            return oldValue;
        } else if ("deActivationCommand".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDeActivationCommand;
            propDeActivationCommand = newValue.toString();
            return oldValue;
        } else if ("helpCommand".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propHelpCommand;
            propHelpCommand = newValue.toString();
            return oldValue;
        } else if ("speechLoopDelay".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propSpeechLoopDelay;
            if (newValue != null) {
                propSpeechLoopDelay = Integer.parseInt((String) newValue);
            } else {
                propSpeechLoopDelay = 1500;
            }
            return oldValue;
        } else {
            String s;
            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                s = "command" + (i + 1);
                if (s.equalsIgnoreCase(propertyName)) {
                    final Object oldValue = propCommand[i];
                    propCommand[i] = newValue.toString();
                    return oldValue;
                }
            }
        }
        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipSpeak = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            String words = ConversionUtils.stringFromBytes(data);

            String message = "say:" + words;
            // System.out.println(message);

            RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());

        }
    };

    /**
     * Event Listerner Ports.
     */

    final IRuntimeEventListenerPort elpHelp = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if (playback == false) {
                tellWords();
            }
        }
    };

    final IRuntimeEventListenerPort elpActivate = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if (!recoActive) {
                recoActive = true;
                etpActivated.raiseEvent();
            }
        }
    };

    final IRuntimeEventListenerPort elpDeActivate = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(String data) {
            if (recoActive) {
                recoActive = false;
                etpDeActivated.raiseEvent();
            }
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        if (!openConnection(tcpPort)) {
            AstericsErrorHandling.instance.reportInfo(this, "Starting " + speechProcessorExePath);

            try {
                Thread.sleep(200);
                p = Runtime.getRuntime().exec(speechProcessorExePath);
                printConsoleOutput();

                AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            p.waitFor();
                            if (p.exitValue() > 0) {
                                AstericsErrorHandling.instance.reportError(instance,
                                        "SpeechProcessor could not be initialized ! Please verify that the Microsoft Speech Runtime 11 is installed correctly for your CPU  architecture.");

                            }
                        } catch (InterruptedException e) {
                            AstericsErrorHandling.instance.reportError(instance,
                                    "Exception in SpeechProcessor:" + e.getMessage());
                        }
                    }
                });

                /*
                 * AstericsThreadPool.instance.execute(new Runnable() { public
                 * void run() { try { Thread.sleep(CONNECTION_TIMEOUT); if
                 * (connectionEstablished==false) {
                 * AstericsErrorHandling.instance.reportError(instance,
                 * "Could not connect to SpeechRecognizer (" +
                 * speechProcessorExePath+")."); } if (speechProcessorOK==false)
                 * { AstericsErrorHandling.instance.reportError(instance,
                 * "Could not activate Speech Synthesizer and Recognizer."); } }
                 * catch (InterruptedException e) {}
                 * 
                 * } } );
                 */
            } catch (InterruptedException e) {
            } catch (IOException ioe) {
                AstericsErrorHandling.instance.reportError(this, "Could not start " + speechProcessorExePath);
                ioe.printStackTrace();
            }

        } else {
            AstericsErrorHandling.instance.reportInfo(instance,
                    String.format("ReUsing old Connection to SpeechProcessor"));

        }
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
        AstericsErrorHandling.instance.reportInfo(instance, "stop()");

        recoActive = false;
        bypassSpeechOutput = true;

        if (connectionEstablished == true) {
            String message = "@close@";
            System.out.println("Sending to TCP: " + message);

            RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());

        }
        closeConnection();
        super.stop();
    }

    /**
     * Opens the connection to the SpeechProcessor application
     * 
     * @param tcpPort
     *            the TCP port to connect to
     * @return true if there is already a connection available on this TCP port,
     *         false otherwise which will result in the connection being opened
     *         and handled through the connection listener
     */
    private boolean openConnection(int tcpPort) {
        connectionListener = new SpeechProcessorConnectionListener();
        if (RemoteConnectionManager.instance.requestConnection(Integer.toString(tcpPort), connectionListener)) {
            // connection already existed
            connectionEstablished = true;
            connectionListener.connectionEstablished();
            return true;
        } else {
            // no existing connection, user should wait for connection
            // established message from listener
            return false;
        }
    }

    /**
     * Closes the connection
     */
    void closeConnection() {
        AstericsErrorHandling.instance.reportInfo(instance, String.format("Closing Connection to SpeechProcessor"));

        RemoteConnectionManager.instance.closeConnection(Integer.toString(tcpPort));
        connectionEstablished = false;
    }

    public void printConsoleOutput() {

        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                String s;
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                try {
                    Thread.sleep(200);
                    while ((s = in.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (InterruptedException e) {
                } catch (IOException e) {
                }
            }
        });
    }

    public void tellWords() {
        playback = true;
        bypassSpeechOutput = false;
        AstericsThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    message = ("say:" + propActivationCommand);
                    if (bypassSpeechOutput == false) {
                        RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());
                        Thread.sleep(500 + propActivationCommand.length() * 90);
                    }

                    if (bypassSpeechOutput == false) {
                        message = ("say:" + propDeActivationCommand);
                        RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());
                        Thread.sleep(500 + propDeActivationCommand.length() * 90);
                    }

                    if (bypassSpeechOutput == false) {
                        message = ("say:" + propHelpCommand);
                        RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());
                        Thread.sleep(500 + propHelpCommand.length() * 90);
                    }

                    for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                        if ((propCommand[i] != "") && (bypassSpeechOutput == false)) {
                            message = ("say:" + (i + 1) + ":" + propCommand[i]);
                            RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());

                            Thread.sleep(1500 + propCommand[i].length() * 90);
                        }
                    }
                    playback = false;
                } catch (InterruptedException e) {
                }
            }
        });

    }

    /**
     * Implementation of the remote connection listener interface for the
     * communication with the PSeechProcessor. This class handles all incoming
     * packets as well as the set up and tear down of the connection.
     * 
     */
    class SpeechProcessorConnectionListener implements IRemoteConnectionListener {
        /**
         * Constructs the listener
         * 
         * @param owner
         *            the SpeechProcessor component instance owning the listener
         */
        public SpeechProcessorConnectionListener() {
        }

        /**
         * Called once a connection is set up on a TCP port. Initiates the
         * Recognizer Culture, Grammar and the speech commands.
         */
        @Override
        public void connectionEstablished() {
            AstericsErrorHandling.instance.reportInfo(instance, "Connection established");

            connectionEstablished = true;

            if (propActivationCommand.length() < 2) {
                propActivationCommand = "activate";
            }
            if (propDeActivationCommand.length() < 2) {
                propDeActivationCommand = "deactivate";
            }
            if (propHelpCommand.length() < 2) {
                propHelpCommand = "help";
            }
            if ((propRecognitionConfidence < 0.01) || (propRecognitionConfidence > 1)) {
                propRecognitionConfidence = 0.5;
            }

            String message = "";
            if (propMode < MODE_TTSONLY) {
                message += "culture:";
            } else {
                message += "ttsonly:";
            }

            switch (propLanguage) {
            case 0:
                message += "en-US";
                break;
            case 1:
                message += "de-DE";
                break;
            case 2:
                message += "es-ES";
                break;
            case 3:
                message += "pl-PL";
                break;
            default:
                message += "en-US";
                break;
            }
            message += "#confidence:" + propRecognitionConfidence;
            message += "#speechLoopDelay:" + propSpeechLoopDelay;
            message += "#grammar:" + propActivationCommand + ";" + propDeActivationCommand + ";" + propHelpCommand;

            for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                if (propCommand[i] != "") {
                    message += (";" + propCommand[i]);
                } else {
                    i = NUMBER_OF_COMMANDS;
                }
            }

            // System.out.println("Sending init to TCP: " + message);

            RemoteConnectionManager.instance.writeData(Integer.toString(tcpPort), message.getBytes());

        }

        /**
         * Called when the SpeechProcessor sends data. Handles all incoming
         * packets
         */
        @Override
        public void dataReceived(byte[] data) {

            String input = new String(data).trim();

            bypassSpeechOutput = true;

            AstericsErrorHandling.instance.reportInfo(instance, String.format("SpeechProcessor: data: %s", input));

            if (input.equals("@SpeechProcessor OK@")) {
            }

            if (recoActive == true) {

                if (compareCommand(propHelpCommand, input)) {
                    if (playback == false) {
                        tellWords();
                    }
                } else if ((propMode == MODE_VOICETRIGGERED) && (compareCommand(propDeActivationCommand, input))) {
                    recoActive = false;
                    etpDeActivated.raiseEvent();
                } else {
                    for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
                        if (compareCommand(propCommand[i], input)) {
                            opCommand.sendData(ConversionUtils.stringToBytes(propCommand[i]));
                            etpCommandRecognized[i].raiseEvent();
                            if (propMode == MODE_AUTO) {
                                recoActive = false;
                                etpDeActivated.raiseEvent();
                            }
                        }
                    }
                }
            } else {
                if (compareCommand(propActivationCommand, input)) {
                    etpActivated.raiseEvent();
                    recoActive = true;
                }

            }
        }

        /**
         * Called when connection is lost
         */
        @Override
        public void connectionLost() {
            AstericsErrorHandling.instance.reportInfo(instance, "Connection lost");
            connectionEstablished = false;
        }

        /**
         * Called after connection has been closed
         */
        @Override
        public void connectionClosed() {
            AstericsErrorHandling.instance.reportInfo(instance, "Connection closed");

            connectionEstablished = false;
        }

        /**
         * Compares recognized command sent by the SpeechProcessor with the
         * property command.
         */

        private boolean compareCommand(String propertyCommand, String serverCommand) {
            if (propertyCommand.length() == serverCommand.length()) {
                char[] propertyCommandChars = new char[propertyCommand.length()];
                char[] serverCommandChars = new char[serverCommand.length()];

                boolean equal = true;

                propertyCommand.toUpperCase().getChars(0, propertyCommand.length(), propertyCommandChars, 0);
                serverCommand.toUpperCase().getChars(0, serverCommand.length(), serverCommandChars, 0);
                ;
                for (int i = 0; i < propertyCommand.length(); i++) {
                    if ((serverCommandChars[i] != propertyCommandChars[i]) && (serverCommandChars[i] != '?')) {
                        equal = false;
                        break;
                    }
                }

                return equal;
            }
            return false;
        }

    }

}