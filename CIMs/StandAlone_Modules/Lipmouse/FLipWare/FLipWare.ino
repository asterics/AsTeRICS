  
/* 
     FLipWare - built upon Assistive Button Interface (FABI) Version 2.0  - AsTeRICS Academy 2015 - http://www.asterics-academy.net
      allows control of HID functions via FLipmouse module and/or AT-commands  
   

   requirements:  USB HID capable Arduino (Leonardo / Micro / Pro Micro)
                  or Teensy 2.0++ with Teensyduino AddOn setup as USB composite device (Mouse + Keyboard + Serial)
       optional:  Momentary switches connected to GPIO pins / force sensors connected to ADC pins
       
   
   Supported AT-commands:  
   (sent via serial interface, use spaces between parameters and Enter (<cr>, ASCII-code 0x0d) to finish a command)
   
          AT                returns "OK"
          AT ID             identification string will be returned (e.g. "FLipMouse V2.0")
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
                            
          AT KR <text>      key release: releases all keys identified in text    
          AT RA             release all: releases all currently pressed keys and buttons    
          
          AT SAVE <name>    save settings and current button modes to next free eeprom slot under given name (e.g. AT SAVE mouse1)
          AT LOAD <name>    load button modes from eeprom slot (e.g. AT LOAD mouse1 -> loads profile named "mouse1")
          AT LIST           list all saved mode names 
          AT NEXT           next mode will be loaded (wrap around after last slot)
          AT CLEAR          clear EEPROM content (delete all stored slots)
          AT IDLE           idle command (no operation)
          
    FLipMouse-specific commands:

          AT MM           mouse movements on, alternative functions off
          AT AF           alternative functions on, mouse movements off 
          AT SW           switch between mouse movement and alternative functions
          AT SR           start reporting raw values
          AT ER           end reporting raw values
          AT CA           calibration of zeropoint
          AT AX <num>     acceleration x-axis 
          AT AY <num>     acceleration y-axis 
          AT DX <num>     deadzone x-axis 
          AT DY <num>     deadzone y-axis
          AT TS <num>     treshold sip action
          AT TP <num>     treshold puff action
          AT TT <num>     treshold short/long sip or puff (milliseconds)

   supported key identifiers for key press command (AT KP):
 
    KEY_A   KEY_B   KEY_C   KEY_D    KEY_E   KEY_F   KEY_G   KEY_H   KEY_I   KEY_J    KEY_K    KEY_L
    KEY_M   KEY_N   KEY_O   KEY_P    KEY_Q   KEY_R   KEY_S   KEY_T   KEY_U   KEY_V    KEY_W    KEY_X 
    KEY_Y   KEY_Z   KEY_1   KEY_2    KEY_3   KEY_4   KEY_5   KEY_6   KEY_7   KEY_8    KEY_9    KEY_0
    KEY_F1  KEY_F2  KEY_F3  KEY_F4   KEY_F5  KEY_F6  KEY_F7  KEY_F8  KEY_F9  KEY_F10  KEY_F11  KEY_F12	
    
    KEY_RIGHT   KEY_LEFT       KEY_DOWN        KEY_UP      KEY_ENTER    KEY_ESC   KEY_BACKSPACE   KEY_TAB	
    KEY_HOME    KEY_PAGE_UP    KEY_PAGE_DOWN   KEY_DELETE  KEY_INSERT   KEY_END	  KEY_NUM_LOCK    KEY_SCROLL_LOCK
    KEY_SPACE   KEY_CAPS_LOCK  KEY_PAUSE       KEY_SHIFT   KEY_CTRL     KEY_ALT   KEY_RIGHT_ALT   KEY_GUI 
    KEY_RIGHT_GUI
    
*/

#include "fabi.h"        //  Bounce library used for button debouncing
#include <EEPROM.h>

// Constants and Macro definitions

#define PRESSURE_SENSOR_PIN A0
#define DOWN_SENSOR_PIN     45
#define LEFT_SENSOR_PIN     43
#define UP_SENSOR_PIN       44
#define RIGHT_SENSOR_PIN    42

#define DEFAULT_WAIT_TIME       5   // wait time for one loop interation in milliseconds
#define DEFAULT_CLICK_TIME      8    // time for mouse click (loop iterations from press to release)
#define DOUBLECLICK_MULTIPLIER  5    // CLICK_TIME factor for double clicks
#define DEFAULT_DEBOUNCING_TIME 7   // debouncing interval for button-press / release

