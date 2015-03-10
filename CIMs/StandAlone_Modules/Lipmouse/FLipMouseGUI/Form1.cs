
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
    public partial class FLipMouseGUI : Form
    {
        const int CMD_NOACTION     = 0;
        const int CMD_NEXT         = 1;
        const int CMD_CLICK_LEFT   = 2;
        const int CMD_CLICK_RIGHT  = 3;
        const int CMD_CLICK_MIDDLE = 4;
        const int CMD_CLICK_DOUBLE = 5;
        const int CMD_PRESS_LEFT   = 6;
        const int CMD_PRESS_RIGHT  = 7;
        const int CMD_PRESS_MIDDLE = 8;
        const int CMD_WHEEL_UP     = 9;
        const int CMD_WHEEL_DOWN   = 10;
        const int CMD_CALIBRATE    = 11;
        const int CMD_MOVE_X       = 12;
        const int CMD_MOVE_Y       = 13;
        const int CMD_WRITE_TEXT   = 14;
        const int CMD_PRESS_KEYS   = 15;

        String[] commands = {  "No Action", "Switch to next configuration", 
                               "Click Left Mouse Button", "Click Right Mouse Button", "Click Middle Mouse Button" , "Double Click Left Mouse Button",
                               "Press Left Mouse Button", "Press Right Mouse Button", "Press Middle Mouse Button", 
                               "Wheel Up", "Wheel down", "Calibrate Middle",
                               "Move Mouse X", "Move Mouse Y",
                               "Write Text", "Press Keys"
                             };
        String[] keyOptions = {    "clear Keycodes!", "KEY_A","KEY_B","KEY_C","KEY_D","KEY_E","KEY_F","KEY_G","KEY_H","KEY_I","KEY_J","KEY_K","KEY_L",
                                   "KEY_M","KEY_N","KEY_O","KEY_P","KEY_Q","KEY_R","KEY_S","KEY_T","KEY_U","KEY_V","KEY_W","KEY_X",
                                   "KEY_Y","KEY_Z","KEY_1","KEY_2","KEY_3","KEY_4","KEY_5","KEY_6","KEY_7","KEY_8","KEY_9","KEY_0",
                                   "KEY_F1","KEY_F2","KEY_F3","KEY_F4","KEY_F5","KEY_F6","KEY_F7","KEY_F8","KEY_F9","KEY_F10","KEY_F11","KEY_F12",	
                                   "KEY_RIGHT","KEY_LEFT","KEY_DOWN","KEY_UP","KEY_ENTER","KEY_ESC","KEY_BACKSPACE","KEY_TAB",
                                   "KEY_HOME","KEY_PAGE_UP","KEY_PAGE_DOWN","KEY_DELETE","KEY_INSERT","KEY_END","KEY_NUM_LOCK",
                                   "KEY_SCROLL_LOCK","KEY_SPACE","KEY_CAPS_LOCK","KEY_PAUSE","KEY_SHIFT","KEY_CTRL","KEY_ALT","KEY_GUI" 
                              };

        const int PARAMETERLESS_FUNCTIONS = 12;
        
        int mouseOff = 0;
        String receivedString = "";
        Boolean readDone = false;

        public delegate void RawValuesDelegate(string newValues);
        public RawValuesDelegate rawValuesDelegate;


        public FLipMouseGUI()
        {
            InitializeComponent();
            foreach (string str in commands)
            {
                Button1FunctionBox.Items.Add(str);
                Button2FunctionBox.Items.Add(str);
                Button3FunctionBox.Items.Add(str);
                UpFunctionMenu.Items.Add(str);
                DownFunctionMenu.Items.Add(str);
                LeftFunctionMenu.Items.Add(str);
                RightFunctionMenu.Items.Add(str);
                SipFunctionMenu.Items.Add(str);
                LongSipFunctionMenu.Items.Add(str);
                PuffFunctionMenu.Items.Add(str);
                LongPuffFunctionMenu.Items.Add(str);
            }

            Button1FunctionBox.SelectedIndex = CMD_NEXT;
            Button2FunctionBox.SelectedIndex = CMD_WHEEL_UP;
            Button3FunctionBox.SelectedIndex = CMD_WHEEL_DOWN;
            UpFunctionMenu.SelectedIndex = CMD_PRESS_KEYS; UpParameterText.Text = "KEY_UP ";
            DownFunctionMenu.SelectedIndex = CMD_PRESS_KEYS; DownParameterText.Text = "KEY_DOWN ";
            LeftFunctionMenu.SelectedIndex = CMD_PRESS_KEYS; LeftParameterText.Text = "KEY_LEFT ";
            RightFunctionMenu.SelectedIndex = CMD_PRESS_KEYS; RightParameterText.Text = "KEY_RIGHT ";
            SipFunctionMenu.SelectedIndex = CMD_PRESS_LEFT;
            LongSipFunctionMenu.SelectedIndex = CMD_NOACTION;
            PuffFunctionMenu.SelectedIndex = CMD_CLICK_RIGHT;
            LongPuffFunctionMenu.SelectedIndex = CMD_CALIBRATE;

            foreach (string str in keyOptions)
            {
                Button1ComboBox.Items.Add(str);
                Button2ComboBox.Items.Add(str);
                Button3ComboBox.Items.Add(str);
                UpComboBox.Items.Add(str);
                DownComboBox.Items.Add(str);
                LeftComboBox.Items.Add(str);
                RightComboBox.Items.Add(str);
                SipComboBox.Items.Add(str);
                LongSipComboBox.Items.Add(str);
                PuffComboBox.Items.Add(str);
                LongPuffComboBox.Items.Add(str);
            }

            addToLog("FLipMouse GUI ready!");
            this.Load += LipmouseGUI_Load;
        }

        private void LipmouseGUI_Load(object sender, EventArgs e)
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;
            this.rawValuesDelegate = new RawValuesDelegate(gotValues);
            BeginInvoke(this.rawValuesDelegate, new Object[] { "512,512,512,512,512" });
        }


        // update paint areas if tabs are changed
        private void tabControl_SelectedIndexChanged(object sender, EventArgs e)
        {
            BeginInvoke(this.rawValuesDelegate, new Object[] { "512,512,512,512,512" });
        }

        // update activity log
        private void addToLog(String text)
        {
            activityLogTextbox.SelectedText = DateTime.Now.ToString() + ": ";
            activityLogTextbox.AppendText(text); activityLogTextbox.AppendText("\n");
        }


        // serial port / communication handling
        private void Connect(string portName)
        {
            if (!serialPort1.IsOpen)
            {
                serialPort1.PortName = portName;
                serialPort1.BaudRate = 9600;
                serialPort1.DataBits = 8;
                serialPort1.Parity = Parity.None;
                serialPort1.Handshake = Handshake.None;

                serialPort1.ReadTimeout =4000;
                serialPort1.WriteTimeout =4000;
                serialPort1.NewLine = "\n";
                serialPort1.Open();
            }
        }

        private void sendCmd(string command)
        {
            if (serialPort1.IsOpen)
            {
                addToLog("Send:" + command);
                //Console.WriteLine("Send:" + command);
                serialPort1.Write(command + "\r");
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
                        saveSettings.Enabled = true;
                        mouseOffButton.Enabled = true;
                        calButton.Enabled = true;
                        dcButton.Enabled = true;
                        ClearButton.Enabled = true;
                        ApplyButton.Enabled = true;

                        readDone = false;
                        Thread thread = new Thread(new ThreadStart(WorkThreadFunction));
                        thread.Start();


                        sendCmd("AT LMSR");   // start reporting raw values !
                    }
                    else addToLog("Could not connect COM Port");
                }
            }
            else addToLog("No port has been selected");
        }

        public void WorkThreadFunction()
        {
            try
            {
                while (serialPort1.IsOpen && !readDone)
                {
                    receivedString = serialPort1.ReadLine();
                    // Console.Write("received:" + receivedString);
                    if (receivedString.ToUpper().StartsWith("AT PR "))
                    {
                        BeginInvoke(this.rawValuesDelegate, new Object[] { receivedString.Substring(6) });
                    }
                }
            }
            catch (Exception ex)
            { }
        }
        
        private void dcButton_Click(object sender, EventArgs e) //disconnect button
        {
            addToLog("Disconnect from COM Port ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT LMER");  // end reporting raw values !
                readDone = true;

                portStatus.Text = "Disconnected";
                addToLog("Port " + portComboBox.Text + " is now disconnected");

                portStatus.ForeColor = Color.SlateGray;
                saveSettings.Enabled = false;
                mouseOffButton.Enabled = false;
                calButton.Enabled = false;
                dcButton.Enabled = false;
                ClearButton.Enabled = false;
                ApplyButton.Enabled = false;

                serialPort1.Close();
                receivedString = "";
            }
        }

        // update assigned actions
        private void updateOneButton(int button, int cmdIndex, String parameter)
        {
            switch (cmdIndex)
            {
                case 0: sendCmd("AT BM " + button); sendCmd("AT IDLE"); break;
                case 1: sendCmd("AT BM " + button); sendCmd("AT NEXT"); break;
                case 2: sendCmd("AT BM " + button); sendCmd("AT CL"); break;
                case 3: sendCmd("AT BM " + button); sendCmd("AT CR"); break;
                case 4: sendCmd("AT BM " + button); sendCmd("AT CM"); break;
                case 5: sendCmd("AT BM " + button); sendCmd("AT CD"); break;
                case 6: sendCmd("AT BM " + button); sendCmd("AT PL"); break;
                case 7: sendCmd("AT BM " + button); sendCmd("AT PR"); break;
                case 8: sendCmd("AT BM " + button); sendCmd("AT PM"); break;
                case 9: sendCmd("AT BM " + button); sendCmd("AT WU"); break;
                case 10: sendCmd("AT BM " + button); sendCmd("AT WD"); break;
                case 11: sendCmd("AT BM " + button); sendCmd("AT LMCA"); break;
                case 12: sendCmd("AT BM " + button); sendCmd("AT MX " + parameter); break;
                case 13: sendCmd("AT BM " + button); sendCmd("AT MY " + parameter); break;
                case 14: sendCmd("AT BM " + button); sendCmd("AT KW " + parameter); break;
                case 15: sendCmd("AT BM " + button); sendCmd("AT KP " + parameter); break;
            }
        }

        // handle settings- and slot-management buttons

        private void calibration_Click(object sender, EventArgs e) //calibration button
        {
            addToLog("Start Calibration ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT LMCA");
                addToLog("Your device has been calibrated. \n");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void onOff_Click(object sender, EventArgs e) // on/off button
        {
            addToLog("Mouse Function on/off ...");
            if (serialPort1.IsOpen)
            {
                if (mouseOff == 0) //if the mouse function is on, turn it off
                {
                    mouseOffButton.Text = "Use Mouse Functions";
                    mouseOff = 1;
                    sendCmd("AT LMOFF");
                }
                else if (mouseOff == 1) //if the mouse function is off, turn it on
                {
                    mouseOffButton.Text = "Use Alternative Functions";
                    mouseOff = 0;
                    sendCmd("AT LMON");
                }
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void ApplyButton_Click(object sender, EventArgs e)
        {
            addToLog("Apply Settings ...");
            if (serialPort1.IsOpen)
            {
                // sendLipmouseCmd("AT LMER");

                sendCmd("AT LMAX " + speedLabel.Text);
                sendCmd("AT LMAY " + speedLabel.Text);
                sendCmd("AT LMDX " + deadzoneLabel.Text);
                sendCmd("AT LMDY " + deadzoneLabel.Text);
                sendCmd("AT LMTS " + sipThresholdLabel.Text);
                sendCmd("AT LMTP " + puffThresholdLabel.Text);
                sendCmd("AT LMTT " + timeThresholdLabel.Text);

                updateOneButton(1, Button1FunctionBox.SelectedIndex, Button1ParameterText.Text);
                updateOneButton(2, Button2FunctionBox.SelectedIndex, Button2ParameterText.Text);
                updateOneButton(3, Button3FunctionBox.SelectedIndex, Button3ParameterText.Text);
                updateOneButton(4, UpFunctionMenu.SelectedIndex, UpParameterText.Text);
                updateOneButton(5, DownFunctionMenu.SelectedIndex, DownParameterText.Text);
                updateOneButton(6, LeftFunctionMenu.SelectedIndex, LeftParameterText.Text);
                updateOneButton(7, RightFunctionMenu.SelectedIndex, RightParameterText.Text);
                updateOneButton(8, SipFunctionMenu.SelectedIndex, SipParameterText.Text);
                updateOneButton(9, LongSipFunctionMenu.SelectedIndex, LongSipParameterText.Text);
                updateOneButton(10, PuffFunctionMenu.SelectedIndex, PuffParameterText.Text);
                updateOneButton(11, LongPuffFunctionMenu.SelectedIndex, LongPuffParameterText.Text);

                addToLog("The selected settings have been applied.");
                // sendLipmouseCmd("AT LMSR");
            }
            else addToLog("Please connect a device before applying configuration changes.");
        }

        private void saveSettings_Click(object sender, EventArgs e) //button to save options to EEPROM
        {
            addToLog("Save Settings ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("at save" + slotName.Text);
                addToLog("The settings were saved");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void ClearButton_Click(object sender, EventArgs e)
        {
            addToLog("Clear EEPROM settings ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("at clear\n");
                addToLog("The EEPROM settings have been cleared.");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        // update scroll bars :

        private void speedBar_Scroll(object sender, EventArgs e)
        {
            speedLabel.Text = speedBar.Value.ToString();
        }

        private void deadzone_Scroll(object sender, EventArgs e)
        {
            deadzoneLabel.Text = deadzoneBar.Value.ToString();
        }
        private void sipThresholdBar_Scroll(object sender, EventArgs e)
        {
            sipThresholdLabel.Text = sipThresholdBar.Value.ToString();
        }

        private void puffThresholdBar_Scroll(object sender, EventArgs e)
        {
            puffThresholdLabel.Text = puffThresholdBar.Value.ToString();
        }

        private void timeThresholdBar_Scroll(object sender, EventArgs e)
        {
            timeThresholdLabel.Text = timeThresholdBar.Value.ToString();
        }


        // update visibility of parameter fields:

        private void updateVisibility(int selectedFunction, TextBox tb, NumericUpDown nud, ComboBox cb, Label la)
        {
            switch (selectedFunction)
            {
                case 12:
                case 13: la.Visible = true; la.Text = "   Speed:"; nud.Visible = true; tb.Visible = false; cb.Visible = false; break;
                case 14: la.Visible = true; la.Text = "    Text:"; nud.Visible = false; tb.Enabled = true; tb.Visible = true; tb.Text = "";  cb.Visible = false; break;
                case 15: la.Visible = true; la.Text = "KeyCodes:"; nud.Visible = false; tb.Enabled = false; tb.Visible = true; tb.Text = "";  cb.Visible = true; break;
                default: la.Visible = false;  nud.Visible = false; tb.Visible = false; cb.Visible = false; break;
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

        private void UpFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(UpFunctionMenu.SelectedIndex, UpParameterText, UpNumericParameter, UpComboBox, UpLabel);
        }

        private void DownFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(DownFunctionMenu.SelectedIndex, DownParameterText, DownNumericParameter, DownComboBox, DownLabel);
        }

        private void LeftFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(LeftFunctionMenu.SelectedIndex, LeftParameterText, LeftNumericParameter, LeftComboBox, LeftLabel);
        }

        private void RightFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(RightFunctionMenu.SelectedIndex, RightParameterText, RightNumericParameter, RightComboBox, RightLabel);
        }

        private void SipFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(SipFunctionMenu.SelectedIndex, SipParameterText, SipNumericParameter, SipComboBox, SipParameterLabel);
        }

        private void LongSipFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(LongSipFunctionMenu.SelectedIndex, LongSipParameterText, LongSipNumericParameter, LongSipComboBox, LongSipParameterLabel);
        }

        private void PuffFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(PuffFunctionMenu.SelectedIndex, PuffParameterText, PuffNumericParameter, PuffComboBox, PuffParameterLabel);
        }

        private void LongPuffFunctionMenu_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateVisibility(LongPuffFunctionMenu.SelectedIndex, LongPuffParameterText, LongPuffNumericParameter, LongPuffComboBox, LongPuffParameterLabel);
        }

        // update the keycode parameters:

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

        private void UpComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(UpComboBox, UpParameterText);
        }

        private void DownComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(DownComboBox, DownParameterText);
        }

        private void LeftComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(LeftComboBox, LeftParameterText);
        }

        private void RightComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(RightComboBox, RightParameterText);
        }

        private void SipComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(SipComboBox, SipParameterText);
        }

        private void LongSipComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(LongSipComboBox, LongSipParameterText);
        }

        private void PuffComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(PuffComboBox, PuffParameterText);
        }

        private void LongPuffComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateKeyCodeParameter(LongPuffComboBox, LongPuffParameterText);
        }

        // update numeric paramters:
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

        private void UpNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            UpParameterText.Text = UpNumericParameter.Value.ToString();
        }

        private void DownNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            DownParameterText.Text = DownNumericParameter.Value.ToString();
        }

        private void LeftNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            LeftParameterText.Text = LeftNumericParameter.Value.ToString();
        }

        private void RightNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            RightParameterText.Text = RightNumericParameter.Value.ToString();
        }

        private void SipNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            SipParameterText.Text = SipNumericParameter.Value.ToString();
        }

        private void LongSipNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            LongSipParameterText.Text = LongSipNumericParameter.Value.ToString();
        }

        private void PuffNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            PuffParameterText.Text = PuffNumericParameter.Value.ToString();
        }

        private void LongPuffNumericParameter_ValueChanged(object sender, EventArgs e)
        {
            LongPuffParameterText.Text = LongPuffNumericParameter.Value.ToString();
        }

        public void gotValues(String newValues) //n
        {
            if (newValues.Length == 0)
                return;

            //            Console.WriteLine(newValues);

            String[] values = newValues.Split(',');
            if (values.Length == 5)
            {
                pressureLabel.Text = values[0];
                Int32 value = Convert.ToInt32(values[0]);
                Graphics g = panel1.CreateGraphics();
                Brush brush = new SolidBrush(Color.Green);
                Brush brush2 = new SolidBrush(Color.White);
                value = value * panel1.Height / 1024;
                g.FillRectangle(brush, 0, panel1.Height - value, 30, value);
                g.FillRectangle(brush2, 0, 0, 30, panel1.Height - value);

                //Label.Text = values[1];
                value = Convert.ToInt32(values[1]);
                g = panel2.CreateGraphics();
                brush = new SolidBrush(Color.Red);
                brush2 = new SolidBrush(Color.White);
                value = value * panel2.Height / 1024;
                g.FillRectangle(brush, 0, panel2.Height - value, 30, value);
                g.FillRectangle(brush2, 0, 0, 30, panel2.Height - value);

                //Label.Text = values[2];
                value = Convert.ToInt32(values[2]);
                g = panel3.CreateGraphics();
                brush = new SolidBrush(Color.Red);
                brush2 = new SolidBrush(Color.White);
                value = value * panel3.Height / 1024;
                g.FillRectangle(brush, 0, panel3.Height - value, 30, value);
                g.FillRectangle(brush2, 0, 0, 30, panel3.Height - value);

                //Label.Text = values[3];
                value = Convert.ToInt32(values[3]);
                g = panel4.CreateGraphics();
                brush = new SolidBrush(Color.Red);
                brush2 = new SolidBrush(Color.White);
                value = value * panel4.Height / 1024;
                g.FillRectangle(brush, 0, panel4.Height - value, 30, value);
                g.FillRectangle(brush2, 0, 0, 30, panel4.Height - value);

                //Label.Text = values[4];
                value = Convert.ToInt32(values[4]);
                g = panel5.CreateGraphics();
                brush = new SolidBrush(Color.Red);
                brush2 = new SolidBrush(Color.White);
                value = value * panel5.Height / 1024;
                g.FillRectangle(brush, 0, panel5.Height - value, 30, value);
                g.FillRectangle(brush2, 0, 0, 30, panel5.Height - value);
            }
        }
    }
}
