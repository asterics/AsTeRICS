/*
* lcd.h
*
* Description: Contains variables and prototypes of lcd.c
*
* Created: 09.09.2014 17:37:37
* Author: Christoph Ulbinger
* Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
* NOTE : R/W has to be connected to GND
*/

#define Data7 PORTC0
#define Data6 PORTC1
#define Data5 PORTC2
#define Data4 PORTC3

#define DirectionData DDRC
#define DirectionControl DDRA

#define PortData PORTC
#define PortControl PORTA
 
#define RegisterSelect PORTA5
#define ReadWrite PORTA6
#define Enable PORTA7

// LCD Line Code
#define lcd_Line_One     0x00
#define lcd_Line_Two     0x40

// LCD instructions
#define lcd_Clear           0b00000001          // replace all characters with ASCII 'space'
#define lcd_Home            0b00000010          // return cursor to first position on first line
#define lcd_EntryMode       0b00000110          // shift cursor from left to right on read/write
#define lcd_DisplayOff      0b00001000          // turn display off
#define lcd_DisplayOn       0b00001100          // display on, cursor off, don't blink character
#define lcd_FunctionReset   0b00110000          // reset the LCD
#define lcd_FunctionSet4bit 0b00101000          // 4-bit data, 2-line display, 5 x 7 font
#define lcd_SetCursor       0b10000000          // set cursor position


void lcd_port_init(void);						// initialize the ports for the lcd module

void lcd_init_4Bit(void);						// initialize the lcd module in a 4 bit mode

void lcd_write_instruction_4Bit(uint8_t);		// prepare to write nibbles to the lcd module

void lcd_write_4Bit(uint8_t);					// write nibbles to the lcd module

void lcd_write_string(char*);				// write one string to the lcd module

void lcd_write_char(uint8_t);					// write ine character to the lcd module

void lcd_write_first_line(char*);

void lcd_write_second_line(char*);