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

package eu.asterics.mw.displayguimanagement;

import java.io.File;

import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.cimcommunication.CIMEvent;
import eu.asterics.mw.cimcommunication.CIMEventHandler;
import eu.asterics.mw.cimcommunication.CIMEventPacketReceived;
import eu.asterics.mw.cimcommunication.CIMPortManager;
import eu.asterics.mw.cimcommunication.CIMProtocolPacket;
import eu.asterics.mw.displayguimanagement.IDisplayItem.NavigationDirection;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;
import eu.asterics.mw.services.IAREEventListener;
import eu.asterics.mw.systemstatechange.SystemChangeListener;
import eu.asterics.mw.systemstatechange.SystemChangeNotifier;

/**
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at] Date: July 23,
 *         2012 Time: 02:22:08 PM
 */
public class DisplayGuiManager implements IAREEventListener, SystemChangeListener {
    public static DisplayGuiManager instance = new DisplayGuiManager();
    boolean displayAvailable = false;
    // CIMPortController port = null;
    boolean errorsActive = false;

    public final static short CORE_CIM_ID = 0x0602;
    public final static int DISPLAY_WIDTH = 112;
    public final static int DISPLAY_HEIGHT = 64;

    private static final boolean debug = false;
    private static final boolean useLoggerForDebug = false;
    private static final boolean enableHeartBeat = true;
    boolean usbChanged = false;

    private DisplayRootCanvas rootCanvas = null;

    DisplayListCanvas mainMenuCanvas = null;
    DisplayListCanvas modelSwitchCanvas = null;
    DisplayListCanvas modelCanvas = null;

    boolean errorMessageActive = false;
    private DisplayCanvas errorMsgCanvas = null;
    DisplayErrorMessageButton errorButton = null;

    boolean shutdownActive = false;
    DisplayLabel shutDownLabel = null;

    public static void debugMessage(String s) {
        if (debug) {
            if (useLoggerForDebug) {
                AstericsErrorHandling.instance.getLogger().fine(s);
            } else {
                System.out.println(s);
            }

        }
    }

    DisplayCIMListener cimListener = new DisplayCIMListener();
    long lastDisplayActivity;

    private DisplayGuiManager() {
        debugMessage("DisplayGuiManager.DisplayGuiManager()");
        SystemChangeNotifier.instance.addListener(this);
        AREServices.instance.registerAREEventListener(this);
    }

