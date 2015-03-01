  
/* 
   Flexible Assistive Button Interface (FABI) Version 1.0  - AsTeRICS Academy 2015 - http://www.asterics-academy.net
      allows control of HID functions via AT-commands and/or momentary switches 
   

   requirements:  USB HID capable Arduino (Leonardo / Pro Micro resp. Clone ;-)
                  or Teensy 2.0++ with Teensyduino AddOn setup as USB composite device (Mouse + Keyboard + Serial)
                  Bounce2 library, see: https://github.com/thomasfredericks/Bounce2/wiki
       optional:  Momentary switches connected to GPIO pins (first pin: see START_BUTTON_PIN)
       
   
   Supported AT-commands:  
   (sent via serial interface, use spaces between parameters and Enter (<cr>, ASCII-code 0x0d) to finish a command)
   
          AT                returns "OK"
          AT ID             identification string will be returned (e.g. "FABI Version 2.0" or "Lipmouse Version 1.0")
          AT BM <num>       button mode setting for a button (e.g. AT BM 2 -> next command defines the new function for button 2)

          AT CL             click left mouse button  
          AT CR             click right mouse button  
          AT CD             click double with left mouse button
          AT CM             click middle mouse button  

          AT PL             press/hold the left mouse button  
          AT PR             press/hold the right mouse button
          AT PM             press/hold the middle mouse button 
  
          AT RL             release the left mouse button  
          AT RR             release the right mouse button
          AT RM             release the middle mouse button 
          
          AT WU             move mouse wheel up  
          AT WD             move mouse wheel down  
          AT WS <num>       set mouse wheel stepsize (e.g. AT WS 3 sets the wheel stepsize to 3 rows)
   
          AT MX <num>       move mouse in x direction (e.g. AT X 4 moves 4 pixels to the right)  
          AT MY <num>       move mouse in y direction (e.g. AT Y -10 moves 10 pixels up)  

          AT KW <text>      keyboard write text (e.g. AT KW Hello! writes "Hello!")    
          AT KP <text>      key press: press/hold all keys identified in text 
                            (e.g. AT KP KEY_UP presses the "Cursor-Up" key, AT KP KEY_CTRL KEY_ALT KEY_DELETE presses all three keys)
                            for a list of supported key idientifier strings see below ! 
                            
          AT KR             key release: releases all currently pressed keys (TBD: release individual keys ...)    
          
          AT SAVE <name>    save settings and current button modes to next free eeprom slot under given name (e.g. AT SAVE mouse1)
          AT LOAD <name>    load button modes from eeprom slot (e.g. AT LOAD mouse1 -> loads profile named "mouse1")
          AT LIST           list all saved mode names 
          AT NEXT           next mode will be loaded (wrap around after last slot)
          AT CLEAR          clear EEPROM content (delete all stored slots)

   optional commands for lipmouse module support (with force sensors for mouse x/y movement and pressure sensor for sip/puff):

          AT LMON             lipmouse on           (only for lipmouse)
          AT LMOFF            lipmouse off          (only for lipmouse)
          AT LMCA             start calibration     (only for lipmouse)
          AT LMAX <num>       acceleration x-axis   (only for lipmouse) 
          AT LMAY <num>       acceleration y-axis   (only for lipmouse) 
          AT LMDX <num>       deadzone x-axis       (only for lipmouse) 
          AT LMDY <num>       deadzone y-axis       (only for lipmouse)
          AT LMTS <num>       treshold sip action   (only for lipmouse)
          AT LMTP <num>       treshold puff action  (only for lipmouse)

   supported key identifiers for key press command (AT KP):
 
    KEY_A   KEY_B   KEY_C   KEY_D    KEY_E   KEY_F   KEY_G   KEY_H   KEY_I   KEY_J    KEY_K    KEY_L
    KEY_M   KEY_N   KEY_O   KEY_P    KEY_Q   KEY_R   KEY_S   KEY_T   KEY_U   KEY_V    KEY_W    KEY_X 
    KEY_Y   KEY_Z   KEY_1   KEY_2    KEY_3   KEY_4   KEY_5   KEY_6   KEY_7   KEY_8    KEY_9    KEY_0
    KEY_F1  KEY_F2  KEY_F3  KEY_F4   KEY_F5  KEY_F6  KEY_F7  KEY_F8  KEY_F9  KEY_F10  KEY_F11  KEY_F12	
    
    KEY_RIGHT   KEY_LEFT       KEY_DOWN        KEY_UP      KEY_ENTER    KEY_ESC   KEY_BACKSPACE   KEY_TAB	
    KEY_HOME    KEY_PAGE_UP    KEY_PAGE_DOWN   KEY_DELETE  KEY_INSERT   KEY_END	  KEY_NUM_LOCK    KEY_SCROLL_LOCK
    KEY_SPACE   KEY_CAPS_LOCK  KEY_PAUSE 
    
*/

 #define ARDUINO_PRO_MICRO   //  if Arduino Leonardo or Arduino Pro Micro is used  (comment or remove if Teensy is used !)
// #define TEENSY              //  if teensy is used (but not a lipmouse module)
 
// #define LIPMOUSE         //  Lipmouse module with Teensy!
// #define LIPMOUSE_V0      //  first HW version of lipmouse, powers pressure sensor via GPIO pins !

#define DEBUG_OUTPUT

#include <EEPROM.h>
#include "Bounce2.h"        //  Bounce library used for button debouncing

// Constants and Macro definitions

