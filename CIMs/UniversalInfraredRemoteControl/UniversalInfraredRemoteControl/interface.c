/*
 * interface.c
 *
 * Created: 16.10.2014 08:56:24
 * Author:  Christoph Ulbinger
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */ 

#define F_CPU 8000000L

#include <avr/io.h>
#include <avr/interrupt.h>
#include <string.h>
#include <util/delay.h>

#include "interface.h"
#include "lcd.h"
#include "ADC.h"
#include "settings.h"
#include "fat.h"
#include "Buffer.h"
#include "RotaryEncoder.h"
#include "usb_serial.h"
#include <avr/wdt.h>

//lcd
uint8_t program_author[]   = "  ULBI";
uint8_t program_version[]  = " DOBLER";

char *part;
char tmp_value[3];
uint16_t ir_buffer_count = 0;

void displayMain()				// displays the startup sequence 
{
	lcd_write_first_line(program_author);
	lcd_write_second_line(program_version);
}
	
void init_menu()						// initializes variables of the menu
{
	settings_Value = 0;

	menu_level = 0;
	menu_position = 0;
	menu0_selected = 0;		// Main Menu, level 0
	menu1_selected = 0;		// Level 1
	menu2_selected = 0;		// Level 2
	
	// [level] [sub_menu_1] [sub_menu_2] -> amount of positions for the menu tree
	
	// Level 0 Menu tree
	MenuLevel0_Tree = 5;		// 5 categories
	
	//Level 1
	//MenuLevel1_Tree[0] = 0;		// Start RC  -> Done in Settings
	//MenuLevel1_Tree[1] = 0;		// Change RC -> Done in Settings
	MenuLevel1_Tree[2] = 1;			// Record New Device
	MenuLevel1_Tree[3] = 6;			// Settings
	MenuLevel1_Tree[4] = 3;			// Shutdown
	
	DeviceFunction_Tree = 0;		// Delete all functions
	
	Settings_Tree[1] = 3;			// JoyStick
	Settings_Tree[2] = 2;			// IR Code File
	Settings_Tree[3] = 2;			// Sorting
	Settings_Tree[4] = 1;			// kA
	Settings_Tree[5] = 1;			// kA
	
	Shutdown_Tree[0] = 1;
	Shutdown_Tree[1] = 1;
	
	//MAIN MENU
	strcpy(MainMenu[0],"Devices");
	strcpy(MainMenu_Info[0],"     1/5");
	
	strcpy(MainMenu[1],"Edit");
	strcpy(MainMenu_Info[1],"     2/5");
	
	strcpy(MainMenu[2],"New");
	strcpy(MainMenu_Info[2],"     3/5");
	
	strcpy(MainMenu[3],"Settings");
	strcpy(MainMenu_Info[3],"     4/5");	
	
	strcpy(MainMenu[4],"Shutdown");
	strcpy(MainMenu_Info[4],"     5/5");
	//END MAIN MENU
		
		
		//SETTINGS MENU
		strcpy(Settings[0],"return");
		strcpy(Settings_Info[0],"");
		
		strcpy(Settings[1],"Joystick");
		strcpy(Settings_Info[1],"     1/5");
	
		strcpy(Settings[2],"IR Codes");
		strcpy(Settings_Info[2],"     2/5");
	
		strcpy(Settings[3],"Sorting");
		strcpy(Settings_Info[3],"     3/5");
	
		strcpy(Settings[4],"SETTING4");
		strcpy(Settings_Info[4],"CHANGE       4/5");
		
		strcpy(Settings[5],"SETTING5");
		strcpy(Settings_Info[5],"CHANGE       5/5");
		//END SETTINGS MENU	
		
				//Joystick Sensitivity MENU
				strcpy(SettingsJoyStick[0],"return");
				strcpy(SettingsJoyStick_Info[0],"");
		
				strcpy(SettingsJoyStick[1],"Speed");
				strcpy(SettingsJoyStick_Info[1]," 1/2");
				
				strcpy(SettingsJoyStick[2],"Sensib.");
				strcpy(SettingsJoyStick_Info[2],"   2/2");
				//Joystick Sensitivity MENU
				
				//IR Code File MENU
				strcpy(IRCodeFileSetting_Info,"     1/1");
				//IR Code File MENU
				
				//IR Code File MENU
				strcpy(SortingSettings_Info,"     1/1");
				//IR Code File MENU
	
		//SHUTDWON MENU
		strcpy(shutdown[0],"return");
		strcpy(shutdown_Info[0],"");
		
		strcpy(shutdown[1],"Reboot");
		strcpy(shutdown_Info[1],"");
		
		strcpy(shutdown[2],"Standby");
		strcpy(shutdown_Info[2],"");
		//END SHUTDWON MENU
		
		show_menu();
}

