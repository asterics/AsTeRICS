using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace KeyboardLibraryTester
{
    public class KeyEvent
    {
        public int scanCode;
        public int virtualCode;
        
        public HookMessage hookMessage;
        
        public bool extendedKey;
        public bool injectedKey;
        public bool altKeyPressed;
        public bool keyPress;
        public bool sentFromLibrary;

        public KeyEvent()
        {
            scanCode = 0;
            virtualCode=0;
            hookMessage=HookMessage.HM_None;
            extendedKey=false;
            injectedKey = false;
            altKeyPressed = false;
            keyPress = false;
            sentFromLibrary = false;

        }

        public override string ToString()
        {
            string s = "";

            s = @"sc: " + scanCode.ToString("D3");
            s = s + @" vk: " + virtualCode.ToString("D3");

            switch (hookMessage)
            {
                case HookMessage.HM_None:
                    s = s + " mes.: None   ";
                    break;
                case HookMessage.HM_KEYDOWN:
                    s = s + " mes.: Down";
                    break;
                case HookMessage.HM_KEYUP:
                    s = s + " mes.: Up     ";
                    break;
                case HookMessage.HM_SYSKEYDOWN:
                    s = s + " mes.: sysDown";
                    break;
                case HookMessage.HM_SYSKEYUP:
                    s = s + " mes.: sysUP  ";
                    break;
            }


            if (extendedKey)
            {
                s = s + @" Ext: T";
            }
            else
            {
                s = s + @" Ext: F";
            }

            if (injectedKey)
            {
                s = s + @" Inj: T";
            }
            else
            {
                s = s + @" Inj: F";
            }

            if (altKeyPressed)
            {
                s = s + @" Alt: T";
            }
            else
            {
                s = s + @" Alt: F";
            }


            if (keyPress)
            {
                s = s + @" Ps: T";
            }
            else
            {
                s = s + @" Ps: F";
            }

            if (sentFromLibrary)
            {
                s = s + @" SFL: T";
            }
            else
            {
                s = s + @" SFL: F";
            }

            return s;
        }
    }
}
