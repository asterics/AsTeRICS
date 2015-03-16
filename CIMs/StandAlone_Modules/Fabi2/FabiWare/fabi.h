#include <Arduino.h>
#include <string.h>
#include <stdint.h>


#define TEENSY                 //  if a Teensy controller is used
// #define ARDUINO_PRO_MICRO   //  if Arduino Leonardo or Arduino Pro Micro is used 
 

#define NUMBER_OF_BUTTONS 6          // number of connected or virtual switches
#define NUMBER_OF_PHYSICAL_BUTTONS 6  // number of connected switches
#define NUMBER_OF_LEDS      3         // number of connected leds
#define MAX_SLOTS          5          // maximum number of EEPROM memory slots

#define MAX_KEYSTRING_LEN 26          // maximum lenght for key identifiers / keyboard text
#define MAX_SLOTNAME_LEN  15          // maximum lenght for a slotname
#define MAX_CMDLEN MAX_KEYSTRING_LEN+3

#define DEBUG_NOOUTPUT 0
#define DEBUG_FULLOUTPUT 1
#define DEBUG_LIVEREPORTS 2
#define DEFAULT_DEBUGLEVEL DEBUG_FULLOUTPUT

#define TOKEN_SEPERATOR "-,-"

// command identifiers

#define CMD_IDLE                     0
#define CMD_NEXT_SLOT                1
#define CMD_MOUSE_CLICK_LEFT         2
#define CMD_MOUSE_CLICK_RIGHT        3
#define CMD_MOUSE_CLICK_MIDDLE       4
#define CMD_MOUSE_CLICK_DOUBLE       5
#define CMD_MOUSE_PRESS_LEFT         6
#define CMD_MOUSE_PRESS_RIGHT        7
#define CMD_MOUSE_PRESS_MIDDLE       8
#define CMD_MOUSE_WHEEL_UP           9
#define CMD_MOUSE_WHEEL_DOWN        10
#define CMD_MOUSE_MOVEX             11
#define CMD_MOUSE_MOVEY             12
#define CMD_KEY_WRITE               13
#define CMD_KEY_PRESS               14

#define CMD_KEY_RELEASE             50
#define CMD_RELEASE_ALL             51
#define CMD_MOUSE_RELEASE_LEFT      52
#define CMD_MOUSE_RELEASE_RIGHT     53
#define CMD_MOUSE_RELEASE_MIDDLE    54
#define CMD_MOUSE_WHEEL_STEP        55
#define CMD_SAVE_SLOT               56
#define CMD_LOAD_SLOT               57
#define CMD_LIST_SLOTS              58
#define CMD_DELETE_SLOTS            59
#define CMD_PRINT_ID                60
#define CMD_BUTTON_MODE             61


struct settingsType {
  char slotname[MAX_SLOTNAME_LEN];     // EEPROM data is stored only until ths string's end
  uint8_t  ws;     // wheel stepsize  
  uint16_t tt;     // threshold time 
};

struct buttonType {                      // holds settings for a button function 
  int mode;
  int value;
  char keystring[MAX_KEYSTRING_LEN];
};

struct buttonDebouncerType {              // holds working data for button debouncing and longpress detection 
  uint8_t bounceCount;
  uint8_t bounceState;
  uint8_t stableState;
  uint8_t longPressed;
  uint32_t timestamp;
} ; 

extern uint8_t DebugOutput;
extern uint8_t actSlot;
extern struct settingsType settings;
extern int EmptySlotAddress;
extern struct buttonType buttons[NUMBER_OF_BUTTONS];
extern struct buttonDebouncerType buttonDebouncers[NUMBER_OF_BUTTONS];

void performCommand (uint8_t cmd, int16_t par1, char * keystring, int8_t periodicMouseMovement);
void saveToEEPROM(char * slotname);
void readFromEEPROM(char * slotname);
void deleteSlots();
void listSlots();


void BlinkLed();
int freeRam ();
void parseByte (int newByte);

void setKeyValues(char* text); // presses individual keys
void releaseKeys(char* text);  // releases individual keys
void release_all();            // releases all previously pressed keys and buttons



