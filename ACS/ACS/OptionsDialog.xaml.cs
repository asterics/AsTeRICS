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
 *   Classname: OptionsDialog
 *   Description: Representing the options dialog. Functionalities of the dialog can be foud in the ACS user manual
 * Author: Roland Ossmann
 * Date: 01.02.2011
 * Version: 0.3
 * Comments: Interaction logic for OptionsDialog.xaml
 * --------------------------------------------------------------------------------
 */

using System;
using System.Windows;
using System.Windows.Media;
using Microsoft.Samples.CustomControls;

namespace Asterics.ACS {
   
    /// <summary>
    /// Interaction logic for OptionsDialog.xaml. Representing the options dialog. 
    /// Functionalities of the dialog can be foud in the ACS user manual.
    /// </summary>
    public partial class OptionsDialog : Window {
        private MainWindow mainWindow;

        private string headerColor;
        private string groupColor;
        private string bodyColor;
        private string inPortColor;
        private string outPortColor;
        private string eventInPortColor;
        private string eventOutPortColor;

        public OptionsDialog(MainWindow mainWindow) {
            InitializeComponent();

            // set the LanguageCombo to the currently used language/culture:
            this.mainWindow = mainWindow;
            String lang;
            if (ACS.Properties.Resources.Culture != null)
                lang = ACS.Properties.Resources.Culture.Name;
            else
                lang = "en-GB";
            switch (lang) {
                case "en-GB":
                    LanguageCombo.SelectedIndex = 0;
                    break;
                case "de-AT":
                    LanguageCombo.SelectedIndex = 1;
                    break;
                case "es-ES":
                    LanguageCombo.SelectedIndex = 2;
                    break;
                case "pl-PL":
                    LanguageCombo.SelectedIndex = 3;
                    break;
            }


            // set host and port to values from ini
            HostBox.Text = mainWindow.Ini.IniReadValue("ARE", "default_host");
            PortBox.Text = mainWindow.Ini.IniReadValue("ARE", "default_port");
            if (mainWindow.Ini.IniReadValue("ARE", "enable_autodetection").Equals("true")) {
                AutodetectARERadioButton1.IsChecked = true;
                HostBox.IsEnabled = false;
            } else {
                AutodetectARERadioButton2.IsChecked = true;
                HostBox.IsEnabled = true;
            }

            // read connection timeout setting
            ConnectionTimeoutBox.Text = mainWindow.Ini.IniReadValue("ARE", "socket_timeout");
            double timeoutSliderOut = 5000;
            Double.TryParse(mainWindow.Ini.IniReadValue("ARE", "socket_timeout"), out timeoutSliderOut);
            ConnectionTimeoutSlider.Value = timeoutSliderOut;

            // read and enable status polling
            if (mainWindow.Ini.IniReadValue("ARE", "enable_status_polling").Equals("true")) {
                EnableStatusUpdateCheckBox.IsChecked = true;
                StatusUpdateSlider.IsEnabled = true;
            } else {
                EnableStatusUpdateCheckBox.IsChecked = false;
                StatusUpdateSlider.IsEnabled = false;
            }
            StatusUpdateFrequencyBox.Text = mainWindow.Ini.IniReadValue("ARE", "status_polling_frequency");
            double updateSliderOut = 5000;
            Double.TryParse(mainWindow.Ini.IniReadValue("ARE", "status_polling_frequency"), out updateSliderOut);
            StatusUpdateSlider.Value = updateSliderOut;

            if (mainWindow.Ini.IniReadValue("Options", "createBackupFile").Equals("true")) {
                EnableAutomaticBackupCheckBox.IsChecked = true;
            } else {
                EnableAutomaticBackupCheckBox.IsChecked = false;
            }

            // set checkboxes to values from ini
            if (mainWindow.Ini.IniReadValue("Options", "showNamingDialogOnComponentInsert").Equals("true")) {
                NamingDialogCheckBox.IsChecked = true;
            } else {
                NamingDialogCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showHostPortDialogOnConnect").Equals("true")) {
                HostPortDialogCheckBox.IsChecked = true;
            } else {
                HostPortDialogCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showEventChannelConnectMessage").Equals("true")) {
                EventChannelMessageCheckBox.IsChecked = true;
            } else {
                EventChannelMessageCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showAREConnectedMessage").Equals("true")) {
                AREConnectedMessageCheckBox.IsChecked = true;
            } else {
                AREConnectedMessageCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showOverrideModelQuestion").Equals("true")) {
                OverrideModelQuestionCheckBox.IsChecked = true;
            } else {
                OverrideModelQuestionCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showOverrideLocalModelQuestion").Equals("true")) {
                OverrideLocalModelQuestionCheckBox.IsChecked = true;
            } else {
                OverrideLocalModelQuestionCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showOverrideLocalWhenConnected").Equals("true")) {
                OverrideModelFromAREAtConnectQuestionCheckBox.IsChecked = true;
            } else {
                OverrideModelFromAREAtConnectQuestionCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showOverrideAndRunLocalWhenConnected").Equals("true")) {
                OverrideAndRunFromAREAtConnectQuestionCheckBox.IsChecked = true;
            } else {
                OverrideAndRunFromAREAtConnectQuestionCheckBox.IsChecked = false;
            }
            if (mainWindow.Ini.IniReadValue("Options", "showOverrideComponentCollectionQuestion").Equals("true")) {
                OverrideComponentCollectionQuestionCheckBox.IsChecked = true;
            } else {
                OverrideComponentCollectionQuestionCheckBox.IsChecked = false;
            }

            // set the colors to the ones currently used (if no settings can be found in the ini, the default colors are used):
            BrushConverter bc = new BrushConverter();
            headerColor = mainWindow.Ini.IniReadValue("Layout", "headercolor");
            if (headerColor.Equals("")) headerColor = ACS.LayoutConstants.TOPRECTANGLECOLOR;
            groupColor = mainWindow.Ini.IniReadValue("Layout", "groupcolor");
            HeaderColorRectangle.Fill = (Brush)bc.ConvertFrom(headerColor);
            if (groupColor.Equals(""))
            groupColor = ACS.LayoutConstants.GROUPRECTANGLECOLOR;
            GroupColorRectangle.Fill = (Brush)bc.ConvertFrom(groupColor);
            bodyColor = mainWindow.Ini.IniReadValue("Layout", "bodycolor");
            if (bodyColor.Equals("")) bodyColor = ACS.LayoutConstants.MAINRECTANGLECOLOR;
            BodyColorRectangle.Fill = (Brush)bc.ConvertFrom(bodyColor);
            inPortColor = mainWindow.Ini.IniReadValue("Layout", "inportcolor");
            if (inPortColor.Equals("")) inPortColor = ACS.LayoutConstants.INPORTRECTANGLECOLOR;
            InPortColorRectangle.Fill = (Brush)bc.ConvertFrom(inPortColor);
            outPortColor = mainWindow.Ini.IniReadValue("Layout", "outportcolor");
            if (outPortColor.Equals("")) outPortColor = ACS.LayoutConstants.OUTPORTRECTANGLECOLOR;
            OutPortColorRectangle.Fill = (Brush)bc.ConvertFrom(outPortColor);
            eventInPortColor = mainWindow.Ini.IniReadValue("Layout", "eventinportcolor");
            if (eventInPortColor.Equals("")) eventInPortColor = ACS.LayoutConstants.EVENTINPORTCOLOR;
            EventInPortColorRectangle.Fill = (Brush)bc.ConvertFrom(eventInPortColor);
            eventOutPortColor = mainWindow.Ini.IniReadValue("Layout", "eventoutportcolor");
            if (eventOutPortColor.Equals("")) eventOutPortColor = ACS.LayoutConstants.EVENTOUTPORTCOLOR;
            EventOutPortColorRectangle.Fill = (Brush)bc.ConvertFrom(eventOutPortColor);

            //creationToolPathText.Text = mainWindow.Ini.IniReadValue("Options", "pathToPluginCreationTool");
            //activationToolPathText.Text = mainWindow.Ini.IniReadValue("Options", "pathToPluginActivationTool");

        }

