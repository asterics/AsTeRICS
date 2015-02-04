
using System;
using System.Threading;
using System.Net;
using System.Net.Sockets;

using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Linq;
using System.Text;

using System.Media;
using System.Globalization;

//using System.Speech.Recognition;
//using System.Speech.Synthesis;
 using Microsoft.Speech.Recognition;
 using Microsoft.Speech.Synthesis;


namespace AsynchronousRecognition
{


    public class Recog
    {

        static IAsyncResult m_asynResult;
        static public AsyncCallback pfnCallBack;
        static public Socket m_socClient=null;

        static String strCulture = "xx";
        static Double confidenceLevel = 0.7;
        static RecognizerInfo recogInfo = null;
        static SpeechRecognitionEngine recognizer = null;

        static SpeechSynthesizer tts = null;
        static VoiceInfo voiceInfo = null;
 //       static SoundPlayer player = new SoundPlayer();

        static long speechTimeStamp=0;
        static Int32 speechLoopDelay = 1500;
        static String spokenText = "";



        // Indicate whether asynchronous recognition is complete.
        static bool completed;
        static bool speaking=false;
        
        public static void speechPlatformMain() 
        {

            Console.WriteLine("\nSpeech Processor: initializing!");

            try
            {
                Console.WriteLine("Speech Processor: Trying to connect to Server at 127.0.0.1:8221");

				m_socClient = new Socket (AddressFamily.InterNetwork,SocketType.Stream ,ProtocolType.Tcp );

				// get the remote IP address...

                //IPAddress ip = IPAddress.Parse (txtIPAddr.Text);
                IPAddress ip = IPAddress.Parse("127.0.0.1");
                int iPortNo = System.Convert.ToInt16("8221");
				//create the end point 
				IPEndPoint ipEnd = new IPEndPoint (ip.Address,iPortNo);

                //connect to the remote host...
				m_socClient.Connect ( ipEnd );

                Console.WriteLine("Speech Processor: Server connection successful.");

                /*
                    CreateSynthesizer();
                    CreateRecognizer();

                    if (recognizer != null)
                    {
                        Console.WriteLine("Starting asynchronous recognition ...");
                        UpdateGrammar(new string[] { "start speech", "stop speech" });
                        recognizer.RecognizeAsync(RecognizeMode.Multiple);
                    }
                 */

               
                completed = false;
                //watch for data ( asynchronously )...
                WaitForData();

                // Wait for close request via Socket 
                while (!completed)
                {
                    Thread.Sleep(333);
                }


                if (recognizer != null)
                    recognizer.RecognizeAsyncCancel();

                /*
                completed = false;


                // Wait for close command.
                while (!completed)
                {
                    Thread.Sleep(333);
                }

                Console.WriteLine("SECOND COMPLETE !!!");
                */

                Console.WriteLine("Speech Processor: Shutting down ...");
                
                if (m_socClient != null)
                {
                    Console.WriteLine("Speech Processor: Closing Socket..");

                    m_socClient.Shutdown( SocketShutdown.Send);
                    m_socClient.Close();
                    m_socClient = null;
                }

			}
			catch(SocketException se)
			{
                Console.WriteLine(se.Message);
                
			}

            //Console.WriteLine("Press any key to exit...");
            //Console.ReadKey();

        }

        static void SocketSend(String text)
        {

            try
            {
                byte[] byData = System.Text.Encoding.ASCII.GetBytes(text);
                if (m_socClient !=null) m_socClient.Send(byData);

            }
            catch (SocketException se)
            {
                Console.WriteLine(se.Message);
            }
        }

        static void WaitForData()
        {
            try
            {
                if (pfnCallBack == null)
                {
                    pfnCallBack = new AsyncCallback(OnDataReceived);
                }
                CSocketPacket theSocPkt = new CSocketPacket();
                theSocPkt.thisSocket = m_socClient;
                // now start to listen for any data...
                m_asynResult = m_socClient.BeginReceive(theSocPkt.dataBuffer, 0, theSocPkt.dataBuffer.Length, SocketFlags.None, pfnCallBack, theSocPkt);
            }
            catch (SocketException se)
            {
                Console.WriteLine(se.Message);
            }

        }


        public class CSocketPacket
        {
            public System.Net.Sockets.Socket thisSocket;
            public byte[] dataBuffer = new byte[1000];
        }


