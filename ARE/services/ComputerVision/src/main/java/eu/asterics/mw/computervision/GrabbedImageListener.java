package eu.asterics.mw.computervision;

import org.bytedeco.javacpp.opencv_core.IplImage;

/**
 * Defines callback method(s) for a registered image listener. Is called in case of a grabbed image. 
 * @author mad
 *
 */
public interface GrabbedImageListener {
	/**
	 * Is called in case an image was grabbed.
	 * @param image
	 */
	public void imageGrabbed(IplImage image);
}
