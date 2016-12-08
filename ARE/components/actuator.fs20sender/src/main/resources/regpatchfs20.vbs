args=""
for(i = 0; i < WScript.Arguments.length; i++) {
  args = args + " \"" + WScript.Arguments(i) + "\"";
}
ShA=new ActiveXObject("Shell.Application");
baseDir=WScript.ScriptFullName.substring(0, WScript.ScriptFullName.length-WScript.ScriptName.length);
ShA.ShellExecute("cmd.exe","/c \"" + baseDir + "regpatchfs20.cmd" + args + "\"","","runas",5);