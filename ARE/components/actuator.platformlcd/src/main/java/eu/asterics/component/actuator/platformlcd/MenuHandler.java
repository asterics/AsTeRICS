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

package eu.asterics.component.actuator.platformlcd;

import java.io.File;
import java.util.ArrayList;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * MenuHandler operates all the navigation actions in the menu of the core CIM
 * display.
 * 
 * @author Chris Weiss [weissch@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:55:05 AM
 */
public class MenuHandler {
    PlatformLCDInstance owner;

    MenuItem modelSwitchMenuItem = null;
    // MenuItem dummyMenuItem = null;

    MenuItem baseItem = null;;

    MenuItem currentItem = null;

    /**
     * In the constructor the menu structure is set up in hard code. It scans
     * for stored models and generates a menu tree for those.
     * 
     * @param owner
     *            the display instance which generates this MenuHandler instance
     */
    public MenuHandler(PlatformLCDInstance owner) {
        this.owner = owner;

        File dir = new File("./models");
        File[] models = dir.listFiles();
        ModelMenuItem[] storedModels = new ModelMenuItem[1];

        ArrayList<ModelMenuItem> storedModelList = new ArrayList<ModelMenuItem>();

        modelSwitchMenuItem = new MenuItem("Switch model", owner);
        // dummyMenuItem = new MenuItem("dummy", owner);

        baseItem = modelSwitchMenuItem;

        // modelSwitchMenuItem.setLeft(dummyMenuItem);
        // modelSwitchMenuItem.setRight(dummyMenuItem);

        // dummyMenuItem.setLeft(modelSwitchMenuItem);
        // dummyMenuItem.setRight(modelSwitchMenuItem);

        for (int i = 0; i < models.length; i++) {
            AstericsErrorHandling.instance.getLogger().fine("Adding " + models[i].getName());
            if (models[i].isFile()) {
                storedModelList.add(new ModelMenuItem(models[i].getName(), owner));
            }
        }

        storedModels = storedModelList.toArray(storedModels);
        for (int i = 0; i < storedModels.length; i++) {
            try {
                storedModels[i].left = storedModels[(((i - 1) % storedModels.length) + storedModels.length)
                        % storedModels.length];
                storedModels[i].right = storedModels[(i + 1) % storedModels.length];
                storedModels[i].up = modelSwitchMenuItem;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(String.format("i = %d, modulo: %d", i,
                        (((i - 1) % storedModels.length) + storedModels.length) % storedModels.length));
            }
        }
        modelSwitchMenuItem.down = storedModels[0];
        modelSwitchMenuItem.ok = storedModels[0];

        AstericsErrorHandling.instance.getLogger().fine("MenuHandler constructed");
    }

    /**
     * Should be called when the display is switch to inactive. Clears the
     * current item
     */
    public void inactivity() {
        currentItem = null;
    }

    /**
     * Should be called when the display is reactivated
     * 
     * @return false if there has been no active menu item set, true otherwise
     */
    private boolean wakeUpDisplay() {
        if (currentItem == null) {
            currentItem = baseItem;
            currentItem.display();
            return false;
        }
        return true;
    }

    /**
     * Moves active menu item to the next item left
     */
    public synchronized void handleLeft() {
        if (wakeUpDisplay()) {
            if (currentItem.left != null) {
                currentItem = currentItem.left;
                currentItem.display();
            }
        }
    }

    /**
     * Moves active menu item to the next item right
     */
    public synchronized void handleRight() {
        if (wakeUpDisplay()) {
            if (currentItem.right != null) {
                currentItem = currentItem.right;
                currentItem.display();
            }
        }
    }

    /**
     * Moves active menu item to the next item up
     */
    public synchronized void handleUp() {
        if (wakeUpDisplay()) {
            if (currentItem.up != null) {
                currentItem = currentItem.up;
                currentItem.display();
            }
        }
    }

    /**
     * Moves active menu item to the next item down
     */
    public synchronized void handleDown() {
        if (wakeUpDisplay()) {
            if (currentItem.down != null) {
                currentItem = currentItem.down;
                currentItem.display();
            }
        }
    }

    /**
     * Will perform the action of the ok button slot on the active menu item
     */
    public synchronized void handleOk() {
        if (wakeUpDisplay()) {
            if (currentItem.ok != null) {
                currentItem = currentItem.ok;
                currentItem.display();
            } else {
                if (currentItem instanceof ActionMenuItem) {
                    ((ActionMenuItem) currentItem).action();
                }
            }
        }
    }

}
