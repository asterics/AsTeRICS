
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.folderbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * 
 * browser for folders / directories ad files in a filesystem
 * 
 * 
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: 2016-12-16
 */
public class FolderBrowserInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opFolderName = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opFolderPath = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opFileNames = new DefaultRuntimeOutputPort();
    final IRuntimeOutputPort opFilePaths = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    String propInitialFolder = ".";
    boolean propWrapAround = true;
    boolean propExitInitialFolder = false;
    boolean propAutoListFiles = false;
    String propNoFolderMessage = "no subfolder available";
    String propNoFileMessage = "no file available";

    // declare member variables here

    String currentFolder = "none";
    List<String> actFiles = null;
    List<String> actFolders = null;
    int currentIndex = -1;
    private Stack<Integer> folderpos = new Stack<Integer>();

    /**
     * The class constructor.
     */
    public FolderBrowserInstance() {
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
        if ("folderName".equalsIgnoreCase(portID)) {
            return opFolderName;
        }
        if ("folderPath".equalsIgnoreCase(portID)) {
            return opFolderPath;
        }
        if ("fileNames".equalsIgnoreCase(portID)) {
            return opFileNames;
        }
        if ("filePaths".equalsIgnoreCase(portID)) {
            return opFilePaths;
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
        if ("next".equalsIgnoreCase(eventPortID)) {
            return elpNext;
        }
        if ("previous".equalsIgnoreCase(eventPortID)) {
            return elpPrevious;
        }
        if ("enter".equalsIgnoreCase(eventPortID)) {
            return elpEnter;
        }
        if ("exit".equalsIgnoreCase(eventPortID)) {
            return elpExit;
        }
        if ("current".equalsIgnoreCase(eventPortID)) {
            return elpCurrent;
        }
        if ("listFiles".equalsIgnoreCase(eventPortID)) {
            return elpListFiles;
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
        if ("initialFolder".equalsIgnoreCase(propertyName)) {
            return propInitialFolder;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            return propWrapAround;
        }
        if ("exitInitialFolder".equalsIgnoreCase(propertyName)) {
            return propExitInitialFolder;
        }
        if ("autoListFiles".equalsIgnoreCase(propertyName)) {
            return propAutoListFiles;
        }
        if ("noFolderMessage".equalsIgnoreCase(propertyName)) {
            return propNoFolderMessage;
        }
        if ("noFileMessage".equalsIgnoreCase(propertyName)) {
            return propNoFileMessage;
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
        if ("initialFolder".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInitialFolder;
            propInitialFolder = (String) newValue;
            return oldValue;
        }
        if ("wrapAround".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWrapAround;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propWrapAround = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propWrapAround = false;
            }
            return oldValue;
        }
        if ("exitInitialFolder".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propExitInitialFolder;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propExitInitialFolder = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propExitInitialFolder = false;
            }
            return oldValue;
        }
        if ("autoListFiles".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoListFiles;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoListFiles = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoListFiles = false;
            }
            return oldValue;
        }
        if ("noFolderMessage".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propNoFolderMessage;
            propNoFolderMessage = (String) newValue;
            return oldValue;
        }
        if ("noFileMessage".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propNoFileMessage;
            propNoFileMessage = (String) newValue;
            return oldValue;
        }

        return null;
    }

    String stripFolderPath(String inputPath) {
        if (inputPath.lastIndexOf("\\") >= 0) {
            return (inputPath.substring(inputPath.lastIndexOf("\\") + 1));
        } else if (inputPath.lastIndexOf("/") >= 0) {
            return (inputPath.substring(inputPath.lastIndexOf("/") + 1));
        }
        return (inputPath);
    }

    String stripFilePath(String inputPath) {
        if (inputPath.lastIndexOf("\\") >= 0) {
            return (inputPath.substring(inputPath.lastIndexOf("\\") + 1));
        } else if (inputPath.lastIndexOf("/") >= 0) {
            return (inputPath.substring(inputPath.lastIndexOf("/") + 1));
        }
        return (inputPath);
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpNext = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (actFolders == null) {
                return;
            }
            if (actFolders.size() == 0) {
                System.out.println("no subfolder");
                opFolderName.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                return;
            }
            currentIndex++;
            if (currentIndex >= actFolders.size()) {
                if (propWrapAround == true) {
                    currentIndex = 0;
                } else {
                    currentIndex = actFolders.size() - 1;
                }
            }
            System.out.println(
                    "act element (" + currentIndex + "/" + actFolders.size() + ") :" + actFolders.get(currentIndex));
            opFolderName.sendData(ConversionUtils.stringToBytes(stripFolderPath(actFolders.get(currentIndex))));
            opFolderPath.sendData(ConversionUtils.stringToBytes(actFolders.get(currentIndex)));
        }
    };
    final IRuntimeEventListenerPort elpPrevious = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (actFolders == null) {
                return;
            }
            if (actFolders.size() == 0) {
                System.out.println("no subfolder");
                opFolderName.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                opFolderPath.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                return;
            }
            currentIndex--;
            if (currentIndex < 0) {
                if (propWrapAround == true) {
                    currentIndex = actFolders.size() - 1;
                } else {
                    currentIndex = 0;
                }
            }
            System.out.println(
                    "act element (" + currentIndex + "/" + actFolders.size() + ") :" + actFolders.get(currentIndex));
            opFolderName.sendData(ConversionUtils.stringToBytes(stripFolderPath(actFolders.get(currentIndex))));
            opFolderPath.sendData(ConversionUtils.stringToBytes(actFolders.get(currentIndex)));
        }
    };
    final IRuntimeEventListenerPort elpEnter = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (actFolders == null) {
                return;
            }
            if ((actFolders.size() == 0) || (currentIndex < 0)) {
                System.out.println("no subfolder");
                opFolderName.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                opFolderPath.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                return;
            }
            if (currentIndex >= 0) {
                System.out.println("enter folder: " + actFolders.get(currentIndex));
                folderpos.push(currentIndex);
                getFolderList(actFolders.get(currentIndex));
                opFolderName.sendData(ConversionUtils.stringToBytes(stripFolderPath(currentFolder)));
                opFolderPath.sendData(ConversionUtils.stringToBytes(currentFolder));
            }

        }
    };
    final IRuntimeEventListenerPort elpExit = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if ((propExitInitialFolder == false) && (currentFolder.equals(propInitialFolder))) {
                return;
            }
            String targetFolder;
            System.out.println("exit folder: " + currentFolder);
            if (currentFolder.lastIndexOf("\\") >= 0) {
                // format
                targetFolder = currentFolder.substring(0, currentFolder.lastIndexOf("\\"));
            } else if (currentFolder.lastIndexOf("/") >= 0) {
                // format
                targetFolder = currentFolder.substring(0, currentFolder.lastIndexOf("/"));
            } else {
                System.out.println("could not exit folder" + currentFolder);
                opFolderName.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                opFolderPath.sendData(ConversionUtils.stringToBytes(propNoFolderMessage));
                return;
            }
            System.out.println("change to folder: " + targetFolder);
            getFolderList(targetFolder);
            currentIndex = folderpos.pop();
            opFolderName.sendData(ConversionUtils.stringToBytes(stripFolderPath(targetFolder)));
            opFolderPath.sendData(ConversionUtils.stringToBytes(targetFolder));
        }
    };
    final IRuntimeEventListenerPort elpCurrent = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            System.out.println("current folder: " + currentFolder);
            opFolderName.sendData(ConversionUtils.stringToBytes(stripFolderPath(currentFolder)));
            opFolderPath.sendData(ConversionUtils.stringToBytes(currentFolder));
        }
    };
    final IRuntimeEventListenerPort elpListFiles = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if ((actFolders == null) || (actFiles == null)) {
                opFileNames.sendData(ConversionUtils.stringToBytes(propNoFileMessage));
                opFilePaths.sendData(ConversionUtils.stringToBytes(propNoFileMessage));
                return;
            }
            System.out.println("list files in current folder: " + currentFolder);
            for (int i = 0; i < actFiles.size(); i++) {
                System.out.println("file: " + actFiles.get(i));
                opFileNames.sendData(ConversionUtils.stringToBytes(stripFilePath(actFiles.get(i))));
                opFilePaths.sendData(ConversionUtils.stringToBytes(actFiles.get(i)));
            }
        }
    };

    public void getFolderList(String root) {
        System.out.println("folderBrowser: root folder=" + root);

        try {
            File pathName = new File(root);

            if (!pathName.isDirectory()) {
                System.out.println("folderBrowser: " + root + " is not a folder - cancelling");
                return; // cancel if root is not a directory
            }

            currentFolder = root;
            currentIndex = -1;
            actFolders = new ArrayList<String>();
            actFiles = new ArrayList<String>();

            String[] fileNames = pathName.list(); // lists all files in the
                                                  // directory

            for (int i = 0; i < fileNames.length; i++) {
                File f = new File(pathName.getPath(), fileNames[i]); // getPath
                                                                     // converts
                                                                     // abstract
                                                                     // path to
                                                                     // path in
                                                                     // String,
                // constructor creates new File object with fileName name
                if (f.isDirectory()) {
                    // currentIndex=0;
                    actFolders.add(f.getPath());
                    // System.out.println("adding sub folder: " + f.getPath());
                } else {
                    actFiles.add(f.getPath());
                    // System.out.println("adding file: " + f.getPath());
                    if (propAutoListFiles == true) {
                        opFileNames.sendData(ConversionUtils.stringToBytes(stripFilePath(f.getPath())));
                        opFilePaths.sendData(ConversionUtils.stringToBytes(f.getPath()));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("could not find directories !");
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        getFolderList(propInitialFolder);
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

        super.stop();
    }
}