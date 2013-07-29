using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace KeyboardLibraryTester
{
    public partial class Form1 : Form
    {
        private LibraryManager manager;
        public Form1()
        {
            InitializeComponent();
            manager = new LibraryManager();
            LibraryManager.form = this;
            comboBox1.SelectedIndex = 2;
            comboBox2.SelectedIndex = 1;
        }

        private void button6_Click(object sender, EventArgs e)
        {
            prepareKeysSend(SendMethod.SM_ScanCode);
        }

        private void button9_Click(object sender, EventArgs e)
        {
            listBox1.Items.Clear();
        }

        private void EventPropertiesClear()
        {
            textBox10.Text = "";
            textBox11.Text = "";
            textBox12.Text = "";
            textBox13.Text = "";
            textBox14.Text = "";
            checkBox1.Checked = false;
            checkBox1.Checked = false;
            checkBox2.Checked = false;
            checkBox3.Checked = false;
            checkBox4.Checked = false;
            checkBox5.Checked = false;

        }

        private void FillEventProperties(KeyEvent keyEvent)
        {

            EventPropertiesClear();

            textBox10.Text = keyEvent.scanCode.ToString();
            textBox11.Text = @"0x" + keyEvent.scanCode.ToString("X");
            textBox12.Text = keyEvent.virtualCode.ToString();
            textBox13.Text = @"0x" + keyEvent.virtualCode.ToString("X");

            switch (keyEvent.hookMessage)
            {
                case HookMessage.HM_KEYDOWN:
                    textBox14.Text = "Key Down";
                    break;
                case HookMessage.HM_KEYUP:
                    textBox14.Text = "Key Up";
                    break;
                case HookMessage.HM_SYSKEYDOWN:
                    textBox14.Text = "System Key Down";
                    break;
                case HookMessage.HM_SYSKEYUP:
                    textBox14.Text = "System Key up";
                    break;
                case HookMessage.HM_None:
                    textBox14.Text = "";
                    break;
            }

            if (keyEvent.extendedKey)
            {
                checkBox1.Checked = true;
            }

            if (keyEvent.injectedKey)
            {
                checkBox2.Checked = true;
            }

            if (keyEvent.altKeyPressed)
            {
                checkBox3.Checked = true;
            }

            if (keyEvent.keyPress)
            {
                checkBox4.Checked = true;
            }

            if (keyEvent.sentFromLibrary)
            {
                checkBox5.Checked = true;
            }

        }

        public void AddEvent(KeyEvent keyEvent)
        {
            listBox1.Items.Add(keyEvent);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            int result = LibraryManager.init();
            textBox1.Text = result.ToString();

            if (result > 0)
            {
                button1.Enabled = false;
                button2.Enabled = true;
                groupBox1.Enabled = true;
                button3.Enabled = true;
                button4.Enabled = false;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            int result = LibraryManager.close();
            textBox2.Text = result.ToString();
            if (result > 0)
            {
                button1.Enabled = true;
                button2.Enabled = false;
                groupBox1.Enabled = false;
                button3.Enabled = false;
                button4.Enabled = true;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            int result = LibraryManager.startHook();
            textBox3.Text = result.ToString();
            if (result > 0)
            {
                button3.Enabled = false;
                button4.Enabled = true;
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            int result = LibraryManager.stopHook();
            textBox4.Text = result.ToString();
            if (result > 0)
            {
                button3.Enabled = true; ;
                button4.Enabled = false;
            }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            int index = comboBox1.SelectedIndex;

            if (index < 0 || index > 2)
            {
                textBox5.Text = "E";
                return;
            }

           BlockOptions blockOptions = BlockOptions.BO_PassAll;

            switch (index)
            {
                case 0:
                    blockOptions = BlockOptions.BO_BlockAll;
                    break;
                case 1:
                    blockOptions = BlockOptions.BO_PassSentFromLibrary;
                    break;
                case 2:
                    blockOptions = BlockOptions.BO_PassAll;;
                    break;
            }

            int result=LibraryManager.blockKeys(blockOptions);
            textBox5.Text = result.ToString();
        }

        private void button10_Click(object sender, EventArgs e)
        {
            switch (comboBox2.SelectedIndex)
            {
                case 0:
                    LibraryManager.hookReturnValue = 1;
                    break;
                case 1:
                    LibraryManager.hookReturnValue = 0;
                    break;
                case 2:
                    LibraryManager.hookReturnValue = -1;
                    break;
            }
        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex > -1)
            {
                KeyEvent keyEvent = (KeyEvent)listBox1.SelectedItem;
                FillEventProperties(keyEvent);

            }
        }

        private void trackBar1_Scroll(object sender, EventArgs e)
        {
            textBox18.Text = trackBar1.Value.ToString() + " s";
        }

        private enum SendMethod
        {
            SM_ScanCode,
            SM_VirtualCode,
            SM_Text
        }

        private string sendText;

        private void prepareKeysSend(SendMethod method)
        {
            button6.Enabled = false;
            button7.Enabled = false;
            button8.Enabled = false;
            trackBar1.Enabled = false;

            switch (method)
            {
                case SendMethod.SM_ScanCode:
                    sendText=textBox7.Text;
                    break;
                case SendMethod.SM_VirtualCode:
                    sendText=textBox8.Text;
                    break;
                case SendMethod.SM_Text:
                    sendText = textBox9.Text;
                    break;
            }


            textBox6.Focus();

            if (trackBar1.Value > 0)
            {
                sendMethod = method;
                timer1.Interval = 1000*trackBar1.Value;
                timer1.Enabled = true;
            }
            else
            {
                SentKeys(method);
            }
        }

        private SendMethod sendMethod;

        private void SentKeys(SendMethod method)
        {
            int result = 0;

            switch (method)
            {
                case SendMethod.SM_ScanCode:
                    {
                        LibraryManager.SendKeyFlags flags = LibraryManager.SendKeyFlags.SKF_KeyPress;
                        if (checkBox6.Checked)
                        {
                            flags = flags | LibraryManager.SendKeyFlags.SKF_KeyExtended;
                        }

                        int scanCode = 0;

                        try
                        {
                            scanCode = int.Parse(sendText);
                        }
                        catch (Exception e)
                        {
                            textBox15.Text = "E";
                            break;
                        }

                        result = LibraryManager.sendKeyByScanCode(scanCode, flags);
                        textBox15.Text = result.ToString();
                        break;
                    }
                case SendMethod.SM_VirtualCode:
                    {
                        LibraryManager.SendKeyFlags flags = LibraryManager.SendKeyFlags.SKF_KeyPress;
                        if (checkBox7.Checked)
                        {
                            flags = flags | LibraryManager.SendKeyFlags.SKF_KeyExtended;
                        }

                        int virtualCode = 0;

                        try
                        {
                            virtualCode = int.Parse(sendText);
                        }
                        catch (Exception e)
                        {
                            textBox16.Text = "E";
                            break;
                        }

                        result = LibraryManager.sendKeyByVirtualCode(virtualCode, flags);
                        textBox16.Text = result.ToString();
                        break;
                    }
                case SendMethod.SM_Text:
                    {
                        result = LibraryManager.sendText(sendText);
                        textBox17.Text = result.ToString();
                        break;
                    }
            }
            
            button6.Enabled = true;
            button7.Enabled = true;
            button8.Enabled = true;
            trackBar1.Enabled = true;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            SentKeys(sendMethod);
            timer1.Enabled = false;
        }

        private void button7_Click(object sender, EventArgs e)
        {
            prepareKeysSend(SendMethod.SM_VirtualCode);
        }

        private void button8_Click(object sender, EventArgs e)
        {
            prepareKeysSend(SendMethod.SM_Text);
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (button2.Enabled)
            {
                int result = LibraryManager.close();
            }
        }
    }
}