#define NUMBER_OF_BUTTONS 6             // number of connected switches

#define MAX_KEYSTRING_LEN 50         // maximum lenght for key identifiers / keyboard text
#define MAX_SLOTS         10         // maximum number of EEPROM memory slots

#define DEFAULT_WAIT_TIME       10   // wait time for one loop interation in milliseconds
#define DEFAULT_CLICK_TIME      8    // time for mouse click (loop iterations from press to release)
#define DOUBLECLICK_MULTIPLIER  5    // CLICK_TIME factor for double clicks
#define DEFAULT_DEBOUNCING_TIME 10   // debouncing interval for button-press / release

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

#define CMD_LM_ON                   26
#define CMD_LM_OFF                  27
#define CMD_LM_CA                   28
#define CMD_LM_AX                   29
#define CMD_LM_AY                   30
#define CMD_LM_DX                   31
#define CMD_LM_DY                   32
#define CMD_LM_TS                   33
#define CMD_LM_TP                   34

// Global Variables

struct settingsType {
  uint8_t input_map[NUMBER_OF_BUTTONS];
  uint8_t LED_PIN;
  uint8_t  mouseOn;
  uint8_t  ws;     // wheel stepsize  
  uint8_t  ax;     // acceleration x (lipmouse only)
  uint8_t  ay;     // acceleration y (lipmouse only)
  uint8_t  dx;     // deadzone x (lipmouse only)
  uint8_t  dy;     // deydzone y (lipmouse only)
  uint16_t ts;     // threshold sip  (lipmouse only)
  uint16_t tp;     // threshold puff (lipmouse only)
};

#ifdef ARDUINO_PRO_MICRO
  struct settingsType settings = {
    {2,3,4,5,6,7},           // default button pins for Arduino Pro Micro
    17,                      // default Led pin for Arduino Pro Micro
    0,                       // default mouse off
    0,0,0,0,0,0,0    // default values for acceleration, deadzone & thresholds (if lipmouse is used)
  }; 
#endif

#ifdef TEENSY
 struct settingsType settings = {
    {19,20,21,22,23,24},     // default button pins for Teensy
    25,                      // default Led pin for Teensy
    0,                       // default mouse off
    0,0,0,0,0,0,0            // default values for acceleration, deadzone & thresholds (if lipmouse is used)
  }; 
#endif 

#ifdef LIPMOUSE
  struct settingsType settings = {
    {2,3,4,5,6,7},           // default button pins for Lipmouse / Teensy
    25,                      // default Led pin for Lipmouse / Teensy
    1,                       // default mouse on
    3, 10, 10, 30, 30, 500, 530    // default values for acceleration, deadzone & thresholds (if lipmouse is used)
  }; 
#endif

unsigned long previousTime = 0;
float accumXpos = 0.f;
float accumYpos = 0.f;
static int x;
static int y;
static int x_offset;
static int y_offset;
static int calib_now = 1;


struct {                         // holds command and data for a button function 
  int mode;
  int value;
  char keystring[MAX_KEYSTRING_LEN];
  Bounce * bouncer;
} buttons [NUMBER_OF_BUTTONS];   // array for all buttons - defines one memory slot 

int clickTime=DEFAULT_CLICK_TIME;
int waitTime=DEFAULT_WAIT_TIME;

byte actButton=0;

byte leftMouseButton=0,old_leftMouseButton=0;
byte middleMouseButton=0,old_middleMouseButton=0;
byte rightMouseButton=0,old_rightMouseButton=0;

int leftClickRunning=0;
int rightClickRunning=0;
int middleClickRunning=0;
int doubleClickRunning=0;

int8_t moveX=0;       
int8_t moveY=0;       

int inByte=0;
char * keystring=0;
char * writeKeystring=0;

unsigned long time=0;
int EmptySlotAddress = 0;

// Setup: program execution starts here

void setup() {
   Serial.begin(9600);
   delay(5000);
    //while (!Serial) ;
   
   #ifdef DEBUG_OUTPUT  
     Serial.println("Flexible Assistive Button Interface started !");
     // Serial.print("Free RAM:");  Serial.println(freeRam());
   #endif

   #ifdef ARDUINO_PRO_MICRO   // only needed for Arduino, automatically done for Teensy(duino)
     Mouse.begin();
     Keyboard.begin();
     TXLED1;
   #endif  

   #ifdef LIPMOUSE_V0     // only needed if first lipmouse hardware version
     pinMode(21,OUTPUT);
     pinMode(22,OUTPUT);  
     digitalWrite(21,HIGH);  // supply voltage for pressure sensor
     digitalWrite(22,LOW);
   #endif


   pinMode(settings.LED_PIN,OUTPUT);

   for (int i=0; i<NUMBER_OF_BUTTONS; i++)   // initialize button array
   {
      pinMode (settings.input_map[i], INPUT_PULLUP);   // configure the pins for input mode with pullup resistors
      buttons[i].bouncer=new Bounce();
      buttons[i].bouncer->attach(settings.input_map[i]);
      buttons[i].bouncer->interval(DEFAULT_DEBOUNCING_TIME);
      buttons[i].mode=CMD_MOUSE_PRESS_LEFT;              // default command for every button is left mouse click
      buttons[i].value='L';
      buttons[i].keystring[0]=0;
   }

   readFromEEPROM(0);  // read button modes from first EEPROM slot if available !  
   BlinkLed();
   #ifdef DEBUG_OUTPUT  
     Serial.print("Free RAM:");  Serial.println(freeRam());
   #endif
}

