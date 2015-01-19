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
 * Filename: GUIProperties.cs
 * Class(es):
 *   Classname: GUIProperties
 *   Description: A calss to store the properties of the GUI editor and the Properties of the ARE window 
 * Author: Roland Ossmann
 * Date: 20.03.2013
 * Version: 0.1
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;



namespace Asterics.ACS {
    
    
    /// <summary>
    /// The Properties for the GUI editor and the ARE window
    /// Initialization and default properties are set in MainWindow.xaml.cs, NewAREGUIWindow()
    /// </summary>
    class GUIProperties:Observable {
    
        public enum GridStep {small = 5, medium = 10, large = 15, huge = 25};

        public enum ScreenResolution { FiveFour, SixteenNine, FourThree };

        bool enableGrid = true;  //// CV:set to true by default

        [Category("Editor Properties")]
        public bool EnableGrid {
            get { return enableGrid; }
            set { enableGrid = value; }
        }

        bool showGrid = true;  //// CV:set to true by default

        [Category("Editor Properties")]
        public bool ShowGrid {
            get { return showGrid; }
            set { showGrid = value; OnPropertyChanged("showGrid"); }
        }

        GridStep step = GridStep.small;    //// CV:set to small by default

        [Category("Editor Properties")]
        public GridStep GridSteps {
            get { return step; }
            set { step = value; OnPropertyChanged("step"); }
        }

        ScreenResolution screenRes = ScreenResolution.SixteenNine;

        [Category("Editor Properties")]
        public ScreenResolution ScreenRes {
            get { return screenRes; }
            set { screenRes = value; OnPropertyChanged("screenRes"); }
        }

        bool decoration;

        [Category("ARE Properties")]
        public bool Decoration {
            get { return decoration; }
            set { decoration = value; OnPropertyChanged("decoration"); }
        }

        bool fullscreen;

        [Category("ARE Properties")]
        public bool Fullscreen {
            get { return fullscreen; }
            set { fullscreen = value; OnPropertyChanged("fullscreen"); }
        }

        bool alwaysOnTop;

        [Category("ARE Properties")]
        public bool AlwaysOnTop {
            get { return alwaysOnTop; }
            set { alwaysOnTop = value; OnPropertyChanged("alwaysOnTop"); }
        }

        bool toSystemTray;

        [Category("ARE Properties")]
        public bool ToSystemTray {
            get { return toSystemTray; }
            set { toSystemTray = value; OnPropertyChanged("toSystemTray"); }
        }

        bool showControlPanel;

        [Category("ARE Properties")]
        public bool ShowControlPanel {
            get { return showControlPanel; }
            set { showControlPanel = value; OnPropertyChanged("showControlPanel"); }
        }

        

    }
}
