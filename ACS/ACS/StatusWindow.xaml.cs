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
 * Filename: StatusWindow.xaml.cs
 * Class(es):
 *   Classname: StatusWindow
 *   Description: Represents the status of the ARE and its components in a window
 * Author: Roland Ossmann
 * Date: 08.05.2011
 * Version: 0.4
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System.Text;
using System.Windows;
using System.Windows.Media;
using System.IO;

namespace Asterics.ACS {
    
    /// <summary>
    /// Interaction logic for StatusWindow.xaml.
    /// Represents the status of the ARE and its components in a window
    /// </summary>
    public partial class StatusWindow : Window {

        private System.Windows.Forms.DataGridView statusDataGrid;
        private componentType involvedComponent = null;

        public componentType InvolvedComponent {
            get { return involvedComponent; }
            set { 
                involvedComponent = value;
                if (involvedComponent.ComponentCanvas.Background == Brushes.Red) {
                    removeErrorButton.Visibility = Visibility.Visible;
                }
            }
        }

        public System.Windows.Forms.DataGridView StatusDataGrid {
            get { return statusDataGrid; }
            set { statusDataGrid = value; }
        }
        
        public StatusWindow() {
            InitializeComponent();

            statusDataGrid = new System.Windows.Forms.DataGridView();
            statusDataGrid.ColumnCount = 3;

            statusDataGrid.RowHeadersVisible = false;
            statusDataGrid.EditMode = System.Windows.Forms.DataGridViewEditMode.EditProgrammatically;
            //statusDataGrid.Location = new System.Drawing.Point(8, 8);
            //statusDataGrid.Size = new System.Drawing.Size(500, 250);
            statusDataGrid.Name = "statusDataGridView";
            statusDataGrid.Columns[0].Name = Properties.Resources.StatusWindowGridStatus;
            statusDataGrid.Columns[1].Name = Properties.Resources.StatusWindowGridComponent;
            statusDataGrid.Columns[2].Name = Properties.Resources.StatusWindowGridMessage;
            statusDataGrid.Columns[0].Width = 80;
            statusDataGrid.Columns[1].Width = 120;
            statusDataGrid.Columns[2].Width = (int)formsHost.Width - 80 - 120;

            this.formsHost.Child = statusDataGrid;
            statusDataGrid.Width = (int)this.formsHost.Width;
            statusDataGrid.Height = (int)this.formsHost.Height;
            statusDataGrid.ScrollBars = System.Windows.Forms.ScrollBars.Both;


            this.SizeChanged += new SizeChangedEventHandler(StatusWindow_SizeChanged);
        }

        void StatusWindow_SizeChanged(object sender, SizeChangedEventArgs e) {
            formsHost.Height = this.Height - 150;
            formsHost.Width = this.Width - 60;
            statusDataGrid.Width = (int)this.formsHost.Width;
            statusDataGrid.Height = (int)this.formsHost.Height;
            statusDataGrid.Columns[2].Width = (int)formsHost.Width - statusDataGrid.Columns[0].Width - statusDataGrid.Columns[1].Width -4;
        }

        private void clipboardButton_Click(object sender, RoutedEventArgs e) {
            Clipboard.SetDataObject(dataGridToString(), true);
        }

        private void removeErrorButton_Click(object sender, RoutedEventArgs e) {
            involvedComponent.ComponentCanvas.Background = null;
        }

        private void saveButton_Click(object sender, RoutedEventArgs e) {
            System.Windows.Forms.SaveFileDialog saveLog = new System.Windows.Forms.SaveFileDialog();
            saveLog.Filter = "Logging-Files (*.log)|*.log|All files (*.*)|*.*";
            saveLog.FilterIndex = 1;
            saveLog.RestoreDirectory = true;
            if (saveLog.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                StreamWriter sw = new StreamWriter(saveLog.FileName, false, Encoding.Default);
                sw.Write(dataGridToString());
                sw.Flush();
                sw.Close();
            }
        }

        private string dataGridToString() {
            string text = "";
            foreach (System.Windows.Forms.DataGridViewRow r in statusDataGrid.Rows) {
                text += r.Cells[0].Value + "\t";
                text += r.Cells[1].Value + "\t";
                text += r.Cells[2].Value + System.Environment.NewLine;
            }
            return text;
        }

    }
}
