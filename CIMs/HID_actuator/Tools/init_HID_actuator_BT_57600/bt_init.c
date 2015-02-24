/* Simple example for Teensy USB Development Board
 * http://www.pjrc.com/teensy/
 * Copyright (c) 2008 PJRC.COM, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#include <avr/io.h>
#include <avr/pgmspace.h>
#include <util/delay.h>
#include "usb_debug_only.h"
#include "print.h"
#include "uart.h"

#define LED_CONFIG	(DDRD |= (1<<6))
#define LED_ON		(PORTD |= (1<<6))
#define LED_OFF		(PORTD &= ~(1<<6))
#define LED_TOGGLE	(PORTD ^= (1<<6))
#define CPU_PRESCALE(n)	(CLKPR = 0x80, CLKPR = (n))


// write a string to the uart
#define uart_print(s) uart_print_P(PSTR(s))

void uart_print_P(const char *str)
{
	char c;
	while (1) {
		c = pgm_read_byte(str++);
		if (!c) break;
		uart_putchar(c);
		LED_TOGGLE;
		_delay_ms(100);
	}
}





void check_reply(void)
{
	_delay_ms(500);

	print("Reply:");
	while (uart_available()) {
		pchar(uart_getchar());
		//phex16(c);
	}
	
}


int main(void)
{
	uint16_t count=1;

	// set for 16 MHz clock, and turn on the LED
	CPU_PRESCALE(0);
	LED_CONFIG;
	LED_ON;

	// initialize the USB, and then wait for the host
	// to set configuration.  If the Teensy is powered
	// without a PC connected to the USB port, this 
	// will wait forever.

	uart_init(19200);

	usb_init();
	while (!usb_configured()) /* wait */ ;

	// wait an extra second for the PC's operating system
	// to load drivers and do whatever it does to actually
	// be ready for input
	_delay_ms(1000);

	// start printing stuff.  If hid_listen is running on
	// the host, this should appear.
	print("Bluetooth Initialisation sequence for HID actuator\n");
	print("Sending AT commands to BTM-222 at 19200 Baud.\n");
	print("After initialisation, baud rate is 57600\n\n");
	

	_delay_ms(2000);
	
	phex16(count++); print(" Sending: ATL4 (Set baud rate to 57600)\n");
	uart_print("ATL4\r");
	_delay_ms(1000);
	uart_init(57600);
	check_reply();
	print("\n\n");

	
	phex16(count++); print(" Sending: ATE1 (Echo on)\n");
	uart_print("ATE1\r");
	check_reply();
	print("\n\n");

	phex16(count++); print(" Sending: ATQ0 (allow reply messages)\n");
	uart_print("ATQ0\r");
	check_reply();
	print("\n\n");


	phex16(count++); print(" Sending: AT\n");
	uart_print("AT\r");
	check_reply();
	print("\n\n");


	phex16(count++); print(" Query Echo mode\n");
	uart_print("ATE?\r");
	check_reply();
	print("\n\n");

	phex16(count++); print(" Query Baud Rate\n");
	uart_print("ATL?\r");
	check_reply();
	print("\n\n");

	phex16(count++); print(" Sending: ATR1 (Put device into slave mode)\n");
	uart_print("ATR1\r");
	check_reply();
	print("\n\n");

	phex16(count++); print(" Sending: ATQ1 (suppress reply messages)\n");
	uart_print("ATQ1\r");
	check_reply();
	print("\n\n");

	phex16(count++); print(" Sending: ATE0 (Echo off)\n");
	uart_print("ATE0\r");
	check_reply();
	print("\n\n");


	print("\nDone.");print("\n");
	while (1);
}


