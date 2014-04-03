// Definitions of the CIM protocol

#define CIM_HEADER_LEN      11
#define CIM_FRAME_START     0x5440  // little endian
#define CIM_ID_TEENSY      0xa002

#define CMD_REQUEST_FEATURELIST   0x00
#define CMD_REPLY_FEATURELIST     0x00
#define CMD_REQUEST_WRITE_FEATURE 0x10
#define CMD_REPLY_WRITE_FEATURE   0x10
#define CMD_REQUEST_READ_FEATURE  0x11
#define CMD_REPLY_READ_FEATURE    0x11
#define CMD_EVENT_REPLY			  0x20
#define CMD_REQUEST_RESET_CIM 	  0x80
#define CMD_REPLY_RESET_CIM       0x80
#define CMD_REQUEST_START_CIM 	  0x81
#define CMD_REPLY_START_CIMM      0x81
#define CMD_REQUEST_STOP_CIM 	  0x82
#define CMD_REPLY_STOP_CIM 	      0x82

#define CIM_STATUS_CRC      		  (1<<0)
#define CIM_ERROR_LOST_PACKETS        (1<<1)
#define CIM_ERROR_CRC_MISMATCH        (1<<2)
#define CIM_ERROR_INVALID_FEATURE     (1<<3)
#define CIM_ERROR_INVALID_ARE_VERSION (1<<4)
#define CIM_ERROR_CIM_NOT_READY       (1<<5)
#define CIM_ERROR_CIM_OTHER_ERROR     (1<<7)

#define TEENSY_CIM_FEATURE_UNIQUENUMBER     0x00
#define TEENSY_CIM_FEATURE_SET_PPM_VALUES   0x01


#define DATABUF_SIZE 16

struct ARE_frame_t {
   uint16_t packet_id;
   uint16_t are_id;
   uint16_t data_size;
   uint8_t  serial_number;
   uint16_t cim_feature;
   uint16_t request_code;
   uint8_t  data[DATABUF_SIZE];
   uint32_t crc; 
 };

struct CIM_frame_t {
   uint16_t packet_id;
   uint16_t cim_id;
   uint16_t data_size;
   uint8_t  serial_number;
   uint16_t cim_feature;
   uint16_t reply_code;
   uint8_t  data[DATABUF_SIZE];
   uint32_t crc; 
 };


void reply_FeatureList(void);
void reply_UniqueNumber(void);
void reply_Acknowledge(void);
void reply_DataFrame(void);

void parse_CIM_protocol(void);
void init_CIM_frame (void);
