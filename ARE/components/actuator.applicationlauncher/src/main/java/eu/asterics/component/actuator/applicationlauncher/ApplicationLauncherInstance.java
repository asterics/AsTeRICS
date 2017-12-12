
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

package eu.asterics.component.actuator.applicationlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.sun.org.apache.bcel.internal.generic.IRETURN;

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
import eu.asterics.mw.utils.OSUtils;
import eu.asterics.mw.utils.OSUtils.OS_NAMES;

/**
 * 
 * ApplicationLauncherInstance can external software applications via full path and filename. A default application is given as property, which can be replace
 * by an incoming application name at the input port. The Launch can be performed automatically at startup, at incoming filename or only via incoming event
 * trigger.
 * 
 * @author Chris Veigl [veigl@technikum-wien.at]
 * 
 */
public class ApplicationLauncherInstance extends AbstractRuntimeComponentInstance {
    Logger logger = AstericsErrorHandling.instance.getLogger();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();
    enum EXECUTION_MODES {
        START_APPLICATION, OPEN_URL;
    }

    String propExecuteOnPlatform = OSUtils.OS_NAMES.ALL.toString();
    String propExecutionMode = EXECUTION_MODES.START_APPLICATION.toString();
    String propDefaultApplication = "c:\\windows\\notepad.exe";
    String propArguments = "";
    String propWorkingDirectory = ".";

    String propCloseCmd = "";
    boolean propAutoLaunch = false;
    boolean propAutoClose = true;
    boolean propOnlyByEvent = false;

    // declare member variables here
    Process process = null;
    boolean processStarted = false;
    Future<?> stdOutFuture;
    Future<?> stdErrFuture;

    String stdInString = "";

    IRuntimeOutputPort opStdOut = new DefaultRuntimeOutputPort();
    IRuntimeOutputPort opStdErr = new DefaultRuntimeOutputPort();
    IRuntimeOutputPort opExitValue = new DefaultRuntimeOutputPort();

    IRuntimeEventTriggererPort etpStartedSuccessfully = new DefaultRuntimeEventTriggererPort();
    IRuntimeEventTriggererPort etpStartedWithError = new DefaultRuntimeEventTriggererPort();

