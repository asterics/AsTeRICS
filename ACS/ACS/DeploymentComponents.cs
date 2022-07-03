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
 * Filename: DeploymentComponents.cs
 * Class(es):
 *   Classname: componentType
 *   Description: Represents one component within the deployment model
 *   Classname: propertyType
 *   Description: Represents a property of a component or a port
 *   Classname: modelComponentPorts
 *   Description: Represents the ports of a component
 *   Classname: inputPortType
 *   Description: Represents an in-port
 *   Classname: outputPortType
 *   Description: Represents an out-port
 *   Classname: modelChannel
 *   Description: Represents a channel, connecting two ports of two components
 * Author: Roland Ossmann
 * Date: 10.11.2011
 * Version: 0.4
 * Comments: Partial classes to extend the generated classes of deployment_model.cs.
 * --------------------------------------------------------------------------------
 */

using System;
using System.Xml.Serialization;
using System.Windows.Shapes;
using System.Windows.Controls;
using System.Windows.Media;
using System.Collections;
using System.ComponentModel;
using System.Windows;
using System.IO;
using System.Collections.Specialized;
using System.Globalization;
using System.Collections.Generic;

namespace Asterics.ACS {


    /// <summary>
    /// Implementation of the INotifyPropertyChanged interface
    /// Fires events, when it will be bounded to properties
    /// </summary>
    public class Observable : INotifyPropertyChanged {
        internal virtual void OnPropertyChanged(string propertyName) {
            if (PropertyChanged != null) {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }
        public event PropertyChangedEventHandler PropertyChanged;

        internal virtual void OnPropertyChangeError(string propertyName) {
            if (PropertyChangeError != null) {
                PropertyChangeError(this, new PropertyChangedEventArgs(propertyName));
            }
        }
        public event PropertyChangedEventHandler PropertyChangeError;
    }

    /// <summary>
    /// Represents the model, containing all components, channels and events
    /// </summary>
    public partial class model {
        
        // version number of the used deployment_model. On Changes of the model, the verison MUST be changed!!!
        public static string VERSION = "20130320";

        public model() {
            
            this.version = VERSION; 
        }
    }

    /// <summary>
    /// Represents one component within the deployment model
    /// extended with Observable for the PropertyEditor
    /// </summary>
    public partial class componentType:Observable {
        
        
        private Rectangle mainRectangle;
        private Rectangle topRectangle;
        private TextBlock label;
        private Grid topGrid;
        private Canvas componentCanvas;
        private EventListenerPolygon eventListenerPolygon;
        private EventTriggerPolygon eventTriggerPolygon;

        // new with XSD, getter/setter needed
        private Asterics.ACS2.componentTypeDataTypes componentTypeDataType;
        //private string componentId;
        private ArrayList propertiesArrayList;
        
        // storing the event listeners and event triggers in a helper array
        private ArrayList eventTriggerList;
        private ArrayList eventListenerList;

        // List containing all ports of a component. Used, because lists are easier to manipulate then arrays
        private OrderedDictionary portsList;
        
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public OrderedDictionary PortsList {
            get { return portsList; }
            set { portsList = value; }
        }

        // HasVersionConflict indicates a version conflict between the component in a stored model and
        // the component in the bundle descriptor
        private bool hasVersionConflict;

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public bool HasVersionConflict {
            get {
                return hasVersionConflict;
            }
            set {
                hasVersionConflict = value;
            }
        }

        // category string for the propety editor
        private const string categoryComponent = "ACSInternalProperty";

        [XmlIgnoreAttribute()]
        [Category(categoryComponent)]
        [DisplayName("Component Class")]
        [ReadOnly(true)]
        public Asterics.ACS2.componentTypeDataTypes ComponentType {
            get { return componentTypeDataType; }
            set { componentTypeDataType = value; }
        }

        // The 'type' are helper, needed for the property editor
        private string stringType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string StringType {
            get { return stringType; }
            set { stringType = value; }
        }

        private IList listType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public IList ListType {
            get { return listType; }
            set { listType = value; }
        }
        
        private int intType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public int IntType {
            get { return intType; }
            set { intType = value; }
        }

        private bool boolType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public bool BoolType {
            get { return boolType; }
            set { boolType = value; }
        }

        private char charType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public char CharType {
            get { return charType; }
            set { charType = value; }
        }

        private double doubleType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public double DoubleType {
            get { return doubleType; }
            set { doubleType = value; }
        }

        private byte byteType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public byte ByteType {
            get { return byteType; }
            set { byteType = value; }
        }


        //[XmlIgnoreAttribute()]
        //[Browsable(false)]
        //public string ComponentId {
        //    get { return componentId; }
        //    set { componentId = value; }
        //}

