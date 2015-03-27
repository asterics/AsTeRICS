
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

        const String TOKEN_SEPERATOR = "-,-";

        String[] commands = {  "No Action", "Switch to next configuration", 
                               "Click Left Mouse Button", "Click Right Mouse Button", "Click Middle Mouse Button" , "Double Click Left Mouse Button",
                               "Hold Left Mouse Button", "Hold Right Mouse Button", "Hold Middle Mouse Button", 
                               "Wheel Up", "Wheel down", 
                               "Move Mouse X", "Move Mouse Y",
                               "Write Text", "Press Keys"
                             };
        String[] keyOptions = {    "Clear Keycodes!", "KEY_A","KEY_B","KEY_C","KEY_D","KEY_E","KEY_F","KEY_G","KEY_H","KEY_I","KEY_J","KEY_K","KEY_L",
                                   "KEY_M","KEY_N","KEY_O","KEY_P","KEY_Q","KEY_R","KEY_S","KEY_T","KEY_U","KEY_V","KEY_W","KEY_X",
                                   "KEY_Y","KEY_Z","KEY_1","KEY_2","KEY_3","KEY_4","KEY_5","KEY_6","KEY_7","KEY_8","KEY_9","KEY_0",
                                   "KEY_F1","KEY_F2","KEY_F3","KEY_F4","KEY_F5","KEY_F6","KEY_F7","KEY_F8","KEY_F9","KEY_F10","KEY_F11","KEY_F12",	
                                   "KEY_UP","KEY_DOWN","KEY_LEFT","KEY_RIGHT","KEY_SPACE","KEY_ENTER",
                                   "KEY_ALT","KEY_BACKSPACE","KEY_CAPS_LOCK","KEY_CTRL","KEY_DELETE","KEY_END","KEY_ESC","KEY_GUI",
                                   "KEY_HOME","KEY_INSERT","KEY_NUM_LOCK","KEY_PAGE_DOWN","KEY_PAGE_UP","KEY_PAUSE","KEY_RIGHT_ALT",
                                   "KEY_RIGHT_GUI","KEY_SCROLL_LOCK","KEY_SHIFT","KEY_TAB"
                              };


        String receivedString = "";
        Boolean readDone=false;

        public delegate void SlotValuesDelegate(string newValues);
        public SlotValuesDelegate slotValuesDelegate;
        public delegate void LoadValuesDelegate(string newValues);
        public LoadValuesDelegate loadValuesDelegate;


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

            addToLog("Fabi GUI ready!");
            this.Load += LipmouseGUI_Load;
        }

        private void updateComPorts()
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;
        }

        private void portComboBox_Click(object sender, EventArgs e)
        {
            updateComPorts();
        }

        private void LipmouseGUI_Load(object sender, EventArgs e)
        {
            var ports = SerialPort.GetPortNames();
            portComboBox.DataSource = ports;

            this.slotValuesDelegate = new SlotValuesDelegate(gotSlotValues);
            this.loadValuesDelegate = new LoadValuesDelegate(gotLoadValues);
        }

        public void gotSlotValues(String newValues)
        {
            slotNames.Items.Add(newValues);
        }


        public void gotLoadValues(String newValues)
        {
            String actToken;
            int i=0;
            bool done = false;
            while (!done)
            {
                actToken = newValues.Substring(0, newValues.IndexOf("-,-"));
                // Console.WriteLine("Found Token " + i + " " + actToken);
                switch (i)
                {
                    case 0: slotNames.Text = actToken;  break;  // slotname
                    case 1: break;  // mouse wheel stepsize, currently not used
                    case 2: break;  // time threshold for longpress, currently not used
                    case 3: Button1FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 4: Button1NumericParameter.Value = Int32.Parse(actToken); break;
                    case 5: Button1ParameterText.Text = actToken; break;
                    case 6: Button2FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 7: Button2NumericParameter.Value = Int32.Parse(actToken); break;
                    case 8: Button2ParameterText.Text = actToken; break;
                    case 9: Button3FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 10: Button3NumericParameter.Value = Int32.Parse(actToken); break;
                    case 11: Button3ParameterText.Text = actToken; break;
                    case 12: Button4FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 13: Button4NumericParameter.Value = Int32.Parse(actToken); break;
                    case 14: Button4ParameterText.Text = actToken; break;
                    case 15: Button5FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 16: Button5NumericParameter.Value = Int32.Parse(actToken); break;
                    case 17: Button5ParameterText.Text = actToken; break;
                    case 18: Button6FunctionBox.SelectedIndex = Int32.Parse(actToken); break;
                    case 19: Button6NumericParameter.Value = Int32.Parse(actToken); break;
                    case 20: Button6ParameterText.Text = actToken; break;
                    default: done = true; break;
                }
                newValues = newValues.Substring(actToken.Length + 3);
                if (newValues.ToUpper().StartsWith("END"))
                {
                    done = true;
                }
                else i++;
            }      
            
        }

        bool Connect(string portName)
        {
            if (!serialPort1.IsOpen)
            {
                serialPort1.PortName = portName;
                serialPort1.BaudRate = 115200;
                serialPort1.DataBits = 8;
                serialPort1.Parity = Parity.None;
                serialPort1.Handshake = Handshake.None;
                serialPort1.DtrEnable = true;
                serialPort1.ReadTimeout =2500;
                serialPort1.WriteTimeout =2500;
                serialPort1.NewLine = "\n";
                try {
                    serialPort1.Open();
                    return (true);
                }
                catch (Exception ex)  {
                    addToLog("Could not open COM port");
                }
            }
            return (false);
        }

        public void WorkThreadFunction()
        {
            Console.WriteLine("Started ReaderThread");
            try
            {
                while (serialPort1.IsOpen && !readDone)
                {
                    try  {
                        receivedString = serialPort1.ReadLine();
                        Console.WriteLine("received:"+receivedString);
                        if (receivedString.ToUpper().StartsWith("SLOT"))  // slot name found ?
                        {
                            BeginInvoke(this.slotValuesDelegate, new Object[] { receivedString.Substring(6) });
                        }
                        if (receivedString.ToUpper().StartsWith("LOADING:"))  // slot name found ?
                        {
                            BeginInvoke(this.loadValuesDelegate, new Object[] { receivedString.Substring(8) });
                        }
                    }
                    catch (Exception ex)  {
                        //  Console.WriteLine("timed out ...");
                    }
                }
                Console.WriteLine("Ended ReaderThread");
            }
            catch (Exception ex)
            {
                // log errors
            }
        }


        private void select_Click(object sender, EventArgs e) //select button
        {
            addToLog("Connecting to COM port");
            if (portComboBox.SelectedIndex > -1)
            {
                if (serialPort1.IsOpen)
                {
                    addToLog(String.Format("Port '{0}' is already connected.", portComboBox.SelectedItem));
                }
                else
                {
                    if (Connect(portComboBox.SelectedItem.ToString()))
                    {
                        addToLog(String.Format("Port '{0}' is now connected", portComboBox.SelectedItem));
                        portStatus.Text = "Connected";
                        portStatus.ForeColor = Color.Green;
                        saveSettings.Enabled = true;
                        SelectButton.Enabled = false;
                        dcButton.Enabled = true;
                        loadButton.Enabled = true;
                        ClearButton.Enabled = true;
                        ApplyButton.Enabled = true;

                        readDone = false;
                        Thread thread = new Thread(new ThreadStart(WorkThreadFunction));
                        thread.Start();

                        slotNames.Items.Clear();
                        sendCmd("AT LIST");
                    }
                }
            }
            else addToLog("No port has been selected");
        }

        private void dcButton_Click(object sender, EventArgs e) //disconnect button
        {
            addToLog("Disconnecting from COM Port");
            if (serialPort1.IsOpen)
            {
                readDone = true;

                portStatus.Text = "Disconnected";
                addToLog("Port "+portComboBox.Text+" is now disconnected");

                portStatus.ForeColor = Color.SlateGray;
                saveSettings.Enabled = false;
                SelectButton.Enabled = true;
                dcButton.Enabled = false;
                ClearButton.Enabled = false;
                ApplyButton.Enabled = false;
                loadButton.Enabled = false;

                try
                {
                    receivedString = "";
                    slotNames.Items.Clear();
                    serialPort1.Close();
                }
                catch (Exception ex)
                {
                    addToLog("Error disconnecting COM Port");
                }

            }
        }

        private void sendCmd(string command)
        {
            if (serialPort1.IsOpen)
            {
                Console.Write("Send:" + command);
                try {
                    serialPort1.Write(command + "\r");
                }
                catch (Exception ex)  {
                    addToLog("Could not write to COM port");
                }
            }
        }


        private void saveSettings_Click(object sender, EventArgs e) //button to save options to EEPROM
        {
            slotNames.Text = slotNames.Text.Replace(" ", "");
            slotNames.Text = slotNames.Text.Replace("\n", "");
            slotNames.Text = slotNames.Text.Replace("\r", "");
            addToLog("Saving Slot: "+slotNames.Text);
            if (serialPort1.IsOpen)
            {
                ApplyButton_Click(this, null);
                sendCmd("AT SAVE " + slotNames.Text);
                addToLog("The settings were saved");
                slotNames.Items.Clear();
                sendCmd("AT LIST");
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void ClearButton_Click(object sender, EventArgs e)
        {
            addToLog("Clearing EEPROM settings...");
            if (serialPort1.IsOpen)
            {
                sendCmd("AT CLEAR\n");
                addToLog("The EEPROM settings have been cleared.");
                slotNames.Items.Clear();
            }
            else addToLog("Could not send to device - please connect COM port !");
        }

        private void load_Click(object sender, EventArgs e)
        {
            addToLog("Loading Slot: "+slotNames.Text);
            if (serialPort1.IsOpen)
            {
                sendCmd("AT LOAD "+slotNames.Text);
            }
            else addToLog("Could not send to device - please connect COM port !");
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
            addToLog("Applying Settings...");
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
                case CMD_WRITE_TEXT: la.Visible = true; la.Text = "    Text:"; nud.Visible = false; tb.Enabled = true; tb.ReadOnly = false; tb.Visible = true; tb.Text = ""; cb.Visible = false; break;
                case CMD_PRESS_KEYS: la.Visible = true; la.Text = "KeyCodes:"; nud.Visible = false; tb.Visible = true; tb.Text = ""; tb.ReadOnly = true; cb.Visible = true; break; // tb.Enabled = false; 
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


    }
}
