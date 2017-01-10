
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

package eu.asterics.component.actuator.skype;

import com.skype.Call;
import com.skype.CallMonitorListener;
import com.skype.Skype;
import com.skype.SkypeException;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 * 
 * @author <your name> [<your email address>] Date: Time:
 */
public class SkypeInstance extends AbstractRuntimeComponentInstance {
    final IRuntimeOutputPort opCallerID = new DefaultRuntimeOutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    final IRuntimeEventTriggererPort etpIncomingCall = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort etpCallFinished = new DefaultRuntimeEventTriggererPort();
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    private String propUser1 = "";
    private String propUser2 = "";
    private String propUser3 = "";
    private String propUser4 = "";
    private String propUser5 = "";
    private String skypeID = "";
    // declare member variables here
    private Call actCall = null;

    private CallMonitorListener callMonitorListener;

    /**
     * The class constructor.
     */
    public SkypeInstance() {
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
        if ("skypeID".equalsIgnoreCase(portID)) {
            return ipSkypeID;
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
        if ("callerID".equalsIgnoreCase(portID)) {
            return opCallerID;
        }

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
        if ("callUser1".equalsIgnoreCase(eventPortID)) {
            return elpCallUser1;
        }
        if ("callUser2".equalsIgnoreCase(eventPortID)) {
            return elpCallUser2;
        }
        if ("callUser3".equalsIgnoreCase(eventPortID)) {
            return elpCallUser3;
        }
        if ("callUser4".equalsIgnoreCase(eventPortID)) {
            return elpCallUser4;
        }
        if ("callUser5".equalsIgnoreCase(eventPortID)) {
            return elpCallUser5;
        }
        if ("call".equalsIgnoreCase(eventPortID)) {
            return elpCall;
        }
        if ("cancelCall".equalsIgnoreCase(eventPortID)) {
            return elpCancelCall;
        }
        if ("answerCall".equalsIgnoreCase(eventPortID)) {
            return elpAnswerCall;
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
        if ("incomingCall".equalsIgnoreCase(eventPortID)) {
            return etpIncomingCall;
        }
        if ("callFinished".equalsIgnoreCase(eventPortID)) {
            return etpCallFinished;
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
        if ("user1".equalsIgnoreCase(propertyName)) {
            return propUser1;
        }
        if ("user2".equalsIgnoreCase(propertyName)) {
            return propUser2;
        }
        if ("user3".equalsIgnoreCase(propertyName)) {
            return propUser3;
        }
        if ("user4".equalsIgnoreCase(propertyName)) {
            return propUser4;
        }
        if ("user5".equalsIgnoreCase(propertyName)) {
            return propUser5;
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
        if ("user1".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUser1;
            propUser1 = (String) newValue;
            return oldValue;
        }
        if ("user2".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUser2;
            propUser2 = (String) newValue;
            return oldValue;
        }
        if ("user3".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUser3;
            propUser3 = (String) newValue;
            return oldValue;
        }
        if ("user4".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUser4;
            propUser4 = (String) newValue;
            return oldValue;
        }
        if ("user5".equalsIgnoreCase(propertyName)) {
            final Object oldValue = propUser5;
            propUser5 = (String) newValue;
            return oldValue;
        }

        return null;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipSkypeID = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            skypeID = ConversionUtils.stringFromBytes(data);
        }
    };

    private void callUser(String user) {
        if (user == null) {
            return;
        }
        user = user.trim();
        if (user.length() > 0) {
            try {
                actCall = Skype.call(user);
            } catch (SkypeException se) {
                se.printStackTrace();
            }
        }
    }

    private void cancelCall() {
        if (actCall != null) {
            try {
                actCall.finish();
            } catch (SkypeException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpCall = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(skypeID);
        }
    };

    final IRuntimeEventListenerPort elpAnswerCall = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
        }
    };

    final IRuntimeEventListenerPort elpCancelCall = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            cancelCall();
        }
    };

    final IRuntimeEventListenerPort elpCallUser1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(propUser1);
        }
    };
    final IRuntimeEventListenerPort elpCallUser2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(propUser2);
        }
    };
    final IRuntimeEventListenerPort elpCallUser3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(propUser3);
        }
    };
    final IRuntimeEventListenerPort elpCallUser4 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(propUser4);
        }
    };
    final IRuntimeEventListenerPort elpCallUser5 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            callUser(propUser5);
        }
    };

    private void addCallListener() {
        try {
            callMonitorListener = new CallMonitorListener() {
                @Override
                public void callMonitor(Call call, Call.Status status) throws SkypeException {
                    System.out.println("Status: " + status + " Type: " + call.getType());
                    if (status == Call.Status.RINGING) {
                        if (call.getType() == Call.Type.INCOMING_P2P || call.getType() == Call.Type.INCOMING_PSTN) {
                            etpIncomingCall.raiseEvent();
                        }
                        actCall = call;
                        System.out.println("Set actCall to ringing call");
                    }
                    if (status == Call.Status.CANCELLED || status == Call.Status.FINISHED
                            || status == Call.Status.REFUSED) {
                        etpCallFinished.raiseEvent();
                    }
                }
            };
            Skype.addCallMonitorListener(callMonitorListener);
        } catch (SkypeException se) {
        }
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        addCallListener();
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
        Skype.removeCallMonitorListener(callMonitorListener);
        super.stop();
    }
}