/* PS3 Teensy HID Gamepad
 * Copyright (C) 2010 Josh Kropf <josh@slashdev.ca>
 *
 * Based on works by:
 *   grunskis <http://github.com/grunskis/gamepad>
 *   Toodles <http://forums.shoryuken.com/showthread.php?t=131230>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* USB Keyboard Example for Teensy USB Development Board
 * http://www.pjrc.com/teensy/usb_keyboard.html
 * Copyright (c) 2009 PJRC.COM, LLC
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


#ifndef usb_gamepad_h__
#define usb_gamepad_h__

#include <stdint.h>

void usb_init(void);			// initialize everything
uint8_t usb_configured(void);		// is the USB port configured

typedef struct {
	// digital buttons, 0 = off, 1 = on

	uint8_t square_btn : 1;
	uint8_t cross_btn : 1;
	uint8_t circle_btn : 1;
	uint8_t triangle_btn : 1;

	uint8_t l1_btn : 1;
	uint8_t r1_btn : 1;
	uint8_t l2_btn : 1;
	uint8_t r2_btn : 1;

	uint8_t select_btn : 1;
	uint8_t start_btn : 1;
	uint8_t : 2;
	uint8_t ps_btn : 1;
	uint8_t : 3;

	// digital direction, use the dir_* constants(enum)
	// 8 = center, 0 = up, 1 = up/right, 2 = right, 3 = right/down
	// 4 = down, 5 = down/left, 6 = left, 7 = left/up

	uint8_t direction;

	// left and right analog sticks, 0x00 left/up, 0x80 middle, 0xff right/down

	uint8_t l_x_axis;
	uint8_t l_y_axis;
	uint8_t r_x_axis;
	uint8_t r_y_axis;

	uint8_t unknown[4];

	// button axis, 0x00 = unpressed, 0xff = fully pressed

	uint8_t triangle_axis;
	uint8_t circle_axis;
	uint8_t cross_axis;
	uint8_t square_axis;

	uint8_t l1_axis;
	uint8_t r1_axis;
	uint8_t l2_axis;
	uint8_t r2_axis;
} gamepad_state_t;

extern gamepad_state_t gamepad_state;

void usb_gamepad_reset_state(void);

int8_t usb_gamepad_send(void);


// Everything below this point is only intended for usb_gamepad.c
#ifdef USB_GAMEPAD_PRIVATE_INCLUDE
#include <avr/io.h>
#include <avr/pgmspace.h>
#include <avr/interrupt.h>

#define EP_TYPE_CONTROL			0x00
#define EP_TYPE_BULK_IN			0x81
#define EP_TYPE_BULK_OUT		0x80
#define EP_TYPE_INTERRUPT_IN		0xC1
#define EP_TYPE_INTERRUPT_OUT		0xC0
#define EP_TYPE_ISOCHRONOUS_IN		0x41
#define EP_TYPE_ISOCHRONOUS_OUT		0x40

#define EP_SINGLE_BUFFER		0x02
#define EP_DOUBLE_BUFFER		0x06

#define EP_SIZE(s)	((s) == 64 ? 0x30 :	\
			((s) == 32 ? 0x20 :	\
			((s) == 16 ? 0x10 :	\
			             0x00)))

#define MAX_ENDPOINT		4

#define LSB(n) (n & 255)
#define MSB(n) ((n >> 8) & 255)

#if defined(__AVR_AT90USB162__)
#define HW_CONFIG()
#define PLL_CONFIG() (PLLCSR = ((1<<PLLE)|(1<<PLLP0)))
#define USB_CONFIG() (USBCON = (1<<USBE))
#define USB_FREEZE() (USBCON = ((1<<USBE)|(1<<FRZCLK)))
#elif defined(__AVR_ATmega32U4__)
#define HW_CONFIG() (UHWCON = 0x01)
#define PLL_CONFIG() (PLLCSR = 0x12)
#define USB_CONFIG() (USBCON = ((1<<USBE)|(1<<OTGPADE)))
#define USB_FREEZE() (USBCON = ((1<<USBE)|(1<<FRZCLK)))
#elif defined(__AVR_AT90USB646__)
#define HW_CONFIG() (UHWCON = 0x81)
#define PLL_CONFIG() (PLLCSR = 0x1A)
#define USB_CONFIG() (USBCON = ((1<<USBE)|(1<<OTGPADE)))
#define USB_FREEZE() (USBCON = ((1<<USBE)|(1<<FRZCLK)))
#elif defined(__AVR_AT90USB1286__)
#define HW_CONFIG() (UHWCON = 0x81)
#define PLL_CONFIG() (PLLCSR = 0x16)
#define USB_CONFIG() (USBCON = ((1<<USBE)|(1<<OTGPADE)))
#define USB_FREEZE() (USBCON = ((1<<USBE)|(1<<FRZCLK)))
#endif

// standard control endpoint request types
#define GET_STATUS			0
#define CLEAR_FEATURE			1
#define SET_FEATURE			3
#define SET_ADDRESS			5
#define GET_DESCRIPTOR			6
#define GET_CONFIGURATION		8
#define SET_CONFIGURATION		9
#define GET_INTERFACE			10
#define SET_INTERFACE			11
// HID (human interface device)
#define HID_GET_REPORT			1
#define HID_GET_IDLE			2
#define HID_GET_PROTOCOL		3
#define HID_SET_REPORT			9
#define HID_SET_IDLE			10
#define HID_SET_PROTOCOL		11
// CDC (communication class device)
#define CDC_SET_LINE_CODING		0x20
#define CDC_GET_LINE_CODING		0x21
#define CDC_SET_CONTROL_LINE_STATE	0x22
#endif
#endif
