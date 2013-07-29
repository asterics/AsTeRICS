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
 * Filename: BundleComponents.cs
 * Class(es):
 *   Classname: componentTypesComponentType
 *   Description: Defines one component. Is extended with graphical elements and position them on the internal canvas
 *   Classname: componentTypesComponentTypePorts
 *   Description: Defines the ports of a component
 *   Classname: inputPortType
 *   Description: Defines the in-ports within the ports. Extended mainly with the graphical port representation
 *   Classname: outputPortType
 *   Description: Defines the out-ports within the ports. Extended mainly with the graphical port representation
 * Author: Roland Ossmann
 * Date: 10.11.2011
 * Version: 0.4
 * Comments: Partial classes to extend the generated classes of bundle_model.cs. 
 *   Namespace ACS2 is used to avoid naming conflicts with DeploymentComponents classes.
 * --------------------------------------------------------------------------------
 */


using System;
using System.Xml.Serialization;
using System.Windows.Shapes;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows;
using System.Collections;
using System.IO;
using System.Collections.Specialized;

namespace Asterics.ACS2 {

    /// <summary>
    /// Defining one component (one plug-in). Beside the members, being defined in the generated class of the file bundle_model, the class is extended with graphical elements.
    /// Furthermore, this class handles the position on the internal canvas
    /// </summary> 
    public partial class componentTypesComponentType {               
        
        private Rectangle mainRectangle;

        private TextBlock label;

        private Canvas componentCanvas;

        private Asterics.ACS.IniFile ini;
                
        // just a helper field to get the same order of ports because of the Hashtable-keys
        private OrderedDictionary portsList;
        
        /// <summary>
        /// Constructor
        /// </summary>
        public componentTypesComponentType() {
            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new Asterics.ACS.IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new Asterics.ACS.IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Asterics.ACS.Properties.Resources.IniFileNotFoundText, Asterics.ACS.Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }
            // init graphical elements
            componentCanvas = new Canvas();
            label = new TextBlock();

            mainRectangle = new Rectangle();
            mainRectangle.Height = ACS.LayoutConstants.MAINRECTANGLEHEIGHT;
            mainRectangle.Width = ACS.LayoutConstants.MAINRECTANGLEWIDTH;
            mainRectangle.Stroke = new SolidColorBrush(Colors.Black);
            BrushConverter bc = new BrushConverter();
            String bodyColor = ini.IniReadValue("Layout", "bodycolor");
            if (bodyColor.Equals("")) bodyColor = ACS.LayoutConstants.MAINRECTANGLECOLOR;
            mainRectangle.Fill = (Brush)bc.ConvertFrom(bodyColor);
            mainRectangle.RadiusX = 4;
            mainRectangle.RadiusY = 4;
            label.FontSize=12;
            
            componentCanvas.Children.Add(mainRectangle);
            componentCanvas.Children.Add(label);
            Canvas.SetLeft(mainRectangle, ACS.LayoutConstants.MAINRECTANGLEOFFSETX);
            Canvas.SetTop(mainRectangle, ACS.LayoutConstants.MAINRECTANGLEOFFSETY);
            Canvas.SetLeft(label, 20);
            Canvas.SetTop(label, 70);
            componentCanvas.Width = ACS.LayoutConstants.COMPONENTCANVASWIDTH;
            componentCanvas.Height = ACS.LayoutConstants.COMPONENTCANVASHEIGHT;

            portsList = new OrderedDictionary();
        }

        [XmlIgnoreAttribute()]
        public OrderedDictionary PortsList {
            get {
                return portsList;
            }
            set {
                portsList = value;
            }
        }

        [XmlIgnoreAttribute()]
        public Canvas ComponentCanvas {
            get { return this.componentCanvas; }
            set { this.componentCanvas = value; }
        }

        [XmlIgnoreAttribute()]
        public Rectangle MainRectangle {
            get {return mainRectangle;}
            set {mainRectangle = value;}
        }

        [XmlIgnoreAttribute()]
        public TextBlock Label {
            get {return label;}
            set {label = value;}
        }

