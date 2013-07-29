using System;
using System.Collections.Generic;
using System.Text;

namespace WPG
{
	[AttributeUsage(AttributeTargets.Property, Inherited = false, AllowMultiple = false)]
	public sealed class FlatAttribute : Attribute { }
}