void show_menu()			// displays the menu
{
	if(menu_level==0)									// MAIN MENU Level 0
	{
		lcd_write_first_line(MainMenu[menu_position]);
		lcd_write_second_line(MainMenu_Info[menu_position]);
	}
	else if(menu_level==1)	                                                // Menu level 1
	{
		switch(menu0_selected)
		{
			case 0:		
				lcd_write_first_line(devices[menu_position].DeviceName);
				lcd_write_second_line(devices[menu_position].DeviceType);
				break;
			case 1:
				lcd_write_first_line(devices[menu_position].DeviceName);
				lcd_write_second_line(devices[menu_position].DeviceType);
				break;
			case 2:
				lcd_write_first_line("Record");
				lcd_write_second_line("...");
				start_record_ir();
				break;
			case 3:
				lcd_write_first_line(Settings[menu_position]);
				lcd_write_second_line(Settings_Info[menu_position]);
				break;
			case 4:
				lcd_write_first_line(shutdown[menu_position]);
				lcd_write_second_line(shutdown_Info[menu_position]);
				break;
		}
	}	
	else if(menu_level==2)	                                                // Menu level 2
	{
		switch(menu0_selected)
		{
			case 0:
			case 1:
				if(menu_position>0)
				{
					lcd_write_first_line(devices[menu1_selected].DeviceName);
					lcd_write_second_line(functions[menu_position].Functions);
				}
				else
				{
					lcd_write_first_line(functions[menu_position].Functions);
					lcd_write_second_line("");
				}				
				break;
			case 2:														//New Device
				break;
			case 3:														// Settings
				switch(menu1_selected)
				{
					case 0:
						break;
					case 1:						//Joystick
						switch(menu_position)
						{
							case 0:
								lcd_write_first_line(SettingsJoyStick[menu_position]);
								lcd_write_second_line("");
								break;
							case 1:
								settings_Value = joystick_speed;
								lcd_write_first_line(SettingsJoyStick[menu_position]);
								lcd_write_second_line(itoa(settings_Value,menu_setting,10));
								_delay_ms(4);
								lcd_write_string(SettingsJoyStick_Info[menu_position]);
								
								break;
							case 2:
								settings_Value = joystick_sensibility;
								lcd_write_first_line(SettingsJoyStick[menu_position]);
								lcd_write_second_line(itoa(settings_Value,menu_setting,10));
								_delay_ms(4);
								lcd_write_string(SettingsJoyStick_Info[menu_position]);
								break;
						}								
						break;
					case 2:		// IR Code File
						switch(menu_position)
						{
							case 0:
								lcd_write_first_line("return");
								lcd_write_second_line("");
								break;
							default:								
								lcd_write_first_line(IRCodeFileSetting);
								lcd_write_second_line(IRCodeFileSetting_Info);
								break;
						}
						break;
					case 3:
						switch(menu_position)
						{
							case 0:
								lcd_write_first_line("return");
								lcd_write_second_line("");
								break;
							default:
								lcd_write_first_line(SortingSettings);
								lcd_write_second_line(SortingSettings_Info);
							break;
						}
						break;
				}
				break;
			case 4:
				switch(menu1_selected)
				{
					case 0:
						break;
					case 1:
						break;
				}
				break;
		}
	}
}


void ReadDeviceFunctions(unsigned char *DeviceTypeTmp, unsigned char *DeviceNameTmp)
{
	//Read IRCodes.CSV
	ClustervarRoot = 0;		//suche im Root Verzeichnis
	if (fat_search_file((unsigned char *)IRCodeFile,&ClustervarRoot,&Size,&Dir_Attrib,Buffer) == 1)
	{
		//Go through content
		IRDeviceSpecCounter = 0;
		characterCounter = 0;
		//Lese File und gibt es auf der seriellen Schnittstelle aus
		for (int b = 0;b<128;b++)
		{
			fat_read_file (ClustervarRoot,Buffer,b);
			if(getIRDeviceFunction(Buffer, DeviceTypeTmp, DeviceNameTmp)==0x01)
			{
				break;
			}
		}
	}
	SortFunctions(deviceSorting);
	AddReturnToFunctionList(functions);
}

