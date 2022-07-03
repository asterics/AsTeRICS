/*#######################################################################################
Connect AVR to MMC/SD 

Copyright (C) 2004 Ulrich Radig

Bei Fragen und Verbesserungen wendet euch per EMail an

mail@ulrichradig.de

oder im Forum meiner Web Page : www.ulrichradig.de

Dieses Programm ist freie Software. Sie können es unter den Bedingungen der 
GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
weitergeben und/oder modifizieren, entweder gemäß Version 2 der Lizenz oder 
(nach Ihrer Option) jeder späteren Version.

Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, 
daß es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, 
sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT 
FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License. 

Sie sollten eine Kopie der GNU General Public License zusammen mit diesem 
Programm erhalten haben. 
Falls nicht, schreiben Sie an die Free Software Foundation, 
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
#######################################################################################*/

#include "mmc.h"
#include <util/delay.h>
#include <stdlib.h>

// initialize built-in SPI peripheral
void spi_init()
{
    /* enable outputs for MOSI, SCK, SS, input for MISO */
    configure_pin_mosi();
    configure_pin_sck();
    configure_pin_ss();
    configure_pin_miso();
    
    /* initialize SPI with lowest frequency; max. 400kHz during identification mode of card */
    SPCR = (0 << SPIE) | /* SPI Interrupt Disable */
           (1 << SPE)  | /* SPI Enable */
           (0 << DORD) | /* Data Order: MSB first */
           (1 << MSTR) | /* Master mode */
           (0 << CPOL) | /* Clock Polarity: SCK low when idle */
           (0 << CPHA) | /* Clock Phase: sample on rising SCK edge */
           (1 << SPR1) | /* Clock Frequency: f_OSC / 128 */
           (1 << SPR0);
    SPSR &= ~(1 << SPI2X); /* No doubled clock frequency */
}

// set SPI clock for low frequency
inline void spi_lowfreq()
{
    SPCR = (0 << SPIE) | /* SPI Interrupt Disable */
           (1 << SPE)  | /* SPI Enable */
           (0 << DORD) | /* Data Order: MSB first */
           (1 << MSTR) | /* Master mode */
           (0 << CPOL) | /* Clock Polarity: SCK low when idle */
           (0 << CPHA) | /* Clock Phase: sample on rising SCK edge */
           (1 << SPR1) | /* Clock Frequency: f_OSC / 128 */
           (1 << SPR0);
    SPSR &= ~(1 << SPI2X); /* No doubled clock frequency */
}

// set SPI clock for high frequency
inline void spi_highfreq()
{
    SPCR = (0 << SPIE) | /* SPI Interrupt Disable */
           (1 << SPE)  | /* SPI Enable */
           (0 << DORD) | /* Data Order: MSB first */
           (1 << MSTR) | /* Master mode */
           (0 << CPOL) | /* Clock Polarity: SCK low when idle */
           (0 << CPHA) | /* Clock Phase: sample on rising SCK edge */
           (0 << SPR1) | /* Clock Frequency: f_OSC / 4 */
           (0 << SPR0);
    SPSR |= (1 << SPI2X); /* Doubled clock frequency, f_osc / 2 */
}

// read a byte through SPI
inline uint8_t spi_rx()
{
    SPDR = 0xFF; // start blank transfer
    loop_until_bit_is_set(SPSR, SPIF); // wait until transfer finishes
    return SPDR; // return result
}

// send a byte through SPI
inline uint8_t spi_tx(uint8_t d)
{
    SPDR = d; // send
    loop_until_bit_is_set(SPSR, SPIF); // wait until transfer finishes
    return SPDR;
}

// utility to send commands to SD card
/**
 * \ingroup sd_raw
 * Send a command to the memory card which responses with a R1 response (and possibly others).
 *
 * \param[in] command The command to send.
 * \param[in] arg The argument for command.
 * \returns The command answer.
 */

unsigned char mmc_write_command (unsigned char *cmd)
//############################################################################
{
	unsigned char tmp = 0xff;
	unsigned int Timeout = 0;

	//set MMC_Chip_Select to high (MMC/SD-Karte Inaktiv)
	unselect_card();

	//sendet 8 Clock Impulse
	spi_tx(0xFF);
	
	//set MMC_Chip_Select to low (MMC/SD-Karte Aktiv)
	select_card();

	//sendet 6 Byte Commando
	for (unsigned char a = 0;a<0x06;a++) //sendet 6 Byte Commando zur MMC/SD-Karte
	{
		spi_tx(*cmd++);
	}

	//Wartet auf ein gültige Antwort von der MMC/SD-Karte
	while (tmp == 0xff)
	{
		tmp = spi_rx();
		if (Timeout++ > 500)
		{
			break; //Abbruch da die MMC/SD-Karte nicht Antwortet
		}
	}

	return(tmp);
}

