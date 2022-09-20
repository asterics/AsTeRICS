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

package eu.asterics.component.sensor.spacenavigtor3Dmouse;

import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Interfaces the 3D Mouse native library for the Mouse3DInstance class.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 11, 2011 Time: 4:27:47
 *         PM
 */

public class SpaceNavigtor3DMouseBridge implements Runnable {
    /**
     * Statically load the native library
     */
    static {
        System.loadLibrary("Mouse3Dlibrary");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"Mouse3Dlibrary.dll\" ... ok!");

        System.loadLibrary("Mouse3DBridge");
        AstericsErrorHandling.instance.getLogger().fine("Loading \"Mouse3DBridge.dll\" ... ok!");
    }

    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseX;
    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseY;
    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseZ;
    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseRx;
    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseRy;
    private final SpaceNavigtor3DMouseInstance.OutputPort opMouseRz;
    private final SpaceNavigtor3DMouseInstance.OutputPort opButtons;
    private final IRuntimeComponentInstance componentInstance;
    private final int No_3D_mouse_device_found = -5;

    // Thread t;
    long[] mouse3DData;
    private boolean active = false;
    private int propInterval = 300;
    private boolean exit = false;

    /**
     * The class constructor.
     */
    public SpaceNavigtor3DMouseBridge(final IRuntimeComponentInstance componentInstance,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseX,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseY,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseZ,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseRx,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseRy,
            final SpaceNavigtor3DMouseInstance.OutputPort opMouseRz,
            final SpaceNavigtor3DMouseInstance.OutputPort opButtons) {
        this.opMouseX = opMouseX;
        this.opMouseY = opMouseY;
        this.opMouseZ = opMouseZ;
        this.opMouseRx = opMouseRx;
        this.opMouseRy = opMouseRy;
        this.opMouseRz = opMouseRz;
        this.opButtons = opButtons;
        this.componentInstance = componentInstance;

        mouse3DData = new long[7];
    }

    /**
     * Sets the interval for reading data from the 3D mouse.
     * 
     * @param newInterval
     *            interval
     */
    public void setInterval(int newInterval) {
        propInterval = newInterval;
    }

    /**
     * Gets the interval.
     * 
     * @return interval
     */
    public int getInterval() {
        return propInterval;
    }

    public void pause() {
        active = false;
    }

    public void resume() {
        active = true;
    }

    /**
     * Activates the class.
     */
    public void start() {

        int result = activate();

        if (result < 0) {
            if (result == No_3D_mouse_device_found) {
                AstericsErrorHandling.instance.getLogger().warning("3D mouse not found!");
            } else {
                AstericsErrorHandling.instance.reportError(componentInstance,
                        "3D mouse start Error! ErrorCode:" + Integer.toString(result));
            }
        } else {
            exit = false;
            active = true;
            AstericsThreadPool.instance.execute(this);
        }

    }

    /**
     * Deactivates the class.
     */
    public void stop() {
        int result = deactivate();
        exit = true;
        active = false;

        if (result < 0) {
            AstericsErrorHandling.instance.reportInfo(componentInstance, "3D mouse stop Error!");
        }
    }

    /**
     * Thread function.
     */
    @Override
    public void run() {
        exit = false;
        while (exit == false) {
            if (active) {
                int result = getData(mouse3DData);

                if (result > 0) {

                    int x = (int) mouse3DData[0];
                    opMouseX.sendData(x);

                    int y = (int) mouse3DData[1];
                    opMouseY.sendData(y);

                    int z = (int) mouse3DData[2];
                    opMouseZ.sendData(z);

                    int rx = (int) mouse3DData[3];
                    opMouseRx.sendData(rx);

                    int ry = (int) mouse3DData[4];
                    opMouseRy.sendData(ry);

                    int rz = (int) mouse3DData[5];
                    opMouseRz.sendData(rz);

                    int b = (int) mouse3DData[6];
                    opButtons.sendData(b);
                } else {
                    AstericsErrorHandling.instance.reportInfo(componentInstance, "3D mouse read data Error!");
                }
            }

            try {
                Thread.sleep(propInterval);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Activates the underlying native code/hardware.
     *
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int activate();

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int deactivate();

    /**
     * Getting data from 3D Mouse.
     *
     * @param output
     *            data from the 3D mouse
     * @return 1 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    native public int getData(long[] output);

}