void ReadDeviceIRCode(unsigned char *DeviceTypeTmp, unsigned char *DeviceNameTmp, unsigned char *DeviceFunctionTmp)
{
	//Read IRCodes.CSV
	ClustervarRoot = 0;		//suche im Root Verzeichnis
	if (fat_search_file((unsigned char *)IRCodeFile,&ClustervarRoot,&Size,&Dir_Attrib,Buffer) == 1)
	{
		//Go through content
		IRDeviceSpecCounter = 0;
		characterCounter = 0;
		InitBuffer();
		//Lese File und gibt es auf der seriellen Schnittstelle aus
		for (int b = 0;b<128;b++)
		{
			fat_read_file (ClustervarRoot,Buffer,b);
			if(getIRDeviceIRCode(Buffer, DeviceTypeTmp, DeviceNameTmp, DeviceFunctionTmp)==0x01)
			{
				break;
			}
		}
	}
}


void interfaceUp()
{
	if(menu_position>0)
	{
		menu_position--;
	}
	show_menu();
}

void interfaceDown()
{
	switch(menu_level)
	{
		case 0:
			if(menu_position<(MenuLevel0_Tree-1))
			{
				menu_position++;
			}
			break;
		case 1:
			if(menu_position<(MenuLevel1_Tree[menu0_selected]-1))
			{
				menu_position++;
			}
			break;
		case 2:
			switch(menu0_selected)
			{
				case 0:					// start / change device
				case 1:
					if(menu_position<(DeviceFunction_Tree-1))
					{
						menu_position++;
					}
					break;
				case 2:				// New Device
					break;
				case 3:				// Settings
					if(menu_position<(Settings_Tree[menu1_selected]-1))
					{
						menu_position++;
					}
					break;
				case 4:				// StandBy
					break;
			}
			break;
		case 3:
			break;
		case 4:
			break;
	}
	show_menu();
}

void interfaceSelect()
{
	switch(menu_level)
	{
		case 0:
			selectMenu();
			break;
		case 1:
			switch(menu0_selected)
			{
				case 0:
				case 1:
					switch(menu_position)
					{
						case 0:
							menuGoBack();
							break;
						default:
							selectMenu();
							break;
					}
					break;
				case 3:
				switch(menu_position)
				{
					case 0:
						menuGoBack();
						break;
					default:
						selectMenu();
						break;
				}
				break;
			case 4:
				switch(menu_position)
				{
					case 0:
					menuGoBack();
				break;
					case 1:
					lcd_write_first_line("Reboot");
					_delay_ms(200);
					lcd_write_first_line("Reboot");
					lcd_write_second_line("..");
					_delay_ms(200);
					lcd_write_first_line("Reboot");
					lcd_write_second_line("....");
					_delay_ms(200);
					lcd_write_first_line("Reboot");
					lcd_write_second_line("......");
					_delay_ms(200);
					lcd_write_first_line("Reboot");
					lcd_write_second_line("........");
					_delay_ms(200);
					init();
					return;
					break;
				case 2:
					lcd_write_first_line("Standby");
					_delay_ms(100);
					lcd_write_first_line("Standby");
					lcd_write_second_line("..");
					_delay_ms(100);
					lcd_write_first_line("Standby");
					lcd_write_second_line("....");
					_delay_ms(100);
					lcd_write_first_line("Standby");
					lcd_write_second_line("......");
					_delay_ms(100);
					lcd_write_first_line("Standby");
					lcd_write_second_line("........");
					_delay_ms(100);
					lcd_write_first_line("");
					menu_level=0;
					menu_position=0;
					menu0_selected=0;
					return;
					break;
				}
				break;
			}
		
			break;
		case 2:
			switch(menu0_selected)
			{
				case 0:										// start RC's
					switch(menu_position)
					{
						case 0:								// IR Code File
						menuGoBack();
						break;
						default:
						ReadDeviceIRCode(devices[menu1_selected].DeviceType,devices[menu1_selected].DeviceName,functions[menu_position].Functions);
						start_send_ir();
					}
			
					break;
				case 1:										// start RC's
					switch(menu_position)
					{
						case 0:								// IR Code File
						menuGoBack();
						break;
						default:
						lcd_write_first_line("not");
						lcd_write_second_line("available");
						_delay_ms(1000);
					}
			
					break;
				case 3:										// Settings
					switch(menu_position)
					{
						case 0:
						menuGoBack();
						break;
						default:
						break;
					}
					break;
			}
			break;
	}
	show_menu();
}


