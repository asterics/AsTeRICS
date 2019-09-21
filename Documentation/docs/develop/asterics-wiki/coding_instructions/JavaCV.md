# Objective
This tutorial demonstrates how to use the APIs for computer vision tasks in AsTeRICS. The tutorial does not show how to create an [AsTeRICS plugin](https://github.com/asterics/AsTeRICS/wiki/Plugin-Development) where the API would be used normally.

# Introduction
AsTeRICS has several computer vision plugins (e.g. [XFacetrackerLK](http://asterics.github.io/AsTeRICS/webapps/WebACS/help/index.html?plugins&sensors/XFacetrackerLK.htm) for face tracking).

To simplify the development of such plugins and adding crossplatform support easily, AsTeRICS 3.0 uses a subset of [JavaCV 1.3](https://github.com/bytedeco/javacv/tree/1.3). Additionally, the [computervision service](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE/services/ComputerVision/src/main/java/eu/asterics/mw/computervision) provides helper classes for frame grabbing, face detection and frame visualization.

## JavaCV 
[JavaCV](https://github.com/bytedeco/javacv/tree/1.3) is a Java wrapper for commonly used computer vision libraries and uses [JavaCPP technology](https://github.com/bytedeco/javacpp) for the binding of native libraries (based on JNI). [JavaCPP Presets](https://github.com/bytedeco/javacpp-presets/tree/1.3) define the respective bindings (e.g. OpenCV, FFmpeg, OpenKinect, videoInput, flandmark, ARToolkitPlus, …) that can then be used within Java.

# Prerequisites
* [AsTeRICS 3.0 installed](https://github.com/asterics/AsTeRICS/releases/tag/v3.0)
* Java IDE ([Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/neon3) recommended)
* [Java Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Webcam or USB camera or RaspiCam

# Preparation

1. Start Eclipse
2. Create a new Java project (```File/New/Java Project```)
3. Add the following libraries to the build configuration (```Project/Properties/Java Build Path/Libraries```)

  * ```<ARE baseURI>/asterics.ARE.jar```
  * ```<ARE baseURI>/asterics.mw.computervision.jar```
  * ```<ARE baseURI>/javacv-1.3.0-basic-windows.jar``` (on Linux/Mac OSX use ```javacv-1.3.0-basic-linux|macosx.jar```)
  * ```<ARE baseURI>/../APE/lib/commons-io-2.4.jar```
  * ```<ARE baseURI>/../APE/lib/commons-codec-1.11.jar```
4. Copy the directory ```<ARE baseURI>/data/service.computervision``` to ```<projectdir>/data/```

![Build path dialog with external libraries](developer_guide/coding_instructions/images/BuildPath.JPG)

4. Create a main class ```FaceDetectionExample``` and copy and paste the following template code into it

```java
import java.awt.Dimension;
import java.awt.Point;

//Imports OpenCV wrapper (classes, methods and constants)
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacv.FrameGrabber;

import eu.asterics.mw.computervision.FaceDetection;
import eu.asterics.mw.computervision.GrabbedImageListener;
import eu.asterics.mw.computervision.SharedCanvasFrame;
import eu.asterics.mw.computervision.SharedFrameGrabber;

public class FaceDetectionExample implements GrabbedImageListener {
  //Utility class which simplifies face detection and drawing.
  FaceDetection faceDetection=new FaceDetection();
  
  //Define camera device and id for frame display window.
  String camDeviceKey="1";
  String canvasInstanceId="FaceDetectionExample";
  
  //CvPoint is an OpenCV structure for describing point: https://docs.opencv.org/3.2.0/dc/dd1/structCvPoint.html
  //We can allocate native arrays using constructors taking an integer as argument.
  CvPoint hatPoints = new CvPoint(3);
  
  public static void main(String[] args) throws Exception {
    FaceDetectionExample faceDetectionExample=new FaceDetectionExample();
    faceDetectionExample.start();
  }

  public FaceDetectionExample() {    
  }
  
  public void start() throws Exception {
  }
  
  public void stop() {
  }

  /**
   * Listener method which is called for each grabbed frame.
   */
  @Override
  public void imageGrabbed(IplImage frame) {
  }
}

```
## Example 1 - Face detection and face rectangle

The computer vision service in AsTeRICS provides convinience methods that facilitate the task of frame grabbing (```class SharedFrameGrabber```) and visualization (```SharedCanvasFrame```).

### Init and Start grabbing
1. Create a [```FrameGrabber```](https://github.com/bytedeco/javacv/wiki/Video-Preview-and-Video-Recording-Classes) instance, which is an abstraction of a frame grabbing functionality implemented by a computer vision library (e.g. videoInput, OpenCV). Using the method ```getFrameGrabber(...)``` returns the default frame grabber (**Windows**: videoInput, **Linux**: FFmpeg, **Mac OSX**: OpenCV) for the platform the program is running on.
2. Register a ```GrabbedImageListener``` which receives grabbed frames
3. Create window for displaying video frames.
4. Start grabbing.

Copy and paste the following code into the method ```public void start()```:
```java
    //Get a frame grabber for the device with the given key (either a number e.g. 0 or a device path e.g. /dev/video0)
    //This is dependent on the used frame grabber (e.g. FFMpeg only supports device paths)
    FrameGrabber grabber = SharedFrameGrabber.instance.getFrameGrabber(camDeviceKey);
    
    //Register a listener to receive the grabbed images of type IplImage.
    SharedFrameGrabber.instance.registerGrabbedImageListener(camDeviceKey,this);
    
    //Create a window which is used to display the video frame.
    SharedCanvasFrame.instance.createCanvasFrame(canvasInstanceId, "Face Detection Example", grabber.getGamma(), new Point(100,100), new Dimension(200,200));
    
    //Starts grabbing in a dedicated thread and notifies all registered listeners with the IplImage frame grabbed.
    SharedFrameGrabber.instance.startGrabbing(camDeviceKey);
```
### Face detection and drawing

The class ```FaceDetection``` provides convinience methods for face detection using a [Haar cascade for the face](https://docs.opencv.org/2.4/modules/objdetect/doc/cascade_classification.html) and drawing with typical parameters.

Copy and paste the following code into the method ```public void imageGrabbed(IplImage frame)```:
```java
    try {
      //Utility method which does face detection with standard parameters.
      CvRect faceRect = faceDetection.detectFace(frame);
      if (faceRect != null) {
        faceDetection.drawFaceRect(faceRect, frame);        
      }
      
      //Finally show the image with added drawings
      SharedCanvasFrame.instance.showImage(canvasInstanceId, frame);
    } catch (Exception e) {
      e.printStackTrace();
    }
```

### Stop grabbing & cleanup
Finally you must stop grabbing, deregister the ```GrabbedImageListener``` and dispose the window showing the video frame.

Copy and paste the following code into the method ```public void stop()```:
```java
    //Stop the grabber thread.
    SharedFrameGrabber.instance.stopGrabbing(camDeviceKey);
    //Deregister this as listener.
    SharedFrameGrabber.instance.deregisterGrabbedImageListener(camDeviceKey, this);
    //Dispose the window for frame visualization. 
    SharedCanvasFrame.instance.disposeFrame(canvasInstanceId);
```

## Example 2 - Drawing a hat on top of the face
This example shows how to use the [drawing functions](https://docs.opencv.org/2.4/modules/core/doc/drawing_functions.html) of OpenCV to draw a hat on top of the facial position. Generally you can use both the C-API or the C++-API of OpenCV with similar syntax. Nevertheless, there are some rules of how to [convert OpenCV code to JavaCV code](https://github.com/bytedeco/javacv/wiki/Converting-OpenCV).
You can draw a rectangle with ```cvRectangle(...)``` and draw a filled polygon with ```cvFillConvexPoly(...)```.

Use the code of [Example 1](#example-1---face-detection-and-face-rectangle) and overwrite the implementation of the method ```public void imageGrabbed(...)```:
```java
    try {
      //Utility method which does face detection with standard parameters.
      CvRect faceRect = faceDetection.detectFace(frame);
      if (faceRect != null) {
        int x = faceRect.x(), y = faceRect.y(), w = faceRect.width(), h = faceRect.height();
        
        //Draw a red face rectangle with cvRectangle
        cvRectangle(frame, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);

        //Draw a green hat on top of the face.
        //To access or pass as argument the elements of a native array, 
        //call position() before. --> position(0) refers to the first element.
        hatPoints.position(0).x(x - w / 10).y(y - h / 10);
        hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
        hatPoints.position(2).x(x + w / 2).y(y - h / 2);
        cvFillConvexPoly(frame, hatPoints.position(0), 3, CvScalar.GREEN, CV_AA, 0);
      }

      // Finally show the image with added drawings
      SharedCanvasFrame.instance.showImage(canvasInstanceId, frame);
    } catch (Exception e) {
      e.printStackTrace();
    }
```
# References
* [Source code of class XFacetrackerLK](https://github.com/asterics/AsTeRICS/blob/v3.0/ARE/components/sensor.XfacetrackerLK/src/main/java/eu/asterics/component/sensor/XfacetrackerLK/XFacetrackerLKInstance.java)
* [computervision service](https://github.com/asterics/AsTeRICS/tree/v3.0/ARE/services/ComputerVision/src/main/java/eu/asterics/mw/computervision)
* [JavaCV 1.3](https://github.com/bytedeco/javacv/tree/1.3)
* [JavaCV 1.3 Wiki](https://github.com/bytedeco/javacv/wiki)
* [JavaCV google group](groups.google.com/group/javacv)
* [JavaCV examples including OpenCV Cookbook](https://github.com/bytedeco/javacv-examples)
* [OpenCV examples for HCI (ch6, ch7): Mastering OpenCV with Practical Computer Vision Projects](https://github.com/MasteringOpenCV/code)

