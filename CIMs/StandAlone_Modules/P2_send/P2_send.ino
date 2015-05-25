
#define LED_PIN 13

uint8_t TXBuf[17];            // Buffer for the Packet
uint8_t CurrentCh = 0;
int8_t packetcount = 0 ;
uint8_t blinktime = 0;
uint16_t adcValue;
uint32_t timestamp;

void setup() {
    pinMode(1,INPUT_PULLUP);
    pinMode(LED_PIN,OUTPUT);       // define Led Pin as output
    digitalWrite(LED_PIN,HIGH);       // define Led Pin as output

    // Prepare Transmit Buffer
    TXBuf[0]= 0xA5;          // sync byte one
    TXBuf[1]= 0x5A;          // sync byte two
    TXBuf[2]= 0x02;          // version info

    Serial.begin(115200);
}

void loop() {

  if(digitalRead(1)==LOW)
  {
      while (micros()-timestamp<3906);
      timestamp=micros();
      TXBuf[3]=packetcount++;    //  update packet counter
      TXBuf[16]= PIND;           //  Update button values

      for (CurrentCh=0;CurrentCh<6;CurrentCh++)
      {
         adcValue = analogRead(CurrentCh);  
	 TXBuf[4+CurrentCh*2]= (uint8_t)(adcValue >> 8);       // (high byte)
	 TXBuf[4+CurrentCh*2+1]= (uint8_t)(adcValue & 0xff);  // (low byte)
      }
      
      Serial.write(TXBuf,17);
      #if (!(defined(__AVR_ATmega328P__) || defined(__AVR_ATmega168__)))
      Serial.send_now();
      #endif
	   
      if (!(++blinktime % 16)) 
         digitalWrite(LED_PIN,!digitalRead(LED_PIN)); 	
  }
}
