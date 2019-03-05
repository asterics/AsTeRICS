#include <Keyboard.h>
#include <Mouse.h>

#define CHANNELS 4
#define USE_BUFFERED_AVG 1
#define RXLED  17
#define STARTBUTTON 14
#define DELAYTIME 50
#define READ_TIME 5
#define BLINKTIME 5
#define MAX_INPUT_LENGTH 100

int SENSORS[CHANNELS] = {A3, A0, A2, A1};
int channel_values[CHANNELS] = {0, 0, 0, 0};
const int N_AVERAGE = 10;
long lastPrint = 0;

typedef struct {
  long sum;
  int avg[N_AVERAGE];
  int index;
} avgStruct;

avgStruct bufAveragers[CHANNELS];
float movAverage[CHANNELS] = {0};

// the setup routine runs once when you press reset:
void setup() {
  // initialize the digital pin as an output.
  Serial.begin(9600);
  Serial.setTimeout(2000);

  //pinMode(RXLED, OUTPUT);
  //digitalWrite(RXLED, HIGH);

  pinMode(SENSORS[0], INPUT);
  pinMode(SENSORS[1], INPUT);
  pinMode(SENSORS[2], INPUT);
  pinMode(SENSORS[3], INPUT);
  //pinMode(STARTBUTTON, INPUT_PULLUP);
  Keyboard.begin();
  Mouse.begin();
  //Mouse.screenSize(1920, 1080);
  Mouse.screenSize(3840, 1080);
  analogReadResolution(12);
  delay(2);
}

int value;
int cnt = 0;
int runmode = 1;
char receiveBuffer[MAX_INPUT_LENGTH + 1];
int lastPosX = -1;
int lastPosY = -1;
unsigned long lastrun = 0;
unsigned long lastread = 0;

// the loop routine runs over and over again forever:
void loop() {

  // handle user button input
  if (!runmode && digitalRead(STARTBUTTON) == LOW) {
    runmode = !runmode;
    delay(50);
    while (digitalRead(STARTBUTTON) == LOW);
    delay(50);
  }

  if (Serial.available() > 0) {
    Serial.readBytesUntil(';', receiveBuffer, MAX_INPUT_LENGTH);
    //Serial.println("got: ");
    //Serial.write(receiveBuffer, strlen(receiveBuffer));
    int posX = 0, posY = 0;
    int key1, key2, key3, mouseFunction;
    int n = sscanf(receiveBuffer, "%d %d %d %d %d %d", &posX, &posY, &mouseFunction, &key1, &key2, &key3);
    memset(receiveBuffer, 0, sizeof(receiveBuffer));
    if (posX != lastPosX || posY != lastPosY) {
      lastPosX = posX;
      lastPosY = posY;
      Mouse.moveTo(posX, posY);
    }
    if (mouseFunction != 0) {
      switch (mouseFunction) {
        case 1: Mouse.click(MOUSE_LEFT); break;
        case 2: Mouse.click(MOUSE_LEFT); Mouse.click(MOUSE_LEFT); break;
        case 3: Mouse.click(MOUSE_RIGHT); break;
        case 4: Mouse.click(MOUSE_MIDDLE); break;
        case 5: Mouse.press(MOUSE_LEFT); break;
        case 6: Mouse.release(MOUSE_LEFT); break;
      }
      if (mouseFunction != 5) {
        Mouse.release(MOUSE_LEFT);
      }
    }
    if (key1 != 0) {
      Keyboard.press(key1);
    }
    if (key2 != 0) {
      Keyboard.press(key2);
    }
    if (key3 != 0) {
      Keyboard.press(key3);
    }
    Keyboard.releaseAll();
  }

  if (runmode && millis() - lastread >= READ_TIME) {
    lastread = millis();
    for (int i = 0; i < CHANNELS; i++) {
#ifdef USE_BUFFERED_AVG
      channel_values[i] = getBufferedAvg(&bufAveragers[i], analogRead(SENSORS[i]));
#else
      channel_values[i] = getMovingAvg(&movAverage[i], analogRead(SENSORS[i]));
#endif
    }
  }

  if (runmode && millis() - lastrun >= DELAYTIME) {
    //Serial.print("real delay: ");
    //Serial.println(millis() - lastrun);
    lastrun = millis();
    
    // process up to 4 channels
    for (int i = 0; i < CHANNELS; i++) {
      Serial.print(channel_values[i] * 1.0 / 4);
      Serial.print(",");
    }
    Serial.println(" ");
  }
}

int getBufferedAvg(avgStruct * averager, int newValue) {
  averager->sum -= averager->avg[averager->index];
  averager->avg[averager->index] = newValue;
  averager->sum += newValue;
  averager->index = (averager->index + 1) % N_AVERAGE;
  return (averager->sum / N_AVERAGE);
}

int getMovingAvg(float * average, int newValue) {
  *average -= *average * 1.0 / N_AVERAGE;
  *average += newValue * 1.0 / N_AVERAGE;

  return (int) * average;
}
