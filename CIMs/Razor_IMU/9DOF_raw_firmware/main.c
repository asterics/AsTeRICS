/*
    1-1-10
    Copyright Spark Fun Electronics© 2010
    Aaron Weiss
    aaron at sparkfun dot com
    
	9DOF Serial
	
	Hardware v13
	Firmware v11
	
	ATMega328@3.3V w/ external 16MHz resonator
	High Fuse DA
    Low Fuse FF
	
	38400bps
	
	ADXL345: Accelerometer
	HMC5843: Magnetometer
	LY530:	Yaw Gyro
	LPR530:	Pitch and Roll Gyro
*/

#include <stdio.h>
#include <avr/pgmspace.h>
#include "types.h"
#include "defs.h"
#include "i2c.h"

#define STATUS_LED 5 //stat LED is on PB5

#define sbi(var, mask)   ((var) |= (uint8_t)(1 << mask))
#define cbi(var, mask)   ((var) &= (uint8_t)~(1 << mask))

#define WRITE_sda() DDRC = DDRC | 0b00010000 //SDA must be output when writing
#define READ_sda()  DDRC = DDRC & 0b11101111 //SDA must be input when reading - don't forget the resistor on SDA!!

///============Global Vars=========/////////////////
uint16_t x_mag, y_mag, z_mag; //x, y, and z magnetometer values

///============Function Prototypes=========/////////////////
void adxl345(void);
void config_menu(void);
void hmc5843(void);
void magnetometer(void);
void raw(void);
void stgyros(void);
uint16_t x_accel(void);
uint16_t y_accel(void);
uint16_t z_accel(void);
uint16_t x_gyro(void);
uint16_t y_gyro(void);
uint16_t z_gyro(void);

///============I2C Prototypes=============//////////////////
void i2cSendStart(void);
void i2cSendStop(void);
void i2cWaitForComplete(void);
void i2cSendByte(unsigned char data);
void i2cHz(long uP_F, long scl_F);

///============Initialize Prototypes=====//////////////////
void init(void);
void UART_Init(unsigned int ubrr);
uint8_t uart_getchar(void);
static int uart_putchar(char c, FILE *stream);
void put_char(unsigned char byte);
static FILE mystdout = FDEV_SETUP_STREAM(uart_putchar, NULL, _FDEV_SETUP_WRITE);
void delay_ms(uint16_t x);
void i2cInit(void);

///============Display Strings============//////////////////
const char wlcm_str[] PROGMEM = "\n\n\r9DOF IMU Test Firmware v11 \n\r==========================";
const char accel[] PROGMEM = "\n\r[1]Accelerometer: ADXL345 \n\r";
const char mag[] PROGMEM = "[2]Magnetometer: HMC5843 \n\r";
const char gyro[] PROGMEM = "[3]Gyroscope: LPR530 and LY530 \n\r";
const char raw_out[] PROGMEM = "[4]Raw \n\r";

/////===================================////////////////////

int main(void)
{
	init();
	
	while(1)
	{	
		
		sbi(PORTB, STATUS_LED);
		delay_ms(500);
		cbi(PORTB, STATUS_LED);
		delay_ms(500);
		sbi(PORTB, STATUS_LED);
		delay_ms(500);
		cbi(PORTB, STATUS_LED);
		
		config_menu();
	}
}

void adxl345(void)
{	
	printf("x=%4d, ", x_accel());
	printf("y=%4d, ", y_accel());
	printf("z=%4d \n\r", z_accel());
	//delay_ms(20);
}

void config_menu(void)
{
    uint8_t choice=0;
	
	printf_P(wlcm_str);
	printf_P(accel);
	printf_P(mag);
	printf_P(gyro);
	printf_P(raw_out);
	
	while(1)
    {
		choice = uart_getchar();
		putchar('\n');
		putchar('\r');
		
		if(choice=='1')
		{
			while(!(UCSR0A & (1 << RXC0)))adxl345();
			config_menu();
		}
		if(choice=='2')
		{
			while(!(UCSR0A & (1 << RXC0)))
			{
				hmc5843();
				delay_ms(550);//at least 100ms interval between measurements
			}
			config_menu();
		}
		if(choice=='3')
		{
			while(!(UCSR0A & (1 << RXC0)))stgyros();
			config_menu();
		}
		if(choice=='4')
		{
			while(!(UCSR0A & (1 << RXC0)))raw();
			config_menu();
		}
    }
}

void hmc5843(void)
{
	magnetometer();
	printf("x=%4d, ", x_mag);
	printf("y=%4d, ", y_mag);
	printf("z=%d \n\r", z_mag);
}

