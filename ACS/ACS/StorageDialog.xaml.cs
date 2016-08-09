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
 * Filename: StorageDialog.xaml.cs
 * Class(es):
 *   Classname: StorageDialog
 *   Description: Dialog to store models in the ARE storage
 * Author: Roland Ossmann
 * Date: 26.04.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */
using System.Windows;
using System.Windows.Input;

namespace Asterics.ACS {

    /// <summary>
    /// Interaction logic for StorageDialog.xaml.
    /// Dialog to store models in the ARE storage
    /// </summary>
    public partial class StorageDialog : Window {
        public StorageDialog() {
            InitializeComponent();
        }

        
        private void okButton_Click(object sender, RoutedEventArgs e) {
            if (filenameTextbox.Text != "") {
                this.DialogResult = true;
                this.Close();
            }
        }

        private void cancelButton_Click(object sender, RoutedEventArgs e) {
            filenameTextbox.Text = "";
            this.Close();
        }

        private void filenameTextbox_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Enter) {
                if (filenameTextbox.Text != "") {
                    this.Close();
                }
            }
        }

    }
}
