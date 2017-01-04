
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

package eu.asterics.component.actuator.remotewindow;

import java.awt.Point;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.services.AREServices;

/**
 * 
 * Sets Position and State of a remote window (identified by window name)
 * 
 * 
 * 
 * @author Chris Veigl Date: 29 12 2014
 */
public class RemoteWindowInstance extends AbstractRuntimeComponentInstance {
    final int MODE_EXACT_MATCH = 0;
    final int MODE_CONTAINS_TEXT = 1;

    String propWindowName = "Notepad";
    int propXPos = 0;
    int propYPos = 0;
    int propMode = MODE_CONTAINS_TEXT;
    boolean propAutoSetPosition = false;

    /**
     * The class constructor.
     */
    public RemoteWindowInstance() {
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
        if ("name".equalsIgnoreCase(portID)) {
            return ipName;
        }
        if ("xPos".equalsIgnoreCase(portID)) {
            return ipXPos;
        }
        if ("yPos".equalsIgnoreCase(portID)) {
            return ipYPos;
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
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("moveToTop".equalsIgnoreCase(eventPortID)) {
            return elpMoveToTop;
        }
        if ("moveToBottom".equalsIgnoreCase(eventPortID)) {
            return elpMoveToBottom;
        }
        if ("moveToLeft".equalsIgnoreCase(eventPortID)) {
            return elpMoveToLeft;
        }
        if ("moveToRight".equalsIgnoreCase(eventPortID)) {
            return elpMoveToRight;
        }
        if ("moveToCenter".equalsIgnoreCase(eventPortID)) {
            return elpMoveToCenter;
        }
        if ("minimize".equalsIgnoreCase(eventPortID)) {
            return elpMinimize;
        }
        if ("restore".equalsIgnoreCase(eventPortID)) {
            return elpRestore;
        }
        if ("bringToFront".equalsIgnoreCase(eventPortID)) {
            return elpBringToFront;
        }
        if ("moveNow".equalsIgnoreCase(eventPortID)) {
            return elpMoveNow;
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
        if ("windowName".equalsIgnoreCase(propertyName)) {
            return propWindowName;
        }
        if ("mode".equalsIgnoreCase(propertyName)) {
            return propMode;
        }
        if ("xPos".equalsIgnoreCase(propertyName)) {
            return propXPos;
        }
        if ("yPos".equalsIgnoreCase(propertyName)) {
            return propYPos;
        }
        if ("setAutoSetPosition".equalsIgnoreCase(propertyName)) {
            return propAutoSetPosition;
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
        if ("windowName".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propWindowName;
            propWindowName = (String) newValue;
            return oldValue;
        }
        if ("mode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMode;
            propMode = Integer.parseInt(newValue.toString());
            return oldValue;
        }

        if ("xPos".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propXPos;
            propXPos = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("yPos".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propYPos;
            propYPos = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("autoSetPosition".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAutoSetPosition;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAutoSetPosition = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAutoSetPosition = false;
            }
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipName = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propWindowName = ConversionUtils.stringFromBytes(data);
            if (propAutoSetPosition == true) {
                elpMoveNow.receiveEvent(null);
            }
        }
    };
    private final IRuntimeInputPort ipXPos = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propXPos = ConversionUtils.intFromBytes(data);
            if (propAutoSetPosition == true) {
                elpMoveNow.receiveEvent(null);
            }

        }
    };
    private final IRuntimeInputPort ipYPos = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propYPos = ConversionUtils.intFromBytes(data);
            if (propAutoSetPosition == true) {
                elpMoveNow.receiveEvent(null);
            }

        }
    };

    public interface User32 extends StdCallLibrary {

        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        final int SWP_NOSIZE = 1;
        final int SWP_RESTORE = 9;
        final int SWP_MINIMIZE = 6;

        interface WNDENUMPROC extends StdCallCallback {
            boolean callback(Pointer hWnd, Pointer arg);
        }

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

        int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);

        int GetWindowRect(Pointer handle, int[] rect);

        Pointer FindWindowA(byte[] lpClass, byte[] title);

        boolean ShowWindow(Pointer hWnd, int nCmdShow);

        boolean SetWindowPos(Pointer hWnd, Pointer hWndInsertAfter, int X, int Y, int cx, int cy, int uFlags);