void magnetometer(void)
{
	/*
		The magnetometer values must be read consecutively
		in order to move the pointer. Therefore the x, y, and z
		outputs need to be kept in this function. To read the magnetometer 
		values call global vars x_mag, y_mag, z_mag.
	*/
	
	uint8_t xh, xl, yh, yl, zh, zl;
	
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0x3C);    //write to HMC
	i2cWaitForComplete();
	i2cSendByte(0x02);    //mode register
	i2cWaitForComplete();
	i2cSendByte(0x00);    //continuous measurement mode
	i2cWaitForComplete();
	i2cSendStop();
	
	//must read all six registers plus one to move the pointer back to 0x03
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0x3D);          //read from HMC
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	xh = i2cGetReceivedByte();	//x high byte
	i2cWaitForComplete();
	
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	xl = i2cGetReceivedByte();	//x low byte
	i2cWaitForComplete();
	x_mag = xl|(xh << 8);
	
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	yh = i2cGetReceivedByte();	//y high byte
	i2cWaitForComplete();
	
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	yl = i2cGetReceivedByte();	//y low byte
	i2cWaitForComplete();
	y_mag = yl|(yh << 8);
	
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	zh = i2cGetReceivedByte();	
	i2cWaitForComplete();      //z high byte
	
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	zl = i2cGetReceivedByte();	//z low byte
	i2cWaitForComplete();
	z_mag = zl|(zh << 8);
	
	i2cSendByte(0x3D);         //must reach 0x09 to go back to 0x03
	i2cWaitForComplete();
	
	i2cSendStop();	
}

void raw(void)
{
	printf("$,");
	printf("%d,", x_accel());
	printf("%d,", y_accel());
	printf("%d,", z_accel());
	printf("%d,", x_gyro());
	printf("%d,", y_gyro());
	printf("%d,", z_gyro());
	magnetometer();
	printf("%d,", x_mag);
	printf("%d,", y_mag);
	printf("%d,", z_mag);
	printf("#\n\r");
}

void stgyros(void)
{
	
	printf("x= %4d, ", x_gyro());
	printf("y= %4d, ", y_gyro());
	printf("z= %d \r\n", z_gyro());
	//delay_ms(300);
}

uint16_t x_gyro(void)
{
	uint16_t  xl;// xlow register
	uint16_t  xh;// xhigh register
	
	// x-axis
	ADMUX = (1 << REFS0)|(1 << MUX0);//ADC1
	ADCSRA = (1 << ADEN)|(1 << ADSC)|(1<<ADPS2)|(1<<ADPS1);
	
	while(ADCSRA & (1 << ADSC));
	xl = ADCL;
	xh = ADCH & 0x03;
	xh = xh << 8;
	xh = xh + xl;
	
	return xh;
}

uint16_t y_gyro(void)
{
	uint16_t  yl;// ylow register
	uint16_t  yh;// yhigh register
	
	// y-axis
	ADMUX = (1 << REFS0)|(1 << MUX1);//ADC2
	ADCSRA = (1 << ADEN)|(1 << ADSC)|(1<<ADPS2)|(1<<ADPS1);
	
	while(ADCSRA & (1 << ADSC));
	yl = ADCL;
	yh = ADCH & 0x03;
	yh = yh << 8;
	yh = yh + yl;
	
	return yh;
}

uint16_t z_gyro(void)
{
	uint16_t  zl;// zlow register
	uint16_t  zh;// zhigh register
	
	// z-axis
	ADMUX = (1 << REFS0);//ADC0
	ADCSRA = (1 << ADEN)|(1 << ADSC)|(1<<ADPS2)|(1<<ADPS1);
	
	while(ADCSRA & (1 << ADSC));
	zl = ADCL;
	zh = ADCH & 0x03;
	zh = zh << 8;
	zh = zh + zl;
	
	return zh;
}

uint16_t x_accel(void)
{		
	//0xA6 for a write
	//0xA7 for a read
	
	uint8_t dummy, xh, xl;
	uint16_t xo;
	
	//0x32 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x32);    //X0 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	xl = i2cGetReceivedByte();	//x low byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();	
	
	//0x33 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x33);    //X1 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	xh = i2cGetReceivedByte();	//x high byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();
	xo = xl|(xh << 8);
	return xo;
}

uint16_t y_accel(void)
{		
	//0xA6 for a write
	//0xA7 for a read
	
	uint8_t dummy, yh, yl;
	uint16_t yo;
	
	//0x34 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x34);    //Y0 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	yl = i2cGetReceivedByte();	//x low byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();	
	
	//0x35 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x35);    //Y1 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	yh = i2cGetReceivedByte();	//y high byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();
	yo = yl|(yh << 8);
	return yo;
}

uint16_t z_accel(void)
{	
	//0xA6 for a write
	//0xA7 for a read
	
	uint8_t dummy, zh, zl;
	uint16_t zo;
	
	//0x36 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x36);    //Z0 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	zl = i2cGetReceivedByte();	//z low byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();	
	
	//0x37 data registers
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x37);    //Z1 data register
	i2cWaitForComplete();
	
	i2cSendStop();		 //repeat start
	i2cSendStart();

	i2cWaitForComplete();
	i2cSendByte(0xA7);    //read from ADXL
	i2cWaitForComplete();
	i2cReceiveByte(TRUE);
	i2cWaitForComplete();
	zh = i2cGetReceivedByte();	//z high byte
	i2cWaitForComplete();
	i2cReceiveByte(FALSE);
	i2cWaitForComplete();
	dummy = i2cGetReceivedByte();	//must do a multiple byte read?
	i2cWaitForComplete();
	i2cSendStop();
	zo = zl|(zh << 8);	
	return zo;
}

