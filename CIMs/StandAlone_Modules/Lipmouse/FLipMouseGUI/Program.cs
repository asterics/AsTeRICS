using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Threading;
using System.Windows.Forms;



namespace MouseApp2
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        private static string appGuid = "c0a76b5a-12ab-45c5-b9d9-d693faa6e7b9";

        [STAThread]
        static void Main()
        {


            using (Mutex mutex = new Mutex(false, "Global\\" + appGuid))
            {
                if (!mutex.WaitOne(0, false))
                {
                    MessageBox.Show("FlipMouseGUI is already running !");
                    return;
                }

                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);
                Application.Run(new FLipMouseGUI());
            }
        }
    }
}
