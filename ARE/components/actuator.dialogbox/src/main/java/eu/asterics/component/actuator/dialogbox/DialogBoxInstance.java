
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

package eu.asterics.component.actuator.dialogbox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class DialogBoxInstance extends AbstractRuntimeComponentInstance {
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    String propCaption = "info";
    String propText = "this is an information";
    boolean propAlwaysOnTop = true;
    private int propMessageType = 0;
    private int NUM_BUTTONS = 5;
    private String[] propButtonTextArray = new String[NUM_BUTTONS];
    final IRuntimeEventTriggererPort[] etpButtonPressed = new DefaultRuntimeEventTriggererPort[NUM_BUTTONS];

    // declare member variables here

    JOptionPane op = null;
    JDialog dialog = null;

    /**
     * The class constructor.
     */
    public DialogBoxInstance() {
        for (int i = 0; i < NUM_BUTTONS; ++i) {
            etpButtonPressed[i] = new DefaultRuntimeEventTriggererPort();
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
        if ("setText".equalsIgnoreCase(portID)) {
            return ipSetText;
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
        if ("displayBox".equalsIgnoreCase(eventPortID)) {
            return elpDisplayBox;
        }
        if ("hideBox".equalsIgnoreCase(eventPortID)) {
            return elpHideBox;
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
        for (int i = 1; i < NUM_BUTTONS + 1; ++i) {
            if (("button" + i).equals(eventPortID)) {
                return etpButtonPressed[i - 1];
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
        if ("caption".equalsIgnoreCase(propertyName)) {
            return propCaption;
        }
        if ("text".equalsIgnoreCase(propertyName)) {
            return propText;
        }
        if ("alwaysOnTop".equalsIgnoreCase(propertyName)) {
            return propAlwaysOnTop;
        }
        if ("messageType".equalsIgnoreCase(propertyName)) {
            return propMessageType;
        }
        for (int i = 1; i < NUM_BUTTONS + 1; ++i) {
            if (("buttonText" + i).equalsIgnoreCase(propertyName)) {
                return propButtonTextArray[i - 1];
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
        if ("caption".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCaption;
            propCaption = (String) newValue;
            return oldValue;
        }
        if ("text".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propText;
            propText = (String) newValue;
            return oldValue;
        }
        if ("alwaysOnTop".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propAlwaysOnTop;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propAlwaysOnTop = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propAlwaysOnTop = false;
            }
            return oldValue;
        }
        if ("messageType".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMessageType;
            propMessageType = Integer.parseInt((String) newValue);
            return oldValue;
        }
        for (int i = 1; i < NUM_BUTTONS + 1; ++i) {
            if (("buttonText" + i).equalsIgnoreCase(propertyName)) {
                final Object oldValue = propButtonTextArray[i - 1];
                propButtonTextArray[i - 1] = (String) newValue;
                return oldValue;
            }
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipSetText = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propText = ConversionUtils.stringFromBytes(data);
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpDisplayBox = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (op == null) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        propText = propText.replaceAll("/n", "\n");

                        int messageType;
                        if (propMessageType == 1) {
                            messageType = JOptionPane.INFORMATION_MESSAGE;
                        } else if (propMessageType == 2) {
                            messageType = JOptionPane.QUESTION_MESSAGE;
                        } else if (propMessageType == 3) {
                            messageType = JOptionPane.WARNING_MESSAGE;
                        } else if (propMessageType == 4) {
                            messageType = JOptionPane.ERROR_MESSAGE;
                        } else {
                            messageType = JOptionPane.PLAIN_MESSAGE;
                        }

                        List<String> buttons = new ArrayList<String>();
                        for (int i = 0; i < NUM_BUTTONS; ++i) {
                            if (propButtonTextArray[i] == null || "".equals(propButtonTextArray[i])) {
                                break;
                            }
                            buttons.add(propButtonTextArray[i]);
                        }

                        op = new JOptionPane(propText, messageType, JOptionPane.DEFAULT_OPTION, null, buttons.toArray(),
                                null);

                        dialog = op.createDialog(propCaption);
                        dialog.setAlwaysOnTop(propAlwaysOnTop);
                        dialog.setModal(false);
                        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                        op.addPropertyChangeListener(new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent e) {
                                String prop = e.getPropertyName();
                                // System.out.println("PROPERTY CHANGED:" +
                                // prop);

                                if (prop.equals(JOptionPane.VALUE_PROPERTY)) {
                                    if (e.getNewValue() != null) {
                                        for (int i = 0; i < NUM_BUTTONS; ++i) {
                                            if (e.getNewValue().equals(propButtonTextArray[i])) {
                                                etpButtonPressed[i].raiseEvent();
                                                break;
                                            }
                                        }
                                    }
                                    closeDialog();
                                }
                                /*
                                 * if (dialog.isVisible() && (e.getSource() ==
                                 * op) &&
                                 * (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                 * 
                                 * }
                                 */
                            }
                        });

                        dialog.setVisible(true);

                    }
                });
            }
        }
    };
    final IRuntimeEventListenerPort elpHideBox = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            closeDialog();
        }
    };

    private void closeDialog() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (dialog != null) {
                    // dialog.setVisible(false);
                    dialog.dispose();
                    dialog = null;
                    op = null;

                }
            }
        });
    }

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
        closeDialog();
        super.stop();
    }
}