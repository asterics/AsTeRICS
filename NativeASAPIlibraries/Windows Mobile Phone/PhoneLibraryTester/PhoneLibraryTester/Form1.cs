using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace PhoneLibraryTester
{
    public enum PhoneState
    {
        PS_IDLE = 1, 
        PS_RING = 2, 
        PS_CONNECTED = 3
    }
    
    public partial class Form1 : Form
    {
        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void DeviceFound(UInt64 deviceAddress, [MarshalAs(UnmanagedType.LPWStr)] string deviceName, IntPtr param);
        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void NewSMS([MarshalAs(UnmanagedType.LPWStr)] string PhoneID, [MarshalAs(UnmanagedType.LPWStr)] string subject, IntPtr param);
        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void PhoneStateChanged(PhoneState phoneState, [MarshalAs(UnmanagedType.LPWStr)] string phoneID, IntPtr param);

        public DeviceFound DeviceFoundDelegate;
        public NewSMS NewSMSDelegate;
        public PhoneStateChanged PhoneStateChangedDelegate;

        [DllImport("PhoneLibrary.dll",CharSet=CharSet.Unicode)]
        private static extern Int32 init(DeviceFound deviceFound, NewSMS newSMS, PhoneStateChanged phoneStateChanged, IntPtr param);

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 searchDevices();

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 connectToDevice(UInt64 deviceAddress, Int32 port);

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 disconnect();

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 close();

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 makePhoneCall (string recipientID);

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 acceptCall();

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 dropCall();

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 sendSMS(string recipientID, string subject);

        [DllImport("PhoneLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 getPhoneState(ref PhoneState phoneState);

        public void deviceFound(UInt64 deviceAddress, string deviceName, IntPtr param)
        {
            if (deviceAddress != 0)
            {
                BluetootDevice bd = new BluetootDevice();
                bd.address = deviceAddress;
                bd.name = deviceName.ToString();
                listBox1.Invoke((MethodInvoker)delegate() { listBox1.Items.Add(bd); });
            }
            else
            {
                progressBar1.Invoke((MethodInvoker)delegate() { progressBar1.Style = ProgressBarStyle.Blocks; });
            }
        }

        public void newSMS(string PhoneID, string subject, IntPtr param)
        {
            textBox17.Invoke((MethodInvoker)delegate() { textBox17.Text = PhoneID; });
            textBox18.Invoke((MethodInvoker)delegate() { textBox18.Text = subject; });
        }

        public void phoneStateChanged(PhoneState phoneState, string phoneID, IntPtr param)
        {
            string s="";
            if (phoneState == PhoneState.PS_IDLE)
            {
                s = "Idle";
            }
            else
            {
                if (phoneState == PhoneState.PS_CONNECTED)
                {
                    s = "Connected to " + phoneID;
                }
                else
                {
                    s = phoneID + " RING !!!";
                }
            }

            textBox7.Invoke((MethodInvoker)delegate() { textBox7.Text=s; });

        }

        public Form1()
        {
            InitializeComponent();
            DeviceFoundDelegate = new DeviceFound(deviceFound);
            NewSMSDelegate = new NewSMS(newSMS);
            PhoneStateChangedDelegate = new PhoneStateChanged(phoneStateChanged);
        }

        

        private void button1_Click(object sender, EventArgs e)
        {

            Int32 result = init(DeviceFoundDelegate, NewSMSDelegate, PhoneStateChangedDelegate, (IntPtr)0);
            if (result < 0)
            {
                textBox1.Text = result.ToString();
            }
            else
            {
                textBox1.Text = @"OK";
                panel1.Enabled = true;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Int32 result = close();
            if (result < 0)
            {
                textBox2.Text = result.ToString();
            }
            else
            {
                textBox2.Text = @"OK";
                panel1.Enabled = false;
                panel2.Enabled = false;
                panel3.Enabled = false;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            listBox1.Items.Clear();
            
            Int32 result = searchDevices();

            if (result < 0)
            {
                textBox3.Text = result.ToString();
            }
            else
            {
                textBox3.Text = @"OK";
                progressBar1.Style = ProgressBarStyle.Marquee;
            }

           

            
        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex > -1)
            {
                BluetootDevice db = (BluetootDevice) listBox1.Items[listBox1.SelectedIndex];
                textBox4.Text = db.address.ToString("X");
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {

            UInt64 address = 0;
            Int32 port = -1; //default;

            bool parseError = false;

            try
            {
                address = UInt64.Parse(textBox4.Text, System.Globalization.NumberStyles.HexNumber);

                if (checkBox1.Checked == false)
                {

                    port = Int32.Parse(textBox5.Text);
                }
            }
            catch (Exception)
            {
                parseError = true;
            }

            if (parseError == false)
            {

                Int32 result = connectToDevice(address, port);

                if (result < 0)
                {
                    textBox6.Text = result.ToString();
                }
                else
                {
                    textBox6.Text = @"OK";
                    panel2.Enabled = true;
                    panel3.Enabled = true;
                    panel1.Enabled = false;
                }
            }
            else
            {
                textBox6.Text = "E";
            }

        }

        private void button6_Click(object sender, EventArgs e)
        {
            Int32 result = makePhoneCall(textBox9.Text);

            if (result < 0)
            {
                textBox10.Text = result.ToString();
            }
            else
            {
                textBox10.Text = @"OK";
            }
        }

        private void button7_Click(object sender, EventArgs e)
        {
            Int32 result = dropCall();

            if (result < 0)
            {
                textBox11.Text = result.ToString();
            }
            else
            {
                textBox11.Text = @"OK";
            }
        }

        private void button8_Click(object sender, EventArgs e)
        {
            Int32 result = acceptCall();

            if (result < 0)
            {
                textBox12.Text = result.ToString();
            }
            else
            {
                textBox12.Text = @"OK";
            }
        }

        private void button9_Click(object sender, EventArgs e)
        {
            Int32 result = sendSMS(textBox15.Text,textBox16.Text);

            if (result < 0)
            {
                textBox13.Text = result.ToString();
            }
            else
            {
                textBox13.Text = @"OK";
            }
        }

        private void button10_Click(object sender, EventArgs e)
        {
            PhoneState state = new PhoneState();


            Int32 result = getPhoneState(ref state);

            if (result < 0)
            {
                textBox14.Text = result.ToString();
            }
            else
            {
                switch (state)
                {
                    case PhoneState.PS_CONNECTED:
                        {
                            textBox14.Text = "Connected";
                            break;
                        }
                    case PhoneState.PS_IDLE:
                        {
                            textBox14.Text = "Idle";
                            break;
                        }
                    case PhoneState.PS_RING:
                        {
                            textBox14.Text = "RING !!!";
                            break;
                        }
                }
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            Int32 result = disconnect();

            if (result < 0)
            {
                textBox8.Text = result.ToString();
                if (result == -1 || result == -2 || result == -5 || result == -33)
                {
                    panel2.Enabled = false;
                    panel3.Enabled = false;
                    panel1.Enabled = true;
                }
            }
            else
            {
                textBox8.Text = @"OK";
                panel2.Enabled = false;
                panel3.Enabled=false;
                panel1.Enabled = true;
            }
        }

        private void progressBar1_Click(object sender, EventArgs e)
        {

        }

        private void listBox1_DoubleClick(object sender, EventArgs e)
        {
            listBox1.Items.Clear();
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked == true)
            {
                textBox5.Enabled = false;
            }
            else
            {
                textBox5.Enabled = true;
            }
        }
    }
}