    /**
     * The class constructor.
     */
    public ApplicationLauncherInstance() {
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
        if ("filename".equalsIgnoreCase(portID)) {
            return ipFilename;
        }
        if ("arguments".equalsIgnoreCase(portID)) {
            return ipArguments;
        }        
        if ("stdIn".equalsIgnoreCase(portID)) {
            return ipStdIn;
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
        if ("stdErr".equalsIgnoreCase(portID)) {
            return opStdErr;
        }
        if ("stdOut".equalsIgnoreCase(portID)) {
            return opStdOut;
        }
        if ("exitValue".equalsIgnoreCase(portID)) {
            return opExitValue;
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
        if ("launchNow".equalsIgnoreCase(eventPortID)) {
            return elpLaunchNow;
        }
        if ("closeNow".equalsIgnoreCase(eventPortID)) {
            return elpCloseNow;
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
        if ("startedSuccessfully".equalsIgnoreCase(eventPortID)) {
            return etpStartedSuccessfully;
        }
        if ("startedWithError".equalsIgnoreCase(eventPortID)) {
            return etpStartedWithError;
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
        if ("executeOnPlatform".equalsIgnoreCase(propertyName)) {
            return propExecuteOnPlatform;
        }
        if ("executionMode".equalsIgnoreCase(propertyName)) {
            return propExecutionMode;
        }
        if ("defaultApplication".equalsIgnoreCase(propertyName)) {
            return propDefaultApplication;
        }
        if ("arguments".equalsIgnoreCase(propertyName)) {
            return propArguments;
        }
        if ("workingDirectory".equalsIgnoreCase(propertyName)) {
            return propWorkingDirectory;
        }
        if ("closeCmd".equalsIgnoreCase(propertyName)) {
            return propCloseCmd;
        }

        if ("autoLaunch".equalsIgnoreCase(propertyName)) {
            return propAutoLaunch;
        }
        if ("autoClose".equalsIgnoreCase(propertyName)) {
            return propAutoClose;
        }
        if ("onlyByEvent".equalsIgnoreCase(propertyName)) {
            return propOnlyByEvent;
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
        if ("executeOnPlatform".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propExecuteOnPlatform;
            propExecuteOnPlatform = (String) newValue;
            return oldValue;
        }
        if ("executionMode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propExecutionMode;
            propExecutionMode = (String) newValue;
            return oldValue;
        }
        if ("defaultApplication".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propDefaultApplication;
            propDefaultApplication = (String) newValue;
            return oldValue;
        }
        if ("arguments".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propArguments;
            propArguments = (String) newValue;
            return oldValue;
        }
        if ("workingDirectory".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWorkingDirectory;
            propWorkingDirectory = (String) newValue;
            if (propWorkingDirectory == "") {
                propWorkingDirectory = ".";
            }
            return oldValue;
        }
        if ("closeCmd".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCloseCmd;
            propCloseCmd = (String) newValue;
            return oldValue;
        }

        if ("autoLaunch".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoLaunch;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoLaunch = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoLaunch = false;
            }
            return oldValue;
        }
        if ("autoClose".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoClose;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoClose = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoClose = false;
            }
            return oldValue;
        }
        if ("onlyByEvent".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOnlyByEvent;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propOnlyByEvent = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propOnlyByEvent = false;
            }
            return oldValue;
        }

        return null;
    }

    private final void launchNow(boolean throwException) {
        if (process != null || processStarted == true) {
            closeNow();
        }
        try {
            if (EXECUTION_MODES.valueOf(propExecutionMode).equals(EXECUTION_MODES.START_APPLICATION)) {
                logger.fine("Starting application: " + propDefaultApplication);
                process = OSUtils.startApplication(propDefaultApplication, propArguments, propWorkingDirectory, OS_NAMES.valueOf(propExecuteOnPlatform));
            } else if (EXECUTION_MODES.valueOf(propExecutionMode).equals(EXECUTION_MODES.OPEN_URL)) {
                logger.fine("Opening URL: " + propArguments);
                process = OSUtils.openURL(propArguments, OS_NAMES.valueOf(propExecuteOnPlatform));
            } else {
                // execution mode not supported
                logger.warning("Execution mode not supported: " + propExecutionMode);
                return;
            }

            // Attach standard out and standard error streams and send data to output ports.
            if (process != null) {
                stdOutFuture = AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
                            String s;
                            while ((s = in.readLine()) != null) {
                                opStdOut.sendData(ConversionUtils.stringToBytes(s));
                                Thread.sleep(5);
                            }
                        } catch (Exception e) {
                            // can be ignored, means either interrupted or the process was killed.
                        }
                    }
                });
                stdErrFuture = AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));) {
                            String s;
                            while ((s = in.readLine()) != null) {
                                opStdErr.sendData(ConversionUtils.stringToBytes(s));
                                Thread.sleep(5);
                            }
                        } catch (Exception e) {
                            // can be ignored, means either interrupted or the process was killed.
                        }
                    }
                });

                // Send string received through ipStdIn to the input of the process.
                if (stdInString != null && !"".equals(stdInString)) {
                    logger.fine("Sending input string: " + stdInString);
                    new PrintWriter(new OutputStreamWriter(process.getOutputStream())).println(stdInString);
                    // mark string as being sent.
                    stdInString = null;
                }
                // Check for exit value, in case of error the process should already have terminated.
                try {
                    process.waitFor(1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // can be ignored
                }

                if (!process.isAlive()) {
                    // Process has already terminated, check exit value to determine success
                    int exitValue = process.exitValue();
                    logger.fine("Process already terminated with exit value: " + exitValue);
                    opExitValue.sendData(ConversionUtils.intToBytes(exitValue));

                    // cleanup
                    closeProcessAndCleanup();

                    if (exitValue == 0) {
                        etpStartedSuccessfully.raiseEvent();
                        processStarted = true;
                    } else {
                        etpStartedWithError.raiseEvent();
                        processStarted = false;
                        return;
                    }
                } else {
                    // Process is still running --> started successfully
                    etpStartedSuccessfully.raiseEvent();
                    processStarted = true;
                }
            }
        } catch (Exception e) {
            logger.warning("Could not start: " + propDefaultApplication + " " + propArguments);
            processStarted = false;
            opExitValue.sendData(ConversionUtils.intToBytes(1));
            opStdErr.sendData(ConversionUtils.stringToBytes(e.getMessage()));
            etpStartedWithError.raiseEvent();
            closeProcessAndCleanup();
            if (throwException) {
                throw new RuntimeException(e);
            }
        }
    }

    private final void closeNow() {
        closeProcessAndCleanup();

        if (propCloseCmd != null && !"".equals(propCloseCmd)) {
            logger.fine("Executing close cmd: " + propCloseCmd);
            try {
                Process destroyProcess = OSUtils.startApplication(propCloseCmd, null, OS_NAMES.valueOf(propExecuteOnPlatform));
                try {
                    destroyProcess.waitFor(3000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // can be safely ignored
                }
                if (destroyProcess != null) {
                    destroyProcess.destroy();
                }
            } catch (Exception e1) {
                logger.warning("Error executing close cmd: " + propCloseCmd + ", reason: " + e1.getMessage());
            }

        }

        processStarted = false;
    }

    private void closeProcessAndCleanup() {
        if (process != null) {
            logger.fine("Closing process for: " + propDefaultApplication + " " + propArguments);
            if (stdOutFuture != null) {
                stdOutFuture.cancel(true);
            }
            if (stdErrFuture != null) {
                stdErrFuture.cancel(true);
            }
            process.destroy();
            try {
                process.waitFor(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e1) {
                // can be safely ignored.
            }
            process = null;
        }
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipFilename = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propDefaultApplication = ConversionUtils.stringFromBytes(data);
            logger.fine("Setting new command: " + propDefaultApplication);
            if (propOnlyByEvent == false) {
                launchNow(false);
            }
        }

    };

    private final IRuntimeInputPort ipArguments = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propArguments = ConversionUtils.stringFromBytes(data);
            logger.fine("Setting new arguments: " + propArguments);
            if (propOnlyByEvent == false) {
                launchNow(false);
            }
        }

    };
    
    private final IRuntimeInputPort ipStdIn = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            stdInString = ConversionUtils.stringFromBytes(data);
            if (processStarted && process != null) {
                logger.fine("Sending input string to process: " + stdInString);
                new PrintWriter(new OutputStreamWriter(process.getOutputStream())).println(stdInString);
                // mark string as being sent.
                stdInString = null;
            }
        }

    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpLaunchNow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            launchNow(false);
        }
    };
    final IRuntimeEventListenerPort elpCloseNow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            closeNow();
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        if (propAutoLaunch == true) {
            launchNow(true);
        }
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
        if (propAutoClose == true) {
            closeNow();
        }

        super.stop();
    }
}