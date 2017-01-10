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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.asapi.StatusObject;
import eu.asterics.mw.gui.ErrorLogPane;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;

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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * This class provides Error Handling functionality the ARE and the components
 * 
 * Date: Aug 25, 2010 Time: 11:35:35 AM
 */

public class AstericsErrorHandling implements IAstericsErrorHandling {
    public static final String LOG_PATH_PROPERTY = "eu.asterics.mw.services.AstericsErrorHandling.logPath";
    public static final String LOG_CONSOLE_LOGLEVEL_PROPERTY = "eu.asterics.mw.services.AstericsErrorHandling.consoleLogLevel";
    public static final AstericsErrorHandling instance = new AstericsErrorHandling();
    private ArrayList<StatusObject> statusObjects = null;
    static String LOGGER_OPTIONS = ".logger";

    private static int statusObjectIndex = 0;
    private static Logger logger = null;

    private AstericsErrorHandling() {
        super();
        this.statusObjects = new ArrayList<StatusObject>();
    }

    /**
     * This method is used by the components to report an error. It logs the
     * error in "warning" logger and sets the status of the ARE to "ERROR" to
     * denote that an error has occurred
     * 
     * @param component
     *            the component instance that reports the error
     * @param errorMsg
     *            the error message
     */
    @Override
    public void reportError(IRuntimeComponentInstance component, final String errorMsg) {
        if (component != null) {
            String componentID = DeploymentManager.instance
                    .getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component);
            if (componentID != null) {
                // System.out.println("componentID: "+componentID);
                logger.warning(componentID + ": " + errorMsg);
                setStatusObject(AREStatus.ERROR.toString(), componentID, errorMsg);
            }
        }

        AREProperties props = AREProperties.instance;
        if (props.checkProperty("showErrorDialogs", "1")) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ErrorLogPane.appendLog(errorMsg);
                    DeploymentManager.instance.setStatus(AREStatus.ERROR);

                    AstericsErrorHandling.this.notifyAREEventListeners("onAreError", errorMsg);

                    JOptionPane op = new JOptionPane(errorMsg, JOptionPane.WARNING_MESSAGE);

