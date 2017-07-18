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
# The serial port library seems to be working, execept the hangup while 
# writing to  unpluged serial port. BTW the mingw32 library behavior 
# is the same.
#
# Parallel port library compiles, but I have not used it
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
# If you wish to make the version with debug info then do something 
# like this
# nmake serial DEBUG_INFO=1
#
# 'nmake clean' will remove all object dll and other working files  
#
# Please make sure that variable JAVA_HOME points to the place where 
# your Java SDK is located
#

#JAVA_HOME = C:\Java\jdk1.7.0_02
JAVAC = "$(JAVA_HOME)\bin\javac"
JAR = "$(JAVA_HOME)\bin\jar"
JAVAH = "$(JAVA_HOME)\bin\javah"


SRC=..\src
CFLAGS= -nologo -I"$(JAVA_HOME)\include" -I"$(JAVA_HOME)\include\win32" -I"C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\Include" -I"C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\include" -I$(SRC) -I. -DWIN32 
LINK_FLAGS = -nologo -map -incremental:no /libpath:"C:\Program Files (x86)\Microsoft SDKs\Windows\v7.0A\Lib" /libpath:"C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\lib"

!IFDEF DEBUG_INFO
CFLAGS = -Z7 -Oi -Oy- $(CFLAGS)
CFLAGS_DLL = $(CFLAGS_DLL) -GZ
LINK_FLAGS = $(LINK_FLAGS) -debug -debugtype:CV -pdb:NONE
DEBUG_INFO_FLAG = DEBUG_INFO^=1
!ELSE
CFLAGS = $(CFLAGS) -Ox
!ENDIF

OBJS=init.obj SerialImp.obj termios.obj fuserImp.obj
PARALLEL_OBJS= ParallelImp.obj termios.obj init.obj

vcvarsall:
	"C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\vcvarsall.bat" x86

vcbuildtools:
	"C:\Program Files (x86)\Microsoft Visual C++ Build Tools\vcbuildtools.bat" x86
	
all: serial parallel

serial: vcvarsall RXTXcomm.jar rxtxSerial.dll

parallel: RXTXcomm.jar rxtxParallel.dll

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
	"C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\bin\link.exe" -dll -out:$@ $** $(LINK_FLAGS)

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
