//-----------------------------------------------------------------------------------
// Arduino Sketch for record/replay of infrared commands via the IrMicro plugin
// connect TSOP module (e.g. TSOP4838) to pin 11.
// connect IR led with current-limiting resistor to send pin (e.g. 3 for Arduino Uno)
// for a list of compatible controllers and default send pins see:
// https://github.com/z3t0/Arduino-IRremote
//-----------------------------------------------------------------------------------
//


#include <IRremote.h>

// #define DEBUG_OUTPUT
#define CODE_COMPLETE 1
#define IR_MAXLEN 500

//------------------------------------------------------------------------------
// Tell IRremote which Arduino pin is connected to the IR Receiver (TSOP4838)
//
int recvPin = 11;
int khz = 38; // 38kHz carrier frequency for IR signal

IRrecv irrecv(recvPin);
IRsend irsend;

static unsigned int irSignal[IR_MAXLEN] = {0};
unsigned int irSignalLength = 0;  



//+=============================================================================
// Configure the Arduino
//
void  setup ( )
{
  Serial.begin(9600);   // Status message will be sent to PC at 9600 baud
  irrecv.enableIRIn();  // Start the receiver
}

//+=============================================================================
// Dump out the decode_results structure.
//
void  dumpCode (decode_results *results)
{
  Serial.print("T:");  // sync bytes for timing values

  // Dump raw timing data
  for (int i = 1;  i < results->rawlen;  i++) {
    Serial.print(results->rawbuf[i] * USECPERTICK, DEC);
    if ( i < results->rawlen-1 ) Serial.print(","); // ',' not needed on last one
    if (!(i & 1))  Serial.print(" ");
  }
  Serial.println("");

  // Now dump "known" codes
  if (results->decode_type != UNKNOWN) {  

    Serial.print("0x");  // sync bytes for hex values
    Serial.print(results->value, HEX);
    Serial.println("");
  }
  else      Serial.println("unknown");
}

uint8_t parseSerialData(uint8_t actByte) {
  static uint8_t actState=0;
  static unsigned int actValue=0;

  switch (actState) {
    case 0: 
        if (actByte == 'T')      // wait for first sync byte ('T')
            actState++; 
        break;

    case 1:                     // wait for second sync byte (':')
        if (actByte == ':') {  
           actValue=0; irSignalLength=0; actState++; 
        } else actState=0;
        break;

    case 2:                    // get a comma-separated list of timing values

        if ((actByte >= '0') && (actByte <='9')) {   // accumulate one timing value
          actValue = actValue * 10 + actByte-'0';
        }
        else if (actByte == ',') {                   // value separator
            if (irSignalLength < IR_MAXLEN-1)
              irSignal[irSignalLength++]=actValue;
            actValue=0;                             // prepare for next value
        }
        else if ((actByte=='\n') || (actByte=='\r')) {  // end of timinglist
            if (irSignalLength < IR_MAXLEN-1)
              irSignal[irSignalLength++]=actValue;
            actState=0;
            return CODE_COMPLETE;
        }
        else if (actByte!=' ') {    // ignore spaces 
             actState=0;         // illegal character: restart parser
        }
        break;
  }
  return (0);  
}


//+=============================================================================
// The repeating section of the code
//
void  loop ( )
{
  decode_results  results;        // Somewhere to store the results

  if (irrecv.decode(&results)) {  // Grab an IR code
    dumpCode(&results);           // Output the timing and code results
    Serial.println("");           // Blank line between entries
    irrecv.resume();              // Prepare for the next value
  }

  if (Serial.available()) {
    if (parseSerialData(Serial.read()) == CODE_COMPLETE) {
      #ifdef DEBUG_OUTPUT
      Serial.print("will play serial timings:");         
      for (int i=0;i<irSignalLength;i++) {
          Serial.print(irSignal[i]);
          if (i<irSignalLength-1) Serial.print(", ");
      }
      Serial.println("");
      #endif
      irsend.sendRaw(irSignal, irSignalLength, khz); //Note the approach used to automatically calculate the size of the array.
      irrecv.enableIRIn();  // Start the receiver

    }
  }
}