uint8_t sd_raw_send_command(uint8_t command, uint32_t arg)
{
    uint8_t response;

    /* wait some clock cycles */
    spi_rx();

    /* send command via SPI */
    spi_tx(0x40 | command);
    spi_tx((arg >> 24) & 0xff);
    spi_tx((arg >> 16) & 0xff);
    spi_tx((arg >> 8) & 0xff);
    spi_tx((arg >> 0) & 0xff);
    switch(command)
    {
        case CMD_GO_IDLE_STATE:
           spi_tx(0x95);
           break;
        case CMD_SEND_IF_COND:
           spi_tx(0x87);
           break;
        default:
           spi_tx(0xff);
           break;
    }
    
    /* receive response */
    for(uint8_t i = 0; i < 10; ++i)
    {
        response = spi_rx();
        if(response != 0xff)
            break;
    }

    return response;
}


//############################################################################
//Routine zur Initialisierung der MMC/SD-Karte (SPI-MODE)
unsigned char mmc_init ()
//############################################################################
{
		//usb_send_str("init begins...\n");
	
		spi_init();
		uint8_t response;
		unselect_card();
		
		/* card needs 74 cycles minimum to start up */
		for(char i = 0; i < 10; i++)
		{
			spi_rx();
		}
		
		/* address card */
		select_card();

		/* reset card */
		
		for(uint16_t i = 0; i <= 0x1ff; ++i)
		{
			response = sd_raw_send_command(CMD_GO_IDLE_STATE, 0);
			
			if(response == (1 << R1_IDLE_STATE))
			break;

			if(i == 0x1ff)
			{
				unselect_card();
				//usb_send_str("Error: SD card won't go idle\n");
				return 1;
			}
		}
		
		
		//printf("Response: %d\r\n", response);
		
		/* determine SD/MMC card type */
		//printf("Reading Card Type\r\n");
		response = sd_raw_send_command(CMD_SEND_IF_COND, 0x100 /* 2.7V - 3.6V */ | 0xaa /* test pattern */);
		if((response & (1 << R1_ILL_COMMAND)) == 0)
		{
			spi_rx();
			spi_rx();
			
			if((spi_rx() & 0x01) == 0)
			{
				/* card operation voltage range doesn't match */
				//usb_send_str("Error: card operation voltage range doesn't match\r\n");
				return 1;
			}
			
			if(spi_rx() != 0xaa)
			{
				/* wrong test pattern */
				//usb_send_str("Error: Wrong test pattern\r\n");
				while (1); // freeze due to error
			}

			/* card conforms to SD 2 card specification */
			sd_raw_card_type |= (1 << SD_RAW_SPEC_2);
			//printf("Card Type: SD Spec 2\r\n");
		}
		else
		{
			sd_raw_send_command(CMD_APP, 0);
			response = sd_raw_send_command(CMD_SD_SEND_OP_COND, 0);
			if((response & (1 << R1_ILL_COMMAND)) == 0)
			{
				/* card conforms to SD 1 card specification */
				sd_raw_card_type |= (1 << SD_RAW_SPEC_1);
				//printf("Card Type: SD Spec 1\r\n");
			}
			else
			{
				//printf("Card Type: MMC\r\n");
			}
		}
		
		/* wait for card to get ready */
		//usb_send_str("Waiting for card to be ready\r\n");
		for(uint16_t i = 0; ; ++i)
		{
			if(sd_raw_card_type & ((1 << SD_RAW_SPEC_1) | (1 << SD_RAW_SPEC_2)))
			{
				uint32_t arg = 0;
				if(sd_raw_card_type & (1 << SD_RAW_SPEC_2))
				arg = 0x40000000;
				sd_raw_send_command(CMD_APP, 0);
				response = sd_raw_send_command(CMD_SD_SEND_OP_COND, arg);
			}
			else
			{
				response = sd_raw_send_command(CMD_SEND_OP_COND, 0);
			}

			if((response & (1 << R1_IDLE_STATE)) == 0)
			break;

			if(i == 0x7fff)
			{
				unselect_card();
				return 1;
			}
		}

		if(sd_raw_card_type & (1 << SD_RAW_SPEC_2))
		{
			if(sd_raw_send_command(CMD_READ_OCR, 0))
			{
				unselect_card();
				return 1;
			}

			if(spi_rx() & 0x40)
			sd_raw_card_type |= (1 << SD_RAW_SPEC_SDHC);

			spi_rx();
			spi_rx();
			spi_rx();
		}

		/* set block size to 512 bytes */
		//printf("Setting block size = 512\r\n");
		if(sd_raw_send_command(CMD_SET_BLOCKLEN, 512))
		{
			unselect_card();
			//usb_send_str("Error: failed to set block size\r\n");
			return 1;
		}

		/* deaddress card */
		unselect_card();

		/* switch to highest SPI frequency possible */
		spi_highfreq();
		
		return 0;
}

