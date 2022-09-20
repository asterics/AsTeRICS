


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


	 file: Uart.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010

*/



#include <avr/io.h>
#include <avr/interrupt.h>
#include "Uart.h"
#include "CimProtocol.h"


struct ringbuf_i uart_in, input_mouse, input_absmouse, input_keyboard, input_joystick;
struct ringbuf_o output_buffer;


void init_InBuf(struct ringbuf_i * rb)
{
 	rb->buflen=0;
 	rb->rpos=0;
 	rb->wpos=0;
}

void insert_InBuf(struct ringbuf_i * rb, unsigned char c)
{
 	if (rb->buflen<INBUF_SIZE) {
    	rb->data[rb->wpos]=c; rb->wpos=(rb->wpos+1)%INBUF_SIZE;
    	rb->buflen++;
 	}
}

unsigned char read_InBuf(struct ringbuf_i * rb)
{
 	unsigned char c;
	DISABLE_RCV_ISR;
 	if (!rb->buflen)  return (0); 
 	c=rb->data[rb->rpos];
 	rb->rpos=(rb->rpos+1)%INBUF_SIZE;
 	rb->buflen--;
	ENABLE_RCV_ISR;
 	return(c);
}

int keys_in_InBuf(struct ringbuf_i * rb)
{
 	return rb->buflen;
}

int free_in_InBuf(struct ringbuf_i * rb)
{
 	return (INBUF_SIZE-rb->buflen);
}


void init_OutBuf(struct ringbuf_o * rb)
{
 	rb->buflen=0;
 	rb->rpos=0;
 	rb->wpos=0;
}

void insert_OutBuf(struct ringbuf_o * rb, unsigned char c)
{
 	if (rb->buflen<OUTBUF_SIZE) {
    	rb->data[rb->wpos]=c; rb->wpos=(rb->wpos+1)%OUTBUF_SIZE;
    	rb->buflen++;
 	}
}

unsigned char read_OutBuf(struct ringbuf_o * rb)
{
 	unsigned char c;
 	if (!rb->buflen)  return (0); 
 	c=rb->data[rb->rpos];
 	rb->rpos=(rb->rpos+1)%OUTBUF_SIZE;
 	rb->buflen--;
 	return(c);
}


int keys_in_OutBuf(struct ringbuf_o * rb)
{
 	return rb->buflen;
}

int free_in_OutBuf(struct ringbuf_o * rb)
{
 	return (OUTBUF_SIZE-rb->buflen);
}


ISR (USART1_RX_vect) // interrupt UART character received -> parse / store in ringbuf
{
	// parse_CIM_protocol(UDR1);
	insert_InBuf(&uart_in, UDR1);   // parse AsTeRICS CIM communication protocol
}

ISR (USART1_UDRE_vect) // Interrupt UART Data Register empty -> send next byte )
{
	if (keys_in_OutBuf(&output_buffer))        // characters left ?
		UDR1=read_OutBuf(&output_buffer); // send current character from the buffer
	else DISABLE_SEND_ISR; 	// Transmission of TXBuf finished -> disable USART_UDRE interrupts
}


void UART_Init(long baudrate)
{
	init_InBuf(&uart_in);
	init_InBuf(&input_mouse);
	init_InBuf(&input_absmouse);
	init_InBuf(&input_keyboard);
	init_InBuf(&input_joystick);
	init_OutBuf(&output_buffer);

	UBRR1 = F_CPU / baudrate / 8 - 1;
 
	UCSR1A |= (1<<U2X1);
	UCSR1B = (1<<RXEN1) | (1<<TXEN1) | (1<<RXCIE1);
  //UCSR1C =  (1<<USBS1) | (3<<UCSZ10);

}

void UART_Putchar(unsigned char c)
{
	while ( !( UCSR1A & (1<<UDRE1)) );
	UDR1 = c;
}

void UART_Print( char *str)  // polling
{
	while ( *str) {
      UART_Putchar ((unsigned char) *str); str++;
	}
}

void UART_Send_NonBlocking( char *str, int len) // interrupt driven
{
	  DISABLE_SEND_ISR;
	  while (len--) insert_OutBuf(&output_buffer, *str++);
	  ENABLE_SEND_ISR;
}


