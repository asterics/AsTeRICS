

// calibration: get left-right offset and up-down offset values  - done?
// COM port with higher numbers - DONE
// live feedback of values in GUI
// activate / deactivety mouse function in GUI during setup - DONE - button added
// selectable threshold values and click types for sip and puff
// store values in EEPROM, read on startup
// later: sepcial keyboard mode (cursor left/rigth/up/down, enter, space



unsigned long previousTime = 0;
float accumXpos = 0.f;
float accumYpos = 0.f;

static int x;
static int y;
static int x_offset;
static int y_offset;

static int calib_now = 1;

int  deadzoneX = 20;
int  deadzoneY = 20;

int  sipThreshold = 500;
int  puffThreshold = 530;
int  clickState = 0;

static int speedNum = 5;
static int mouseOn = 1;


void setup()
{ 

  pinMode(21,OUTPUT);
  pinMode(22,OUTPUT);
  
  digitalWrite(21,HIGH);  // supply voltage for pressure sensor
  digitalWrite(22,LOW);

  Serial.begin(115200);
  Mouse.begin();
  delay(500);
} 


void loop() 
{
  unsigned long currentTime = millis();
  float timeDifference = (currentTime - previousTime)/1000.f;
  previousTime = currentTime;


  if (Serial.available() > 0) 
  {
    static char input[6];
    static uint8_t i;
    char c = Serial.read ();

    if ( c != '\r' && i < 5 ) // use Carriage Return for the line ending character
    {
      input[i++] = c;
    }
    else
    {
      if (input[0] == 'm') // speed command
      {
        speedNum = (input[1] - '0')*2;
      }
      else if (input[0] == 'd') // deadzone command
      {
        deadzoneX = (input[1] - '0')*15;
        deadzoneY = (input[1] - '0')*15;
      }
      else if (input[0] == 'c') //calibration command
      {
        calib_now = 1;
      }
      else if (input[0] == 'o') // turn on/off command
      {
        if (input[1] == 'n')
        {
          mouseOn = 1;
            Serial.print("on");
        }
        else if (input[1] == 'f')
        {
           mouseOn = 0;
           Serial.print("off");
        }
      }

      input[i] = '\0';
      i = 0;
      }
  }



  int pressure = analogRead(A0);
  int down = analogRead(42);
  int left = analogRead(43);
  int up = analogRead(44);
  int right = analogRead(45);

  if (calib_now == 1)
  {
     x_offset = (left-right);
     y_offset = (up-down);
     calib_now=0;
  }    
  else
  {
      x = (left-right) - x_offset;
      y = (up-down) - y_offset;
  }

  if (abs(x)< deadzoneX) x=0;
  if (abs(y)< deadzoneY) y=0;

if (mouseOn== 1)
{
  accumYpos += y*speedNum*timeDifference; 
  accumXpos += x*speedNum*timeDifference; 

  int xMove = (int)accumXpos;
  int yMove = (int)accumYpos;


  Mouse.move(xMove, yMove);
  

  accumXpos -= xMove;
  accumYpos -= yMove;
}
  
  if (pressure > puffThreshold)
  {
    Mouse.set_buttons(0, 0, 1);
  }
  else if (pressure < sipThreshold)
  {
    Mouse.set_buttons(1, 0, 0);
  }
  else Mouse.set_buttons(0, 0, 0);


  /*
   Serial.print("pressure: ");
   Serial.print(pressure);
   Serial.print("      down: ");
   Serial.print(down);
   Serial.print("      left: ");
   Serial.print(left);
   Serial.print("      up: ");
   Serial.print(up);
   Serial.print("      right: ");
   Serial.print(right);
   Serial.println("");
   delay (100);
*/
   Serial.print("      x: ");
   Serial.print(x);
   Serial.print("      y: ");
   Serial.print(y);


}



