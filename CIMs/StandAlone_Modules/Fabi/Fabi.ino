
/* 
   Flexible Assistive Button Interface (FABI) Version 1.0  - AsTeRICS Academy 2015 - http://www.asterics-academy.net
      allows control of HID functions via AT-commands and/or momentary switches 
   

   requirements:  USB HID capable Arduino (Leonardo / Pro Micro resp. Clone ;-)
                  or Teensy 2.0++ with Teensyduino AddOn setup as USB composite device (Mouse + Keyboard + Serial)
                  Bounce2 library, see: https://github.com/thomasfredericks/Bounce2/wiki
       optional:  Momentary switches connected to GPIO pins (first pin: see START_BUTTON_PIN)
       
   
   Supported AT-commands  (via serial interface, 9600 baud): 
   
          AT                returns "OK"
          AT I              identification string will be returned (e.g. "FABI Version 1.0")
          AT M<num>         mode setting for a button (e.g. AT M 2 -> next command defines the new function for button 2)

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
   
          AT X<num>         move mouse in x direction (e.g. AT X 4 moves 4 pixels to the right)  
          AT Y<num>         move mouse in y direction (e.g. AT Y -10 moves 10 pixels up)  

          AT KW<text>       keyboard write text (e.g. AT KTHello! writes "Hello!")    
          AT KP<text>       key press: press/hold all keys identified in text 
                            (e.g. AT KP KEY_UP presses the "Cursor-Up" key, AT KP KEY_CTRL KEY_ALT KEY_DELETE presses all three keys)
                            for a list of supported Key idientifiers see below ! 
          AT KR             key release: releases all currently pressed keys (TBD: release individual keys ...)    

          AT D<num>         delay time setting for mouse movement (e.g. AT D 10 -> sets delay to 10 milliseconds) 
          AT S              save modes and settings to next free eeprom slot
          AT L<num>         load modes and settings from eeprom slot (e.g. AT L3 -> loads profile from slot 3)
          AT N              next mode will be loaded from eeprom slot (wrap around after last slot)
          AT -              delete EEPROM content (remove all stored slots)


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
#define DEBUG_OUTPUT

#include <EEPROM.h>
#include "Bounce2.h"        //  Bounce library used for button debouncing

// Constants and Macro definitions

#ifdef ARDUINO_PRO_MICRO
  #define START_BUTTON_PIN  2        // this is the first Pin to connect switches for Arduino Pro Micro
  #define LED_PIN           17       // the pin for the indication led
#else
  #define START_BUTTON_PIN  20       // this is the first Pin to connect switches for Teensy 2.0++
  #define LED_PIN           25       // the pin for the indication led
#endif

#define NUMBER_OF_BUTTONS  5         // number of connected switches
#define MAX_KEYSTRING_LEN 50         // maximum lenght for key identifiers / keyboard text
#define MAX_SLOTS         10         // maximum number of EEPROM memory slots

#define DEFAULT_WAIT_TIME       10   // wait time for one loop interation in milliseconds
#define DEFAULT_CLICK_TIME      8    // time for mouse click (loop iterations from press to release)
#define DOUBLECLICK_MULTIPLIER  5    // CLICK_TIME factor for double clicks
#define DEFAULT_DEBOUNCING_TIME 10   // debouncing interval for button-press / release

#define CMD_SETMODE            0     // command identifiers
#define CMD_MOUSE_CLICK        1
#define CMD_MOUSE_PRESS        2
#define CMD_MOUSE_RELEASE      3
#define CMD_MOUSE_WHEEL        4
#define CMD_MOUSE_MOVEX        5
#define CMD_MOUSE_MOVEY        6
#define CMD_KEYBOARD           7
#define CMD_SETDELAY           8
#define CMD_SAVE_SLOT          9
#define CMD_LOAD_SLOT         10
#define CMD_NEXT_SLOT         11
#define CMD_DELETE_SLOTS      12
#define CMD_PRINT_ID          13


// Global Variables

int clickTime=DEFAULT_CLICK_TIME;
int waitTime=DEFAULT_WAIT_TIME;

struct {                         // holds command and data for a button function 
  Bounce * bouncer;
  int mode;
  int value;
  char keystring[MAX_KEYSTRING_LEN];
} buttons [NUMBER_OF_BUTTONS];   // array for all buttons - defines one memory slot 

byte actButton=0;

byte leftMouseButton=0,old_leftMouseButton=0;
byte middleMouseButton=0,old_middleMouseButton=0;
byte rightMouseButton=0,old_rightMouseButton=0;

int leftClickRunning=0;
int rightClickRunning=0;
int middleClickRunning=0;
int doubleClickRunning=0;

uint8_t actSlot=1;
uint8_t numSlots=1;

int8_t moveX=0;       
int8_t moveY=0;       

int inByte=0;
char keystring[MAX_KEYSTRING_LEN];
char * writeKeystring=0;

unsigned long time=0;
int EmptySlotAddress = 0;

// Setup: program execution starts here

void setup() {
   Serial.begin(9600);
   #ifdef DEBUG_OUTPUT  
     Serial.println("Flexible Assistive Button Interface started !");
   #endif

   #ifdef ARDUINO_PRO_MICRO   // only needed for Arduino, automatically done for Teensy(duino)
     Mouse.begin();
     Keyboard.begin();
     TXLED1;
   #endif  

   pinMode(LED_PIN,OUTPUT);

   for (int i=0; i<NUMBER_OF_BUTTONS; i++)   // initialize button array
   {
      pinMode (START_BUTTON_PIN+i, INPUT_PULLUP);   // configure the pins for input mode with pullup resistors
      buttons[i].bouncer=new Bounce();
      buttons[i].bouncer->attach(START_BUTTON_PIN+i);
      buttons[i].bouncer->interval(DEFAULT_DEBOUNCING_TIME);
      buttons[0].mode=CMD_MOUSE_PRESS;              // default command for every button is left mouse click
      buttons[0].value='L';
      buttons[i].keystring[0]=0;
   }

   readFromEEPROM(0);  // read button modes from first EEPROM slot (if eeprom data is available) !  
   BlinkLed();
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
    
    delay(waitTime);  // to imit move movement speed. TBD: remove delay, use millis() !
}


void handlePress (int buttonIndex)   // a button was pressed
{   
    if (buttons[buttonIndex].mode == CMD_KEYBOARD)
      strcpy(keystring,buttons[buttonIndex].keystring);
    performCommand(buttons[buttonIndex].mode,buttons[buttonIndex].value,1);
}

void handleRelease (int buttonIndex)    // a button was released
{
    if (buttons[buttonIndex].mode == CMD_MOUSE_PRESS)
       performCommand(CMD_MOUSE_RELEASE,buttons[buttonIndex].value,1);
    else if (buttons[buttonIndex].mode == CMD_MOUSE_MOVEX)
      moveX=0;       
    else if (buttons[buttonIndex].mode == CMD_MOUSE_MOVEY)
      moveY=0;      
    else if ((buttons[buttonIndex].mode == CMD_KEYBOARD) && (buttons[buttonIndex].keystring[0]=='P'))
      releaseKeys(); 
}

void BlinkLed()
{
    for (uint8_t i=0; i < 5;i++)
    {
        digitalWrite (LED_PIN, !digitalRead(LED_PIN));
        delay(100);
    }
    digitalWrite (LED_PIN, HIGH);
}

// perform a command
//   cmd: command identifier
//   par1: optional numeric parameter
//   periodicMouseMovement: if true, mouse will continue moving - if false: only one movement
void performCommand (uint8_t cmd, int16_t par1, int8_t periodicMouseMovement)
{
  if (actButton != 0)
  {
      if (cmd!=CMD_KEYBOARD) keystring[0]=0;
      #ifdef DEBUG_OUTPUT  
        Serial.print("got new mode for button "); Serial.print(actButton);Serial.print(":");
        Serial.print(cmd);Serial.print(",");Serial.print(par1);Serial.print(",");Serial.println(keystring);
      #endif
      buttons[actButton-1].mode=cmd;
      buttons[actButton-1].value=par1;
      strcpy(buttons[actButton-1].keystring,keystring);
      actButton=0;
      BlinkLed();
      return;
  }
  
  switch(cmd) {
      case CMD_SETMODE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("set mode for button "); Serial.println(par1);
             #endif
             if ((par1>0) && (par1<=NUMBER_OF_BUTTONS))
                 actButton=par1;
             else  Serial.println("?");
          break;
      
      case CMD_MOUSE_CLICK:
             #ifdef DEBUG_OUTPUT  
               Serial.print("click "); Serial.write(par1); Serial.println();
             #endif
             if(par1=='L')
              { leftMouseButton=1;  leftClickRunning=clickTime; }
             else if(par1=='R')
              {   rightMouseButton=1; rightClickRunning=clickTime; }
             else if(par1=='D')
              {   leftMouseButton=1;  doubleClickRunning=clickTime*DOUBLECLICK_MULTIPLIER; }
             else if(par1=='M')
              {   middleMouseButton=1; middleClickRunning=clickTime; }
             else  Serial.println("?");
            break;
      case CMD_MOUSE_PRESS:
             #ifdef DEBUG_OUTPUT  
               Serial.print("press "); Serial.write(par1); Serial.println();
             #endif
             if (par1=='L') leftMouseButton=1; 
             else if (par1=='R') rightMouseButton=1; 
             else if (par1=='M') middleMouseButton=1; 
             else  Serial.println("?");
          break;
      case CMD_MOUSE_RELEASE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("release "); Serial.write(par1); Serial.println();
             #endif
             if (par1=='L') leftMouseButton=0; 
             else if (par1=='R') rightMouseButton=0; 
             else if (par1=='M') middleMouseButton=0; 
             else  Serial.println("?");
          break;
      case CMD_MOUSE_WHEEL:
             #ifdef DEBUG_OUTPUT  
               Serial.print("wheel "); Serial.write(par1); Serial.println();
             #endif
             #ifndef ARDUINO_PRO_MICRO
               if (par1=='U') Mouse.scroll(-1); 
               else if (par1=='D') Mouse.scroll(1); 
               else  Serial.println("?");
             #else
               if (par1=='U') Mouse.move (0,0,-1); 
               else if (par1=='D') Mouse.move (0,0,1);
               else  Serial.println("?");
             #endif
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
      case CMD_KEYBOARD:
             #ifdef DEBUG_OUTPUT  
               Serial.print("keyboard: "); Serial.println(keystring);
             #endif
             if (keystring[0]=='W')
                 writeKeystring=keystring+1;
             else if (keystring[0]=='P')
             {
                 strcat(keystring," ");
                 setKeyValues(keystring+1);
             }
             else if (keystring[0]=='R')
                 releaseKeys();             
             else  Serial.println("?");
          break;
      case CMD_SETDELAY:
             #ifdef DEBUG_OUTPUT  
               Serial.print("set delay: "); Serial.println(par1);
             #endif
             waitTime=par1;
          break;
      case CMD_SAVE_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("save slot!"); 
             #endif
             saveToEEPROM(); 
          break;
      case CMD_LOAD_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("load from slot: "); Serial.println(par1);
             #endif
             if ((par1>0) && (par1<=numSlots))
             {
               readFromEEPROM(par1-1);
               actSlot=par1;
             } 
             else  Serial.println("?");
          break;
      case CMD_NEXT_SLOT:
             actSlot++;
             if (actSlot>numSlots) actSlot=1;
             #ifdef DEBUG_OUTPUT  
               Serial.print("load next slot: "); Serial.println(actSlot);
             #endif
             readFromEEPROM(actSlot-1); 
          break;
      case CMD_DELETE_SLOTS:
             #ifdef DEBUG_OUTPUT  
               Serial.println("delete slots"); 
             #endif
             deleteSlots(); 
          break;
      case CMD_PRINT_ID:
             Serial.println("FABI Version 1.0"); 
          break;
  }
}

#define STATE_IDLE          0
#define STATE_GET_INT      20
#define STATE_GET_UINT     25
#define STATE_GET_KEY      30
#define STATE_GET_TEXT     35
#define STATE_GET_ENTER    40

void parseByte (int newByte)  // parse an incoming commandbyte from serial interface, perform command if valid
{
  static uint8_t state=STATE_IDLE;
  static int16_t num=0;
  static int8_t fact=1;
  static uint8_t cmd=0;
 
  digitalWrite (LED_PIN, !digitalRead(LED_PIN));

  if ((state!=STATE_GET_TEXT) || (num==0))  //only if not getting new text!
  {
     if (newByte==' ') return;   // skip spaces
     if ((newByte>='a') && (newByte <= 'z'))     // convert to uppercase
        newByte=newByte-'a'+'A';
  }

  switch (state) {
    case STATE_IDLE: if (newByte=='A') state++;
         break;
    case STATE_IDLE+1: if (newByte=='T') state++; else state=STATE_IDLE;
        break;
    case STATE_IDLE+2: switch (newByte) {
            case 10:
            case 13:  Serial.println("OK");  state=STATE_IDLE; break;
            case 'M': cmd=CMD_SETMODE; state=STATE_GET_UINT; break;
            case 'C': cmd=CMD_MOUSE_CLICK; state=STATE_GET_KEY; break;
            case 'P': cmd=CMD_MOUSE_PRESS; state=STATE_GET_KEY; break;
            case 'R': cmd=CMD_MOUSE_RELEASE; state=STATE_GET_KEY; break;
            case 'W': cmd=CMD_MOUSE_WHEEL; state=STATE_GET_KEY; break;
            case 'X': cmd=CMD_MOUSE_MOVEX; state=STATE_GET_INT; break;
            case 'Y': cmd=CMD_MOUSE_MOVEY; state=STATE_GET_INT; break;
            case 'K': cmd=CMD_KEYBOARD; state=STATE_GET_TEXT; break;
            case 'D': cmd=CMD_SETDELAY; state=STATE_GET_UINT; break;
            case 'S': cmd=CMD_SAVE_SLOT; state=STATE_GET_ENTER; break;
            case 'L': cmd=CMD_LOAD_SLOT; state=STATE_GET_UINT; break;
            case 'N': cmd=CMD_NEXT_SLOT; state=STATE_GET_ENTER; break;
            case '-': cmd=CMD_DELETE_SLOTS; state=STATE_GET_ENTER; break;
            case 'I': cmd=CMD_PRINT_ID; state=STATE_GET_ENTER; break;
            default: goto err;
          }
          break;
          
    case STATE_GET_UINT:
              if ((newByte >= '0') && (newByte<='9'))
              {  num=num*10+(newByte-'0'); }
              else if ((newByte == 10) || (newByte==13))
              { performCommand(cmd, num, 0); num=0; state=STATE_IDLE;}
              else goto err;
              break;

    case STATE_GET_INT:
              if (newByte == '-') fact=-1; 
              else if ((newByte >= '0') && (newByte<='9'))
              {  num=num*10+(newByte-'0'); }
              else if ((newByte == 10) || (newByte==13))
              { performCommand(cmd, num*fact, 0); num=0; fact=1; state=STATE_IDLE;}
              else goto err;
              break;
          
    case STATE_GET_ENTER:
              if ((newByte == 10) || (newByte==13))
              { performCommand(cmd, 0, 0); state=STATE_IDLE;}
              else goto err;
              break;
          
    case STATE_GET_KEY:
              if ((newByte != 10) && (newByte!=13))
                 num=newByte; 
              else {performCommand(cmd, num, 0); num=0; state=STATE_IDLE;}
              break;

    case STATE_GET_TEXT:
              if ((newByte == 10) || (newByte==13) || (num >= MAX_KEYSTRING_LEN-1))
              { keystring[num]=0; performCommand(cmd, num, 0); num=0; state=STATE_IDLE;}
              else {keystring[num++]=newByte;}
              break;
                            
    default: err: Serial.println("?");state=STATE_IDLE; num=0;
  }
}

void saveToEEPROM()
{
   uint8_t done;
   int address = EmptySlotAddress;

   EEPROM.write(address++,123);  // indicates valid eeprom content !
 
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
      
   #ifdef DEBUG_OUTPUT
     Serial.print(address); Serial.println(" bytes saved to EEPROM");
   #endif
}


void deleteSlots()
{
   int address=0;
   EmptySlotAddress=0;
   EEPROM.write(0,0);
   numSlots=1;
}


void readFromEEPROM(uint8_t slot)
{
   int address=0;
   uint8_t done;
   uint8_t slots=0;
   byte b;
   byte* p;
   
   while (EEPROM.read(address)==123)  // indicates valid eeprom content !
   {
     address++;
     for (int i=0;i<NUMBER_OF_BUTTONS;i++)
     {
        done=0;
        p = (byte*) &(buttons[i].mode);
        for (int t=0;(t<MAX_KEYSTRING_LEN+4)&&(!done);t++)
        {
          b=EEPROM.read(address++);
         //Serial.print("address:"); Serial.print(address-1);
         //Serial.print("value: ");Serial.println(b);
          if (slot==slots) *p++=b;         // copy to SRAM only if intended slot !
          if ((t>3) && (b==0)) done=1;     // skip rest of keystring when end detected !
        }
     }
     slots++;
   }
   
   EmptySlotAddress=address;
   #ifdef DEBUG_OUTPUT
     if (slots) {
       Serial.print(slots); Serial.print(" slots were read from EEPROM, occupying ");
       Serial.print(address); Serial.println(" bytes.");
     }
     else Serial.println("no data found in EEPROM.");
   #endif

   numSlots=slots;
   if (numSlots<1) numSlots=1;
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

int numKeys=0;

// press all supported keys 
// text is a string which contains the key identifiers eg. "KEY_CTRL KEY_C" for Ctrl-C
// TBD: improve ! add a real parser which iterated through key identifiers, add new commands e.g. WAIT etc...
void setKeyValues(char* text)
{
  int modifiers=0;
  numKeys=0;

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
  
      if (strstr(text,"KEY_SHIFT"))  addKey(KEY_LEFT_SHIFT);
      if (strstr(text,"KEY_CTRL"))  addKey(KEY_LEFT_CTRL);
      if (strstr(text,"KEY_ALT"))  addKey(KEY_RIGHT_ALT);
      if (strstr(text,"KEY_GUI"))  addKey(KEY_RIGHT_GUI);
      // if (strstr(text,"KEY_PRINTSCREEN")) addKey(KEY_PRINTSCREEN);
      
  #else     // for Teensy2.0++
      if (strstr(text,"KEY_SCROLL_LOCK")) addKey(KEY_SCROLL_LOCK);
      if (strstr(text,"KEY_PAUSE")) addKey(KEY_PAUSE);
      if (strstr(text,"KEY_NUM_LOCK")) addKey(KEY_NUM_LOCK);
      if (strstr(text,"KEY_PRINTSCREEN")) addKey(KEY_PRINTSCREEN);
      
      if (strstr(text,"KEY_SHIFT"))  modifiers|=MODIFIERKEY_SHIFT;
      if (strstr(text,"KEY_CTRL"))  modifiers|=MODIFIERKEY_CTRL;
      if (strstr(text,"KEY_ALT"))  modifiers|=MODIFIERKEY_ALT;
      if (strstr(text,"KEY_GUI"))  modifiers|=MODIFIERKEY_GUI;
  #endif

    if (strstr(text,"KEY_UP")) addKey(KEY_UP);
    if (strstr(text,"KEY_DOWN")) addKey(KEY_DOWN);
    if (strstr(text,"KEY_LEFT")) addKey(KEY_LEFT);
    if (strstr(text,"KEY_RIGHT")) addKey(KEY_RIGHT);
    if (strstr(text,"KEY_ENTER")) addKey(KEY_ENTER);
    if (strstr(text,"KEY_SPACE")) addKey(KEY_SPACE);
    if (strstr(text,"KEY_ESC")) addKey(KEY_ESC);
    if (strstr(text,"KEY_BACKSPACE")) addKey(KEY_BACKSPACE);
    if (strstr(text,"KEY_TAB")) addKey(KEY_TAB);
    if (strstr(text,"KEY_CAPS_LOCK")) addKey(KEY_CAPS_LOCK);
    if (strstr(text,"KEY_F1")) addKey(KEY_F1);
    if (strstr(text,"KEY_F2")) addKey(KEY_F2);
    if (strstr(text,"KEY_F3")) addKey(KEY_F3);
    if (strstr(text,"KEY_F4")) addKey(KEY_F4);
    if (strstr(text,"KEY_F5")) addKey(KEY_F5);
    if (strstr(text,"KEY_F6")) addKey(KEY_F6);
    if (strstr(text,"KEY_F7")) addKey(KEY_F7);
    if (strstr(text,"KEY_F8")) addKey(KEY_F8);
    if (strstr(text,"KEY_F9")) addKey(KEY_F9);
    if (strstr(text,"KEY_F10")) addKey(KEY_F10);
    if (strstr(text,"KEY_F11")) addKey(KEY_F11);
    if (strstr(text,"KEY_F12")) addKey(KEY_F12);
    if (strstr(text,"KEY_INSERT")) addKey(KEY_INSERT);
    if (strstr(text,"KEY_HOME")) addKey(KEY_HOME);
    if (strstr(text,"KEY_PAGE_UP")) addKey(KEY_PAGE_UP);
    if (strstr(text,"KEY_DELETE")) addKey(KEY_DELETE);
    if (strstr(text,"KEY_END")) addKey(KEY_END);
    if (strstr(text,"KEY_PAGE_DOWN")) addKey(KEY_PAGE_DOWN);

    if (strstr(text,"KEY_A ")) addKey(KEY_A);
    if (strstr(text,"KEY_B ")) addKey(KEY_B);
    if (strstr(text,"KEY_C ")) addKey(KEY_C);
    if (strstr(text,"KEY_D ")) addKey(KEY_D);
    if (strstr(text,"KEY_E ")) addKey(KEY_E);
    if (strstr(text,"KEY_F ")) addKey(KEY_F);
    if (strstr(text,"KEY_G ")) addKey(KEY_G);
    if (strstr(text,"KEY_H ")) addKey(KEY_H);
    if (strstr(text,"KEY_I ")) addKey(KEY_I);
    if (strstr(text,"KEY_J ")) addKey(KEY_J);
    if (strstr(text,"KEY_K ")) addKey(KEY_K);
    if (strstr(text,"KEY_L ")) addKey(KEY_L);
    if (strstr(text,"KEY_M ")) addKey(KEY_M);
    if (strstr(text,"KEY_N ")) addKey(KEY_N);
    if (strstr(text,"KEY_O ")) addKey(KEY_O);
    if (strstr(text,"KEY_P ")) addKey(KEY_P);
    if (strstr(text,"KEY_Q ")) addKey(KEY_Q);
    if (strstr(text,"KEY_R ")) addKey(KEY_R);
    if (strstr(text,"KEY_S ")) addKey(KEY_S);
    if (strstr(text,"KEY_T ")) addKey(KEY_T);
    if (strstr(text,"KEY_U ")) addKey(KEY_U);
    if (strstr(text,"KEY_V ")) addKey(KEY_V);
    if (strstr(text,"KEY_W ")) addKey(KEY_W);
    if (strstr(text,"KEY_X ")) addKey(KEY_X);
    if (strstr(text,"KEY_Y ")) addKey(KEY_Y);
    if (strstr(text,"KEY_Z ")) addKey(KEY_Z);
    if (strstr(text,"KEY_1 ")) addKey(KEY_1);
    if (strstr(text,"KEY_2 ")) addKey(KEY_2);
    if (strstr(text,"KEY_3 ")) addKey(KEY_3);
    if (strstr(text,"KEY_4 ")) addKey(KEY_4);
    if (strstr(text,"KEY_5 ")) addKey(KEY_5);
    if (strstr(text,"KEY_6 ")) addKey(KEY_6);
    if (strstr(text,"KEY_7 ")) addKey(KEY_7);
    if (strstr(text,"KEY_8 ")) addKey(KEY_8);
    if (strstr(text,"KEY_9 ")) addKey(KEY_9);
    if (strstr(text,"KEY_0 ")) addKey(KEY_0);
   
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

