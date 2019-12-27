---
title: List View
---

# List View

The List View provides the same model as shown in the Model Designer, but as a list of elements. It is sorted by Sensors, Processors and Actuators. For each component it shows a list of Ports and for each port a list of connections, including a direct link to the connected element. It also contains buttons to connect channels. For example by pressing _Start new datachannel_, a new datachannel is started. The focus automatically jumps to the _Action Pending_ section at the top of the page, where the pending action is indicated and a _Cancel channel_ button enables the user to continue without completing the channel. However, if the user presses a _Connect datachannel here_ button at a matching input port, the connection is finalised and the channel now exists. This works in the same way for event channels.

![Screenshot: List View](./img/list_view.png "Screenshot: List View")

List View

Keyboard users can enter keyboard mode by pressing _Enter_. Then the user can navigate the elements by using the _arrow_ keys. _Space_ activates port mode, so that the ports of the selected components can be navigated by using the _arrow_ keys. _Esc_ ends port mode.  
When in port mode, the user can activate channel mode by pressing the _Space_ bar again. Now the channels connected to the selected port can be navigated using the _arrow_ keys. _Esc_ ends channel mode.  
The buttons for connecting the channels and for jumping to connected elements can be reached by using the _Tab_ key