        static void OnDataReceived(IAsyncResult asyn)
        {
            try
            {
                CSocketPacket theSockId = (CSocketPacket)asyn.AsyncState;
                //end receive...
                int iRx = 0;
                iRx = theSockId.thisSocket.EndReceive(asyn);
                char[] chars = new char[iRx];
                System.Text.Decoder d = System.Text.Encoding.Default.GetDecoder();
                int charLen = d.GetChars(theSockId.dataBuffer, 0, iRx, chars, 0);
                System.String message = ((new System.String(chars)).Trim()); //.ToLower();


                if (iRx > 0)
                {
                    //System.String message = enc.GetString(theSockId.dataBuffer);
                    //System.String message = new System.String(theSockId.dataBuffer, 0, iRx);

                    // Console.WriteLine("Received and trimmend:" + message);

                    string[] tokenList = message.Split('#');

                    foreach (string token in tokenList)
                    {
                        // Console.WriteLine("Speech Processor: act token =" + token);

                        if (String.Compare(token, "@close@") == 0)
                        {
                            Console.WriteLine("Speech Processor: received CLOSE");
                            completed = true;
                        }
                        else if (String.Compare(token, "@stop@") == 0)
                        {
                            Console.WriteLine("Speech Processor: received STOP, cleaning up Recognizer !");
                            recognizer.UnloadAllGrammars();
                            recognizer.RequestRecognizerUpdate();
                            recognizer.Dispose();

                            recognizer = null;
                            tts = null;
                        }
                        else if (token.StartsWith("say:"))
                        {
                            Console.WriteLine("Speech Processor: saying:" + token.Substring(4, token.Length - 4));
                            Speak(token.Substring(4, token.Length - 4));
                        }
                        else if (token.StartsWith("culture:"))
                        {
                            string newCulture = (token.Substring(8, token.Length - 8));
                            Console.WriteLine("Speech Processor: Initialising Engines for new culture:" + newCulture);
                            strCulture = newCulture;
                            CreateSynthesizer();
                            CreateRecognizer();

                        }
                        else if (token.StartsWith("ttsonly:"))
                        {
                            string newCulture = (token.Substring(8, token.Length - 8));
                            Console.WriteLine("Speech Processor: Initialising TTS-Engine for new culture:" + newCulture);
                            strCulture = newCulture;
                            CreateSynthesizer();
                        }
                        else if (token.StartsWith("grammar:"))
                        {
                            string[] words = (token.Substring(8, token.Length - 8)).Split(';');

                            if (recognizer != null)
                            {
                                UpdateGrammar(words);

                                Console.WriteLine("Speech Processor: Starting asynchronous recognition ...");
                                try
                                {
                                    recognizer.RecognizeAsync(RecognizeMode.Multiple);
                                    SocketSend("@SpeechProcessor OK@");
                                }
                                catch (Exception ex)
                                { Console.WriteLine("Speech Processor: could not start asynchronous recognition ..."); }

                            }
                            else Console.WriteLine("Speech Processor: could not start asynchronous recognition due to missing recognizer.");
                        }
                        else if (token.StartsWith("confidence:"))
                        {
                            confidenceLevel = double.Parse(token.Substring(11, token.Length - 11), System.Globalization.CultureInfo.InvariantCulture);
                            //Console.WriteLine("confidence update:" + token.Substring(11, token.Length - 11));
                            Console.WriteLine("Speech Processor: confidence update:" + confidenceLevel);

                        }
                        else if (token.StartsWith("speechLoopDelay:"))
                        {
                            speechLoopDelay = int.Parse(token.Substring(16, token.Length - 16), System.Globalization.CultureInfo.InvariantCulture);
                        }
                    }
                }
                else { Console.WriteLine("Speech Processor: received empty message, closing down socket !"); completed = true; }
 
                if (!completed)  WaitForData();
            }
            catch (ObjectDisposedException)
            {
                Console.WriteLine("Speech Processor: OnDataReceived: Socket has been closed");
            }
            catch (SocketException se)
            {
                Console.WriteLine(se.Message);
            }
        }



        static bool CreateSynthesizer()
        {

            if (tts == null)
            {
                try
                {
                    Console.WriteLine("\nSpeech Processor: creating Speech Synthesizer");
                    tts = new SpeechSynthesizer();
                    Console.WriteLine("\nSpeech Processor: created");
                    tts.SetOutputToDefaultAudioDevice();
                    tts.SpeakCompleted += new EventHandler<SpeakCompletedEventArgs>(tts_SpeakCompleted);
                }
                catch (Exception e) { Console.WriteLine("Speech Processor: Exception at Speech Synthesizer creation: \n" + e.Message+"\n please check your Audio playback device !" ); }

            }

            voiceInfo = null;
            speaking = false;

            Console.WriteLine("\nThe following Voices are available:");
            foreach (InstalledVoice voice in tts.GetInstalledVoices())
            {
                // Prepare a listview row to add
                Console.Write("  "+voice.VoiceInfo.Name);
                Console.WriteLine(voice.VoiceInfo.Culture.ToString());

                if (voice.VoiceInfo.Culture.Name.Equals(strCulture))
                {
                    voiceInfo = voice.VoiceInfo;
                }
            }
            if (voiceInfo != null)
            {
                Console.WriteLine("\nUsing voice " + voiceInfo.Name + " for culture " + strCulture);

                tts.SpeakAsyncCancelAll();
                tts.SelectVoice(voiceInfo.Name);

                Speak("Okay.");                
                return (true);
            }
            Console.WriteLine("\nSpeech Processor: could not find a voice synthesizer for culture ..." + strCulture);
            return (false);
        }

