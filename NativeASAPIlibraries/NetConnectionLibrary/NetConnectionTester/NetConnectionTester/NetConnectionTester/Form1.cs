using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace NetConnectionTester
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void NewEvent(int port, IntPtr param);

        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void NewIntegerValue(int port, int value, IntPtr param);

        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void NewDoubleValue(int port, double value, IntPtr param);

        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate void NewStringValue(int port, [MarshalAs(UnmanagedType.LPWStr)]  string value, IntPtr param);

        public NewEvent NewEventDelegate;
        public NewIntegerValue NewIntegerValueDelegate;
        public NewDoubleValue NewDoubleValueDelegate;
        public NewStringValue NewStringValueDelegate;

        public enum ServerMode
        {
            SM_CLIENT = 1,
            SM_SERVER_SINGLE_SESSION = 2,
            SM_SERVER_MULTISESSION = 3
        }

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 init(ServerMode serverMode, string IP, int port, NewEvent newEvent, NewIntegerValue newIntegerValue, NewDoubleValue newDoubleValue, NewStringValue newStringValue, IntPtr param);

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 close();

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 sendEvent(int port);

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 sendInteger(int port, int value);

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 sendDouble(int port, double value);

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 sendText(int port, string text);

        [DllImport("NetConnectionLibrary.dll", CharSet = CharSet.Unicode)]
        private static extern Int32 numberOfConnections();

        public void newEvent(int port, IntPtr param)
        {
            string str = "Event port: " + port.ToString();
            listBox1.Invoke((MethodInvoker)delegate() { listBox1.Items.Add(str); });
        }

        public void newIntegerValue(int port, int value, IntPtr param)
        {
            string str = "Integer port: " + port.ToString() + " value: " + value.ToString();
            listBox1.Invoke((MethodInvoker)delegate() { listBox1.Items.Add(str); });
        }

        public void newDoubleValue(int port, double value, IntPtr param)
        {
            string str = "Double port: " + port.ToString() + " value: " + value.ToString();
            listBox1.Invoke((MethodInvoker)delegate() { listBox1.Items.Add(str); });
        }

        public void newStringValue(int port, string value, IntPtr param)
        {
            string str = "String port: " + port.ToString() + " text: " + value.ToString();
            listBox1.Invoke((MethodInvoker)delegate() { listBox1.Items.Add(str); });
        }

        private void button6_Click(object sender, EventArgs e)
        {
            ServerMode sm = ServerMode.SM_CLIENT;

            switch (comboBox5.SelectedIndex)
            {
                case 0:
                    sm = ServerMode.SM_CLIENT;
                    break;
                case 1:
                    sm = ServerMode.SM_SERVER_SINGLE_SESSION;
                    break;
                case 2:
                    sm = ServerMode.SM_SERVER_MULTISESSION;
                    break;
            }

            int port = 0;
            bool result = int.TryParse(textBox5.Text, out port);
            int initResult=0;
            if (result)
            {
                //string ip = "";
                if (sm == ServerMode.SM_SERVER_MULTISESSION || sm == ServerMode.SM_SERVER_SINGLE_SESSION)
                {
                    initResult=init(sm, "127.0.0.1", port, NewEventDelegate, NewIntegerValueDelegate, NewDoubleValueDelegate, NewStringValueDelegate, (IntPtr)0);
                }
                else
                {
                    if (textBox1.Text.Length > 0)
                    {
                        initResult=init(ServerMode.SM_CLIENT, textBox1.Text, port, NewEventDelegate, NewIntegerValueDelegate, NewDoubleValueDelegate, NewStringValueDelegate, (IntPtr)0);
                    }
                }
            }

            if (initResult > 0)
            {
                panel4.Enabled = false;
                panel1.Enabled = true;
                panel3.Enabled = true;
                button6.Enabled = false;
                comboBox5.Enabled = false;
                button7.Enabled = true;
                timer1.Enabled = true;
            }

        }

        private void Form1_Load(object sender, EventArgs e)
        {
            comboBox1.SelectedIndex = 0;
            comboBox2.SelectedIndex = 0;
            comboBox3.SelectedIndex = 0;
            comboBox4.SelectedIndex = 0;
            comboBox5.SelectedIndex = 0;
            NewEventDelegate = new NewEvent(newEvent);
            NewIntegerValueDelegate = new NewIntegerValue(newIntegerValue);
            NewDoubleValueDelegate = new NewDoubleValue(newDoubleValue);
            NewStringValueDelegate = new NewStringValue(newStringValue);
        }

        private void button7_Click(object sender, EventArgs e)
        {
            int result = close();
            panel4.Enabled = true;
            panel1.Enabled = false;
            panel3.Enabled = false;
            button6.Enabled = true;
            comboBox5.Enabled = true;
            button7.Enabled = false;
            timer1.Enabled = false;
            textBox6.Text = "";
        }

        private void button1_Click(object sender, EventArgs e)
        {
            int port=0;
            
            bool result=int.TryParse(comboBox1.SelectedItem.ToString(), out port);

            if (result)
            {
                int sendResult=sendEvent(port);
            }

           
            
        }

        private void button2_Click(object sender, EventArgs e)
        {
            int port = 0;

            bool result = int.TryParse(comboBox2.SelectedItem.ToString(), out port);

            if (result)
            {
                int value = 0;
                result = int.TryParse(textBox2.Text, out value);
                if (result)
                {
                    int sendResult = sendInteger(port, value);
                }
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            int port = 0;

            bool result = int.TryParse(comboBox3.SelectedItem.ToString(), out port);

            if (result)
            {
                double value = 0;
                result = Double.TryParse(textBox3.Text, out value);
                if (result)
                {
                    int sendResult = sendDouble(port, value);
                }
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            int port = 0;

            bool result = int.TryParse(comboBox4.SelectedItem.ToString(), out port);

            if (result)
            {
                int sendResult = sendText(port, textBox4.Text);
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            listBox1.Items.Clear();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            textBox6.Text = numberOfConnections().ToString();
        }
    }
}
