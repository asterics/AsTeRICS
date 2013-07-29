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
 *   Classname: CustomMessageBox.xaml.cs
 *   Description: Defining a message box with a "show next time" checkbox
 * Author: Roland Ossmann
 * Date: 01.02.2011
 * Version: 0.3
 * Comments: The message box can be Info, Warning or Error, with an Ok button or Yes/No buttons
 * --------------------------------------------------------------------------------
 */

using System.Windows;
using System.Windows.Media;

namespace Asterics.ACS {
   
    /// <summary>
    /// A message window with a "show next time" checkbox
    /// </summary>
    public partial class CustomMessageBox : Window {

        public enum messageType {Info, Error, Warning, Question};
        public enum resultType {OK, YesNo};

        public CustomMessageBox(string messageText, string messageBoxTitle, messageType mType, resultType rType) {
            InitializeComponent();

            messageTextBlock.Text = messageText;
            this.Title = messageBoxTitle;
            switch (mType) {
                case messageType.Info:
                    messageIcon.Source = new ImageSourceConverter().ConvertFromString("pack://application:,,,/ACS;component/images/info.png") as ImageSource;
                    break;
                case messageType.Error:
                    messageIcon.Source = new ImageSourceConverter().ConvertFromString("pack://application:,,,/ACS;component/images/error.png") as ImageSource;
                    break;
                case messageType.Warning:
                    messageIcon.Source = new ImageSourceConverter().ConvertFromString("pack://application:,,,/ACS;component/images/Warning.png") as ImageSource;
                    break;
                case messageType.Question:
                    messageIcon.Source = new ImageSourceConverter().ConvertFromString("pack://application:,,,/ACS;component/images/Question.png") as ImageSource;
                    break;
                default:
                    break;
            }

            switch (rType) {
                case resultType.OK:
                    okButton.Visibility = Visibility.Visible;
                    noButton.Visibility = Visibility.Hidden;
                    yesButton.Visibility = Visibility.Hidden;
                    break;
                case resultType.YesNo:
                    okButton.Visibility = Visibility.Hidden;
                    noButton.Visibility = Visibility.Visible;
                    yesButton.Visibility = Visibility.Visible;
                    break;
                default:
                    break;
            }
        }

        private void okButton_Click(object sender, RoutedEventArgs e) {
            this.DialogResult = true;
        }

        private void yesButton_Click(object sender, RoutedEventArgs e) {
            this.DialogResult = true;
        }

    }
}
