/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 * --------------------------------------------------------------------------------
 * AsTeRICS – EC Grant Agreement No. 247730
 * Assistive Technology Rapid Integration and Construction Set
 * --------------------------------------------------------------------------------
 * Filename: AstericsStack.cs
 * Class(es):
 *   Classname: AstericsStack
 *   Description: Extension of Stack<T>, to have PropertyChangedEvent capabilities
 * Author: Roland Ossmann
 * Date: 01.02.2011
 * Version: 0.3
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System.Collections.Generic;
using System.ComponentModel;

namespace Asterics.ACS {
    
    /// <summary>
    /// Extension of Stack<T>, to have PropertyChangedEvent capabilities
    /// </summary>
    /// <typeparam name="T">Type of the Stack</typeparam>
    class AstericsStack<T> : Stack<T>, INotifyPropertyChanged {

        internal virtual void OnPropertyChanged(string propertyName) {
            if (PropertyChanged != null) {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        public AstericsStack() : base () {

        }

        public new void Push(T item) {
            base.Push(item);
            OnPropertyChanged("Push");
        }


        public new T Pop() {
            OnPropertyChanged("Pop");
            return base.Pop();
        }

        public new void Clear() {
            base.Clear();
            OnPropertyChanged("Clear");
        }
    }
}
