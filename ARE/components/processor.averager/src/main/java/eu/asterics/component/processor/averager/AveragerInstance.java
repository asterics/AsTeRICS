
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

package eu.asterics.component.processor.averager;

import java.util.LinkedList;

import org.apache.commons.math3.stat.StatUtils;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * Implements the Averager plugin, which outputs the average value of the most
 * recent n samples of an input signal
 * 
 * 
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 20, 2010 Time:
 *         10:22:08 AM
 */
public class AveragerInstance extends AbstractRuntimeComponentInstance {

    public static final int DEFAULT_BUFFER_SIZE = 10;
    public static final int MODE_AVERAGE = 0;
    public static final int MODE_AVERAGE_ROUND = 1;
    public static final int MODE_ACCUMULATE = 2;
    public static final int MODE_MEDIAN = 3;

    private IRuntimeInputPort ipInput = new InputPort1();
    private IRuntimeOutputPort opOutput = new OutputPort1();

    private int propBufferSize = DEFAULT_BUFFER_SIZE;
    private int propMode = 0;

    private final LinkedList<Double> buffer = new LinkedList<Double>();
    private long lastUpdate = 0;
    private double accu = 0;

    private double sum = 0;

    /**
     * The class constructor.
     */
    public AveragerInstance() {
        // empty constructor - needed for OSGi service factory operations
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
        if ("input".equalsIgnoreCase(portID)) {
            return ipInput;
        } else {
            return null;
        }
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
        if ("output".equalsIgnoreCase(portID)) {
            return opOutput;
        } else {
            return null;
        }
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
        if ("bufferSize".equalsIgnoreCase(propertyName)) {
            return propBufferSize;
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            return propMode;
        } else {
            return null;
        }
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
    public synchronized Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("bufferSize".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propBufferSize;

            if (newValue != null) {
                try {
                    propBufferSize = Integer.parseInt(newValue.toString());
                    buffer.clear();
                    sum = 0;

                    // truncate unnecessary tail elements
                    // while(propBufferSize < buffer.size())
                    // {
                    // buffer.removeLast();
                    // }
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("Invalid property value for " + propertyName + ": " + newValue);
                }
            }

            return oldValue;
        } else if ("mode".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propMode;

            if (newValue != null) {
                try {
                    propMode = Integer.parseInt(newValue.toString());
                    buffer.clear();
                    sum = 0;
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("Invalid property value for " + propertyName + ": " + newValue);
                }
            }
            return oldValue;
        } else {
            return null;
        }
    }

    /**
     * processes the averager function according to selected mode MODE_AVERAGE:
     * the samples are summed and divided by buffer the size MODE_AVERAGE_ROUND:
     * same as AVERAGE, but result is rounded to integer MODE_ACCUMULATE: the
     * samples are summed but not divided
     */
    private synchronized void process(final double in) {
        if (propMode == MODE_AVERAGE) {
            buffer.addFirst(in);
            sum += in;
            if (buffer.size() > propBufferSize) {
                sum -= buffer.removeLast();
            }
            // System.out.println(Arrays.toString(buffer.toArray()));
            opOutput.sendData(ConversionUtils.doubleToBytes(sum / buffer.size()));
        }

        else if (propMode == MODE_AVERAGE_ROUND) {
            buffer.addFirst(in);
            sum += in;
            if (buffer.size() > propBufferSize) {
                sum -= buffer.removeLast();
            }
            opOutput.sendData(ConversionUtils.doubleToBytes(Math.round(sum / buffer.size())));
        } else if (propMode == MODE_ACCUMULATE) {
            accu += in;

            if (System.currentTimeMillis() - lastUpdate > propBufferSize) {
                lastUpdate = System.currentTimeMillis();
                opOutput.sendData(ConversionUtils.doubleToBytes(accu));
                accu = 0;
            }
        } else if (propMode == MODE_MEDIAN) {
            buffer.addFirst(in);
            if (buffer.size() > propBufferSize) {
                buffer.removeLast();
            }
            // System.out.println(Arrays.toString(buffer.toArray()));
            double[] array = new double[buffer.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = buffer.get(i);
            }
            opOutput.sendData(ConversionUtils.doubleToBytes(StatUtils.percentile(array, 50)));
        }
    }

    /**
     * Input Port for receiving samples.
     */
    private class InputPort1 extends DefaultRuntimeInputPort {
        @Override
        public void receiveData(byte[] data) {
            process(ConversionUtils.doubleFromBytes(data));
        }

    }

    /**
     * Output Port for sending result.
     */
    private class OutputPort1 extends DefaultRuntimeOutputPort {
        // empty
    }

    /**
     * called when model is started.
     */
    @Override
    public synchronized void start() {
        lastUpdate = System.currentTimeMillis();

        buffer.clear();
        accu = 0;
        sum = 0;
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public synchronized void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public synchronized void resume() {
        lastUpdate = System.currentTimeMillis();
        accu = 0;
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public synchronized void stop() {
        super.stop();
        buffer.clear();
    }

}