        /// <summary>
        /// Adding the ports to the helper collection (for faster and easier access)
        /// and positioning of the portRectangles on the componentCanvas
        /// </summary>
        /// <param name="id">Id (Name) of the component</param>
        public void InitGraphPorts(String id) {
            int counterIn = 0;
            int counterOut = 0;
            if (this.ports != null) {
                foreach (object o in this.ports) {
                    if (o is inputPortType) {
                        this.PortsList.Add(((inputPortType)o).id, (inputPortType)o);
                    } else if (o is outputPortType) {
                        this.PortsList.Add(((outputPortType)o).id, (outputPortType)o);
                    }
                }
                foreach (Object o in this.PortsList.Values) {
                    if (o is inputPortType) {
                        inputPortType pIn = (inputPortType)o;
                        componentCanvas.Children.Add(pIn.PortRectangle);
                        Canvas.SetLeft(pIn.PortRectangle, ACS.LayoutConstants.INPORTRECTANGLEOFFSETX);
                        Canvas.SetTop(pIn.PortRectangle, ACS.LayoutConstants.INPORTRECTANGLEOFFSETY + counterIn * ACS.LayoutConstants.INPORTDISTANCE);
                        counterIn++;
                        pIn.ComponentId = this.id;
                    } else {
                        outputPortType pOut = (outputPortType)o;
                        componentCanvas.Children.Add(pOut.PortRectangle);
                        Canvas.SetLeft(pOut.PortRectangle, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETX);
                        Canvas.SetTop(pOut.PortRectangle, ACS.LayoutConstants.OUTPORTRECTANGLEOFFSETY + counterOut * ACS.LayoutConstants.OUTPORTDISTANCE);
                        counterOut++;
                        pOut.ComponentId = this.id;
                    }
                }
            }

            // TODO: make the following dependent on the existance of eventports
            //Event100
            if (true) {
                Asterics.ACS.EventListenerPolygon inputEventPolygon = new Asterics.ACS.EventListenerPolygon();
                componentCanvas.Children.Add(inputEventPolygon.InputEventPortCanvas);
                Canvas.SetLeft(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETX);
                Canvas.SetTop(inputEventPolygon.InputEventPortCanvas, ACS.LayoutConstants.EVENTINPORTCANVASOFFSETY);
            }
            if (true) {
                Asterics.ACS.EventTriggerPolygon outputEventPolygon = new Asterics.ACS.EventTriggerPolygon();
                componentCanvas.Children.Add(outputEventPolygon.OutputEventPortCanvas);
                Canvas.SetLeft(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETX);
                Canvas.SetTop(outputEventPolygon.OutputEventPortCanvas, ACS.LayoutConstants.EVENTOUTPORTCANVASOFFSETY);
            }
        }
    }


    /// <summary>
    /// Defines the inports within the ports. Extended mainly with the graphical port representation
    /// </summary>
    public partial class inputPortType {
        private Rectangle portRectangle;
        private int chanelId = -1;
        private string componentId = "";
        private ACS.refType refPort;       

        private Asterics.ACS.IniFile ini;

        /// <summary>
        /// Constructor
        /// </summary>
        public inputPortType()  {
            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new Asterics.ACS.IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new Asterics.ACS.IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Asterics.ACS.Properties.Resources.IniFileNotFoundText, Asterics.ACS.Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

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
            this.descriptionField = "Input port description";
        }
        

        [XmlIgnoreAttribute()]
        public Rectangle PortRectangle {
            get { return this.portRectangle; }
            set { this.portRectangle = value; }
        }

        [XmlIgnoreAttribute()]
        public int ChannelId {
            get { return this.chanelId; }
            set { this.chanelId = value; }
        }

        [XmlIgnoreAttribute()]
        public string ComponentId {
            get { return this.componentId; }
            set { this.componentId = value; }
        }

        [XmlIgnoreAttribute()]
        public ACS.refType RefPort {
            get { return refPort; }
            set { refPort = value; }
        }

    }


    /// <summary>
    /// Defines the outports within the ports. Extended mainly with the graphical port representation
    /// </summary>
    public partial class outputPortType {
        private Rectangle portRectangle;
        private int chanelId = -1;
        private string componentId = "";
        private ACS.refType refPort;

        private Asterics.ACS.IniFile ini;

        /// <summary>
        /// Constructor
        /// </summary>
        public outputPortType() {
            // loading the asterics.ini file to get the correct colors for the components
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new Asterics.ACS.IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new Asterics.ACS.IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Asterics.ACS.Properties.Resources.IniFileNotFoundText, Asterics.ACS.Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

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
            this.descriptionField = "Output port description";
        }

        [XmlIgnoreAttribute()]
        public Rectangle PortRectangle {
            get {
                return this.portRectangle;
            }
            set {
                this.portRectangle = value;
            }
        }

        [XmlIgnoreAttribute()]
        public int ChannelId {
            get {
                return this.chanelId;
            }
            set {
                this.chanelId = value;
            }
        }

        [XmlIgnoreAttribute()]
        public string ComponentId {
            get {
                return this.componentId;
            }
            set {
                this.componentId = value;
            }
        }
       
        [XmlIgnoreAttribute()]
        public ACS.refType RefPort {
            get {
                return refPort;
            }
            set {
                refPort = value;
            }
        }
    }

}

