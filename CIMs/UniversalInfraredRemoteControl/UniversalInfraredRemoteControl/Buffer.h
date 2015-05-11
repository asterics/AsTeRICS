/*
 * Buffer.h
 *
 * Created: 08.01.2015 08:58:26
 * Author: Christoph
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */

#include <avr/io.h>

uint8_t irBuffer[512];
uint8_t* irBufferPointer;

uint8_t irBufferOverflow;
uint16_t irBufferCounter;

void InitBuffer(void);					// Reset Buffer