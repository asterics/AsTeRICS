
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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.sampler;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * 
 * Implements the timer for the sampler component.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 10, 2012 Time: 11:54:11
 *         AM
 */

public class SamplingTimer implements Runnable {
    private volatile boolean active = false;
    private long sampingTime = -1;
    private double value = 0;

    private final IRuntimeOutputPort opOutput;

    /**
     * The class constructor.
     * 
     * @param owner
     *            the SamplerInstance
     * @param opOutput
     *            the component output port
     */
    public SamplingTimer(SamplerInstance owner, IRuntimeOutputPort opOutput) {
        this.opOutput = opOutput;
    }

    /**
     * Sets the sampling time.
     * 
     * @param samplingTime
     *            the sampling time
     */
    public void setSamplingTime(long samplingTime) {
        this.sampingTime = samplingTime;
    }

    /**
     * Starts the sampling
     */
    public void startSampling() {
        if (active == false) {
            active = true;
            AstericsThreadPool.instance.execute(this);
        }
    }

    /**
     * Stops the sampling
     */
    public void stopSampling() {
        active = false;
    }

    /**
     * Sets the sample value.
     * 
     * @param value
     *            the sample value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * The timer function.
     */
    @Override
    public void run() {
        while (active) {
            opOutput.sendData(ConversionUtils.doubleToBytes(value));

            try {
                Thread.sleep(sampingTime);
            } catch (InterruptedException e) {

            }
        }
    }
}