
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

        String[] commands = {  "Switch to next configuration",
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
                                   "KEY_SCROLL_LOCK","KEY_SPACE","KEY_CAPS_LOCK","KEY_PAUSE","KEY_SHIFT","KEY_CTRL","KEY_ALT","KEY_GUI" 
                              };

        const int PARAMETERLESS_FUNCTIONS = 10;

        String receivedString = "";
        Boolean readDone=false;

        public delegate void ChangePressureDelegate(string newPressureValue);
        public ChangePressureDelegate pressureDelegate;


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

            Button1FunctionBox.SelectedIndex = 5;
            Button2FunctionBox.SelectedIndex = 2;
            Button3FunctionBox.SelectedIndex = 4;
            Button4FunctionBox.SelectedIndex = 8;
            Button5FunctionBox.SelectedIndex = 9;
            Button6FunctionBox.SelectedIndex = 3;

            foreach (string str in keyOptions)
            {
                Button1ComboBox.Items.Add(str);
                Button2ComboBox.Items.Add(str);
                Button3ComboBox.Items.Add(str);
                Button4ComboBox.Items.Add(str);
                Button5ComboBox.Items.Add(str);
                Button6ComboBox.Items.Add(str);
            }

            addToLog("Fabi GUI ready!");
            this.Load += LipmouseGUI_Load;
        }

        private void LipmouseGUI_Load(object sender, EventArgs e)
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;

            this.pressureDelegate = new ChangePressureDelegate(ChangePressure);

        }

        public void ChangePressure(String newPressure) //n
        {
            if (newPressure.Length == 0)
                return;

            Console.WriteLine("newPressure");

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
                serialPort1.Open();
            }
        }

        public void WorkThreadFunction()
        {
            try
            {
                while (serialPort1.IsOpen && !readDone)
                {
                    receivedString = serialPort1.ReadLine();
                    BeginInvoke(this.pressureDelegate, new Object[] { receivedString });
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
            addToLog("Save Settings ...");
            if (serialPort1.IsOpen)
            {
                sendCmd("at save" + slotName.Text);
                addToLog("The settings were saved");
            }
            else addToLog("Could not send to device - please connect COM port !");        }

        private void updateOneButton(int button, int cmdIndex, String parameter) 
        {
            switch (cmdIndex)
            {
                case 0: sendCmd("at bm " + button); sendCmd("at next"); break;
                case 1: sendCmd("at bm " + button); sendCmd("at cl"); break;
                case 2: sendCmd("at bm " + button); sendCmd("at cr"); break;
                case 3: sendCmd("at bm " + button); sendCmd("at cm"); break;
                case 4: sendCmd("at bm " + button); sendCmd("at cd"); break;
                case 5: sendCmd("at bm " + button); sendCmd("at pl"); break;
                case 6: sendCmd("at bm " + button); sendCmd("at pr"); break;
                case 7: sendCmd("at bm " + button); sendCmd("at pm"); break;
                case 8: sendCmd("at bm " + button); sendCmd("at wu"); break;
                case 9: sendCmd("at bm " + button); sendCmd("at wd"); break;
                case 10: sendCmd("at bm " + button); sendCmd("at mx " + parameter); break;
                case 11: sendCmd("at bm " + button); sendCmd("at my " + parameter); break;
                case 12: sendCmd("at bm " + button); sendCmd("at kw " + parameter); break;
                case 13: sendCmd("at bm " + button); sendCmd("at kp " + parameter); break;
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
                updateOneButton(1, Button1FunctionBox.SelectedIndex, Button1ParameterText.Text);
                updateOneButton(2, Button2FunctionBox.SelectedIndex, Button2ParameterText.Text);
                updateOneButton(3, Button3FunctionBox.SelectedIndex, Button3ParameterText.Text);
                updateOneButton(4, Button4FunctionBox.SelectedIndex, Button4ParameterText.Text);
                updateOneButton(5, Button5FunctionBox.SelectedIndex, Button5ParameterText.Text);
                updateOneButton(6, Button6FunctionBox.SelectedIndex, Button6ParameterText.Text);
                addToLog("The selected settings have been applied.");
            }
            else addToLog("Please connect a device before applying configuration changes.");
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

        private void updateVisibility(int selectedFunction, TextBox tb, NumericUpDown nud, ComboBox cb, Label la)
        {
            switch (selectedFunction)
            {
                case 10:
                case 11: la.Visible = true; la.Text = "   Speed:"; nud.Visible = true; tb.Visible = false; cb.Visible = false; break;
                case 12: la.Visible = true; la.Text = "    Text:"; nud.Visible = false; tb.Enabled = true; tb.Visible = true; cb.Visible = false; break;
                case 13: la.Visible = true; la.Text = "KeyCodes:"; nud.Visible = false; tb.Enabled = false; tb.Visible = true; cb.Visible = true; break;
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
