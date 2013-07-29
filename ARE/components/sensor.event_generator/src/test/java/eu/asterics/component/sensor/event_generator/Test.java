package eu.asterics.component.sensor.event_generator;

import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import junit.framework.TestCase;

/**
 * Date: 1/10/11
 * Time: 10:11 AM
 */
public class Test extends TestCase
{
    public void test()
    {
        final EventGeneratorInstance eventGeneratorInstance = new EventGeneratorInstance();
        eventGeneratorInstance.runtimeEventTriggererPort.addEventListener("me", "here",
                new IRuntimeEventListenerPort()
                {
                    @Override
                    public void receiveEvent(String data)
                    {
                        System.out.println("Event received - \"" + data + "\"");
                    }
                });

        eventGeneratorInstance.start();
        try {
            Thread.currentThread();
            Thread.sleep(2000);
        }
        catch(InterruptedException ie)
        {
            // empty
        }

        eventGeneratorInstance.setRuntimePropertyValue("generation_delay", 500);
        try {
            Thread.currentThread();
            Thread.sleep(2000);
        }
        catch(InterruptedException ie)
        {
            // empty
        }

        eventGeneratorInstance.setRuntimePropertyValue("event_payload", "my payload!");
        try {
            Thread.currentThread();
            Thread.sleep(1000);
        }
        catch(InterruptedException ie)
        {
            // empty
        }

        eventGeneratorInstance.pause();
        try {
            Thread.currentThread();
            Thread.sleep(3000);
        }
        catch(InterruptedException ie)
        {
            // empty
        }

        eventGeneratorInstance.resume();
        try {
            Thread.currentThread();
            Thread.sleep(2000);
        }
        catch(InterruptedException ie)
        {
            // empty
        }
    }
}