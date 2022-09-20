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

package eu.asterics.component.sensor.event_generator;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.RuntimeState;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Jan 09, 2010 Time:
 *         9:03:08 AM
 */
public class EventGeneratorInstance extends AbstractRuntimeComponentInstance implements Runnable {
    // by default generate 1 event every 1000 milliseconds (i.e. 1 sec)
    public static final int DEFAULT_GENERATION_DELAY = 1000;

    // property
    private int generationDelay = DEFAULT_GENERATION_DELAY;

    // property
    private String eventPayload = null;

    public EventGeneratorInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("generation_delay".equalsIgnoreCase(propertyName)) {
            return this.generationDelay;
        } else if ("event_payload".equalsIgnoreCase(propertyName)) {
            return this.eventPayload;
        }

        return null;
    }

    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("generation_delay".equalsIgnoreCase(propertyName)) {
            final Integer oldValue = this.generationDelay;

            this.generationDelay = Integer.parseInt(newValue.toString());

            return oldValue;
        } else if ("event_payload".equalsIgnoreCase(propertyName)) {
            final String oldValue = this.eventPayload;

            this.eventPayload = newValue.toString();

            // change the event payload immediately
            runtimeEventTriggererPort.setEventChannelID(eventPayload);

            return oldValue;
        }

        return null;
    }

    final IRuntimeEventTriggererPort runtimeEventTriggererPort = new DefaultRuntimeEventTriggererPort();

    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("event_out_1".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort;
        }

        return null;
    }

    @Override
    public void run() {
        while (runtimeState == RuntimeState.ACTIVE) {
            runtimeEventTriggererPort.raiseEvent();

            try {
                Thread.sleep(generationDelay);
            } catch (InterruptedException ie) {
            }
        }
    }

    @Override
    public void start() {
        super.start();

        AstericsThreadPool.instance.execute(this);
    }

    @Override
    public void resume() {
        super.resume();

        AstericsThreadPool.instance.execute(this);
    }
}