/*********************
 ****Initialize****
 *********************/
 
void init (void)
{
    //1 = output, 0 = input
	DDRB = 0b01100000; //PORTB4, B5 output for stat LED
    DDRC = 0b00010000; //PORTC4 (SDA), PORTC5 (SCL), PORTC all others are inputs
    DDRD = 0b00000010; //PORTD (TX output on PD1), 
	PORTC = 0b00110000; //pullups on the I2C bus
	
	UART_Init(25);	//38400bps, fill in UBRR to set baud http://www.wormfood.net/avrbaudcalc.php

	i2cInit();
}

void UART_Init(unsigned int ubrr)
{
	int ubrr_new;
	// set baud rate
	ubrr_new = ubrr; 
	UBRR0H = ubrr_new>>8;
	UBRR0L = ubrr_new;
	
	// Enable receiver and transmitter 
	UCSR0A = (0<<U2X0);
	UCSR0B = (1<<RXEN0)|(1<<TXEN0);
	
	// Set frame format: 8 bit, no parity, 1 stop bit,   
	UCSR0C = (1<<UCSZ00)|(1<<UCSZ01);
	
	stdout = &mystdout; //Required for printf init
}

uint8_t uart_getchar(void)
{
    while( !(UCSR0A & (1<<RXC0)) );
    return(UDR0);
}

static int uart_putchar(char c, FILE *stream)
{
    if (c == '\n') uart_putchar('\r', stream);
  
    loop_until_bit_is_set(UCSR0A, UDRE0);
    UDR0 = c;
    
    return 0;
}

void put_char(unsigned char byte)
{
	/* Wait for empty transmit buffer */
	while ( !( UCSR0A & (1<<UDRE0)) );
	/* Put data into buffer, sends the data */
	UDR0 = byte;
}

void delay_ms(uint16_t x)
{
  uint8_t y, z;
  for ( ; x > 0 ; x--){
    for ( y = 0 ; y < 90 ; y++){
      for ( z = 0 ; z < 6 ; z++){
        asm volatile ("nop");
      }
    }
  }
}

/*********************
 ****I2C Functions****
 *********************/

void i2cInit(void)
{
	// set i2c bit rate to 40KHz
	i2cSetBitrate(100);
	// enable TWI (two-wire interface)
	sbi(TWCR, TWEN);
	
	//initialize
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x2D);    //power register
	i2cWaitForComplete();
	i2cSendByte(0x08);    //measurement mode
	i2cWaitForComplete();
	i2cSendStop();
	
	i2cSendStart();
	i2cWaitForComplete();
	i2cSendByte(0xA6);    //write to ADXL
	i2cWaitForComplete();
	i2cSendByte(0x31);    //data format
	i2cWaitForComplete();
	i2cSendByte(0x08);    //full resolution
	i2cWaitForComplete();
	i2cSendStop();
}

void i2cSetBitrate(unsigned short bitrateKHz)
{
	unsigned char bitrate_div;
	// set i2c bitrate
	// SCL freq = F_CPU/(16+2*TWBR))
	//#ifdef TWPS0
		// for processors with additional bitrate division (mega128)
		// SCL freq = F_CPU/(16+2*TWBR*4^TWPS)
		// set TWPS to zero
		cbi(TWSR, TWPS0);
		cbi(TWSR, TWPS1);
	//#endif
	// calculate bitrate division	
	bitrate_div = ((F_CPU/4000l)/bitrateKHz);
	if(bitrate_div >= 16)
		bitrate_div = (bitrate_div-16)/2;
	outb(TWBR, bitrate_div);
}

void i2cSendStart(void)
{
	WRITE_sda();
	// send start condition
	TWCR = (1<<TWINT)|(1<<TWSTA)|(1<<TWEN);
}

void i2cSendStop(void)
{
	// transmit stop condition
        TWCR = (1<<TWINT)|(1<<TWEN)|(1<<TWSTO);
}

void i2cWaitForComplete(void)
{
	int i = 0;		//time out variable
	
	// wait for i2c interface to complete operation
        while ((!(TWCR & (1<<TWINT))) && (i < 90))
			i++;
}

void i2cSendByte(unsigned char data)
{

	WRITE_sda();
	// save data to the TWDR
	TWDR = data;
	// begin send
	TWCR = (1<<TWINT)|(1<<TWEN);
}

void i2cReceiveByte(unsigned char ackFlag)
{
	// begin receive over i2c
	if( ackFlag )
	{
		// ackFlag = TRUE: ACK the recevied data
		outb(TWCR, (inb(TWCR)&TWCR_CMD_MASK)|BV(TWINT)|BV(TWEA));
	}
	else
	{
		// ackFlag = FALSE: NACK the recevied data
		outb(TWCR, (inb(TWCR)&TWCR_CMD_MASK)|BV(TWINT));
	}
}

unsigned char i2cGetReceivedByte(void)
{
	// retieve received data byte from i2c TWDR
	return( inb(TWDR) );
}

unsigned char i2cGetStatus(void)
{
	// retieve current i2c status from i2c TWSR
	return( inb(TWSR) );
}