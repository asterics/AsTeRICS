# ----
# The original author is  Eugene Melekhov <eugene_melekhov@mail.ru>
#  Object Tools http://www.object-tools.com
#  Contributed to rxtx Wed Sep 8 2004
#  Reportedly builds rxtxSerial.dll but rxtxParallel.dll is untested.
#  Accepted as is by taj@www.linux.org.uk
# ---
# This is the first quick and durty attempt to compile rxtx for Windows 
# using Microsoft Visual C compiler. I've done this mostly to debug rxtx 
# with Microsoft debugger
#
# This makefile was made for MSVC 6.0. I'm afraid that debug info command
# line switches like /Z7 -debugtype:CV -pdb:NONE won't work with 
# MSVC 7.0 or above.
#
#Update on 2017-07-19, mad
#Added setting of dependencies and environment manually because vcvarsall.bat did work for me. Please ensure to define the paths of
# MS Visual Studio (tested with 10.0)
# MS SDK
# JDK
# Ensure to add executables like CL.exe and LINK.exe to the environment variable PATH, also add the path %VS10%\Common7\IDE to the PATH variable

# The serial port library seems to be working, execept the hangup while 
# writing to  unpluged serial port. BTW the mingw32 library behavior 
# is the same.
#
#
# To build rxtx library execute commands like the following
#  mkdir build
#  copy Makefile.msc build\Makefile
#  cd build
#  nmake
#
# To build only serial/parallel library use
# nmake serial
# or 
# nmake parallel
#
# By default the build target is x86, to compile for x64 set the variable TARGET=amd64, e.g.
# nmake TARGET=amd64 serial
#
# If you wish to make the version with debug info then do something 
# like this
# nmake serial DEBUG_INFO=1
#
# 'nmake clean' will remove all object dll and other working files  
#

JAVAC = "$(JAVA_HOME)\bin\javac"
JAR = "$(JAVA_HOME)\bin\jar"
JAVAH = "$(JAVA_HOME)\bin\javah"
MSVS=C:\Program Files (x86)\Microsoft Visual Studio 10.0
MSSDK=C:\Program Files (x86)\Microsoft SDKs\Windows
CC= "$(MSVS)\VC\bin\link.exe"
LINK= "$(MSVS)\VC\bin\link.exe"


SRC=..\src

CFLAGS= -nologo -I"$(JAVA_HOME)\include" -I"$(JAVA_HOME)\include\win32" -I"$(MSSDK)\v7.0A\Include" -I"$(MSVS)\VC\include" -I$(SRC) -I.
CFLAGS_X86= -DWIN32
CFLAGS_X64=
 
LINK_FLAGS= -nologo -map -incremental:no
LINK_FLAGS_X86 = /libpath:"$(MSSDK)\v7.0A\Lib" /libpath:"$(MSVS)\VC\lib"
LINK_FLAGS_X64 = /libpath:"$(MSSDK)\v7.0A\Lib\x64" /libpath:"$(MSVS)\VC\lib\amd64"

!IFDEF TARGET
!ELSE
TARGET=x86
!ENDIF

!IFDEF DEBUG_INFO
CFLAGS = -Z7 -Oi -Oy- $(CFLAGS)
CFLAGS_DLL = $(CFLAGS_DLL) -GZ
LINK_FLAGS = $(LINK_FLAGS) -debug -debugtype:CV -pdb:NONE
DEBUG_INFO_FLAG = DEBUG_INFO^=1
!ELSE
CFLAGS = $(CFLAGS) -Ox
!ENDIF

!IF "$(TARGET)" == "x86"
CC= "$(MSVS)\VC\bin\cl.exe"
LINK= "$(MSVS)\VC\bin\link.exe"

CFLAGS=$(CFLAGS) $(CFLAGS_X86)
LINK_FLAGS=$(LINK_FLAGS) $(LINK_FLAGS_X86)
!ELSE
CC= "$(MSVS)\VC\bin\amd64\cl.exe"
LINK= "$(MSVS)\VC\bin\amd64\link.exe"

CFLAGS=$(CFLAGS) $(CFLAGS_X86)
LINK_FLAGS=$(LINK_FLAGS) $(LINK_FLAGS_X64)
!ENDIF

OBJS=init.obj SerialImp.obj termios.obj fuserImp.obj
PARALLEL_OBJS= ParallelImp.obj termios.obj init.obj

vcvarsall:
	"$(MSVS)\VC\vcvarsall.bat" $(TARGET)

all: serial parallel

serial: clean vcvarsall RXTXcomm.jar rxtxSerial.dll

parallel: clean vcvarsall RXTXcomm.jar rxtxParallel.dll

init.obj: config.h
	$(CC) $(CFLAGS) /TP -c $(SRC)\init.cc

fixup.obj: config.h
	$(CC) $(CFLAGS) -c $(SRC)\fixup.c

fuserImp.obj: config.h gnu_io_CommPortIdentifier.h
	$(CC) $(CFLAGS) -c $(SRC)/fuserImp.c

termios.obj:
	$(CC) $(CFLAGS) -c $(SRC)/termios.c
SerialImp.obj:  config.h gnu_io_RXTXPort.h
	$(CC) $(CFLAGS) -c $(SRC)\SerialImp.c

ParallelImp.obj:  config.h gnu_io_LPRPort.h
	$(CC) $(CFLAGS) -c $(SRC)\ParallelImp.c

rxtxSerial.dll: $(OBJS)
	$(LINK) -dll -out:$@ $** $(LINK_FLAGS)

rxtxParallel.dll: $(PARALLEL_OBJS)
	$(LINK) -dll -out:$@ $** $(LINK_FLAGS)


gnu_io_RXTXPort.h gnu_io_CommPortIdentifier.h gnu_io_LPRPort.h: RXTXcomm.jar
	$(JAVAH) -jni gnu.io.RXTXPort gnu.io.CommPortIdentifier gnu.io.LPRPort

RXTXcomm.jar:
	$(JAVAC) -d . ..\src\*.java
	$(JAR) -cf RXTXcomm.jar gnu

config.h: Makefile
	echo #define HAVE_FCNTL_H >> config.h
	echo #define HAVE_SIGNAL_H >> config.h
	echo #undef HAVE_SYS_FCNTL_H >> config.h
	echo #undef HAVE_SYS_FILE_H >> config.h
	echo #undef HAVE_SYS_SIGNAL_H >> config.h
	echo #undef HAVE_TERMIOS_H >> config.h
	echo #undef HAVE_SYS_TIME_H >> config.h

clean:
	-rm -rf gnu
	-del *.obj *.h RXTXcomm.jar rxtxSerial.* rxtxParallel.*
