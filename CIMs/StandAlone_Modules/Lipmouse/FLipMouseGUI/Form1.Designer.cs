namespace MouseApp2
{
    partial class FLipMouseGUI
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.serialPort1 = new System.IO.Ports.SerialPort(this.components);
            this.portComboBox = new System.Windows.Forms.ComboBox();
            this.label3 = new System.Windows.Forms.Label();
            this.SelectButton = new System.Windows.Forms.Button();
            this.saveSettings = new System.Windows.Forms.Button();
            this.dcButton = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.portStatus = new System.Windows.Forms.Label();
            this.ClearButton = new System.Windows.Forms.Button();
            this.ApplyButton = new System.Windows.Forms.Button();
            this.tabControl = new System.Windows.Forms.TabControl();
            this.LipmouseTab = new System.Windows.Forms.TabPage();
            this.deadzoneLabel = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.deadzoneBar = new System.Windows.Forms.TrackBar();
            this.mouseOffButton = new System.Windows.Forms.Button();
            this.calButton = new System.Windows.Forms.Button();
            this.speedLabel = new System.Windows.Forms.Label();
            this.speedBar = new System.Windows.Forms.TrackBar();
            this.SpeedNameLabel = new System.Windows.Forms.Label();
            this.PressureTab = new System.Windows.Forms.TabPage();
            this.panel1 = new System.Windows.Forms.Panel();
            this.pressureLabel = new System.Windows.Forms.Label();
            this.PuffComboBox = new System.Windows.Forms.ComboBox();
            this.PuffParameterText = new System.Windows.Forms.TextBox();
            this.SipComboBox = new System.Windows.Forms.ComboBox();
            this.SipParameterText = new System.Windows.Forms.TextBox();
            this.PuffParameterLabel = new System.Windows.Forms.Label();
            this.SipParameterLabel = new System.Windows.Forms.Label();
            this.PuffNumericParameter = new System.Windows.Forms.NumericUpDown();
            this.SipNumericParameter = new System.Windows.Forms.NumericUpDown();
            this.puffThresholdLabel = new System.Windows.Forms.Label();
            this.puffThresholdBar = new System.Windows.Forms.TrackBar();
            this.thresholdLabelForPuff = new System.Windows.Forms.Label();
            this.label20 = new System.Windows.Forms.Label();
            this.PuffFunctionMenu = new System.Windows.Forms.ComboBox();
            this.sipThresholdLabel = new System.Windows.Forms.Label();
            this.sipThresholdBar = new System.Windows.Forms.TrackBar();
            this.thresholdLabelForSip = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.SipFunctionMenu = new System.Windows.Forms.ComboBox();
            this.ButtonsTab = new System.Windows.Forms.TabPage();
            this.Button3ComboBox = new System.Windows.Forms.ComboBox();
            this.Button2ComboBox = new System.Windows.Forms.ComboBox();
            this.Button1ComboBox = new System.Windows.Forms.ComboBox();
            this.Button3NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button2NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button1NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button3Label = new System.Windows.Forms.Label();
            this.Button3ParameterText = new System.Windows.Forms.TextBox();
            this.Button2Label = new System.Windows.Forms.Label();
            this.Button2ParameterText = new System.Windows.Forms.TextBox();
            this.Button1Label = new System.Windows.Forms.Label();
            this.Button1ParameterText = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.Button3FunctionBox = new System.Windows.Forms.ComboBox();
            this.label7 = new System.Windows.Forms.Label();
            this.Button2FunctionBox = new System.Windows.Forms.ComboBox();
            this.label6 = new System.Windows.Forms.Label();
            this.Button1FunctionBox = new System.Windows.Forms.ComboBox();
            this.slotName = new System.Windows.Forms.TextBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.activityLogTextbox = new System.Windows.Forms.RichTextBox();
            this.tabControl.SuspendLayout();
            this.LipmouseTab.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.deadzoneBar)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.speedBar)).BeginInit();
            this.PressureTab.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.PuffNumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.SipNumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.puffThresholdBar)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.sipThresholdBar)).BeginInit();
            this.ButtonsTab.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Button3NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button2NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button1NumericParameter)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // serialPort1
            // 
            this.serialPort1.BaudRate = 115200;
            // 
            // portComboBox
            // 
            this.portComboBox.Location = new System.Drawing.Point(57, 65);
            this.portComboBox.Name = "portComboBox";
            this.portComboBox.Size = new System.Drawing.Size(132, 28);
            this.portComboBox.TabIndex = 60;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(55, 32);
            this.label3.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(342, 20);
            this.label3.TabIndex = 7;
            this.label3.Text = "Please select the COM Port of your FLipMouse:";
            // 
            // SelectButton
            // 
            this.SelectButton.Location = new System.Drawing.Point(202, 63);
            this.SelectButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.SelectButton.Name = "SelectButton";
            this.SelectButton.Size = new System.Drawing.Size(92, 35);
            this.SelectButton.TabIndex = 8;
            this.SelectButton.Text = "Connect";
            this.SelectButton.UseVisualStyleBackColor = true;
            this.SelectButton.Click += new System.EventHandler(this.select_Click);
            // 
            // saveSettings
            // 
            this.saveSettings.Enabled = false;
            this.saveSettings.ForeColor = System.Drawing.Color.Black;
            this.saveSettings.Location = new System.Drawing.Point(278, 615);
            this.saveSettings.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.saveSettings.Name = "saveSettings";
            this.saveSettings.Size = new System.Drawing.Size(151, 47);
            this.saveSettings.TabIndex = 14;
            this.saveSettings.Text = "Store slot as:";
            this.saveSettings.UseVisualStyleBackColor = true;
            this.saveSettings.Click += new System.EventHandler(this.saveSettings_Click);
            // 
            // dcButton
            // 
            this.dcButton.Enabled = false;
            this.dcButton.ForeColor = System.Drawing.Color.Black;
            this.dcButton.Location = new System.Drawing.Point(303, 63);
            this.dcButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.dcButton.Name = "dcButton";
            this.dcButton.Size = new System.Drawing.Size(105, 35);
            this.dcButton.TabIndex = 10;
            this.dcButton.Text = "Disconnect";
            this.dcButton.UseVisualStyleBackColor = true;
            this.dcButton.Click += new System.EventHandler(this.dcButton_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(591, 70);
            this.label1.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(93, 20);
            this.label1.TabIndex = 11;
            this.label1.Text = "Port Status:";
            // 
            // portStatus
            // 
            this.portStatus.AutoSize = true;
            this.portStatus.ForeColor = System.Drawing.Color.SlateGray;
            this.portStatus.Location = new System.Drawing.Point(688, 70);
            this.portStatus.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.portStatus.Name = "portStatus";
            this.portStatus.Size = new System.Drawing.Size(107, 20);
            this.portStatus.TabIndex = 12;
            this.portStatus.Text = "Disconnected";
            // 
            // ClearButton
            // 
            this.ClearButton.Enabled = false;
            this.ClearButton.ForeColor = System.Drawing.Color.Black;
            this.ClearButton.Location = new System.Drawing.Point(628, 617);
            this.ClearButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.ClearButton.Name = "ClearButton";
            this.ClearButton.Size = new System.Drawing.Size(169, 49);
            this.ClearButton.TabIndex = 46;
            this.ClearButton.Text = "Clear all Slots";
            this.ClearButton.UseVisualStyleBackColor = true;
            this.ClearButton.Click += new System.EventHandler(this.ClearButton_Click);
            // 
            // ApplyButton
            // 
            this.ApplyButton.Enabled = false;
            this.ApplyButton.ForeColor = System.Drawing.Color.Black;
            this.ApplyButton.Location = new System.Drawing.Point(57, 615);
            this.ApplyButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.ApplyButton.Name = "ApplyButton";
            this.ApplyButton.Size = new System.Drawing.Size(213, 47);
            this.ApplyButton.TabIndex = 36;
            this.ApplyButton.Text = "Apply settings";
            this.ApplyButton.UseVisualStyleBackColor = true;
            this.ApplyButton.Click += new System.EventHandler(this.ApplyButton_Click);
            // 
            // tabControl
            // 
            this.tabControl.Controls.Add(this.LipmouseTab);
            this.tabControl.Controls.Add(this.PressureTab);
            this.tabControl.Controls.Add(this.ButtonsTab);
            this.tabControl.Location = new System.Drawing.Point(56, 133);
            this.tabControl.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.tabControl.Name = "tabControl";
            this.tabControl.SelectedIndex = 0;
            this.tabControl.Size = new System.Drawing.Size(745, 474);
            this.tabControl.TabIndex = 61;
            this.tabControl.SelectedIndexChanged += new System.EventHandler(this.tabControl_SelectedIndexChanged);
            // 
            // LipmouseTab
            // 
            this.LipmouseTab.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.LipmouseTab.Controls.Add(this.deadzoneLabel);
            this.LipmouseTab.Controls.Add(this.label2);
            this.LipmouseTab.Controls.Add(this.deadzoneBar);
            this.LipmouseTab.Controls.Add(this.mouseOffButton);
            this.LipmouseTab.Controls.Add(this.calButton);
            this.LipmouseTab.Controls.Add(this.speedLabel);
            this.LipmouseTab.Controls.Add(this.speedBar);
            this.LipmouseTab.Controls.Add(this.SpeedNameLabel);
            this.LipmouseTab.Location = new System.Drawing.Point(4, 29);
            this.LipmouseTab.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.LipmouseTab.Name = "LipmouseTab";
            this.LipmouseTab.Padding = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.LipmouseTab.Size = new System.Drawing.Size(737, 441);
            this.LipmouseTab.TabIndex = 0;
            this.LipmouseTab.Text = "Mouse Movement";
            // 
            // deadzoneLabel
            // 
            this.deadzoneLabel.AutoSize = true;
            this.deadzoneLabel.Location = new System.Drawing.Point(629, 193);
            this.deadzoneLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.deadzoneLabel.Name = "deadzoneLabel";
            this.deadzoneLabel.Size = new System.Drawing.Size(36, 20);
            this.deadzoneLabel.TabIndex = 21;
            this.deadzoneLabel.Text = "100";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(85, 193);
            this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(83, 20);
            this.label2.TabIndex = 20;
            this.label2.Text = "Deadzone";
            // 
            // deadzoneBar
            // 
            this.deadzoneBar.LargeChange = 10;
            this.deadzoneBar.Location = new System.Drawing.Point(73, 218);
            this.deadzoneBar.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.deadzoneBar.Maximum = 250;
            this.deadzoneBar.Name = "deadzoneBar";
            this.deadzoneBar.Size = new System.Drawing.Size(604, 69);
            this.deadzoneBar.TabIndex = 17;
            this.deadzoneBar.TickFrequency = 20;
            this.deadzoneBar.Value = 100;
            this.deadzoneBar.Scroll += new System.EventHandler(this.deadzone_Scroll);
            // 
            // mouseOffButton
            // 
            this.mouseOffButton.Enabled = false;
            this.mouseOffButton.ForeColor = System.Drawing.Color.Black;
            this.mouseOffButton.Location = new System.Drawing.Point(469, 325);
            this.mouseOffButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.mouseOffButton.Name = "mouseOffButton";
            this.mouseOffButton.Size = new System.Drawing.Size(207, 46);
            this.mouseOffButton.TabIndex = 19;
            this.mouseOffButton.Text = "Turn mouse off";
            this.mouseOffButton.UseVisualStyleBackColor = true;
            this.mouseOffButton.Click += new System.EventHandler(this.onOff_Click);
            // 
            // calButton
            // 
            this.calButton.Enabled = false;
            this.calButton.ForeColor = System.Drawing.Color.Black;
            this.calButton.Location = new System.Drawing.Point(86, 325);
            this.calButton.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.calButton.Name = "calButton";
            this.calButton.Size = new System.Drawing.Size(280, 46);
            this.calButton.TabIndex = 18;
            this.calButton.Text = "Calibrate Middle Position";
            this.calButton.UseVisualStyleBackColor = true;
            this.calButton.Click += new System.EventHandler(this.calibration_Click);
            // 
            // speedLabel
            // 
            this.speedLabel.AutoSize = true;
            this.speedLabel.Location = new System.Drawing.Point(638, 75);
            this.speedLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.speedLabel.Name = "speedLabel";
            this.speedLabel.Size = new System.Drawing.Size(27, 20);
            this.speedLabel.TabIndex = 16;
            this.speedLabel.Text = "10";
            // 
            // speedBar
            // 
            this.speedBar.LargeChange = 1;
            this.speedBar.Location = new System.Drawing.Point(73, 100);
            this.speedBar.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.speedBar.Maximum = 20;
            this.speedBar.Minimum = 1;
            this.speedBar.Name = "speedBar";
            this.speedBar.Size = new System.Drawing.Size(604, 69);
            this.speedBar.TabIndex = 15;
            this.speedBar.TickFrequency = 2;
            this.speedBar.Value = 10;
            this.speedBar.Scroll += new System.EventHandler(this.speedBar_Scroll);
            // 
            // SpeedNameLabel
            // 
            this.SpeedNameLabel.AutoSize = true;
            this.SpeedNameLabel.Location = new System.Drawing.Point(83, 77);
            this.SpeedNameLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.SpeedNameLabel.Name = "SpeedNameLabel";
            this.SpeedNameLabel.Size = new System.Drawing.Size(56, 20);
            this.SpeedNameLabel.TabIndex = 14;
            this.SpeedNameLabel.Text = "Speed";
            // 
            // PressureTab
            // 
            this.PressureTab.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.PressureTab.Controls.Add(this.panel1);
            this.PressureTab.Controls.Add(this.pressureLabel);
            this.PressureTab.Controls.Add(this.PuffComboBox);
            this.PressureTab.Controls.Add(this.PuffParameterText);
            this.PressureTab.Controls.Add(this.SipComboBox);
            this.PressureTab.Controls.Add(this.SipParameterText);
            this.PressureTab.Controls.Add(this.PuffParameterLabel);
            this.PressureTab.Controls.Add(this.SipParameterLabel);
            this.PressureTab.Controls.Add(this.PuffNumericParameter);
            this.PressureTab.Controls.Add(this.SipNumericParameter);
            this.PressureTab.Controls.Add(this.puffThresholdLabel);
            this.PressureTab.Controls.Add(this.puffThresholdBar);
            this.PressureTab.Controls.Add(this.thresholdLabelForPuff);
            this.PressureTab.Controls.Add(this.label20);
            this.PressureTab.Controls.Add(this.PuffFunctionMenu);
            this.PressureTab.Controls.Add(this.sipThresholdLabel);
            this.PressureTab.Controls.Add(this.sipThresholdBar);
            this.PressureTab.Controls.Add(this.thresholdLabelForSip);
            this.PressureTab.Controls.Add(this.label5);
            this.PressureTab.Controls.Add(this.SipFunctionMenu);
            this.PressureTab.Location = new System.Drawing.Point(4, 29);
            this.PressureTab.Name = "PressureTab";
            this.PressureTab.Size = new System.Drawing.Size(737, 441);
            this.PressureTab.TabIndex = 2;
            this.PressureTab.Text = "Sip/Puff Actions";
            // 
            // panel1
            // 
            this.panel1.Location = new System.Drawing.Point(537, 113);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(44, 256);
            this.panel1.TabIndex = 90;
            // 
            // pressureLabel
            // 
            this.pressureLabel.AutoSize = true;
            this.pressureLabel.Location = new System.Drawing.Point(540, 86);
            this.pressureLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.pressureLabel.Name = "pressureLabel";
            this.pressureLabel.Size = new System.Drawing.Size(18, 20);
            this.pressureLabel.TabIndex = 81;
            this.pressureLabel.Text = "0";
            this.pressureLabel.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // PuffComboBox
            // 
            this.PuffComboBox.FormattingEnabled = true;
            this.PuffComboBox.Location = new System.Drawing.Point(301, 304);
            this.PuffComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.PuffComboBox.Name = "PuffComboBox";
            this.PuffComboBox.Size = new System.Drawing.Size(102, 28);
            this.PuffComboBox.TabIndex = 89;
            this.PuffComboBox.Visible = false;
            // 
            // PuffParameterText
            // 
            this.PuffParameterText.Location = new System.Drawing.Point(172, 305);
            this.PuffParameterText.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.PuffParameterText.Name = "PuffParameterText";
            this.PuffParameterText.Size = new System.Drawing.Size(121, 26);
            this.PuffParameterText.TabIndex = 88;
            this.PuffParameterText.Visible = false;
            // 
            // SipComboBox
            // 
            this.SipComboBox.FormattingEnabled = true;
            this.SipComboBox.Location = new System.Drawing.Point(304, 176);
            this.SipComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.SipComboBox.Name = "SipComboBox";
            this.SipComboBox.Size = new System.Drawing.Size(103, 28);
            this.SipComboBox.TabIndex = 87;
            this.SipComboBox.Visible = false;
            // 
            // SipParameterText
            // 
            this.SipParameterText.Location = new System.Drawing.Point(175, 178);
            this.SipParameterText.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.SipParameterText.Name = "SipParameterText";
            this.SipParameterText.Size = new System.Drawing.Size(121, 26);
            this.SipParameterText.TabIndex = 86;
            this.SipParameterText.Visible = false;
            // 
            // PuffParameterLabel
            // 
            this.PuffParameterLabel.AutoSize = true;
            this.PuffParameterLabel.Location = new System.Drawing.Point(70, 309);
            this.PuffParameterLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.PuffParameterLabel.Name = "PuffParameterLabel";
            this.PuffParameterLabel.Size = new System.Drawing.Size(83, 20);
            this.PuffParameterLabel.TabIndex = 85;
            this.PuffParameterLabel.Text = "Parameter";
            // 
            // SipParameterLabel
            // 
            this.SipParameterLabel.AutoSize = true;
            this.SipParameterLabel.Location = new System.Drawing.Point(73, 183);
            this.SipParameterLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.SipParameterLabel.Name = "SipParameterLabel";
            this.SipParameterLabel.Size = new System.Drawing.Size(83, 20);
            this.SipParameterLabel.TabIndex = 84;
            this.SipParameterLabel.Text = "Parameter";
            // 
            // PuffNumericParameter
            // 
            this.PuffNumericParameter.Location = new System.Drawing.Point(176, 306);
            this.PuffNumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.PuffNumericParameter.Name = "PuffNumericParameter";
            this.PuffNumericParameter.Size = new System.Drawing.Size(64, 26);
            this.PuffNumericParameter.TabIndex = 83;
            this.PuffNumericParameter.Visible = false;
            // 
            // SipNumericParameter
            // 
            this.SipNumericParameter.Location = new System.Drawing.Point(178, 180);
            this.SipNumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.SipNumericParameter.Name = "SipNumericParameter";
            this.SipNumericParameter.Size = new System.Drawing.Size(64, 26);
            this.SipNumericParameter.TabIndex = 82;
            this.SipNumericParameter.Visible = false;
            // 
            // puffThresholdLabel
            // 
            this.puffThresholdLabel.AutoSize = true;
            this.puffThresholdLabel.Location = new System.Drawing.Point(614, 85);
            this.puffThresholdLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.puffThresholdLabel.Name = "puffThresholdLabel";
            this.puffThresholdLabel.Size = new System.Drawing.Size(36, 20);
            this.puffThresholdLabel.TabIndex = 80;
            this.puffThresholdLabel.Text = "525";
            // 
            // puffThresholdBar
            // 
            this.puffThresholdBar.AllowDrop = true;
            this.puffThresholdBar.LargeChange = 20;
            this.puffThresholdBar.Location = new System.Drawing.Point(581, 113);
            this.puffThresholdBar.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.puffThresholdBar.Maximum = 1023;
            this.puffThresholdBar.Minimum = 514;
            this.puffThresholdBar.Name = "puffThresholdBar";
            this.puffThresholdBar.Orientation = System.Windows.Forms.Orientation.Vertical;
            this.puffThresholdBar.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.puffThresholdBar.RightToLeftLayout = true;
            this.puffThresholdBar.Size = new System.Drawing.Size(69, 146);
            this.puffThresholdBar.TabIndex = 79;
            this.puffThresholdBar.TickFrequency = 100;
            this.puffThresholdBar.Value = 525;
            // 
            // thresholdLabelForPuff
            // 
            this.thresholdLabelForPuff.AutoSize = true;
            this.thresholdLabelForPuff.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.thresholdLabelForPuff.Location = new System.Drawing.Point(574, 60);
            this.thresholdLabelForPuff.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.thresholdLabelForPuff.Name = "thresholdLabelForPuff";
            this.thresholdLabelForPuff.Size = new System.Drawing.Size(112, 20);
            this.thresholdLabelForPuff.TabIndex = 78;
            this.thresholdLabelForPuff.Text = "Puff Threshold";
            this.thresholdLabelForPuff.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // label20
            // 
            this.label20.AutoSize = true;
            this.label20.Location = new System.Drawing.Point(48, 273);
            this.label20.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label20.Name = "label20";
            this.label20.Size = new System.Drawing.Size(104, 20);
            this.label20.TabIndex = 77;
            this.label20.Text = "Puff Function";
            // 
            // PuffFunctionMenu
            // 
            this.PuffFunctionMenu.FormattingEnabled = true;
            this.PuffFunctionMenu.Location = new System.Drawing.Point(172, 268);
            this.PuffFunctionMenu.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.PuffFunctionMenu.Name = "PuffFunctionMenu";
            this.PuffFunctionMenu.Size = new System.Drawing.Size(232, 28);
            this.PuffFunctionMenu.TabIndex = 76;
            // 
            // sipThresholdLabel
            // 
            this.sipThresholdLabel.AutoSize = true;
            this.sipThresholdLabel.Location = new System.Drawing.Point(459, 83);
            this.sipThresholdLabel.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.sipThresholdLabel.Name = "sipThresholdLabel";
            this.sipThresholdLabel.Size = new System.Drawing.Size(36, 20);
            this.sipThresholdLabel.TabIndex = 75;
            this.sipThresholdLabel.Text = "500";
            // 
            // sipThresholdBar
            // 
            this.sipThresholdBar.LargeChange = 20;
            this.sipThresholdBar.Location = new System.Drawing.Point(467, 227);
            this.sipThresholdBar.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.sipThresholdBar.Maximum = 510;
            this.sipThresholdBar.Name = "sipThresholdBar";
            this.sipThresholdBar.Orientation = System.Windows.Forms.Orientation.Vertical;
            this.sipThresholdBar.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.sipThresholdBar.Size = new System.Drawing.Size(69, 142);
            this.sipThresholdBar.TabIndex = 74;
            this.sipThresholdBar.TickFrequency = 100;
            this.sipThresholdBar.Value = 500;
            // 
            // thresholdLabelForSip
            // 
            this.thresholdLabelForSip.AutoSize = true;
            this.thresholdLabelForSip.Location = new System.Drawing.Point(426, 60);
            this.thresholdLabelForSip.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.thresholdLabelForSip.Name = "thresholdLabelForSip";
            this.thresholdLabelForSip.Size = new System.Drawing.Size(106, 20);
            this.thresholdLabelForSip.TabIndex = 73;
            this.thresholdLabelForSip.Text = "Sip Threshold";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(58, 141);
            this.label5.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(98, 20);
            this.label5.TabIndex = 72;
            this.label5.Text = "Sip Function";
            // 
            // SipFunctionMenu
            // 
            this.SipFunctionMenu.FormattingEnabled = true;
            this.SipFunctionMenu.Location = new System.Drawing.Point(176, 136);
            this.SipFunctionMenu.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.SipFunctionMenu.Name = "SipFunctionMenu";
            this.SipFunctionMenu.Size = new System.Drawing.Size(230, 28);
            this.SipFunctionMenu.TabIndex = 71;
            // 
            // ButtonsTab
            // 
            this.ButtonsTab.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.ButtonsTab.Controls.Add(this.Button3ComboBox);
            this.ButtonsTab.Controls.Add(this.Button2ComboBox);
            this.ButtonsTab.Controls.Add(this.Button1ComboBox);
            this.ButtonsTab.Controls.Add(this.Button3NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button2NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button1NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button3Label);
            this.ButtonsTab.Controls.Add(this.Button3ParameterText);
            this.ButtonsTab.Controls.Add(this.Button2Label);
            this.ButtonsTab.Controls.Add(this.Button2ParameterText);
            this.ButtonsTab.Controls.Add(this.Button1Label);
            this.ButtonsTab.Controls.Add(this.Button1ParameterText);
            this.ButtonsTab.Controls.Add(this.label8);
            this.ButtonsTab.Controls.Add(this.Button3FunctionBox);
            this.ButtonsTab.Controls.Add(this.label7);
            this.ButtonsTab.Controls.Add(this.Button2FunctionBox);
            this.ButtonsTab.Controls.Add(this.label6);
            this.ButtonsTab.Controls.Add(this.Button1FunctionBox);
            this.ButtonsTab.Location = new System.Drawing.Point(4, 29);
            this.ButtonsTab.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.ButtonsTab.Name = "ButtonsTab";
            this.ButtonsTab.Padding = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.ButtonsTab.Size = new System.Drawing.Size(737, 441);
            this.ButtonsTab.TabIndex = 1;
            this.ButtonsTab.Text = "Button Actions";
            // 
            // Button3ComboBox
            // 
            this.Button3ComboBox.FormattingEnabled = true;
            this.Button3ComboBox.Location = new System.Drawing.Point(534, 321);
            this.Button3ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button3ComboBox.Name = "Button3ComboBox";
            this.Button3ComboBox.Size = new System.Drawing.Size(135, 28);
            this.Button3ComboBox.TabIndex = 114;
            this.Button3ComboBox.Visible = false;
            this.Button3ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button3ComboBox_SelectedIndexChanged);
            // 
            // Button2ComboBox
            // 
            this.Button2ComboBox.FormattingEnabled = true;
            this.Button2ComboBox.Location = new System.Drawing.Point(534, 214);
            this.Button2ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button2ComboBox.Name = "Button2ComboBox";
            this.Button2ComboBox.Size = new System.Drawing.Size(135, 28);
            this.Button2ComboBox.TabIndex = 113;
            this.Button2ComboBox.Visible = false;
            this.Button2ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button2ComboBox_SelectedIndexChanged);
            // 
            // Button1ComboBox
            // 
            this.Button1ComboBox.FormattingEnabled = true;
            this.Button1ComboBox.Location = new System.Drawing.Point(534, 117);
            this.Button1ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button1ComboBox.Name = "Button1ComboBox";
            this.Button1ComboBox.Size = new System.Drawing.Size(135, 28);
            this.Button1ComboBox.TabIndex = 112;
            this.Button1ComboBox.Visible = false;
            this.Button1ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button1ComboBox_SelectedIndexChanged);
            // 
            // Button3NumericParameter
            // 
            this.Button3NumericParameter.Location = new System.Drawing.Point(252, 325);
            this.Button3NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button3NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button3NumericParameter.Name = "Button3NumericParameter";
            this.Button3NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button3NumericParameter.TabIndex = 109;
            this.Button3NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button3NumericParameter.Visible = false;
            this.Button3NumericParameter.ValueChanged += new System.EventHandler(this.Button3NumericParameter_ValueChanged);
            // 
            // Button2NumericParameter
            // 
            this.Button2NumericParameter.Location = new System.Drawing.Point(252, 215);
            this.Button2NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button2NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button2NumericParameter.Name = "Button2NumericParameter";
            this.Button2NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button2NumericParameter.TabIndex = 108;
            this.Button2NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button2NumericParameter.Visible = false;
            this.Button2NumericParameter.ValueChanged += new System.EventHandler(this.Button2NumericParameter_ValueChanged);
            // 
            // Button1NumericParameter
            // 
            this.Button1NumericParameter.Location = new System.Drawing.Point(252, 116);
            this.Button1NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button1NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button1NumericParameter.Name = "Button1NumericParameter";
            this.Button1NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button1NumericParameter.TabIndex = 107;
            this.Button1NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button1NumericParameter.Visible = false;
            this.Button1NumericParameter.ValueChanged += new System.EventHandler(this.Button1NumericParameter_ValueChanged);
            // 
            // Button3Label
            // 
            this.Button3Label.AutoSize = true;
            this.Button3Label.Location = new System.Drawing.Point(146, 327);
            this.Button3Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button3Label.Name = "Button3Label";
            this.Button3Label.Size = new System.Drawing.Size(87, 20);
            this.Button3Label.TabIndex = 102;
            this.Button3Label.Text = "Parameter:";
            // 
            // Button3ParameterText
            // 
            this.Button3ParameterText.Location = new System.Drawing.Point(252, 323);
            this.Button3ParameterText.Name = "Button3ParameterText";
            this.Button3ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button3ParameterText.TabIndex = 101;
            // 
            // Button2Label
            // 
            this.Button2Label.AutoSize = true;
            this.Button2Label.Location = new System.Drawing.Point(146, 217);
            this.Button2Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button2Label.Name = "Button2Label";
            this.Button2Label.Size = new System.Drawing.Size(87, 20);
            this.Button2Label.TabIndex = 100;
            this.Button2Label.Text = "Parameter:";
            // 
            // Button2ParameterText
            // 
            this.Button2ParameterText.Location = new System.Drawing.Point(252, 214);
            this.Button2ParameterText.Name = "Button2ParameterText";
            this.Button2ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button2ParameterText.TabIndex = 99;
            // 
            // Button1Label
            // 
            this.Button1Label.AutoSize = true;
            this.Button1Label.Location = new System.Drawing.Point(146, 118);
            this.Button1Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button1Label.Name = "Button1Label";
            this.Button1Label.Size = new System.Drawing.Size(87, 20);
            this.Button1Label.TabIndex = 98;
            this.Button1Label.Text = "Parameter:";
            this.Button1Label.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // Button1ParameterText
            // 
            this.Button1ParameterText.Location = new System.Drawing.Point(252, 118);
            this.Button1ParameterText.Name = "Button1ParameterText";
            this.Button1ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button1ParameterText.TabIndex = 97;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(74, 289);
            this.label8.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(159, 20);
            this.label8.TabIndex = 92;
            this.label8.Text = "Function for Button3:";
            // 
            // Button3FunctionBox
            // 
            this.Button3FunctionBox.FormattingEnabled = true;
            this.Button3FunctionBox.Location = new System.Drawing.Point(252, 286);
            this.Button3FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button3FunctionBox.Name = "Button3FunctionBox";
            this.Button3FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button3FunctionBox.TabIndex = 91;
            this.Button3FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button3FunctionBox_SelectedIndexChanged_1);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(74, 179);
            this.label7.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(159, 20);
            this.label7.TabIndex = 90;
            this.label7.Text = "Function for Button2:";
            // 
            // Button2FunctionBox
            // 
            this.Button2FunctionBox.FormattingEnabled = true;
            this.Button2FunctionBox.Location = new System.Drawing.Point(252, 176);
            this.Button2FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button2FunctionBox.Name = "Button2FunctionBox";
            this.Button2FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button2FunctionBox.TabIndex = 89;
            this.Button2FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button2FunctionBox_SelectedIndexChanged_1);
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(74, 81);
            this.label6.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(159, 20);
            this.label6.TabIndex = 88;
            this.label6.Text = "Function for Button1:";
            // 
            // Button1FunctionBox
            // 
            this.Button1FunctionBox.FormattingEnabled = true;
            this.Button1FunctionBox.Location = new System.Drawing.Point(252, 78);
            this.Button1FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button1FunctionBox.Name = "Button1FunctionBox";
            this.Button1FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button1FunctionBox.TabIndex = 87;
            this.Button1FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button1FunctionBox_SelectedIndexChanged_1);
            // 
            // slotName
            // 
            this.slotName.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.slotName.ForeColor = System.Drawing.SystemColors.MenuHighlight;
            this.slotName.Location = new System.Drawing.Point(437, 623);
            this.slotName.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.slotName.Multiline = true;
            this.slotName.Name = "slotName";
            this.slotName.Size = new System.Drawing.Size(166, 32);
            this.slotName.TabIndex = 71;
            this.slotName.Text = "Slot1";
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.activityLogTextbox);
            this.groupBox1.Location = new System.Drawing.Point(61, 701);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(740, 115);
            this.groupBox1.TabIndex = 74;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Activity-Log";
            // 
            // activityLogTextbox
            // 
            this.activityLogTextbox.ForeColor = System.Drawing.SystemColors.MenuText;
            this.activityLogTextbox.HideSelection = false;
            this.activityLogTextbox.Location = new System.Drawing.Point(19, 27);
            this.activityLogTextbox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.activityLogTextbox.Name = "activityLogTextbox";
            this.activityLogTextbox.ReadOnly = true;
            this.activityLogTextbox.Size = new System.Drawing.Size(701, 68);
            this.activityLogTextbox.TabIndex = 63;
            this.activityLogTextbox.Text = "";
            // 
            // LipMouseGUI
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.AutoSize = true;
            this.ClientSize = new System.Drawing.Size(854, 852);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.slotName);
            this.Controls.Add(this.tabControl);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.portStatus);
            this.Controls.Add(this.saveSettings);
            this.Controls.Add(this.ClearButton);
            this.Controls.Add(this.ApplyButton);
            this.Controls.Add(this.dcButton);
            this.Controls.Add(this.SelectButton);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.portComboBox);
            this.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Name = "LipMouseGUI";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "FLipMouse Settings Manager";
            this.Load += new System.EventHandler(this.LipmouseGUI_Load);
            this.tabControl.ResumeLayout(false);
            this.LipmouseTab.ResumeLayout(false);
            this.LipmouseTab.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.deadzoneBar)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.speedBar)).EndInit();
            this.PressureTab.ResumeLayout(false);
            this.PressureTab.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.PuffNumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.SipNumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.puffThresholdBar)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.sipThresholdBar)).EndInit();
            this.ButtonsTab.ResumeLayout(false);
            this.ButtonsTab.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Button3NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button2NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button1NumericParameter)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.IO.Ports.SerialPort serialPort1;
        private System.Windows.Forms.ComboBox portComboBox;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button SelectButton;
        private System.Windows.Forms.Button dcButton;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label portStatus;
        private System.Windows.Forms.Button saveSettings;
        private System.Windows.Forms.Button ClearButton;
        private System.Windows.Forms.Button ApplyButton;
        private System.Windows.Forms.TabControl tabControl;
        private System.Windows.Forms.TabPage LipmouseTab;
        private System.Windows.Forms.TabPage ButtonsTab;
        private System.Windows.Forms.TextBox slotName;
        private System.Windows.Forms.Label deadzoneLabel;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TrackBar deadzoneBar;
        private System.Windows.Forms.Button mouseOffButton;
        private System.Windows.Forms.Button calButton;
        private System.Windows.Forms.Label speedLabel;
        private System.Windows.Forms.TrackBar speedBar;
        private System.Windows.Forms.Label SpeedNameLabel;
        private System.Windows.Forms.TabPage PressureTab;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Label pressureLabel;
        private System.Windows.Forms.ComboBox PuffComboBox;
        private System.Windows.Forms.TextBox PuffParameterText;
        private System.Windows.Forms.ComboBox SipComboBox;
        private System.Windows.Forms.TextBox SipParameterText;
        private System.Windows.Forms.Label PuffParameterLabel;
        private System.Windows.Forms.Label SipParameterLabel;
        private System.Windows.Forms.NumericUpDown PuffNumericParameter;
        private System.Windows.Forms.NumericUpDown SipNumericParameter;
        private System.Windows.Forms.Label puffThresholdLabel;
        private System.Windows.Forms.TrackBar puffThresholdBar;
        private System.Windows.Forms.Label thresholdLabelForPuff;
        private System.Windows.Forms.Label label20;
        private System.Windows.Forms.ComboBox PuffFunctionMenu;
        private System.Windows.Forms.Label sipThresholdLabel;
        private System.Windows.Forms.TrackBar sipThresholdBar;
        private System.Windows.Forms.Label thresholdLabelForSip;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.ComboBox SipFunctionMenu;
        private System.Windows.Forms.ComboBox Button3ComboBox;
        private System.Windows.Forms.ComboBox Button2ComboBox;
        private System.Windows.Forms.ComboBox Button1ComboBox;
        private System.Windows.Forms.NumericUpDown Button3NumericParameter;
        private System.Windows.Forms.NumericUpDown Button2NumericParameter;
        private System.Windows.Forms.NumericUpDown Button1NumericParameter;
        private System.Windows.Forms.Label Button3Label;
        private System.Windows.Forms.TextBox Button3ParameterText;
        private System.Windows.Forms.Label Button2Label;
        private System.Windows.Forms.TextBox Button2ParameterText;
        private System.Windows.Forms.Label Button1Label;
        private System.Windows.Forms.TextBox Button1ParameterText;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.ComboBox Button3FunctionBox;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.ComboBox Button2FunctionBox;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.ComboBox Button1FunctionBox;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.RichTextBox activityLogTextbox;
    }
}

