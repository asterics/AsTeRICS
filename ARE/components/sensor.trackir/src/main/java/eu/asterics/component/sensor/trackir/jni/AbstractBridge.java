package eu.asterics.component.sensor.trackir.jni;

import eu.asterics.component.sensor.trackir.TrackIRInstance;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsErrorHandling;

public abstract class AbstractBridge {

    protected TrackIRInstance.OutputPort opYaw;
    protected TrackIRInstance.OutputPort opPitch;
    protected TrackIRInstance.OutputPort opRoll;
    protected TrackIRInstance.OutputPort opX;
    protected TrackIRInstance.OutputPort opY;
    protected TrackIRInstance.OutputPort opZ;

    public AbstractBridge() {
        super();
    }
    
    /**
     * Activates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    abstract public int activate();

    /**
     * Deactivates the underlying native code/hardware.
     *
     * @return 0 if everything was OK, a negative number corresponding to an
     *         error code otherwise
     */
    abstract public int deactivate();

    /**
     * request an update of tracking data
     *
     * @return 0 if everything was OK, error code otherwise
     */
    abstract public int getUpdate();

    /**
     * request centering coordinates
     *
     * @return 0 if everything was OK, error code otherwise
     */
    native public int centerCoordinates();
	
	
    /**
     * This method is called back from the native code on demand. The passed
     * arguments correspond to the x and y movement of the mouse
     *
     * @param yaw_value
     *            the yaw angle (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     * @param pitch_value
     *            the pitch angle (range is [Int.MIN_VALUE, Int.MAX_VALUE])
     */
    protected void newCoordinates_callback(final int yaw_value, final int pitch_value, final int roll_value, 
											final int x_value, final int y_value, final int z_value) {
        opYaw.sendData(ConversionUtils.doubleToBytes((double)yaw_value));
        opPitch.sendData(ConversionUtils.doubleToBytes((double)pitch_value));
        opRoll.sendData(ConversionUtils.doubleToBytes((double)roll_value));
        opX.sendData(ConversionUtils.doubleToBytes((double)x_value));
        opY.sendData(ConversionUtils.doubleToBytes((double)y_value));
        opZ.sendData(ConversionUtils.doubleToBytes((double)z_value));
    }

}