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
 * Filename: LayoutConstants.cs
 * Class(es):
 *   Classname: LayoutConstants
 *   Description: Constants for the graphical layout
 * Author: Roland Ossmann
 * Date: 10.09.2010
 * Version: 0.2
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Asterics.ACS {

    /// <summary>
    /// Layout constants for the ACS drawing canvas and the components
    /// </summary>
    class LayoutConstants {
        public static string DEFAULTSTROKECOLOR = "#eb000000";
        // Main Rectangle
        public static int MAINRECTANGLEHEIGHT = 100;
        public static int MAINRECTANGLEWIDTH = 85;
        public static int MAINRECTANGLENUMBEROFPORTS = 4;
        public static int MAINRECTANGLEOFFSETX = 15; 
        public static int MAINRECTANGLEOFFSETY = 48; 
        public static string MAINRECTANGLECOLOR = "#c08cd3d3";
        // Top Rectangle (Label)
        public static int TOPRECTANGLEHEIGHT = 40;
        public static int TOPRECTANGLEWIDTH = 85;
        public static int TOPRECTANGLEOFFSETX = 15;
        public static int TOPRECTANGLEOFFSETY = 13; 
        public static string TOPRECTANGLECOLOR = "#fffffe2e";
        public static string GROUPRECTANGLECOLOR = "#FFFF972E";
        public static int TOPGRIDHEIGHT = 35;
        public static int TOPGRIDWIDTH = 85;
        public static int TOPGRIDOFFSETX = 15;
        public static int TOPGRIDOFFSETY = 13;
        public static int LABELFONTSIZE = 10;
        // Component Canvas
        public static int COMPONENTCANVASWIDTH = 115;
        public static int COMPONENTCANVASHEIGHT = 160;
        // In Port
        public static int INPORTRECTANGLEWIDTH = 20;
        public static int INPORTRECTANGLEHEIGHT = 10;
        public static string INPORTRECTANGLECOLOR = "#C06495ED";
        public static int INPORTRECTANGLEOFFSETX = 5;
        public static int INPORTRECTANGLEOFFSETY = 58;
        public static int INPORTDISTANCE = 20;
        // Out Port
        public static int OUTPORTRECTANGLEWIDTH = 20;
        public static int OUTPORTRECTANGLEHEIGHT = 10;
        public static string OUTPORTRECTANGLECOLOR = "#C0B22222";
        public static int OUTPORTRECTANGLEOFFSETX = 90;
        //public static int OUTPORTRECTANGLEOFFSETY = 58;
        public static int OUTPORTRECTANGLEOFFSETY = 68;
        public static int OUTPORTDISTANCE = 20;
        // Port Label
        public static int PORTLABELFONTSIZE = 9;
        public static int INPORTLABELWIDTH = 55;
        public static int OUTPORTLABELWIDTH = 55;
        // Eventport
        public static int EVENTPORTWIDTH = 8;
        public static int EVENTPORTHEIGHT = 15;
        public static string EVENTINPORTCOLOR = "#C019FC02";
        public static string EVENTOUTPORTCOLOR = "#C0C904FA";
        public static int EVENTINPORTCANVASOFFSETX = 30;
        public static int EVENTINPORTCANVASOFFSETY = 140;
        public static int EVENTOUTPORTCANVASOFFSETX = 70;
        public static int EVENTOUTPORTCANVASOFFSETY = 140;
    }
}
