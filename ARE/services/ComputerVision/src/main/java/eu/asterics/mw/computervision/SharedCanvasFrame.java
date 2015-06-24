package eu.asterics.mw.computervision;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

import javax.swing.SwingUtilities;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Contains code to simplify and unify opening/closing and drawing on a
 * canvas/frame. It's planned to also support a shared usage of the canvas by
 * several plugins.
 * 
 * @author mad
 * 
 */
public class SharedCanvasFrame {
	public static SharedCanvasFrame instance = new SharedCanvasFrame();

	private Map<String, CanvasFrame> key2canvasFrame = new HashMap<String, CanvasFrame>();

	public void createCanvasFrame(final String canvasKey, final String title,
			final double gammaOfGrabber, final Point pos, final Dimension d) {
		// must be invoked non-blocking because obviously the ctor of
		// CanvasFrame performs a SwingUtilities.invokeAndWait and
		// if we are called by an ARE-GUI action this would result in a
		// dead lock
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// CanvasFrame is a JFrame containing a Canvas
				// component, which is hardware accelerated.
				// It can also switch into full-screen mode when called
				// with a screenNumber.
				// We should also specify the relative monitor/camera
				// response for proper gamma correction.

				synchronized (key2canvasFrame) {
					final CanvasFrame oldFrame = key2canvasFrame.get(canvasKey);
					if (oldFrame != null) {
						AstericsErrorHandling.instance.reportDebugInfo(null, "Disposing old frame, then create new one.");
						key2canvasFrame.remove(canvasKey);
						oldFrame.dispose();
					}

					CanvasFrame frame = new CanvasFrame(title, CanvasFrame
							.getDefaultGamma() / gammaOfGrabber);
					AstericsErrorHandling.instance.reportDebugInfo(null,"Setting to pos: "+pos+", size: "+d);
					frame.setLocation(pos);
					//set default canvas size, if the plugin does not set it.
					int w=d.width > 0 ? d.width : 96;
					int h=d.height > 0 ? d.width : 96;
					frame.setCanvasSize(w, h);

					key2canvasFrame.put(canvasKey, frame);
				}
			}
		});
	}

	public CanvasFrame getCanvasFrame(String canvasKey) {
		synchronized (key2canvasFrame) {
			CanvasFrame frame = key2canvasFrame.get(canvasKey);
			return frame;
		}
	}

	public void showImage(String canvasKey, final IplImage image) {
		final CanvasFrame frame = getCanvasFrame(canvasKey);
		if (frame != null) {
			// invoke in Event Dispatch Thread, because of Swing
			// SwingUtilities.invokeLater(new Runnable() {
			// @Override
			// public void run() {
			frame.showImage(image);
			// }
			// });
		}
	}

	public void disposeFrame(final String canvasKey) {
		// Invoke the disposal in the event dispatch thread to resolve a
		// potential deadlock if the dispose is actually called from an ARE GUI
		// action.
		synchronized (key2canvasFrame) {
			final CanvasFrame frame = key2canvasFrame.get(canvasKey);
			if (frame != null) {
				key2canvasFrame.remove(canvasKey);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.dispose();						
					}
				});
			}
		}
	}
}