#define UP_BUTTON       3     // index numbers of the virtual buttons (not pin numbers)
#define DOWN_BUTTON     4
#define LEFT_BUTTON     5
#define RIGHT_BUTTON    6
#define SIP_BUTTON      7
#define LONGSIP_BUTTON  8
#define PUFF_BUTTON     9
#define LONGPUFF_BUTTON 10


// global variables

#ifdef TEENSY
  int8_t  input_map[NUMBER_OF_PHYSICAL_BUTTONS]={13,2,3};  //  mapa physical button pins to button index 0,1,2  
  int8_t  led_map[NUMBER_OF_LEDS]={18,19,29};              //  maps leds pins   
  uint8_t LED_PIN = 25;                                    //  Led output pin
#endif

#ifdef ARDUINO_PRO_MICRO
  int8_t  input_map[NUMBER_OF_PHYSICAL_BUTTONS]={2,3,4};
  int8_t  led_map[NUMBER_OF_LEDS]={8,9,10};            
  uint8_t LED_PIN = 17;
#endif

struct settingsType settings = {         // type definition see fabi.h
    1,                                   //  Mouse cursor movement active (not the alternative functions )
    3, 10, 10, 30, 30, 500, 525, 1000    // wheel step, accx, accy, deadzone x, deadzone y, threshold sip, threshold puff, threshold time (short/longpress)
}; 

struct buttonType buttons [NUMBER_OF_BUTTONS];   // array for all buttons - type definition see fabi.h 

uint8_t calib_now = 1;                           // calibrate zeropoint right at startup !
uint8_t DebugOutput = DEFAULT_DEBUGLEVEL;        // default: very chatty at the serial interface ...
int clickTime=DEFAULT_CLICK_TIME;
int waitTime=DEFAULT_WAIT_TIME;

int EmptySlotAddress = 0;

unsigned long currentTime;
unsigned long previousTime = 0;
float timeDifference;
uint32_t timeStamp = 0;
unsigned long time=0;

uint8_t actButton=0;
uint8_t actSlot=0;

int pressure;
int down;
int left;
int up;
int right;
float accumXpos = 0.f;
float accumYpos = 0.f;
float accelFactor;
int x;
int y;
int x_offset;
int y_offset;

int8_t moveX=0;       
int8_t moveY=0;       
uint8_t leftMouseButton=0,old_leftMouseButton=0;
uint8_t middleMouseButton=0,old_middleMouseButton=0;
uint8_t rightMouseButton=0,old_rightMouseButton=0;
uint8_t leftClickRunning=0;
uint8_t rightClickRunning=0;
uint8_t middleClickRunning=0;
uint8_t doubleClickRunning=0;

int inByte=0;
char * keystring=0;
char * writeKeystring=0;
uint8_t cnt =0,cnt2=0;


// function declarations 
void handlePress (int buttonIndex);      // a button was pressed
void handleRelease (int buttonIndex);    // a button was released
uint32_t handleButton(int i, uint8_t b);  // button debouncing
void UpdateLeds();

////////////////////////////////////////
// Setup: program execution starts here
////////////////////////////////////////

void setup() {
   Serial.begin(9600);
    //while (!Serial) ;
   
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.println("Flexible Assistive Button Interface started !");

   #ifdef ARDUINO_PRO_MICRO   // only needed for Arduino, automatically done for Teensy(duino)
     Mouse.begin();
     Keyboard.begin();
     TXLED1;
   #endif  

   #ifdef LIPMOUSE_V0     // only needed for first lipmouse hardware version
     pinMode(21,OUTPUT);
     pinMode(22,OUTPUT);  
     digitalWrite(21,HIGH);  // supply voltage for pressure sensor
     digitalWrite(22,LOW);
   #endif


   pinMode(LED_PIN,OUTPUT);

   for (int i=0; i<NUMBER_OF_PHYSICAL_BUTTONS; i++)   // initialize physical buttons and bouncers
      pinMode (input_map[i], INPUT_PULLUP);   // configure the pins for input mode with pullup resistors

   for (int i=0; i<NUMBER_OF_LEDS; i++)   // initialize physical buttons and bouncers
      pinMode (led_map[i], OUTPUT);   // configure the pins for input mode with pullup resistors

   for (int i=0; i<NUMBER_OF_BUTTONS; i++)   // initialize button array
   {
      buttons[i].bounceState=0;
      buttons[i].stableState=0;
      buttons[i].bounceCount=0;
      buttons[i].longPressed=0;
      buttons[i].mode=CMD_MOUSE_PRESS_LEFT;              // default command for every button is left mouse click
      buttons[i].value='L';
      buttons[i].keystring[0]=0;
   }
   
   readFromEEPROM(0);  // read button modes from first EEPROM slot if available !  

   BlinkLed();
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.print("Free RAM:");  Serial.println(freeRam());
}

