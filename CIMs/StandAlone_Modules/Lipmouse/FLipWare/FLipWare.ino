  
/* 
   Flexible Assistive Button Interface (FABI) Version 2.0  - AsTeRICS Academy 2015 - http://www.asterics-academy.net
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

   supported key identifiers for key press command (AT KP):
 
    KEY_A   KEY_B   KEY_C   KEY_D    KEY_E   KEY_F   KEY_G   KEY_H   KEY_I   KEY_J    KEY_K    KEY_L
    KEY_M   KEY_N   KEY_O   KEY_P    KEY_Q   KEY_R   KEY_S   KEY_T   KEY_U   KEY_V    KEY_W    KEY_X 
    KEY_Y   KEY_Z   KEY_1   KEY_2    KEY_3   KEY_4   KEY_5   KEY_6   KEY_7   KEY_8    KEY_9    KEY_0
    KEY_F1  KEY_F2  KEY_F3  KEY_F4   KEY_F5  KEY_F6  KEY_F7  KEY_F8  KEY_F9  KEY_F10  KEY_F11  KEY_F12	
    
    KEY_RIGHT   KEY_LEFT       KEY_DOWN        KEY_UP      KEY_ENTER    KEY_ESC   KEY_BACKSPACE   KEY_TAB	
    KEY_HOME    KEY_PAGE_UP    KEY_PAGE_DOWN   KEY_DELETE  KEY_INSERT   KEY_END	  KEY_NUM_LOCK    KEY_SCROLL_LOCK
    KEY_SPACE   KEY_CAPS_LOCK  KEY_PAUSE       KEY_SHIFT   KEY_CTRL     KEY_ALT   KEY_GUI 

   optional commands for lipmouse module support (uses force sensors for mouse x/y movement and pressure sensor for sip/puff):

          AT LMON             lipmouse on
          AT LMOFF            lipmouse off
          AT LMSR             start reporting raw values
          AT LMER             end reporting raw values
          AT LMCA             start calibration
          AT LMAX <num>       acceleration x-axis 
          AT LMAY <num>       acceleration y-axis 
          AT LMDX <num>       deadzone x-axis 
          AT LMDY <num>       deadzone y-axis
          AT LMTS <num>       treshold sip action
          AT LMTP <num>       treshold puff action
    
*/

#include "fabi.h"        //  Bounce library used for button debouncing
#include <EEPROM.h>

// Constants and Macro definitions

#define DEFAULT_WAIT_TIME       10   // wait time for one loop interation in milliseconds
#define DEFAULT_CLICK_TIME      8    // time for mouse click (loop iterations from press to release)
#define DOUBLECLICK_MULTIPLIER  5    // CLICK_TIME factor for double clicks
#define DEFAULT_DEBOUNCING_TIME 10   // debouncing interval for button-press / release


#define PRESSURESTATE_IDLE 0
#define PRESSURESTATE_SIP  1
#define PRESSURESTATE_PUFF 2

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
    {1,1,13,2,3,1},           // default button pins for Lipmouse / Teensy
    25,                      // default Led pin for Lipmouse / Teensy
    1,                       // default mouse on
    3, 10, 10, 30, 30, 500, 530    // default values for acceleration, deadzone & thresholds (if lipmouse is used)
  }; 
#endif

unsigned long previousTime = 0;
float accumXpos = 0.f;
float accumYpos = 0.f;
int x;
int y;
int x_offset;
int y_offset;
uint8_t calib_now = 1;
uint8_t DebugOutput = DEFAULT_DEBUGLEVEL;
uint8_t pressureState = PRESSURESTATE_IDLE;
uint8_t pressureDebounce = 0;

struct buttonType buttons [NUMBER_OF_BUTTONS];   // array for all buttons - defines one memory slot 

int clickTime=DEFAULT_CLICK_TIME;
int waitTime=DEFAULT_WAIT_TIME;

uint8_t actButton=0;

uint8_t leftMouseButton=0,old_leftMouseButton=0;
uint8_t middleMouseButton=0,old_middleMouseButton=0;
uint8_t rightMouseButton=0,old_rightMouseButton=0;

int leftClickRunning=0;
int rightClickRunning=0;
int middleClickRunning=0;
int doubleClickRunning=0;