// Loop: the main program loop

void loop() {  
    if (Serial.available() > 0) {
      // get incoming byte:
      inByte = Serial.read();
      parseByte (inByte);
    }
  
    for (int i=0;i<NUMBER_OF_BUTTONS;i++)    // update button press / release events
    {
      buttons[i].bouncer->update();
  
      if (buttons[i].bouncer->fell()) 
         handlePress(i); 
      else if (buttons[i].bouncer->rose())
         handleRelease(i);
    }
      
    // handle running clicks or double clicks
    if (leftClickRunning)
        if (--leftClickRunning==0)  leftMouseButton=0; 
    
    if (rightClickRunning)
        if (--rightClickRunning==0)  rightMouseButton=0; 
 
    if (middleClickRunning)
        if (--middleClickRunning==0)  middleMouseButton=0; 

    if (doubleClickRunning)
    {
        doubleClickRunning--;
        if (doubleClickRunning==clickTime*2)  leftMouseButton=0; 
        else if (doubleClickRunning==clickTime)    leftMouseButton=1; 
        else if (doubleClickRunning==0)    leftMouseButton=0; 
    }
  
    // handle mouse movement
    if ((moveX!=0) || (moveY!=0))  
         Mouse.move(moveX, moveY);
 
    // if any changes were made, update the Mouse buttons
    if((leftMouseButton!=old_leftMouseButton) ||
       (middleMouseButton!=old_middleMouseButton) ||
       (rightMouseButton!=old_rightMouseButton))  {

         #ifdef ARDUINO_PRO_MICRO
           if (leftMouseButton) Mouse.press(MOUSE_LEFT); else Mouse.release(MOUSE_LEFT);
           if (rightMouseButton) Mouse.press(MOUSE_RIGHT); else Mouse.release(MOUSE_RIGHT);
           if (middleMouseButton) Mouse.press(MOUSE_MIDDLE); else Mouse.release(MOUSE_MIDDLE);
         #else         
           Mouse.set_buttons(leftMouseButton, middleMouseButton, rightMouseButton);
         #endif
         old_leftMouseButton=leftMouseButton;
         old_middleMouseButton=middleMouseButton;
         old_rightMouseButton=rightMouseButton;
    }
    
    // handle Keyboard output (single key press/release is done seperately via setKeyValues() ) 
    if (writeKeystring)
    {
        Keyboard.print(writeKeystring);
        writeKeystring=0;
    }    
    
    
    #ifdef LIPMOUSE
      unsigned long currentTime = millis();
      float timeDifference = (currentTime - previousTime)/1000.f;
      previousTime = currentTime;
      
      int pressure = analogRead(A0);
      int down = analogRead(42);
      int left = analogRead(43);
      int up = analogRead(44);
      int right = analogRead(45);
  
      if (calib_now == 1)
      {
         x_offset = (left-right);
         y_offset = (up-down);
         calib_now=0;
      }    
      else
      {
          x = (left-right) - x_offset;
          y = (up-down) - y_offset;
      }
    
      if (abs(x)< settings.dx) x=0;
      if (abs(y)< settings.dy) y=0;
      
      if (settings.mouseOn == 1)
      {
        accumYpos += y*settings.ay*timeDifference; 
        accumXpos += x*settings.ax*timeDifference; 
      
        int xMove = (int)accumXpos;
        int yMove = (int)accumYpos;
      
        Mouse.move(xMove, yMove);
      
        accumXpos -= xMove;
        accumYpos -= yMove;
      }
  
      if (pressure > settings.tp) rightMouseButton=1;
      else rightMouseButton=0; 

      if (pressure > settings.ts) leftMouseButton=1;
      else leftMouseButton=0;       
      
    #endif
    
    delay(waitTime);  // to limit move movement speed. TBD: remove delay, use millis() !
}


void handlePress (int buttonIndex)   // a button was pressed
{   
    performCommand(buttons[buttonIndex].mode,buttons[buttonIndex].value,buttons[buttonIndex].keystring,1);
}

void handleRelease (int buttonIndex)    // a button was released
{
   switch(buttons[buttonIndex].mode) {
     case CMD_MOUSE_PRESS_LEFT: leftMouseButton=0; break;
     case CMD_MOUSE_PRESS_RIGHT: rightMouseButton=0; break;
     case CMD_MOUSE_PRESS_MIDDLE: middleMouseButton=0; break;
     case CMD_MOUSE_MOVEX: moveX=0; break;      
     case CMD_MOUSE_MOVEY: moveY=0; break;      
     case CMD_KEY_PRESS: releaseKeys(); break; 
   }
}

void BlinkLed()
{
    for (uint8_t i=0; i < 5;i++)
    {
        digitalWrite (settings.LED_PIN, !digitalRead(settings.LED_PIN));
        delay(100);
    }
    digitalWrite (settings.LED_PIN, HIGH);
}

