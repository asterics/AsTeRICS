/* 
   Flexible Assistive Button Interface (FABI) Version 1.0
   allows control of HID functions via AT-commands and/or momentary switches 

   requirements:  Teensy 2.0++ microcontroller + Teensyduino
                  setup as USB composite devices (Mouse + Keyboard + Joystick + Serial)
   optional:      Momentary switches connected to GPIO pins (first pin: see START_BUTTON_PIN)
   
   Supported AT-commands  (via serial interface, 9600 baud): 
   
          AT                returns "OK"
         
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
                            for a list of supported Key idientifiers see function setKeyValues() 
          AT KR             key release: releases all currently pressed keys (TBD: release individual keys ...)    

          AT M<num>         mode setting for a button (e.g. AT M 2 -> next command defines the new function for button 2)
          AT D<num>         delay time setting for mouse movement (e.g. AT D 10 -> sets delay to 10 milliseconds)
 
          AT S              save modes and settings to next free eeprom slot
          AT L<num>         load modes and settings from eeprom slot (e.g. AT L3 -> loads profile from slot 3)
          AT N              next mode will be loaded from eeprom slot (wrap around after last slot)
          AT -              delete EEPROM content (remove all stored slots)
 
          AT I              identification string will be returned (e.g. "FABI Version 1.0")
          
*/

#include <Bounce.h>
#include <EEPROM.h>

#define DEBUG_OUTPUT

#define START_BUTTON_PIN  20       // this is the first Pin to connect switches
#define NUMBER_OF_BUTTONS  5       // number of connected switches
#define LED_PIN           25       // the pin for the indication led

#define MAX_KEYSTRING_LEN 50
#define MAX_SLOTS         10

#define DEFAULT_CLICK_TIME    20
#define DEFAULT_WAIT_TIME     10
#define CMD_SETMODE            0

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

#define STATE_IDLE          0
#define STATE_GET_INT      20
#define STATE_GET_UINT     25
#define STATE_GET_KEY      30
#define STATE_GET_TEXT     35
#define STATE_GET_ENTER    40

int clickTime=DEFAULT_CLICK_TIME;
int waitTime=DEFAULT_WAIT_TIME;

struct {
  Bounce * bouncer;
  int mode;
  int value;
  char keystring[MAX_KEYSTRING_LEN];
} buttons [NUMBER_OF_BUTTONS]; 

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



void setup() {
   Serial.begin(9600);
   #ifdef DEBUG_OUTPUT  
     Serial.println("Flexible Assistive Button Interface started !");
   #endif

   pinMode(LED_PIN,OUTPUT);

   for (int i=0; i<NUMBER_OF_BUTTONS; i++)
   {
      pinMode (START_BUTTON_PIN+i, INPUT_PULLUP);   // Configure the pins for input mode with pullup resistors.
      buttons[i].bouncer=new Bounce(START_BUTTON_PIN+i,10);
      buttons[0].mode=CMD_MOUSE_PRESS;
      buttons[0].value='L';
      buttons[i].keystring[0]=0;
   }

   readFromEEPROM(0);  // init button modes from slot 0 (if eeprom data is available) !  
   blinkButton();
}


void loop() {  

    if (Serial.available() > 0) {
      // get incoming byte:
      inByte = Serial.read();
      parseByte (inByte);
    }
  
    for (int i=0;i<NUMBER_OF_BUTTONS;i++)
    {
      buttons[i].bouncer->update();
  
      if (buttons[i].bouncer->fallingEdge()) 
         handlePress(i);
      
      else if (buttons[i].bouncer->risingEdge())
         handleRelease(i);
    }
      
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
  
  
    if ((moveX!=0) || (moveY!=0))  
         Mouse.move(moveX, moveY);
 
    // if any changes were made, update the Mouse buttons
    if((leftMouseButton!=old_leftMouseButton) ||
       (middleMouseButton!=old_middleMouseButton) ||
       (rightMouseButton!=old_rightMouseButton))  {
           Mouse.set_buttons(leftMouseButton, middleMouseButton, rightMouseButton);
           old_leftMouseButton=leftMouseButton;
           old_middleMouseButton=middleMouseButton;
           old_rightMouseButton=rightMouseButton;
    }
    
    if (writeKeystring)
    {
        Keyboard.print(writeKeystring);
        writeKeystring=0;
    }    
    
    delay(waitTime);
}


