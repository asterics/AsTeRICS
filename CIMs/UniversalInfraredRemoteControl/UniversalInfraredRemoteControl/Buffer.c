/*
 * Buffer.c
 *
 * Created: 08.01.2015 08:58:26
 * Author: Christoph
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */

#include "Buffer.h"

void InitBuffer()			// Reset Buffer
{
	irBufferPointer = irBuffer;
	irBufferOverflow = 0;
	irBufferCounter = 0;	
}