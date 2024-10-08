
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

package eu.asterics.component.processor.adjustmentcurve;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Implements the AdjustmentCurve processor plugin, which transforms one signal
 * curve into another one. The GUI of this element allows to draw the mapping
 * from input to output signal
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 28, 2013 Time:
 *         10:55:05 AM
 */

class CurvePoint implements java.io.Serializable {
    double x;
    double y;
}

public class AdjustmentCurveInstance extends AbstractRuntimeComponentInstance {
    final AdjustmentCurveInstance thisInstance;

    final int MODE_CLIPMINMAX = 0;
    final int MODE_AUTOMINMAX = 1;

    static final int OPMODE_FROM_FILE = 0;
    static final int OPMODE_FROM_PROP_PERCENT = 1;
    static final int OPMODE_FROM_PROP_ABSOLUTE = 2;

    final int MAX_REDRAWSPEED = 50;

    private double input = 500;
    private double output = 500;
    private boolean guiVisible = false;

    public Vector<CurvePoint> curvePoints = new Vector<CurvePoint>();

    public boolean propDisplayGui = true;
    public double propInMin = 0;
    public double propInMax = 1000;
    public double propOutMin = 0;
    public double propOutMax = 1000;
    public int propMode = 1;
    public int propFontSize = 14;
    public String propCaption = "dotMeter";
    public String propFilename = "curve1";
    public int propOperationMode = 0;
    String propCurvePoints;

    long timestamp = 0;

    public final IRuntimeOutputPort opOut = new DefaultRuntimeOutputPort();

    private GUI gui = null;

    /**
     * The class constructor. initializes the GUI
     */
    public AdjustmentCurveInstance() {
        thisInstance = this;

        curvePoints.clear();
        /*
         * CurvePoint e= new CurvePoint(); e.x=0; e.y=0; curvePoints.add(e); e=
         * new CurvePoint(); e.x=1000; e.y=1000; curvePoints.add(e);
         */
    }

