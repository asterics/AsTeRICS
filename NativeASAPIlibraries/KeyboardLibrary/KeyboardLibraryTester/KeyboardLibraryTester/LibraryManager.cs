using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace KeyboardLibraryTester
{

    public enum HookMessage
    {
        HM_None = 0,
        HM_KEYDOWN = 1,
        HM_KEYUP,
        HM_SYSKEYDOWN,
        HM_SYSKEYUP
    };

    public enum BlockOptions
    {
        BO_BlockAll = 1,
        BO_PassSentFromLibrary = 2,
        BO_PassAll = 3
    };

    class LibraryManager
    {
        
        [Flags]
        public enum SendKeyFlags
        {
            SKF_KeyDown = 1,
            SKF_KeyUP = 2,
            SKF_KeyPress = 3,
            SKF_KeyExtended = 4,
        };

        [Flags]
        public enum HookFlags
        {
            HF_None = 0,
            HF_ExtendedKey = 1,
            HF_InjectedKey = 2,
            HF_AltKeyPressed = 4,
            HF_KeyPress = 8,
            HF_SentFromLibrary =0x10
        };

        public static Form1 form;

        [UnmanagedFunctionPointer(CallingConvention.StdCall)]
        public delegate int HookCallBack(int scanCode, int virtualCode, HookMessage message, HookFlags flags, IntPtr param);

        public static HookCallBack HookCallBackDelegate=new HookCallBack(hook);

        public static int init()
        {
            return (int)init(HookCallBackDelegate, (IntPtr)0);
        }

        public static int hookReturnValue=0;

        public static int hook(int scanCode, int virtualCode, HookMessage message, HookFlags flags, IntPtr param)
        {
            KeyEvent keyEvent = new KeyEvent();
            keyEvent.scanCode = scanCode;
            keyEvent.virtualCode = virtualCode;
            keyEvent.hookMessage = message;

            int nFlags = (int)flags;

            if ((nFlags & (int)HookFlags.HF_ExtendedKey) > 0)
            {
                keyEvent.extendedKey = true;
            }

            if ((nFlags & (int)HookFlags.HF_AltKeyPressed) > 0)
            {
                keyEvent.altKeyPressed = true;
            }

            if ((nFlags & (int)HookFlags.HF_InjectedKey) > 0)
            {
                keyEvent.injectedKey = true;
            }

            if ((nFlags & (int)HookFlags.HF_KeyPress) > 0)
            {
                keyEvent.keyPress = true;
            }

            if ((nFlags & (int)HookFlags.HF_SentFromLibrary) > 0)
            {
                keyEvent.sentFromLibrary = true;
            }


            form.AddEvent(keyEvent);

            return hookReturnValue;
        }

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 init(HookCallBack hookCallBack, IntPtr param);

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 close();

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 startHook();
        
        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 stopHook();

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 sendKeyByScanCode(int scanCode, SendKeyFlags flags);

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 sendKeyByVirtualCode(int virtualCode, SendKeyFlags flags);

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 sendText(string text);

        [DllImport("KeyboardLibrary.dll", CharSet = CharSet.Unicode)]
        public static extern Int32 blockKeys(BlockOptions blockOptions);
    }
}