        static void tts_SpeakCompleted(object sender, SpeakCompletedEventArgs e)
        {
            // Console.Write("\nSpeak completed.\n");
            //  if (recognizer != null) recognizer.SetInputToDefaultAudioDevice();
            // if (recognizer != null)  recognizer.RecognizeAsync(RecognizeMode.Multiple);
            // player.Stream.Position = 0;
            // player.Play();
            // Console.Write("\nBypass reco at "+speechTimeStamp);
            speechTimeStamp = DateTime.Now.Ticks;
            speaking = false;

        }


        static public void Speak(string text)
        {
            if (tts!=null)
            {
                // Console.Write("\nStarting speak:"+text);
                // player.Stream = new System.IO.MemoryStream();
                // tts.SetOutputToWaveStream(player.Stream);
                // if (recognizer != null) recognizer.SetInputToNull();
                spokenText = text;
                speaking = true;
                tts.SpeakAsync(text);
            }
            else Console.Write("\nSpeech Processor: could not speak because no synthesizer exists ...");

        }

        static bool CreateRecognizer()
        {

            // Was the SRE already defined ?
            if (recognizer != null)
            {
                // Yes
                recognizer = null;
            }

            try
            {

                recogInfo = null;
                Console.WriteLine("\nSpeech Processor: The following Speech Recognizers are available:");

                foreach (RecognizerInfo ri in SpeechRecognitionEngine.InstalledRecognizers())
                {
                    Console.Write("  "); Console.WriteLine(ri.Description);
                    if (ri.Culture.Name.Equals(strCulture))
                    {
                        recogInfo = ri;
                    }
                }

                if (recogInfo != null)
                {
                    recognizer = new SpeechRecognitionEngine(recogInfo);
                    Console.WriteLine("\nUsing recognizer " + recognizer.RecognizerInfo.Name + " for culture " + strCulture);
                        
                    // Attach event handlers.
                    recognizer.SpeechDetected +=
                        new EventHandler<SpeechDetectedEventArgs>(
                        SpeechDetectedHandler);
                    /*    recognizer.SpeechHypothesized +=
                            new EventHandler<SpeechHypothesizedEventArgs>(SpeechHypothesizedHandler);
                          recognizer.SpeechRecognitionRejected +=
                            new EventHandler<SpeechRecognitionRejectedEventArgs>(SpeechRecognitionRejectedHandler);
                    */
                    recognizer.SpeechRecognized +=
                        new EventHandler<SpeechRecognizedEventArgs>(
                        SpeechRecognizedHandler);
                    recognizer.RecognizeCompleted +=
                        new EventHandler<RecognizeCompletedEventArgs>(
                        RecognizeCompletedHandler);
                    recognizer.RecognizerUpdateReached +=
                        new EventHandler<RecognizerUpdateReachedEventArgs>(RecognizerUpdateReachedHandler);

                    // Assign input to the recognizer and start asynchronous
                    // recognition.
                    recognizer.SetInputToDefaultAudioDevice();

                    return (true);
                }
                else
                {
                    Console.WriteLine("\nSpeech Processor: could not find a recognizer for culture ..." + strCulture);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("\nSpeech Processor: unable to create Speech Recognition Engine");
            }

            return (false);

        }


        static bool UpdateGrammar(string[] words)
        {
            if (recognizer != null)
            {
                Choices actchoices = new Choices(words);
                GrammarBuilder gb = new GrammarBuilder();
                try
                {
                    gb.Culture = new System.Globalization.CultureInfo(strCulture);
                    gb.Append(actchoices);

                    Grammar newGrammar = new Grammar(gb);
                    newGrammar.Name = "AsTeRICS Dictionary";

                    Console.Write("\nReceived Grammar Update (" + strCulture + "): ");
                    foreach (string s in words)
                    {
                        Console.Write(s + " ");
                    }
                    Console.WriteLine();

                    recognizer.LoadGrammar(newGrammar);
                    return (true);
                }
                catch (Exception ex)
                { Console.Write("\nSpeech Processor: Exception: Could create grammar."); }
            }
            else Console.Write("\nSpeech Processor: could not update grammar because no recognizer exists ...");
            return (false);

            //Thread.Sleep(5000);
        }

        // Handle the SpeechDetected event.
        static void SpeechDetectedHandler(object sender, SpeechDetectedEventArgs e)
        {
            Console.WriteLine("  \nSpeech Processor: speech detected at AudioPosition {0}", e.AudioPosition);
        }

        // Handle the SpeechHypothesized event.
        static void SpeechHypothesizedHandler(
          object sender, SpeechHypothesizedEventArgs e)
        {
            Console.WriteLine("Speech Processor: In SpeechHypothesizedHandler:");

            string grammarName = "<not available>";
            string resultText = "<not available>";
            if (e.Result != null)
            {
                if (e.Result.Grammar != null)
                {
                    grammarName = e.Result.Grammar.Name;
                }
                resultText = e.Result.Text;
            }

            Console.WriteLine(" - Grammar Name = {0}; Result Text = {1}",
              grammarName, resultText);
        }

        // Handle the SpeechRecognitionRejected event.
        static void SpeechRecognitionRejectedHandler(
          object sender, SpeechRecognitionRejectedEventArgs e)
        {
            Console.WriteLine("Speech Processor: In SpeechRecognitionRejectedHandler:");

            string grammarName = "<not available>";
            string resultText = "<not available>";
            if (e.Result != null)
            {
                if (e.Result.Grammar != null)
                {
                    grammarName = e.Result.Grammar.Name;
                }
                resultText = e.Result.Text;
            }

            Console.WriteLine(" - Grammar Name = {0}; Result Text = {1}",
              grammarName, resultText);
        }

        // Handle the SpeechRecognized event.
        static void SpeechRecognizedHandler(
          object sender, SpeechRecognizedEventArgs e)
        {
            // Console.WriteLine(" In SpeechRecognizedHandler.");

            string grammarName = "<not available>";
            string resultText = "<not available>";
            if (e.Result != null)
            {
                if (e.Result.Grammar != null)
                {
                    grammarName = e.Result.Grammar.Name;
                }
                resultText = e.Result.Text;
            }

            Console.Write("Speech Processor:  Recognized {0} with Confidence {1}, ",
               resultText, e.Result.Confidence);

            if (e.Result.Confidence > confidenceLevel)
            {

                //if ((resultText == spokenText)  &&
                if ((speaking==true) || ((DateTime.Now.Ticks - speechTimeStamp) / 10000 < speechLoopDelay))
                    Console.WriteLine(" bypassed recognition to avoid loop.");
                else
                {
                        Console.WriteLine(" sending to host application.");
                        SocketSend(resultText);
                }
                speechTimeStamp = 0;
                
            }
            else Console.WriteLine(" supressing because confidence was too low.");

        }

        // Handle the RecognizeCompleted event.
        static void RecognizeCompletedHandler(
          object sender, RecognizeCompletedEventArgs e)
        {
            Console.WriteLine("Speech Processor: In RecognizeCompletedHandler.");

            if (e.Error != null)
            {
                Console.WriteLine(
                  " - Error occurred during recognition: {0}", e.Error);
                return;
            }
            if (e.InitialSilenceTimeout || e.BabbleTimeout)
            {
                Console.WriteLine(
                  " - BabbleTimeout = {0}; InitialSilenceTimeout = {1}",
                  e.BabbleTimeout, e.InitialSilenceTimeout);
                return;
            }
            if (e.InputStreamEnded)
            {
                Console.WriteLine(
                  " - AudioPosition = {0}; InputStreamEnded = {1}",
                  e.AudioPosition, e.InputStreamEnded);
            }
            if (e.Result != null)
            {
                Console.WriteLine(
                  " - Grammar = {0}; Text = {1}; Confidence = {2}",
                  e.Result.Grammar.Name, e.Result.Text, e.Result.Confidence);
                Console.WriteLine(" - AudioPosition = {0}", e.AudioPosition);
            }
            else
            {
                Console.WriteLine(" - No result.");
            }

            completed = true;
        }


        // At the update, get the names and enabled status of the currently loaded grammars.
        public static void RecognizerUpdateReachedHandler(
          object sender, RecognizerUpdateReachedEventArgs e)
        {
            //Thread.Sleep(1000);

            Console.WriteLine("Speech Processor: Now listing grammars ....");

            string qualifier;
            List<Grammar> grammars = new List<Grammar>(recognizer.Grammars);
            foreach (Grammar g in grammars)
            {
                qualifier = (g.Enabled) ? "enabled" : "disabled";
                Console.WriteLine("Grammar {0} is loaded and is {1}.",
                g.Name, qualifier);
            }
        }

    }
}

