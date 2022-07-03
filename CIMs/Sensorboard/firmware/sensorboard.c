#define F_CPU 8000000UL
#define CPU_PRESCALE(n)	(CLKPR = 0x80, CLKPR = (n))

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/twi.h>
#include <avr/sleep.h>
#include <util/delay.h>
#include <inttypes.h>

#include "periphery.h"
#include "usb_serial.h"
#include "cim_protocol.h"
#include "twi_master.h"

int main(void) 
{
	uint8_t packet_state;
	uint8_t sel_measurement;
	volatile uint8_t transmit_buffer0 [CIM_PACKET_SIZE] = {
		0,0,0,0,0, 0,0,0,0,0,
		0,0,0,0,0, 0,0,0,0,0,
		0,0,0,0,0, 0,0,0,0,0,
		0,0,0,0,0, 0,0,0,0,0,
		0,0,0,0,0, 0 };
	//uint8_t transmit_buffer1 [CIM_PACKET_SIZE];
	//uint8_t transmit_buffer2 [CIM_PACKET_SIZE];
	uint8_t *ptr_read_buf = (uint8_t*)transmit_buffer0;
	uint8_t *ptr_fill_buf = (uint8_t*)transmit_buffer0;
	struct sensor_data_t *ptr_next_m_values;

	// set the CPU clock to 8 MHz
	CPU_PRESCALE(1);
	
	while (!usb_configured()) {
		usb_init();
		_delay_ms(1000);
	}
	set_sleep_mode(SLEEP_MODE_IDLE);
	
	create_CIM_template (ptr_fill_buf);

	init_LEDs();
	init_twi();			//also includes init of i2c devices
	init_ADC();
	init_Timer0();	

	sei();

	on_greenLED();

	if (TWI_device_status()) {
		on_orangeLED();
	}

	while (1) {
		sleep_mode();		

		//check if serial data was sent to CIM & process it
		if (usb_serial_available()) {
			fetch_ARE_packet();
			process_ARE_packet();
		}

		//check if measurement data is available & process it
		sel_measurement = measurement_data_available();
		if (sel_measurement) {
			sel_measurement--;

			ptr_next_m_values = fetch_struct(sel_measurement);
			packet_state = create_CIM_data_packet(ptr_next_m_values, ptr_fill_buf);

			if (packet_state==1) {
				send_CIM_packet(ptr_read_buf);
				clear_measurement_data();				
			}
		}

		//check if application is connected to serial port
		if (!(usb_serial_get_control() & USB_SERIAL_DTR)) {
			_delay_ms(500);
			disable_measurement();
		}

	}

	return 1;
}