        boolean SetForegroundWindow(HWND hWnd);

    }

    private void moveNativeWindow(Pointer hWnd) {
        User32.INSTANCE.SetWindowPos(hWnd, null, propXPos, propYPos, 0, 0, User32.SWP_NOSIZE);
    }

    private void moveNativeWindowToTop(Pointer hWnd) {
        int[] rect = { 0, 0, 0, 0 };
        int result = User32.INSTANCE.GetWindowRect(hWnd, rect);
        if (result != 0) {
            int xPos = rect[0] + propXPos;
            int yPos = propYPos;
            User32.INSTANCE.SetWindowPos(hWnd, null, xPos, yPos, 0, 0, User32.SWP_NOSIZE);
        }
    }

    private void moveNativeWindowToBottom(Pointer hWnd) {
        int[] rect = { 0, 0, 0, 0 };
        int result = User32.INSTANCE.GetWindowRect(hWnd, rect);
        if (result != 0) {
            int xPos = rect[0] + propXPos;
            int height = rect[3] - rect[1];
            Point screen = AREServices.instance.getScreenDimension();
            int yPos = screen.y - height + propYPos;
            User32.INSTANCE.SetWindowPos(hWnd, null, xPos, yPos, 0, 0, User32.SWP_NOSIZE);
        }
    }

    private void moveNativeWindowToLeft(Pointer hWnd) {
        int[] rect = { 0, 0, 0, 0 };
        int result = User32.INSTANCE.GetWindowRect(hWnd, rect);
        if (result != 0) {
            int yPos = rect[1] + propYPos;
            int xPos = propXPos;
            User32.INSTANCE.SetWindowPos(hWnd, null, xPos, yPos, 0, 0, User32.SWP_NOSIZE);
        }
    }

    private void moveNativeWindowToRight(Pointer hWnd) {
        int[] rect = { 0, 0, 0, 0 };
        int result = User32.INSTANCE.GetWindowRect(hWnd, rect);
        if (result != 0) {
            int yPos = rect[1] + propYPos;
            int width = rect[2] - rect[0];
            Point screen = AREServices.instance.getScreenDimension();
            int xPos = screen.x - width + propXPos;
            User32.INSTANCE.SetWindowPos(hWnd, null, xPos, yPos, 0, 0, User32.SWP_NOSIZE);
        }
    }

    private void moveNativeWindowToCenter(Pointer hWnd) {
        int[] rect = { 0, 0, 0, 0 };
        int result = User32.INSTANCE.GetWindowRect(hWnd, rect);
        if (result != 0) {
            int width = rect[2] - rect[0];
            int height = rect[3] - rect[1];
            Point screen = AREServices.instance.getScreenDimension();
            int xPos = screen.x / 2 - width / 2 + propXPos;
            int yPos = screen.y / 2 - height / 2 + propYPos;
            User32.INSTANCE.SetWindowPos(hWnd, null, xPos, yPos, 0, 0, User32.SWP_NOSIZE);
        }
    }

    private void minimizeNativeWindow(Pointer hWnd) {
        User32.INSTANCE.ShowWindow(hWnd, User32.SWP_MINIMIZE);
    }

    private void restoreNativeWindow(Pointer hWnd) {
        User32.INSTANCE.ShowWindow(hWnd, User32.SWP_RESTORE);
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpMoveToTop = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                // System.out.println("Try to find Window "+propWindowName);
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindowToTop(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            moveNativeWindowToTop(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpMoveToBottom = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindowToBottom(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            moveNativeWindowToBottom(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpMoveToLeft = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindowToLeft(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            moveNativeWindowToLeft(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpMoveToRight = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindowToRight(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            moveNativeWindowToRight(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };

    final IRuntimeEventListenerPort elpMoveToCenter = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindowToCenter(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            moveNativeWindowToCenter(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };

    final IRuntimeEventListenerPort elpMinimize = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                System.out.println("Try to Minimize " + propWindowName);
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    minimizeNativeWindow(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            // System.out.println("Found matching window " +
                            // wText);
                            minimizeNativeWindow(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpRestore = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    restoreNativeWindow(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            restoreNativeWindow(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpBringToFront = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    minimizeNativeWindow(hWnd);
                    restoreNativeWindow(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            minimizeNativeWindow(hWnd);
                            restoreNativeWindow(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };
    final IRuntimeEventListenerPort elpMoveNow = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            final User32 user32 = User32.INSTANCE;

            if (propMode == MODE_EXACT_MATCH) {
                Pointer hWnd = User32.INSTANCE.FindWindowA(null, Native.toByteArray(propWindowName)); // window
                                                                                                      // title
                if (hWnd != null) {
                    moveNativeWindow(hWnd);
                }
            } else {
                user32.EnumWindows(new User32.WNDENUMPROC() {
                    @Override
                    public boolean callback(Pointer hWnd, Pointer userData) {
                        byte[] windowText = new byte[512];
                        user32.GetWindowTextA(hWnd, windowText, 512);
                        String wText = Native.toString(windowText);
                        if (wText.contains(propWindowName)) {
                            moveNativeWindow(hWnd);
                            return false;
                        }
                        return true;
                    }
                }, null);
            }
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {

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

        super.stop();
    }
}