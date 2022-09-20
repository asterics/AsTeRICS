


uint8_t deviceSorting;
uint16_t joystick_speed;
uint16_t joystick_sensibility;
char IRCodeFile[12];

uint8_t sending_mode;
uint8_t record_mode;

uint8_t IRDeviceCounter;		// number of device
uint16_t IRDeviceSpecCounter;	// counter for type, name, function
uint8_t characterCounter;


char CurrentName[17];
char CurrentType[17];
char CurrentFunction[17];
uint8_t CurrentIRCode[3];
uint8_t IRDeviceFunctionCounter;

void AddReturnToDeviceList(void);

void LoadSettings(void);

void LoadDevices(void);

int getIRDeviceTypeName(unsigned char *BufferPointerNL);

int getIRDeviceFunction(unsigned char *BufferPointerNL, unsigned char *Type, unsigned char *Name);

int getIRDeviceIRCode(unsigned char *BufferPointerNL, unsigned char *Type, unsigned char *Name, unsigned char *Function);

void SortDevices(uint8_t);

void SortFunctions(uint8_t);