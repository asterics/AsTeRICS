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
 * Filename: ConnectAREDialog.cs
 * Class(es):
 *   Classname: ConnectAREDialog
 *   Description: Hosting of the ARE connection dialog. This dialog is used, setting the ip and port of the ARE to connect
 * Author: Roland Ossmann
 * Date: 03.09.2010
 * Version: 0.2
 * Comments: Partial class of ConnectAREDialog, other parts of this class in file
 *   ConnectAREDialog.xaml.
 * --------------------------------------------------------------------------------
 */


using System.Windows;
using System.Windows.Controls;

namespace Asterics.ACS {
    /// <summary>
    /// Interaction logic for ConnectAREDialog.xaml.
    /// Hosting of the ARE connection dialog. This dialog is used, setting the ip and port of the ARE to connect.
    /// </summary>
    public partial class ConnectAREDialog : Window
    {
        public ConnectAREDialog() {
            InitializeComponent();

        }

        void okButton_Click(object sender, RoutedEventArgs e) {
            if (Validation.GetHasError(this)) {
                this.errorField.Text = "Error: invalid input";
                return;
            }
            this.DialogResult = true;
        }

        void cancelButton_Click(object sender, RoutedEventArgs e) {
            // Dialog box canceled
            this.DialogResult = false;
        }
    }
}