void selectMenu()
{
		switch(menu_level)
		{
			case 0:
				if(MenuLevel1_Tree[menu_position]>0)		// is in the next level something?
				{
					menu_level++;
					menu0_selected = menu_position;
					menu1_selected = 0;
				}
				break;
			case 1:
				switch(menu0_selected)
				{
					case 0:										// start or change RC's
					case 1:
						//LOAD FUNCTIONS
						IRDeviceFunctionCounter = 0;
						FunctionCounter = 0;
						ReadDeviceFunctions(devices[menu_position].DeviceType,devices[menu_position].DeviceName);
						if(FunctionCounter)				// something in the folder?
						{
							DeviceFunction_Tree= FunctionCounter+1;
							menu1_selected = menu_position;
							menu_level++;
							menu_position = 0;
						}
				
						break;
					case 2:												// New Device
				
					break;
					case 3:												// Settings
						if(Settings_Tree[menu_position]>0)
						{
							menu_level++;
							menu1_selected = menu_position;
							menu2_selected = 0;
						}
						break;
					case 4:
						if(Shutdown_Tree[menu_position]>0)
						{
							menu_level++;
							menu1_selected = menu_position;
							menu2_selected = 0;
						}
						break;
				}
				break;
			case 2:
				break;
			case 3:
				break;
		}
		menu_position = 0;
}

void menuGoBack()
{
	if(menu_level == 3)
	{
		menu_position = menu2_selected;
		menu2_selected = 0;
		menu_level--;
	}
	else if(menu_level == 2)
	{
		menu_position = menu1_selected;
		menu1_selected = 0;
		menu_level--;
	}
	else if(menu_level == 1)
	{
		menu_position = menu0_selected;
		menu1_selected = 0;
		menu0_selected = 0;
		menu_level--;
	}
	else if(menu_level == 0)
	{
		menu0_selected = 0;
		menu1_selected = 0;
	}
}

void init_timer0()		 // initializes the timer for the joystick recognition
{
	TIMSK0 |= (1<<TOIE0);
	TCCR0A = 0x00;
	TCCR0B |= (1<<CS12);
}


ISR(TIMER0_OVF_vect)	// ISR for joystick position recognition
{
	static uint16_t extButton_delay = 0;
	extButton_delay++;
	getRotaryEncoderState();
	if(extButton_delay==joystick_speed)	
	{
		getExternalInputs();
		extButton_delay = 0;
	}
}

void getExternalInputs()
{
	
	//External Input 0
	if(adc_read(0)<0x05)
	{		
		interfaceSelect();
	}
	
	//External Input 1
	if(adc_read(1)<0x05)
	{
		menuGoBack();
		show_menu();
	}
	
	//External Input 2
	if(adc_read(2)<0x05)
	{
		interfaceUp();
	}
	
	//External Input 3
	if(adc_read(3)<0x05)
	{
		interfaceDown();
	}
	
	//External Input 4
	if(adc_read(4)<0x05)
	{
		menu_level = 0;
		menu_position = 0;
		menu0_selected = 0;
		menu1_selected = 1;
		show_menu();
	}
	
	//External Input 5
	if(adc_read(5)<0x05)
	{
		interfaceSelect();
	}
}

void record_status(int status)		// states the condition of the recording procedure
{
	if(status==1)	//Error
	{
		lcd_write_first_line("Record");
		lcd_write_second_line("failed");
	}
	else if(status==0)	//OK
	{
		lcd_write_first_line("success.");
		lcd_write_second_line("recorded");
	}
	
	if(menu_level>0)
	{
		menu_position = menu0_selected;
		menu1_selected = 0;
		menu0_selected = 0;
		menu_level--;
	}
	_delay_ms(2000);
	show_menu();
}

void send_status(int status)			// states the condition of the sending procedure
{	
	_delay_ms(500);	
	show_menu();
}