    private void initDisplay() {
        debugMessage("DisplayGuiManager.initDisplay()");

        if (CIMPortManager.getInstance().getConnection(CORE_CIM_ID) != null) {
            // just do this once, we can assume that even with
            // USB device changes the display stays available
            displayAvailable = true;
            CIMPortManager.getInstance().getConnection(CORE_CIM_ID).addEventListener(cimListener);
            setupDisplayCIM();
            setupGui();
            if (enableHeartBeat) {
                // add a thread to enable blinking heart symbol on display to
                // indicate that ARE is up and running
                AstericsThreadPool.instance.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            while (true) {
                                CIMPortManager.getInstance().sendPacket(CORE_CIM_ID, null,
                                        CIMProtocolPacket.FEATURE_UNIQUE_SERIAL_NUMBER,
                                        CIMProtocolPacket.COMMAND_REQUEST_READ_FEATURE, false);
                                Thread.sleep(2000);
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }

            debugMessage("Display Gui Init ... done");
        } else {
            debugMessage("No core CIM found");
        }
    }

    void lockDisplay() {
    }

    void unlockDisplay() {
    }

    int writeToDisplay(short feature, byte[] data) {
        if (displayAvailable) {
            return CIMPortManager.getInstance().sendPacket(CORE_CIM_ID, data, feature,
                    CIMProtocolPacket.COMMAND_REQUEST_WRITE_FEATURE, false);
        }
        return -1;
    }

    private void setupDisplayCIM() {
        writeToDisplay((short) 0x70, null);

        byte[] data = { 1 };
        writeToDisplay((short) 0x83, data);
    }

    public boolean displayAvailable() {
        return displayAvailable;
    }

    void setupGui() {
        rootCanvas = new DisplayRootCanvas(0, 0, DISPLAY_WIDTH, 64);

        mainMenuCanvas = new DisplayListCanvas("  Main Menu", 0, 0, DISPLAY_WIDTH, 48);
        modelSwitchCanvas = new DisplayListCanvas("  Switch model", 0, 0, DISPLAY_WIDTH, 48);
        shutDownLabel = new DisplayLabel("Activating standby", 0, 0, DISPLAY_WIDTH, 64);
        rootCanvas.setName("Canvas:Root");
        modelSwitchCanvas.setName("Canvas:Model switch");
        mainMenuCanvas.setName("Canvas:Main menu");

        errorMsgCanvas = new DisplayCanvas(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        errorMsgCanvas.setName("Canvas:ErrorMsg");
        errorButton = new DisplayErrorMessageButton(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        errorButton.setName("Button:ErrorBtn");
        errorMsgCanvas.addChild(errorButton);

        // switch model canvas

        File dir = new File("./models");
        File[] models = dir.listFiles();

        for (File model : models) {
            if (model.isFile() && model.getName().endsWith(".acs")) {
                final String caption = model.getName().substring(0, model.getName().length() - 4);
                debugMessage("Adding button for model " + caption);
                DisplayButton button = new DisplayButton(caption, 0, 16, DISPLAY_WIDTH, 16);
                button.setName("Button (" + caption + ")");
                button.addEventListener(new IDisplayEventListener() {
                    @Override
                    public void action() {
                        AstericsErrorHandling.instance.getLogger().fine("Loading model: " + caption);
                        AREServices.instance.stopModel();

                        modelCanvas = new DisplayListCanvas(caption, 0, 0, DISPLAY_WIDTH, 48);

                        AREServices.instance.deployFile(caption + ".acs");
                        try {
                            AREServices.instance.runModel();
                            rootCanvas.setCanvas(modelCanvas);
                            drawDisplay();
                        } catch (AREAsapiException e) {
                            String errorMsg = "Could not run model: " + caption;
                            AstericsErrorHandling.instance.getLogger().severe(errorMsg);
                            onAreError(errorMsg);
                        }
                    }
                });
                modelSwitchCanvas.addChild(button);
            }

        }

        DisplayButton button = new DisplayButton("Switch Model", 0, 16, DISPLAY_WIDTH, 16);
        button.setName("Button (Switch model)");
        button.addEventListener(new IDisplayEventListener() {

            @Override
            public void action() {
                debugMessage("Switch model listener");
                rootCanvas.setCanvas(modelSwitchCanvas);
                drawDisplay();
            }
        });
        mainMenuCanvas.addChild(button);

        DisplayNavigationButton btnUp = new DisplayNavigationButton("up", 79, 48, 16, 16, NavigationDirection.UP);
        btnUp.canvasName = "NavButton (Up)";
        btnUp.setIcon(upIconData); //
        btnUp.setIconLocation(0, 0, 16, 16); //
        btnUp.enableText(false); //
        rootCanvas.addChild(btnUp);

        DisplayNavigationButton btnDown = new DisplayNavigationButton("dn", 95, 48, 16, 16, NavigationDirection.DOWN);
        btnDown.canvasName = "NavButton (Down)";
        btnDown.setIcon(downIconData); //
        btnDown.setIconLocation(0, 0, 16, 16); //
        btnDown.enableText(false); //
        rootCanvas.addChild(btnDown);

        DisplayNavigationButton btnBack = new DisplayNavigationButton("bk", 0, 48, 16, 16, NavigationDirection.BACK);
        btnBack.canvasName = "NavButton (Back)";
        btnBack.setIcon(backIconData); //
        btnBack.setIconLocation(0, 0, 16, 16); //
        btnBack.enableText(false); //
        rootCanvas.addChild(btnBack);

        DisplayButton stopModelButton = new DisplayButton("st", 48, 48, 16, 16);
        stopModelButton.setName("Button (Stop Model)");
        stopModelButton.canvasName = "btnStop"; //
        stopModelButton.setIcon(stopIconData); //
        stopModelButton.setIconLocation(0, 0, 16, 16); //
        stopModelButton.enableText(false); //
        stopModelButton.addEventListener(new IDisplayEventListener() {

            @Override
            public void action() {
                debugMessage("Stop model listener");
                AREServices.instance.stopModel();
            }

        });
        rootCanvas.addChild(stopModelButton);

        DisplayButton playModelButton = new DisplayButton("pl", 32, 48, 16, 16);
        playModelButton.setName("Button (Play Model)");
        playModelButton.canvasName = "btnPlay";
        playModelButton.setIcon(playIconData);
        playModelButton.setIconLocation(0, 0, 16, 16);
        playModelButton.enableText(false);

        playModelButton.addEventListener(new IDisplayEventListener() {

            @Override
            public void action() {
                debugMessage("Play model listener");
                if (AREServices.instance.isAREStoppedAndHealthy()) {
                    try {
                        AREServices.instance.runModel();
                    } catch (AREAsapiException e) {
                        AstericsErrorHandling.instance.getLogger().severe("Could not run model");
                    }
                }
            }

        });
        rootCanvas.addChild(playModelButton);

        // set main menu and draw display
        rootCanvas.setMainMenuCanvas(mainMenuCanvas);

        displayMainMenu();
    }

    void displayPress(int x, int y) {
        if (!shutdownActive) {
            if (errorMessageActive) {
                errorMsgCanvas.press(x, y);
            } else {
                rootCanvas.press(x, y);
            }
        }
    }

    public void addGuiSlider(String menuTitle, String sliderTitle, Double initValue, Double stepSize,
            Double lowerBoundary, Double upperBoundary, IGuiChangeListener listener) {
        // DisplayButton mainMenuItem = new DisplayButton(menuTitle, 0, 0,
        // DISPLAY_WIDTH, 16);
        // final DisplayListCanvas dlcCanvas = new DisplayListCanvas(" " +
        // sliderTitle, 0,0,DISPLAY_WIDTH, 48);
        modelCanvas.addChild(new DisplayLabel("* " + sliderTitle, 0, 0, DISPLAY_WIDTH, 16));

        DisplaySlider slider = new DisplaySlider(0, 0, DISPLAY_WIDTH, 16);
        slider.setValue(initValue);
        slider.setStepSize(stepSize);
        if (lowerBoundary != null && upperBoundary != null) {
            slider.setBoundaries(lowerBoundary, upperBoundary);
        }
        slider.addGuiChangeListener(listener);
        modelCanvas.addChild(slider);
        /*
         * mainMenuItem.addEventListener(new IDisplayEventListener() {
         * 
         * @Override public void action() { rootCanvas.displayCanvas(dlcCanvas);
         * } } );
         * 
         * mainMenuCanvas.addChild(mainMenuItem);
         */
    }

    class DisplayCIMListener implements CIMEventHandler {

        @Override
        public void handlePacketReceived(CIMEvent e) {
            CIMProtocolPacket p = ((CIMEventPacketReceived) e).packet;

            // System.out.println("Incoming packet: " + p.toString());

            if (p.getFeatureAddress() == 0x82) {
                int x = 0, y = 0;
                byte[] data = p.getData();
                x = data[1] << 8 | data[0];
                y = data[3] << 8 | data[2];

                debugMessage("Press: [" + x + "," + y + "]");
                displayPress(x, y);

            }
        }

        @Override
        public void handlePacketError(CIMEvent e) {
        }
    }

    @Override
    public void preDeployModel() {
    }

    @Override
    public void postDeployModel() {
        // debugMessage("DisplayGuiManager.postDeployModel()");
        // rootCanvas.displayCanvas(mainMenuCanvas);
    }

    @Override
    public void preStartModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postStartModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void preStopModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void prePauseModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postPauseModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postStopModel() {
        if (usbChanged) {
            debugMessage("DisplayGuiManager.postStopModel() with usbChanged");
        }
        debugMessage("DisplayGuiManager.postStopModel()");
    }

    @Override
    public void preResumeModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postResumeModel() {
        // TODO Auto-generated method stub

    }

    @Override
    public void preBundlesInstalled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void postBundlesInstalled() {
        debugMessage("DisplayGuiManager.postBundlesInstalled()");
        initDisplay();
    }

    @Override
    public void usbDevicesAttached() {
        debugMessage("DisplayGuiManager.usbDevicesAttached()");
        usbChanged = true;
    }

    @Override
    public void usbDevicesRemoved() {
        debugMessage("DisplayGuiManager.usbDevicesRemoved()");
        usbChanged = true;
    }

    byte[] playInvertedIconData = new byte[8 * 16];
    byte playIconData[] = { (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33 };

    byte[] stopInvertedIconData = new byte[8 * 16];
    byte stopIconData[] = { (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0xf5, (byte) 0x5f, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33 };

    byte[] upInvertedIconData = new byte[8 * 16];
    byte upIconData[] = { (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x5f, (byte) 0xf5, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0xf5, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33 };

    byte[] downInvertedIconData = new byte[8 * 16];
    byte downIconData[] = { (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0xf5, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x5f, (byte) 0xf5, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33 };

    byte[] backInvertedIconData = new byte[8 * 16];
    byte backIconData[] = { (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x5f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x5f, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0x5f, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xf5,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0x5f, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x55, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0x5f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0xf3, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
            (byte) 0x3f, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33,
            (byte) 0x33 };

    @Override
    public void onAreError(String msg) {
        if (displayAvailable) {
            errorButton.addErrorMessage(msg);
            errorMessageActive = true;
            drawDisplay();
        }
    }

    private void drawDisplay() {
        if (shutdownActive) {
            shutDownLabel.draw();
        } else if (errorMessageActive) {
            errorMsgCanvas.draw();
        } else {
            rootCanvas.draw();
        }
    }

    /**
     * Callback when system is requesting standby. Will display standby
     * activation message label
     */
    @Override
    public void systemSleepRequested() {
        if (displayAvailable) {
            shutdownActive = true;
            drawDisplay();
        }
    }

    /**
     * Callback when system is entering standby. Will display standby activation
     * message label
     */
    @Override
    public void systemSleep() {
        if (displayAvailable) {
            shutdownActive = true;
            drawDisplay();
        }
    }

    /**
     * Callback when system is resuming from standby. Will display last active
     * canvas.
     */
    @Override
    public void systemResume() {
        if (displayAvailable) {
            shutdownActive = false;
            drawDisplay();
        }
    }

    public void displayMainMenu() {
        rootCanvas.setCanvas(mainMenuCanvas);
        drawDisplay();
    }

    void displayCanvas(DisplayCanvas canvas) {
        rootCanvas.setCanvas(canvas);
        drawDisplay();
    }

    void navigate(NavigationDirection nav) {
        rootCanvas.navigate(nav);
    }

}
