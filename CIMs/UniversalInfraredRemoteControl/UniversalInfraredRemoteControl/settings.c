
#include <avr/io.h>
#include "settings.h"
#include "fat.h"
#include "interface.h"
#include "Buffer.h"

void LoadSettingsWithoutSDcard()
{
	record_mode = 0;
	sending_mode = 0;
	joystick_speed = 61;		// 0,5 sec
	MenuLevel1_Tree[0] = 0;		// subcategories
	MenuLevel1_Tree[1] = 0;		// subcategories
}

void LoadSettingsFromSDcard()
{
	record_mode = 0;
	sending_mode = 0;
	
	Dir_Attrib = 0;
	Size = 0;	
	
	//Read CONFIG.CSV
	ClustervarRoot = 0;		//suche im Root Verzeichnis
	if (fat_search_file((unsigned char *)"CONFIG.CSV",&ClustervarRoot,&Size,&Dir_Attrib,Buffer) == 1)
	{
		//usb_send_str("\nFile Found!!\r\n");
		//Lese File und gibt es auf der seriellen Schnittstelle aus
		//fat_write_file(Clustervar,Buffer,0);
		for (int b = 0;b<52;b++)
		{
			fat_read_file (ClustervarRoot,Buffer,b);
			if(Buffer[0]!=NULL)
			{
					//Go through content
					char delimiter1[] = ",";
					char delimiter2[] = "\r\n";
					char *part;
					
					//Joystick Speed
					part = strtok(Buffer, delimiter1);					
					part = strtok(NULL, delimiter2);
					joystick_speed = atoi(part);
					
					//Joystick Speed
					part = strtok(NULL, delimiter1);
					part = strtok(NULL, delimiter2);
					joystick_sensibility = atoi(part);
					
					//IR Code File
					part = strtok(NULL, delimiter1);
					part = strtok(NULL, delimiter2);
					strcpy(IRCodeFile, part);
					strcpy(IRCodeFileSetting, IRCodeFile);
					
					//Sort
					part = strtok(NULL, delimiter1);
					part = strtok(NULL, delimiter2);
					deviceSorting = atoi(part);
					switch(deviceSorting)
					{
						case 0:
							strcpy(SortingSettings,"No Sort.");
							break;
						case 1:
							strcpy(SortingSettings,"Name");
							break;
						case 2:
							strcpy(SortingSettings,"Type");
							break;
					}
					
					break;
			}
			else
			{
				break;
			}
		}
	}
	
	
}

/*
Load devices from database
*/
void LoadDevices()
{
	//Read IRCodes.CSV
	ClustervarRoot = 0;		//search in Root Folder
	if (fat_search_file((unsigned char *)IRCodeFile,&ClustervarRoot,&Size,&Dir_Attrib,Buffer) == 1)
	{
		//Go through content
		IRDeviceSpecCounter = 0;
		characterCounter = 0;
		IRDeviceCounter = 0;
		for (int b = 0;b<128;b++)
		{
			fat_read_file (ClustervarRoot,Buffer,b);
			if(getIRDeviceTypeName(Buffer)==0x01)
			{
				break;
			}
		}
		SortDevices(deviceSorting);		// Sort the list of devices
		AddReturnToDeviceList();		// Add a string to the device list for a return button in the menu
	}
}

/*
Reconstruct a list of devices out of raw data snippets of 512 byte
*/
int getIRDeviceTypeName(unsigned char *BufferPointerNL)
{
	for(uint16_t i = 0; i<512; i++)
	{
		
		if(*BufferPointerNL==0)	// End of file
		{
			return 0x01;
		}
		switch(*BufferPointerNL)
		{
			case '\r':
			case '\n':
				//new IR Code per each line
				
				if(IRDeviceCounter>0)
				{
					for(uint8_t j = 0; j<=(IRDeviceCounter-1);j++)	// Check if this device is already in the list
					{
						if(!strcmp(devices[IRDeviceCounter].DeviceType,devices[j].DeviceType))
						{
							if(!strcmp(devices[IRDeviceCounter].DeviceName,devices[j].DeviceName))
							{
								IRDeviceCounter--;
								break;
							}
						}
					}
					
				}
				IRDeviceSpecCounter = 0;
				IRDeviceCounter++;
				BufferPointerNL++;
				i++;
				characterCounter = 0;
				break;
			case ',':		// new value per each comma
				IRDeviceSpecCounter++;
				characterCounter = 0;
				break;
			default:
				switch(IRDeviceSpecCounter)
				{
					case 0:			// type
						devices[IRDeviceCounter].DeviceType[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
						}
						devices[IRDeviceCounter].DeviceType[characterCounter] = 0;
						break;
					case 1:			// name
						devices[IRDeviceCounter].DeviceName[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
						}
						devices[IRDeviceCounter].DeviceName[characterCounter] = 0;
						break;
					case 2:			// function, not required in this function
						break;
					default:		// IR Data, not required in this function
						break;
				}
				break;
			
		}
		BufferPointerNL++;
	}
	
	MenuLevel1_Tree[0] = IRDeviceCounter+1;		// subcategories for LCD menu
	MenuLevel1_Tree[1] = IRDeviceCounter+1;		// subcategories for LCD menu
	
	return 0x00;
}

