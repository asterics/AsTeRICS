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
 * Filename: LogfileWindow.xaml.cs
 * Class(es):
 *   Classname: modelComponent
 *   Description: Representing the logging file in a Window
 * Author: Roland Ossmann
 * Date: 22.05.2011
 * Version: 0.4
 * Comments: Interaction logic for LogfileWindow.xaml
 * --------------------------------------------------------------------------------
 */

using System.Text;
using System.Windows;
using System.IO;

namespace Asterics.ACS {
    
    /// <summary>
    /// Representing the logging file in a Window
    /// </summary>
    public partial class LogfileWindow : Window {
        public LogfileWindow() {
            InitializeComponent();

            this.SizeChanged += new SizeChangedEventHandler(LogfileWindow_SizeChanged);

        }

        void LogfileWindow_SizeChanged(object sender, SizeChangedEventArgs e) {
            textBlock.Height = this.Height-150;
            textBlock.Width = this.Width-60;
        }

        private void clipboardButton_Click(object sender, RoutedEventArgs e) {
            Clipboard.SetDataObject(textBlock.Text, true);
        }

        private void saveButton_Click(object sender, RoutedEventArgs e) {
            System.Windows.Forms.SaveFileDialog saveLog = new System.Windows.Forms.SaveFileDialog();
            saveLog.Filter = "Logging-Files (*.log)|*.log|All files (*.*)|*.*";
            saveLog.FilterIndex = 1;
            saveLog.RestoreDirectory = true;
            if (saveLog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                StreamWriter sw = new StreamWriter(saveLog.FileName, false, Encoding.Default);
                sw.Write(textBlock.Text);
                sw.Flush();
                sw.Close();
            }

        }
    }
}
