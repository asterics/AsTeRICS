# Installer build instructions (Windows)

## Prerequisites

* Build requirements for AsTeRICS, see [README of root page](/README.md)
* [InnoSetup tool, iscc.exe](http://www.jrsoftware.org/isinfo.php)
* [git cmdline tool](http://git-scm.com/):
  * (git.exe, bash.exe download from http://git-scm.com/, add e.g. **"C:\Program Files\Git\bin"** to **PATH**)
    NOTE: Windows 10 seems to have an built in **"bash"** command, so the git bash has to be renamed in the
		**ProgramFiles/bin** folder to another name, e.g. **git-bash.exe**

## Windows

To build a full installer for Windwos containing all components (ACS, ARE, WebACS, APE) please invoke the following script:

```cmd
cd ..
bash -ex ./Installer/jenkins-release-script.sh
```
