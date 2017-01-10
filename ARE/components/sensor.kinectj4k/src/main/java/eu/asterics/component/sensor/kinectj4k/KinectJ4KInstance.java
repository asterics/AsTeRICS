
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

package eu.asterics.component.sensor.kinectj4k;

import edu.ufl.digitalworlds.j4k.J4KSDK;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * 
 * Find skeleton with Kinect v1 (SDK for Windows) and return Joint-Points with 3
 * Dimensions (X,Y,Z)
 * 
 * 
 * 
 * @author Cornelia Salomon [cornelia.salomon@technikum-wien.at] Date:
 *         13.01.2015 Time:
 */
public class KinectJ4KInstance extends AbstractRuntimeComponentInstance {
    Kinect kinect1;

    final OutputPort opFootLeftX = new OutputPort();
    final OutputPort opFootLeftY = new OutputPort();
    final OutputPort opFootLeftZ = new OutputPort();
    final OutputPort opFootRightX = new OutputPort();
    final OutputPort opFootRightY = new OutputPort();
    final OutputPort opFootRightZ = new OutputPort();
    final OutputPort opAnkleLeftX = new OutputPort();
    final OutputPort opAnkleLeftY = new OutputPort();
    final OutputPort opAnkleLeftZ = new OutputPort();
    final OutputPort opAnkleRightX = new OutputPort();
    final OutputPort opAnkleRightY = new OutputPort();
    final OutputPort opAnkleRightZ = new OutputPort();
    final OutputPort opKneeLeftX = new OutputPort();
    final OutputPort opKneeLeftY = new OutputPort();
    final OutputPort opKneeLeftZ = new OutputPort();
    final OutputPort opKneeRightX = new OutputPort();
    final OutputPort opKneeRightY = new OutputPort();
    final OutputPort opKneeRightZ = new OutputPort();
    final OutputPort opHipLeftX = new OutputPort();
    final OutputPort opHipLeftY = new OutputPort();
    final OutputPort opHipLeftZ = new OutputPort();
    final OutputPort opHipCenterX = new OutputPort();
    final OutputPort opHipCenterY = new OutputPort();
    final OutputPort opHipCenterZ = new OutputPort();
    final OutputPort opHipRightX = new OutputPort();
    final OutputPort opHipRightY = new OutputPort();
    final OutputPort opHipRightZ = new OutputPort();
    final OutputPort opSpineX = new OutputPort();
    final OutputPort opSpineY = new OutputPort();
    final OutputPort opSpineZ = new OutputPort();
    final OutputPort opShoulderLeftX = new OutputPort();
    final OutputPort opShoulderLeftY = new OutputPort();
    final OutputPort opShoulderLeftZ = new OutputPort();
    final OutputPort opShoulderCenterX = new OutputPort();
    final OutputPort opShoulderCenterY = new OutputPort();
    final OutputPort opShoulderCenterZ = new OutputPort();
    final OutputPort opShoulderRightX = new OutputPort();
    final OutputPort opShoulderRightY = new OutputPort();
    final OutputPort opShoulderRightZ = new OutputPort();
    final OutputPort opElbowLeftX = new OutputPort();
    final OutputPort opElbowLeftY = new OutputPort();
    final OutputPort opElbowLeftZ = new OutputPort();
    final OutputPort opElbowRightX = new OutputPort();
    final OutputPort opElbowRightY = new OutputPort();
    final OutputPort opElbowRightZ = new OutputPort();
    final OutputPort opWristLeftX = new OutputPort();
    final OutputPort opWristLeftY = new OutputPort();
    final OutputPort opWristLeftZ = new OutputPort();
    final OutputPort opWristRightX = new OutputPort();
    final OutputPort opWristRightY = new OutputPort();
    final OutputPort opWristRightZ = new OutputPort();
    final OutputPort opHandLeftX = new OutputPort();
    final OutputPort opHandLeftY = new OutputPort();
    final OutputPort opHandLeftZ = new OutputPort();
    final OutputPort opHandRightX = new OutputPort();
    final OutputPort opHandRightY = new OutputPort();
    final OutputPort opHandRightZ = new OutputPort();
    final OutputPort opHeadX = new OutputPort();
    final OutputPort opHeadY = new OutputPort();
    final OutputPort opHeadZ = new OutputPort();
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));

    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    // declare member variables here

    /**
     * The class constructor.
     */
    public KinectJ4KInstance() {
        // setLoadingProgress("Intitializing Kinect...",20);
        // kinect1.start(J4KSDK.SKELETON);
        kinect1 = new Kinect(this);
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
        if ("footLeftX".equalsIgnoreCase(portID)) {
            return opFootLeftX;
        }
        if ("footLeftY".equalsIgnoreCase(portID)) {
            return opFootLeftY;
        }
        if ("footLeftZ".equalsIgnoreCase(portID)) {
            return opFootLeftZ;
        }
        if ("footRightX".equalsIgnoreCase(portID)) {
            return opFootRightX;
        }
        if ("footRightY".equalsIgnoreCase(portID)) {
            return opFootRightY;
        }
        if ("footRightZ".equalsIgnoreCase(portID)) {
            return opFootRightZ;
        }
        if ("ankleLeftX".equalsIgnoreCase(portID)) {
            return opAnkleLeftX;
        }
        if ("ankleLeftY".equalsIgnoreCase(portID)) {
            return opAnkleLeftY;
        }
        if ("ankleLeftZ".equalsIgnoreCase(portID)) {
            return opAnkleLeftZ;
        }
        if ("ankleRightX".equalsIgnoreCase(portID)) {
            return opAnkleRightX;
        }
        if ("ankleRightY".equalsIgnoreCase(portID)) {
            return opAnkleRightY;
        }
        if ("ankleRightZ".equalsIgnoreCase(portID)) {
            return opAnkleRightZ;
        }
        if ("kneeLeftX".equalsIgnoreCase(portID)) {
            return opKneeLeftX;
        }
        if ("kneeLeftY".equalsIgnoreCase(portID)) {
            return opKneeLeftY;
        }
        if ("kneeLeftZ".equalsIgnoreCase(portID)) {
            return opKneeLeftZ;
        }
        if ("kneeRightX".equalsIgnoreCase(portID)) {
            return opKneeRightX;
        }
        if ("kneeRightY".equalsIgnoreCase(portID)) {
            return opKneeRightY;
        }
        if ("kneeRightZ".equalsIgnoreCase(portID)) {
            return opKneeRightZ;
        }
        if ("hipLeftX".equalsIgnoreCase(portID)) {
            return opHipLeftX;
        }
        if ("hipLeftY".equalsIgnoreCase(portID)) {
            return opHipLeftY;
        }
        if ("hipLeftZ".equalsIgnoreCase(portID)) {
            return opHipLeftZ;
        }
        if ("hipCenterX".equalsIgnoreCase(portID)) {
            return opHipCenterX;
        }
        if ("hipCenterY".equalsIgnoreCase(portID)) {
            return opHipCenterY;
        }
        if ("hipCenterZ".equalsIgnoreCase(portID)) {
            return opHipCenterZ;
        }
        if ("hipRightX".equalsIgnoreCase(portID)) {
            return opHipRightX;
        }
        if ("hipRightY".equalsIgnoreCase(portID)) {
            return opHipRightY;
        }
        if ("hipRightZ".equalsIgnoreCase(portID)) {
            return opHipRightZ;
        }
        if ("spineX".equalsIgnoreCase(portID)) {
            return opSpineX;
        }
        if ("spineY".equalsIgnoreCase(portID)) {
            return opSpineY;
        }
        if ("spineZ".equalsIgnoreCase(portID)) {
            return opSpineZ;
        }
        if ("shoulderLeftX".equalsIgnoreCase(portID)) {
            return opShoulderLeftX;
        }
        if ("shoulderLeftY".equalsIgnoreCase(portID)) {
            return opShoulderLeftY;
        }
        if ("shoulderLeftZ".equalsIgnoreCase(portID)) {
            return opShoulderLeftZ;
        }
        if ("shoulderCenterX".equalsIgnoreCase(portID)) {
            return opShoulderCenterX;
        }
        if ("shoulderCenterY".equalsIgnoreCase(portID)) {
            return opShoulderCenterY;
        }
        if ("shoulderCenterZ".equalsIgnoreCase(portID)) {
            return opShoulderCenterZ;
        }
        if ("shoulderRightX".equalsIgnoreCase(portID)) {
            return opShoulderRightX;
        }
        if ("shoulderRightY".equalsIgnoreCase(portID)) {
            return opShoulderRightY;
        }
        if ("shoulderRightZ".equalsIgnoreCase(portID)) {
            return opShoulderRightZ;
        }
        if ("elbowLeftX".equalsIgnoreCase(portID)) {
            return opElbowLeftX;
        }
        if ("elbowLeftY".equalsIgnoreCase(portID)) {
            return opElbowLeftY;
        }
        if ("elbowLeftZ".equalsIgnoreCase(portID)) {
            return opElbowLeftZ;
        }
        if ("elbowRightX".equalsIgnoreCase(portID)) {
            return opElbowRightX;
        }
        if ("elbowRightY".equalsIgnoreCase(portID)) {
            return opElbowRightY;
        }
        if ("elbowRightZ".equalsIgnoreCase(portID)) {
            return opElbowRightZ;
        }
        if ("wristLeftX".equalsIgnoreCase(portID)) {
            return opWristLeftX;
        }
        if ("wristLeftY".equalsIgnoreCase(portID)) {
            return opWristLeftY;
        }
        if ("wristLeftZ".equalsIgnoreCase(portID)) {
            return opWristLeftZ;
        }
        if ("wristRightX".equalsIgnoreCase(portID)) {
            return opWristRightX;
        }
        if ("wristRightY".equalsIgnoreCase(portID)) {
            return opWristRightY;
        }
        if ("wristRightZ".equalsIgnoreCase(portID)) {
            return opWristRightZ;
        }
        if ("handLeftX".equalsIgnoreCase(portID)) {
            return opHandLeftX;
        }
        if ("handLeftY".equalsIgnoreCase(portID)) {
            return opHandLeftY;
        }
        if ("handLeftZ".equalsIgnoreCase(portID)) {
            return opHandLeftZ;
        }
        if ("handRightX".equalsIgnoreCase(portID)) {
            return opHandRightX;
        }
        if ("handRightY".equalsIgnoreCase(portID)) {
            return opHandRightY;
        }
        if ("handRightZ".equalsIgnoreCase(portID)) {
            return opHandRightZ;
        }
        if ("headX".equalsIgnoreCase(portID)) {
            return opHeadX;
        }
        if ("headY".equalsIgnoreCase(portID)) {
            return opHeadY;
        }
        if ("headZ".equalsIgnoreCase(portID)) {
            return opHeadZ;
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

        return null;
    }

    /**
     * Input Ports for receiving values.
     */

    /**
     * Event Listerner Ports.
     */

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        super.start();
        initKinect();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        releaseKinect();
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
        initKinect();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        releaseKinect();
        super.stop();
    }

    private void initKinect() {
        releaseKinect();
        if (!kinect1.start(J4KSDK.COLOR | J4KSDK.DEPTH | J4KSDK.UV | J4KSDK.XYZ | J4KSDK.SKELETON)) {
            AstericsErrorHandling.instance.reportError(this,
                    "<html><center><br>ERROR: The Kinect #1 device could not be initialized.<br><br>1. Check if the Microsoft's Kinect SDK was succesfully installed on this computer.<br> 2. Check if the Kinect is plugged into a power outlet.<br>3. Check if the Kinect is connected to a USB port of this computer.</center>");
        }
    }

    private void releaseKinect() {
        if (kinect1 != null) {
            kinect1.stop();
        }
    }

    public void setJointPointsFoot(double FootLeftX, double FootLeftY, double FootLeftZ, double FootRightX,
            double FootRightY, double FootRightZ) {
        this.opFootLeftX.sendData(FootLeftX);
        this.opFootLeftY.sendData(FootLeftY);
        this.opFootLeftZ.sendData(FootLeftZ);
        this.opFootRightX.sendData(FootRightX);
        this.opFootRightY.sendData(FootRightY);
        this.opFootRightZ.sendData(FootRightZ);
    }

    public void setJointPointsAnkle(double AnkleLeftX, double AnkleLeftY, double AnkleLeftZ, double AnkleRightX,
            double AnkleRightY, double AnkleRightZ) {
        this.opAnkleLeftX.sendData(AnkleLeftX);
        this.opAnkleLeftY.sendData(AnkleLeftY);
        this.opAnkleLeftZ.sendData(AnkleLeftZ);
        this.opAnkleRightX.sendData(AnkleRightX);
        this.opAnkleRightY.sendData(AnkleRightY);
        this.opAnkleRightZ.sendData(AnkleRightZ);
    }

    public void setJointPointsKnee(double KneeLeftX, double KneeLeftY, double KneeLeftZ, double KneeRightX,
            double KneeRightY, double KneeRightZ) {
        this.opKneeLeftX.sendData(KneeLeftX);
        this.opKneeLeftY.sendData(KneeLeftY);
        this.opKneeLeftZ.sendData(KneeLeftZ);
        this.opKneeRightX.sendData(KneeRightX);
        this.opKneeRightY.sendData(KneeRightY);
        this.opKneeRightZ.sendData(KneeRightZ);
    }

    public void setJointPointHip(double HipLeftX, double HipLeftY, double HipLeftZ, double HipCenterX,
            double HipCenterY, double HipCenterZ, double HipRightX, double HipRightY, double HipRightZ) {
        this.opHipLeftX.sendData(HipLeftX);
        this.opHipLeftY.sendData(HipLeftY);
        this.opHipLeftZ.sendData(HipLeftZ);
        this.opHipCenterX.sendData(HipCenterX);
        this.opHipCenterY.sendData(HipCenterY);
        this.opHipCenterZ.sendData(HipCenterZ);
        this.opHipLeftX.sendData(HipRightX);
        this.opHipLeftY.sendData(HipRightY);
        this.opHipLeftZ.sendData(HipRightZ);
    }

    public void setJointPointSpine(double SpineX, double SpineY, double SpineZ) {
        this.opSpineX.sendData(SpineX);
        this.opSpineY.sendData(SpineY);
        this.opSpineZ.sendData(SpineZ);
    }

    public void setJointPointShoulder(double ShoulderLeftX, double ShoulderLeftY, double ShoulderLeftZ,
            double ShoulderCenterX, double ShoulderCenterY, double ShoulderCenterZ, double ShoulderRightX,
            double ShoulderRightY, double ShoulderRightZ) {
        this.opShoulderLeftX.sendData(ShoulderLeftX);
        this.opShoulderLeftY.sendData(ShoulderLeftY);
        this.opShoulderLeftZ.sendData(ShoulderLeftZ);
        this.opShoulderCenterX.sendData(ShoulderCenterX);
        this.opShoulderCenterY.sendData(ShoulderCenterY);
        this.opShoulderCenterZ.sendData(ShoulderCenterZ);
        this.opShoulderRightX.sendData(ShoulderRightX);
        this.opShoulderRightY.sendData(ShoulderRightY);
        this.opShoulderRightZ.sendData(ShoulderRightZ);
    }

    public void setJointPointElbow(double ElbowLeftX, double ElbowLeftY, double ElbowLeftZ, double ElbowRightX,
            double ElbowRightY, double ElbowRightZ) {
        this.opElbowLeftX.sendData(ElbowLeftX);
        this.opElbowLeftY.sendData(ElbowLeftY);
        this.opElbowLeftZ.sendData(ElbowLeftZ);
        this.opElbowRightX.sendData(ElbowRightX);
        this.opElbowRightY.sendData(ElbowRightY);
        this.opElbowRightZ.sendData(ElbowRightZ);
    }

    public void setJointPointWrist(double WristLeftX, double WristLeftY, double WristLeftZ, double WristRightX,
            double WristRightY, double WristRightZ) {
        this.opWristLeftX.sendData(WristLeftX);
        this.opWristLeftY.sendData(WristLeftY);
        this.opWristLeftZ.sendData(WristLeftZ);
        this.opWristRightX.sendData(WristRightX);
        this.opWristRightY.sendData(WristRightY);
        this.opWristRightZ.sendData(WristRightZ);
    }

    public void setJointPointHand(double HandLeftX, double HandLeftY, double HandLeftZ, double HandRightX,
            double HandRightY, double HandRightZ) {
        this.opHandLeftX.sendData(HandLeftX);
        this.opHandLeftY.sendData(HandLeftY);
        this.opHandLeftZ.sendData(HandLeftZ);
        this.opHandRightX.sendData(HandRightX);
        this.opHandRightY.sendData(HandRightY);
        this.opHandRightZ.sendData(HandRightZ);
    }

    public void setJointPointHead(double HeadX, double HeadY, double HeadZ) {
        // System.out.println("x="+HeadX+", y="+HeadY+", z="+HeadZ);
        this.opHeadX.sendData(HeadX);
        this.opHeadY.sendData(HeadY);
        this.opHeadZ.sendData(HeadZ);
    }

    /**
     * implementation of the default output port for sending double
     */
    public class OutputPort extends DefaultRuntimeOutputPort {
        public void sendData(double data) {
            super.sendData(ConversionUtils.doubleToBytes(data));
        }
    }
}