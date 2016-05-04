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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.sensor.kinect;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SkeletonPanel extends JPanel implements SkeletonListener{

	private static final long serialVersionUID = 1L;
	private Skeleton skel;
	
	
	@Override
	public void skeletonUpdate(Skeleton skel) {
		this.skel = skel;
		this.repaint();
	}


	@Override
	public void paint(Graphics g) {
		if (skel != null) {
			g.setColor(Color.black);
			g.fillRect(0,0,640,480);
			g.setColor(Color.GREEN);
			if (skel.head != null)
				g.fillOval((int)skel.head.getX(),(int)skel.head.getY(), 15, 15);
			if (skel.neck != null)
				g.fillRect((int)skel.neck.getX(),(int)skel.neck.getY(), 5, 5);
			if (skel.leftShoulder != null)
				g.fillRect((int)skel.leftShoulder.getX(),(int)skel.leftShoulder.getY(), 5, 5);
			if (skel.rightShoulder != null)
				g.fillRect((int)skel.rightShoulder.getX(),(int)skel.rightShoulder.getY(), 5, 5);
			if (skel.leftElbow != null)
				g.fillRect((int)skel.leftElbow.getX(),(int)skel.leftElbow.getY(), 5, 5);
			if (skel.rightElbow != null)
				g.fillRect((int)skel.rightElbow.getX(),(int)skel.rightElbow.getY(), 5, 5);
			if (skel.leftHand != null)
				g.fillRect((int)skel.leftHand.getX(),(int)skel.leftHand.getY(), 5, 5);
			if (skel.rightHand != null)
				g.fillRect((int)skel.rightHand.getX(),(int)skel.rightHand.getY(), 5, 5);
			if (skel.leftHip != null)
				g.fillRect((int)skel.leftHip.getX(),(int)skel.leftHip.getY(), 5, 5);
			if (skel.rightHip != null)
				g.fillRect((int)skel.rightHip.getX(),(int)skel.rightHip.getY(), 5, 5);
			if (skel.leftKnee != null)
				g.fillRect((int)skel.leftKnee.getX(),(int)skel.leftKnee.getY(), 5, 5);
			if (skel.rightKnee != null)
				g.fillRect((int)skel.rightKnee.getX(),(int)skel.rightKnee.getY(), 5, 5);
			if (skel.leftFoot != null)
				g.fillRect((int)skel.leftFoot.getX(),(int)skel.leftFoot.getY(), 5, 5);
			if (skel.rightFoot != null)
				g.fillRect((int)skel.rightFoot.getX(),(int)skel.rightFoot.getY(), 5, 5);
		}
	}

	
	
	
	
}
