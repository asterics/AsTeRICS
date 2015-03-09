#include "fabi.h"
#include <EEPROM.h>

#define SLOT_VALID 123
#define MAX_SLOTNAME_LENGTH 15

int nextSlotAddress=0,firstSlotAddress=0;

void saveToEEPROM(char * slotname)
{
   uint8_t done,len=0;
   uint8_t * p;
   int address = 0;

   EEPROM.write(address++,SLOT_VALID);  
   p = (uint8_t*) &settings;
   for (int t=0;t<sizeof(settingsType);t++)
      EEPROM.write(address++,*p++);

   if (EmptySlotAddress>address)
      address=EmptySlotAddress;
 
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.print("Writing slot ");Serial.print(slotname);Serial.print(" starting from EEPROM address "); Serial.println(address);
   
   EEPROM.write(address++,SLOT_VALID);
   while (slotname && *slotname && (len++<MAX_SLOTNAME_LENGTH))
      EEPROM.write(address++,*slotname++);
   EEPROM.write(address++,0);
 
   for (int i=0;i<NUMBER_OF_BUTTONS;i++)
   {
      done=0;
      uint8_t* p = (uint8_t*) &(buttons[i].mode);
      for (int t=0;(t<MAX_KEYSTRING_LEN+4)&&(!done);t++)
      {
        EEPROM.write(address++,*p);
        if ((t>3) && (*p==0)) done=1;     // skip rest of keystring when end detected !
        else p++;
      }
   }
   EEPROM.write(address,0);  // indicates last slot !
   EmptySlotAddress=address;
   
   if (DebugOutput==DEBUG_FULLOUTPUT)  
     Serial.print(address); Serial.println(" bytes saved to EEPROM");
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
   uint8_t b;
   uint8_t* p;
   
   if (EEPROM.read(address)==SLOT_VALID)
   {
      p = (uint8_t*) &settings;
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
     if (DebugOutput==DEBUG_FULLOUTPUT)  
       Serial.print("found slotname "); Serial.println(act_slotname);
     
     if (slotname)
     {
        if (!strcmp(act_slotname, slotname)) found=1;  
     }
     if (DebugOutput==DEBUG_FULLOUTPUT)  
       if (found) Serial.println(" -> loading slot!");
     
     for (i=0;i<NUMBER_OF_BUTTONS;i++)
     {
        done=0;
        p = (uint8_t*) &(buttons[i].mode);
               
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
   
   if (DebugOutput==DEBUG_FULLOUTPUT)  
   {
       Serial.print(numSlots); Serial.print(" slots were found in EEPROM, occupying ");
       Serial.print(address); Serial.println(" bytes.");
   }
}

void listSlots()
{
   int address=0;
   uint8_t done,b;
   
   if (EEPROM.read(address)==SLOT_VALID)
   {
      address++;
      for (int t=0;t<sizeof(settingsType);t++)
         EEPROM.read(address++);
   }

   while (EEPROM.read(address)==SLOT_VALID)  // indicates valid eeprom content !
   {
     uint8_t i=0;
     address++;
     while ((b=EEPROM.read(address++)) != 0)
        Serial.print(b);
         
     Serial.println();
     for (i=0;i<NUMBER_OF_BUTTONS;i++)
     {
        done=0;
        for (int t=0;(t<MAX_KEYSTRING_LEN+4)&&(!done);t++)
        {
          b=EEPROM.read(address++);
          if ((t>3) && (b==0)) done=1;     // skip rest of keystring when end detected !
        }
     }
   }
}