//############################################################################
//Routine zum schreiben eines Blocks(512Byte) auf die MMC/SD-Karte
unsigned char mmc_write_sector (unsigned long addr,unsigned char *Buffer)
//############################################################################
{
	unsigned char tmp;
	//Commando 24 zum schreiben eines Blocks auf die MMC/SD - Karte
	unsigned char cmd[] = {0x58,0x00,0x00,0x00,0x00,0xFF}; 
	
	/*Die Adressierung der MMC/SD-Karte wird in Bytes angegeben,
	  addr wird von Blocks zu Bytes umgerechnet danach werden 
	  diese in das Commando eingefügt*/
	  
	addr = addr << 9; //addr = addr * 512
	
	cmd[1] = ((addr & 0xFF000000) >>24 );
	cmd[2] = ((addr & 0x00FF0000) >>16 );
	cmd[3] = ((addr & 0x0000FF00) >>8 );
	
	//Sendet Commando cmd24 an MMC/SD-Karte (Write 1 Block/512 Bytes)

	tmp = mmc_write_command(cmd);
	if (tmp != 0)
		{
		return(tmp);
		}
			
	//Wartet einen Moment und sendet einen Clock an die MMC/SD-Karte
	for (unsigned char a=0;a<100;a++)
		{
		spi_rx();
		}
	
	//Sendet Start Byte an MMC/SD-Karte
	spi_tx(0xFE);	
	
	//Schreiben des Bolcks (512Bytes) auf MMC/SD-Karte
	for (unsigned int a=0;a<512;a++)
		{
		spi_tx(*Buffer++);
		}
	
	//CRC-Byte schreiben
	spi_tx(0xFF); //Schreibt Dummy CRC
	spi_tx(0xFF); //CRC Code wird nicht benutzt
	//Fehler beim schreiben? (Data Response XXX00101 = OK)
	if((spi_rx()&0x1F) != 0x05) return(1);

	//Wartet auf MMC/SD-Karte Bussy
	while (spi_rx() != 0xff){};
	
	//set MMC_Chip_Select to high (MMC/SD-Karte Inaktiv)
	unselect_card();
	
return(0);
}

//############################################################################
//Routine zum lesen des CID Registers von der MMC/SD-Karte (16Bytes)
void mmc_read_block(unsigned char *cmd,unsigned char *Buffer,unsigned int Bytes)
//############################################################################
{	
	unsigned int timeout;
	//Sendet Commando cmd an MMC/SD-Karte
	while (mmc_write_command (cmd) != 0)
			{
			if (timeout++ > 200)	
				{
				//usb_send_str("FAT init stopped, trying again...\n");
				return;
				}			
			}

	//Wartet auf Start Byte von der MMC/SD-Karte (FEh/Start Byte)
	
	while (spi_rx() != 0xfe){};

	//Lesen des Bolcks (normal 512Bytes) von MMC/SD-Karte
	for (unsigned int a=0;a<Bytes;a++)
		{
		*Buffer++ = spi_rx();
		}
	//CRC-Byte auslesen
	spi_rx();//CRC - Byte wird nicht ausgewertet
	_delay_ms(1000);
	spi_rx();//CRC - Byte wird nicht ausgewertet
	
	//set MMC_Chip_Select to high (MMC/SD-Karte Inaktiv)
	unselect_card();
	
	return;
}

uint8_t mmc_read_sector(unsigned long offset, unsigned char *buffer)
{
	offset = offset * 512;
	uint16_t length = 512;
	offset_t block_address;
	uint16_t block_offset;
	uint16_t read_length;
	while(length > 0)
	{
		/* determine byte count to read at once */
		block_offset = offset & 0x01ff;
		block_address = offset - block_offset;
		read_length = 512 - block_offset; /* read up to block border */
		if(read_length > length)
		read_length = length;
		
		/* address card */
		select_card();

		/* send single block request */

		if(sd_raw_send_command(CMD_READ_SINGLE_BLOCK, (sd_raw_card_type & (1 << SD_RAW_SPEC_2) ? block_address / 512 : block_address)))
		{
			unselect_card();
			return 1;
		}

		/* wait for data block (start byte 0xfe) */
		while(spi_rx() != 0xfe);

		/* read byte block */
		uint16_t read_to = block_offset + read_length;
		for(uint16_t i = 0; i < 512; ++i)
		{
			uint8_t b = spi_rx();
			if(i >= block_offset && i < read_to)
			*buffer++ = b;
		}
		
		/* read crc16 */
		spi_rx();
		spi_rx();
		
		/* deaddress card */
		unselect_card();

		/* let card some time to finish */
		spi_rx();

		length -= read_length;
		offset += read_length;
	}

	return 0;
}

//############################################################################
//Routine zum lesen des CID Registers von der MMC/SD-Karte (16Bytes)
unsigned char mmc_read_cid (unsigned char *Buffer)
//############################################################################
{
	//Commando zum lesen des CID Registers
	unsigned char cmd[] = {0x4A,0x00,0x00,0x00,0x00,0xFF}; 
	
	mmc_read_block(cmd,Buffer,16);

	return(0);
}

//############################################################################
//Routine zum lesen des CSD Registers von der MMC/SD-Karte (16Bytes)
unsigned char mmc_read_csd (unsigned char *Buffer)
//############################################################################
{	
	//Commando zum lesen des CSD Registers
	unsigned char cmd[] = {0x49,0x00,0x00,0x00,0x00,0xFF};
	
	mmc_read_block(cmd,Buffer,16);

	return(0);
}