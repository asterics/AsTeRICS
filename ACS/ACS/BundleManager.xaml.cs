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
 * Filename: OptionsDialog.xaml.cs
 * Class(es):
 *   Classname: BundleManager
 *   Description: Representing the bundle management dialog. Functionalities of the dialog can be foud in the ACS user manual
 * Author: Roland Ossmann
 * Date: 15.05.2011
 * Version: 0.1
 * Comments: Interaction logic for BundleManager.xaml
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.IO;

namespace Asterics.ACS {


    /// <summary>
    /// Interaction logic for BundleManager.xaml
    /// Within this dialog, stored bundles can be set active and downloaded bundles can be saved.
    /// </summary>
    public partial class BundleManager : Window {

        private int selectedBundleID = -1;

        public int SelectedBundleID {
            get { return selectedBundleID; }
            set { selectedBundleID = value; }
        }

        private IniFile mainIni;

        public IniFile MainIni {
            get { return mainIni; }
            set { mainIni = value; }
        }


        public BundleManager() {
            InitializeComponent();
        }

        private void okButton_Click(object sender, RoutedEventArgs e) {
            selectedBundleID = bundlesListbox.SelectedIndex;
            this.Close();
            
        }
        
        private void defaultButton_Click(object sender, RoutedEventArgs e) {
            selectedBundleID = -2;
            mainIni.IniWriteValue("model", "bundle_model_startup", "defaultComponentCollection.abd");
            this.Close();
        }

        private void autostartButton_Click(object sender, RoutedEventArgs e) {
            string filename;
            try {
                if (mainIni.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                    filename = Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\" + activeBundleTextBox.Text;
                } else {
                    filename = AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\" + activeBundleTextBox.Text;
                }
                filename += ".abd";
                File.Copy(System.IO.Path.GetTempPath() + "tempBundle.xml", filename, true);
                mainIni.IniWriteValue("model", "bundle_model_startup", filename);
                this.Close();               
            } catch (Exception) {
                MessageBox.Show(Properties.Resources.SaveBundleError, Properties.Resources.SaveBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }

        }
        
        private void saveButton_Click(object sender, RoutedEventArgs e) {
            string filename;
            try {
                if (mainIni.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                    filename = Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\" + activeBundleTextBox.Text;
                } else {
                    filename = AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\" + activeBundleTextBox.Text;
                }
                filename += ".abd";
                File.Copy(System.IO.Path.GetTempPath() + "tempBundle.xml", filename, true);
                this.Close();
            } catch (Exception) {
                MessageBox.Show(Properties.Resources.SaveBundleError, Properties.Resources.SaveBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void cancelButton_Click(object sender, RoutedEventArgs e) {
            this.Close();
        }


    }
}
