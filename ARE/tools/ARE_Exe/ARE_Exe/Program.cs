using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.IO;

namespace ARE_Exe
{
    class Program
    {
        static void ExecuteCommand(string command, string args)
        {
            ProcessStartInfo processInfo;
            Process process;
            
            processInfo = new ProcessStartInfo(command);
            processInfo.CreateNoWindow = true;
            processInfo.UseShellExecute = true;
            processInfo.CreateNoWindow = false;
            processInfo.WorkingDirectory = Path.GetDirectoryName(System.Reflection.Assembly.GetEntryAssembly().Location);
            processInfo.WindowStyle = ProcessWindowStyle.Hidden;
            processInfo.Arguments = args;
            process = Process.Start(processInfo);
            /*process.WaitForExit();
            process.Close();*/
        }

        static string StringArrayToString(string[] array)
        {
            StringBuilder builder = new StringBuilder();
            foreach (string value in array)
            {
                builder.Append(value);
                builder.Append(' ');
            }
            return builder.ToString();
        }

        static void Main(string[] args)
        {
            try
            {
                ExecuteCommand("start.bat", StringArrayToString(args));
            }
            catch (Exception e)
            {
            }
        }
    }
}