        private void LayoutResetButton_Click(object sender, RoutedEventArgs e) {
            mainWindow.RestoreDefaultLayout(sender, e);
        }

        private void OKButton_Click(object sender, RoutedEventArgs e) {
            // check inputs and return to dialog in case of faulty input
            int port;
            if (HostBox.Text.Length == 0) {
                ErrorTextBlock.Text = Properties.Resources.OptionsDialogHostEmpty;
                return;
            } else if (PortBox.Text.Length == 0) {
                ErrorTextBlock.Text = Properties.Resources.OptionsDialogPortEmpty;
                return;
            } else if (!Int32.TryParse(PortBox.Text, out port)) {
                ErrorTextBlock.Text = Properties.Resources.OptionsDialogPortInvalid;
                return;
            }

            // save all values in the asterics.ini:
            switch (LanguageCombo.SelectedIndex) {
                case 0:
                    mainWindow.Ini.IniWriteValue("Options", "language", "en-GB");
                    break;
                case 1:
                    mainWindow.Ini.IniWriteValue("Options", "language", "de-AT");
                    break;
                case 2:
                    mainWindow.Ini.IniWriteValue("Options", "language", "es-ES");
                    break;
                case 3:
                    mainWindow.Ini.IniWriteValue("Options", "language", "pl-PL");
                    break;
            }

            mainWindow.Ini.IniWriteValue("ARE", "default_host", HostBox.Text);
            mainWindow.Ini.IniWriteValue("ARE", "default_port", PortBox.Text);
            if (AutodetectARERadioButton1.IsChecked != null && (bool)AutodetectARERadioButton1.IsChecked) {
                mainWindow.Ini.IniWriteValue("ARE", "enable_autodetection", "true");
            } else {
                mainWindow.Ini.IniWriteValue("ARE", "enable_autodetection", "false");
            }
            mainWindow.Ini.IniWriteValue("ARE", "socket_timeout", ConnectionTimeoutBox.Text);
            if (EnableStatusUpdateCheckBox.IsChecked != null && (bool)EnableStatusUpdateCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("ARE", "enable_status_polling", "true");
            } else {
                mainWindow.Ini.IniWriteValue("ARE", "enable_status_polling", "false");
            }
            mainWindow.Ini.IniWriteValue("ARE", "status_polling_frequency", StatusUpdateFrequencyBox.Text);
            if (EnableAutomaticBackupCheckBox.IsChecked != null && (bool)EnableAutomaticBackupCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "createBackupFile", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "createBackupFile", "false");
            }

