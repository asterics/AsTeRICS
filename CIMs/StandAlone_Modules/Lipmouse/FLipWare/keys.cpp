
#include "fabi.h"

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

#define KEY_ADD     0
#define KEY_RELEASE 1

int keyAction=KEY_ADD;
char tmptxt[MAX_KEYSTRING_LEN];   // for parsing keystrings

void updateKey(int key)
{
   if (keyAction==KEY_ADD)
      Keyboard.press(key);     // press keys individually   
   else
      Keyboard.release(key);   // release keys individually    
}


void release_all()  // releases all previously pressed keys
{
    Keyboard.releaseAll();
}

void releaseKeys (char * text)
{
   keyAction=KEY_RELEASE; 
   setKeyValues(text);
   keyAction=KEY_ADD; 
}

// press all supported keys 
// text is a string which contains the key identifiers eg. "KEY_CTRL KEY_C" for Ctrl-C
void setKeyValues(char* text)
{
  char * acttoken;
  int modifiers=0;

  strcpy(tmptxt, text); 
  acttoken = strtok(tmptxt," ");
  
  while (acttoken)
  {
    if (!strcmp(acttoken,"KEY_SHIFT"))  updateKey(KEY_LEFT_SHIFT);
    if (!strcmp(acttoken,"KEY_CTRL"))  updateKey(KEY_LEFT_CTRL);
    if (!strcmp(acttoken,"KEY_ALT"))  updateKey(KEY_LEFT_ALT);
    if (!strcmp(acttoken,"KEY_RIGHT_ALT"))  updateKey(KEY_RIGHT_ALT);
    if (!strcmp(acttoken,"KEY_GUI"))  updateKey(KEY_LEFT_GUI);
    if (!strcmp(acttoken,"KEY_RIGHT_GUI"))  updateKey(KEY_RIGHT_GUI);
    if (!strcmp(acttoken,"KEY_UP")) updateKey(KEY_UP);
    if (!strcmp(acttoken,"KEY_DOWN")) updateKey(KEY_DOWN);
    if (!strcmp(acttoken,"KEY_LEFT")) updateKey(KEY_LEFT);
    if (!strcmp(acttoken,"KEY_RIGHT")) updateKey(KEY_RIGHT);
    if (!strcmp(acttoken,"KEY_ENTER")) updateKey(KEY_ENTER);
    if (!strcmp(acttoken,"KEY_SPACE")) updateKey(KEY_SPACE);
    if (!strcmp(acttoken,"KEY_ESC")) updateKey(KEY_ESC);
    if (!strcmp(acttoken,"KEY_BACKSPACE")) updateKey(KEY_BACKSPACE);
    if (!strcmp(acttoken,"KEY_TAB")) updateKey(KEY_TAB);
    if (!strcmp(acttoken,"KEY_CAPS_LOCK")) updateKey(KEY_CAPS_LOCK);
    if (!strcmp(acttoken,"KEY_F1")) updateKey(KEY_F1);
    if (!strcmp(acttoken,"KEY_F2")) updateKey(KEY_F2);
    if (!strcmp(acttoken,"KEY_F3")) updateKey(KEY_F3);
    if (!strcmp(acttoken,"KEY_F4")) updateKey(KEY_F4);
    if (!strcmp(acttoken,"KEY_F5")) updateKey(KEY_F5);
    if (!strcmp(acttoken,"KEY_F6")) updateKey(KEY_F6);
    if (!strcmp(acttoken,"KEY_F7")) updateKey(KEY_F7);
    if (!strcmp(acttoken,"KEY_F8")) updateKey(KEY_F8);
    if (!strcmp(acttoken,"KEY_F9")) updateKey(KEY_F9);
    if (!strcmp(acttoken,"KEY_F10")) updateKey(KEY_F10);
    if (!strcmp(acttoken,"KEY_F11")) updateKey(KEY_F11);
    if (!strcmp(acttoken,"KEY_F12")) updateKey(KEY_F12);
    if (!strcmp(acttoken,"KEY_INSERT")) updateKey(KEY_INSERT);
    if (!strcmp(acttoken,"KEY_HOME")) updateKey(KEY_HOME);
    if (!strcmp(acttoken,"KEY_PAGE_UP")) updateKey(KEY_PAGE_UP);
    if (!strcmp(acttoken,"KEY_DELETE")) updateKey(KEY_DELETE);
    if (!strcmp(acttoken,"KEY_END")) updateKey(KEY_END);
    if (!strcmp(acttoken,"KEY_PAGE_DOWN")) updateKey(KEY_PAGE_DOWN);

    if (!strcmp(acttoken,"KEY_A")) updateKey(KEY_A);
    if (!strcmp(acttoken,"KEY_B")) updateKey(KEY_B);
    if (!strcmp(acttoken,"KEY_C")) updateKey(KEY_C);
    if (!strcmp(acttoken,"KEY_D")) updateKey(KEY_D);
    if (!strcmp(acttoken,"KEY_E")) updateKey(KEY_E);
    if (!strcmp(acttoken,"KEY_F")) updateKey(KEY_F);
    if (!strcmp(acttoken,"KEY_G")) updateKey(KEY_G);
    if (!strcmp(acttoken,"KEY_H")) updateKey(KEY_H);
    if (!strcmp(acttoken,"KEY_I")) updateKey(KEY_I);
    if (!strcmp(acttoken,"KEY_J")) updateKey(KEY_J);
    if (!strcmp(acttoken,"KEY_K")) updateKey(KEY_K);
    if (!strcmp(acttoken,"KEY_L")) updateKey(KEY_L);
    if (!strcmp(acttoken,"KEY_M")) updateKey(KEY_M);
    if (!strcmp(acttoken,"KEY_N")) updateKey(KEY_N);
    if (!strcmp(acttoken,"KEY_O")) updateKey(KEY_O);
    if (!strcmp(acttoken,"KEY_P")) updateKey(KEY_P);
    if (!strcmp(acttoken,"KEY_Q")) updateKey(KEY_Q);
    if (!strcmp(acttoken,"KEY_R")) updateKey(KEY_R);
    if (!strcmp(acttoken,"KEY_S")) updateKey(KEY_S);
    if (!strcmp(acttoken,"KEY_T")) updateKey(KEY_T);
    if (!strcmp(acttoken,"KEY_U")) updateKey(KEY_U);
    if (!strcmp(acttoken,"KEY_V")) updateKey(KEY_V);
    if (!strcmp(acttoken,"KEY_W")) updateKey(KEY_W);
    if (!strcmp(acttoken,"KEY_X")) updateKey(KEY_X);
    if (!strcmp(acttoken,"KEY_Y")) updateKey(KEY_Y);
    if (!strcmp(acttoken,"KEY_Z")) updateKey(KEY_Z);
    if (!strcmp(acttoken,"KEY_1")) updateKey(KEY_1);
    if (!strcmp(acttoken,"KEY_2")) updateKey(KEY_2);
    if (!strcmp(acttoken,"KEY_3")) updateKey(KEY_3);
    if (!strcmp(acttoken,"KEY_4")) updateKey(KEY_4);
    if (!strcmp(acttoken,"KEY_5")) updateKey(KEY_5);
    if (!strcmp(acttoken,"KEY_6")) updateKey(KEY_6);
    if (!strcmp(acttoken,"KEY_7")) updateKey(KEY_7);
    if (!strcmp(acttoken,"KEY_8")) updateKey(KEY_8);
    if (!strcmp(acttoken,"KEY_9")) updateKey(KEY_9);
    if (!strcmp(acttoken,"KEY_0")) updateKey(KEY_0);
    
    #ifdef TEENSY     // for Teensy2.0++
      if (!strcmp(acttoken,"KEY_SCROLL_LOCK")) updateKey(KEY_SCROLL_LOCK);
      if (!strcmp(acttoken,"KEY_PAUSE")) updateKey(KEY_PAUSE);
      if (!strcmp(acttoken,"KEY_NUM_LOCK")) updateKey(KEY_NUM_LOCK);
      if (!strcmp(acttoken,"KEY_PRINTSCREEN")) updateKey(KEY_PRINTSCREEN);
    #endif

    acttoken = strtok(NULL," ");
  }
}


