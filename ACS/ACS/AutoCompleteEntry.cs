using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Asterics.ACS
{
    public class AutoCompleteEntry
    {
        private string[] keywordStrings;
        private string displayString;
        private string toolTop="";
        private string key;

        public string[] KeywordStrings
        {
            get
            {
                if (keywordStrings == null)
                {
                    keywordStrings = new string[] { displayString };
                }
                return keywordStrings;
            }
            set
            {
                keywordStrings = value;
            }
        }

        public string DisplayName
        {
            get { return displayString; }
            set { displayString = value; }
        }

        public string Key
        {
            get { return key; }
            set { key = value; }
        }

        public string ToolTip
        {
            get { return toolTop; }
            set { toolTop = value; }
        }

        public AutoCompleteEntry(string name,string keyString, params string[] keywords)
        {
            displayString = name;
            key = keyString;
            keywordStrings = keywords;
        }

        public override string ToString()
        {
            return displayString;
        }
    }
}
