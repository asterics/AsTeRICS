#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/twi.h>
#include <avr/sleep.h>
#include <util/delay.h>

#include "periphery.h"
#include "usb_serial.h"
#include "cim_protocol.h"
#include "twi_master.h"

volatile uint8_t TWI_buf[TWI_buf_size];			//this buffer is used to store data for and from TWI
volatile uint8_t cnt;												//for selection of the TWI_buf array
volatile uint8_t TWI_msg_size;
volatile uint8_t in_transmission=0;


/******** TWI Device Status **************************************/
volatile uint8_t device_status = 0;

/********* Bits are set if device is erroneous ********************

accelerometer		0b0000 0001		0x01
gyro				0b0000 0010		0x02
compass				0b0000 0100		0x04
ir-optical sensor	0b0000 1000		0x08

******************************************************************/



void init_twi(void) {		//set TWBR to 72 for 50kHz
												//set TWBR to 32 for 100kHz
	TWBR = 32;						//(TWBR should be >10 in Master Mode)
	
	init_ADXL();
	init_gyro();
	init_compass();	
	init_ir();
}

uint8_t TWI_device_status(void){
	return device_status;
}

void init_ADXL (void) {

	//BW_Rate Register: set 25Hz Output-Rate, Low Power mode OFF
	write_loop_twi(ADXL_W, 0x2C, 0x08);	
	
	//Power_CTL Register: enable measurement
	write_loop_twi(ADXL_W, 0x2D, 0x08);	

	/*DATA_FORMAT Register: for left justified mode*/
	//write_ADXL(ADXL_W, 0x31, 0x04);	
}

void init_gyro (void) {
	
	//Hardware Reset, for Register description see below
	write_loop_twi(gyro_W, 0x3E, 0x80);
	
	//Register SMPLRT_DIV - Sample Rate Divider: 0x13 --> Sample rate set to 50 Hz
	write_loop_twi(gyro_W, 0x15, 0x13);

		/**** Register DLPF:
		Bit7:5	Bit4:3		Bit2:0 	Default Value
		N/A			FS_SEL 	DLPF_CFG	00h
		**************
		FS_SEL: set to 0b11 for proper operation
		 					Low Pass  						
		DLPF_CFG	Filter Bandwidth		Internal Sample Rate
		0					256Hz								8kHz
		1					188Hz								1kHz
		2					98Hz								1kHz
		3					42Hz								1kHz
		4					20Hz								1kHz
		5					10Hz								1kHz
		6					5Hz									1kHz
	**************/
	write_loop_twi(gyro_W, 0x16, 0x18);

	//Register Power Management
	//Bit7 		Bit6 	Bit5 		Bit4 		Bit3 		Bit2:0	Default Value
	//H_RESET SLEEP STBY_XG STBY_YG STBY_ZG CLK_SEL 00h
	//selected clock source: PLL with X gyro reference
	write_loop_twi(gyro_W, 0x3E, 0x01);
}

void init_compass (void) {
	//hmc 5883L: Configuration Register A, adress 0x00
	//0b 0111 1000
	//0x78
	write_loop_twi(compass_W, 0x00, 0x78);
	write_loop_twi(compass_W, 0x02, 0x00);	//continuous mode
}

