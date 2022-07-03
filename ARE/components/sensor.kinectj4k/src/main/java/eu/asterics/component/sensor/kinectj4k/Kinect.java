package eu.asterics.component.sensor.kinectj4k;

import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

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

public class Kinect extends J4KSDK {

    KinectJ4KInstance astericskinect;

    public Kinect(KinectJ4KInstance kinectinstance) {
        super();
        astericskinect = kinectinstance;
    }

    @Override
    public void onColorFrameEvent(byte[] color_frame) {
    }

    @Override
    public void onDepthFrameEvent(short[] depth_frame, byte[] body_index, float[] xyz, float[] uv) {
    }

    @Override
    public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] positions, float[] orientations,
            byte[] joint_status) {
        Skeleton skeletons[] = new Skeleton[getMaxNumberOfSkeletons()];
        int id = 0;
        for (int i = 0; i < getMaxNumberOfSkeletons(); i++) {
            skeletons[i] = Skeleton.getSkeleton(i, skeleton_tracked, positions, orientations, joint_status, this);
            if (skeletons[i].isTracked()) {
                id = i;
            }
        }

        Skeleton skeleton = skeletons[id];

        astericskinect.setJointPointsFoot(skeleton.get3DJointX(Skeleton.FOOT_LEFT),
                skeleton.get3DJointY(Skeleton.FOOT_LEFT), skeleton.get3DJointZ(Skeleton.FOOT_LEFT),
                skeleton.get3DJointX(Skeleton.FOOT_RIGHT), skeleton.get3DJointY(Skeleton.FOOT_RIGHT),
                skeleton.get3DJointZ(Skeleton.FOOT_RIGHT));
        astericskinect.setJointPointsAnkle(skeleton.get3DJointX(Skeleton.ANKLE_LEFT),
                skeleton.get3DJointY(Skeleton.ANKLE_LEFT), skeleton.get3DJointZ(Skeleton.ANKLE_LEFT),
                skeleton.get3DJointX(Skeleton.ANKLE_RIGHT), skeleton.get3DJointY(Skeleton.ANKLE_RIGHT),
                skeleton.get3DJointZ(Skeleton.ANKLE_RIGHT));
        astericskinect.setJointPointShoulder(skeleton.get3DJointX(Skeleton.SHOULDER_LEFT),
                skeleton.get3DJointY(Skeleton.SHOULDER_LEFT), skeleton.get3DJointZ(Skeleton.SHOULDER_LEFT),
                skeleton.get3DJointX(Skeleton.SPINE_SHOULDER), skeleton.get3DJointY(Skeleton.SPINE_SHOULDER),
                skeleton.get3DJointZ(Skeleton.SPINE_SHOULDER), skeleton.get3DJointX(Skeleton.SHOULDER_RIGHT),
                skeleton.get3DJointY(Skeleton.SHOULDER_RIGHT), skeleton.get3DJointZ(Skeleton.SHOULDER_RIGHT));
        astericskinect.setJointPointsKnee(skeleton.get3DJointX(Skeleton.KNEE_LEFT),
                skeleton.get3DJointY(Skeleton.KNEE_LEFT), skeleton.get3DJointZ(Skeleton.KNEE_LEFT),
                skeleton.get3DJointX(Skeleton.KNEE_RIGHT), skeleton.get3DJointY(Skeleton.KNEE_RIGHT),
                skeleton.get3DJointZ(Skeleton.KNEE_RIGHT));
        astericskinect.setJointPointHip(skeleton.get3DJointX(Skeleton.HIP_LEFT),
                skeleton.get3DJointY(Skeleton.HIP_LEFT), skeleton.get3DJointZ(Skeleton.HIP_LEFT),
                skeleton.get3DJointX(Skeleton.SPINE_BASE), skeleton.get3DJointY(Skeleton.SPINE_BASE),
                skeleton.get3DJointZ(Skeleton.SPINE_BASE), skeleton.get3DJointX(Skeleton.HIP_RIGHT),
                skeleton.get3DJointY(Skeleton.HIP_RIGHT), skeleton.get3DJointZ(Skeleton.HIP_RIGHT));
        astericskinect.setJointPointSpine(skeleton.get3DJointX(Skeleton.SPINE_MID),
                skeleton.get3DJointY(Skeleton.SPINE_MID), skeleton.get3DJointZ(Skeleton.SPINE_MID));
        astericskinect.setJointPointElbow(skeleton.get3DJointX(Skeleton.ELBOW_LEFT),
                skeleton.get3DJointY(Skeleton.ELBOW_LEFT), skeleton.get3DJointZ(Skeleton.ELBOW_LEFT),
                skeleton.get3DJointX(Skeleton.ELBOW_RIGHT), skeleton.get3DJointY(Skeleton.ELBOW_RIGHT),
                skeleton.get3DJointZ(Skeleton.ELBOW_RIGHT));
        astericskinect.setJointPointHand(skeleton.get3DJointX(Skeleton.HAND_LEFT),
                skeleton.get3DJointY(Skeleton.HAND_LEFT), skeleton.get3DJointZ(Skeleton.HAND_LEFT),
                skeleton.get3DJointX(Skeleton.HAND_RIGHT), skeleton.get3DJointY(Skeleton.HAND_RIGHT),
                skeleton.get3DJointZ(Skeleton.HAND_RIGHT));
        astericskinect.setJointPointWrist(skeleton.get3DJointX(Skeleton.WRIST_LEFT),
                skeleton.get3DJointY(Skeleton.WRIST_LEFT), skeleton.get3DJointZ(Skeleton.WRIST_LEFT),
                skeleton.get3DJointX(Skeleton.WRIST_RIGHT), skeleton.get3DJointY(Skeleton.WRIST_RIGHT),
                skeleton.get3DJointZ(Skeleton.WRIST_RIGHT));
        astericskinect.setJointPointHead(skeleton.get3DJointX(Skeleton.HEAD), skeleton.get3DJointY(Skeleton.HEAD),
                skeleton.get3DJointZ(Skeleton.HEAD));
    }

}
