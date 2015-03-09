
#include "fabi.h"

int numKeys=0;
char tmptxt[MAX_KEYSTRING_LEN];   // for parsing keystrings

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

void releaseKeys()  // releases all previously pressed keys
{
  if (DebugOutput==DEBUG_FULLOUTPUT)  
    Serial.println("key release");

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