    public synchronized void load() {
        FileInputStream fIn = null;
        ObjectInputStream oIn = null;

        try {
            fIn = new FileInputStream("data/processor.adjustmentcurve/" + propFilename + ".ser");
            oIn = new ObjectInputStream(fIn);

            Vector<CurvePoint> tmp = (Vector<CurvePoint>) oIn.readObject();
            curvePoints = tmp;

            oIn.close();
            fIn.close();
            if (gui != null) {
                gui.updateMinMax();
            }
        } catch (IOException e) {

            AstericsErrorHandling.instance.getLogger().fine(
                    "Could not load adjustmentCurve file data/processor.adjustmentcurve/" + propFilename + ".ser");
                    // AstericsErrorHandling.instance.getLogger().fine("Using
                    // default values ...");

            // e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void loadFromProperty() {
        curvePoints.clear();
        try {
            if (propCurvePoints != null && !"".equals(propCurvePoints)) {
                propCurvePoints = propCurvePoints.replace(" ", "");
                String[] stringPoints = propCurvePoints.split("\\),");
                for (String stringPoint : stringPoints) {
                    stringPoint = stringPoint.replace(")", "").replace("(", "");
                    String[] stringValues = stringPoint.split(",");
                    CurvePoint c = new CurvePoint();
                    c.x = Double.parseDouble(stringValues[0]);
                    c.y = Double.parseDouble(stringValues[1]);
                    curvePoints.add(c);
                }
            }
        } catch (Exception e) {
            AstericsErrorHandling.instance.reportError(this,
                    "AdjustmentCurve: Could not parse 'propCurvePoints'. Make sure the format is like '(1,2),(2,3),(4,5)'.");
        }
    }

    public synchronized void save() {
        FileOutputStream fOut = null;
        ObjectOutputStream oOut = null;

        try {
            fOut = new FileOutputStream("data/processor.adjustmentcurve/" + propFilename + ".ser");
            oOut = new ObjectOutputStream(fOut);
            oOut.writeObject(curvePoints);
        } catch (IOException e) {
            AstericsErrorHandling.instance.getLogger().fine(
                    "Could not save adjustmentCurve file data/processor.adjustmentcurve/" + propFilename + ".ser");
            // e.printStackTrace();
        } finally {
            try {
                oOut.flush();
                oOut.close();
                fOut.close();
            } catch (IOException e1) {
                AstericsErrorHandling.instance.getLogger().fine(
                        "Could not save adjustmentCurve file data/processor.adjustmentcurve/" + propFilename + ".ser");
                // e1.printStackTrace();
            }
        }
    }

    public double calculateOutputValue(double x) {
        CurvePoint a, b;

        if (curvePoints.size() > 1) {
            if (x <= getX(0)) {
                return (getY(0));
            }
            if (x >= getX(curvePoints.size() - 1)) {
                return (getY(curvePoints.size() - 1));
            }
            int i = 0;
            while ((i < curvePoints.size() - 1) && (x > getX(i))) {
                i++;
            }

            double ax = getX(i - 1);
            double ay = getY(i - 1);
            double bx = getX(i);
            double by = getY(i);
            // System.out.println("x="+x+", a.x="+a.x+",b.x="+b.x);

            double f = (x - ax) / (bx - ax);
            return (ay + (by - ay) * f);
        } else {
            return (0);
        }
    }

    private double getX(int index) {
        double value = curvePoints.get(index).x;
        if(propOperationMode == OPMODE_FROM_PROP_PERCENT) {
            value = value > 100 ? 100 : value;
            value = value < 0 ? 0 : value;
            double range = propInMax - propInMin;
            value = (value * range / 100) + propInMin;
        }
        return value;
    }

    private double getY(int index) {
        double value = curvePoints.get(index).y;
        if(propOperationMode == OPMODE_FROM_PROP_PERCENT) {
            value = value > 100 ? 100 : value;
            value = value < 0 ? 0 : value;
            double range = propOutMax - propOutMin;
            value = (value * range / 100) + propOutMin;
        }
        return value;
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
        if ("in".equalsIgnoreCase(portID)) {
            return ipIn;
        }
        if ("curveName".equalsIgnoreCase(portID)) {
            return ipCurveName;
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
        if ("out".equalsIgnoreCase(portID)) {
            return opOut;
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
        if ("filename".equalsIgnoreCase(propertyName)) {
            return propFilename;
        }
        if ("displayGui".equalsIgnoreCase(propertyName)) {
            return propDisplayGui;
        }
        if ("inMin".equalsIgnoreCase(propertyName)) {
            return propInMin;
        }
        if ("inMax".equalsIgnoreCase(propertyName)) {
            return propInMax;
        }
        if ("outMin".equalsIgnoreCase(propertyName)) {
            return propOutMin;
        }
        if ("outMax".equalsIgnoreCase(propertyName)) {
            return propOutMax;
        }
        if ("mode".equalsIgnoreCase(propertyName)) {
            return propMode;
        }
        if ("operationMode".equalsIgnoreCase(propertyName)) {
            return propOperationMode;
        }
        if ("fontSize".equalsIgnoreCase(propertyName)) {
            return propFontSize;
        }
        if ("caption".equalsIgnoreCase(propertyName)) {
            return propCaption;
        }
        if ("curvePoints".equalsIgnoreCase(propertyName)) {
            return propCurvePoints;
        }
        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param portID
     *            the name of the port
     * @return the event listener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if ("displayGui".equalsIgnoreCase(eventPortID)) {
            return elpDisplayGui;
        } else if ("hideGui".equalsIgnoreCase(eventPortID)) {
            return elpHideGui;
        } else if ("loadCurve".equalsIgnoreCase(eventPortID)) {
            return elpLoadCurve;
        } else if ("saveCurve".equalsIgnoreCase(eventPortID)) {
            return elpSaveCurve;
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
        if ("filename".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFilename;
            propFilename = newValue.toString();
            load();
            return oldValue;
        }
        if ("displayGui".equalsIgnoreCase(propertyName)) {
            final boolean oldValue = propDisplayGui;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propDisplayGui = true;
                if (oldValue == false) {
                    showGui();
                }
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propDisplayGui = false;
                if (oldValue == true) {
                    hideGui();
                }
            }
            return oldValue;
        }
        if ("inMin".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInMin;
            propInMin = Double.parseDouble(newValue.toString());
            if (gui != null) {
                gui.updateMinMax();
            }
            return oldValue;
        }
        if ("inMax".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propInMax;
            propInMax = Double.parseDouble(newValue.toString());
            if (gui != null) {
                gui.updateMinMax();
            }
            return oldValue;
        }
        if ("outMin".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOutMin;
            propOutMin = Double.parseDouble(newValue.toString());
            if (gui != null) {
                gui.updateMinMax();
            }
            return oldValue;
        }
        if ("outMax".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propOutMax;
            propOutMax = Double.parseDouble(newValue.toString());
            if (gui != null) {
                gui.updateMinMax();
            }
            return oldValue;
        }
        if ("mode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMode;
            propMode = Integer.parseInt(newValue.toString());
            if (gui != null) {
                gui.updateMinMax();
            }
            return oldValue;
        }
        if ("operationMode".equalsIgnoreCase(propertyName)) {
            final int oldValue = propOperationMode;
            propOperationMode = Integer.parseInt(newValue.toString());
            if (oldValue != propOperationMode) {
                switch (propOperationMode) {
                case OPMODE_FROM_FILE:
                    load();
                    if (propDisplayGui) {
                        showGui();
                    }
                    break;
                case OPMODE_FROM_PROP_PERCENT:
                    loadFromProperty();
                    hideGui();
                    break;
                case OPMODE_FROM_PROP_ABSOLUTE:
                    loadFromProperty();
                    hideGui();
                    break;
                }
            }
            return oldValue;
        }
        if ("fontSize".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propFontSize;
            propFontSize = Integer.parseInt(newValue.toString());
            return oldValue;
        }
        if ("caption".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCaption;
            propCaption = newValue.toString();
            return oldValue;
        }
        if ("curvePoints".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propCurvePoints;
            propCurvePoints = newValue.toString();
            if(propOperationMode == OPMODE_FROM_PROP_ABSOLUTE || propOperationMode == OPMODE_FROM_PROP_PERCENT) {
                loadFromProperty();
            }
            return oldValue;
        }
        return null;
    }

    /**
     * Input Port for receiving values.
     */
    private final IRuntimeInputPort ipIn = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            input = ConversionUtils.doubleFromBytes(data);
            output = calculateOutputValue(input);
            if ((propDisplayGui == true) && guiVisible) {
                if (System.currentTimeMillis() - timestamp > MAX_REDRAWSPEED) {
                    gui.updateGraph(input, output);
                    timestamp = System.currentTimeMillis();
                }
            }
            opOut.sendData(ConversionUtils.doubleToBytes(output));
        }

    };

    /**
     * Input Port for receiving curve file names.
     */
    private final IRuntimeInputPort ipCurveName = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            propFilename = ConversionUtils.stringFromBytes(data);
            load();
        }

    };

    /**
     * Event Listener Ports.
     */
    final IRuntimeEventListenerPort elpDisplayGui = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            if (propDisplayGui == false) {
                propDisplayGui = true;
                showGui();
            }
        }
    };
    final IRuntimeEventListenerPort elpHideGui = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            propDisplayGui = false;
            hideGui();
            // gui.updateGraph(input,output);
        }
    };
    final IRuntimeEventListenerPort elpLoadCurve = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // System.out.println("Load Curve");
            // if (guiVisible)
            load();
        }
    };
    final IRuntimeEventListenerPort elpSaveCurve = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            // System.out.println("Save Curve");
            save();
        }
    };

    /**
     * called when model is started.
     */
    @Override
    public void start() {

        input = (propInMin + propInMax) / 2;
        output = calculateOutputValue(input);

        if (propDisplayGui == true) {
            showGui();
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
        hideGui();
        super.stop();
    }
    
    private void showGui() {
        if(propOperationMode != OPMODE_FROM_FILE || guiVisible) {
            return;
        }
        gui = new GUI(this, AREServices.instance.getAvailableSpace(this));
        AREServices.instance.displayPanel(gui, this, true);
        gui.updateGraph(input, output);
        guiVisible = true;
    }
    
    private void hideGui() {
        if (gui != null) {
            AREServices.instance.displayPanel(gui, this, false);
            guiVisible = false;
        }
    }
}