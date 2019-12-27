---
title: Events
---

# Events

The AsTeRICS platform knows two concepts of connecting two components to each other. The first one is channels, where data is transported from one component to another. The second one is the events-concept. Events are single or continuous happenings, which should trigger an action at the receiver. After connecting two components with an event channel, the event connections have to be set in the events tab (which appears in the property editor on the right side of the ACS, when an event channel is focussed). This event tab consists of a table with two columns: the left column lists the event listeners (at the component receiving the event), the right column lists the event triggers (coming from the component that sends the event). So, with the selection box on the right hand side (second column), one or several triggering events can be set for any listener. One component can send and receive events to and from several other components. The following figure shows the setting of events.

![Screenshot: ACS with Active Events Tab](./img/acs_with_active_events_tab.png "Screenshot: ACS with Active Events Tab")

ACS with Active Events Tab