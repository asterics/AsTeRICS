
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Threading;
using System.Windows.Forms;
using System.IO.Ports;
using System.IO;



namespace MouseApp2
{
    public partial class FabiGUI : Form
    {

        const int CMD_NOACTION = 0;
        const int CMD_NEXT = 1;
        const int CMD_CLICK_LEFT = 2;
        const int CMD_CLICK_RIGHT = 3;
        const int CMD_CLICK_MIDDLE = 4;
        const int CMD_CLICK_DOUBLE = 5;
        const int CMD_PRESS_LEFT = 6;
        const int CMD_PRESS_RIGHT = 7;
        const int CMD_PRESS_MIDDLE = 8;
        const int CMD_WHEEL_UP = 9;
        const int CMD_WHEEL_DOWN = 10;
        const int CMD_MOVE_X = 11;
        const int CMD_MOVE_Y = 12;
        const int CMD_WRITE_TEXT = 13;
        const int CMD_PRESS_KEYS = 14;

        String[] commands = {  "No Action", "Switch to next configuration", 
                               "Click Left Mouse Button", "Click Right Mouse Button", "Click Middle Mouse Button" , "Double Click Left Mouse Button",
                               "Press Left Mouse Button", "Press Right Mouse Button", "Press Middle Mouse Button", 
                               "Wheel Up", "Wheel down", 
                               "Move Mouse X", "Move Mouse Y",
                               "Write Text", "Press Keys"
                             };
        String[] keyOptions = {    "clear Keycodes!", "KEY_A","KEY_B","KEY_C","KEY_D","KEY_E","KEY_F","KEY_G","KEY_H","KEY_I","KEY_J","KEY_K","KEY_L",
                                   "KEY_M","KEY_N","KEY_O","KEY_P","KEY_Q","KEY_R","KEY_S","KEY_T","KEY_U","KEY_V","KEY_W","KEY_X",
                                   "KEY_Y","KEY_Z","KEY_1","KEY_2","KEY_3","KEY_4","KEY_5","KEY_6","KEY_7","KEY_8","KEY_9","KEY_0",
                                   "KEY_F1","KEY_F2","KEY_F3","KEY_F4","KEY_F5","KEY_F6","KEY_F7","KEY_F8","KEY_F9","KEY_F10","KEY_F11","KEY_F12",	
                                   "KEY_RIGHT","KEY_LEFT","KEY_DOWN","KEY_UP","KEY_ENTER","KEY_ESC","KEY_BACKSPACE","KEY_TAB",
                                   "KEY_HOME","KEY_PAGE_UP","KEY_PAGE_DOWN","KEY_DELETE","KEY_INSERT","KEY_END","KEY_NUM_LOCK",
                                   "KEY_SCROLL_LOCK","KEY_SPACE","KEY_CAPS_LOCK","KEY_PAUSE","KEY_SHIFT","KEY_CTRL","KEY_ALT","KEY_RIGHT_ALT","KEY_GUI", "KEY_RIGHT_GUI" 
                              };


        String receivedString = "";
        Boolean readDone=false;

        public FabiGUI()
        {
            InitializeComponent();
            foreach (string str in commands)
            {
                Button1FunctionBox.Items.Add(str);
                Button2FunctionBox.Items.Add(str);
                Button3FunctionBox.Items.Add(str);
                Button4FunctionBox.Items.Add(str);
                Button5FunctionBox.Items.Add(str);
                Button6FunctionBox.Items.Add(str);
            }

            Button1FunctionBox.SelectedIndex = CMD_PRESS_LEFT;
            Button2FunctionBox.SelectedIndex = CMD_CLICK_RIGHT;
            Button3FunctionBox.SelectedIndex = CMD_CLICK_DOUBLE;
            Button4FunctionBox.SelectedIndex = CMD_WHEEL_UP;
            Button5FunctionBox.SelectedIndex = CMD_WHEEL_DOWN;
            Button6FunctionBox.SelectedIndex = CMD_NEXT;

            foreach (string str in keyOptions)
            {
                Button1ComboBox.Items.Add(str);
                Button2ComboBox.Items.Add(str);
                Button3ComboBox.Items.Add(str);
                Button4ComboBox.Items.Add(str);
                Button5ComboBox.Items.Add(str);
                Button6ComboBox.Items.Add(str);
            }

            updateComPorts();

            System.Windows.Forms.Timer t = new System.Windows.Forms.Timer();
            t.Interval = 4000; // udpate interval for COM ports
            t.Tick += new EventHandler(OnTimedEvent);
            t.Start();

            addToLog("Fabi GUI ready!");
            this.Load += LipmouseGUI_Load;
        }

        private void updateComPorts()
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;
        }

        private void OnTimedEvent(object source, EventArgs e)
        {
            updateComPorts();
        }

