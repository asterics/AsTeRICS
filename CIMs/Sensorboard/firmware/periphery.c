#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/twi.h>
#include <avr/sleep.h>
#include <util/delay.h>

#include "periphery.h"
#include "usb_serial.h"
#include "cim_protocol.h"
#include "twi_master.h"

//this struct is used by the TWI ISR to write the data to
volatile struct sensor_data_t sensor_data [MEASUREMENT_BUFSIZE+1] = {
	{0,0,0, 0,0,0, 0,0,0, 0,0, 0,0, 0,0, 0,0, 0},
	{0,0,0, 0,0,0, 0,0,0, 0,0, 0,0, 0,0, 0,0, 0},
	{0,0,0, 0,0,0, 0,0,0, 0,0, 0,0, 0,0, 0,0, 0},
	{0,0,0, 0,0,0, 0,0,0, 0,0, 0,0, 0,0, 0,0, 0}
};

volatile uint16_t measurement_period = 249;	/*initialised for 1 Hz*/
volatile uint8_t next_free_struct = 0;
volatile uint8_t next_data_struct = 0;
uint8_t adxl_read [9] = {ADXL_W,0x32,ADXL_R, 0,0, 0,0, 0,0};
uint8_t gyro_read [9] = {gyro_W,0x1D,gyro_R, 0,0, 0,0, 0,0};
uint8_t compass_read [9] = {compass_W, 0x03, compass_R, 0,0, 0,0, 0,0};
uint8_t ircam_start [2] = {ircam_W, 0x37};
uint8_t ircam_capt [13] = {ircam_R, 0,0,0,0,0, 0,0,0,0,0, 0,0};

void init_LEDs(void) {
	DDRD|=(1<<DDD6);
	DDRD|=(1<<DDD7);
}

void toggle_orangeLED(void) {
	PORTD^=(1<<PORTD6);
}

void on_orangeLED(void) {
	PORTD|=(1<<PORTD6);
}

void toggle_greenLED(void) {
	PORTD^=(1<<PORTD7);
}

void on_greenLED(void) {
	PORTD|=(1<<PORTD7);
}

void init_Timer0 (void) {			//interrupts @ 250 Hz --> 50 Hz f_measurement
	TCCR0A |= (1<<WGM01);				//set CTC mode
	TCCR0B |= (1<<CS02);				//prescaler: 256
	OCR0A = 125;
}

void enable_measurement (void) {
	if (measurement_period>=3) {
		TCNT0 = 0;
		TIMSK0 |= (1<<OCIE0A); 		
	}
}

void disable_measurement (void) {
	waitForTransmission();
	TIMSK0 &=~(1<<OCIE0A);
}

void set_measurement_period(uint16_t val) {
	//lowest period is 20 ms; with increasing steps of 4 ms
	val = (val/4)-1;					//input is in ms, by division of 4 val is calculated
	if (val<4) {
		if (val == 0) disable_measurement();
		else  val = 4;		//lowest possible setting: 5x4ms = 20 ms = 50 Hz
	}
	cli();
	measurement_period = val;
	sei();
}

void init_ADC(void) {
	//AVCC with external capacitor on AREF pin
	//left adjust as only 8 bit resolution needed
	ADMUX |= (1<<REFS0)|(1<<ADLAR);		
			
	//prescaler set to 64 for 125kHz input clock frequency
	ADCSRA |= (1<<ADEN)|(1<<ADIE)|(1<<ADPS1)|(1<<ADPS2);

	//disable digital input to reduce power consumption
	DIDR0 |= (1<<ADC1D)|(1<<ADC0D);
}

void start_ADC_single_conv(void) {	//is called when compass measurement is finished
	ADCSRA |= (1<<ADSC);
}

ISR(TIMER0_COMPA_vect) {
	static uint16_t count = 0;

	//for each sensor, check if there is a device error, if not: start TWI communication
	
	switch (count) {		

		case 0:	
			//ADXL
			
			if (TWI_device_status() & 1) {
				on_orangeLED();
			}
			else start_twi_with_data((uint8_t*) adxl_read,9);				
			
			break;

		case 1:
			//gyro
			
			if (TWI_device_status() & 2) {
				on_orangeLED();
			}
			else start_twi_with_data((uint8_t*) gyro_read,9);			
			
			break;

		case 2:
			//compass, at end of compass measurement, ADC measurement is started
			
			if (TWI_device_status() & 4) {
				start_ADC_single_conv();
				on_orangeLED();
			}
			else start_twi_with_data((uint8_t*) compass_read,9);
			
			break;

		case 3:
			//select register of IR cam
			
			if (TWI_device_status() & 8) {
				on_orangeLED();
			}
			else start_twi_with_data((uint8_t*) ircam_start,2);
			
			break;

		case 4:
			//IR-optical sensor, at end of measurement, indicate that measurement series is finished
			
			if (TWI_device_status() & 8) {
				finish_measurement();
				on_orangeLED();
			}			
			else start_twi_with_data((uint8_t*) ircam_capt,13);
			
			break;

		default:
			break;
	}

	if (count >= measurement_period) {
		count = 0;	
	}
	else count ++;

}

