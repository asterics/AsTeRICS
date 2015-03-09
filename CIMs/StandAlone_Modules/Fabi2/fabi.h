#include <Arduino.h>
#include <string.h>
#include <stdint.h>
#include "Bounce2.h"        //  Bounce library used for button debouncing


// #define ARDUINO_PRO_MICRO   //  if Arduino Leonardo or Arduino Pro Micro is used  (comment or remove if Teensy is used !)
#define TEENSY                 //  if teensy is used (but not a lipmouse module)

#define NUMBER_OF_BUTTONS 6          // number of connected switches
#define MAX_SLOTS         10         // maximum number of EEPROM memory slots

#define MAX_KEYSTRING_LEN 50         // maximum lenght for key identifiers / keyboard text
#define MAX_CMDLEN MAX_KEYSTRING_LEN+3

#define DEBUG_NOOUTPUT 0
#define DEBUG_FULLOUTPUT 1
#define DEBUG_LIVEREPORTS 2
#define DEFAULT_DEBUGLEVEL DEBUG_FULLOUTPUT

// command identifiers
#define CMD_PRINT_ID                 1
#define CMD_BUTTON_MODE              2
#define CMD_MOUSE_CLICK_LEFT         3
#define CMD_MOUSE_CLICK_RIGHT        4
#define CMD_MOUSE_CLICK_DOUBLE       5
#define CMD_MOUSE_CLICK_MIDDLE       6
#define CMD_MOUSE_PRESS_LEFT         7
#define CMD_MOUSE_PRESS_RIGHT        8
#define CMD_MOUSE_PRESS_MIDDLE       9
#define CMD_MOUSE_RELEASE_LEFT      10
#define CMD_MOUSE_RELEASE_RIGHT     11
#define CMD_MOUSE_RELEASE_MIDDLE    12
#define CMD_MOUSE_WHEEL_UP          13
#define CMD_MOUSE_WHEEL_DOWN        14
#define CMD_MOUSE_WHEEL_STEP        15
#define CMD_MOUSE_MOVEX             16
#define CMD_MOUSE_MOVEY             17
#define CMD_KEY_WRITE               18
#define CMD_KEY_PRESS               19
#define CMD_KEY_RELEASE             20
#define CMD_SAVE_SLOT               21
#define CMD_LOAD_SLOT               22
#define CMD_LIST_SLOTS              23
#define CMD_NEXT_SLOT               24
#define CMD_DELETE_SLOTS            25

// Global Variables
#include <stdint.h>

struct settingsType {
  uint8_t input_map[NUMBER_OF_BUTTONS];
  uint8_t LED_PIN;
  uint8_t  ws;     // wheel stepsize  
  uint8_t  ax;     // acceleration x (lipmouse only)
  uint8_t  ay;     // acceleration y (lipmouse only)
  uint8_t  dx;     // deadzone x (lipmouse only)
  uint8_t  dy;     // deydzone y (lipmouse only)
  uint16_t ts;     // threshold sip  (lipmouse only)
  uint16_t tp;     // threshold puff (lipmouse only)
};

struct buttonType {                         // holds command and data for a button function 
  int mode;
  int value;
  char keystring[MAX_KEYSTRING_LEN];
  Bounce * bouncer;
} ; 

extern uint8_t DebugOutput;

extern struct settingsType settings;
extern int EmptySlotAddress;
extern struct buttonType buttons[NUMBER_OF_BUTTONS];

void performCommand (uint8_t cmd, int16_t par1, char * keystring, int8_t periodicMouseMovement);
void saveToEEPROM(char * slotname);
void readFromEEPROM(char * slotname);
void deleteSlots();
void listSlots();


void BlinkLed();
int freeRam ();
void parseByte (int newByte);

void releaseKeys();  // releases all previously pressed keys
void setKeyValues(char* text);



