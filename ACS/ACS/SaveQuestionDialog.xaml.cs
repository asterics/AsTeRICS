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
 * Filename: SaveQuestionDialog.xaml.cs
 * Class(es):
 *   Classname: SaveQuestionDialog
 *   Description: Representing the "save model?" question
 * Author: Roland Ossmann
 * Date: 26.02.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System;
using System.Windows;

namespace Asterics.ACS {
    /// <summary>
    /// Interaction logic for SaveDialog.xaml.
    /// Representing the "save model?" question
    /// </summary>
    public partial class SaveQuestionDialog : Window {

        String result;
        public const string save = "save";
        public const string dontSave = "dontSave";
        public const string cancel = "cancel";

        public String Result {
            get {
                return result;
            }
            set {
                result = value;
            }
        }
        
        public SaveQuestionDialog() {
            InitializeComponent();
            result = cancel;
        }

        private void buttonSave_Click(object sender, RoutedEventArgs e) {
            result = save;
            this.DialogResult = true;
        }

        private void buttonDontSave_Click(object sender, RoutedEventArgs e) {
            result = dontSave;
            this.DialogResult = true;
        }
    }
}
