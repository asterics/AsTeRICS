package eu.asterics.mw.are;

import java.awt.EventQueue;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.asterics.mw.are.UDP.UDPThread;
import eu.asterics.mw.are.asapi.Activator;
import eu.asterics.mw.gui.AstericsGUI;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.utils.OSUtils;

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
 * Starting point for ARE middleware
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 23, 2010 Time: 11:36:14 AM
 */
public class Main implements BundleActivator {
    public static final String ASAPI_ENABLE_ARE_AUTODETECTION_PROPKEY = "ASAPI.enableAREAutoDetection";
    public static final String ASAPI_ENABLE_ACS_PORT_CONNECTION = "ASAPI.enableACSPortConnection";

    private static Logger logger = null;

    private BundleManager bundleManager = null;

    private AstericsGUI astericsGUI = null;
    private JFrame astericsFrame;

    private static BundleContext areContext;

    public static BundleContext getAREContext() {
        return areContext;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        logger = AstericsErrorHandling.instance.getLogger();

        // set default uncaught exception handler to get logged messages in case of exception.
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.log(Level.SEVERE, "in Thread <" + t.getName() + ">: " + e.getMessage(), e);
            }
        });

        // Set default values for ASAPI interface which is used for ACS to ARE connection
        AREProperties.instance.setDefaultPropertyValue(ASAPI_ENABLE_ACS_PORT_CONNECTION, "1",
                "Enables/Disables ASAPI port registration for ACS. 1=ACS may connect to ARE through the port specified with the key '"
                        + Activator.ASAPI_ACS_PORT_NUMBER_PROPKEY + "'. 0=ACS may not connect to the ARE.");
        AREProperties.instance.setDefaultPropertyValue(ASAPI_ENABLE_ARE_AUTODETECTION_PROPKEY, "1",
                "Enables/Disables ARE autodetection by the ACS. 1=Autodetection enabled, 0=Autodetection disabled");
        AREProperties.instance.setDefaultPropertyValue(Activator.ASAPI_ACS_PORT_NUMBER_PROPKEY, String.valueOf(Activator.ASAPI_ACS_PORT_NUMBER_DEFAULT),
                "Sets the ASAPI port number which is used by the ACS to connect to the ARE.");

        // Check if not 32bit
        String bits = System.getProperty("sun.arch.data.model");
        if (OSUtils.isWindows() && bits.compareTo("64") == 0) {
            String message = bits
                    + "bit Java Runtime detected! Many plugins of the ARE need a 32bit Java Runtime.\nJava Download: http://www.java.com/de/download/manual.jsp";
            logger.warning(message);
        }
        logger.info("JVM " + bits + " bit detected");
        final String startModel = context.getProperty("eu.asterics.ARE.startModel");
        logger.info("Property eu.asterics.ARE.startModel: " + startModel);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    astericsGUI = new AstericsGUI(context);

                    astericsFrame = astericsGUI.getFrame();

                    DeploymentManager.instance.setGui(astericsGUI);

                    DeploymentManager.instance.setStatus(AREStatus.UNKNOWN);
                    AstericsErrorHandling.instance.setStatusObject(AREStatus.UNKNOWN.toString(), "", "");
                    areContext = context;

                    bundleManager = new BundleManager(context);
                    context.addBundleListener(bundleManager);
                    context.addFrameworkListener(bundleManager);
                    bundleManager.start();

                    DeploymentManager.instance.setBundleManager(bundleManager);

                    DeploymentManager.instance.start(context);

                    // Create thread pools and eventually store back
                    // properties
                    AstericsThreadPool.getInstance();
                    AstericsModelExecutionThreadPool.getInstance();

                    DeploymentManager.instance.setStatus(AREStatus.OK);
                    AstericsErrorHandling.instance.setStatusObject(AREStatus.OK.toString(), "", "");

                    if (AREProperties.instance.checkProperty(ASAPI_ENABLE_ACS_PORT_CONNECTION, "1")) {
                        logger.info("ASAPI: Enabling ACS port connection");
                        Thread asapiServerThread = new Thread(new Activator());
                        asapiServerThread.start();
                    } else {
                        logger.info("ASAPI: ACS port connection disabled");
                    }

                    if (AREProperties.instance.checkProperty(ASAPI_ENABLE_ARE_AUTODETECTION_PROPKEY, "1")) {
                        logger.info("ASAPI: Enabling ARE auto detection");
                        Thread udpThread = new Thread(new UDPThread());
                        udpThread.start();
                    } else {
                        logger.info("ASAPI: ARE auto detection disabled");
                    }

                    // This is very ugly, we have to store back properties, if
                    // any of the other initialization code
                    // sets a property key. This is to generate a default
                    // areProperties file if it does not exist.
                    // @TODO: Collect all property keys and move them to
                    // AREProperties class and provide mechanism to generate
                    // default
                    // file including comments
                    AREProperties.instance.storeProperties();

                    // Finally autostart model, everything else should now be initialized correctly, if not the code should have thrown an exception already
                    AsapiSupport as = new AsapiSupport();
                    // System.out.println("*** starting model !");
                    as.autostart(startModel);
                } catch (Throwable e) {
                    // In case of a startup error show the ARE gui panel, so
                    // that the user is able to select a model manually.
                    if (astericsFrame != null && astericsGUI != null) {
                        astericsGUI.unsetSystemTray();
                    }

                    String reason = e.getMessage() != null ? ":\n\n" + e.getMessage() : "";
                    String message = "The AsTeRICS Runtime Environment started with errors" + reason;
                    logger.log(Level.SEVERE, message, e);
                    startupMessage(message, JOptionPane.ERROR_MESSAGE, false);
                }
            }
        });
    }

    /**
     * Show non-modal info/warning/error message not disable-able by areProperties.
     * 
     * @param message
     * @param messageType
     */
    private void startupMessage(String message, int messageType, boolean exit) {
        JOptionPane op = new JOptionPane(message, messageType);

        // Show error dialog, but not modal to not risk a dead lock because of
        // other modal error dialogs of components.
        JDialog dialog = op.createDialog("ARE message");
        dialog.setAlwaysOnTop(true);
        // if exit==true make dialog modal
        dialog.setModal(exit);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        if (exit) {
            System.exit(1);
        }
    }

    /**
     * This method stops the ARE.
     * 
     * @param context
     *            the BundleContext
     * @throws Exception
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        logger.fine(this.getClass().getName() + ".stop: " + "removing bundle listener \n");

        context.removeBundleListener(bundleManager);

        bundleManager.stop();

        logger.fine(this.getClass().getName() + ".stop: " + "destroying the bundle manager \n");

        bundleManager = null;

        logger.fine(this.getClass().getName() + ".stop: " + "stopping deployment manager \n");

        DeploymentManager.instance.stop();

        logger.fine(this.getClass().getName() + ".stop: " + "destroying the bundle manager \n");

        logger.fine(this.getClass().getName() + ".stop: OK \n");
        astericsFrame.setVisible(false);
    }
}