                    JDialog dialog = op.createDialog("AsTeRICS Runtime Environment:");
                    dialog.setAlwaysOnTop(true);
                    dialog.setModal(false);
                    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    dialog.setVisible(true);
                }
            });
        }
    }

    /**
     * This method is used by the components to report informative messages. It
     * logs the message in "info" logger.
     * 
     * @param component
     *            the component instance that reports the informative message
     * @param info
     *            the informative message
     */
    @Override
    public void reportInfo(IRuntimeComponentInstance component, String info) {
        String componentID = DeploymentManager.instance
                .getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component);
        logger.fine(componentID + ": " + info);

    }

    /**
     * This method is used by the components to report.. Ok
     */
    @Override
    public void reportOk(IRuntimeComponentInstance component, String info) {
        String componentID = DeploymentManager.instance
                .getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component);
        logger.fine(componentID + ": " + info);
        setStatusObject(AREStatus.OK.toString(), componentID, info);

    }

    /**
     * This method is used by the components to report debugging messages. It
     * logs the message in "fine" logger.
     * 
     * @param component
     *            the component instance that reports the debugging message
     * @param info
     *            the debugging message
     */
    @Override
    public void reportDebugInfo(IRuntimeComponentInstance component, String info) {
        String componentID = DeploymentManager.instance
                .getIRuntimeComponentInstanceIDFromIRuntimeComponentInstance(component);
        logger.fine(componentID + ": " + info);

    }

    /**
     * This method is used to set a new Status Object. The object is set by the
     * component specified by componentID with the error message specified by
     * errorMsg and with the ARE status as specified by status. If a component
     * is not specified, the Status Object is set for the ARE.
     * 
     * @param componentID
     *            the ID of the component that sets the Status Object or null if
     *            the Status Object is set by the ARE
     * @param errorMsg
     *            the error message in the new Status Object
     * @param status
     *            the new status of the ARE
     */
    public void setStatusObject(String status, String componentID, String errorMsg) {
        StatusObject statusObject = new StatusObject(status, componentID, errorMsg);
        statusObjects.add(statusObject);
    }

    /**
     * This method returns an array of all Status Objects if fullList is true or
     * an array of all Status Objects created after the last time this method
     * has been called if otherwise.
     * 
     * @param fullList
     *            the boolean flag. If true an array of all Status Objects will
     *            be returned. If false an array of all Status Objects created
     *            after the last time this method has been called will be
     *            returned
     */
    public StatusObject[] getStatusObjects(boolean fullList) {

        StatusObject[] allObjects = statusObjects.toArray(new StatusObject[statusObjects.size()]);
        StatusObject[] res = null;
        int i = 0;

        if (!fullList) {
            res = new StatusObject[statusObjects.size() - statusObjectIndex];
            i = statusObjectIndex;
            statusObjectIndex = statusObjects.size();
        } else {
            res = new StatusObject[statusObjects.size()];
            i = 0;
            statusObjectIndex = statusObjects.size();
        }

        for (int j = 0; i < statusObjects.size(); i++, j++) {
            res[j] = allObjects[i];
        }

        return res;
    }

    /**
     * This method creates the logger. Actually there are 4 types of loggers:
     * "severe": logs fatal errors i.e. errors that prevent the ARE from
     * functioning "warning": logs errors other than fatal e.g. component errors
     * "info": logs informative messages "fine": logs debugging messages
     * 
     * Each logger by default also logs all messages with severity level higher
     * than its own. E.g. the warning logger logs warning and severe messages,
     * the info logger logs info, warning and severe messages etc. The same
     * applies to the consoleHandler, i.e. by setting its level to info, the
     * console will also print severe and warning messages along with info
     * messages
     */
    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("AstericsLogger");

            FileHandler warningFileHandler, severeFileHandler, infoFileHandler, fineFileHandler;
            ConsoleHandler consoleHandler;
            try {
                // cleanup before starting:
                logger.setUseParentHandlers(false);

                // remove and handlers that will be replaced
                Handler[] handlers = logger.getHandlers();
                for (Handler handler : handlers) {
                    if (handler.getClass() == ConsoleHandler.class) {
                        logger.removeHandler(handler);
                    }
                }

                File logFolder = new File(System.getProperty(LOG_PATH_PROPERTY, ResourceRegistry.TMP_FOLDER));
                if (!logFolder.exists()) {
                    FileUtils.forceMkdir(logFolder);
                }
                // Create handlers
                severeFileHandler = new FileHandler(new File(logFolder, "asterics_logger_severe.log").getPath(), true);
                warningFileHandler = new FileHandler(new File(logFolder, "asterics_logger_warning.log").getPath(),
                        true);
                infoFileHandler = new FileHandler(new File(logFolder, "asterics_logger.log").getPath(), true);
                fineFileHandler = new FileHandler(new File(logFolder, "asterics_logger_fine.log").getPath(), true);
                consoleHandler = new ConsoleHandler();

                // Set report level of handlers
                severeFileHandler.setLevel(Level.SEVERE);
                warningFileHandler.setLevel(Level.WARNING);
                infoFileHandler.setLevel(Level.INFO);
                fineFileHandler.setLevel(Level.FINE);

                // The consoleHandler prints log messaged to the console. Its
                // severety level can be adjusted accordingly.
                String level = getLoggerLevel();
                switch (level) {
                case "INFO":
                    consoleHandler.setLevel(Level.INFO);
                    break;
                case "WARNING":
                    consoleHandler.setLevel(Level.WARNING);
                    break;
                case "FINE":
                    consoleHandler.setLevel(Level.FINE);
                    break;
                case "SEVERE":
                    consoleHandler.setLevel(Level.SEVERE);
                    break;

                default:
                    consoleHandler.setLevel(Level.INFO);
                    break;
                }

                // Add handlers to the logger
                logger.addHandler(warningFileHandler);
                logger.addHandler(severeFileHandler);
                logger.addHandler(infoFileHandler);
                logger.addHandler(fineFileHandler);
                logger.addHandler(consoleHandler);

                // Create formatters for the handlers (optional)
                severeFileHandler.setFormatter(new SimpleFormatter());
                warningFileHandler.setFormatter(new SimpleFormatter());
                infoFileHandler.setFormatter(new SimpleFormatter());
                fineFileHandler.setFormatter(new SimpleFormatter());
                consoleHandler.setFormatter(new SimpleFormatter());

                logger.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);

            } catch (SecurityException e) {
                System.out.println(
                        AstericsErrorHandling.class.getName() + ": Error creating AstericsLogger: " + e.getMessage());
            } catch (IOException e) {
                // logger.warning(this.getClass().getName()+
                // ": Error creating AstericsLogger: "+e.getMessage());
                System.out.println(
                        AstericsErrorHandling.class.getName() + ": Error creating AstericsLogger: " + e.getMessage());
            }
        }

        return logger;
    }

    private String getLoggerLevel() {
        String lineInput;
        StringTokenizer tkz;
        String consoleLogLevel = "";
        try (BufferedReader in = new BufferedReader(new FileReader(LOGGER_OPTIONS));) {

            while ((lineInput = in.readLine()) != null) {
                tkz = new StringTokenizer(lineInput, ":");
                if (tkz.nextToken().compareToIgnoreCase("error_level") == 0) {
                    consoleLogLevel = tkz.nextToken();
                    break;
                }
            }
            in.close();
        } catch (IOException ioe) {
        }
        consoleLogLevel = System.getProperty(LOG_CONSOLE_LOGLEVEL_PROPERTY, consoleLogLevel);
        return consoleLogLevel;
    }

    private void notifyAREEventListeners(String methodName, String msg) {
        ArrayList<IAREEventListener> listeners = AREServices.instance.getAREEventListners();

        if (methodName.equals("onAreError")) {
            for (IAREEventListener listener : listeners) {
                listener.onAreError(msg);
            }
        }

    }

}
