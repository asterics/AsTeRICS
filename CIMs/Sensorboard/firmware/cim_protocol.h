/*

   AsTeRICS CIM Protocol Packet Frame:
   ===================================

 	Packet ID				2 bytes	"@T" (0x4054 )
 	ARE ID / CIM ID			2 bytes	
	Data Size				2 bytes	0x0000-0x0800
	Packet serial number	1 byte	0x00-0x7f (0x80-0xff for event-replies from CIM )
	CIM-Feature address		2 bytes	
	Request / Reply code	2 bytes	
	-------------------------------------------> 11 bytes = minimum frame length
	Optional data			0-2048 bytes	
	Optional CRC checksum	0 or 4 bytes CRC32
	-------------------------------------------> 2063 bytes = maximum frame length  
*/

#ifndef _CIMPROTOCOL_H_
#define _CIMPROTOCOL_H_

#define CIM_HEADER_LEN      11
#define CIM_FRAME_START     0x5440  // little endian
#define CIM_ID_HID_ACTUATOR 0x0101  // little endian

#define CMD_REQUEST_FEATURELIST   0x00
#define CMD_REPLY_FEATURELIST     0x00
#define CMD_REQUEST_WRITE_FEATURE 0x10
#define CMD_REPLY_WRITE_FEATURE   0x10
#define CMD_REQUEST_READ_FEATURE  0x11
#define CMD_REPLY_READ_FEATURE    0x11
#define CMD_EVENT_REPLY	     0x20
#define CMD_REQUEST_RESET_CIM     0x80
#define CMD_REPLY_RESET_CIM       0x80
#define CMD_REQUEST_START_CIM     0x81
#define CMD_REPLY_START_CIM	      0x81
#define CMD_REQUEST_STOP_CIM      0x82
#define CMD_REPLY_STOP_CIM 	     0x82

#define CIM_ERROR_LOST_PACKETS        (1<<1)
#define CIM_ERROR_CRC_MISMATCH        (1<<2)
#define CIM_ERROR_INVALID_FEATURE     (1<<3)
#define CIM_ERROR_INVALID_ARE_VERSION (1<<4)
#define CIM_ERROR_CIM_NOT_READY       (1<<5)
#define CIM_ERROR_CIM_OTHER_ERROR     (1<<7)

#define CIM_FEATURE_UNIQUENUMBER 0x0000
#define CIM_FEATURE_PERIODICREPORTS 0x0001
#define CIM_FEATURE_SENSORVALUEREPORT 0x0002

#define CIM_PACKET_SIZE 0x2E
#define ARE_PACKET_SIZE 0x3C


struct ARE_frame_t {
   uint16_t packet_id;
   uint16_t are_id;
   uint16_t data_size;
   uint8_t  serial_number;
   uint16_t cim_feature;
   uint16_t request_code;
   uint8_t * data;
   uint32_t crc; 
 };

struct CIM_frame_t {
   uint16_t packet_id;
   uint16_t cim_id;
   uint16_t data_size;
   uint8_t  serial_number;
   uint16_t cim_feature;
   uint16_t reply_code;
   uint8_t * data;
   uint32_t crc; 
 };

void fetch_ARE_packet(void);
void process_ARE_packet(void);
void create_CIM_template (uint8_t *buf);
uint8_t create_CIM_data_packet(struct sensor_data_t *ptr_m_values, uint8_t *buf);
uint8_t send_CIM_packet( uint8_t *buf);

void create_reply_package (uint16_t data_size, uint8_t reply_code);
void reply_FeatureList (void);
void reply_UniqueNumber (void); 
void reply_SensorValueReport (void);
void reply_Acknowledge (uint8_t status_code);

#endif
