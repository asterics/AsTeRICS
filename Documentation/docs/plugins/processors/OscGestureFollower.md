---
title: Osc Gesture Follower
---

# Osc Gesture Follower

Component Type: Processor (Subcategory: DSP and Feature Detection)

The OscGestureFollower component copules the ARE with the external gesture recognition software GestureFollower. The program is copyrighted by IRCAM. GestureFollower is stored in the ARE subfolder tools/GestureFollower. Gesture data can be stored and loaded from files in this subdirectory, these files have the extension ".mubu". The communication between GestureFollower and the ARE is based on the OpenSoundControl (OSC) protocol. This plugin utilizes the NetUtil java library (http://www.sciss.de/netutil/) for the OSC implementation, it is (C)opyrighted 2004-2011 by Hanns Holger Rutz and released under the GNU Lesser General Public License.

![OscGestureFollower howto](./img/OscGestureFollower.jpg "OscGestureFollower howto")

OscGestureFollower howto

## Requirements

- The plugin requires the gfOSC_v1.exe in subfolder tools/GestureFollower, which implements the actual gesture follower algorithms.
- Check your firewall configuration and network settings to ensure that OSC messages are not blocked.

## Functional Principle

Input data is received through CH1 to CH4 e.g. from sensors like the acceleration measurement unit. Not all inputs must be connected, but the synchronized checkboxes have to be checked correct. The events must be connected like illustrated in the picture. First the gestures must be teached in. To teach in the first gesture, send an event into the 'learn1' eventListener. After finishing the first gesture, send an event to 'learn2' to teach in the second gesture, and so on. After all gestures are teached in, send the 'stoplearn' event. To clear all gestures send the 'clear' event. To start the gesture recognition process, send the 'follow' event. To stop the gesture following process, send the 'stop' event. The 'load' and 'save' events can be used to load or store the learned gesture data into the given filename.

## Input Port Description

- **CH1 - CH4 \[double\]:** The input port which receive data values. **These 4 input ports support synchronization**

## Output Port Description

- **likeliest \[double\]:** While the gesturefollower is in 'follwing mode' the most likeliest gesture is indicated on the likeliest output port. Before it can sample the input data and recognize a gesture, some data must be teached in.

## Properties

- **InPort \[integer\]:** This value specifies the Port where OscMessages form the gesture follower are received.
- **OutPort \[integer\]:** This value specifies the Port where OscMessages are send to.
- **filename \[string\]:** Filename for the gesture data (load or save, .mubu file stored in the ARE subfoler tools/GestureFollower/). **Supports value suggestions from ARE (dynamic property)**.

## Event Listener Ports

- **stop:** this event stops the gesture following
- **stoplearn:** this event stops the gesture learning process
- **learn1 - learn5:** these events select gestures 1 - 5 for learning
- **learn1 - learn5:** these events select gestures 1 - 5 for learning
- **clear:** this event clears learned gestures
- **follow:** this event starts the gesture recognition phase
- **load:** this event loads gesture data from file
- **save:** this event saves gesture data to file

## Referred Plugins

- OscOutClient
- OpenVibe
- OscServer
