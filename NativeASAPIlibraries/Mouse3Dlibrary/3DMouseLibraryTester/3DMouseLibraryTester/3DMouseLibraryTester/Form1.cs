using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;


namespace _3DMouseLibraryTester
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }



        [DllImport("Mouse3Dlibrary.dll")]
        private static extern Int32 init();

        [DllImport("Mouse3Dlibrary.dll")]
        private static extern Int32 close();

        [DllImport("Mouse3Dlibrary.dll")]
        private static extern Int32 get3DMouseState(ref int x, ref int y, ref int z, ref int Rx, ref int Ry, ref int Rz, ref int buttons);

        private void button1_Click(object sender, EventArgs e)
        {
            int res = init();

            textBox8.Text = res.ToString();
            
            if (res > 0)
            {
                groupBox1.Enabled = true;
                timer1.Interval = 250;
                timer1.Enabled = true;
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            int res = close();

            textBox9.Text = res.ToString();

            if (res > 0)
            {
                groupBox1.Enabled = false;
                timer1.Enabled = false;
            }
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            int x = 0;
            int y=0;
            int z=0;
            int Rx=0;
            int Ry=0;
            int Rz=0;
            int buttons=0;

            int res = get3DMouseState(ref x,ref  y,ref z,ref Rx,ref Ry,ref Rz,ref buttons);

            textBox10.Text = res.ToString();

            if (res > 0)
            {
                textBox1.Text = x.ToString();
                textBox2.Text = y.ToString();
                textBox3.Text = z.ToString();

                textBox4.Text = Rx.ToString();
                textBox5.Text = Ry.ToString();
                textBox6.Text = Rz.ToString();

                textBox7.Text = buttons.ToString("X");
            }
        }
    }
}
