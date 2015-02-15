using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO.Ports;

namespace MouseApp2
{
    public partial class Form1 : Form
    {
        string speed;
        string sensitivity;
        string deadzone;
        int mouseOff = 0;

        public Form1()
        {
            InitializeComponent();
            this.Load += Form1_Load;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            var ports = SerialPort.GetPortNames();
            comboBox1.DataSource = ports;
        }

        private void Connect(string portName)
        {
            if (!serialPort1.IsOpen)
            {
                serialPort1.PortName = portName;
                serialPort1.BaudRate = 115200;
                serialPort1.Open();
                               
            }
        }


        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void button3_Click(object sender, EventArgs e) //select button
        {

            if (comboBox1.SelectedIndex > -1)
            {
                MessageBox.Show(String.Format("You selected port '{0}'", comboBox1.SelectedItem));
                Connect(comboBox1.SelectedItem.ToString());
                portStatus.Text = "Connected";
                portStatus.ForeColor = Color.Green;
            }
            else
            {
                MessageBox.Show("You must select a port to continue");
            }
        }
        
        private void sendSpeed(int speedInt)
        {
            // Clamp to maximum of 9
            speedInt = Math.Min(speedInt, 9);
            // Clamp to minimum of 1
            speedInt = Math.Max(speedInt, 1);
           
            serialPort1.Write('m' + speed + '\r');
            label4.Text = speed;
        }
        private void sendDeadzone(int deadzoneInt)
        {
            // Clamp to maximum of 9
            deadzoneInt = Math.Min(deadzoneInt, 9);
            // Clamp to minimum of 1
            deadzoneInt = Math.Max(deadzoneInt, 1);

            serialPort1.Write('d' + deadzone + '\r');
            label5.Text = deadzone;
        }


        private void trackBar1_Scroll(object sender, EventArgs e) //speed scrollbar
        {

            if (serialPort1.IsOpen)
            {
                speed = SpeedBar.Value.ToString();
                int speedInt = Convert.ToInt32(speed);
                sendSpeed(speedInt);
            }
            else if (!serialPort1.IsOpen)
            {
                MessageBox.Show("You must select a port first");
                SpeedBar.Value = Convert.ToInt32(speed);
                label4.Text = speed;
            }
        }

        private void trackBar1_Scroll_1(object sender, EventArgs e)
        {
            if (serialPort1.IsOpen)
            {
                deadzone = trackBar1.Value.ToString();
                int deadzoneInt = Convert.ToInt32(deadzone);
                sendDeadzone(deadzoneInt);
            }
            else if (!serialPort1.IsOpen)
            {
                MessageBox.Show("You must select a port first");
                trackBar1.Value = Convert.ToInt32(deadzone);
                label5.Text = deadzone;
            }
        }

        private void button1_Click(object sender, EventArgs e) //calibration button
        {
            if (serialPort1.IsOpen) 
            {
                serialPort1.Write("cal" + '\r');
            }
            else if (!serialPort1.IsOpen)
            {
                MessageBox.Show("You must select a port first");
            }
        }

        private void button2_Click(object sender, EventArgs e) // on/off button
        {

            if (serialPort1.IsOpen)
            {
                if (mouseOff == 0) //if the mouse function is on, turn it off
                {
                    button2.Text = "Turn mouse on";
                    mouseOff = 1;
                    serialPort1.Write("off" + '\r');
                }
                else if (mouseOff == 1) //if the mouse function is off, turn it on
                {
                    button2.Text = "Turn mouse off";
                    mouseOff = 0;
                    serialPort1.Write("on" + '\r');
                }
            }
            else if (!serialPort1.IsOpen)
            {
                MessageBox.Show("You must select a port first");
            }
        }

        private void button3_Click_1(object sender, EventArgs e) //disconnect button
        {
            if (serialPort1.IsOpen)
            {
                serialPort1.Close();
                portStatus.Text = "Disconnected";
                portStatus.ForeColor = Color.SlateGray;
            }
        }



                        
    }
}
