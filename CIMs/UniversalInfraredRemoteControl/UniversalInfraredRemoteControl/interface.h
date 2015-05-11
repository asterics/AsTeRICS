/*
 * interface.h
 *
 * Description: Contains variables and prototypes of interface.c
 *
 * Created: 16.10.2014 08:56:24
 * Author:  Christoph Ulbinger
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */ 

#ifndef _INTERFACE_H_
#define _INTERFACE_H_

#include <string.h>


#define NUMBER_OF_DEVICES 32
#define NUMBER_OF_FUNCTIONS 32

uint8_t settings_Value;

uint8_t menu_level;
uint8_t menu_position;
uint8_t menu0_selected;
uint8_t menu1_selected;
uint8_t menu2_selected;


uint8_t MenuLevel0_Tree;
uint8_t MenuLevel1_Tree[5];			// Level 1
uint8_t DeviceFunction_Tree;			// Number of Functions 1
uint8_t Settings_Tree[6];			// Number of Settings 1
uint8_t Shutdown_Tree[2];			// Number of Settings 1
	
//MAIN MENU
char MainMenu[5][17];		// [menu_position][]
char MainMenu_Info[5][17];

//START URC MENU
//Change RC Menu 


struct Devices_t{
	
	unsigned char DeviceType[17];
	unsigned char DeviceName[17];

};
	
struct Devices_t devices[NUMBER_OF_DEVICES+1];

struct Functions_t{
	unsigned char Functions[17];	
};

uint8_t FunctionCounter;

struct Functions_t functions[NUMBER_OF_FUNCTIONS+1];

//Settings Menu
char Settings[6][17];		// [menu_position][]
char Settings_Info[6][17];
//Settings Joystick Menu
char SettingsJoyStick[3][17];		// [menu_position][]
char SettingsJoyStick_Info[3][17];

//Setting IR Code File
char IRCodeFileSetting_Info[17];
char IRCodeFileSetting[17];

//Settings for Sorting
char SortingSettings[17];
char SortingSettings_Info[17];

//Shutdown menu
char shutdown[3][17];		// [menu_position][]
char shutdown_Info[3][17];

char menu_setting[17];


void displayMain(void);			// displays the startup sequence 
	
void init_menu(void);			// initializes variables of the menu

void show_menu(void);			// displays the menu

void selectMenu(void);

void menuGoBack(void);

void interfaceSelect(void);

void interfaceUp(void);

void interfaceDown(void);

void getExternalInputs(void);

void init_joystick(void);		// initializes the joystick and ADC

void init_timer1(void);			// initializes the timer for the joystick recognition

void record_status(int);		// states the condition of the recording procedure

void send_status(int);			// states the condition of the sending procedure

#endif