void init_ir (void) {

	//simple init
	write_loop_twi (ircam_W, 0x30, 0x01);	//control register
	write_loop_twi (ircam_W, 0x06, 0x90);	//MAXSIZE register
	write_loop_twi (ircam_W, 0x08, 0xC0);	//GAIN register
	write_loop_twi (ircam_W, 0x1A, 0x40);	//GAINLIMIT register
	write_loop_twi (ircam_W, 0x33, 0x03);	//MODE register
	write_loop_twi (ircam_W, 0x30, 0x08);	//control register
	
	//uint8_t SLA_W, uint8_t data_register, uint8_t *data, uint8_t size
	//advanced init
	/*
	uint8_t data_array[9];	
	data_array = {0x01};
	write_loop_twi_multiple_data (ircam_W, 0x30, data_array, 1); 
	data_array = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x90};
	write_loop_twi_multiple_data (ircam_W, 0x00, data_array, 7); 
	data_array = {0x00, 0x41};
	write_loop_twi_multiple_data (ircam_W, 0x07, data_array, 2);
	data_array = {0x40, 0x00};
	write_loop_twi_multiple_data (ircam_W, 0x1A, data_array, 2);
	data_array = {0x03};
	write_loop_twi_multiple_data (ircam_W, 0x33, data_array, 1);
	data_array = {0x08};
	write_loop_twi_multiple_data (ircam_W, 0x30, data_array, 1);
	*/

	/******************* http://wiki.wiimoteproject.com/IR_Sensor
	0xB00000 	UNKNOWN
	0xB00001 	0x00
	0xB00002 	0x00
	0xB00003 	0x71
	0xB00004 	0x01
	0xB00005 	0x00
	0xB00006 	MAXSIZE
	0xB00007 	0x00
	0xB00008 	GAIN
	0xB0001A 	GAINLIMIT
	0xB0001B 	MINSIZE
	0xB00030 	CONTROL
	0xB00033 	MODE

	UNKNOWN: Wii uses values: 2 (standard) and 7 (max sensitivity). No known function.
	MAXSIZE: Maximum blob size. Wii uses values from 0x62 to 0xc8.
	GAIN: Sensor Gain. Smaller values = higher gain.
	GAINLIMIT: Sensor Gain Limit. Must be less than GAIN for camera to function. No other effect?
	MINSIZE: Minimum blob size. Wii uses values from 3 to 5
	CONTROL: Write 1 before configuring camera, 8 when done.
	MODE: output format, 1, 3, or 5. See output formats below.
	Other values: As used by Wii. Function not known. 
	**********************/

	/************* Simple Initialization: **************************************
	Just write the following byte sequences, with a small delay between writes (assumes a successful ACK). The first byte on each line is the register you are writing to.
	0×30 0×01
	0×30 0×08
	0×06 0×90
	0×08 0xC0
	0x1A 0×40
	0×33 0×33 (0x33 as data did not work out, but 0x03 was good)
	*/

	/************* Initialization with sensitivity settings: **************************
	The author defined 5 sensitivity levels, and there are four parameters (p0, p1, p2, p3) that are adjusted for each level. Here are the settings:

	Level 1: p0 = 0×72, p1 = 0×20, p2 = 0x1F, p3 = 0×03
	Level 2: p0 = 0xC8, p1 = 0×36, p2 = 0×35, p3 = 0×03
	Level 3: p0 = 0xAA, p1 = 0×64, p2 = 0×63, p3 = 0×03
	Level 4: p0 = 0×96, p1 = 0xB4, p2 = 0xB3, p3 = 0×04
	Level 5: p0 = 0×96, p1 = 0xFE, p2 = 0xFE, p3 = 0×05

	Quoting the Wiimote Wiki IR sensor page, these parameters correspond to:
	p0: MAXSIZE: Maximum blob size. Wii uses values from 0×62 to 0xc8
	p1: GAIN: Sensor Gain. Smaller values = higher gain
	p2: GAINLIMIT: Sensor Gain Limit. Must be less than GAIN for camera to function. No other effect?
	p3: MINSIZE: Minimum blob size. Wii uses values from 3 to 5

	Either pick your own custom settings for the parameters, or choose them from one of the 5 levels above, then send the following data to the device:

	0×30, 0×01
	0×00, 0×02, 0×00, 0×00, 0×71, 0×01, 0×00, p0
	0×07, 0×00, p1
	0x1A, p2, p3
	0×33, 0×03
	0×30, 0×08
	*/
}


