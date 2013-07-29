

First version of the Arduino CIM, FHTW,
for the Atmel AVR ATmega328 microcontroller mounted on the "Arduino UNO" PCB. 

The firmware provides access to the digital I/O Pins and 6 ADC Channels
(PWM may be enabled in the future) via the Arduino ARE plugin.

Please note that the auto-reset feature of the Arduino PCB (reset pin is driven by the DTR signal of the serial connection)
has to be disabled by opening the solder jumper "RESET-EN", because the bootloader prevents the ARE from recognizing the CIM at startup.