        [XmlIgnoreAttribute()]
        public ArrayList PropertyArrayList {
            get {return propertiesArrayList;}
            set {propertiesArrayList = value;}
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Rectangle MainRectangle {
            get {return mainRectangle;}
            set {mainRectangle = value;}
        }
        
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Rectangle TopRectangle {
            get { return topRectangle; }
            set { topRectangle = value; }
        }
        
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Grid TopGrid {
            get { return this.topGrid; }
            set { this.topGrid = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Canvas ComponentCanvas {
            get {return this.componentCanvas;}
            set {this.componentCanvas = value;}
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public TextBlock Label {
            get {return label;}
            set {label = value;}
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public EventListenerPolygon EventListenerPolygon {
            get { return eventListenerPolygon; }
            set { eventListenerPolygon = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public EventTriggerPolygon EventTriggerPolygon {
            get { return eventTriggerPolygon; }
            set { eventTriggerPolygon = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public ArrayList EventTriggerList {
            get { return eventTriggerList; }
            set { eventTriggerList = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public ArrayList EventListenerList {
            get { return eventListenerList; }
            set { eventListenerList = value; }
        }


        private IniFile ini;
        
        /// <summary>
        /// Constructor
        /// </summary>
        public componentType() 
        {
            propertiesField = new propertyType[0];
            mainRectangle = new Rectangle();
            topRectangle = new Rectangle();
            topGrid = new Grid();
            componentCanvas = new Canvas();
            Canvas.SetZIndex(componentCanvas, 9999999);
            label = new TextBlock();
            layout = new layoutType();
            propertiesArrayList = new ArrayList();
            eventListenerList = new ArrayList();
            eventTriggerList = new ArrayList();
            hasVersionConflict = false;

            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }
            portsList = new OrderedDictionary();
        }

        /// <summary>
        /// Init the graphical elements of a component includs
        /// the drawing on the ports and event ports
        /// </summary>
        /// <param name="id">The id of the component</param>
        public void InitGraphLayout(String id) {
            componentCanvas.Width = ACS.LayoutConstants.COMPONENTCANVASWIDTH;
            componentCanvas.Height = ACS.LayoutConstants.COMPONENTCANVASHEIGHT;
            componentCanvas.FocusVisualStyle = null;
            mainRectangle.Height = ACS.LayoutConstants.MAINRECTANGLEHEIGHT;
            mainRectangle.Width = ACS.LayoutConstants.MAINRECTANGLEWIDTH;
            mainRectangle.Stroke = new SolidColorBrush(Colors.Black);
            BrushConverter bc = new BrushConverter();
            String bodyColor = ini.IniReadValue("Layout", "bodycolor");
            if (bodyColor.Equals("")) bodyColor = ACS.LayoutConstants.MAINRECTANGLECOLOR;
            mainRectangle.Fill = (Brush)bc.ConvertFrom(bodyColor);
            mainRectangle.RadiusX = 4;
            mainRectangle.RadiusY = 4;

            componentCanvas.Children.Add(mainRectangle);
            Canvas.SetLeft(mainRectangle, ACS.LayoutConstants.MAINRECTANGLEOFFSETX);
            Canvas.SetTop(mainRectangle, ACS.LayoutConstants.MAINRECTANGLEOFFSETY);
            InitLabeling();
            
            label.Text = id;
            
            int counterIn = 0;
            int counterOut = 0;
            // set the portRectangels on the componentCanvas
            foreach (Object o in this.portsList.Values) {
                if (o is inputPortType) {
                    inputPortType pIn = (inputPortType)o;
                    
                    pIn.PortLabel.Text = pIn.portTypeID;
                    componentCanvas.Children.Add(pIn.PortLabel);
                    Canvas.SetLeft(pIn.PortLabel, ACS.LayoutConstants.INPORTRECTANGLEOFFSETX + ACS.LayoutConstants.INPORTRECTANGLEWIDTH + 2);
                    Canvas.SetTop(pIn.PortLabel, ACS.LayoutConstants.INPORTRECTANGLEOFFSETY + counterIn * ACS.LayoutConstants.INPORTDISTANCE - 2);

                    componentCanvas.Children.Add(pIn.PortRectangle);
                    Canvas.SetLeft(pIn.PortRectangle, ACS.LayoutConstants.INPORTRECTANGLEOFFSETX);
                    Canvas.SetTop(pIn.PortRectangle, ACS.LayoutConstants.INPORTRECTANGLEOFFSETY + counterIn * ACS.LayoutConstants.INPORTDISTANCE);
                    counterIn++;
                    pIn.ComponentId = this.id;
                } else {
                    outputPortType pOut = (outputPortType)o;

                    pOut.PortLabel.Text = pOut.portTypeID;
                    componentCanvas.Children.Add(pOut.PortLabel);
                    Canvas.SetLeft(pOut.PortLabel, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETX - 2 - pOut.PortLabel.Width);
                    Canvas.SetTop(pOut.PortLabel, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETY + counterOut * ACS.LayoutConstants.OUTPORTDISTANCE - 2);
                    
                    componentCanvas.Children.Add(pOut.PortRectangle);
                    Canvas.SetLeft(pOut.PortRectangle, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETX);
                    Canvas.SetTop(pOut.PortRectangle, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETY + counterOut * ACS.LayoutConstants.OUTPORTDISTANCE);
                    counterOut++;
                    pOut.ComponentId = this.id;
                }
            }

            // Draw the event listener port and the event trigger port, if listeners or triggers are defined
            if (EventListenerList.Count > 0) {
                EventListenerPolygon inputEventPolygon = new EventListenerPolygon();
                this.EventListenerPolygon = inputEventPolygon;
                componentCanvas.Children.Add(inputEventPolygon.InputEventPortCanvas);
                Canvas.SetLeft(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETX);
                Canvas.SetTop(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETY);
            }
            if (EventTriggerList.Count > 0) {
                EventTriggerPolygon outputEventPolygon = new EventTriggerPolygon();
                this.EventTriggerPolygon = outputEventPolygon;
                componentCanvas.Children.Add(outputEventPolygon.OutputEventPortCanvas);
                Canvas.SetLeft(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETX);
                Canvas.SetTop(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETY);
            }
        }

        /// <summary>
        /// Set up the rectange at the top of each component and place the text (name)
        /// </summary>
        private void InitLabeling() {
            topGrid.Height = ACS.LayoutConstants.TOPGRIDHEIGHT;
            topGrid.Width = ACS.LayoutConstants.TOPGRIDWIDTH;
            topRectangle.Height = ACS.LayoutConstants.TOPRECTANGLEHEIGHT;
            topRectangle.Width = ACS.LayoutConstants.TOPRECTANGLEWIDTH;
            topRectangle.Stroke = new SolidColorBrush(Colors.Black);
            BrushConverter bc = new BrushConverter();
            String headerColor = ini.IniReadValue("Layout", "headercolor");
            if (headerColor.Equals("")) headerColor = ACS.LayoutConstants.TOPRECTANGLECOLOR;
            topRectangle.Fill = (Brush)bc.ConvertFrom(headerColor);
            topRectangle.RadiusX = 4;
            topRectangle.RadiusY = 4;
            Canvas.SetZIndex(topGrid, 3);
            label.FontSize = ACS.LayoutConstants.LABELFONTSIZE;
            label.VerticalAlignment = VerticalAlignment.Center;
            label.Margin = new Thickness(5, 0, 5, 0);
            label.TextWrapping = TextWrapping.Wrap;
            componentCanvas.Children.Add(topRectangle);
            topGrid.Children.Add(label);
            componentCanvas.Children.Add(topGrid);
            Canvas.SetLeft(topRectangle, ACS.LayoutConstants.TOPRECTANGLEOFFSETX);
            Canvas.SetTop(topRectangle, ACS.LayoutConstants.TOPRECTANGLEOFFSETY);
            Canvas.SetLeft(topGrid, ACS.LayoutConstants.TOPGRIDOFFSETX);
            Canvas.SetTop(topGrid, ACS.LayoutConstants.TOPGRIDOFFSETY);
        }


        /// <summary>
        /// Copy the arrays into an arraylists for faster and easier access
        /// </summary>
        public void InitArrayLists() {
            if (this.ports != null) {
                foreach (object o in this.ports) {
                    if (o is inputPortType) {
                        this.PortsList.Add(((inputPortType)o).portTypeID, (inputPortType)o);
                        foreach (propertyType portProperty in ((inputPortType)o).properties) {
                            if (portProperty != null)
                                ((inputPortType)o).PropertyArrayList.Add(portProperty);
                        }
                    } else if (o is outputPortType) {
                        this.PortsList.Add(((outputPortType)o).portTypeID, (outputPortType)o);
                        foreach (propertyType portProperty in ((outputPortType)o).properties) {
                            if (portProperty != null)
                                ((outputPortType)o).PropertyArrayList.Add(portProperty);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// copy all _relevant_ properties of a rectangel
        /// </summary>
        /// <param name="source">The original rectangle</param>
        /// <param name="target">The new rectangle, with the properties of the original rectangel</param>
        private void CopyRectangel(Rectangle source, Rectangle target) {
            target.Width = source.Width;
            target.Height = source.Height;
            target.Fill = source.Fill;
            target.RadiusX = source.RadiusX;
            target.RadiusY = source.RadiusY;
            target.Stroke = source.Stroke;
        }

        // copy all relevant properties of an eventport
        //private void CopyEventPort(Canvas source, Canvas target) {
        //    target.Width = source.Width;
        //    target.Height = source.Height;
        //    PointCollection eventPortPointCollection = new PointCollection();
        //    Polygon eventPortPolygon = new Polygon();
        //    foreach (Polygon pg in source.Children) {
        //        foreach (Point p in pg.Points) {
        //            eventPortPointCollection.Add(new Point(p.X, p.Y));
        //        }
        //        eventPortPolygon.Points = eventPortPointCollection;
        //        eventPortPolygon.Fill = pg.Fill;
        //        eventPortPolygon.Stroke = pg.Stroke;
        //    }
        //    target.Children.Add(eventPortPolygon);    
        //}

        /// <summary>
        /// Generate a new deployment component form a bundle component, deep copy
        /// </summary>
        /// <param name="component">The bundle component, which should be copied</param>
        /// <param name="id">The id of the new component</param>
        /// <returns>A new model component based on the bundle component</returns>
        public static componentType CopyFromBundleModel(Asterics.ACS2.componentTypesComponentType component, String id){
            componentType mComponent = new componentType();
            //int portIndex = 0;
            
            mComponent.id = id;
            mComponent.componentTypeDataType = component.type.Value;
            //mComponent.componentId = component.id;
            mComponent.type_id = component.id;
            mComponent.description = component.description;

            // copy the graphical elements
            mComponent.ComponentCanvas.Height = component.ComponentCanvas.Height;
            mComponent.ComponentCanvas.Width = component.ComponentCanvas.Width;
            mComponent.ComponentCanvas.FocusVisualStyle = null;
            mComponent.CopyRectangel(component.MainRectangle, mComponent.MainRectangle);
            mComponent.ComponentCanvas.Children.Add(mComponent.MainRectangle);
            Canvas.SetLeft(mComponent.MainRectangle, Canvas.GetLeft(component.MainRectangle));
            Canvas.SetTop(mComponent.MainRectangle, Canvas.GetTop(component.MainRectangle));
            mComponent.Label.Text = id;

            mComponent.InitLabeling();

            // copy the properties
            if (component.properties != null) {
                foreach (Asterics.ACS2.propertyType compProperty in component.properties) {
                    propertyType property = new propertyType();
                    property.name = compProperty.name;
                    property.DataType = compProperty.type;
                    property.Description = compProperty.description;
                    property.value = compProperty.value;
                    if (compProperty.combobox != null) {
                        property.ComboBoxStrings = compProperty.combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                    }
                    if (compProperty.getStringList) {
                        property.GetStringList = true;
                    } else {
                        property.GetStringList = false;
                    }
                    mComponent.PropertyArrayList.Add(property);
                }
            }
            mComponent.propertiesField = new propertyType[mComponent.PropertyArrayList.Count];
            mComponent.PropertyArrayList.CopyTo(mComponent.propertiesField);

            // copy the ports
            
            if (component.ports != null) {
                foreach (object o in component.ports) {
                    // copy the inports
                    if (o is Asterics.ACS2.inputPortType) {
                        Asterics.ACS2.inputPortType compInPort = (Asterics.ACS2.inputPortType)o;
                        inputPortType inPort = new inputPortType();
                        inPort.portTypeID = compInPort.id;
                        inPort.ComponentId = id;
                        inPort.Description = compInPort.description;
                        inPort.MustBeConnected = compInPort.mustBeConnected;
                        inPort.PortDataType = compInPort.dataType;
                        inPort.ComponentTypeId = component.id;

                        inPort.PortLabel.Text = compInPort.id;
                        mComponent.componentCanvas.Children.Add(inPort.PortLabel);
                        Canvas.SetLeft(inPort.PortLabel, Canvas.GetLeft(compInPort.PortRectangle) + ACS.LayoutConstants.INPORTRECTANGLEWIDTH + 2);
                        Canvas.SetTop(inPort.PortLabel, Canvas.GetTop(compInPort.PortRectangle) - 2 );

                        mComponent.CopyRectangel(compInPort.PortRectangle, inPort.PortRectangle);
                        mComponent.ComponentCanvas.Children.Add(inPort.PortRectangle);
                        Canvas.SetLeft(inPort.PortRectangle, Canvas.GetLeft(compInPort.PortRectangle));
                        Canvas.SetTop(inPort.PortRectangle, Canvas.GetTop(compInPort.PortRectangle));

                        // copy the properties of the inports
                        if (compInPort.properties != null) {
                            foreach (Asterics.ACS2.propertyType portProperty in compInPort.properties) {
                                propertyType property = new propertyType();
                                property.name = portProperty.name;
                                property.DataType = portProperty.type;
                                property.Description = portProperty.description;
                                property.value = portProperty.value;
                                if (portProperty.combobox != null) {
                                    property.ComboBoxStrings = portProperty.combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                }
                                inPort.PropertyArrayList.Add(property);
                            }
                            inPort.properties = new propertyType[inPort.PropertyArrayList.Count];
                            inPort.PropertyArrayList.CopyTo(inPort.properties);
                        }

                        // copy the reference to another port (only available, if it is a group element)
                        if (compInPort.RefPort != null) {
                            inPort.refs = compInPort.RefPort;
                        }

                        mComponent.PortsList.Add(inPort.portTypeID,inPort);

                    // copy the outports
                    } else if (o is Asterics.ACS2.outputPortType) {
                        Asterics.ACS2.outputPortType compOutPort = (Asterics.ACS2.outputPortType)o;
                        outputPortType outPort = new outputPortType();
                        outPort.portTypeID = compOutPort.id;
                        outPort.ComponentId = id;
                        outPort.Description = compOutPort.description;
                        outPort.PortDataType = compOutPort.dataType;
                        outPort.ComponentTypeId = component.id;
                    
                        outPort.PortLabel.Text = compOutPort.id;
                        mComponent.componentCanvas.Children.Add(outPort.PortLabel);
                        Canvas.SetLeft(outPort.PortLabel, Canvas.GetLeft(compOutPort.PortRectangle) - ACS.LayoutConstants.OUTPORTLABELWIDTH - 2);
                        Canvas.SetTop(outPort.PortLabel, Canvas.GetTop(compOutPort.PortRectangle) - 2);

                        mComponent.CopyRectangel(compOutPort.PortRectangle, outPort.PortRectangle);
                        mComponent.ComponentCanvas.Children.Add(outPort.PortRectangle);
                        Canvas.SetLeft(outPort.PortRectangle, Canvas.GetLeft(compOutPort.PortRectangle));
                        Canvas.SetTop(outPort.PortRectangle, Canvas.GetTop(compOutPort.PortRectangle));

                        // copy the properties of the outports
                        if (compOutPort.properties != null) {
                            foreach (Asterics.ACS2.propertyType portProperty in compOutPort.properties) {
                                propertyType property = new propertyType();
                                property.name = portProperty.name;
                                property.DataType = portProperty.type;
                                property.Description = portProperty.description;
                                property.value = portProperty.value;
                                if (portProperty.combobox != null) {
                                    property.ComboBoxStrings = portProperty.combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                }
                                outPort.PropertyArrayList.Add(property);
                            }
                            outPort.properties = new propertyType[outPort.PropertyArrayList.Count];
                            outPort.PropertyArrayList.CopyTo(outPort.properties);
                        }

                        // copy the reference to another port (only available, if it is a group element)
                        if (compOutPort.RefPort != null) {
                            outPort.refs = compOutPort.RefPort;
                        }

                        mComponent.PortsList.Add(outPort.portTypeID,outPort);
                    } 
                
                }
                mComponent.ports = new object[mComponent.PortsList.Count];
                mComponent.PortsList.Values.CopyTo(mComponent.ports, 0);
            }

            // Searching for event listeners and event triggers and saving them in arrays
            if ((component.events != null)&&(component.events != null)) {
                foreach (object eventO in component.events) {
                    if (eventO is ACS2.eventsTypeEventListenerPortType) {
                        ACS2.eventsTypeEventListenerPortType compEl = (ACS2.eventsTypeEventListenerPortType)eventO;
                        EventListenerPort el = new EventListenerPort();
                        el.EventListenerId = compEl.id;
                        el.EventDescription = compEl.description;
                        el.ComponentId = id;
                        mComponent.EventListenerList.Add(el);
                    } else if (eventO is ACS2.eventsTypeEventTriggererPortType) {
                        ACS2.eventsTypeEventTriggererPortType compEl = (ACS2.eventsTypeEventTriggererPortType)eventO;
                        EventTriggerPort et = new EventTriggerPort();
                        et.EventTriggerId = compEl.id;
                        et.EventDescription = compEl.description;
                        et.ComponentId = id;
                        mComponent.EventTriggerList.Add(et);
                    }
                }
            }

            // Adapt the size of MainRectangle, if more then MAINRECTANGLENUMBEROFPORTS are in a component
            int numInPorts = 0;
            int numOutPorts = 0;
            foreach (object o in mComponent.PortsList.Values) {
                if (o is inputPortType) {
                    numInPorts++;
                } else {
                    numOutPorts++;
                }
            }
            if (numOutPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                mComponent.MainRectangle.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE );
                mComponent.ComponentCanvas.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE);
            } else if (numInPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                mComponent.MainRectangle.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
                mComponent.ComponentCanvas.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
            }

            // Drawing the event listener and event trigger port, if events are available
            if (mComponent.EventListenerList.Count > 0) {
                EventListenerPolygon inputEventPolygon = new EventListenerPolygon();
                mComponent.EventListenerPolygon = inputEventPolygon;
                mComponent.componentCanvas.Children.Add(inputEventPolygon.InputEventPortCanvas);
                Canvas.SetLeft(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETX);
                //Canvas.SetTop(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETY);
                Canvas.SetTop(inputEventPolygon.InputEventPortCanvas, mComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
            }
            if (mComponent.EventTriggerList.Count > 0) {
                EventTriggerPolygon outputEventPolygon = new EventTriggerPolygon();
                mComponent.EventTriggerPolygon = outputEventPolygon;
                mComponent.componentCanvas.Children.Add(outputEventPolygon.OutputEventPortCanvas);
                Canvas.SetLeft(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETX);
                //Canvas.SetTop(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETY);
                Canvas.SetTop(outputEventPolygon.OutputEventPortCanvas, mComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
            }
                                      
            return mComponent;
        }        

    }


    /// <summary>
    /// Represents a property of a component or a port
    /// </summary>
    public partial class propertyType : Observable {
        private String description;
        private Asterics.ACS2.dataType dataType;
        private String[] comboBoxStrings;
        private Boolean getStringList;


        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Boolean GetStringList {
            get {return getStringList; }
            set {getStringList = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public String[] ComboBoxStrings {
            get { return comboBoxStrings; }
            set { comboBoxStrings = value; }
        }


        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        public String Description {
            get {return description;}
            set {description = value;}
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Asterics.ACS2.dataType DataType {
            get {return dataType;}
            set {dataType = value;}
        }

        // validation of the input of a property
        private bool Validate(object valueToEval) {
            if (dataType == Asterics.ACS2.dataType.integer) {
                int i;
                return int.TryParse(valueToEval.ToString(), out i);
            } else if (dataType == ACS2.dataType.@double) {
                double d;
                NumberStyles style = NumberStyles.AllowDecimalPoint | NumberStyles.AllowLeadingSign;
                CultureInfo culture = CultureInfo.CreateSpecificCulture("en-GB");
                if (valueToEval.ToString().Contains(",")) {
                    valueToEval += ((string)valueToEval).Replace(',','.');
                }
                return double.TryParse(valueToEval.ToString(), style, culture, out d);
            //} else if (dataType == ACS2.dataType.boolean) {
            //    bool b;
            //return bool.TryParse(valueToEval.ToString(), out b);
            } else if (dataType == ACS2.dataType.@byte) {
                byte b;
                return byte.TryParse(valueToEval.ToString(), out b);
            } else if (dataType == ACS2.dataType.@char) {
                char c;
                return char.TryParse(valueToEval.ToString(), out c);
            }
            return true;
        }
    }


 
    /// <summary>
    /// Represents an input port on a component
    /// </summary>
    public partial class inputPortType : Observable {
        private Rectangle portRectangle;
        private TextBlock portLabel;

        private string channelId = "";
        private string groupChannelId = "";

        private string componentId = "";
        private string description;
        private bool mustBeConnected;
        private ArrayList propertyArrayList = new ArrayList();
        private ACS2.dataType portDataType;

        private string portAliasForGroups;

        // type_id of the parents component
        private string componentTypeId;


        [XmlIgnoreAttribute()]
        [DisplayName("Port Alias")]
        [Category("ACSInternalProperty")]
        public string PortAliasForGroups {
            get {
                return this.portAliasForGroups;
            }
            set {
                this.portAliasForGroups = value;
                OnPropertyChanged("Port Alias");
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentTypeId {
            get { return componentTypeId; }
            set { componentTypeId = value; }
        }        

        // several dummy-types, needed for the property editor.
        // Not pretty, but working
        private string stringType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string StringType {
            get { return stringType; }
            set { stringType = value; }
        }

        private int intType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public int IntType {
            get { return intType; }
            set { intType = value; }
        }

        private IList listType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public IList ListType {
            get { return listType; }
            set { listType = value; }
        }

        private bool boolType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public bool BoolType {
            get { return boolType; }
            set { boolType = value; }
        }

        private char charType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public char CharType {
            get { return charType; }
            set { charType = value; }
        }

        private double doubleType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public double DoubleType {
            get { return doubleType; }
            set { doubleType = value; }
        }

        private byte byteType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public byte ByteType {
            get { return byteType; }
            set { byteType = value; }
        }

        [XmlIgnoreAttribute()]
        public ArrayList PropertyArrayList {
            get { return propertyArrayList; }
            set { propertyArrayList = value; }
        }

        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        [Category("ACSInternalProperty")]
        public ACS2.dataType PortDataType {
            get { return portDataType; }
            set { portDataType = value; }
        }

        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        [Category("ACSInternalProperty")]
        public string Description {
            get { return description; }
            set { description = value; }
        }

        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        [Category("ACSInternalProperty")]
        public bool MustBeConnected {
            get { return mustBeConnected; }
            set { mustBeConnected = value; }
        }

        private IniFile ini;

        public inputPortType() {
            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

            propertiesField = new propertyType[0];
            portRectangle = new Rectangle();
            portRectangle.Height = ACS.LayoutConstants.INPORTRECTANGLEHEIGHT;
            portRectangle.Width = ACS.LayoutConstants.INPORTRECTANGLEWIDTH;
            portRectangle.RadiusX = 3;
            portRectangle.RadiusY = 3;
            portRectangle.Stroke = new SolidColorBrush(Colors.Black);
            BrushConverter bc = new BrushConverter();
            String inPortColor = ini.IniReadValue("Layout", "inportcolor");
            if (inPortColor.Equals("")) inPortColor = ACS.LayoutConstants.INPORTRECTANGLECOLOR;
            portRectangle.Fill = (Brush)bc.ConvertFrom(inPortColor);

            portLabel = new TextBlock();
            portLabel.TextAlignment = TextAlignment.Left;
            portLabel.FontSize = ACS.LayoutConstants.PORTLABELFONTSIZE;
            portLabel.Width = ACS.LayoutConstants.INPORTLABELWIDTH;
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Rectangle PortRectangle {
            get { return this.portRectangle; }
            set { this.portRectangle = value; }
        }
        
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public TextBlock PortLabel {
            get { return portLabel; }
            set { portLabel = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ChannelId {
            get { return this.channelId; }
            set { this.channelId = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string GroupChannelId {
            get { return groupChannelId; }
            set { groupChannelId = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentId {
            get { return this.componentId; }
            set { this.componentId = value; }
        }
    }

    /// <summary>
    /// Represents an output port of a component
    /// </summary>
    public partial class outputPortType : Observable {
        private Rectangle portRectangle;
        private TextBlock portLabel;

        private ArrayList channelIds = new ArrayList();
        private string componentId = "";
        private string description;
        private ArrayList propertyArrayList = new ArrayList();
        private ACS2.dataType portDataType;

        private IniFile ini;

        private string portAliasForGroups;


        // type_id of the parents component
        private string componentTypeId;

        [XmlIgnoreAttribute()]
        [DisplayName("Port Alias")]
        [Category("ACSInternalProperty")]
        public string PortAliasForGroups {
            get {
                return this.portAliasForGroups;
            }
            set {
                this.portAliasForGroups = value;
                OnPropertyChanged("Port Alias");
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentTypeId {
            get { return componentTypeId; }
            set { componentTypeId = value; }
        }


        public outputPortType() {
            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

            propertiesField = new propertyType[0];
            portRectangle = new Rectangle();

            portRectangle.Height = ACS.LayoutConstants.OUTPORTRECTANGLEHEIGHT;
            portRectangle.Width = ACS.LayoutConstants.OUTPORTRECTANGLEWIDTH;
            portRectangle.RadiusX = 3;
            portRectangle.RadiusY = 3;
            portRectangle.Stroke = new SolidColorBrush(Colors.Black);
            BrushConverter bc = new BrushConverter();
            String outPortColor = ini.IniReadValue("Layout", "outportcolor");
            if (outPortColor.Equals("")) outPortColor = ACS.LayoutConstants.OUTPORTRECTANGLECOLOR;
            portRectangle.Fill = (Brush)bc.ConvertFrom(outPortColor);
            
            portLabel = new TextBlock();
            portLabel.TextAlignment = TextAlignment.Right;
            portLabel.FontSize = ACS.LayoutConstants.PORTLABELFONTSIZE;
            portLabel.Width = ACS.LayoutConstants.OUTPORTLABELWIDTH;
        }


        // several dummy-types, needed for the property editor.
        // Not pretty, but working
        private IList listType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public IList ListType {
            get { return listType; }
            set { listType = value; }
        }

        private string stringType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string StringType {
            get { return stringType; }
            set { stringType = value; }
        }

        private int intType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public int IntType {
            get { return intType; }
            set { intType = value; }
        }

        private bool boolType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public bool BoolType {
            get { return boolType; }
            set { boolType = value; }
        }

        private char charType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public char CharType {
            get { return charType; }
            set { charType = value; }
        }

        private double doubleType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public double DoubleType {
            get { return doubleType; }
            set { doubleType = value; }
        }

        private byte byteType;
        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public byte ByteType {
            get { return byteType; }
            set { byteType = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Rectangle PortRectangle {
            get { return this.portRectangle; }
            set { this.portRectangle = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public TextBlock PortLabel {
            get { return portLabel; }
            set { portLabel = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public ArrayList ChannelIds {
            get { return this.channelIds; }
            set { this.channelIds = value; }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentId {
            get { return this.componentId; }
            set { this.componentId = value; }
        }
        
        [XmlIgnoreAttribute()]
        public ArrayList PropertyArrayList {
            get { return propertyArrayList; }
            set { propertyArrayList = value; }
        }

        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        [Category("ACSInternalProperty")]
        public ACS2.dataType PortDataType {
            get { return portDataType; }
            set { portDataType = value; }
        }

        [XmlIgnoreAttribute()]
        [ReadOnly(true)]
        [Category("ACSInternalProperty")]
        public string Description {
            get { return description; }
            set { description = value; }
        }
    }

    /// <summary>
    /// Represents a channel, connecting two ports of two components
    /// </summary>
    public partial class channel {

        private Line line;

        private bindingEdge groupOriginalSource = null;
        private bindingEdge groupOriginalTarget = null;

        public channel() {
            line = new Line();
            line.Name = this.id;

            BrushConverter bc = new BrushConverter();
            line.Stroke = (Brush)bc.ConvertFrom("#AA000000");
            //line.Stroke = System.Windows.Media.Brushes.Black;

            line.StrokeThickness = 3;
            line.FocusVisualStyle = null;
            this.source = new bindingEdge();
            this.source.port = new bindingEdgePortType();
            this.source.component = new bindingEdgeComponentType();
            this.target = new bindingEdge();
            this.target.port = new bindingEdgePortType();
            this.target.component = new bindingEdgeComponentType();
        }

        [XmlIgnoreAttribute()]
        public Line Line {
            get {
                return this.line;
            }
            set {
                this.line = value;
            }
        }

        [XmlIgnoreAttribute()]
        public bindingEdge GroupOriginalSource {
            get {
                return this.groupOriginalSource;
            }
            set {
                this.groupOriginalSource = value;
            }
        }

        [XmlIgnoreAttribute()]
        public bindingEdge GroupOriginalTarget {
            get {
                return this.groupOriginalTarget;
            }
            set {
                this.groupOriginalTarget = value;
            }
        }
    }


    /// <summary>
    /// Representing an input eventport (aka event listener port)
    /// </summary>
    public partial class EventListenerPort : Observable {
        private ArrayList eventChannelIds = new ArrayList();
        private string componentId = "";
        private string eventListenerId = "";
        private string eventDescription = "";

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string EventDescription {
            get {
                return eventDescription;
            }
            set {
                eventDescription = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public ArrayList EventChannelIds {
            get {
                return this.eventChannelIds;
            }
            set {
                this.eventChannelIds = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentId {
            get {
                return this.componentId;
            }
            set {
                this.componentId = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string EventListenerId {
            get { return eventListenerId; }
            set { eventListenerId = value; }
        }
    }

    /// <summary>
    /// The event listener polygon on the bottom of the component 
    /// </summary>
    public class EventListenerPolygon {
        private Canvas inputEventPortCanvas;
        private Polygon eventPortPolygon = new Polygon();
        private PointCollection eventPortPointCollection;

        private IniFile ini;

        public EventListenerPolygon() {
            // loading the asterics.ini file to get the correct colors for the eventports
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

            inputEventPortCanvas = new Canvas();
            inputEventPortCanvas.Width = ACS.LayoutConstants.EVENTPORTWIDTH + 3;
            inputEventPortCanvas.Height = ACS.LayoutConstants.EVENTPORTHEIGHT + 3;
            inputEventPortCanvas.Name = "EventListenerPort";
            PointCollection eventPortPointCollection = new PointCollection();
            eventPortPointCollection.Add(new Point(4, 4));
            eventPortPointCollection.Add(new Point(4, 4+ACS.LayoutConstants.EVENTPORTHEIGHT));
            eventPortPointCollection.Add(new Point(4+ACS.LayoutConstants.EVENTPORTWIDTH/2, 4+3*ACS.LayoutConstants.EVENTPORTHEIGHT/4));
            eventPortPointCollection.Add(new Point(4+ACS.LayoutConstants.EVENTPORTWIDTH, 4+ACS.LayoutConstants.EVENTPORTHEIGHT));
            eventPortPointCollection.Add(new Point(4+ACS.LayoutConstants.EVENTPORTWIDTH, 4));
            eventPortPolygon.Points = eventPortPointCollection;
            inputEventPortCanvas.Children.Add(eventPortPolygon);

            BrushConverter bc = new BrushConverter();
            String eventInPortColor = ini.IniReadValue("Layout", "eventinportcolor");
            if (eventInPortColor.Equals("")) eventInPortColor = ACS.LayoutConstants.EVENTINPORTCOLOR;
            eventPortPolygon.Fill = (Brush)bc.ConvertFrom(eventInPortColor);
            eventPortPolygon.Stroke = (Brush)bc.ConvertFrom(ACS.LayoutConstants.DEFAULTSTROKECOLOR);
            eventPortPolygon.Name = "EventListenerPortPolygon";
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Canvas InputEventPortCanvas {
            get {
                return this.inputEventPortCanvas;
            }
            set {
                this.inputEventPortCanvas = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public PointCollection EventPortPointCollection {
            get {
                return this.eventPortPointCollection;
            }
            set {
                this.eventPortPointCollection = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Polygon EventPortPolygon {
            get {
                return this.eventPortPolygon;
            }
            set {
                this.eventPortPolygon = value;
            }
        }
    }

    /// <summary>
    /// Representing an output eventport (aka event trigger port)
    /// </summary>
    public partial class EventTriggerPort : Observable {
        private ArrayList eventChannelIds = new ArrayList();
        private string componentId = "";
        private string eventTriggerId = "";
        private string eventDescription = "";

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string EventDescription {
            get {
                return eventDescription;
            }
            set {
                eventDescription = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public ArrayList EventChannelIds {
            get {
                return this.eventChannelIds;
            }
            set {
                this.eventChannelIds = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string ComponentId {
            get {
                return this.componentId;
            }
            set {
                this.componentId = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public string EventTriggerId {
            get {
                return eventTriggerId;
            }
            set {
                eventTriggerId = value;
            }
        }
    }

    /// <summary>
    /// The event trigger polygon at the botton of the component 
    /// </summary>
    public partial class EventTriggerPolygon {
        private Canvas outputEventPortCanvas;
        private Polygon eventPortPolygon = new Polygon();

        private PointCollection eventPortPointCollection;

        private IniFile ini;

        public EventTriggerPolygon() {
            // loading the asterics.ini file to get the correct colors for the eventports
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

            outputEventPortCanvas = new Canvas();
            outputEventPortCanvas.Width = ACS.LayoutConstants.EVENTPORTWIDTH + 3;
            outputEventPortCanvas.Height = ACS.LayoutConstants.EVENTPORTHEIGHT + 3;
            outputEventPortCanvas.Name = "EventTriggerPort";
            PointCollection eventPortPointCollection = new PointCollection();
            eventPortPointCollection.Add(new Point(4, 4));
            eventPortPointCollection.Add(new Point(4, 4 + 3 * ACS.LayoutConstants.EVENTPORTHEIGHT / 4));
            eventPortPointCollection.Add(new Point(4 + ACS.LayoutConstants.EVENTPORTWIDTH / 2, 4 + ACS.LayoutConstants.EVENTPORTHEIGHT));
            eventPortPointCollection.Add(new Point(4 + ACS.LayoutConstants.EVENTPORTWIDTH, 4 + 3 * ACS.LayoutConstants.EVENTPORTHEIGHT / 4));
            eventPortPointCollection.Add(new Point(4 + ACS.LayoutConstants.EVENTPORTWIDTH, 4));
            eventPortPolygon.Points = eventPortPointCollection;
            outputEventPortCanvas.Children.Add(eventPortPolygon);

            BrushConverter bc = new BrushConverter();
            String eventOutPortColor = ini.IniReadValue("Layout", "eventoutportcolor");
            if (eventOutPortColor.Equals(""))
                eventOutPortColor = ACS.LayoutConstants.EVENTOUTPORTCOLOR;
            eventPortPolygon.Fill = (Brush)bc.ConvertFrom(eventOutPortColor);
            eventPortPolygon.Stroke = (Brush)bc.ConvertFrom(ACS.LayoutConstants.DEFAULTSTROKECOLOR);
            eventPortPolygon.Name = "EventTriggerPortPolygon";
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Canvas OutputEventPortCanvas {
            get {
                return this.outputEventPortCanvas;
            }
            set {
                this.outputEventPortCanvas = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public PointCollection EventPortPointCollection {
            get {
                return this.eventPortPointCollection;
            }
            set {
                this.eventPortPointCollection = value;
            }
        }

        [XmlIgnoreAttribute()]
        [Browsable(false)]
        public Polygon EventPortPolygon {
            get {
                return this.eventPortPolygon;
            }
            set {
                this.eventPortPolygon = value;
            }
        }
    }


    /// <summary>
    /// Representing an event channel (one event) between two components
    /// </summary>
    public partial class eventChannel{

        private eventEdge groupOriginalTarget;
        private eventEdge groupOriginalSource;

        public eventChannel() {
            eventEdge triggerEdge = new eventEdge();
            eventEdge listenerEdge = new eventEdge();
            eventChannelSourcesType ecs = new eventChannelSourcesType();
            ecs.source = new eventEdge();
            ecs.source = triggerEdge;
            this.sources = ecs;
            eventChannelTargetsType ect = new eventChannelTargetsType();
            ect.target = new eventEdge();
            ect.target = listenerEdge;
            this.targets = ect;
        }

        [XmlIgnoreAttribute()]
        public eventEdge GroupOriginalTarget {
            get {
                return this.groupOriginalTarget;
            }
            set {
                this.groupOriginalTarget = value;
            }
        }

        [XmlIgnoreAttribute()]
        public eventEdge GroupOriginalSource {
            get {
                return this.groupOriginalSource;
            }
            set {
                this.groupOriginalSource = value;
            }
        }
    }

    /// <summary>
    /// Representing an event edge (start or end point of an event)
    /// </summary>
    public partial class eventEdge {
        public eventEdge() {
            eventEdgeComponentType eec = new eventEdgeComponentType();
            eventEdgeEventPortType eep = new eventEdgeEventPortType();
            this.component = eec;
            this.eventPort = eep;
        }
    }

    /// <summary>
    /// Representing a line between two event event polygons
    /// </summary>
    public class eventChannelLine {
        
        private Line line;
        private String triggerComponentId;
        private String listernerComponentId;
        private bool hasGroupSource;
        private bool hasGroupTarget;
        

        public eventChannelLine() {
            line = new Line();
            line.Name = "EventChannelLine";

            BrushConverter bc = new BrushConverter();
            line.Stroke = (Brush)bc.ConvertFrom("#99d41919");
            //line.Stroke = System.Windows.Media.Brushes.Red;

            line.StrokeThickness = 3;
            line.FocusVisualStyle = null;

            hasGroupSource = false;
            hasGroupTarget = false;
        }

        [XmlIgnoreAttribute()]
        public Line Line {
            get {
                return this.line;
            }
            set {
                this.line = value;
            }
        }

        [XmlIgnoreAttribute()]
        public String ListenerComponentId {
            get {
                return listernerComponentId;
            }
            set {
                listernerComponentId = value;
            }
        }

        [XmlIgnoreAttribute()]
        public bool HasGroupSource {
            get {
                return hasGroupSource;
            }
            set {
                hasGroupSource = value;
            }
        }

        [XmlIgnoreAttribute()]
        public bool HasGroupTarget {
            get {
                return hasGroupTarget;
            }
            set {
                hasGroupTarget = value;
            }
        }

        [XmlIgnoreAttribute()]
        public String TriggerComponentId {
            get {
                return triggerComponentId;
            }
            set {
                triggerComponentId = value;
            }
        }
    }

    public partial class guiType {

        private Canvas guiElementCanvas;

        [XmlIgnoreAttribute()]
        public Canvas GuiElementCanvas {
            get {
                return guiElementCanvas;
            }
            set {
                guiElementCanvas = value;
            }
        }

        private bool isExternalGUIElement;

        [XmlIgnoreAttribute()]
        public bool IsExternalGUIElement {
            get { return isExternalGUIElement; }
            set { isExternalGUIElement = value; }
        }

    }




    //public class groupComponent : modelComponent {

    //    private Canvas groupCollectionCanvas;

    //    public groupComponent():base() {
    //        groupCollectionCanvas = new Canvas();
    //    }
    //}


    public class groupComponent {

        // test for grouping
        private System.Collections.Generic.Dictionary<string, eventEdge> groupPorts = new System.Collections.Generic.Dictionary<string, eventEdge>();

        public System.Collections.Generic.Dictionary<string, eventEdge> GroupPorts {
            get {
                return groupPorts;
            }
            set {
                groupPorts = value;
            }
        }


        private Canvas groupCollectionCanvas;

        public Canvas GroupCollectionCanvas {
            get {
                return groupCollectionCanvas;
            }
            set {
                groupCollectionCanvas = value;
            }
        }
        private LinkedList<componentType> addedComponentList = new LinkedList<componentType>();

        public LinkedList<componentType> AddedComponentList {
            get {
                return addedComponentList;
            }
            set {
                addedComponentList = value;
            }
        }
        private LinkedList<channel> addedChannelsList = new LinkedList<channel>();

        public LinkedList<channel> AddedChannelsList {
            get {
                return addedChannelsList;
            }
            set {
                addedChannelsList = value;
            }
        }
        private LinkedList<eventChannelLine> addedEventChannelsList = new LinkedList<eventChannelLine>();

        public LinkedList<eventChannelLine> AddedEventChannelsList {
            get {
                return addedEventChannelsList;
            }
            set {
                addedEventChannelsList = value;
            }
        }
        private String groupID;

        public String GroupID {
            get {
                return groupID;
            }
            set {
                groupID = value;
            }
        }

        private String id;

        public String ID {
            get {
                return id;
            }
            set {
                id = value;
            }
        }

        public groupComponent() {
            groupCollectionCanvas = new Canvas();
        }

    }
}