        private void LipmouseGUI_Load(object sender, EventArgs e)
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;
        }


        private void Connect(string portName)
        {
            if (!serialPort1.IsOpen)
            {
                serialPort1.PortName = portName;
                serialPort1.BaudRate = 115200;
                serialPort1.DataBits = 8;
                serialPort1.Parity = Parity.None;
                serialPort1.Handshake = Handshake.None;

                serialPort1.ReadTimeout =5000;
                serialPort1.WriteTimeout =5000;
                serialPort1.NewLine = "\n";
                try
                {
                    serialPort1.Open();
                }
                catch (Exception ex)
                {
                    addToLog("Could not open COM port ...");
                }
            }
        }

        public void WorkThreadFunction()
        {
            try
            {
                while (serialPort1.IsOpen && !readDone)
                {
                    receivedString = serialPort1.ReadLine();
                   // BeginInvoke(this.pressureDelegate, new Object[] { receivedString });
                }
            }
            catch (Exception ex)
            {
                // log errors
            }
        }


        private void select_Click(object sender, EventArgs e) //select button
        {
            addToLog("Connect to COM port ...");
            if (portComboBox.SelectedIndex > -1)
            {
                if (serialPort1.IsOpen)
                {
                    addToLog(String.Format("Port '{0}' is already connected.", portComboBox.SelectedItem));
                }
                else
                {
                    activityLogTextbox.SelectedText = DateTime.Now.ToString() + ": ";
                    activityLogTextbox.AppendText(String.Format("You selected port '{0}' \n", portComboBox.SelectedItem));
                    Connect(portComboBox.SelectedItem.ToString());
                    if (serialPort1.IsOpen)
                    {
                        addToLog("COM Port openend");
                        portStatus.Text = "Connected";
                        portStatus.ForeColor = Color.Green;
                        saveSettings.Enabled=true;
                        dcButton.Enabled = true;
                        ClearButton.Enabled = true;
                        ApplyButton.Enabled = true;

                        readDone = false;
                        Thread thread = new Thread(new ThreadStart(WorkThreadFunction));
                        thread.Start();
                    }
                    else addToLog("Could not connect COM Port");
                }
            }
            else addToLog("No port has been selected");
        }

        private void dcButton_Click(object sender, EventArgs e) //disconnect button
        {
            addToLog("Disconnect from COM Port ...");
            if (serialPort1.IsOpen)
            {
                readDone = true;

                portStatus.Text = "Disconnected";
                addToLog("Port "+portComboBox.Text+" is now disconnected");

                portStatus.ForeColor = Color.SlateGray;
                saveSettings.Enabled = false;
                dcButton.Enabled = false;
                ClearButton.Enabled = false;
                ApplyButton.Enabled = false;

                serialPort1.Close();
                receivedString = "";
            }
        }

        private void sendCmd(string command)
        {
            if (serialPort1.IsOpen)
            {
                addToLog("Send:" + command);
                serialPort1.Write(command + "\r");
            }
        }


        private void saveSettings_Click(object sender, EventArgs e) //button to save options to EEPROM
        {
            ApplyButton_Click(this, null);
            addToLog("Save Settings ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT SAVE " + slotName.Text);
                addToLog("The settings were saved");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void ClearButton_Click(object sender, EventArgs e)
        {
            addToLog("Clear EEPROM settings ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT CLEAR\n");
                addToLog("The EEPROM settings have been cleared.");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void list_Click(object sender, EventArgs e)
        {
            sendCmd("AT ER");  // end reporting raw values !
            addToLog("List Slot in EEPROM ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT LIST\n");
            }
            else addToLog("Could not send to device - please connect COM port !");
            sendCmd("AT SR");  // start reporting raw values !

        }


        private void updateOneButton(int button, int cmdIndex, String parameter, String numParameter)
        {
            sendCmd("AT BM " + button);  // store command to this button function !
            switch (cmdIndex)
            {
                case CMD_NOACTION: sendCmd("AT IDLE"); break;
                case CMD_NEXT: sendCmd("AT NEXT"); break;
                case CMD_CLICK_LEFT: sendCmd("AT CL"); break;
                case CMD_CLICK_RIGHT: sendCmd("AT CR"); break;
                case CMD_CLICK_MIDDLE: sendCmd("AT CM"); break;
                case CMD_CLICK_DOUBLE: sendCmd("AT CD"); break;
                case CMD_PRESS_LEFT: sendCmd("AT PL"); break;
                case CMD_PRESS_RIGHT: sendCmd("AT PR"); break;
                case CMD_PRESS_MIDDLE: sendCmd("AT PM"); break;
                case CMD_WHEEL_UP: sendCmd("AT WU"); break;
                case CMD_WHEEL_DOWN: sendCmd("AT WD"); break;
                case CMD_MOVE_X: sendCmd("AT MX " + numParameter); break;
                case CMD_MOVE_Y: sendCmd("AT MY " + numParameter); break;
                case CMD_WRITE_TEXT: sendCmd("AT KW " + parameter); break;
                case CMD_PRESS_KEYS: sendCmd("AT KP " + parameter); break;
            }
        }

        private void addToLog(String text)
        {
             activityLogTextbox.SelectedText = DateTime.Now.ToString() + ": ";
             activityLogTextbox.AppendText(text); activityLogTextbox.AppendText("\n");
        }

        private void ApplyButton_Click(object sender, EventArgs e)
        {
            addToLog("Apply Settings ...");
            if (serialPort1.IsOpen)
            {
                updateOneButton(1, Button1FunctionBox.SelectedIndex, Button1ParameterText.Text, Button1NumericParameter.Value.ToString());
                updateOneButton(2, Button2FunctionBox.SelectedIndex, Button2ParameterText.Text, Button2NumericParameter.Value.ToString());
                updateOneButton(3, Button3FunctionBox.SelectedIndex, Button3ParameterText.Text, Button3NumericParameter.Value.ToString());
                updateOneButton(4, Button4FunctionBox.SelectedIndex, Button4ParameterText.Text, Button4NumericParameter.Value.ToString());
                updateOneButton(5, Button5FunctionBox.SelectedIndex, Button5ParameterText.Text, Button5NumericParameter.Value.ToString());
                updateOneButton(6, Button6FunctionBox.SelectedIndex, Button6ParameterText.Text, Button6NumericParameter.Value.ToString());
                addToLog("The selected settings have been applied.");
            }
            else addToLog("Please connect a device before applying configuration changes.");
        }

        private void updateVisibility(int selectedFunction, TextBox tb, NumericUpDown nud, ComboBox cb, Label la)
        {
            switch (selectedFunction)
            {
                case CMD_MOVE_X:
                case CMD_MOVE_Y: la.Visible = true; la.Text = "   Speed:"; nud.Visible = true; tb.Visible = false; cb.Visible = false; break;
                case CMD_WRITE_TEXT: la.Visible = true; la.Text = "    Text:"; nud.Visible = false; tb.Enabled = true; tb.Visible = true; tb.Text = ""; cb.Visible = false; break;
                case CMD_PRESS_KEYS: la.Visible = true; la.Text = "KeyCodes:"; nud.Visible = false; tb.Visible = true; tb.Text = ""; cb.Visible = true; break; // tb.Enabled = false; 
                default: la.Visible = false; nud.Visible = false; tb.Visible = false; cb.Visible = false; break;
            }
        }

        private void Button1FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button1FunctionBox.SelectedIndex, Button1ParameterText, Button1NumericParameter, Button1ComboBox, Button1Label);
        }

        private void Button2FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button2FunctionBox.SelectedIndex, Button2ParameterText, Button2NumericParameter, Button2ComboBox, Button2Label);
        }

        private void Button3FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button3FunctionBox.SelectedIndex, Button3ParameterText, Button3NumericParameter, Button3ComboBox, Button3Label);
        }

        private void Button4FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button4FunctionBox.SelectedIndex, Button4ParameterText, Button4NumericParameter, Button4ComboBox, Button4Label);
        }

        private void Button5FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button5FunctionBox.SelectedIndex, Button5ParameterText, Button5NumericParameter, Button5ComboBox, Button5Label);
        }

        private void Button6FunctionBox_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            updateVisibility(Button6FunctionBox.SelectedIndex, Button6ParameterText, Button6NumericParameter, Button6ComboBox, Button6Label);
        }



        private void updateKeyCodeParameter(ComboBox cb, TextBox tb)
        {
            if (cb.SelectedIndex == 0)
                tb.Text = "";
            else
            {
                String add = cb.Text.ToString() + " ";
                if (!tb.Text.Contains(add))
                    tb.Text += add;
            }
        }

        // updating the keycode parameters:
        private void Button1ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button1ComboBox,Button1ParameterText);
        }

        private void Button2ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button2ComboBox, Button2ParameterText);
        }

        private void Button3ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button3ComboBox, Button3ParameterText);
        }
        private void Button4ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button4ComboBox, Button4ParameterText);
        }
        private void Button5ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button5ComboBox, Button5ParameterText);
        }
        private void Button6ComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(Button6ComboBox, Button6ParameterText);
        }


        private void Button1NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button1ParameterText.Text = Button1NumericParameter.Value.ToString();
        }

        private void Button2NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button2ParameterText.Text = Button2NumericParameter.Value.ToString();
        }

        private void Button3NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button3ParameterText.Text = Button3NumericParameter.Value.ToString();
        }

        private void Button4NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button4ParameterText.Text = Button4NumericParameter.Value.ToString();
        }

        private void Button5NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button5ParameterText.Text = Button5NumericParameter.Value.ToString();
        }

        private void Button6NumericParameter_ValueChanged(object sender, EventArgs e)
        {
            Button6ParameterText.Text = Button6NumericParameter.Value.ToString();
        }


        private void SipParameterText_TextChanged(object sender, EventArgs e)
        {

        }

        private void PuffParameterText_TextChanged(object sender, EventArgs e)
        {

        }

    }
}
