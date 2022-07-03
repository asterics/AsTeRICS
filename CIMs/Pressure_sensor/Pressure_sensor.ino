
// Simple Arduino Sketch for using a Freescale MPXV7007DP pressure sensor
// connected to A0 for creating keyboard input (here: Key 'a')
// compatible with Leonardo, Teensy/Teensy++/TeensyLC or Arduino Pro Micro
//
// can be used to interface a sip/puff mouthpiece or a plastic ball for
// interaction


#define KEY               'a'
#define ON_TRESHOLD       530
#define OFF_TRESHOLD      520
#define DELAYTIME          10
#define DELAY_AFTER_PRESS 100

#define DEBUG_OUTPUT    // remove this if you do not want serial output

int value;
int cont = 0;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(38400);

}


void loop() {
  
  value = analogRead(0);
  
  #ifdef DEBUG_OUTPUT
    Serial.print("analog 0 is: ");
    Serial.println(value);
  #endif
  
  if (value > ON_TRESHOLD ) {
    Keyboard.press(KEY);
    while (analogRead(0) > OFF_TRESHOLD ) ;
    Keyboard.release(KEY);
    delay(DELAY_AFTER_PRESS);
  }
  delay(DELAYTIME);  

}