int8_t moveX=0;       
int8_t moveY=0;       

int cnt =0;


int inByte=0;

char * keystring=0;
char * writeKeystring=0;

unsigned long time=0;
int EmptySlotAddress = 0;

void handlePress (int buttonIndex);      // a button was pressed
void handleRelease (int buttonIndex);    // a button was released


// Setup: program execution starts here

void setup() {
   Serial.begin(9600);
   // delay(5000);
    //while (!Serial) ;
   
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.println("Flexible Assistive Button Interface started !");

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
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.print("Free RAM:");  Serial.println(freeRam());
}

// Loop: the main program loop

void loop() {  
    if (Serial.available() > 0) {
      // get incoming byte:
      inByte = Serial.read();
      parseByte (inByte);      // implemented in parser.cpp
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
      float timeDifference = (currentTime - previousTime)/5000.f;
      previousTime = currentTime;
      
      int pressure = analogRead(A0);
      int down = analogRead(45);
      int left = analogRead(43);
      int up = analogRead(44);
      int right = analogRead(42);
  
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
        if (y>settings.dy)
           accumYpos += (y-settings.dy)*settings.ay*timeDifference; 
        else if (y<-settings.dy)
           accumYpos += (y+settings.dy)*settings.ay*timeDifference; 

        if (x>settings.dx)
           accumXpos += (x-settings.dx)*settings.ax*timeDifference; 
        else if (x<-settings.dx)
           accumXpos += (x+settings.dx)*settings.ax*timeDifference; 
      
        int xMove = (int)accumXpos;
        int yMove = (int)accumYpos;
      
        Mouse.move(xMove, yMove);
      
        accumXpos -= xMove;
        accumYpos -= yMove;
      }
  
      if (pressureDebounce==0)
      {
        if (pressure < settings.ts)
        { 
           if (pressureState != PRESSURESTATE_SIP)
           {
              pressureState=PRESSURESTATE_SIP;
              handlePress(0); pressureDebounce=DEFAULT_DEBOUNCING_TIME;
           }
        }
        else
        {
           if (pressureState == PRESSURESTATE_SIP)
           {
              pressureState=PRESSURESTATE_IDLE;
              handleRelease(0); pressureDebounce=DEFAULT_DEBOUNCING_TIME;
           }
        }
  
        if (pressure > settings.tp)
        { 
           if (pressureState != PRESSURESTATE_PUFF)
           {
              pressureState=PRESSURESTATE_PUFF;
              handlePress(1); pressureDebounce=DEFAULT_DEBOUNCING_TIME;
           }
        }
        else
        {
           if (pressureState == PRESSURESTATE_PUFF)
           {
              pressureState=PRESSURESTATE_IDLE;
              handleRelease(1); pressureDebounce=DEFAULT_DEBOUNCING_TIME;
           }
        }
     } else pressureDebounce--;

     if (DebugOutput==DEBUG_LIVEREPORTS)
     { 
       if (cnt++ > 5)
       {  
          Serial.print("AT PR ");Serial.println(pressure);
          cnt=0;
       }
     }
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


// perform a command  (called from parser.cpp)
//   cmd: command identifier
//   par1: optional numeric parameter
//   periodicMouseMovement: if true, mouse will continue moving - if false: only one movement
void performCommand (uint8_t cmd, int16_t par1, char * keystring, int8_t periodicMouseMovement)
{
  if (actButton != 0)
  {
      if (DebugOutput==DEBUG_FULLOUTPUT)
      {  
        Serial.print("got new mode for button "); Serial.print(actButton);Serial.print(":");
        Serial.print(cmd);Serial.print(",");Serial.print(par1);Serial.print(",");Serial.println(keystring);
      }
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
             Serial.println("FABI Version 2.0"); 
          break;
      case CMD_BUTTON_MODE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("set mode for button "); Serial.println(par1);
             if ((par1>0) && (par1<=NUMBER_OF_BUTTONS))
                 actButton=par1;
             else  Serial.println("?");
          break;
      
      case CMD_MOUSE_CLICK_LEFT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("click left");
             leftMouseButton=1;  leftClickRunning=clickTime;
             break;
      case CMD_MOUSE_CLICK_RIGHT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("click right");
             rightMouseButton=1; rightClickRunning=clickTime;
             break;
      case CMD_MOUSE_CLICK_DOUBLE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("click double");
             leftMouseButton=1;  doubleClickRunning=clickTime*DOUBLECLICK_MULTIPLIER;
             break;
      case CMD_MOUSE_CLICK_MIDDLE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("click middle");
             middleMouseButton=1; middleClickRunning=clickTime;
            break;
      case CMD_MOUSE_PRESS_LEFT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("press left");
             leftMouseButton=1; 
             break;
      case CMD_MOUSE_PRESS_RIGHT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("press right");
             rightMouseButton=1; 
             break;
      case CMD_MOUSE_PRESS_MIDDLE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("press middle");
             middleMouseButton=1; 
             break;
      case CMD_MOUSE_RELEASE_LEFT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("release left");
             leftMouseButton=0;
             break; 
      case CMD_MOUSE_RELEASE_RIGHT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("release right");
             rightMouseButton=0;
             break; 
      case CMD_MOUSE_RELEASE_MIDDLE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("release middle");
             middleMouseButton=0;
             break; 
      case CMD_MOUSE_WHEEL_UP:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("wheel up");
             #ifndef ARDUINO_PRO_MICRO
               Mouse.scroll(-settings.ws); 
             #else
               Mouse.move (0,0,-settings.ws); 
             #endif
          break;
      case CMD_MOUSE_WHEEL_DOWN:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("wheel down");
             #ifndef ARDUINO_PRO_MICRO
               Mouse.scroll(settings.ws); 
             #else
               Mouse.move (0,0,settings.ws); 
             #endif
          break;
      case CMD_MOUSE_WHEEL_STEP:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("wheel step");
             settings.ws=par1;
          break;
      case CMD_MOUSE_MOVEX:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("mouse move x "); Serial.println(par1);
             Mouse.move(par1, 0);
             if (periodicMouseMovement) moveX=par1;
          break;
      case CMD_MOUSE_MOVEY:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("mouse move y "); Serial.println(par1);
             Mouse.move(0, par1);
             if (periodicMouseMovement) moveY=par1;
          break;
      case CMD_KEY_WRITE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("keyboard write: "); Serial.println(keystring);
             writeKeystring=keystring;
             break;
      case CMD_KEY_PRESS:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("key press: "); Serial.println(keystring);
             strcat(keystring," ");
             setKeyValues(keystring);
             break;
      case CMD_KEY_RELEASE:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("key release: ");
             releaseKeys();             
             break;
      case CMD_SAVE_SLOT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("save slot ");  Serial.println(keystring);
             saveToEEPROM(keystring); 
          break;
      case CMD_LOAD_SLOT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("load slot: "); Serial.println(keystring);
             readFromEEPROM(keystring);
          break;
      case CMD_LIST_SLOTS:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("list slots: ");
             listSlots();
          break;
      case CMD_NEXT_SLOT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("load next slot");
             readFromEEPROM(0); 
          break;
      case CMD_DELETE_SLOTS:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("delete slots"); 
             deleteSlots(); 
          break;


      case CMD_LM_ON:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse on");
             settings.mouseOn=1;
          break;
      case CMD_LM_OFF:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse off");
             settings.mouseOn=0;
          break;
      case CMD_LM_CA:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse calibrate");
             calib_now=1;
          break;
      case CMD_LM_AX:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse acc x");
             settings.ax=par1;
          break;
      case CMD_LM_AY:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse acc y");
             settings.ay=par1;
          break;
      case CMD_LM_DX:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse deadzone x");
             settings.dx=par1;
          break;
      case CMD_LM_DY:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse deadzone y");
             settings.dy=par1;
          break;
      case CMD_LM_TS:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse threshold sip");
             settings.ts=par1;
          break;
      case CMD_LM_TP:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("lipmouse threshold puff");
             settings.tp=par1;
          break;
      case CMD_LM_SR:
             DebugOutput=DEBUG_LIVEREPORTS;
          break;
      case CMD_LM_ER:
             DebugOutput=DEBUG_NOOUTPUT;
          break;
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


int freeRam ()
{
    extern int __heap_start, *__brkval;
    int v;
    return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}