            if (NamingDialogCheckBox.IsChecked != null && (bool)NamingDialogCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showNamingDialogOnComponentInsert", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showNamingDialogOnComponentInsert", "false");
            }
            if (HostPortDialogCheckBox.IsChecked != null && (bool)HostPortDialogCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showHostPortDialogOnConnect", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showHostPortDialogOnConnect", "false");
            }
            if (EventChannelMessageCheckBox.IsChecked != null && (bool)EventChannelMessageCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showEventChannelConnectMessage", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showEventChannelConnectMessage", "false");
            }
            if (AREConnectedMessageCheckBox.IsChecked != null && (bool)AREConnectedMessageCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showAREConnectedMessage", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showAREConnectedMessage", "false");
            }
            if (OverrideModelQuestionCheckBox.IsChecked != null && (bool)OverrideModelQuestionCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideModelQuestion", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideModelQuestion", "false");
            }
            if (OverrideLocalModelQuestionCheckBox.IsChecked != null && (bool)OverrideLocalModelQuestionCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideLocalModelQuestion", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideLocalModelQuestion", "false");
            }
            if (OverrideModelFromAREAtConnectQuestionCheckBox.IsChecked != null && (bool)OverrideModelFromAREAtConnectQuestionCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideLocalWhenConnected", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideLocalWhenConnected", "false");
            }
            if (OverrideAndRunFromAREAtConnectQuestionCheckBox.IsChecked != null && (bool)OverrideAndRunFromAREAtConnectQuestionCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideAndRunLocalWhenConnected", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideAndRunLocalWhenConnected", "false");
            }
            if (OverrideComponentCollectionQuestionCheckBox.IsChecked != null && (bool)OverrideComponentCollectionQuestionCheckBox.IsChecked) {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideComponentCollectionQuestion", "true");
            } else {
                mainWindow.Ini.IniWriteValue("Options", "showOverrideComponentCollectionQuestion", "false");
            }

            mainWindow.Ini.IniWriteValue("Layout", "headercolor", headerColor);
            mainWindow.Ini.IniWriteValue("Layout", "groupcolor", groupColor);
            mainWindow.Ini.IniWriteValue("Layout", "bodycolor", bodyColor);
            mainWindow.Ini.IniWriteValue("Layout", "inportcolor", inPortColor);
            mainWindow.Ini.IniWriteValue("Layout", "outportcolor", outPortColor);
            mainWindow.Ini.IniWriteValue("Layout", "eventinportcolor", eventInPortColor);
            mainWindow.Ini.IniWriteValue("Layout", "eventoutportcolor", eventOutPortColor);

            // update values in MainWindow
            mainWindow.UpdateColors();
            mainWindow.ShowNamingDialogOnComponentInsert = (bool)NamingDialogCheckBox.IsChecked;
            mainWindow.ShowHostPortDialogOnConnect = (bool)HostPortDialogCheckBox.IsChecked;
            mainWindow.ShowEventChannelConnectMessage = (bool)EventChannelMessageCheckBox.IsChecked;
            mainWindow.showAREConnectedMessage = (bool)AREConnectedMessageCheckBox.IsChecked;
            mainWindow.showOverrideModelQuestion = (bool)OverrideModelQuestionCheckBox.IsChecked;
            mainWindow.showOverrideLocalModelQuestion = (bool)OverrideLocalModelQuestionCheckBox.IsChecked;
            mainWindow.showOverrideAtConnectionQuestion = (bool)OverrideModelFromAREAtConnectQuestionCheckBox.IsChecked;
            mainWindow.showOverrideAndRunAtConnectionQuestion = (bool)OverrideAndRunFromAREAtConnectQuestionCheckBox.IsChecked;
            mainWindow.showOverrideComponentCollectionQuestion = (bool)OverrideComponentCollectionQuestionCheckBox.IsChecked;

            //mainWindow.Ini.IniWriteValue("Options", "pathToPluginCreationTool", creationToolPathText.Text);
            //mainWindow.Ini.IniWriteValue("Options", "pathToPluginActivationTool", activationToolPathText.Text);

            this.DialogResult = true;
        }

        private SolidColorBrush ChooseColor(String color) {
            ColorPickerDialog colorPicker = new ColorPickerDialog();
            colorPicker.Height = 425;
            Color actCol;
            try {
                actCol = (Color)ColorConverter.ConvertFromString(color);
            } catch {
                actCol = (Color)ColorConverter.ConvertFromString("#ffffffff");
            }
            colorPicker.StartingColor = actCol;

            bool? result = colorPicker.ShowDialog();

            if (result != null && (bool)result == true) {
                return new SolidColorBrush(colorPicker.SelectedColor);
            } else {
                return new SolidColorBrush(actCol);
            }
        }

        private void HeaderColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(headerColor);
            HeaderColorRectangle.Fill = scb;
            headerColor = scb.ToString();
        }
        
        private void GroupColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(headerColor);
            GroupColorRectangle.Fill = scb;
            groupColor = scb.ToString();
        }

        private void BodyColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(bodyColor);
            BodyColorRectangle.Fill = scb;
            bodyColor = scb.ToString();
        }

        private void InPortColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(inPortColor);
            InPortColorRectangle.Fill = scb;
            inPortColor = scb.ToString();
        }

        private void OutPortColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(outPortColor);
            OutPortColorRectangle.Fill = scb;
            outPortColor = scb.ToString();
        }

        private void EventInPortColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(eventInPortColor);
            EventInPortColorRectangle.Fill = scb;
            eventInPortColor = scb.ToString();
        }

        private void EventOutPortColorButton_Click(object sender, RoutedEventArgs e) {
            SolidColorBrush scb = ChooseColor(eventOutPortColor);
            EventOutPortColorRectangle.Fill = scb;
            eventOutPortColor = scb.ToString();
        }

        private void EnableStatusUpdateCheckBox_Checked(object sender, RoutedEventArgs e) {
            //StatusUpdateFrequencyBox.IsEnabled = true;
            StatusUpdateSlider.IsEnabled = true;
        }
        
        private void EnableStatusUpdateCheckBox_Unchecked(object sender, RoutedEventArgs e) {
            //StatusUpdateFrequencyBox.IsEnabled = false;
            StatusUpdateSlider.IsEnabled = false;
        }

        private void AutodetectARERadioButton2_Checked(object sender, RoutedEventArgs e) {
            HostBox.IsEnabled = true;
        }

        private void AutodetectARERadioButton2_Unchecked(object sender, RoutedEventArgs e) {
            HostBox.IsEnabled = false;
        }

        private void SetCreationWizardPath_Click(object sender, RoutedEventArgs e) {
            System.Windows.Forms.OpenFileDialog fileDialog = new System.Windows.Forms.OpenFileDialog();
            //openLocalXML.InitialDirectory = "c:\\temp\\" ;
            fileDialog.Filter = "AsTeRICS_PluginCreationWizard.exe|AsTeRICS_PluginCreationWizard.exe|All files (*.*)|*.*";
            fileDialog.FilterIndex = 1;
            fileDialog.RestoreDirectory = true;
            if (fileDialog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                //creationToolPathText.Text = fileDialog.FileName;
            }
        }

        private void SetActivationWizardPath_Click(object sender, RoutedEventArgs e) {
            System.Windows.Forms.OpenFileDialog fileDialog = new System.Windows.Forms.OpenFileDialog();
            //openLocalXML.InitialDirectory = "c:\\temp\\" ;
            fileDialog.Filter = "AsTeRICS_PluginActivation.exe|AsTeRICS_PluginActivation.exe|All files (*.*)|*.*";
            fileDialog.FilterIndex = 1;
            fileDialog.RestoreDirectory = true;
            if (fileDialog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                //activationToolPathText.Text = fileDialog.FileName;
            }

        }

        private void StatusUpdateSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e) {            
            StatusUpdateFrequencyBox.Text = ((int)e.NewValue).ToString();
        }
        
        private void ConnectionTimeoutSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e) {
            ConnectionTimeoutBox.Text = ((int)e.NewValue).ToString();
        }

    }
}
