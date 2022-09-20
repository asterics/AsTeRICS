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
 *         This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 * --------------------------------------------------------------------------------
 * AsTeRICS – EC Grant Agreement No. 247730
 * Assistive Technology Rapid Integration and Construction Set
 * --------------------------------------------------------------------------------
 * Filename: Grouping.cs
 * Class(es):
 *   Classname: MainWindow
 *   Description: Implementation of all grouping functionality
 * Author: Roland Ossmann, David Thaller
 * Date: 05.09.2011
 * Version: 0.1
 * Comment: Partial class of MainWindow, other parts of this class in the files
 *   MainWindow.xaml, MainWindow.xaml.cs and MainWindowGUIEditor.xaml.cs
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Windows;
using System.Windows.Controls;
using System.Diagnostics;
using System.Windows.Media;
using System.IO;
using System.Xml.Serialization;
using Microsoft.Windows.Controls.Ribbon;

namespace Asterics.ACS {
    partial class MainWindow {

        private System.Collections.Generic.Dictionary<string, groupComponent> groupsList = new System.Collections.Generic.Dictionary<string, groupComponent>();


        /// <summary>
        /// Calling the DoGrouping function, which implements the grouping
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void GroupButton_Click(object sender, RoutedEventArgs e) {
            DoGrouping("", true, true);
        }

