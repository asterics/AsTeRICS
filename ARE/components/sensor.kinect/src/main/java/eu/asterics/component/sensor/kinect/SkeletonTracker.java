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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

/**
 *
 * @author David Thaller
 */
public class SkeletonTracker extends Thread {

    // class NewSkeletonObserver implements IObserver<>

    class NewUserObserver implements IObserver<UserEventArgs> {

        @Override
        public void update(IObservable<UserEventArgs> observable, UserEventArgs args) {
            System.out.println("New User found");
            if (isTracking()) {
                return;
            }

            userID = args.getId();
            System.out.println("with id " + userID);
            try {
                skelCap.loadSkeletonCalibrationDatadFromFile(userID, "data/sensor.kinect/calib.data");
                skelCap.startTracking(userID);
                System.out.println("Started Tracking for new User " + userID);
            } catch (StatusException e) {
                e.printStackTrace();
            }

        }
    }

    class LostUserObserver implements IObserver<UserEventArgs> {

        @Override
        public void update(IObservable<UserEventArgs> observable, UserEventArgs args) {
            try {
                System.out.println("Lost user " + args.getId());
                if (skelCap.isSkeletonTracking(args.getId())) {
                    skelCap.stopTracking(args.getId());
                }
            } catch (StatusException ex) {
                Logger.getLogger(SkeletonTracker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isTracking() {
        try {
            for (int user : userGen.getUsers()) {
                if (skelCap.isSkeletonTracking(user)) {
                    return true;
                }
            }
        } catch (StatusException ex) {
            Logger.getLogger(SkeletonTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Context context;
    private DepthGenerator depthGen;
    private UserGenerator userGen;
    private SkeletonCapability skelCap;
    private boolean shouldRun;
    private ArrayList<SkeletonListener> skelListeners;
    private int userID = 0;
    private boolean paused = false;

    public SkeletonTracker(Context context, DepthGenerator depthGen, UserGenerator userGen,
            SkeletonCapability skelCap) {
        skelListeners = new ArrayList<SkeletonListener>();

        try {
            this.context = context;
            this.depthGen = depthGen;
            this.userGen = userGen;
            this.skelCap = skelCap;

            userGen.getNewUserEvent().addObserver(new NewUserObserver());

            userGen.getLostUserEvent().addObserver(new LostUserObserver());

            skelCap.setSkeletonProfile(SkeletonProfile.ALL);

        } catch (StatusException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        shouldRun = false;
    }

    @Override
    public void run() {
        try {
            System.out.println("running");
            shouldRun = true;
            context.startGeneratingAll();
            while (shouldRun) {
                if (paused == false) {
                    context.waitAnyUpdateAll();
                    if (isTracking()) {
                        Skeleton skel = getSkeleton();
                        if (skel.head.getZ() != 0) {
                            for (SkeletonListener sl : skelListeners) {
                                sl.skeletonUpdate(skel);
                            }
                        }
                    }
                } else {
                    Thread.sleep(400);
                }
            }
            context.stopGeneratingAll();
        } catch (Exception ex) {
            Logger.getLogger(SkeletonTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pauseTracking() {
        paused = true;
    }

    public void resumeTracking() {
        paused = false;
    }

    public void stopTracking() {
        shouldRun = false;
        paused = false;
    }

    public void addSkeletonListener(SkeletonListener l) {
        skelListeners.add(l);
    }

    private Skeleton getSkeleton() {
        Skeleton skel = new Skeleton();
        skel.head = getJoint(userID, SkeletonJoint.HEAD);
        skel.neck = getJoint(userID, SkeletonJoint.NECK);
        skel.leftShoulder = getJoint(userID, SkeletonJoint.LEFT_SHOULDER);
        skel.rightShoulder = getJoint(userID, SkeletonJoint.RIGHT_SHOULDER);
        skel.leftElbow = getJoint(userID, SkeletonJoint.LEFT_ELBOW);
        skel.rightElbow = getJoint(userID, SkeletonJoint.RIGHT_ELBOW);
        skel.leftHand = getJoint(userID, SkeletonJoint.LEFT_HAND);
        skel.rightHand = getJoint(userID, SkeletonJoint.RIGHT_HAND);
        skel.leftFoot = getJoint(userID, SkeletonJoint.LEFT_FOOT);
        skel.rightFoot = getJoint(userID, SkeletonJoint.RIGHT_FOOT);
        /*
         * skel.leftHip = getJoint(userID, SkeletonJoint.LEFT_HIP);
         * skel.rightHip = getJoint(userID, SkeletonJoint.RIGHT_HIP);
         * skel.leftKnee = getJoint(userID, SkeletonJoint.LEFT_KNEE);
         * skel.rightKnee = getJoint(userID, SkeletonJoint.RIGHT_KNEE);
         */
        return skel;
    }

    private Point3D getJoint(int user, SkeletonJoint joint) {
        try {
            SkeletonJointPosition pos = skelCap.getSkeletonJointPosition(user, joint);
            org.OpenNI.Point3D p = pos.getPosition();
            org.OpenNI.Point3D p1 = depthGen.convertRealWorldToProjective(p);
            return new Point3D(p1.getX(), p1.getY(), p1.getZ());
        } catch (StatusException ex) {
            Logger.getLogger(SkeletonTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