void handlePress (int buttonIndex)
{   
    if (buttons[buttonIndex].mode == CMD_KEYBOARD)
      strcpy(keystring,buttons[buttonIndex].keystring);
    performCommand(buttons[buttonIndex].mode,buttons[buttonIndex].value,1);
}

void handleRelease (int buttonIndex)
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

void blinkButton()
{
    for (uint8_t i=0; i < 5;i++)
    {
        digitalWrite (LED_PIN, !digitalRead(LED_PIN));
        delay(100);
    }
    digitalWrite (LED_PIN, HIGH);
}


void performCommand (uint8_t cmd, int16_t num, int8_t manually)
{
  if (actButton != 0)
  {
      if (cmd!=CMD_KEYBOARD) keystring[0]=0;
      #ifdef DEBUG_OUTPUT  
        Serial.print("got new mode for button "); Serial.print(actButton);Serial.print(":");
        Serial.print(cmd);Serial.print(",");Serial.print(num);Serial.print(",");Serial.println(keystring);
      #endif
      buttons[actButton-1].mode=cmd;
      buttons[actButton-1].value=num;
      strcpy(buttons[actButton-1].keystring,keystring);
      actButton=0;
      blinkButton();
      return;
  }
  
  switch(cmd) {
      case CMD_SETMODE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("set mode for button "); Serial.println(num);
             #endif
             if ((num>0) && (num<=NUMBER_OF_BUTTONS))
                 actButton=num;
             else  Serial.println("?");
          break;
      
      case CMD_MOUSE_CLICK:
             #ifdef DEBUG_OUTPUT  
               Serial.print("click "); Serial.write(num); Serial.println();
             #endif
             if(num=='L')
              { leftMouseButton=1;  leftClickRunning=clickTime; }
             else if(num=='R')
              {   rightMouseButton=1; rightClickRunning=clickTime; }
             else if(num=='D')
              {   leftMouseButton=1;  doubleClickRunning=clickTime*3; }
             else if(num=='M')
              {   middleMouseButton=1; middleClickRunning=clickTime; }
             else  Serial.println("?");
            break;
      case CMD_MOUSE_PRESS:
             #ifdef DEBUG_OUTPUT  
               Serial.print("press "); Serial.write(num); Serial.println();
             #endif
             if (num=='L') leftMouseButton=1; 
             else if (num=='R') rightMouseButton=1; 
             else if (num=='M') middleMouseButton=1; 
             else  Serial.println("?");
          break;
      case CMD_MOUSE_RELEASE:
             #ifdef DEBUG_OUTPUT  
               Serial.print("release "); Serial.write(num); Serial.println();
             #endif
             if (num=='L') leftMouseButton=0; 
             else if (num=='R') rightMouseButton=0; 
             else if (num=='M') middleMouseButton=0; 
             else  Serial.println("?");
          break;
      case CMD_MOUSE_WHEEL:
             #ifdef DEBUG_OUTPUT  
               Serial.print("wheel "); Serial.write(num); Serial.println();
             #endif
             if (num=='U') Mouse.scroll(-1); 
             else if (num=='D') Mouse.scroll(1); 
             else  Serial.println("?");
          break;
      case CMD_MOUSE_MOVEX:
             #ifdef DEBUG_OUTPUT  
               Serial.print("mouse move x "); Serial.println(num);
             #endif
             Mouse.move(num, 0);
             if (manually) moveX=num;
          break;
      case CMD_MOUSE_MOVEY:
             #ifdef DEBUG_OUTPUT  
               Serial.print("mouse move y "); Serial.println(num);
             #endif
             Mouse.move(0, num);
             if (manually) moveY=num;
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
               Serial.print("set delay: "); Serial.println(num);
             #endif
             waitTime=num;
          break;
      case CMD_SAVE_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("save slot!"); 
             #endif
             saveToEEPROM(); 
          break;
      case CMD_LOAD_SLOT:
             #ifdef DEBUG_OUTPUT  
               Serial.print("load from slot: "); Serial.println(num);
             #endif
             if ((num>0) && (num<=numSlots))
             {
               readFromEEPROM(num-1);
               actSlot=num;
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

void parseByte (int newByte)
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
      
   #ifdef DEBUG_OUTPUT
     Serial.print(address); Serial.println(" bytes saved to EEPROM");
   #endif
}


void deleteSlots()
{
   int address=0;
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

void releaseKeys()
{
   Keyboard.set_modifier(0);
   Keyboard.set_key1(0);
   Keyboard.set_key2(0);
   Keyboard.set_key3(0);
   Keyboard.set_key4(0);
   Keyboard.set_key5(0);
   Keyboard.set_key6(0);
   Keyboard.send_now();
}

int numKeys=0;

void setKeyValues(char* text)
{
  int modifiers=0;
  numKeys=0;
   
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
 
  if (strstr(text,"KEY_UP")) addKey(KEY_UP);
  if (strstr(text,"KEY_DOWN")) addKey(KEY_DOWN);
  if (strstr(text,"KEY_LEFT")) addKey(KEY_LEFT);
  if (strstr(text,"KEY_RIGHT")) addKey(KEY_RIGHT);
  if (strstr(text,"KEY_ENTER")) addKey(KEY_ENTER);
  if (strstr(text,"KEY_ESC")) addKey(KEY_ESC);
  if (strstr(text,"KEY_BACKSPACE")) addKey(KEY_BACKSPACE);
  if (strstr(text,"KEY_TAB")) addKey(KEY_TAB);
  if (strstr(text,"KEY_SPACE")) addKey(KEY_SPACE);
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
  if (strstr(text,"KEY_PRINTSCREEN")) addKey(KEY_PRINTSCREEN);
  if (strstr(text,"KEY_SCROLL_LOCK")) addKey(KEY_SCROLL_LOCK);
  if (strstr(text,"KEY_PAUSE")) addKey(KEY_PAUSE);
  if (strstr(text,"KEY_INSERT")) addKey(KEY_INSERT);
  if (strstr(text,"KEY_HOME")) addKey(KEY_HOME);
  if (strstr(text,"KEY_PAGE_UP")) addKey(KEY_PAGE_UP);
  if (strstr(text,"KEY_DELETE")) addKey(KEY_DELETE);
  if (strstr(text,"KEY_END")) addKey(KEY_END);
  if (strstr(text,"KEY_PAGE_DOWN")) addKey(KEY_PAGE_DOWN);
  if (strstr(text,"KEY_NUM_LOCK")) addKey(KEY_NUM_LOCK);
  
  if (strstr(text,"KEY_SHIFT"))  modifiers|=MODIFIERKEY_SHIFT;
  if (strstr(text,"KEY_CTRL"))  modifiers|=MODIFIERKEY_CTRL;
  if (strstr(text,"KEY_ALT"))  modifiers|=MODIFIERKEY_ALT;
  if (strstr(text,"KEY_GUI"))  modifiers|=MODIFIERKEY_GUI;

  Keyboard.set_modifier(modifiers);
  Keyboard.send_now();
  numKeys=0;
}

void addKey(uint8_t key)
{
  numKeys++;
  switch (numKeys) {
     case 1:  Keyboard.set_key1(key); break;
     case 2:  Keyboard.set_key2(key); break;
     case 3:  Keyboard.set_key3(key); break;
     case 4:  Keyboard.set_key4(key); break;
     case 5:  Keyboard.set_key5(key); break;
     case 6:  Keyboard.set_key6(key); break;
  }
}

