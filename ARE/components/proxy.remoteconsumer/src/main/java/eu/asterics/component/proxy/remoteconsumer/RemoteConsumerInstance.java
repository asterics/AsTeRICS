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

package eu.asterics.component.proxy.remoteconsumer;

import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.IRemoteConnectionListener;
import eu.asterics.mw.services.RemoteConnectionManager;

/**
 * @author Christoph Weiss [weissch@technikum-wien.at Date: March 2, 2011 Time:
 *         10:22:08 AM
 */
public class RemoteConsumerInstance extends AbstractRuntimeComponentInstance {

    public enum OskaCommand {
        TITLE("Title", 1), PLAY_WAVE("Play", 1);

        String command;
        int nbParam;

        OskaCommand(String cmd, int nb) {
            command = cmd;
            nbParam = nb;
        }
    }

    class ConnectionListener implements IRemoteConnectionListener {
        @Override
        public void connectionEstablished() {
            sendToOska("Title:" + title);
        }

        @Override
        public void dataReceived(byte[] data) {
        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionLost() {
            // TODO Auto-generated method stub

        }
    }

    String port = "4546";
    String title = "OSKA-ARE Sample Communication";

    public RemoteConsumerInstance() {
        // empty constructor - needed for OSGi service factory operations
    }

    @Override
    public void start() {
        RemoteConnectionManager.instance.requestConnection(port, new ConnectionListener());
        RemoteConsumerGUI gui = new RemoteConsumerGUI(this);
        gui.setVisible(true);
        super.start();

    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("wavefile".equalsIgnoreCase(portID)) {
            return inputPortWavefile;
        }

        else if ("speak".equalsIgnoreCase(portID)) {
            return inputPortSpeak;
        }

        return null;
    }

    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        return null;
    }

    @Override
    public Object getRuntimePropertyValue(String propertyName) {

        if ("port".equalsIgnoreCase(propertyName)) {
            return port;
        } else if ("title".equalsIgnoreCase(propertyName)) {
            return title;
        }
        return null;
    }

    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        if ("port".equalsIgnoreCase(propertyName)) {
            port = (String) newValue;
        } else if ("title".equalsIgnoreCase(propertyName)) {
            ;
        }
        {
            title = (String) newValue;
        }
        return null;
    }

    private boolean sendToOska(String message) {
        RemoteConnectionManager.instance.writeData(port, message.getBytes());
        return false;
    }

    InputPortOskaCommand inputPortWavefile = new InputPortOskaCommand("Play");
    InputPortOskaCommand inputPortSpeak = new InputPortOskaCommand("Speak");

    private class InputPortOskaCommand extends DefaultRuntimeInputPort {
        String cmd;

        public InputPortOskaCommand(String cmd) {
            this.cmd = cmd;
        }

        @Override
        public void receiveData(byte[] data) {
            StringBuffer buf = new StringBuffer();
            buf.append("\"");
            buf.append(cmd);
            buf.append(":");
            buf.append(new String(data));
            buf.append("\"");

            sendToOska(buf.toString());
        }

    }

    private class OutputPort1 extends DefaultRuntimeOutputPort {
        // empty
    }

    public void closeConnection() {
        RemoteConnectionManager.instance.closeConnection("4546");
    }
}