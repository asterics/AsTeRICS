using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AsynchronousRecognition
{
    class StartSpeechServer
    {
        static void Main(string[] args)
        {
            try
            {
                Recog.speechPlatformMain();
                Environment.Exit(0);
            }
            catch (Exception e) { Console.WriteLine("\nException:" + e.Message); Environment.Exit(1); }
        }
    }

    
}
