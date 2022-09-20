using System;
using System.Collections.Generic;
using System.Text;

namespace PhoneLibraryTester
{
    class BluetootDevice
    {
        public string name;
        public UInt64 address;
        public override string ToString()
        {
            String s = name + @" (" + address.ToString("x") + @")";
            return s;
        }
    }

}