// perform a command
//   cmd: command identifier
//   par1: optional numeric parameter
//   periodicMouseMovement: if true, mouse will continue moving - if false: only one movement
void performCommand (uint8_t cmd, int16_t par1, char * keystring, int8_t periodicMouseMovement)
{
  if (actButton != 0)
  {
      #ifdef DEBUG_OUTPUT  
        Serial.print("got new mode for button "); Serial.print(actButton);Serial.print(":");
        Serial.print(cmd);Serial.print(",");Serial.print(par1);Serial.print(",");Serial.println(keystring);
      #endif
      buttons[actButton-1].mode=cmd;
      buttons[actButton-1].value=par1;
      if (keystring==0) buttons[actButton-1].keystring[0]=0;
      else strcpy(buttons[actButton-1].keystring,keystring);
      actButton=0;
      BlinkLed();
      return;
  }
  
  switch(cmd) {
      case CMD_PRINT_ID:
             Serial.println("FABI Version 1.0"); 
          break;
      case CMD_BUTTON_MODE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("set mode for button "); Serial.println(par1);
             #endif
             if ((par1>0) && (par1<=NUMBER_OF_BUTTONS))
                 actButton=par1;
             else  Serial.println("?");
          break;
      
      case CMD_MOUSE_CLICK_LEFT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("click left");
             #endif
             leftMouseButton=1;  leftClickRunning=clickTime;
             break;
      case CMD_MOUSE_CLICK_RIGHT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("click right");
             #endif
             rightMouseButton=1; rightClickRunning=clickTime;
             break;
      case CMD_MOUSE_CLICK_DOUBLE:
             #ifdef DEBUG_OUTPUT  
               Serial.println("click double");
             #endif
             leftMouseButton=1;  doubleClickRunning=clickTime*DOUBLECLICK_MULTIPLIER;
             break;
      case CMD_MOUSE_CLICK_MIDDLE:
             #ifdef DEBUG_OUTPUT  
               Serial.println("click middle");
             #endif
             middleMouseButton=1; middleClickRunning=clickTime;
            break;
      case CMD_MOUSE_PRESS_LEFT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("press left");
             #endif
             leftMouseButton=1; 
             break;
      case CMD_MOUSE_PRESS_RIGHT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("press right");
             #endif
             rightMouseButton=1; 
             break;
      case CMD_MOUSE_PRESS_MIDDLE:
             #ifdef DEBUG_OUTPUT  
               Serial.println("press middle");
             #endif
             middleMouseButton=1; 
             break;
      case CMD_MOUSE_RELEASE_LEFT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("release left");
             #endif
             leftMouseButton=0;
             break; 
      case CMD_MOUSE_RELEASE_RIGHT:
             #ifdef DEBUG_OUTPUT  
               Serial.println("release right");
             #endif
             rightMouseButton=0;
             break; 
      case CMD_MOUSE_RELEASE_MIDDLE:
             #ifdef DEBUG_OUTPUT  
               Serial.println("release middle");
             #endif
             middleMouseButton=0;
             break; 
      case CMD_MOUSE_WHEEL_UP:
             #ifdef DEBUG_OUTPUT  
               Serial.println("wheel up");
             #endif
             #ifndef ARDUINO_PRO_MICRO
               Mouse.scroll(-settings.ws); 
             #else
               Mouse.move (0,0,-settings.ws); 
             #endif
          break;
      case CMD_MOUSE_WHEEL_DOWN:
             #ifdef DEBUG_OUTPUT  
               Serial.println("wheel down");
             #endif
             #ifndef ARDUINO_PRO_MICRO
               Mouse.scroll(settings.ws); 
             #else
               Mouse.move (0,0,settings.ws); 
             #endif
          break;
      case CMD_MOUSE_WHEEL_STEP:
             #ifdef DEBUG_OUTPUT  
               Serial.println("wheel step");
             #endif
             settings.ws=par1;
          break;
      case CMD_MOUSE_MOVEX:
             #ifdef DEBUG_OUTPUT  
               Serial.print("mouse move x "); Serial.println(par1);
             #endif
             Mouse.move(par1, 0);
             if (periodicMouseMovement) moveX=par1;
          break;
      case CMD_MOUSE_MOVEY:
             #ifdef DEBUG_OUTPUT  
               Serial.print("mouse move y "); Serial.println(par1);
             #endif
             Mouse.move(0, par1);
             if (periodicMouseMovement) moveY=par1;
          break;
      case CMD_KEY_WRITE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("keyboard write: "); Serial.println(keystring);
             #endif
             writeKeystring=keystring;
             break;
      case CMD_KEY_PRESS:
             #ifdef DEBUG_OUTPUT  
               Serial.print("key press: "); Serial.println(keystring);
             #endif
             strcat(keystring," ");
             setKeyValues(keystring);
             break;
      case CMD_KEY_RELEASE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("key release: ");
             #endif
             releaseKeys();             
             break;
      case CMD_SAVE_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("save slot ");  Serial.println(keystring);
             #endif
             saveToEEPROM(keystring); 
          break;
      case CMD_LOAD_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("load slot: "); Serial.println(keystring);
             #endif
             readFromEEPROM(keystring);
          break;
      case CMD_NEXT_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("load next slot");
             #endif
             readFromEEPROM(0); 
          break;
      case CMD_DELETE_SLOTS:
             #ifdef DEBUG_OUTPUT  
               Serial.println("delete slots"); 
             #endif
             deleteSlots(); 
          break;


      case CMD_LM_ON:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse on");
             #endif
             settings.mouseOn=1;
          break;
      case CMD_LM_OFF:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse off");
             #endif
             settings.mouseOn=0;
          break;
      case CMD_LM_CA:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse calibrate");
             #endif
             calib_now=1;
          break;
      case CMD_LM_AX:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse acc x");
             #endif
             settings.ax=par1;
          break;
      case CMD_LM_AY:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse acc y");
             #endif
             settings.ay=par1;
          break;
      case CMD_LM_DX:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse deadzone x");
             #endif
             settings.dx=par1;
          break;
      case CMD_LM_DY:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse deadzone y");
             #endif
             settings.dy=par1;
          break;
      case CMD_LM_TS:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse threshold sipp");
             #endif
             settings.ts=par1;
          break;
      case CMD_LM_TP:
             #ifdef DEBUG_OUTPUT  
               Serial.println("lipmouse threshold puff");
             #endif
             settings.tp=par1;
          break;
  }
}


