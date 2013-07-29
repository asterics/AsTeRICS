package eu.asterics.component.sensor.event_generator;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.RuntimeState;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Jan 09, 2010
 *         Time: 9:03:08 AM
 */
public class EventGeneratorInstance extends AbstractRuntimeComponentInstance implements Runnable
{
    // by default generate 1 event every 1000 milliseconds (i.e. 1 sec)
    public static final int DEFAULT_GENERATION_DELAY = 1000;

    // property
    private int generationDelay = DEFAULT_GENERATION_DELAY;

    // property
    private String eventPayload = null;

    public EventGeneratorInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    @Override
    public Object getRuntimePropertyValue(String propertyName)
    {
        if ("generation_delay".equalsIgnoreCase(propertyName))
        {
            return this.generationDelay;
        }
        else if("event_payload".equalsIgnoreCase(propertyName))
        {
            return this.eventPayload;
        }

        return null;
    }

    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
        if ("generation_delay".equalsIgnoreCase(propertyName))
        {
            final Integer oldValue = this.generationDelay;

            this.generationDelay = Integer.parseInt(newValue.toString());

            return oldValue;
        }
        else if("event_payload".equalsIgnoreCase(propertyName))
        {
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
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {
        if("event_out_1".equalsIgnoreCase(eventPortID))
        {
            return runtimeEventTriggererPort;
        }

        return null;
    }

    @Override
    public void run()
    {
        while(runtimeState == RuntimeState.ACTIVE)
        {
            runtimeEventTriggererPort.raiseEvent();
            
            try
            {
                Thread.sleep(generationDelay);
            } catch (InterruptedException ie) {}
        }
    }

    @Override
    public void start()
    {
        super.start();

        AstericsThreadPool.instance.execute(this);
    }

    @Override
    public void resume()
    {
        super.resume();

        AstericsThreadPool.instance.execute(this);
    }
}