/***********************************************************
simple write function, on error: mark device as erroneous
************************************************************/
void write_loop_twi (uint8_t SLA_W, uint8_t data_register, uint8_t data) {
	uint8_t status [2];

	TWCR = (1<<TWSTA)|(1<<TWINT)|(1<<TWEN);		//start condition bit
	while (!(TWCR & (1<<TWINT)));
	status [0] = TW_STATUS;
	if (status[0] != TW_START) {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWDR = SLA_W;									//address
	TWCR = (1<<TWINT)|(1<<TWEN);
	while (!(TWCR & (1<<TWINT)));
	status[0] = TW_STATUS;
	if (status[0] != TW_MT_SLA_ACK)  {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWDR = data_register;						//register for data
	TWCR = (1<<TWINT) | (1<<TWEN);
	while (!(TWCR & (1<<TWINT)));
	status[0] = TW_STATUS;
	if (status[0] != TW_MT_DATA_ACK)  {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWDR = data;										//data
	TWCR = (1<<TWINT) | (1<<TWEN);
	while (!(TWCR & (1<<TWINT)));
	status[0] = TW_STATUS;
	if (status[0] != TW_MT_DATA_ACK)  {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWCR = (1<<TWINT)|(1<<TWEN)|(1<<TWSTO);	//stop condition bit
	while ((TWCR & (1<<TWSTO)));
	_delay_ms(40);		//ADXL cannot be initialised with delay of 20 ms!
}


/***********************************************************
simple write function for multiple data bytes, on error: mark device as erroneous
************************************************************/
void write_loop_twi_multiple_data (uint8_t SLA_W, uint8_t data_register, uint8_t *data, uint8_t size) {
	uint8_t status [2];

	TWCR = (1<<TWSTA)|(1<<TWINT)|(1<<TWEN);		//start condition bit
	while (!(TWCR & (1<<TWINT)));
	status [0] = TW_STATUS;
	if (status[0] != TW_START) {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWDR = SLA_W;									//address
	TWCR = (1<<TWINT)|(1<<TWEN);
	while (!(TWCR & (1<<TWINT)));
	status[0] = TW_STATUS;
	if (status[0] != TW_MT_SLA_ACK)  {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	TWDR = data_register;						//register for data
	TWCR = (1<<TWINT) | (1<<TWEN);
	while (!(TWCR & (1<<TWINT)));
	status[0] = TW_STATUS;
	if (status[0] != TW_MT_DATA_ACK)  {
		//error routine for TWI-errors
		TWI_errorRoutine(SLA_W);
		on_orangeLED();
	}

	for (int i=0; i<size; i++) {
		TWDR = *data;										//data
		data++;
		TWCR = (1<<TWINT) | (1<<TWEN);
		while (!(TWCR & (1<<TWINT)));
		status[0] = TW_STATUS;
		if (status[0] != TW_MT_DATA_ACK)  {
			//error routine for TWI-errors
			TWI_errorRoutine(SLA_W);
			on_orangeLED();
		}
	}

	TWCR = (1<<TWINT)|(1<<TWEN)|(1<<TWSTO);	//stop condition bit
	while ((TWCR & (1<<TWSTO)));
	_delay_ms(40);		//ADXL cannot be initialised with delay of 20 ms!
}


/***********************************************************
write msg into twi buffer and start TWI /w interrupt
************************************************************/
uint8_t start_twi_with_data(uint8_t *message, uint8_t msg_size) {
	
	cli();
	uint8_t check = in_transmission;	

	if (check==0) {		//check if TWI transmission is in progress

		for (int i=0; i<=msg_size; i++) {
			TWI_buf[i] = *message;
			message++;
		}
		TWI_msg_size = msg_size;
		sei();

		TWCR =	(1<<TWINT)|(0<<TWEA)|(1<<TWSTA)|
						(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
		return 1;
	}
	else { 
		sei();
		return 0;
	}
}

ISR(TWI_vect) {
/** \name TWSR values
  Mnemonics:
  <br>TW_MT_xxx - master transmitter
  <br>TW_MR_xxx - master receiver
  */
  	uint8_t errmsg = 0;

	switch (TW_STATUS) {
		    
		case (TW_START):						/* start condition transmitted */
			in_transmission = 1;
			cnt = 0;
			TWDR = TWI_buf[cnt];
			TWCR =	(1<<TWINT)|(1<<TWEA)|(0<<TWSTA)|
					(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
			cnt++;
			break;
		
		case (TW_REP_START):	    	/* repeated start condition transmitted */
			TWDR = TWI_buf[cnt];
			TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
					(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);			
			cnt++;
			break;

	/********* Master Transmitter **********/
		case (TW_MT_SLA_ACK):				/* SLA+W transmitted, ACK received */
			TWDR = TWI_buf[cnt];
			TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
					(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
			cnt++;
			break;

		case (TW_MT_SLA_NACK):			/* SLA+W transmitted, NACK received */
			errmsg = TW_MT_SLA_NACK;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;

		case (TW_MT_DATA_ACK):			/* data transmitted, ACK received */
			
			if (cnt < TWI_msg_size)	{
				if (	(TWI_buf[cnt] == ADXL_R)||
						(TWI_buf[cnt] == gyro_R)||
						(TWI_buf[cnt] == compass_R)||
						(TWI_buf[cnt] == ircam_R)) 
				{
					TWCR =	(1<<TWINT)|(0<<TWEA)|(1<<TWSTA)|
							(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
				}
				else {
					TWDR = TWI_buf[cnt];
					TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
							(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
					cnt++;
				}
				break;
			}
			else {
				TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
						(1<<TWSTO)|(1<<TWEN)|(1<<TWIE);
				in_transmission = 0;
				break;
			}

		case (TW_MT_DATA_NACK):			/* data transmitted, NACK received */
			errmsg = TW_MT_DATA_NACK;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;
		
	/********* Master Receiver *************/
		case (TW_MR_SLA_ACK):				/* SLA+R transmitted, ACK received */
				TWCR =	(1<<TWINT)|(1<<TWEA)|(0<<TWSTA)|
						(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
			break;
		
		 case (TW_MR_SLA_NACK):			/* SLA+R transmitted, NACK received */
		 	errmsg = TW_MR_SLA_NACK;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;
	
		case (TW_MR_DATA_ACK):			/* data received, ACK returned */
			if ((cnt+2) == TWI_msg_size)	{

				TWI_buf[cnt] = TWDR;
				TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
						(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
				cnt++;
			}
			else {
				TWI_buf[cnt] = TWDR;
				TWCR =	(1<<TWINT)|(1<<TWEA)|(0<<TWSTA)|
						(0<<TWSTO)|(1<<TWEN)|(1<<TWIE);
				cnt++;				
			}
			break;
		
		case (TW_MR_DATA_NACK):			/* data received, NACK returned */
				TWI_buf[cnt] = TWDR;
				TWCR =	(1<<TWINT)|(0<<TWEA)|(0<<TWSTA)|
						(1<<TWSTO)|(1<<TWEN)|(1<<TWIE);

				/**************************************************
				check which sensor was selected, 
				call of the corresponding function to save the measurement data
				call function finish_measurement() to indicate that the measurement series is finished
				***************************************************/
				if (TWI_buf[2] == ADXL_R) {
					write_ADXL_measurement ((uint8_t*) &TWI_buf[3]);					
				}

				if (TWI_buf[2] == gyro_R) {
					write_gyro_measurement ((uint8_t*) &TWI_buf[3]);
				}			
				
				if (TWI_buf[2] == compass_R) {
					write_compass_measurement ((uint8_t*) &TWI_buf[3]);
					//start ADC Single Conversion
					start_ADC_single_conv();
				}

				if (TWI_buf[0] == ircam_R) {
					write_ir_measurement((uint8_t*) &TWI_buf[1]);
					//by reading the IR-optical sensor values, the measurement series is finished
					//prepare data packet and toggle green LED
					finish_measurement();
					toggle_greenLED();
				}

				in_transmission=0;
			
			break;

		/* Misc */	
		case (TW_MR_ARB_LOST):			/* arbitration lost in SLA+R, SLA+W, data or NACK */
			errmsg = TW_MR_ARB_LOST;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;

		case (TW_NO_INFO):				/* no state information available */
			errmsg = TW_NO_INFO;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;

		case (TW_BUS_ERROR):			/* illegal start or stop condition */
			errmsg = TW_BUS_ERROR;
			TWI_errorRoutine(TWI_buf[0]);
			on_orangeLED();
			break;
	}
}

void waitForTransmission (void) {
	while (in_transmission != 0);
}


//marks the erroneous device
void TWI_errorRoutine(uint8_t address){

uint8_t devID = address>>1;

	switch(devID) {

		case (0x53):
			device_status |= 0x01;
			break;

		case (0x68):
			device_status |= 0x02;
			break;

		case (0x1E):
			device_status |= 0x04;
			break;

		case (0x58):
			device_status |= 0x08;
			break;
		
		default:
			break;
	}

}