///////////////////////////////
// Loop: the main program loop
///////////////////////////////

void loop() {  

      currentTime = millis();
      timeDifference = currentTime - previousTime;
      previousTime = currentTime;
      accelFactor= timeDifference / 10000.0f;      

      pressure = analogRead(PRESSURE_SENSOR_PIN);
      up = analogRead(UP_SENSOR_PIN);
      down = analogRead(DOWN_SENSOR_PIN);
      left = analogRead(LEFT_SENSOR_PIN);
      right = analogRead(RIGHT_SENSOR_PIN);

      if (DebugOutput==DEBUG_LIVEREPORTS)   { 
        if (cnt++ > 10) {                    // report raw values !
          Serial.print("AT RR ");Serial.print(pressure);Serial.print(",");
          Serial.print(up);Serial.print(",");Serial.print(down);Serial.print(",");
          Serial.print(left);Serial.print(",");Serial.println(right);
          cnt=0;
        }
      }
      
      while (Serial.available() > 0) {
        // get incoming byte:
        inByte = Serial.read();
        parseByte (inByte);      // implemented in parser.cpp
      }
    
      for (int i=0;i<NUMBER_OF_PHYSICAL_BUTTONS;i++)    // update button press / release events
          handleButton(i, -1, digitalRead(input_map[i]) == LOW ? 1 : 0);

      handleButton(SIP_BUTTON, LONGSIP_BUTTON, pressure < settings.ts ? 1 : 0); 
      handleButton(PUFF_BUTTON, LONGPUFF_BUTTON, pressure > settings.tp ? 1 : 0);
    
        
      // handle mouse movement
      if (calib_now == 1)  {
         x_offset = (left-right);
         y_offset = (up-down);
         calib_now=0;
      }    
      else  {
          x = (left-right) - x_offset;
          y = (up-down) - y_offset;
      }
      
      if (abs(x)< settings.dx) x=0;
      if (abs(y)< settings.dy) y=0;
      
      if (settings.mouseOn == 1) {
        if (y>settings.dy)
           accumYpos += (float)((y-settings.dy)*settings.ay) * accelFactor; 
        else if (y<-settings.dy)
           accumYpos += (float)((y+settings.dy)*settings.ay) * accelFactor; 

        if (x>settings.dx)
           accumXpos += (float)((x-settings.dx)*settings.ax) * accelFactor; 
        else if (x<-settings.dx)
           accumXpos += (float)((x+settings.dx)*settings.ax) * accelFactor; 
      
        int xMove = (int)accumXpos;
        int yMove = (int)accumYpos;
        Mouse.move(xMove, yMove);
        accumXpos -= xMove;
        accumYpos -= yMove;
      }
      else  { // handle alternative joystick actions
        handleButton(UP_BUTTON, -1, y<-settings.dy ? 1 : 0);
        handleButton(DOWN_BUTTON, -1, y>settings.dy ? 1 : 0);
        handleButton(LEFT_BUTTON, -1, x<-settings.dx ? 1 : 0);
        handleButton(RIGHT_BUTTON, -1, x>settings.dx ? 1 : 0);
      }

      if ((moveX!=0) || (moveY!=0))   // movement induced by button actions  
      {
        if (cnt2++%4==0)
          Mouse.move(moveX, moveY);
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
 
      // if any changes were made, update the Mouse buttons
      if(leftMouseButton!=old_leftMouseButton) {
         if (leftMouseButton) Mouse.press(MOUSE_LEFT); else Mouse.release(MOUSE_LEFT);
         old_leftMouseButton=leftMouseButton;
      }
      if  (middleMouseButton!=old_middleMouseButton) {
         if (middleMouseButton) Mouse.press(MOUSE_MIDDLE); else Mouse.release(MOUSE_MIDDLE);
         old_middleMouseButton=middleMouseButton;
      }
      if  (rightMouseButton!=old_rightMouseButton)  {
         if (rightMouseButton) Mouse.press(MOUSE_RIGHT); else Mouse.release(MOUSE_RIGHT);
         old_rightMouseButton=rightMouseButton;
     }
    
     // handle Keyboard output (single key press/release is done seperately via setKeyValues() ) 
     if (writeKeystring) {
        Keyboard.print(writeKeystring);
        writeKeystring=0;
    }    
       
    UpdateLeds();
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
     case CMD_KEY_PRESS: releaseKeys(buttons[buttonIndex].keystring); break; 
   }
}

