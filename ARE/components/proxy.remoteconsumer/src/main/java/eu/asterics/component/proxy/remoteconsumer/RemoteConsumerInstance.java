package eu.asterics.component.proxy.remoteconsumer;

import java.util.logging.Logger;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;

import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.IRemoteConnectionListener;
import eu.asterics.mw.services.RemoteConnectionManager;





/**
 * @author Christoph Weiss [weissch@technikum-wien.at
 *         Date: March 2, 2011
 *         Time: 10:22:08 AM
 */
public class RemoteConsumerInstance extends AbstractRuntimeComponentInstance
{

	public enum OskaCommand {
		TITLE("Title", 1),
		PLAY_WAVE("Play", 1);
		
		String command;
		int nbParam;
		
		OskaCommand(String cmd, int nb)
		{
			command = cmd;
			nbParam = nb;
		}
	}
	
	class ConnectionListener implements IRemoteConnectionListener 
	{
		public void connectionEstablished()
		{
			sendToOska("Title:" + title);
		}
		
		public void dataReceived(byte [] data)
		{
		}
		
		public void connectionClosed()
		{
			
		}

		@Override
		public void connectionLost() {
			// TODO Auto-generated method stub
			
		}
	}
	
	String port = "4546";
	String title = "OSKA-ARE Sample Communication";
	
    public RemoteConsumerInstance()
    {
        // empty constructor - needed for OSGi service factory operations
    }

    public void start()
    {
    	RemoteConnectionManager.instance.requestConnection(port,  
    			new ConnectionListener());
    	RemoteConsumerGUI gui=new RemoteConsumerGUI(this);
    	gui.setVisible(true);
    	super.start();
        
    }

    public void pause()
    {
        super.pause();
    }

    public void resume()
    {
        super.resume();
    }

    public void stop()
    {
        super.stop();
    }

    public IRuntimeInputPort getInputPort(String portID)
    {
    	if("wavefile".equalsIgnoreCase(portID))
        {
            return inputPortWavefile;
        }

    	else if("speak".equalsIgnoreCase(portID))
        {
            return inputPortSpeak;
        }
        
    	return null;
    }

    public IRuntimeOutputPort getOutputPort(String portID)
    {
    	return null;
    }

    public Object getRuntimePropertyValue(String propertyName)
    {
    	
    	if ("port".equalsIgnoreCase(propertyName))
    	{
    		return port;
    	}
    	else if ("title".equalsIgnoreCase(propertyName))
    	{
    		return title;
    	}
        return null;
    }

    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
    	if ("port".equalsIgnoreCase(propertyName))
    	{
    		port = (String) newValue;
    	}
    	else if ("title".equalsIgnoreCase(propertyName));
    	{
    		title = (String) newValue;
    	}
        return null;
    }

	
	private boolean sendToOska(String message)
	{
		RemoteConnectionManager.instance.writeData(port, message.getBytes());		
		return false;
	}
	
	
	
	InputPortOskaCommand inputPortWavefile = new InputPortOskaCommand("Play");
	InputPortOskaCommand inputPortSpeak = new InputPortOskaCommand("Speak");
    
    private class InputPortOskaCommand extends DefaultRuntimeInputPort
    {
    	String cmd;
    	
    	public InputPortOskaCommand(String cmd)
    	{
    		this.cmd = cmd;
    	}
    	
        public void receiveData(byte[] data)
        {
        	StringBuffer buf = new StringBuffer();
        	buf.append("\"");
        	buf.append(cmd);
        	buf.append(":");
        	buf.append(new String(data));
        	buf.append("\"");
        	
        	sendToOska(buf.toString());
        }

		
    }

    private class OutputPort1 extends DefaultRuntimeOutputPort
    {
        // empty
    }
    
    public void closeConnection()
    {
    	RemoteConnectionManager.instance.closeConnection("4546");
    }
}