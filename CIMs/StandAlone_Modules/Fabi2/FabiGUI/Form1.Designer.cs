namespace MouseApp2
{
    partial class FabiGUI
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
            this.slotName = new System.Windows.Forms.TextBox();
            this.ButtonsTab = new System.Windows.Forms.TabPage();
            this.Button6ComboBox = new System.Windows.Forms.ComboBox();
            this.Button5ComboBox = new System.Windows.Forms.ComboBox();
            this.Button4ComboBox = new System.Windows.Forms.ComboBox();
            this.Button6NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button5NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button4NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button6Label = new System.Windows.Forms.Label();
            this.Button6ParameterText = new System.Windows.Forms.TextBox();
            this.Button5ParameterText = new System.Windows.Forms.TextBox();
            this.Button4ParameterText = new System.Windows.Forms.TextBox();
            this.Button5Label = new System.Windows.Forms.Label();
            this.Button4Label = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.Button6FunctionBox = new System.Windows.Forms.ComboBox();
            this.label10 = new System.Windows.Forms.Label();
            this.Button5FunctionBox = new System.Windows.Forms.ComboBox();
            this.label11 = new System.Windows.Forms.Label();
            this.Button4FunctionBox = new System.Windows.Forms.ComboBox();
            this.Button3ComboBox = new System.Windows.Forms.ComboBox();
            this.Button2ComboBox = new System.Windows.Forms.ComboBox();
            this.Button1ComboBox = new System.Windows.Forms.ComboBox();
            this.Button3NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button2NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button1NumericParameter = new System.Windows.Forms.NumericUpDown();
            this.Button3Label = new System.Windows.Forms.Label();
            this.Button3ParameterText = new System.Windows.Forms.TextBox();
            this.Button2ParameterText = new System.Windows.Forms.TextBox();
            this.Button1ParameterText = new System.Windows.Forms.TextBox();
            this.Button2Label = new System.Windows.Forms.Label();
            this.Button1Label = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.Button3FunctionBox = new System.Windows.Forms.ComboBox();
            this.label7 = new System.Windows.Forms.Label();
            this.Button2FunctionBox = new System.Windows.Forms.ComboBox();
            this.label6 = new System.Windows.Forms.Label();
            this.Button1FunctionBox = new System.Windows.Forms.ComboBox();
            this.tabControl = new System.Windows.Forms.TabControl();
            this.activityLogTextbox = new System.Windows.Forms.RichTextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.ButtonsTab.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Button6NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button5NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button4NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button3NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button2NumericParameter)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button1NumericParameter)).BeginInit();
            this.tabControl.SuspendLayout();
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
            this.label3.Size = new System.Drawing.Size(300, 20);
            this.label3.TabIndex = 7;
            this.label3.Text = "Please select the COM Port of your FABI:";
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
            this.saveSettings.Location = new System.Drawing.Point(761, 615);
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
            this.label1.Location = new System.Drawing.Point(443, 73);
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
            this.portStatus.Location = new System.Drawing.Point(540, 73);
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
            this.ClearButton.Location = new System.Drawing.Point(1111, 617);
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
            this.ApplyButton.Size = new System.Drawing.Size(373, 47);
            this.ApplyButton.TabIndex = 36;
            this.ApplyButton.Text = "Apply settings";
            this.ApplyButton.UseVisualStyleBackColor = true;
            this.ApplyButton.Click += new System.EventHandler(this.ApplyButton_Click);
            // 
            // slotName
            // 
            this.slotName.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.slotName.ForeColor = System.Drawing.SystemColors.MenuHighlight;
            this.slotName.Location = new System.Drawing.Point(920, 623);
            this.slotName.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.slotName.Multiline = true;
            this.slotName.Name = "slotName";
            this.slotName.Size = new System.Drawing.Size(166, 32);
            this.slotName.TabIndex = 71;
            this.slotName.Text = "Slot1";
            // 
            // ButtonsTab
            // 
            this.ButtonsTab.BackColor = System.Drawing.SystemColors.ButtonFace;
            this.ButtonsTab.Controls.Add(this.Button6ComboBox);
            this.ButtonsTab.Controls.Add(this.Button5ComboBox);
            this.ButtonsTab.Controls.Add(this.Button4ComboBox);
            this.ButtonsTab.Controls.Add(this.Button6NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button5NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button4NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button6Label);
            this.ButtonsTab.Controls.Add(this.Button6ParameterText);
            this.ButtonsTab.Controls.Add(this.Button5ParameterText);
            this.ButtonsTab.Controls.Add(this.Button4ParameterText);
            this.ButtonsTab.Controls.Add(this.Button5Label);
            this.ButtonsTab.Controls.Add(this.Button4Label);
            this.ButtonsTab.Controls.Add(this.label9);
            this.ButtonsTab.Controls.Add(this.Button6FunctionBox);
            this.ButtonsTab.Controls.Add(this.label10);
            this.ButtonsTab.Controls.Add(this.Button5FunctionBox);
            this.ButtonsTab.Controls.Add(this.label11);
            this.ButtonsTab.Controls.Add(this.Button4FunctionBox);
            this.ButtonsTab.Controls.Add(this.Button3ComboBox);
            this.ButtonsTab.Controls.Add(this.Button2ComboBox);
            this.ButtonsTab.Controls.Add(this.Button1ComboBox);
            this.ButtonsTab.Controls.Add(this.Button3NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button2NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button1NumericParameter);
            this.ButtonsTab.Controls.Add(this.Button3Label);
            this.ButtonsTab.Controls.Add(this.Button3ParameterText);
            this.ButtonsTab.Controls.Add(this.Button2ParameterText);
            this.ButtonsTab.Controls.Add(this.Button1ParameterText);
            this.ButtonsTab.Controls.Add(this.Button2Label);
            this.ButtonsTab.Controls.Add(this.Button1Label);
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
            this.ButtonsTab.Size = new System.Drawing.Size(1220, 427);
            this.ButtonsTab.TabIndex = 1;
            this.ButtonsTab.Text = "Select Button Functions";
            // 
            // Button6ComboBox
            // 
            this.Button6ComboBox.FormattingEnabled = true;
            this.Button6ComboBox.Location = new System.Drawing.Point(1033, 324);
            this.Button6ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button6ComboBox.Name = "Button6ComboBox";
            this.Button6ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button6ComboBox.TabIndex = 132;
            this.Button6ComboBox.Visible = false;
            this.Button6ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button6ComboBox_SelectedIndexChanged);
            // 
            // Button5ComboBox
            // 
            this.Button5ComboBox.FormattingEnabled = true;
            this.Button5ComboBox.Location = new System.Drawing.Point(1034, 217);
            this.Button5ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button5ComboBox.Name = "Button5ComboBox";
            this.Button5ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button5ComboBox.TabIndex = 131;
            this.Button5ComboBox.Visible = false;
            this.Button5ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button5ComboBox_SelectedIndexChanged);
            // 
            // Button4ComboBox
            // 
            this.Button4ComboBox.FormattingEnabled = true;
            this.Button4ComboBox.Location = new System.Drawing.Point(1035, 120);
            this.Button4ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button4ComboBox.Name = "Button4ComboBox";
            this.Button4ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button4ComboBox.TabIndex = 130;
            this.Button4ComboBox.Visible = false;
            this.Button4ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button4ComboBox_SelectedIndexChanged);
            // 
            // Button6NumericParameter
            // 
            this.Button6NumericParameter.Location = new System.Drawing.Point(770, 328);
            this.Button6NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button6NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button6NumericParameter.Name = "Button6NumericParameter";
            this.Button6NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button6NumericParameter.TabIndex = 129;
            this.Button6NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button6NumericParameter.Visible = false;
            this.Button6NumericParameter.ValueChanged += new System.EventHandler(this.Button6NumericParameter_ValueChanged);
            // 
            // Button5NumericParameter
            // 
            this.Button5NumericParameter.Location = new System.Drawing.Point(770, 218);
            this.Button5NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button5NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button5NumericParameter.Name = "Button5NumericParameter";
            this.Button5NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button5NumericParameter.TabIndex = 128;
            this.Button5NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button5NumericParameter.Visible = false;
            this.Button5NumericParameter.ValueChanged += new System.EventHandler(this.Button5NumericParameter_ValueChanged);
            // 
            // Button4NumericParameter
            // 
            this.Button4NumericParameter.Location = new System.Drawing.Point(770, 119);
            this.Button4NumericParameter.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button4NumericParameter.Minimum = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button4NumericParameter.Name = "Button4NumericParameter";
            this.Button4NumericParameter.Size = new System.Drawing.Size(68, 26);
            this.Button4NumericParameter.TabIndex = 127;
            this.Button4NumericParameter.Value = new decimal(new int[] {
            1,
            0,
            0,
            0});
            this.Button4NumericParameter.Visible = false;
            this.Button4NumericParameter.ValueChanged += new System.EventHandler(this.Button4NumericParameter_ValueChanged);
            // 
            // Button6Label
            // 
            this.Button6Label.AutoSize = true;
            this.Button6Label.Location = new System.Drawing.Point(664, 330);
            this.Button6Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button6Label.Name = "Button6Label";
            this.Button6Label.Size = new System.Drawing.Size(87, 20);
            this.Button6Label.TabIndex = 126;
            this.Button6Label.Text = "Parameter:";
            // 
            // Button6ParameterText
            // 
            this.Button6ParameterText.Location = new System.Drawing.Point(770, 326);
            this.Button6ParameterText.Name = "Button6ParameterText";
            this.Button6ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button6ParameterText.TabIndex = 125;
            // 
            // Button5ParameterText
            // 
            this.Button5ParameterText.Location = new System.Drawing.Point(770, 217);
            this.Button5ParameterText.Name = "Button5ParameterText";
            this.Button5ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button5ParameterText.TabIndex = 123;
            // 
            // Button4ParameterText
            // 
            this.Button4ParameterText.Location = new System.Drawing.Point(770, 121);
            this.Button4ParameterText.Name = "Button4ParameterText";
            this.Button4ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button4ParameterText.TabIndex = 121;
            // 
            // Button5Label
            // 
            this.Button5Label.AutoSize = true;
            this.Button5Label.Location = new System.Drawing.Point(664, 220);
            this.Button5Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button5Label.Name = "Button5Label";
            this.Button5Label.Size = new System.Drawing.Size(87, 20);
            this.Button5Label.TabIndex = 124;
            this.Button5Label.Text = "Parameter:";
            // 
            // Button4Label
            // 
            this.Button4Label.AutoSize = true;
            this.Button4Label.Location = new System.Drawing.Point(664, 121);
            this.Button4Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button4Label.Name = "Button4Label";
            this.Button4Label.Size = new System.Drawing.Size(87, 20);
            this.Button4Label.TabIndex = 122;
            this.Button4Label.Text = "Parameter:";
            this.Button4Label.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(683, 289);
            this.label9.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(70, 20);
            this.label9.TabIndex = 120;
            this.label9.Text = "Button6:";
            // 
            // Button6FunctionBox
            // 
            this.Button6FunctionBox.FormattingEnabled = true;
            this.Button6FunctionBox.Location = new System.Drawing.Point(770, 289);
            this.Button6FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button6FunctionBox.Name = "Button6FunctionBox";
            this.Button6FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button6FunctionBox.TabIndex = 119;
            this.Button6FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button6FunctionBox_SelectedIndexChanged_1);
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(683, 179);
            this.label10.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(70, 20);
            this.label10.TabIndex = 118;
            this.label10.Text = "Button5:";
            // 
            // Button5FunctionBox
            // 
            this.Button5FunctionBox.FormattingEnabled = true;
            this.Button5FunctionBox.Location = new System.Drawing.Point(770, 179);
            this.Button5FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button5FunctionBox.Name = "Button5FunctionBox";
            this.Button5FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button5FunctionBox.TabIndex = 117;
            this.Button5FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button5FunctionBox_SelectedIndexChanged_1);
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(683, 81);
            this.label11.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(70, 20);
            this.label11.TabIndex = 116;
            this.label11.Text = "Button4:";
            // 
            // Button4FunctionBox
            // 
            this.Button4FunctionBox.FormattingEnabled = true;
            this.Button4FunctionBox.Location = new System.Drawing.Point(770, 81);
            this.Button4FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button4FunctionBox.Name = "Button4FunctionBox";
            this.Button4FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button4FunctionBox.TabIndex = 115;
            this.Button4FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button4FunctionBox_SelectedIndexChanged_1);
            // 
            // Button3ComboBox
            // 
            this.Button3ComboBox.FormattingEnabled = true;
            this.Button3ComboBox.Location = new System.Drawing.Point(436, 321);
            this.Button3ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button3ComboBox.Name = "Button3ComboBox";
            this.Button3ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button3ComboBox.TabIndex = 114;
            this.Button3ComboBox.Visible = false;
            this.Button3ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button3ComboBox_SelectedIndexChanged);
            // 
            // Button2ComboBox
            // 
            this.Button2ComboBox.FormattingEnabled = true;
            this.Button2ComboBox.Location = new System.Drawing.Point(437, 214);
            this.Button2ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button2ComboBox.Name = "Button2ComboBox";
            this.Button2ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button2ComboBox.TabIndex = 113;
            this.Button2ComboBox.Visible = false;
            this.Button2ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button2ComboBox_SelectedIndexChanged);
            // 
            // Button1ComboBox
            // 
            this.Button1ComboBox.FormattingEnabled = true;
            this.Button1ComboBox.Location = new System.Drawing.Point(438, 117);
            this.Button1ComboBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button1ComboBox.Name = "Button1ComboBox";
            this.Button1ComboBox.Size = new System.Drawing.Size(109, 28);
            this.Button1ComboBox.TabIndex = 112;
            this.Button1ComboBox.Visible = false;
            this.Button1ComboBox.SelectedIndexChanged += new System.EventHandler(this.Button1ComboBox_SelectedIndexChanged);
            // 
            // Button3NumericParameter
            // 
            this.Button3NumericParameter.Location = new System.Drawing.Point(173, 325);
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
            this.Button2NumericParameter.Location = new System.Drawing.Point(173, 215);
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
            this.Button1NumericParameter.Location = new System.Drawing.Point(173, 116);
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
            this.Button3Label.Location = new System.Drawing.Point(67, 327);
            this.Button3Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button3Label.Name = "Button3Label";
            this.Button3Label.Size = new System.Drawing.Size(87, 20);
            this.Button3Label.TabIndex = 102;
            this.Button3Label.Text = "Parameter:";
            // 
            // Button3ParameterText
            // 
            this.Button3ParameterText.Location = new System.Drawing.Point(173, 323);
            this.Button3ParameterText.Name = "Button3ParameterText";
            this.Button3ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button3ParameterText.TabIndex = 101;
            // 
            // Button2ParameterText
            // 
            this.Button2ParameterText.Location = new System.Drawing.Point(173, 214);
            this.Button2ParameterText.Name = "Button2ParameterText";
            this.Button2ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button2ParameterText.TabIndex = 99;
            // 
            // Button1ParameterText
            // 
            this.Button1ParameterText.Location = new System.Drawing.Point(173, 118);
            this.Button1ParameterText.Name = "Button1ParameterText";
            this.Button1ParameterText.Size = new System.Drawing.Size(253, 26);
            this.Button1ParameterText.TabIndex = 97;
            // 
            // Button2Label
            // 
            this.Button2Label.AutoSize = true;
            this.Button2Label.Location = new System.Drawing.Point(67, 217);
            this.Button2Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button2Label.Name = "Button2Label";
            this.Button2Label.Size = new System.Drawing.Size(87, 20);
            this.Button2Label.TabIndex = 100;
            this.Button2Label.Text = "Parameter:";
            // 
            // Button1Label
            // 
            this.Button1Label.AutoSize = true;
            this.Button1Label.Location = new System.Drawing.Point(67, 118);
            this.Button1Label.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.Button1Label.Name = "Button1Label";
            this.Button1Label.Size = new System.Drawing.Size(87, 20);
            this.Button1Label.TabIndex = 98;
            this.Button1Label.Text = "Parameter:";
            this.Button1Label.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(86, 286);
            this.label8.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(70, 20);
            this.label8.TabIndex = 92;
            this.label8.Text = "Button3:";
            // 
            // Button3FunctionBox
            // 
            this.Button3FunctionBox.FormattingEnabled = true;
            this.Button3FunctionBox.Location = new System.Drawing.Point(173, 286);
            this.Button3FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button3FunctionBox.Name = "Button3FunctionBox";
            this.Button3FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button3FunctionBox.TabIndex = 91;
            this.Button3FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button3FunctionBox_SelectedIndexChanged_1);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(86, 176);
            this.label7.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(70, 20);
            this.label7.TabIndex = 90;
            this.label7.Text = "Button2:";
            // 
            // Button2FunctionBox
            // 
            this.Button2FunctionBox.FormattingEnabled = true;
            this.Button2FunctionBox.Location = new System.Drawing.Point(173, 176);
            this.Button2FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button2FunctionBox.Name = "Button2FunctionBox";
            this.Button2FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button2FunctionBox.TabIndex = 89;
            this.Button2FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button2FunctionBox_SelectedIndexChanged_1);
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(86, 78);
            this.label6.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(70, 20);
            this.label6.TabIndex = 88;
            this.label6.Text = "Button1:";
            // 
            // Button1FunctionBox
            // 
            this.Button1FunctionBox.FormattingEnabled = true;
            this.Button1FunctionBox.Location = new System.Drawing.Point(173, 78);
            this.Button1FunctionBox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.Button1FunctionBox.Name = "Button1FunctionBox";
            this.Button1FunctionBox.Size = new System.Drawing.Size(253, 28);
            this.Button1FunctionBox.TabIndex = 87;
            this.Button1FunctionBox.SelectedIndexChanged += new System.EventHandler(this.Button1FunctionBox_SelectedIndexChanged_1);
            // 
            // tabControl
            // 
            this.tabControl.Controls.Add(this.ButtonsTab);
            this.tabControl.Location = new System.Drawing.Point(56, 133);
            this.tabControl.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.tabControl.Name = "tabControl";
            this.tabControl.SelectedIndex = 0;
            this.tabControl.Size = new System.Drawing.Size(1228, 460);
            this.tabControl.TabIndex = 61;
            // 
            // activityLogTextbox
            // 
            this.activityLogTextbox.ForeColor = System.Drawing.SystemColors.MenuText;
            this.activityLogTextbox.HideSelection = false;
            this.activityLogTextbox.Location = new System.Drawing.Point(717, 63);
            this.activityLogTextbox.Margin = new System.Windows.Forms.Padding(4, 5, 4, 5);
            this.activityLogTextbox.Name = "activityLogTextbox";
            this.activityLogTextbox.ReadOnly = true;
            this.activityLogTextbox.Size = new System.Drawing.Size(563, 68);
            this.activityLogTextbox.TabIndex = 75;
            this.activityLogTextbox.Text = "";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(713, 32);
            this.label2.Margin = new System.Windows.Forms.Padding(4, 0, 4, 0);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(93, 20);
            this.label2.TabIndex = 76;
            this.label2.Text = "Activity Log:";
            // 
            // FabiGUI
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.AutoSize = true;
            this.ClientSize = new System.Drawing.Size(1336, 694);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.activityLogTextbox);
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
            this.Name = "FabiGUI";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Fabi Settings Manager";
            this.Load += new System.EventHandler(this.LipmouseGUI_Load);
            this.ButtonsTab.ResumeLayout(false);
            this.ButtonsTab.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.Button6NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button5NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button4NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button3NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button2NumericParameter)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.Button1NumericParameter)).EndInit();
            this.tabControl.ResumeLayout(false);
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
        private System.Windows.Forms.TextBox slotName;
        private System.Windows.Forms.TabPage ButtonsTab;
        private System.Windows.Forms.ComboBox Button3ComboBox;
        private System.Windows.Forms.ComboBox Button2ComboBox;
        private System.Windows.Forms.ComboBox Button1ComboBox;
        private System.Windows.Forms.NumericUpDown Button3NumericParameter;
        private System.Windows.Forms.NumericUpDown Button2NumericParameter;
        private System.Windows.Forms.NumericUpDown Button1NumericParameter;
        private System.Windows.Forms.Label Button3Label;
        private System.Windows.Forms.TextBox Button3ParameterText;
        private System.Windows.Forms.TextBox Button2ParameterText;
        private System.Windows.Forms.TextBox Button1ParameterText;
        private System.Windows.Forms.Label Button2Label;
        private System.Windows.Forms.Label Button1Label;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.ComboBox Button3FunctionBox;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.ComboBox Button2FunctionBox;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.ComboBox Button1FunctionBox;
        private System.Windows.Forms.TabControl tabControl;
        private System.Windows.Forms.ComboBox Button6ComboBox;
        private System.Windows.Forms.ComboBox Button5ComboBox;
        private System.Windows.Forms.ComboBox Button4ComboBox;
        private System.Windows.Forms.NumericUpDown Button6NumericParameter;
        private System.Windows.Forms.NumericUpDown Button5NumericParameter;
        private System.Windows.Forms.NumericUpDown Button4NumericParameter;
        private System.Windows.Forms.Label Button6Label;
        private System.Windows.Forms.TextBox Button6ParameterText;
        private System.Windows.Forms.TextBox Button5ParameterText;
        private System.Windows.Forms.TextBox Button4ParameterText;
        private System.Windows.Forms.Label Button5Label;
        private System.Windows.Forms.Label Button4Label;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.ComboBox Button6FunctionBox;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.ComboBox Button5FunctionBox;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.ComboBox Button4FunctionBox;
        private System.Windows.Forms.RichTextBox activityLogTextbox;
        private System.Windows.Forms.Label label2;
    }
}

