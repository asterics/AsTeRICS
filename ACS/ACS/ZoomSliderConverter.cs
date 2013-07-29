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
 * Filename: ZoomSliderConverter.cs
 * Class(es):
 *   Classname: ZoomSliderConverter
 *   Description: Converting double values to string for the Zoom slider
 * Author: Roland Ossmann
 * Date: 14.03.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System;
using System.Windows.Data;
using System.Globalization;

namespace Asterics.ACS {

    /// <summary>
    /// Converting double values to string for the Zoom slider
    /// </summary>
    [ValueConversion(typeof(double), typeof(String))]
    public class ZoomSliderConverter : IValueConverter {
        
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture) {
            double dValue=0;
            if (value is double) {
                dValue = (double)value * 100;
            }
            return dValue.ToString()+"%";
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture) {
            string strValue = value as string;
            double dValue;
            strValue.Replace('%',' ');
            if (Double.TryParse(strValue, out dValue)) {
                return dValue / 100;
            }
            return 1;
        }
    }

}
