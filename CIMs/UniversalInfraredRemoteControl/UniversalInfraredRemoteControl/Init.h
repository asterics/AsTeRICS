/*
 * Init.h
 *
 * Created: 29.10.2014 18:39:18
 * Author: Christoph
 */ 
#define F_CPU 8000000L
#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))


void init(void);
void Bootsequence(uint8_t start, uint8_t stop);

