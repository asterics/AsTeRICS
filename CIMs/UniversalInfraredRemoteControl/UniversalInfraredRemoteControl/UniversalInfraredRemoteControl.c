/*
 * UniversalInfraredRemoteControl.c
 *
 * Created: 02.04.2015 13:25:34
 *  Author: Christoph Ulbinger (FHTW)
 */ 



#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>


#include "SendReceive.h"
#include "Buffer.h"
#include "usb_serial.h"
#include "lcd.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;
extern struct ARE_frame_t ARE_frame;

int main(void)
{
	init();

	while (1)
	{
		parse_CIM_protocol();
		switch(set_ir_status)
		{
			case 0:						// Idle
				break;
			case 1:						// Sending
				get_ir_status = 1;		// Sending
				stop_send_ir();
				stop_record_ir();
				start_send_ir();

				set_ir_status = 0;
				break;
			case 2:						// Recording
				get_ir_status = 2;		// Recording
				stop_send_ir();
				stop_record_ir();
				start_record_ir();

				set_ir_status = 0;
				break;
			default:
				stop_send_ir();
				stop_record_ir();

				set_ir_status = 0;
				break;
		}

	}
}