ISR(ADC_vect) {
	write_ADC_measurement (ADCH);
}

uint8_t measurement_data_available(void) { //check if there is new data
	uint8_t temp_data = next_data_struct;

	if (temp_data != next_free_struct) return (temp_data+1); //if there is new data, return the array Number (+1)
	else return 0;		//if there is no new data
	
}

void clear_measurement_data(void) {
	next_data_struct = (next_data_struct + 1) & MEASUREMENT_BUFSIZE;	
}

//returns a pointer to the next structure where the measurement is stored
struct sensor_data_t * fetch_struct(uint8_t sel_measurement) {
	struct sensor_data_t *ptr_return;
	ptr_return = (struct sensor_data_t*) &sensor_data[sel_measurement];
	return ptr_return;
}

void finish_measurement(void) {

	uint8_t temp_free = (next_free_struct + 1) & MEASUREMENT_BUFSIZE;
	if (temp_free != next_data_struct) {
		next_free_struct = temp_free;
	}
}

void write_ADXL_measurement (uint8_t *data) {
	sensor_data[next_free_struct].ADXL_x = *data;
	data++;
	sensor_data[next_free_struct].ADXL_x |= (*data<<8);
	data++;
	sensor_data[next_free_struct].ADXL_y = *data;
	data++;
	sensor_data[next_free_struct].ADXL_y |= (*data<<8);
	data++;
	sensor_data[next_free_struct].ADXL_z = *data;
	data++;
	sensor_data[next_free_struct].ADXL_z |= (*data<<8);
}
void write_gyro_measurement (uint8_t *data) {
	sensor_data[next_free_struct].gyro_x = (*data<<8);
	data++;
	sensor_data[next_free_struct].gyro_x |= *data;
	data++;
	sensor_data[next_free_struct].gyro_y = (*data<<8);
	data++;
	sensor_data[next_free_struct].gyro_y |= *data;
	data++;
	sensor_data[next_free_struct].gyro_z = (*data<<8);
	data++;
	sensor_data[next_free_struct].gyro_z |= *data;
}
void write_compass_measurement (uint8_t *data) {
	//hmc 5883L: Z and Y axis are swapped in order compared to hmc 5843

	sensor_data[next_free_struct].compass_x = (*data<<8);
	data++;
	sensor_data[next_free_struct].compass_x |= *data;
	data++;
	sensor_data[next_free_struct].compass_z = (*data<<8);
	data++;
	sensor_data[next_free_struct].compass_z |= *data;
	data++;
	sensor_data[next_free_struct].compass_y = (*data<<8);
	data++;
	sensor_data[next_free_struct].compass_y |= *data;	
}
void write_ir_measurement (uint8_t *data) {
	
	uint8_t temp;

	sensor_data[next_free_struct].ir1_x = *data;
	data++;
	sensor_data[next_free_struct].ir1_y = *data;
	data++;
	temp = *data;
	data++;
	sensor_data[next_free_struct].ir1_x += (temp & 0x30) << 4;
	sensor_data[next_free_struct].ir1_y += (temp & 0xC0) << 2;

	sensor_data[next_free_struct].ir2_x = *data;
	data++;
	sensor_data[next_free_struct].ir2_y = *data;
	data++;
	temp = *data;
	data++;
	sensor_data[next_free_struct].ir2_x += (temp & 0x30) << 4;
	sensor_data[next_free_struct].ir2_y += (temp & 0xC0) << 2;
		
	sensor_data[next_free_struct].ir3_x = *data;
	data++;
	sensor_data[next_free_struct].ir3_y = *data;
	data++;
	temp = *data;
	data++;
	sensor_data[next_free_struct].ir3_x += (temp & 0x30) << 4;
	sensor_data[next_free_struct].ir3_y += (temp & 0xC0) << 2;

	sensor_data[next_free_struct].ir4_x = *data;
	data++;
	sensor_data[next_free_struct].ir4_y = *data;
	data++;
	temp = *data;
	data++;
	sensor_data[next_free_struct].ir4_x += (temp & 0x30) << 4;
	sensor_data[next_free_struct].ir4_y += (temp & 0xC0) << 2;
	
}

void write_ADC_measurement (uint8_t data) {
	sensor_data[next_free_struct].pressure = data;
}
