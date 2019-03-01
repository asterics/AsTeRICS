#include <Keyboard.h>
#include <Mouse.h>

#define CHANNELS 4
#define USE_BUFFERED_AVG 1
#define RXLED  17
#define STARTBUTTON 14
#define DELAYTIME 50
#define BLINKTIME 5
#define MAX_INPUT_LENGTH 100


int delta[CHANNELS] = {0};
int oldValue[CHANNELS] = {0};
int SENSORS[CHANNELS] = {A3, A0, A2, A1};
const int N_AVERAGE = 3;
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
  //Serial.setTimeout(100);

  pinMode(RXLED, OUTPUT);
  digitalWrite(RXLED, HIGH);

  pinMode(SENSORS[0], INPUT);
  pinMode(SENSORS[1], INPUT);
  pinMode(SENSORS[2], INPUT);
  pinMode(SENSORS[3], INPUT);
  //pinMode(STARTBUTTON, INPUT_PULLUP);
  Keyboard.begin();
  Mouse.begin();
  Mouse.screenSize(1920, 1080);
  delay(2);
}

int value;
int cnt = 0;
int runmode = 1;
char receiveBuffer[MAX_INPUT_LENGTH + 1];
char n1Buffer[20];
char n2Buffer[20];
int lastPosX = -1;
int lastPosY = -1;

// the loop routine runs over and over again forever:
void loop() {

  // handle user button input
  if (!runmode && digitalRead(STARTBUTTON) == LOW) {
    runmode = !runmode;
    delay(50);
    while (digitalRead(STARTBUTTON) == LOW);
    delay(50);
  }

  if (runmode) {
    if (Serial.available() > 0) {
      Serial.readBytesUntil(';', receiveBuffer, MAX_INPUT_LENGTH);
      //Serial.println("got: ");
      //Serial.write(receiveBuffer, strlen(receiveBuffer));
      int posX = 0, posY = 0;
      int key1, key2, key3, mouseFunction;
      int n = sscanf(receiveBuffer, "%s %s %d %d %d %d", &n1Buffer, &n2Buffer, &mouseFunction, &key1, &key2, &key3);
      posX = round(atof(n1Buffer));
      posY = round(atof(n2Buffer));
      memset(receiveBuffer, 0, sizeof(receiveBuffer));
      memset(n1Buffer, 0, sizeof(n1Buffer));
      memset(n2Buffer, 0, sizeof(n2Buffer));
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

    // indicate send operation
    cnt = (cnt + 1) % BLINKTIME;
    if (!cnt)  digitalWrite(RXLED, !digitalRead(RXLED));

    // process up to 4 channels
    for (int i = 0; i < CHANNELS; i++) {

#ifdef USE_BUFFERED_AVG
      value = getBufferedAvg(&bufAveragers[i], analogRead(SENSORS[i]));
#else
      value = getMovingAvg(&movAverage[i], analogRead(SENSORS[i]));
#endif

      if (value > oldValue[i]) {
        delta[i] += (value - oldValue[i]) * (value - oldValue[i]);
      }
      oldValue[i] = value;

      delta[i] = sqrt(delta[i]);
      // if(delta[i] > 50 && millis() - lastPrint > 200) {
      //Keyboard.press(KEY_LEFT_SHIFT);
      //Keyboard.print('a');AAAAA
      //Keyboard.press(KEY_LEFT_CTRL);
      //AAKeyboard.releaseAll();
      // lastPrint = millis();
      // }

      Serial.print(value);
      Serial.print(",");
      //  Serial.print(delta*10);
      //  Serial.print(",");
    }
    Serial.println(" ");
    delay(DELAYTIME);

  } else {
    // indicate idle mode
    digitalWrite(RXLED, HIGH);
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