        /// <summary>
        /// Creates a group of all selected components.
        /// For each eventchannelline connected to the selected components, events will also be visible in the group element
        /// For each channel connected to the selected components, an in or outputport will be added to the group component
        /// </summary>
        /// <param name="idForGroup">The id (name) of the group</param>
        /// <param name="addToUndoStack">Desition, if the undo-operation should be put on the undo-stack</param>
        /// <param name="storeGroup">Desition, if the group will be part of the model (stored in the deployment model)</param>
        /// <returns></returns>
        private String DoGrouping(String idForGroup, bool addToUndoStack, bool storeGroup) {
            Boolean noGroupInSelection = true;
            ArrayList selectedGroupChannelList = new ArrayList();
            ArrayList selectedGroupComponentList = new ArrayList();
            ArrayList selectedGroupEventChannelList = new ArrayList();
            ArrayList selectedGroupEdgeListenerEventChannelList = new ArrayList();
            ArrayList selectedGroupEdgeTriggerEventChannelList = new ArrayList();
            
            if (selectedComponentList.Count == 0) {
                MessageBox.Show(Properties.Resources.GroupingNoItemsSelected, Properties.Resources.GroupingNoItemsSelectedHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                return "";
            }

            // search for a group in the selection
            foreach (componentType mc in selectedComponentList) {
                if (mc.ComponentType == ACS2.componentTypeDataTypes.group) {
                    noGroupInSelection = false;
                    MessageBox.Show(Properties.Resources.GroupingWithSelectedGroups, Properties.Resources.GroupingWithSelectedGroupsHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    break;
                }
            }
            if (noGroupInSelection == false)
                return "";
            groupComponent newGroup = new groupComponent();
            // building a "new" model
            Asterics.ACS2.componentTypesComponentType newGroupForBundle = new ACS2.componentTypesComponentType();

            newGroupForBundle.type = new ACS2.componentType();
            newGroupForBundle.type.Value = ACS2.componentTypeDataTypes.group;
            
            int counter = 0;
            string suggestID = "";
            /*do {
                counter++;
                suggestID = "group" + counter;
            } while (componentList.ContainsKey(suggestID));*/
            bool gExists;
            
            do {
                gExists = false;
                foreach (componentType ct in deploymentComponentList.Values) {
                    if (ct.id.StartsWith(suggestID)) {
                        counter++;
                        gExists = true;
                    }
                    suggestID = "group" + counter;
                }
            } while (gExists);
            Console.WriteLine("SuggestID is " + suggestID);
            if (idForGroup != null && idForGroup != "")
                newGroupForBundle.id = idForGroup;
            else
                newGroupForBundle.id = suggestID;
            // adding the channels and the eventchannels here
            object[] ports = new object[0];
            newGroupForBundle.ports = ports;

            // find lowest and higest cooridantes to place new group component in the middle
            int lowestX = 2000;
            int lowestY = 2000;
            int highestX = 0;
            int highestY = 0;
            if (selectedComponentList.Count > 0) {
                lowestX = highestX = (Int32)Canvas.GetLeft(selectedComponentList.First().ComponentCanvas);
                lowestY = highestY = (Int32)Canvas.GetTop(selectedComponentList.First().ComponentCanvas);
            }

            // remove selected components from canvas and add to group element
            foreach (componentType mc in selectedComponentList) {
                selectedGroupComponentList.Add(mc);
                if (Canvas.GetLeft(mc.ComponentCanvas) < lowestX) {
                    lowestX = (Int32)Canvas.GetLeft(mc.ComponentCanvas);
                }
                else if (Canvas.GetLeft(mc.ComponentCanvas) > highestX) {
                    highestX = (Int32)Canvas.GetLeft(mc.ComponentCanvas);
                }
                if (Canvas.GetTop(mc.ComponentCanvas) < lowestY) {
                    lowestY = (Int32)Canvas.GetTop(mc.ComponentCanvas);
                }
                else if (Canvas.GetTop(mc.ComponentCanvas) > highestY) {
                    highestY = (Int32)Canvas.GetTop(mc.ComponentCanvas);
                }
                // new with grouping rework
                mc.ComponentCanvas.Visibility = System.Windows.Visibility.Hidden;
                newGroup.AddedComponentList.AddLast(mc);

            }

            /*
             * search channels which are connected to components which will become a member of the group
             */
            ArrayList foundInsideChannels = new ArrayList();
            ArrayList foundEdgeSourceChannels = new ArrayList();
            ArrayList foundEdgeTargetChannels = new ArrayList();

            foreach (channel ch in deploymentChannelList.Values) {
                bool sourceSelected = selectedComponentList.Contains(deploymentComponentList[ch.source.component.id]);
                bool targetSelected = selectedComponentList.Contains(deploymentComponentList[ch.target.component.id]);
                if (sourceSelected && targetSelected) {
                    foundInsideChannels.Add(ch);
                }
                else if (sourceSelected && !targetSelected) {
                    foundEdgeSourceChannels.Add(ch);
                }
                else if (!sourceSelected && targetSelected) {
                    foundEdgeTargetChannels.Add(ch);
                }
            }

            foreach (channel ch in foundInsideChannels) {
                if (canvas.Children.Contains(ch.Line)) {
                    ch.Line.Visibility = System.Windows.Visibility.Hidden;
                    newGroup.AddedChannelsList.AddLast(ch);
                }

            }
            
            foreach (channel ch in foundEdgeSourceChannels) {
                if (canvas.Children.Contains(ch.Line)) {
                    ch.Line.Visibility = System.Windows.Visibility.Hidden;
                    newGroup.AddedChannelsList.AddLast(ch);
                }
                // Add out Port to the component
                componentType source = deploymentComponentList[ch.source.component.id];
                outputPortType outp = null;
                foreach (object o in source.PortsList.Values) {
                    if (o is outputPortType) {
                        outputPortType outp1 = (outputPortType)o;
                        if (outp1.portTypeID.Equals(ch.source.port.id)) {
                            outp = outp1;
                        }
                    }
                }
                if (outp == null)
                    continue;
                Asterics.ACS2.outputPortType outPort = new ACS2.outputPortType();
                outPort.id = ch.source.component.id + "_" + ch.source.port.id;
                outPort.description = outp.Description;
                outPort.dataType = outp.PortDataType;
                refType refT = new refType();
                refT.componentID = ch.source.component.id;
                refT.portID = ch.source.port.id;
                outPort.RefPort = refT;

                if (!newGroupForBundle.PortsList.Contains(outPort.id)) {
                    newGroupForBundle.PortsList.Add(outPort.id, outPort);
                }
            }

            foreach (channel ch in foundEdgeTargetChannels) {
                if (canvas.Children.Contains(ch.Line)) {
                    ch.Line.Visibility = System.Windows.Visibility.Hidden;
                    newGroup.AddedChannelsList.AddLast(ch);
                }

                // Add in Port to the component
                componentType target = deploymentComponentList[ch.target.component.id];
                inputPortType inp = null;
                foreach (object o in target.PortsList.Values) {
                    if (o is inputPortType) {
                        inputPortType inp1 = (inputPortType)o;
                        if (inp1.portTypeID.Equals(ch.target.port.id)) {
                            inp = inp1;
                        }
                    }
                }
                if (inp == null)
                    continue;
                Asterics.ACS2.inputPortType inPort = new ACS2.inputPortType();
                inPort.id = ch.target.component.id + "_" + ch.target.port.id;
                inPort.description = inp.Description;
                inPort.dataType = inp.PortDataType;
                inPort.mustBeConnected = inp.MustBeConnected;
                refType refT = new refType();
                refT.componentID = ch.target.component.id;
                refT.portID = ch.target.port.id;
                inPort.RefPort = refT;
                if (!newGroupForBundle.PortsList.Contains(inPort.id)) {
                    newGroupForBundle.PortsList.Add(inPort.id, inPort);
                }
            }
            
            /*
             * search eventchannels which are connected to components which will become a member of the group
             */
            ArrayList foundEdgeListenerEvents = new ArrayList();
            ArrayList foundEdgeTriggerEvents = new ArrayList();
            foreach (eventChannel ec in eventChannelList) {
                // search for each event channel on the edge of the group element
                if (!selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id])) {
                    foreach (EventTriggerPort etp in deploymentComponentList[ec.sources.source.component.id].EventTriggerList) {
                        ACS2.eventsTypeEventTriggererPortType foundTrigger = new ACS2.eventsTypeEventTriggererPortType();
                        foundTrigger.id = ec.sources.source.component.id + "_" + etp.EventTriggerId;
                        foundTrigger.description = etp.EventDescription;
                        bool contains = false;
                        foreach (ACS2.eventsTypeEventTriggererPortType etel in foundEdgeTriggerEvents) {
                            if (etel.id.Equals(foundTrigger.id)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains)
                            foundEdgeTriggerEvents.Add(foundTrigger);
                    }

                    // search for lines, being connected to an edge of the group
                    foreach (eventChannelLine ecl in eventChannelLinesList) {
                        if (ecl.Line.Visibility == System.Windows.Visibility.Visible &&
                            ecl.ListenerComponentId == ec.targets.target.component.id && ecl.TriggerComponentId == ec.sources.source.component.id) {
                            //selectedEventChannelList.AddFirst(ecl);
                            if (!selectedGroupEdgeTriggerEventChannelList.Contains(ecl))
                                selectedGroupEdgeTriggerEventChannelList.Add(ecl);
                            break;
                        }
                    }
                    // search for each event channel on the edge of the group element
                }
                else if (selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && !selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id])) {
                    foreach (EventListenerPort elp in deploymentComponentList[ec.targets.target.component.id].EventListenerList) {
                        ACS2.eventsTypeEventListenerPortType foundListener = new ACS2.eventsTypeEventListenerPortType();
                        foundListener.id = ec.targets.target.component.id + "_" + elp.EventListenerId;
                        foundListener.description = elp.EventDescription;
                        bool contains = false;
                        foreach (ACS2.eventsTypeEventListenerPortType etel in foundEdgeListenerEvents) {
                            if (etel.id.Equals(foundListener.id)) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains)
                            foundEdgeListenerEvents.Add(foundListener);
                    }
                    // search for lines, being connected to an edge of the group
                    foreach (eventChannelLine ecl in eventChannelLinesList) {
                        if (ecl.Line.Visibility == System.Windows.Visibility.Visible &&
                            ecl.ListenerComponentId == ec.targets.target.component.id && ecl.TriggerComponentId == ec.sources.source.component.id) {
                            //selectedEventChannelList.AddFirst(ecl);
                            if (!selectedGroupEdgeListenerEventChannelList.Contains(ecl))
                                selectedGroupEdgeListenerEventChannelList.Add(ecl);
                            break;
                        }
                    }
                    // search for each event channel in the group element
                }
                else if (selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id])) {
                    // search for lines, being between two selected components, but not selected
                    foreach (eventChannelLine ecl in eventChannelLinesList) {
                        if (ecl.Line.Visibility == System.Windows.Visibility.Visible &&
                            ecl.ListenerComponentId == ec.targets.target.component.id && ecl.TriggerComponentId == ec.sources.source.component.id) {
                            if (!selectedGroupEventChannelList.Contains(ecl))
                                selectedGroupEventChannelList.Add(ecl);
                            break;
                        }
                    }
                }
            }
            ArrayList emptyEventChannelLines = new ArrayList();
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                // only check eventchannelLine that correspond to group components
                bool found = false;
                foreach (eventChannel ech in eventChannelList) {
                    string source = ech.sources.source.component.id;
                    string target = ech.targets.target.component.id;
                    if (ecl.ListenerComponentId.Equals(target) && ecl.TriggerComponentId.Equals(source)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    emptyEventChannelLines.Add(ecl);
            }
            foreach (eventChannelLine ecl in emptyEventChannelLines) {
                DeleteEventChannelCommand(ecl);
            }
            int eventCount = foundEdgeTriggerEvents.Count + foundEdgeListenerEvents.Count;
            if (eventCount > 0) {
                newGroupForBundle.events = new object[eventCount];
                foundEdgeListenerEvents.CopyTo(newGroupForBundle.events, 0);
                foundEdgeTriggerEvents.CopyTo(newGroupForBundle.events, foundEdgeListenerEvents.Count);
            }
            // adding the final "new" model to the list of all components (bundles)
            newGroupForBundle.ports = new object[newGroupForBundle.PortsList.Values.Count];
            newGroupForBundle.PortsList.Values.CopyTo(newGroupForBundle.ports, 0);
            newGroupForBundle.PortsList.Clear();
            newGroupForBundle.ComponentCanvas.Children.Clear();
            foreach (componentType ct in selectedComponentList) {
                if (!componentList.ContainsKey(ct.id))
                    continue;
                ACS2.componentTypesComponentType ctct = (ACS2.componentTypesComponentType) componentList[ct.id];
                if (ctct.singleton) {
                    newGroupForBundle.singleton = true;
                    break;
                }
            }
            if (idForGroup != null && idForGroup != "")
                newGroupForBundle.InitGraphPorts(idForGroup);
            else
                newGroupForBundle.InitGraphPorts(suggestID);

            // generate the id of the group in the acs
            string compName = suggestID;
            counter = 0;
            if (idForGroup == "" || idForGroup == null) {
                do {
                    counter++;
                    compName = suggestID + "." + counter;
                    compName = TrimComponentName(compName);
                } while (deploymentComponentList.ContainsKey(compName));
            }
            else {
                compName = idForGroup;
            }

            BrushConverter bc = new BrushConverter();
            
            if (idForGroup != null && idForGroup != "") {
                if (componentList.ContainsKey(idForGroup))
                    componentList.Remove(idForGroup);
                componentList.Add(idForGroup, newGroupForBundle);
                AddComponent(idForGroup, true,false,false);
            }
            else {
                if (componentList.ContainsKey(suggestID))
                    componentList.Remove(suggestID);
                componentList.Add(suggestID, newGroupForBundle);
                AddComponent(suggestID, false, false,false);
            }
            
            // find the id of the just added group
            componentType componentAsGroupElement = null;
            string searchName;
            if (idForGroup == "" || idForGroup == null)
                searchName = suggestID;
            else
                searchName = idForGroup;

            ArrayList longestNameComp = new ArrayList();
            componentType longestComp = null;
            foreach (componentType mc in deploymentComponentList.Values) {
                if (mc.id.StartsWith(searchName))
                    longestNameComp.Add(mc);
            }

            if (longestNameComp.Count == 1)
                longestComp = (componentType) longestNameComp[0];
            else if (longestNameComp.Count > 1) {
                foreach (componentType ct in longestNameComp) {
                    if (longestComp == null)
                        longestComp = ct;
                    else if (longestComp.id.Length < ct.id.Length)
                        longestComp = ct;
                }
            }

            if (longestComp != null) {
                newGroup.GroupID = longestComp.type_id;
                newGroup.ID = longestComp.id;
                componentAsGroupElement = longestComp;
                groupsList.Add(newGroup.ID, newGroup);
                MoveComponent(longestComp, lowestX + (highestX - lowestX) / 2, lowestY + (highestY - lowestY) / 2);
                String groupColor = ini.IniReadValue("Layout", "groupcolor");
                if (groupColor.Equals(""))
                    groupColor = ACS.LayoutConstants.GROUPRECTANGLECOLOR;
                longestComp.TopRectangle.Fill = (Brush)bc.ConvertFrom(groupColor);
            }

            if (addToUndoStack) {
                CommandObject co = new CommandObject("Ungroup", componentAsGroupElement);
                undoStack.Push(co);
                redoStack.Clear();
            }

            /*
             * Process Channels where the source becomes a member of a group
             */
            foreach (channel ch in foundEdgeSourceChannels) {
                outputPortType outPort = null;
                foreach (object o in componentAsGroupElement.PortsList.Values) {
                    if ((o is outputPortType) && (((outputPortType)o).refs.componentID == ch.source.component.id) && (((outputPortType)o).refs.portID == ch.source.port.id)) {
                        outPort = (outputPortType)o;
                    }
                }
                if (outPort == null)
                    continue;

                /*
                 * add channel with source = newgroup and old target
                 */
                channel groupChannel = new channel();

                groupChannel.id = NewIdForGroupChannel();
                groupChannel.source.component.id = outPort.ComponentId;
                groupChannel.source.port.id = ch.source.component.id + "_" + ch.source.port.id;
                groupChannel.target.component.id = ch.target.component.id;
                groupChannel.target.port.id = ch.target.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel);

                groupChannel.Line.Y1 = 100;
                groupChannel.Line.X1 = 100;

                groupChannel.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 20;

                groupChannel.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[ch.target.component.id]).PortsList[ch.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[ch.target.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[ch.target.component.id]).PortsList[ch.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[ch.target.component.id]).ComponentCanvas);


                Canvas.SetZIndex(groupChannel.Line, Canvas.GetZIndex(groupChannel.Line) + 1000);


                /*
                 * add channel with source = newgroup and old target
                 */
                if (ch.GroupOriginalTarget == null)
                    continue;
                /*
                 *   add channel with source = newgroup and targets group original
                 */

                channel groupChannel1 = new channel();
                groupChannel1.id = NewIdForGroupChannel();
                groupChannel1.source.component.id = outPort.ComponentId;
                groupChannel1.source.port.id = ch.source.component.id + "_" + ch.source.port.id;
                groupChannel1.target.component.id = ch.target.component.id;
                groupChannel1.target.port.id = ch.target.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel1);

                groupChannel1.Line.Y1 = 100;
                groupChannel1.Line.X1 = 100;

                groupChannel1.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[groupChannel1.source.component.id]).PortsList[groupChannel1.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel1.source.component.id]).ComponentCanvas) + 5;
                groupChannel1.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[groupChannel1.source.component.id]).PortsList[groupChannel1.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel1.source.component.id]).ComponentCanvas) + 20;

                groupChannel1.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[ch.target.component.id]).PortsList[ch.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[ch.target.component.id]).ComponentCanvas) + 5;
                groupChannel1.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[ch.target.component.id]).PortsList[ch.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[ch.target.component.id]).ComponentCanvas);

                Canvas.SetZIndex(groupChannel1.Line, Canvas.GetZIndex(groupChannel1.Line) + 1000);

            }
            // Sort Portslist
            

            /*
            * Process Channels where the target becomes a member of a group
            */
            foreach (channel ch in foundEdgeTargetChannels) {
                inputPortType inPort = null;
                foreach (object o in componentAsGroupElement.PortsList.Values) {
                    if ((o is inputPortType) && (((inputPortType)o).refs.componentID == ch.target.component.id) && (((inputPortType)o).refs.portID == ch.target.port.id)) {
                        inPort = (inputPortType)o;
                    }
                }
                if (inPort == null)
                    continue;

                /*
                 * add channel with source = newgroup and old target
                 */
                channel groupChannel = new channel();

                groupChannel.id = NewIdForGroupChannel();
                groupChannel.source.component.id = ch.source.component.id;
                groupChannel.source.port.id = ch.source.port.id;
                groupChannel.target.component.id = inPort.ComponentId;
                groupChannel.target.port.id = ch.target.component.id + "_" + ch.target.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel);


                groupChannel.Line.Y1 = 100;
                groupChannel.Line.X1 = 100;

                groupChannel.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[ch.source.component.id]).PortsList[ch.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[ch.source.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[ch.source.component.id]).PortsList[ch.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[ch.source.component.id]).ComponentCanvas) + 20;


                groupChannel.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas);



                Canvas.SetZIndex(groupChannel.Line, Canvas.GetZIndex(groupChannel.Line) + 1000);


                /*
                 * add channel with source = newgroup and old target
                 */
                if (ch.GroupOriginalSource == null)
                    continue;
                /*
                 *   add channel with source = newgroup and targets group original
                 */

                channel groupChannel1 = new channel();
                groupChannel1.id = NewIdForGroupChannel();

                groupChannel1.source.component.id = ch.source.component.id;
                groupChannel1.source.port.id = ch.source.port.id;

                groupChannel1.target.component.id = inPort.ComponentId;
                groupChannel1.target.port.id = ch.target.component.id + "_" + ch.target.port.id;

                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel1);

                groupChannel1.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[ch.source.component.id]).PortsList[ch.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[ch.source.component.id]).ComponentCanvas) + 5;
                groupChannel1.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[ch.source.component.id]).PortsList[ch.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[ch.source.component.id]).ComponentCanvas);


                groupChannel1.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[groupChannel1.target.component.id]).PortsList[groupChannel1.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel1.target.component.id]).ComponentCanvas) + 5;
                groupChannel1.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[groupChannel1.target.component.id]).PortsList[groupChannel1.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel1.target.component.id]).ComponentCanvas) + 20;


                Canvas.SetZIndex(groupChannel1.Line, Canvas.GetZIndex(groupChannel1.Line) + 1000);

            }



            // hide the eventchannels within a group
            foreach (eventChannelLine ecl in selectedGroupEventChannelList) {
                ecl.Line.Visibility = System.Windows.Visibility.Hidden;
                newGroup.AddedEventChannelsList.AddLast(ecl);
            }

            // hide all eventchannels where the group is a listener
            foreach (eventChannelLine ecl in selectedGroupEdgeListenerEventChannelList) {
                ecl.Line.Visibility = System.Windows.Visibility.Hidden;
                newGroup.AddedEventChannelsList.AddLast(ecl);
            }

            // hide all eventchannels where the group is a trigger
            foreach (eventChannelLine ecl in selectedGroupEdgeTriggerEventChannelList) {
                ecl.Line.Visibility = System.Windows.Visibility.Hidden;
                newGroup.AddedEventChannelsList.AddLast(ecl);
            }

            // Targets
            foreach (eventChannelLine ecl in selectedGroupEdgeListenerEventChannelList) {

                List<eventChannel> ecList = GetEventChannelsFromLine(ecl);
                foreach (eventChannel ec in ecList) {
                    string targetEvent = ec.targets.target.component.id + "_" + ec.targets.target.eventPort.id;
                    if (eventChannelExists(compName, targetEvent, ecl.TriggerComponentId, ec.sources.source.eventPort.id))
                        continue;
                    eventChannel tmpEC = new eventChannel();
                    tmpEC.sources.source.component.id = ecl.TriggerComponentId;
                    tmpEC.sources.source.eventPort.id = ec.sources.source.eventPort.id;
                    tmpEC.targets.target.component.id = compName;
                    tmpEC.targets.target.eventPort.id = targetEvent;
                    tmpEC.id = compName + "_" + ecl.TriggerComponentId + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                    tmpEC.GroupOriginalTarget = ec.targets.target;
                    if (EventChannelHasGroupSource(ec))
                        tmpEC.GroupOriginalSource = ec.GroupOriginalSource;
                    eventChannelList.Add(tmpEC);
                }

                if (eventChannelLineExists(compName, ecl.TriggerComponentId))
                    continue;

                eventChannelLine groupEC = new eventChannelLine();

                double x = lowestX + (highestX - lowestX) / 2;
                double y = lowestY + (highestY - lowestY) / 2;

                groupEC.Line.X1 = ecl.Line.X1;
                groupEC.Line.Y1 = ecl.Line.Y1;

                groupEC.Line.X2 = x + componentAsGroupElement.ComponentCanvas.Width / 2 - 18;
                groupEC.Line.Y2 = y + componentAsGroupElement.ComponentCanvas.Height - 4;

                Canvas.SetZIndex(groupEC.Line, -1001);
                groupEC.TriggerComponentId = ecl.TriggerComponentId;
                groupEC.ListenerComponentId = compName;
                groupEC.HasGroupTarget = true;
                groupEC.HasGroupSource = EventChannelLineHasGroupSource(ecl);
                AddEventChannelCommand(groupEC, false);
                canvas.Children.Add(groupEC.Line);
            }
            // Sources
            foreach (eventChannelLine ecl in selectedGroupEdgeTriggerEventChannelList) {
                //Copy eventChannels
                List<eventChannel> ecList = GetEventChannelsFromLine(ecl);
                foreach (eventChannel ec in ecList) {
                    eventChannel tmpEC = new eventChannel();
                    Console.WriteLine("compName");
                    tmpEC.sources.source.component.id = compName;
                    tmpEC.sources.source.eventPort.id = ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id;
                    tmpEC.targets.target.component.id = ecl.ListenerComponentId;
                    tmpEC.targets.target.eventPort.id = ec.targets.target.eventPort.id;
                    tmpEC.id = compName + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                    tmpEC.GroupOriginalSource = ec.sources.source;
                    if (EventChannelHasGroupTarget(ec))
                        tmpEC.GroupOriginalTarget = ec.GroupOriginalTarget;
                    eventChannelList.Add(tmpEC);
                }

                if (eventChannelLineExists(ecl.ListenerComponentId, compName))
                    continue;

                // Add EventchannelLine
                eventChannelLine groupEC = new eventChannelLine();

                double x = lowestX + (highestX - lowestX) / 2;
                double y = lowestY + (highestY - lowestY) / 2;

                groupEC.Line.X1 = x + componentAsGroupElement.ComponentCanvas.Width / 2 + 18;
                groupEC.Line.Y1 = y + componentAsGroupElement.ComponentCanvas.Height - 4;

                groupEC.Line.X2 = ecl.Line.X2;
                groupEC.Line.Y2 = ecl.Line.Y2;

                Canvas.SetZIndex(groupEC.Line, -1001);
                groupEC.TriggerComponentId = compName;
                groupEC.ListenerComponentId = ecl.ListenerComponentId;
                groupEC.HasGroupSource = true;
                groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(ecl);
                AddEventChannelCommand(groupEC, false);
                canvas.Children.Add(groupEC.Line);
            }
            DeleteDanglingChannels();
            HideGroupChannels();
            // Delete all Eventchannels where either the source or the target does not exist anymore
            DeleteDanglingEventChannelLines();
            DeleteDanglingEventChannels();
            deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));

            // store the group in the deployment model          
            if (storeGroup) {
                group[] tempGroups = deploymentModel.groups;

                if (tempGroups == null) {
                    tempGroups = new group[1];
                }
                else {
                    Array.Resize(ref tempGroups, deploymentModel.groups.Count() + 1);
                }
                deploymentModel.groups = tempGroups;

                group groupForDeployment = new group();
                groupForDeployment.id = compName;
                groupForDeployment.componentId = new string[selectedGroupComponentList.Count];
                for (int i = 0; i < selectedGroupComponentList.Count; i++) {
                    groupForDeployment.componentId[i] = ((componentType)selectedGroupComponentList[i]).id;
                }
                deploymentModel.groups[deploymentModel.groups.Count() - 1] = groupForDeployment;
            }
            ClearSelectedComponentList();
            ClearSelectedChannelList();
            ClearSelectedEventChannelList();
            //AddSelectedComponent(deploymentComponentList[compName]);
            Console.WriteLine("Compname is:" + compName);
            return compName;
        }


        /// <summary>
        /// Calling the DoUngroup function
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void UngroupButton_Click(object sender, RoutedEventArgs e) {
            DoUngrouping(true);
        }

        /// <summary>
        /// Ungrouping of the selected group
        /// </summary>
        /// <param name="addToUndoStack">Desition, if the undo-operation should be put on the undo-stack</param>
        private void DoUngrouping(bool addToUndoStack) {
            ArrayList componentsToRemove = new ArrayList();
            ArrayList groups = new ArrayList();
            ArrayList groupNames = new ArrayList();
            foreach (componentType mc in selectedComponentList) {
                if (mc.ComponentType == ACS2.componentTypeDataTypes.group) {
                    groupComponent undoGroup = groupsList[mc.id];
                    groupNames.Add(mc.id);
                    groups.Add(undoGroup.AddedComponentList);
                    foreach (channel unhideChannel in undoGroup.AddedChannelsList) {
                        if (unhideChannel.Line.Visibility == System.Windows.Visibility.Hidden)
                            unhideChannel.Line.Visibility = System.Windows.Visibility.Visible;
                    }


                    foreach (eventChannelLine unhideEventChannel in undoGroup.AddedEventChannelsList) {
                        componentType source = GetEventChannelLineSource(unhideEventChannel);
                        componentType target = GetEventChannelLineTarget(unhideEventChannel);
                        if (source != null && target != null) {
                            if (source.ComponentCanvas.Visibility != System.Windows.Visibility.Visible) {
                                groupComponent gc = GetParentGroup(source);
                                componentType groupCt = GetGroupComponent(gc);
                                // Add EventchannelLine


                                // copy eventchannels
                                List<eventChannel> ecList = GetEventChannelsFromLine(unhideEventChannel);
                                foreach (eventChannel ec in ecList) {
                                    eventChannel tmpEC = new eventChannel();
                                    if (eventChannelExists(ec.targets.target.component.id, ec.targets.target.eventPort.id, groupCt.id, ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id))
                                        continue;
                                    tmpEC.sources.source.component.id = groupCt.id;
                                    tmpEC.sources.source.eventPort.id = ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id;

                                    tmpEC.targets.target.component.id = ec.targets.target.component.id;
                                    tmpEC.targets.target.eventPort.id = ec.targets.target.eventPort.id;

                                    tmpEC.id = groupCt.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                                    if (EventChannelHasGroupSource(ec))
                                        tmpEC.GroupOriginalSource = ec.sources.source;
                                    if (EventChannelHasGroupTarget(ec))
                                        tmpEC.GroupOriginalTarget = ec.GroupOriginalTarget;

                                    eventChannelList.Add(tmpEC);
                                }
                                if (!eventChannelLineExists(unhideEventChannel.ListenerComponentId, gc.ID)) {
                                    eventChannelLine groupEC = new eventChannelLine();


                                    componentType sourceComp = deploymentComponentList[groupCt.id];
                                    componentType targetComp = deploymentComponentList[unhideEventChannel.ListenerComponentId];


                                    groupEC.Line.X1 = Canvas.GetLeft(sourceComp.ComponentCanvas) + sourceComp.ComponentCanvas.Width / 2 + 18;
                                    groupEC.Line.Y1 = Canvas.GetTop(sourceComp.ComponentCanvas) + sourceComp.ComponentCanvas.Height - 4;

                                    groupEC.Line.X2 = Canvas.GetLeft(targetComp.ComponentCanvas) + targetComp.ComponentCanvas.Width / 2 - 18;
                                    groupEC.Line.Y2 = Canvas.GetTop(targetComp.ComponentCanvas) + targetComp.ComponentCanvas.Height - 4;

                                    Canvas.SetZIndex(groupEC.Line, -1001);
                                    groupEC.TriggerComponentId = gc.ID;
                                    groupEC.ListenerComponentId = unhideEventChannel.ListenerComponentId;
                                    groupEC.HasGroupSource = EventChannelLineHasGroupSource(unhideEventChannel);
                                    groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(unhideEventChannel);
                                    AddEventChannelCommand(groupEC, false);
                                    canvas.Children.Add(groupEC.Line);
                                }
                            }
                            if (target.ComponentCanvas.Visibility != System.Windows.Visibility.Visible) {
                                groupComponent gc = GetParentGroup(target);
                                componentType groupCt = GetGroupComponent(gc);
                                // Add EventchannelLine


                                // copy eventchannels
                                List<eventChannel> ecList = GetEventChannelsFromLine(unhideEventChannel);
                                foreach (eventChannel ec in ecList) {

                                    if (eventChannelExists(groupCt.id, ec.targets.target.component.id + "_" + ec.targets.target.eventPort.id, ec.sources.source.component.id, ec.sources.source.eventPort.id))
                                        continue;
                                    eventChannel tmpEC = new eventChannel();

                                    tmpEC.sources.source.component.id = ec.sources.source.component.id;
                                    tmpEC.sources.source.eventPort.id = ec.sources.source.eventPort.id;

                                    tmpEC.targets.target.component.id = groupCt.id;
                                    tmpEC.targets.target.eventPort.id = ec.targets.target.component.id + "_" + ec.targets.target.eventPort.id;


                                    tmpEC.id = groupCt.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                                    if (EventChannelHasGroupSource(ec))
                                        tmpEC.GroupOriginalSource = ec.sources.source;
                                    if (EventChannelHasGroupTarget(ec))
                                        tmpEC.GroupOriginalTarget = ec.GroupOriginalTarget;

                                    eventChannelList.Add(tmpEC);
                                }
                                if (!eventChannelLineExists(gc.ID, unhideEventChannel.TriggerComponentId)) {

                                    eventChannelLine groupEC = new eventChannelLine();

                                    componentType sourceComp = deploymentComponentList[unhideEventChannel.TriggerComponentId];
                                    componentType targetComp = deploymentComponentList[groupCt.id];


                                    groupEC.Line.X1 = Canvas.GetLeft(sourceComp.ComponentCanvas) + sourceComp.ComponentCanvas.Width / 2 + 18;
                                    groupEC.Line.Y1 = Canvas.GetTop(sourceComp.ComponentCanvas) + sourceComp.ComponentCanvas.Height - 4;

                                    groupEC.Line.X2 = Canvas.GetLeft(targetComp.ComponentCanvas) + targetComp.ComponentCanvas.Width / 2 - 18;
                                    groupEC.Line.Y2 = Canvas.GetTop(targetComp.ComponentCanvas) + targetComp.ComponentCanvas.Height - 4;


                                    Canvas.SetZIndex(groupEC.Line, -1001);
                                    groupEC.TriggerComponentId = unhideEventChannel.TriggerComponentId;
                                    groupEC.ListenerComponentId = gc.ID;
                                    groupEC.HasGroupSource = EventChannelLineHasGroupSource(unhideEventChannel);
                                    groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(unhideEventChannel);
                                    AddEventChannelCommand(groupEC, false);
                                    canvas.Children.Add(groupEC.Line);
                                }
                            }
                            if (target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                                source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible)
                                unhideEventChannel.Line.Visibility = System.Windows.Visibility.Visible;
                            continue;
                        }
                    }
                    foreach (componentType unhideComponent in undoGroup.AddedComponentList) {
                        unhideComponent.ComponentCanvas.Visibility = System.Windows.Visibility.Visible;
                    }
                    componentsToRemove.Add(mc);

                    groupsList.Remove(undoGroup.ID);


                    // remove group from deployment
                    List<group> groupsInDeployment = deploymentModel.groups.ToList();
                    group groupToRemove = null;
                    foreach (group gr in groupsInDeployment) {
                        if (gr.id == undoGroup.ID) {
                            groupToRemove = gr;
                            break;
                        }
                    }
                    groupsInDeployment.Remove(groupToRemove);
                    deploymentModel.groups = groupsInDeployment.ToArray();

                }
            }
            double maxLeftOut = 0;
            double maxRightOut = 0;
            double maxTopOut = 0;
            double maxBottomOut = 0;
            Size renderSize = canvas.RenderSize;
            foreach (componentType ct in selectedComponentList) {
                if (ct.ComponentType == ACS2.componentTypeDataTypes.group)
                    continue;
                double leftPos = Canvas.GetLeft(ct.ComponentCanvas);
                if (leftPos < 0)
                    if (leftPos < maxLeftOut)
                        maxLeftOut = leftPos;
                double topPos = Canvas.GetTop(ct.ComponentCanvas);
                if (topPos < 0)
                    if (topPos < maxTopOut)
                        maxTopOut = topPos;
                double rightPos = Canvas.GetLeft(ct.ComponentCanvas) + ct.ComponentCanvas.Width;
                if (rightPos > renderSize.Width)
                    if (rightPos > maxRightOut)
                        maxRightOut = rightPos;
                double bottomPos = Canvas.GetTop(ct.ComponentCanvas) + ct.ComponentCanvas.Height;
                if (bottomPos > renderSize.Height)
                    if (bottomPos > maxBottomOut)
                        maxBottomOut = bottomPos;
            }
            if (maxLeftOut != 0 && maxRightOut == 0) { // one component is left out
                //move all components to the right
                foreach (componentType ct in selectedComponentList) {
                    if (ct.ComponentType == ACS2.componentTypeDataTypes.group)
                        continue;
                    MoveComponent(ct, (int)Canvas.GetLeft(ct.ComponentCanvas) - (int)maxLeftOut, (int)Canvas.GetTop(ct.ComponentCanvas));
                }
            }

            if (maxRightOut != 0 && maxLeftOut == 0) { // one component is left out
                //move all components to the right
                foreach (componentType ct in selectedComponentList) {
                    if (ct.ComponentType == ACS2.componentTypeDataTypes.group)
                        continue;
                    MoveComponent(ct, (int)Canvas.GetLeft(ct.ComponentCanvas) - (int) (maxRightOut - canvas.RenderSize.Width), (int)Canvas.GetTop(ct.ComponentCanvas));
                }
            }

            if (maxBottomOut != 0 && maxTopOut == 0) { // one component is left out
                //move all components to the right
                foreach (componentType ct in selectedComponentList) {
                    if (ct.ComponentType == ACS2.componentTypeDataTypes.group)
                        continue;
                    MoveComponent(ct, (int) Canvas.GetLeft(ct.ComponentCanvas) , (int)Canvas.GetTop(ct.ComponentCanvas) - (int)(maxBottomOut - canvas.RenderSize.Height));
                }
            }
            
            if (maxTopOut != 0 && maxBottomOut == 0) { // one component is left out
                //move all components to the right
                foreach (componentType ct in selectedComponentList) {
                    if (ct.ComponentType == ACS2.componentTypeDataTypes.group)
                        continue;
                    MoveComponent(ct, (int)Canvas.GetLeft(ct.ComponentCanvas), (int)Canvas.GetTop(ct.ComponentCanvas) - (int)maxTopOut);
                }
            }

            foreach (eventChannelLine ecl in eventChannelLinesList) {
                if (ecl.Line.Visibility != System.Windows.Visibility.Visible) {
                    componentType source = GetEventChannelLineSource(ecl);
                    componentType target = GetEventChannelLineTarget(ecl);
                    if (source == null || target == null)
                        continue;
                    if (source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                        target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible)
                        ecl.Line.Visibility = System.Windows.Visibility.Visible;
                }
            }

            foreach (channel ch in deploymentChannelList.Values) {
                if (ch.Line.Visibility != System.Windows.Visibility.Visible) {
                    if (ch.Line.Visibility == System.Windows.Visibility.Collapsed)
                        continue;
                    bool sourceVisible = false;
                    bool targetVisible = false;
                    if (deploymentComponentList.ContainsKey(ch.source.component.id)) {
                        componentType source = deploymentComponentList[ch.source.component.id];
                        sourceVisible = source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible;
                    }
                    if (deploymentComponentList.ContainsKey(ch.target.component.id)) {
                        componentType target = deploymentComponentList[ch.target.component.id];
                        targetVisible = target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible;
                    }
                    if (!sourceVisible || !targetVisible)
                        continue;
                    ch.Line.Visibility = System.Windows.Visibility.Visible;
                }
            }

            
            foreach (componentType mc in componentsToRemove) {
                DeleteComponent(mc);
                componentList.Remove(mc.type_id);
                selectedComponentList.Remove(mc);
            }
            HideGroupChannels();
            DeleteDanglingChannels();
            DeleteDanglingEventChannelLines();
            DeleteDanglingEventChannels();
            AddMissingEventChannels();
            if (addToUndoStack) {
                CommandObject co = new CommandObject("Group", groups);
                foreach (string name in groupNames) {
                    co.Parameter.Add(name);
                }
                undoStack.Push(co);
                redoStack.Clear();
            }
            ClearSelectedChannelList();
            ClearSelectedEventChannelList();
            ClearSelectedComponentList();
            foreach (LinkedList<componentType> ct in groups) {
                foreach (componentType c in ct) {
                    if (c.ComponentType != ACS2.componentTypeDataTypes.group)
                        AddSelectedComponent(c);
                }
            }
        }

        /// <summary>
        /// Hides all channels where either the source or the target is no visible, because it's within a group
        /// </summary>
        private void HideGroupChannels() {
            foreach (channel c in deploymentChannelList.Values) {
                bool sourceVisible = false;
                bool targetVisible = false;
                if (deploymentComponentList.ContainsKey(c.source.component.id)) {
                    componentType source = deploymentComponentList[c.source.component.id];
                    if (source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible)
                        sourceVisible = true;
                }
                if (deploymentComponentList.ContainsKey(c.target.component.id)) {
                    componentType target = deploymentComponentList[c.target.component.id];
                    if (target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible)
                        targetVisible = true;
                }
                if (!sourceVisible || !targetVisible) {
                    c.Line.Visibility = System.Windows.Visibility.Hidden;
                }
            }
        }

        /// <summary>
        /// Removes all channels which have a non existing source or taget
        /// </summary>
        private void DeleteDanglingChannels() {
            ArrayList channelsToDelete = new ArrayList();
            foreach (channel c in deploymentChannelList.Values) {
                bool sourceExists = false;
                bool targetExists = false;
                if (deploymentComponentList.ContainsKey(c.source.component.id)) {
                    sourceExists = true;
                }
                if (deploymentComponentList.ContainsKey(c.target.component.id)) {
                    targetExists = true;
                }
                if (!sourceExists || !targetExists) {
                    channelsToDelete.Add(c);
                }
            }
            foreach (channel c in channelsToDelete) {
                DeleteChannel(c);
            }
        }

        /// <summary>
        /// Deletes eventchannel lines and channel lines of the canvas that have no counterpart object in the 
        /// deploymentlists
        /// </summary>
        private void DeleteDanglineLines() {
            ArrayList lines = new ArrayList();
            foreach (object o in canvas.Children) {
                if (o is System.Windows.Shapes.Line) {
                    System.Windows.Shapes.Line l = (System.Windows.Shapes.Line)o;
                    bool found = false;
                    foreach (channel c in deploymentChannelList.Values) {
                        if (c.Line == l) {
                            found = true;
                            break;
                        }
                    }
                    foreach (eventChannelLine c in eventChannelLinesList) {
                        if (found)
                            break;
                        if (c.Line == l) {
                            found = true;
                            break;
                        }
                    }
                    if (found == false) {
                        Console.WriteLine("Got a line i could not find");
                        lines.Add(l);
                    }
                }
            }
            foreach (System.Windows.Shapes.Line line in lines)
                canvas.Children.Remove(line);
        }

        /// <summary>
        /// Returns the componenttype which includes an event matching the given eventId
        /// </summary>
        /// <param name="eventId">the name of the event which should get looked for in the components</param>
        /// <returns>the component which holds the given event, null if there is no  such component</returns>
        private componentType GetComponentTypeFromEventString(string eventId) {
            ArrayList matchedComponents = new ArrayList();
            foreach (componentType ct in deploymentComponentList.Values) {
                if (eventId.StartsWith(ct.id))
                    matchedComponents.Add(ct);
            }
            componentType longestNameComp = null;
            for (int i = 0; i < matchedComponents.Count; i++) {
                if (i == 0) {
                    longestNameComp = (componentType)matchedComponents[i];
                    continue;
                }
                componentType checkComp = (componentType)matchedComponents[i];
                if (checkComp.id.Length > longestNameComp.id.Length)
                    longestNameComp = checkComp;
            }
            return longestNameComp;
        }

        /// <summary>
        /// Returns the component corresponding to the given groupComponent
        /// </summary>
        /// <param name="gc"></param>
        /// <returns>componenttype of the given groupComponent</returns>
        private componentType GetGroupComponent(groupComponent gc) {
            foreach (componentType ct in deploymentComponentList.Values) {
                if (ct.id.Equals(gc.ID))
                    return ct;
            }
            return null;
        }

        /// <summary>
        /// Checks if the given componentType is a member of a group. If this is the case the groupComponent will be returned
        /// otherwise null will be returned.
        /// </summary>
        /// <param name="ct">Componenttype to check if it's a member of a group</param>
        /// <returns>groupcomponent where the given componenttype is a member of</returns>
        private groupComponent GetParentGroup(componentType ct) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.AddedComponentList.Contains(ct))
                    return gc;
            }
            return null;
        }


        /// <summary>
        /// Whenever eventchannels are connected to groups, new eventchannels have to be added to the model
        /// to all components which correspond to that new eventchannel
        /// 
        /// Case 1:
        ///   Channel between Group (source) and Component (target):
        ///     --> a new channel has to be added to the groups source component and the target component.
        /// Case 2:
        ///   Channel between Component (source) and Group (target):
        ///     --> a new channel has to be added to the source and the groups source component.
        /// Case 3:
        ///   Channel between Group1 (source) and Group2 (source):
        ///     --> a new channel has to be added to the group1 original source and group2
        ///     --> a new channel has to be added to the group1 and the group2 original source
        ///     --> a new channel has to be added to the group1 original source and group2 original source
        /// </summary>
        private void AddMissingEventChannels() {
            ArrayList eventChannelsToAdd = new ArrayList();
            foreach (eventChannel ec in eventChannelList) {
                componentType source = GetEventChannelSource(ec);
                componentType target = GetEventChannelTarget(ec);
                // check normal case
                if (!eventChannelLineExists(target.id, source.id)) {

                    if (source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                        target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                        eventChannelLine groupEC = new eventChannelLine();
                        Canvas.SetZIndex(groupEC.Line, -1001);

                        groupEC.Line.X1 = Canvas.GetLeft(source.ComponentCanvas) + source.ComponentCanvas.Width / 2 + 18;
                        groupEC.Line.Y1 = Canvas.GetTop(source.ComponentCanvas) + +source.ComponentCanvas.Height - 4;

                        groupEC.Line.X2 = Canvas.GetLeft(target.ComponentCanvas) + target.ComponentCanvas.Width / 2 - 18;
                        groupEC.Line.Y2 = Canvas.GetTop(target.ComponentCanvas) + +target.ComponentCanvas.Height - 4;

                        groupEC.TriggerComponentId = source.id;
                        groupEC.ListenerComponentId = target.id;
                        groupEC.HasGroupSource = EventChannelLineHasGroupSource(ec);
                        groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(ec);
                        AddEventChannelCommand(groupEC, false);
                        canvas.Children.Add(groupEC.Line);
                    }
                }
                // check if the source is within a group which needs an eventchannel
                groupComponent sourceGroup = GetParentGroup(source);
                if (sourceGroup != null) {

                    componentType groupComp = deploymentComponentList[sourceGroup.ID];
                    if (target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                        groupComp.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                        if (!eventChannelLineExists(target.id, sourceGroup.ID)) {
                            eventChannelLine groupEC = new eventChannelLine();
                            Canvas.SetZIndex(groupEC.Line, -1001);

                            groupEC.Line.X1 = Canvas.GetLeft(groupComp.ComponentCanvas) + groupComp.ComponentCanvas.Width / 2 + 18;
                            groupEC.Line.Y1 = Canvas.GetTop(groupComp.ComponentCanvas) + +groupComp.ComponentCanvas.Height - 4;


                            groupEC.Line.X2 = Canvas.GetLeft(target.ComponentCanvas) + target.ComponentCanvas.Width / 2 - 18;
                            groupEC.Line.Y2 = Canvas.GetTop(target.ComponentCanvas) + target.ComponentCanvas.Height - 4;


                            groupEC.TriggerComponentId = sourceGroup.ID;
                            groupEC.ListenerComponentId = target.id;
                            groupEC.HasGroupSource = EventChannelLineHasGroupSource(ec);
                            groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(ec);
                            AddEventChannelCommand(groupEC, false);
                            canvas.Children.Add(groupEC.Line);
                        }

                        if (!eventChannelExists(target.id, ec.targets.target.eventPort.id, groupComp.id, ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id)) {
                            eventChannel tmpEC = new eventChannel();

                            tmpEC.sources.source.component.id = groupComp.id;
                            tmpEC.sources.source.eventPort.id = ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id;

                            tmpEC.targets.target.component.id = target.id;
                            tmpEC.targets.target.eventPort.id = ec.targets.target.eventPort.id;

                            tmpEC.id = groupComp.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                            tmpEC.GroupOriginalSource = ec.sources.source;
                            eventChannelsToAdd.Add(tmpEC);
                        }
                    }
                }
                // check if the target is within a group which needs an eventchannel
                groupComponent targetGroup = GetParentGroup(target);
                if (targetGroup != null) {
                    componentType groupComp = deploymentComponentList[targetGroup.ID];
                    if (source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                        groupComp.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                        if (!eventChannelLineExists(targetGroup.ID, source.id)) {
                            eventChannelLine groupEC = new eventChannelLine();
                            Canvas.SetZIndex(groupEC.Line, -1001);


                            groupEC.Line.X2 = Canvas.GetLeft(groupComp.ComponentCanvas) + groupComp.ComponentCanvas.Width / 2 - 18;
                            groupEC.Line.Y2 = Canvas.GetTop(groupComp.ComponentCanvas) + +groupComp.ComponentCanvas.Height - 4;


                            groupEC.Line.X1 = Canvas.GetLeft(source.ComponentCanvas) + source.ComponentCanvas.Width / 2 + 18;
                            groupEC.Line.Y1 = Canvas.GetTop(source.ComponentCanvas) + source.ComponentCanvas.Height - 4;


                            groupEC.TriggerComponentId = source.id;
                            groupEC.ListenerComponentId = targetGroup.ID;
                            groupEC.HasGroupSource = EventChannelLineHasGroupSource(ec);
                            groupEC.HasGroupTarget = EventChannelLineHasGroupTarget(ec);
                            AddEventChannelCommand(groupEC, false);
                            canvas.Children.Add(groupEC.Line);
                        }
                        if (!eventChannelExists(target.id, ec.targets.target.eventPort.id, groupComp.id, ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id)) {
                            eventChannel tmpEC = new eventChannel();

                            tmpEC.sources.source.component.id = source.id;
                            tmpEC.sources.source.eventPort.id = ec.sources.source.eventPort.id;
                            //tmpEC.sources.source.eventPort.id = ec.sources.source.component.id + "_" + ec.sources.source.eventPort.id;

                            tmpEC.targets.target.component.id = targetGroup.ID;
                            tmpEC.targets.target.eventPort.id = ec.targets.target.component.id + "_" + ec.targets.target.eventPort.id;


                            tmpEC.id = groupComp.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                            tmpEC.GroupOriginalTarget = ec.targets.target;
                            tmpEC.GroupOriginalSource = ec.GroupOriginalSource;
                            eventChannelsToAdd.Add(tmpEC);
                        }
                    }
                }
            }
            foreach (eventChannel ec in eventChannelsToAdd) {
                eventChannelList.Add(ec);
            }

        }


        /// <summary>
        /// Returns the source component of the given eventchannel
        /// </summary>
        /// <param name="ec">Eventchannel of which the source should be returned</param>
        /// <returns>Source of the given eventchannel</returns>
        private componentType GetEventChannelSource(eventChannel ec) {
            foreach (componentType ct in deploymentComponentList.Values)
                if (ct.id.Equals(ec.sources.source.component.id))
                    return ct;
            return null;
        }

        /// <summary>
        /// Returns the target component of the given eventchannel
        /// </summary>
        /// <param name="ec">Eventchannel of which the target should be returned</param>
        /// <returns>Target of the given eventchannel</returns>
        private componentType GetEventChannelTarget(eventChannel ec) {
            foreach (componentType ct in deploymentComponentList.Values)
                if (ct.id.Equals(ec.targets.target.component.id))
                    return ct;
            return null;
        }


        /// <summary>
        /// Returns the target component of the given eventchannelline
        /// </summary>
        /// <param name="ec">Eventchannelline of which the target should be returned</param>
        /// <returns>Target of the given eventchannel</returns>
        private componentType GetEventChannelLineTarget(eventChannelLine ec) {
            foreach (componentType ct in deploymentComponentList.Values)
                if (ct.id.Equals(ec.ListenerComponentId))
                    return ct;
            return null;
        }


        /// <summary>
        /// Returns the source component of the given eventchannelline
        /// </summary>
        /// <param name="ec">Eventchannelline of which the source should be returned</param>
        /// <returns>Source of the given eventchannel</returns>
        private componentType GetEventChannelLineSource(eventChannelLine ec) {
            foreach (componentType ct in deploymentComponentList.Values)
                if (ct.id.Equals(ec.TriggerComponentId))
                    return ct;
            return null;
        }

        /// <summary>
        /// check if there is already a groupchannel with same source and target
        /// </summary>
        /// <param name="groupChannel">channel which existencs should be checked</param>
        /// <returns></returns>
        private bool ChannelExists(channel groupChannel) {
            foreach (channel c in deploymentChannelList.Values) {
                if (c.source.component.id.Equals(groupChannel.source.component.id) &&
                    c.target.component.id.Equals(groupChannel.target.component.id) &&
                    c.source.port.id.Equals(groupChannel.source.port.id) &&
                    c.target.port.id.Equals(groupChannel.target.port.id))
                    return true;
            }
            return false;
        }


        /// <summary>
        /// Returns true if an EventChannelLine with the given listenerID and triggerID exists
        /// </summary>
        /// <param name="listenerComponentID">Name of the </param>
        /// <param name="triggerComponentID"></param>
        /// <returns></returns>
        private bool eventChannelLineExists(string listenerComponentID, string triggerComponentID) {
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                if (ecl.ListenerComponentId.Equals(listenerComponentID) && ecl.TriggerComponentId.Equals(triggerComponentID))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Returns true if an EventChannelLine with the given listenerID and triggerID exists
        /// </summary>
        /// <param name="listenerComponentID">Name of the </param>
        /// <param name="triggerComponentID"></param>
        /// <returns></returns>
        private bool eventChannelExists(string listenerComponentID, string listenerEvent, string triggerComponentID, string triggerEvent) {
            foreach (eventChannel ecl in eventChannelList) {
                if (ecl.targets.target.component.id.Equals(listenerComponentID) && ecl.sources.source.component.id.Equals(triggerComponentID))
                    if (ecl.targets.target.eventPort.id.Equals(listenerEvent) && ecl.sources.source.eventPort.id.Equals(triggerEvent))
                        return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the source of the eventchannelline exists
        /// </summary>
        /// <param name="ecl">eventchannelline to check</param>
        /// <returns>true if the source of the given eventchannelline exists, false otherise</returns>
        private bool EventChannelLineTriggerExists(eventChannelLine ecl) {
            return deploymentComponentList.ContainsKey(ecl.TriggerComponentId);
        }

        /// <summary>
        /// Check if the target of the eventchannelline exists
        /// </summary>
        /// <param name="ecl">eventchannelline to check</param>
        /// <returns>true if the target of the given eventchannelline exists, false otherise</returns>
        private bool EventChannelLineListenerExists(eventChannelLine ecl) {
            return deploymentComponentList.ContainsKey(ecl.ListenerComponentId);
        }

        /// <summary>
        /// Check if the source of the eventchannel exists
        /// </summary>
        /// <param name="ecl">eventchannel to check</param>
        /// <returns>true if the source of the given eventchannel exists, false otherise</returns>
        private bool EventChannelListenerExists(eventChannel ecl) {
            return deploymentComponentList.ContainsKey(ecl.targets.target.component.id);
        }

        /// <summary>
        /// Check if the target of the eventchannel exists
        /// </summary>
        /// <param name="ecl">eventchannel to check</param>
        /// <returns>true if the target of the given eventchannel exists, false otherise</returns>
        private bool EventChannelTriggerExists(eventChannel ecl) {
            return deploymentComponentList.ContainsKey(ecl.sources.source.component.id);
        }

        /// <summary>
        /// Check if the given eventchannelline has a group as target
        /// </summary>
        /// <param name="ec">eventchannelline for which the existence of a group target should get checked</param>
        /// <returns>true if the target of the given eventchannelline is a group, false otherwise</returns>
        private bool EventChannelLineHasGroupTarget(eventChannelLine ecl) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ecl.ListenerComponentId))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the given eventchannel has a group as target
        /// </summary>
        /// <param name="ec">eventchannel for which the existence of a group target should get checked</param>
        /// <returns>true if the target of the given eventchannel is a group, false otherwise</returns>
        private bool EventChannelLineHasGroupTarget(eventChannel ecl) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ecl.targets.target.component.id))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the given eventchannel has a group as target
        /// </summary>
        /// <param name="ec">eventchannel for which the existence of a group target should get checked</param>
        /// <returns>true if the target of the given eventchannel is a group, false otherwise</returns>
        private bool EventChannelLineHasGroupSource(eventChannel ecl) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ecl.sources.source.component.id))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the given eventchannelline has a group as source
        /// </summary>
        /// <param name="ec">eventchannelline for which the existence of a group source should get checked</param>
        /// <returns>true if the source of the given eventchannelline is a group, false otherwise</returns>
        private bool EventChannelLineHasGroupSource(eventChannelLine ecl) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ecl.TriggerComponentId))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the given eventchannel has a group as source
        /// </summary>
        /// <param name="ec">eventchannel for which the existence of a group source should get checked</param>
        /// <returns>true if the source of the given eventchannel is a group, false otherwise</returns>
        private bool EventChannelHasGroupSource(eventChannel ec) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ec.sources.source.component.id))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Check if the given eventchannel has a group as target
        /// </summary>
        /// <param name="ec">eventchannel for which the existence of a group target should get checked</param>
        /// <returns>true if the target of the given eventchannel is a group, false otherwise</returns>
        private bool EventChannelHasGroupTarget(eventChannel ec) {
            foreach (groupComponent gc in groupsList.Values) {
                if (gc.ID.Equals(ec.targets.target.component.id))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// Returns a list of all eventchannels which correspond to the given eventchannelline
        /// </summary>
        /// <param name="ecl">eventchannelline for which the eventchannels should be searched</param>
        /// <returns>A list of all eventchannels within the given eventchannelline</returns>
        private List<eventChannel> GetEventChannelsFromLine(eventChannelLine ecl) {
            List<eventChannel> ecList = new List<eventChannel>();
            foreach (eventChannel ec in eventChannelList) {
                string source = ec.sources.source.component.id;
                string target = ec.targets.target.component.id;
                if (ecl.ListenerComponentId.Equals(target) && ecl.TriggerComponentId.Equals(source))
                    ecList.Add(ec);
            }
            return ecList;
        }

        /// <summary>
        /// Deletes all eventchannellines which have a nonexisting source or target
        /// </summary>
        private void DeleteDanglingEventChannelLines() {
            ArrayList eChToDelete = new ArrayList();
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                if (!EventChannelLineListenerExists(ecl) || !EventChannelLineTriggerExists(ecl))
                    eChToDelete.Add(ecl);
            }
            foreach (eventChannelLine ecl in eChToDelete)
                DeleteEventChannelCommand(ecl);
        }


        /// <summary>
        /// all eventchannels which have a visible source and a visible target will be set visible
        /// </summary>
        private void ShowHiddenEventChannels() {
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                componentType source = GetEventChannelLineSource(ecl);
                componentType target = GetEventChannelLineTarget(ecl);
                if (source.ComponentCanvas.Visibility == System.Windows.Visibility.Visible &&
                    target.ComponentCanvas.Visibility == System.Windows.Visibility.Visible)
                    ecl.Line.Visibility = System.Windows.Visibility.Visible;
            }
        }

        /// <summary>
        /// Removes all eventchannels which have a nonexisting source or target
        /// </summary>
        private void DeleteDanglingEventChannels() {
            ArrayList eChToDelete = new ArrayList();
            foreach (eventChannel ecl in eventChannelList) {
                if (!EventChannelListenerExists(ecl) || !EventChannelTriggerExists(ecl))
                    eChToDelete.Add(ecl);
            }
            foreach (eventChannel ecl in eChToDelete)
                eventChannelList.Remove(ecl);
            deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
        }


        /// <summary>
        /// Save the selected group to a file, so that it can be reused
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveGroupButton_Click(object sender, RoutedEventArgs e) {
            groupComponent groupToSave = null;
            foreach (componentType mc in selectedComponentList) {
                if (mc.ComponentType == ACS2.componentTypeDataTypes.group) {
                    groupToSave = groupsList[mc.id];
                    break;
                }
            }
            if (groupToSave != null) {
                storageDialog = new StorageDialog();

                string[] filesInGroupsFolder;
                if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                    filesInGroupsFolder = Directory.GetFiles(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\groups\\", "*.agr");
                } else {
                    filesInGroupsFolder = Directory.GetFiles(AppDomain.CurrentDomain.BaseDirectory + "\\groups\\", "*.agr");
                }  
                foreach (string s in filesInGroupsFolder) {
                    storageDialog.filenameListbox.Items.Add(s.Substring(s.LastIndexOf('\\') + 1));
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.GroupStoreDialogTitle;
                storageDialog.listLabel.Content = Properties.Resources.GroupStoreDialogListLabel;
                storageDialog.filenameTextbox.Text = "NewGroup.agr";

                storageDialog.modelNameLabel.Content = Properties.Resources.GroupStoreDialogGroupName;

                storageDialog.Owner = this;
                storageDialog.ShowDialog();

                if (storageDialog.filenameTextbox.Text != null && storageDialog.filenameTextbox.Text != "") {
                    try {
                        model groupModelToSave;
                        if (groupToSave != null) {
                            ClearSelectedChannelList();
                            ClearSelectedEventChannelList();
                            ClearSelectedComponentList();

                            foreach (componentType componentInGroup in groupToSave.AddedComponentList) {
                                AddSelectedComponent(deploymentComponentList[componentInGroup.id]);
                            }

                            // make a submodel, containing all grouging relevant data

                            groupModelToSave = new model();
                            groupModelToSave.modelName = groupToSave.GroupID;
                            // insert all selected components to the model
                            LinkedList<componentType> t = new LinkedList<componentType>();
                            for (int i = 0; i < selectedComponentList.Count; i++) {
                                componentType ct = selectedComponentList.ElementAt(i);
                                t.AddLast(ct);
                            }
                            groupModelToSave.components = t.ToArray();
                            // adding a group element to save the aliases
                            groupModelToSave.groups = new group[1];
                            foreach (group groupToAdd in deploymentModel.groups) {
                                if (groupToAdd.id == groupToSave.ID) {
                                    groupModelToSave.groups[0] = groupToAdd;
                                    break;
                                }
                            }

                            //get all selected channels where the source and target components
                            //are also selected
                            LinkedList<channel> copyChannels = new LinkedList<channel>();
                            foreach (channel c in groupToSave.AddedChannelsList) {
                                bool sourceFound, targetFound;
                                sourceFound = targetFound = false;
                                foreach (componentType mc in groupModelToSave.components) {
                                    if (mc.id == c.source.component.id)
                                        sourceFound = true;
                                    if (mc.id == c.target.component.id)
                                        targetFound = true;
                                    if (sourceFound && targetFound)
                                        break;
                                }
                                if (sourceFound && targetFound)
                                    copyChannels.AddLast(c);
                            }

                            // Adding dummy channels to make the input and output ports of the group visible

                            componentType groupComponent = deploymentComponentList[groupToSave.ID];
                            int index = 0;
                            foreach (object o in groupComponent.ports) {
                                channel c = new channel();
                                c.id = "bindingveigl." + index;
                                if (o is inputPortType) {
                                    c.source.component.id = pasteDummyName;
                                    c.source.port.id = "out";
                                    c.target.component.id = ((inputPortType)o).refs.componentID;
                                    c.target.port.id = ((inputPortType)o).refs.portID;
                                } else {
                                    c.source.component.id = ((outputPortType)o).refs.componentID;
                                    c.source.port.id = ((outputPortType)o).refs.portID;
                                    c.target.component.id = pasteDummyName;
                                    c.target.port.id = "in";
                                }
                                copyChannels.AddLast(c);
                                index++;
                            }


                            groupModelToSave.channels = new channel[copyChannels.Count];
                            for (int i = 0; i < copyChannels.Count; i++)
                                groupModelToSave.channels[i] = copyChannels.ElementAt(i);

                            // get all selected Eventchannels

                            index = 0;
                            LinkedList<eventChannel> copyEventChannels = new LinkedList<eventChannel>();
                            LinkedList<EventListenerPort> foundEdgeListenerEvents = new LinkedList<EventListenerPort>();
                            LinkedList<EventTriggerPort> foundEdgeTriggerEvents = new LinkedList<EventTriggerPort>();
                            foreach (eventChannel ec in eventChannelList) {
                                // search for each event channel on the edge of the group element
                                if (!selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id])) {
                                    eventChannel newEc = new eventChannel();
                                    newEc.id = "eventbindingveigl." + index;
                                    newEc.sources.source.component.id = ec.sources.source.component.id;
                                    newEc.sources.source.eventPort.id = ec.sources.source.eventPort.id;
                                    newEc.targets.target.component.id = pasteDummyName;
                                    newEc.targets.target.eventPort.id = "eventlistener";
                                    index++;
                                    copyEventChannels.AddLast(newEc);
                                    // search for each event channel on the edge of the group element
                                } else if (selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && !selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id])) {
                                    eventChannel newEc = new eventChannel();
                                    newEc.id = "eventbindingveigl." + index;
                                    newEc.sources.source.component.id = pasteDummyName;
                                    newEc.sources.source.eventPort.id = "eventtrigger";
                                    newEc.targets.target.component.id = ec.targets.target.component.id;
                                    newEc.targets.target.eventPort.id = ec.targets.target.eventPort.id;
                                    index++;
                                    copyEventChannels.AddLast(newEc);
                                } else if ((selectedComponentList.Contains(deploymentComponentList[ec.targets.target.component.id]) && selectedComponentList.Contains(deploymentComponentList[ec.sources.source.component.id]))) {
                                    copyEventChannels.AddFirst(ec);
                                }
                            }

                            // Adding dummy eventchannels to make the input and output ports of the group visible
                            foreach (EventListenerPort elp in groupComponent. EventListenerList) {                                
                                eventChannel newEc = new eventChannel();
                                foreach (componentType mc in groupModelToSave.components) {
                                    if (elp.EventListenerId.StartsWith(mc.id)) {
                                        newEc.targets.target.component.id = mc.id;
                                        newEc.targets.target.eventPort.id = ((EventListenerPort)mc.EventListenerList[0]).EventListenerId;
                                        newEc.id = "eventbindingveigl." + index;
                                        newEc.sources.source.component.id = pasteDummyName;
                                        newEc.sources.source.eventPort.id = "eventtrigger";

                                        index++;
                                        copyEventChannels.AddLast(newEc);   
                                        break;
                                    }
                                }

                                        
                            }
                            foreach (EventTriggerPort etp in groupComponent.EventTriggerList) {
                                eventChannel newEc = new eventChannel();
                                foreach (componentType mc in groupModelToSave.components) {
                                    if (etp.EventTriggerId.StartsWith(mc.id)) {
                                        newEc.id = "eventbindingveigl." + index;
                                        newEc.sources.source.component.id = mc.id;
                                        newEc.sources.source.eventPort.id = ((EventTriggerPort)mc.EventTriggerList[0]).EventTriggerId;
                                        newEc.targets.target.component.id = pasteDummyName;
                                        newEc.targets.target.eventPort.id = "eventlistener";
                                        index++;
                                        copyEventChannels.AddLast(newEc);
                                        break;
                                    }
                                }
                            }


                            if (copyEventChannels.Count == 0) {
                                groupModelToSave.eventChannels = null;
                            } else {
                                groupModelToSave.eventChannels = copyEventChannels.ToArray();
                            }
                            groupModelToSave = CopyModel(groupModelToSave);

                            // write group stream
                            XmlSerializer x = new XmlSerializer(groupModelToSave.GetType());
                            string filename;
                            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                                filename = Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\groups\\" + storageDialog.filenameTextbox.Text;
                            } else {
                                filename = AppDomain.CurrentDomain.BaseDirectory + "\\groups\\" + storageDialog.filenameTextbox.Text;
                            }  
                            if (!filename.EndsWith(".agr")) {
                                filename += ".agr";
                            }
                            FileStream str = new FileStream(filename, FileMode.Create);
                            x.Serialize(str, groupModelToSave);
                            str.Close();

                            // add new group to list of groups
                            bool alreadyInMenu = false;
                            foreach (RibbonApplicationSplitMenuItem i in groupDropDown.Items) {
                                if (((string)i.CommandParameter) == filename) {
                                    alreadyInMenu = true;
                                    break;
                                }
                            }
                            if (!alreadyInMenu) {
                                RibbonApplicationSplitMenuItem i = new RibbonApplicationSplitMenuItem();
                                string header = filename.Substring(filename.LastIndexOf('\\') + 1);
                                i.Header = header.Substring(0, header.LastIndexOf('.'));
                                i.Click += AddGroupFromRibbonMenu;
                                i.CommandParameter = filename;
                                groupDropDown.Items.Add(i);

                            }

                        }
                    } catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.StoreModelOnAREError, Properties.Resources.GroupStoreErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            } else {
                MessageBox.Show(Properties.Resources.GroupStoreNoGroupSelected, Properties.Resources.GroupStoreErrorHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            }
        }

        /// <summary>
        /// Adding a dummy component to the model. It is needed to make connections, being needed during the grouping process to 
        /// establish the ports and event ports of the group
        /// </summary>
        /// <param name="dummyName">The name of the dummy component</param>
        private void AddDummyToModel(String dummyName) {
            // building a "new" model
            Asterics.ACS2.componentTypesComponentType newGroupForBundle = new ACS2.componentTypesComponentType();

            newGroupForBundle.type = new ACS2.componentType();
            newGroupForBundle.type.Value = ACS2.componentTypeDataTypes.special;
            newGroupForBundle.id = dummyName;
            object[] ports = new object[2];
            ACS2.inputPortType inPort = new ACS2.inputPortType();
            inPort.id = "in";
            inPort.dataType = ACS2.dataType.integer;
            ports[0] = inPort;
            ACS2.outputPortType outPort = new ACS2.outputPortType();
            outPort.id = "out";
            outPort.dataType = ACS2.dataType.integer;
            ports[1] = outPort;
            newGroupForBundle.ports = ports;
            ACS2.eventsTypeEventListenerPortType eventListener = new ACS2.eventsTypeEventListenerPortType();
            eventListener.id = "eventlistener";
            ACS2.eventsTypeEventTriggererPortType eventTrigger = new ACS2.eventsTypeEventTriggererPortType();
            eventTrigger.id = "eventtrigger";
            object[] events = new object[2];
            events[0] = eventListener;
            events[1] = eventTrigger;
            newGroupForBundle.events = events;

            newGroupForBundle.InitGraphPorts(dummyName);
            if (!componentList.ContainsKey(dummyName))
                componentList.Add(dummyName, newGroupForBundle);
            AddComponent(dummyName, true, false,false);
        }

        /// <summary>
        /// Remove the dummy component from the model. It is needed to make connections, being needed during the grouping process to 
        /// establish the ports and event ports of the group
        /// </summary>
        /// <param name="dummyName">The name of the dummy component</param>
        private void RemoveDummyFromModel(String dummyName) {
            if (!deploymentComponentList.ContainsKey(dummyName))
                return;
            componentType mc = deploymentComponentList[dummyName];
            // delete all eventchannels from the mc
            // delete the eventChannels and therefore the events
            eventChannelLine eCL;
            for (int index = eventChannelLinesList.Count - 1; index >= 0; index--) {
                eCL = (eventChannelLine)eventChannelLinesList[index];
                if ((eCL.TriggerComponentId == mc.id) || (eCL.ListenerComponentId == mc.id)) {
                    focusedEventChannel = eCL;
                        
                    DeleteEventChannelCommand(focusedEventChannel);
                    focusedEventChannel = null;
                }
            }
            focusedEventChannel = null;

            // delete all channels from the mc
            if (mc.ports != null && mc.PortsList != null && mc.PortsList.Values != null) {
                foreach (Object o in mc.PortsList.Values) {
                    if (o is inputPortType) {
                        inputPortType pIn = (inputPortType)o;
                        if (pIn.ChannelId != "") {
                            channel tempChannel = deploymentChannelList[pIn.ChannelId];
                            DeleteChannel(tempChannel);
                        }
                    }
                    else if (o is outputPortType) {
                        outputPortType pOut = (outputPortType)o;
                        while (pOut.ChannelIds.Count > 0) {
                            channel tempChannel = deploymentChannelList[pOut.ChannelIds[0].ToString()];
                            DeleteChannel(tempChannel);
                        }
                    }
                }
            }
            ArrayList channelToDelete = new ArrayList();
            foreach (channel c in deploymentChannelList.Values) {
                if (c.target.component.id.Equals(mc.id))
                    channelToDelete.Add(c);
            }
            foreach (channel c in channelToDelete) {
                DeleteChannel(c);
            }
            DeleteComponent(mc);
            if (componentList.ContainsKey(dummyName))
                componentList.Remove(dummyName);
        }

    }

}

