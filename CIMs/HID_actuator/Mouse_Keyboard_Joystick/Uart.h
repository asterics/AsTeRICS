

/*
     AsTeRICS - Assistive Technology Rapid Integration and Construction Set
  
  
         d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
        d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
       d88P888          888           888    888   888  888    888 Y88b.     
      d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
     d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
    d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
   d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
  d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 
 
                     homepage: http://www.asterics.org 
 
     This project has been partly funded by the European Commission, 
                       Grant Agreement Number 247730
 
  The AsTeRICS Universal HID Actuator is based upon	the
  LUFA Library, Copyright (C) Dean Camera, 2010
  dean [at] fourwalledcubicle [dot] com,  www.fourwalledcubicle.com

  Permission to use, copy, modify, distribute, and sell this 
  software and its documentation for any purpose is hereby granted
  without fee, provided that the above copyright notice appear in 
  all copies and that both that the copyright notice and this
  permission notice and warranty disclaimer appear in supporting 
  documentation, and that the name of the author not be used in 
  advertising or publicity pertaining to distribution of the 
  software without specific, written prior permission.

  The author disclaim all warranties with regard to this
  software, including all implied warranties of merchantability
  and fitness.  In no event shall the author be liable for any
  special, indirect or consequential damages or any damages
  whatsoever resulting from loss of use, data or profits, whether
  in an action of contract, negligence or other tortious action,
  arising out of or in connection with the use or performance of
  this software.


	 file: Uart.h
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010


*/

#ifndef _UART_H_
#define _UART_H_

#include <inttypes.h>


#ifndef F_CPU
#define F_CPU 16000000
#endif


#define UART_BAUDRATE 115200

#define INBUF_SIZE 1000      // 1000 bytes for ringbuffers to store HID requests
#define INBUF_WARNING 40

#define OUTBUF_SIZE 500      // 500 bytes for ringbuffer to store CIM replies
#define OUTBUF_WARNING 30  


#define DISABLE_SEND_ISR UCSR1B &= ~(1<<UDRIE1);
#define ENABLE_SEND_ISR UCSR1B |= (1<<UDRIE1);

#define DISABLE_RCV_ISR UCSR1B &= ~(1<<RXCIE1);
#define ENABLE_RCV_ISR UCSR1B |= (1<<RXCIE1);


struct ringbuf_i {
	unsigned int buflen;
	unsigned int rpos;
	unsigned int wpos;
	unsigned char data[INBUF_SIZE];
} ;

struct ringbuf_o {
	unsigned int buflen;
	unsigned int rpos;
	unsigned int wpos;
	unsigned char data[OUTBUF_SIZE];
} ;

extern struct ringbuf_o output_buffer;
extern struct ringbuf_i uart_in,input_mouse,input_absmouse, input_keyboard, input_joystick;

void init_InBuf(struct ringbuf_i * rb);
int keys_in_InBuf(struct ringbuf_i * rb);
int free_in_InBuf(struct ringbuf_i * rb);
unsigned char read_InBuf(struct ringbuf_i * rb);
void insert_InBuf(struct ringbuf_i * rb, unsigned char c);

void init_OutBuf(struct ringbuf_o * rb);
int keys_in_OutBuf(struct ringbuf_o * rb);
int free_in_OutBuf(struct ringbuf_o * rb);
unsigned char read_OutBuf(struct ringbuf_o * rb);
void insert_OutBuf(struct ringbuf_o * rb, unsigned char c);

void UART_Init(long baud);
void UART_Putchar(unsigned char c);		 // polling 
void UART_Print (char * str);            // polling
void UART_Send_NonBlocking( char *str, int len); // interrupt driven

#endif