uint8_t get_uint(char * str, int * result)
{
    int num=0;
    if ((str==0)||(*str==0)) return (0);
    while (*str)
    {
      if ((*str >= '0') && (*str<='9'))
         num=num*10+(*str - '0'); 
      else return(0);
      str ++;
    }
    *result=num;
    return(1);    
}

uint8_t get_int(char * str, int * result)
{
    int num,fact;
    if (str==0) return(0);
    if (*str =='-') {fact=-1; str++;} else fact=1;
    if (!get_uint(str,&num)) return(0);
    *result=num*fact;
    return(1);    
}

void strup (char * str)   // convert to upper case letters
{
  if (!str) return;
  while (*str)
  {
    if ((*str>='a') && (*str<='z')) *str=*str-'a'+'A';
    str++;
  }
}

void parseCommand (char * cmdstr)
{
    uint8_t cmd=0;
    int16_t num=0;
    
     // Serial.print("parseCommand:"); Serial.println(cmdstr);
    char * actpos = strtok(cmdstr," ");   // see a nice explaination of strtok here:  http://www.reddit.com/r/arduino/comments/2h9l1l/using_the_strtok_function/
    if (actpos) 
    {
        strup(actpos);
        if (!strcmp(actpos,"ID")) cmd=CMD_PRINT_ID;
        if (!strcmp(actpos,"BM")) { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_BUTTON_MODE; }
        if (!strcmp(actpos,"CL")) cmd=CMD_MOUSE_CLICK_LEFT;
        if (!strcmp(actpos,"CR")) cmd=CMD_MOUSE_CLICK_RIGHT; 
        if (!strcmp(actpos,"CM")) cmd=CMD_MOUSE_CLICK_MIDDLE; 
        if (!strcmp(actpos,"PL")) cmd=CMD_MOUSE_PRESS_LEFT; 
        if (!strcmp(actpos,"PR")) cmd=CMD_MOUSE_PRESS_RIGHT; 
        if (!strcmp(actpos,"PM")) cmd=CMD_MOUSE_PRESS_MIDDLE; 
        if (!strcmp(actpos,"RL")) cmd=CMD_MOUSE_RELEASE_LEFT; 
        if (!strcmp(actpos,"RR")) cmd=CMD_MOUSE_RELEASE_RIGHT; 
        if (!strcmp(actpos,"RM")) cmd=CMD_MOUSE_RELEASE_MIDDLE; 
        if (!strcmp(actpos,"WU")) cmd=CMD_MOUSE_WHEEL_UP; 
        if (!strcmp(actpos,"WD")) cmd=CMD_MOUSE_WHEEL_DOWN; 
        if (!strcmp(actpos,"WS")) { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_MOUSE_WHEEL_STEP; }
        if (!strcmp(actpos,"MX")) { actpos=strtok(NULL," "); if (get_int(actpos, &num)) cmd=CMD_MOUSE_MOVEX; }
        if (!strcmp(actpos,"MY")) { actpos=strtok(NULL," "); if (get_int(actpos, &num)) cmd=CMD_MOUSE_MOVEY; }
        if (!strcmp(actpos,"KW")) { actpos+=3; cmd=CMD_KEY_WRITE; }
        if (!strcmp(actpos,"KP")) { actpos+=3; cmd=CMD_KEY_PRESS; }
        if (!strcmp(actpos,"KR")) cmd=CMD_KEY_RELEASE;
    
        if (!strcmp(actpos,"SAVE"))  { actpos=strtok(NULL," "); strup (actpos); cmd=CMD_SAVE_SLOT; }
        if (!strcmp(actpos,"LOAD"))  { actpos=strtok(NULL," "); strup (actpos); cmd=CMD_LOAD_SLOT; }
        if (!strcmp(actpos,"NEXT"))  cmd=CMD_NEXT_SLOT;
        if (!strcmp(actpos,"CLEAR")) cmd=CMD_DELETE_SLOTS;
        if (!strcmp(actpos,"LIST"))  cmd=CMD_LIST_SLOTS;

        if (!strcmp(actpos,"LMON"))  cmd=CMD_LM_ON;
        if (!strcmp(actpos,"LMOFF")) cmd=CMD_LM_OFF;
        if (!strcmp(actpos,"LMCA"))  cmd=CMD_LM_CA;
        if (!strcmp(actpos,"LMAX"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_AX;}
        if (!strcmp(actpos,"LMAY"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_AY;}
        if (!strcmp(actpos,"LMDX"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_DX;}
        if (!strcmp(actpos,"LMDY"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_DY;}
        if (!strcmp(actpos,"LMTS"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_TS;}
        if (!strcmp(actpos,"LMTP"))  { actpos=strtok(NULL," "); if (get_uint(actpos, &num)) cmd=CMD_LM_TP;}
    }
    if (cmd)
    {
        // Serial.print("cmd parser found:");Serial.print(cmd); Serial.print(", "); Serial.print(num); 
        // if (actpos) {Serial.print(", "); Serial.println(actpos);} else Serial.println();   
        performCommand(cmd,num,actpos,0);        
    }
    else   Serial.println("cmd parser: ?");              
}



#define MAX_CMDLEN MAX_KEYSTRING_LEN+3
static char cmdstring[MAX_CMDLEN];

void parseByte (int newByte)  // parse an incoming commandbyte from serial interface, perform command if valid
{
  static uint8_t state=0;
  static uint8_t cmdlen=0;
 
  switch (state) {
    case 0: 
            if ((newByte=='A') || (newByte=='a')) state++;
         break;
    case 1: 
            if ((newByte=='T') || (newByte=='t')) state++; else state=0;
        break;
    case 2: 
            if ((newByte==13) || (newByte==10))
            {  Serial.println("OK");  state=0; }
            else if (newByte==' ') { cmdlen=0; state++; } 
            else goto err;
        break;
    case 3: 
            if ((newByte==13) || (newByte==10) || (cmdlen>=MAX_CMDLEN-1))
            {  cmdstring[cmdlen]=0; parseCommand(cmdstring); state=0; }
            else cmdstring[cmdlen++]=newByte;
        break;   
    default: err: Serial.println("?");state=0;
  }
}



#define SLOT_VALID 123
#define MAX_SLOTNAME_LENGTH 15

int nextSlotAddress=0,firstSlotAddress=0;

void saveToEEPROM(char * slotname)
{
   uint8_t done,len=0;
   byte* p;
   int address = 0;

   EEPROM.write(address++,SLOT_VALID);  
   p = (byte*) &settings;
   for (int t=0;t<sizeof(settingsType);t++)
      EEPROM.write(address++,*p++);

   if (EmptySlotAddress>address)
      address=EmptySlotAddress;
 
   #ifdef DEBUG_OUTPUT
     Serial.print("Writing slot ");Serial.print(slotname);Serial.print(" starting from EEPROM address "); Serial.println(address);
   #endif
   
   EEPROM.write(address++,SLOT_VALID);
   while (*slotname && (len++<MAX_SLOTNAME_LENGTH))
      EEPROM.write(address++,*slotname++);
   EEPROM.write(address++,0);
 
   for (int i=0;i<NUMBER_OF_BUTTONS;i++)
   {
      done=0;
      byte* p = (byte*) &(buttons[i].mode);
      for (int t=0;(t<MAX_KEYSTRING_LEN+4)&&(!done);t++)
      {
        EEPROM.write(address++,*p);
        if ((t>3) && (*p==0)) done=1;     // skip rest of keystring when end detected !
        else p++;
      }
   }
   EEPROM.write(address,0);  // indicates last slot !
   EmptySlotAddress=address;
   
   #ifdef DEBUG_OUTPUT
     Serial.print(address); Serial.println(" bytes saved to EEPROM");
   #endif
}

void deleteSlots()
{
   EmptySlotAddress=0;
   nextSlotAddress=0,firstSlotAddress=0;
   EEPROM.write(0,0);
}


void readFromEEPROM(char * slotname)
{
   char act_slotname[MAX_SLOTNAME_LENGTH];
   int address=0;
   int tmpNextSlotAddress=0;
   uint8_t done;
   uint8_t numSlots=0;
   byte b;
   byte* p;
   
   if (EEPROM.read(address)==SLOT_VALID)
   {
      p = (byte*) &settings;
      address++;
      for (int t=0;t<sizeof(settingsType);t++)
      {
          *p++=EEPROM.read(address++);
          //Serial.print("address:"); Serial.print(address-1);
          //Serial.print("value: ");Serial.println(b);          
      }
      firstSlotAddress=address;
      if (nextSlotAddress==0) nextSlotAddress=address;
   }

   while (EEPROM.read(address)==SLOT_VALID)  // indicates valid eeprom content !
   {
     uint8_t i=0;
     uint8_t found=0;
     
     if ((!slotname) && (address==nextSlotAddress)) found=1;
     address++;
     while ((act_slotname[i++]=EEPROM.read(address++)) != 0) ;
     #ifdef DEBUG_OUTPUT
       Serial.print("found slotname "); Serial.println(act_slotname);
     #endif
     
     if (slotname)
     {
        if (!strcmp(act_slotname, slotname)) found=1;  
     }
     #ifdef DEBUG_OUTPUT  
       if (found) Serial.println(" -> loading slot!");
     #endif
     
     for (i=0;i<NUMBER_OF_BUTTONS;i++)
     {
        done=0;
        p = (byte*) &(buttons[i].mode);
               
        for (int t=0;(t<MAX_KEYSTRING_LEN+4)&&(!done);t++)
        {
          b=EEPROM.read(address++);
          //Serial.print("address:"); Serial.print(address-1);
          //Serial.print("value: ");Serial.println(b);
          if (found) *p++=b;               // copy to SRAM only if intended slot !
          if ((t>3) && (b==0)) done=1;     // skip rest of keystring when end detected !
        }
        if ((done)&&(found)) tmpNextSlotAddress=address;
     }
     numSlots++;
   }
   
   EmptySlotAddress=address;
   if (tmpNextSlotAddress) nextSlotAddress=tmpNextSlotAddress;
   if (nextSlotAddress==EmptySlotAddress) nextSlotAddress=firstSlotAddress;
   
   #ifdef DEBUG_OUTPUT
       Serial.print(numSlots); Serial.print(" slots were found in EEPROM, occupying ");
       Serial.print(address); Serial.println(" bytes.");
   #endif
}

void releaseKeys()  // releases all previously pressed keys
{
  #ifdef DEBUG_OUTPUT
    Serial.println("key release");
  #endif   

  #ifdef ARDUINO_PRO_MICRO
    Keyboard.releaseAll();
  #else   // for Teensy2.0++
   Keyboard.set_modifier(0);
   Keyboard.set_key1(0);
   Keyboard.set_key2(0);
   Keyboard.set_key3(0);
   Keyboard.set_key4(0);
   Keyboard.set_key5(0);
   Keyboard.set_key6(0);
   Keyboard.send_now();
  #endif
}




 #ifdef ARDUINO_PRO_MICRO
      #define KEY_UP    KEY_UP_ARROW
      #define KEY_DOWN  KEY_DOWN_ARROW
      #define KEY_LEFT  KEY_LEFT_ARROW
      #define KEY_RIGHT KEY_RIGHT_ARROW
      #define KEY_ENTER KEY_RETURN  
      #define KEY_SPACE ' '
      #define KEY_A 'a'
      #define KEY_B 'b'
      #define KEY_C 'c'
      #define KEY_D 'd'
      #define KEY_E 'e' 
      #define KEY_F 'f'
      #define KEY_G 'g'
      #define KEY_H 'h'
      #define KEY_I 'i'
      #define KEY_J 'j'
      #define KEY_K 'k'
      #define KEY_L 'l'
      #define KEY_M 'm'
      #define KEY_N 'n'
      #define KEY_O 'o'
      #define KEY_P 'p'
      #define KEY_Q 'q'
      #define KEY_R 'r'
      #define KEY_S 's'
      #define KEY_T 't'
      #define KEY_U 'u'
      #define KEY_V 'v'
      #define KEY_W 'w'
      #define KEY_X 'x'
      #define KEY_Y 'y'
      #define KEY_Z 'z'
      #define KEY_0 '0'
      #define KEY_1 '1'
      #define KEY_2 '2'
      #define KEY_3 '3'
      #define KEY_4 '4'
      #define KEY_5 '5'
      #define KEY_6 '6'
      #define KEY_7 '7'
      #define KEY_8 '8'
      #define KEY_9 '9'
#endif


int numKeys=0;
char tmptxt[MAX_KEYSTRING_LEN];   // for parsing keystrings

// press all supported keys 
// text is a string which contains the key identifiers eg. "KEY_CTRL KEY_C" for Ctrl-C
// TBD: improve ! add a real parser which iterated through key identifiers, add new commands e.g. WAIT etc...
void setKeyValues(char* text)
{
  char * acttoken;
  int modifiers=0;
  numKeys=0; 

  strcpy(tmptxt, text); 
  acttoken = strtok(tmptxt," ");
  
  while (acttoken)
  {
    #ifdef ARDUINO_PRO_MICRO
        if (!strcmp(acttoken,"KEY_SHIFT"))  addKey(KEY_LEFT_SHIFT);
        if (!strcmp(acttoken,"KEY_CTRL"))  addKey(KEY_LEFT_CTRL);
        if (!strcmp(acttoken,"KEY_ALT"))  addKey(KEY_RIGHT_ALT);
        if (!strcmp(acttoken,"KEY_GUI"))  addKey(KEY_RIGHT_GUI);
        // if (!strcmp(acttoken,"KEY_PRINTSCREEN")) addKey(KEY_PRINTSCREEN);   
    #else     // for Teensy2.0++
        if (!strcmp(acttoken,"KEY_SCROLL_LOCK")) addKey(KEY_SCROLL_LOCK);
        if (!strcmp(acttoken,"KEY_PAUSE")) addKey(KEY_PAUSE);
        if (!strcmp(acttoken,"KEY_NUM_LOCK")) addKey(KEY_NUM_LOCK);
        if (!strcmp(acttoken,"KEY_PRINTSCREEN")) addKey(KEY_PRINTSCREEN);
        
        if (!strcmp(acttoken,"KEY_SHIFT"))  modifiers|=MODIFIERKEY_SHIFT;
        if (!strcmp(acttoken,"KEY_CTRL"))  modifiers|=MODIFIERKEY_CTRL;
        if (!strcmp(acttoken,"KEY_ALT"))  modifiers|=MODIFIERKEY_ALT;
        if (!strcmp(acttoken,"KEY_GUI"))  modifiers|=MODIFIERKEY_GUI;
    #endif

    if (!strcmp(acttoken,"KEY_UP")) addKey(KEY_UP);
    if (!strcmp(acttoken,"KEY_DOWN")) addKey(KEY_DOWN);
    if (!strcmp(acttoken,"KEY_LEFT")) addKey(KEY_LEFT);
    if (!strcmp(acttoken,"KEY_RIGHT")) addKey(KEY_RIGHT);
    if (!strcmp(acttoken,"KEY_ENTER")) addKey(KEY_ENTER);
    if (!strcmp(acttoken,"KEY_SPACE")) addKey(KEY_SPACE);
    if (!strcmp(acttoken,"KEY_ESC")) addKey(KEY_ESC);
    if (!strcmp(acttoken,"KEY_BACKSPACE")) addKey(KEY_BACKSPACE);
    if (!strcmp(acttoken,"KEY_TAB")) addKey(KEY_TAB);
    if (!strcmp(acttoken,"KEY_CAPS_LOCK")) addKey(KEY_CAPS_LOCK);
    if (!strcmp(acttoken,"KEY_F1")) addKey(KEY_F1);
    if (!strcmp(acttoken,"KEY_F2")) addKey(KEY_F2);
    if (!strcmp(acttoken,"KEY_F3")) addKey(KEY_F3);
    if (!strcmp(acttoken,"KEY_F4")) addKey(KEY_F4);
    if (!strcmp(acttoken,"KEY_F5")) addKey(KEY_F5);
    if (!strcmp(acttoken,"KEY_F6")) addKey(KEY_F6);
    if (!strcmp(acttoken,"KEY_F7")) addKey(KEY_F7);
    if (!strcmp(acttoken,"KEY_F8")) addKey(KEY_F8);
    if (!strcmp(acttoken,"KEY_F9")) addKey(KEY_F9);
    if (!strcmp(acttoken,"KEY_F10")) addKey(KEY_F10);
    if (!strcmp(acttoken,"KEY_F11")) addKey(KEY_F11);
    if (!strcmp(acttoken,"KEY_F12")) addKey(KEY_F12);
    if (!strcmp(acttoken,"KEY_INSERT")) addKey(KEY_INSERT);
    if (!strcmp(acttoken,"KEY_HOME")) addKey(KEY_HOME);
    if (!strcmp(acttoken,"KEY_PAGE_UP")) addKey(KEY_PAGE_UP);
    if (!strcmp(acttoken,"KEY_DELETE")) addKey(KEY_DELETE);
    if (!strcmp(acttoken,"KEY_END")) addKey(KEY_END);
    if (!strcmp(acttoken,"KEY_PAGE_DOWN")) addKey(KEY_PAGE_DOWN);

    if (!strcmp(acttoken,"KEY_A")) addKey(KEY_A);
    if (!strcmp(acttoken,"KEY_B")) addKey(KEY_B);
    if (!strcmp(acttoken,"KEY_C")) addKey(KEY_C);
    if (!strcmp(acttoken,"KEY_D")) addKey(KEY_D);
    if (!strcmp(acttoken,"KEY_E")) addKey(KEY_E);
    if (!strcmp(acttoken,"KEY_F")) addKey(KEY_F);
    if (!strcmp(acttoken,"KEY_G")) addKey(KEY_G);
    if (!strcmp(acttoken,"KEY_H")) addKey(KEY_H);
    if (!strcmp(acttoken,"KEY_I")) addKey(KEY_I);
    if (!strcmp(acttoken,"KEY_J")) addKey(KEY_J);
    if (!strcmp(acttoken,"KEY_K")) addKey(KEY_K);
    if (!strcmp(acttoken,"KEY_L")) addKey(KEY_L);
    if (!strcmp(acttoken,"KEY_M")) addKey(KEY_M);
    if (!strcmp(acttoken,"KEY_N")) addKey(KEY_N);
    if (!strcmp(acttoken,"KEY_O")) addKey(KEY_O);
    if (!strcmp(acttoken,"KEY_P")) addKey(KEY_P);
    if (!strcmp(acttoken,"KEY_Q")) addKey(KEY_Q);
    if (!strcmp(acttoken,"KEY_R")) addKey(KEY_R);
    if (!strcmp(acttoken,"KEY_S")) addKey(KEY_S);
    if (!strcmp(acttoken,"KEY_T")) addKey(KEY_T);
    if (!strcmp(acttoken,"KEY_U")) addKey(KEY_U);
    if (!strcmp(acttoken,"KEY_V")) addKey(KEY_V);
    if (!strcmp(acttoken,"KEY_W")) addKey(KEY_W);
    if (!strcmp(acttoken,"KEY_X")) addKey(KEY_X);
    if (!strcmp(acttoken,"KEY_Y")) addKey(KEY_Y);
    if (!strcmp(acttoken,"KEY_Z")) addKey(KEY_Z);
    if (!strcmp(acttoken,"KEY_1")) addKey(KEY_1);
    if (!strcmp(acttoken,"KEY_2")) addKey(KEY_2);
    if (!strcmp(acttoken,"KEY_3")) addKey(KEY_3);
    if (!strcmp(acttoken,"KEY_4")) addKey(KEY_4);
    if (!strcmp(acttoken,"KEY_5")) addKey(KEY_5);
    if (!strcmp(acttoken,"KEY_6")) addKey(KEY_6);
    if (!strcmp(acttoken,"KEY_7")) addKey(KEY_7);
    if (!strcmp(acttoken,"KEY_8")) addKey(KEY_8);
    if (!strcmp(acttoken,"KEY_9")) addKey(KEY_9);
    if (!strcmp(acttoken,"KEY_0")) addKey(KEY_0);
    
    acttoken = strtok(NULL," ");
  }
   
  #ifndef ARDUINO_PRO_MICRO     // for Teensy2.0++: send pressed keys at once
      Keyboard.set_modifier(modifiers);
      Keyboard.send_now();
      numKeys=0;
  #endif  
  
}

void addKey(uint8_t key)
{
  #ifdef ARDUINO_PRO_MICRO
     Keyboard.press(key);   // for Arduino Micro: press keys individually 
    
  #else    // for Teensy2.0++
  numKeys++;
  switch (numKeys) {
     case 1:  Keyboard.set_key1(key); break;
     case 2:  Keyboard.set_key2(key); break;
     case 3:  Keyboard.set_key3(key); break;
     case 4:  Keyboard.set_key4(key); break;
     case 5:  Keyboard.set_key5(key); break;
     case 6:  Keyboard.set_key6(key); break;
  }
  #endif
}


int freeRam ()
{
    extern int __heap_start, *__brkval;
    int v;
    return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}