void handleButton(int i, int l, uint8_t state)    // button debouncing and longpress detection  
{                                                 //   (if button i is pressed long and index l>=0, virtual button l is activated !)
   if ( buttons[i].bounceState == state) {
     if (buttons[i].bounceCount < DEFAULT_DEBOUNCING_TIME) {
       buttons[i].bounceCount++;
       if (buttons[i].bounceCount == DEFAULT_DEBOUNCING_TIME) {
          if (state != buttons[i].stableState)
          { 
            buttons[i].stableState=state;
            if (state == 1) { 
              handlePress(i); 
              buttons[i].timestamp=currentTime;
            }
            else {
              if (buttons[i].longPressed)
              {
                 buttons[i].longPressed=0;
                 handleRelease(l);
              }
              else handleRelease(i);  
            }
          }
       }
     }
     else { 
       if ((currentTime-buttons[i].timestamp > settings.tt) && (l>=0))
       {
            if ((state == 1) && (buttons[i].longPressed==0) && (buttons[l].mode!=CMD_IDLE)) {
           buttons[i].longPressed=1; 
           handleRelease(i);
           handlePress(l);
          }
       }
     }
   }
   else {
     buttons[i].bounceState = state;
     buttons[i].bounceCount=0;     
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
        BlinkLed();
      }
      buttons[actButton-1].mode=cmd;
      buttons[actButton-1].value=par1;
      if (keystring==0) buttons[actButton-1].keystring[0]=0;
      else strcpy(buttons[actButton-1].keystring,keystring);
      actButton=0;
      return;
  }
  
  switch(cmd) {
      case CMD_PRINT_ID:
             Serial.println("FLipMouse V2.0"); 
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
               Serial.print("key release: ");  Serial.println(keystring);
             strcat(keystring," ");
             releaseKeys(keystring);             
             break;
      case CMD_RELEASE_ALL:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.print("release all");
             release_all();             
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


      case CMD_MM:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("mouse function on");
             settings.mouseOn=1;
          break;
      case CMD_AF:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("alternative functions on");
             settings.mouseOn=0;
          break;
      case CMD_SW:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("switch mouse / alternative function");
             if (settings.mouseOn==0) settings.mouseOn=1;
             else settings.mouseOn=0;
          break;
      case CMD_CA:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("start calibration");
             calib_now=1;
          break;
      case CMD_AX:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set acc x");
             settings.ax=par1;
          break;
      case CMD_AY:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set acc y");
             settings.ay=par1;
          break;
      case CMD_DX:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set deadzone x");
             settings.dx=par1;
          break;
      case CMD_DY:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set deadzone y");
             settings.dy=par1;
          break;
      case CMD_TS:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set threshold sip");
             settings.ts=par1;
          break;
      case CMD_TP:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set threshold puff");
             settings.tp=par1;
          break;
      case CMD_SR:
             DebugOutput=DEBUG_LIVEREPORTS;
          break;
      case CMD_ER:
             DebugOutput=DEBUG_NOOUTPUT;
          break;
      case CMD_TT:
             if (DebugOutput==DEBUG_FULLOUTPUT)  
               Serial.println("set time threshold");
             settings.tt=par1;
          break;
  }
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

void UpdateLeds()
{  
   if (actSlot & 1) digitalWrite (led_map[0],LOW); else digitalWrite (led_map[0],HIGH); 
   if (actSlot & 2) digitalWrite (led_map[1],LOW); else digitalWrite (led_map[1],HIGH); 
   if (actSlot & 4) digitalWrite (led_map[2],LOW); else digitalWrite (led_map[2],HIGH); 
}


int freeRam ()
{
    extern int __heap_start, *__brkval;
    int v;
    return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}
