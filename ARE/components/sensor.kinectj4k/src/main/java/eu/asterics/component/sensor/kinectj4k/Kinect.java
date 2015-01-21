package eu.asterics.component.sensor.kinectj4k;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

/*
 * Copyright 2011-2014, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * When this program is used for academic or research purposes, 
 * please cite the following article that introduced this Java library: 
 * 
 * A. Barmpoutis. "Tensor Body: Real-time Reconstruction of the Human Body 
 * and Avatar Synthesis from RGB-D', IEEE Transactions on Cybernetics, 
 * October 2013, Vol. 43(5), Pages: 1347-1356. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class Kinect extends J4KSDK{
	
	KinectJ4KInstance astericskinect;
	
	public Kinect (KinectJ4KInstance kinectinstance)
	{
		super();
		astericskinect=kinectinstance;		
	}
	
	@Override
	public void onColorFrameEvent(byte[] color_frame) {}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] body_index, float[] xyz, float[] uv) {}

	@Override
	public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] positions,float[] orientations, byte[] joint_status) {
		Skeleton skeletons[]=new Skeleton[getMaxNumberOfSkeletons()];
		int id=0;
        for(int i=0;i<getMaxNumberOfSkeletons();i++)
        {
          skeletons[i]=Skeleton.getSkeleton(i, skeleton_tracked, positions, orientations, joint_status, this);
          if(skeletons[i].isTracked())
        	  id=i;
        }
        
        Skeleton skeleton = skeletons[id];
        
        astericskinect.setJointPointsFoot(skeleton.get3DJointX(Skeleton.FOOT_LEFT), skeleton.get3DJointY(Skeleton.FOOT_LEFT), skeleton.get3DJointZ(Skeleton.FOOT_LEFT), 
        		skeleton.get3DJointX(Skeleton.FOOT_RIGHT), skeleton.get3DJointY(Skeleton.FOOT_RIGHT), skeleton.get3DJointZ(Skeleton.FOOT_RIGHT));
        astericskinect.setJointPointsAnkle(skeleton.get3DJointX(Skeleton.ANKLE_LEFT), skeleton.get3DJointY(Skeleton.ANKLE_LEFT), skeleton.get3DJointZ(Skeleton.ANKLE_LEFT), 
        		skeleton.get3DJointX(Skeleton.ANKLE_RIGHT), skeleton.get3DJointY(Skeleton.ANKLE_RIGHT), skeleton.get3DJointZ(Skeleton.ANKLE_RIGHT));
        astericskinect.setJointPointShoulder(skeleton.get3DJointX(Skeleton.SHOULDER_LEFT), skeleton.get3DJointY(Skeleton.SHOULDER_LEFT), skeleton.get3DJointZ(Skeleton.SHOULDER_LEFT),
        		skeleton.get3DJointX(Skeleton.SPINE_SHOULDER), skeleton.get3DJointY(Skeleton.SPINE_SHOULDER), skeleton.get3DJointZ(Skeleton.SPINE_SHOULDER),
        		skeleton.get3DJointX(Skeleton.SHOULDER_RIGHT), skeleton.get3DJointY(Skeleton.SHOULDER_RIGHT), skeleton.get3DJointZ(Skeleton.SHOULDER_RIGHT));
        astericskinect.setJointPointsKnee(skeleton.get3DJointX(Skeleton.KNEE_LEFT), skeleton.get3DJointY(Skeleton.KNEE_LEFT), skeleton.get3DJointZ(Skeleton.KNEE_LEFT), 
        		skeleton.get3DJointX(Skeleton.KNEE_RIGHT), skeleton.get3DJointY(Skeleton.KNEE_RIGHT), skeleton.get3DJointZ(Skeleton.KNEE_RIGHT));
        astericskinect.setJointPointHip(skeleton.get3DJointX(Skeleton.HIP_LEFT), skeleton.get3DJointY(Skeleton.HIP_LEFT), skeleton.get3DJointZ(Skeleton.HIP_LEFT), 
        		skeleton.get3DJointX(Skeleton.SPINE_BASE), skeleton.get3DJointY(Skeleton.SPINE_BASE), skeleton.get3DJointZ(Skeleton.SPINE_BASE), 
        		skeleton.get3DJointX(Skeleton.HIP_RIGHT), skeleton.get3DJointY(Skeleton.HIP_RIGHT), skeleton.get3DJointZ(Skeleton.HIP_RIGHT));
        astericskinect.setJointPointSpine(skeleton.get3DJointX(Skeleton.SPINE_MID), skeleton.get3DJointY(Skeleton.SPINE_MID), skeleton.get3DJointZ(Skeleton.SPINE_MID));
        astericskinect.setJointPointElbow(skeleton.get3DJointX(Skeleton.ELBOW_LEFT), skeleton.get3DJointY(Skeleton.ELBOW_LEFT), skeleton.get3DJointZ(Skeleton.ELBOW_LEFT), 
        		skeleton.get3DJointX(Skeleton.ELBOW_RIGHT), skeleton.get3DJointY(Skeleton.ELBOW_RIGHT), skeleton.get3DJointZ(Skeleton.ELBOW_RIGHT));
        astericskinect.setJointPointHand(skeleton.get3DJointX(Skeleton.HAND_LEFT), skeleton.get3DJointY(Skeleton.HAND_LEFT), skeleton.get3DJointZ(Skeleton.HAND_LEFT), 
        		skeleton.get3DJointX(Skeleton.HAND_RIGHT), skeleton.get3DJointY(Skeleton.HAND_RIGHT), skeleton.get3DJointZ(Skeleton.HAND_RIGHT));
        astericskinect.setJointPointWrist(skeleton.get3DJointX(Skeleton.WRIST_LEFT), skeleton.get3DJointY(Skeleton.WRIST_LEFT), skeleton.get3DJointZ(Skeleton.WRIST_LEFT), 
        		skeleton.get3DJointX(Skeleton.WRIST_RIGHT), skeleton.get3DJointY(Skeleton.WRIST_RIGHT), skeleton.get3DJointZ(Skeleton.WRIST_RIGHT));
        astericskinect.setJointPointHead(skeleton.get3DJointX(Skeleton.HEAD), skeleton.get3DJointY(Skeleton.HEAD), skeleton.get3DJointZ(Skeleton.HEAD));
	}
}
