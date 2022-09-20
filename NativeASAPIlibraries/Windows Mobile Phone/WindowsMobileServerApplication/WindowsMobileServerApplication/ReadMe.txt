================================================================================
    MICROSOFT FOUNDATION CLASS LIBRARY : WindowsMobileServerApplication Project Overview
===============================================================================

The application wizard has created this WindowsMobileServerApplication application for 
you.  This application not only demonstrates the basics of using the Microsoft 
Foundation Classes but is also a starting point for writing your application.

This file contains a summary of what you will find in each of the files that
make up your WindowsMobileServerApplication application.

WindowsMobileServerApplication.vcproj
    This is the main project file for VC++ projects generated using an application wizard. 
    It contains information about the version of Visual C++ that generated the file, and 
    information about the platforms, configurations, and project features selected with the
    application wizard.

WindowsMobileServerApplication.h
    This is the main header file for the application.  It includes other
    project specific headers and declares the CWindowsMobileServerApplicationApp application class.

WindowsMobileServerApplication.cpp
    This is the main application source file that contains the application
    class CWindowsMobileServerApplicationApp.


WindowsMobileServerApplicationppc.rc
    This is the project's main resource file listing of all of the Microsoft Windows
    resources that the project uses when compiling for the Pocket PC platform, or a
    platform that supports the same user interface model.  It includes the icons,
    bitmaps, and cursors that are stored in the RES subdirectory.  This file can be 
    directly edited in Microsoft Visual C++. Your project resources are in 
    1033. When the .rc file is persisted, the defines in the data section
    are persisted as the hexadecimal version of the numeric value they are defined to
    rather than the friendly name of the define.

res\WindowsMobileServerApplicationppc.rc2
    This file contains resources that are not edited by Microsoft 
    Visual C++. You should place all resources not editable by
    the resource editor in this file.

WindowsMobileServerApplicationsp.rc
    This is the project's main resource file listing of all of the Microsoft Windows
    resources that the project uses when compiling for the Smartphone platform, or a
    platform that supports the same user interface model.  It includes the icons,
    bitmaps, and cursors that are stored in the RES subdirectory.  This file can be 
    directly edited in Microsoft Visual C++. Your project resources are in 
    1033. When the .rc file is persisted, the defines in the data section
    are persisted as the hexadecimal version of the numeric value they are defined to
    rather than the friendly name of the define.

res\WindowsMobileServerApplicationsp.rc2
    This file contains resources that are not edited by Microsoft 
    Visual C++. You should place all resources not editable by
    the resource editor in this file.

res\WindowsMobileServerApplication.ico
    This is an icon file, which is used as the application's icon.  This
    icon is included by the main resource file.


/////////////////////////////////////////////////////////////////////////////

The application wizard creates one dialog class:

CMainDialog.h, CMainDialog.cpp - the dialog
    These files contain your CMainDialog class.  This class defines
    the behavior of your application's main dialog.  The dialog's template is
    in the main resource file, which can be edited in Microsoft Visual C++.


/////////////////////////////////////////////////////////////////////////////

Other Features:

Windows Sockets
    The application has support for establishing communications over TCP/IP networks.

/////////////////////////////////////////////////////////////////////////////

Other standard files:

StdAfx.h, StdAfx.cpp
    These files are used to build a precompiled header (PCH) file
    named WindowsMobileServerApplication.pch and a precompiled types file named StdAfx.obj.

Resourceppc.h
Resourcesp.h
    This is the standard header file, which defines new resource IDs.
    Microsoft Visual C++ reads and updates this file.

/////////////////////////////////////////////////////////////////////////////

Other notes:

The application wizard uses "TODO:" to indicate parts of the source code you
should add to or customize.

If your application uses MFC in a shared DLL, and your application is in a 
language other than the operating system's current language, you will need 
to copy the corresponding localized resources MFC80XXX.DLL to your application
directory ("XXX" stands for the language abbreviation.  For example,
MFC80DEU.DLL contains resources translated to German.)  If you don't do this,
some of the UI elements of your application will remain in the language of the
operating system.

/////////////////////////////////////////////////////////////////////////////
