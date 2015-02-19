package eu.asterics.mw.computervision;

import java.util.*;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Contains code to simplify and unify opening/closing and drawing on a canvas/frame. It's planned to also support a shared usage of the canvas by several plugins. 
 * @author mad
 *
 */
public class SharedCanvasFrame {
	public static SharedCanvasFrame instance=new SharedCanvasFrame();
	
	private Map<String, CanvasFrame> key2canvasFrame=new HashMap<String, CanvasFrame>();

	public CanvasFrame createCanvasFrame(String canvasKey, String title, double gammaOfGrabber) {
		if(key2canvasFrame.containsKey(canvasKey)) {
			AstericsErrorHandling.instance.getLogger().fine("Removing existing CanvasFrame with key <"+canvasKey+">");
			CanvasFrame frame=key2canvasFrame.get(canvasKey);
			frame.dispose();
			key2canvasFrame.remove(canvasKey);
		}
 		
        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame(title, CanvasFrame.getDefaultGamma()/gammaOfGrabber);
        
        key2canvasFrame.put(canvasKey,frame);
        return frame;		
	}
	public CanvasFrame getCanvasFrame(String canvasKey) {
		return key2canvasFrame.get(canvasKey);
	}
	
	public void showImage(String canvasKey, IplImage image) {
		getCanvasFrame(canvasKey).showImage(image);
	}
	
	public void disposeFrame(String canvasKey) {
		getCanvasFrame(canvasKey).dispose();
	}
}