int getIRDeviceFunction(unsigned char *BufferPointerNL, unsigned char *Type, unsigned char *Name)
{
	for(uint16_t i = 0; i<512; i++)
	{
		if(*BufferPointerNL==0)
		{
			return 0x01;
		}
		switch(*BufferPointerNL)
		{
			case '\r':
			case '\n':
				//new IR Code
				if(!strcmp(CurrentName,Name))
				{
					if(!strcmp(CurrentType,Type))
					{
						strcpy(functions[IRDeviceFunctionCounter].Functions, CurrentFunction);
						IRDeviceFunctionCounter++;
					}
				}
				IRDeviceSpecCounter = 0;
				BufferPointerNL++;
				i++;
				characterCounter = 0;
				break;
			case ',':
				if(IRDeviceSpecCounter<65535)
				{
					IRDeviceSpecCounter++;
				}				
				characterCounter = 0;
				break;
				
			default:
				switch(IRDeviceSpecCounter)
				{
					case 0:			// type
						CurrentType[characterCounter] = *BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
						}
						CurrentType[characterCounter] = 0;
						break;
					case 1:			// name
						CurrentName[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
						}
						CurrentName[characterCounter] = 0;
						break;
					case 2:			// function
						CurrentFunction[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
						}
						CurrentFunction[characterCounter] = 0;
						break;
					default:			// IR Data
						break;
				}
				break;			
		}
		BufferPointerNL++;
	}
	
	FunctionCounter = IRDeviceFunctionCounter;
	return 0x00;
}

int getIRDeviceIRCode(unsigned char *BufferPointerNL, unsigned char *Type, unsigned char *Name, unsigned char *Function)
{
	for(uint16_t i = 0; i<512; i++)
	{
		
		if(*BufferPointerNL==0)
		{
			return 0x01;
		}
		switch(*BufferPointerNL)
		{
			case '\r':
			case '\n':
				//new IR Code
				if(!strcmp(CurrentName,Name))
				{
					if(!strcmp(CurrentType,Type))
					{
						if(!strcmp(CurrentFunction,Function))
						{
							return 0x00;
						}
					}
				}
				irBufferPointer = irBuffer;
				irBufferCounter = 0;
				IRDeviceSpecCounter = 0;
				BufferPointerNL++;
				i++;
				characterCounter = 0;
				break;
			case ',':
				if(IRDeviceSpecCounter>2)
				{
					if(irBufferCounter<511)
					{
						*irBufferPointer = atoi(CurrentIRCode);
						irBufferPointer++;
						irBufferCounter++;
					}					
				}
				if(IRDeviceSpecCounter<65535)
				{
					IRDeviceSpecCounter++;
				}				
				characterCounter = 0;
				break;
			
			default:
				switch(IRDeviceSpecCounter)
				{
					case 0:			// type
						CurrentType[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
							CurrentType[characterCounter] = 0;
						}						
						break;
					case 1:			// name
						CurrentName[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
							CurrentName[characterCounter] = 0;
						}						
						break;
					case 2:			// function
						CurrentFunction[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<16)
						{
							characterCounter++;
							CurrentFunction[characterCounter] = 0;
						}
						break;
					default:			// IR Data
						CurrentIRCode[characterCounter] = (char)*BufferPointerNL;
						if(characterCounter<2)
						{
							characterCounter++;
							CurrentIRCode[characterCounter] = 0;
						}						
						break;
				}
				break;
		}
		BufferPointerNL++;
	}
	return 0x00;
}

int deviceName_cmp (const struct Devices_t *struct1, const struct Devices_t *struct2)
{
	return strcmp (struct1->DeviceName, struct2->DeviceName);		// Compare the two names
}

int deviceType_cmp (const struct Devices_t *struct1, const struct Devices_t *struct2)
{
	return strcmp (struct1->DeviceType, struct2->DeviceType);		// Compare the two types
}

void SortDevices(uint8_t sortingType)		// 0 = no sorting, 1 = sort after DeviceType, 2 = sort after DeviceName
{
	switch(sortingType)
	{
		case 0:				// No sorting
			break;
		case 1:				// sort after DeviceName
			qsort (devices, IRDeviceCounter , sizeof (struct Devices_t), deviceName_cmp);
			break;
		case 2:				// sort after DeviceType
			qsort (devices, IRDeviceCounter , sizeof (struct Devices_t), deviceType_cmp);
			break;
	}
}

int deviceFunction_cmp (const struct Functions_t *function1, const struct Functions_t *function2)
{
	return strcmp (function1->Functions,function2->Functions);
}

void SortFunctions(uint8_t sortingType)		// 0 = no sorting, 1 = sort after DeviceType, 2 = sort after DeviceName
{
	switch(sortingType)
	{
		case 0:				// No sorting
			break;
		case 1:				// sort after Function
		case 2:
			qsort (functions, FunctionCounter , sizeof (struct Functions_t), deviceFunction_cmp);
			break;
	}
}

void AddReturnToDeviceList()
{
	for(uint8_t i = (IRDeviceCounter-1); i!=255; i--)
	{
		if(i<NUMBER_OF_DEVICES+1)
		{
			strcpy(devices[i+1].DeviceName, devices[i].DeviceName);
			strcpy(devices[i+1].DeviceType, devices[i].DeviceType);
		}		
	}
	strcpy(devices[0].DeviceName,"return");
	strcpy(devices[0].DeviceType,"");
}

void AddReturnToFunctionList(struct Functions_t *functionToEdit)
{
	for(uint8_t i = (FunctionCounter-1); i!=255; i--)
	{
		if(i<NUMBER_OF_DEVICES+1)
		{
			strcpy(functionToEdit[i+1].Functions, functionToEdit[i].Functions);
		}
	}
	strcpy(functionToEdit[0].Functions,"return");
}

