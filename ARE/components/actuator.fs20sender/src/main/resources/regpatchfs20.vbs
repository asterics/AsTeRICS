args=""
for(i = 0; i < WScript.Arguments.length; i++) {
  args = args + " \"" + WScript.Arguments(i) + "\"";
}
ShA=new ActiveXObject("Shell.Application");
baseDir=WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
WScript.Echo ("Path to current script =", WScript.ScriptFullName);
cmdString="/c \"\"" + baseDir + "regpatchfs20.cmd\"" + args + "\"",""
WScript.Echo ("execute command = ", cmdString);
ShA.ShellExecute("cmd.exe",cmdString,"","runas",5);