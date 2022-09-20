################################################################################
# Automatically-generated file. Do not edit!
################################################################################

SHELL := cmd.exe
RM := rm -rf

USER_OBJS :=

LIBS := 
PROJ := 

O_SRCS := 
C_SRCS := 
S_SRCS := 
S_UPPER_SRCS := 
OBJ_SRCS := 
ASM_SRCS := 
PREPROCESSING_SRCS := 
OBJS := 
OBJS_AS_ARGS := 
C_DEPS := 
C_DEPS_AS_ARGS := 
EXECUTABLES := 
OUTPUT_FILE_PATH :=
OUTPUT_FILE_PATH_AS_ARGS :=
AVR_APP_PATH :=$$$AVR_APP_PATH$$$
QUOTE := "
ADDITIONAL_DEPENDENCIES:=
OUTPUT_FILE_DEP:=
LIB_DEP:=

# Every subdirectory with source files must be described here
SUBDIRS := 


# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS +=  \
../ADC.c \
../Buffer.c \
../Cimprotocol.c \
../fat.c \
../Init.c \
../interface.c \
../lcd.c \
../mmc.c \
../RotaryEncoder.c \
../SendReceive.c \
../settings.c \
../UniversalInfraredRemoteControl.c \
../usb_serial.c


PREPROCESSING_SRCS += 


ASM_SRCS += 


OBJS +=  \
ADC.o \
Buffer.o \
Cimprotocol.o \
fat.o \
Init.o \
interface.o \
lcd.o \
mmc.o \
RotaryEncoder.o \
SendReceive.o \
settings.o \
UniversalInfraredRemoteControl.o \
usb_serial.o

OBJS_AS_ARGS +=  \
ADC.o \
Buffer.o \
Cimprotocol.o \
fat.o \
Init.o \
interface.o \
lcd.o \
mmc.o \
RotaryEncoder.o \
SendReceive.o \
settings.o \
UniversalInfraredRemoteControl.o \
usb_serial.o

C_DEPS +=  \
ADC.d \
Buffer.d \
Cimprotocol.d \
fat.d \
Init.d \
interface.d \
lcd.d \
mmc.d \
RotaryEncoder.d \
SendReceive.d \
settings.d \
UniversalInfraredRemoteControl.d \
usb_serial.d

C_DEPS_AS_ARGS +=  \
ADC.d \
Buffer.d \
Cimprotocol.d \
fat.d \
Init.d \
interface.d \
lcd.d \
mmc.d \
RotaryEncoder.d \
SendReceive.d \
settings.d \
UniversalInfraredRemoteControl.d \
usb_serial.d

OUTPUT_FILE_PATH +=UniversalInfraredRemoteControl.elf

OUTPUT_FILE_PATH_AS_ARGS +=UniversalInfraredRemoteControl.elf

ADDITIONAL_DEPENDENCIES:=

OUTPUT_FILE_DEP:= ./makedep.mk

LIB_DEP+= 

# AVR32/GNU C Compiler



























./%.o: .././%.c
	@echo Building file: $<
	@echo Invoking: AVR/GNU C Compiler : 3.4.2
	$(QUOTE)C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-gcc.exe$(QUOTE)  -funsigned-char -funsigned-bitfields -DDEBUG  -O1 -ffunction-sections -fdata-sections -fpack-struct -fshort-enums -mrelax -g2 -Wall -mmcu=at90usb1286 -c -std=gnu99 -MD -MP -MF "$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -MT"$(@:%.o=%.o)"   -o "$@" "$<"
	@echo Finished building: $<
	



# AVR32/GNU Preprocessing Assembler



# AVR32/GNU Assembler




ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(OUTPUT_FILE_PATH) $(ADDITIONAL_DEPENDENCIES)

$(OUTPUT_FILE_PATH): $(OBJS) $(USER_OBJS) $(OUTPUT_FILE_DEP) $(LIB_DEP)
	@echo Building target: $@
	@echo Invoking: AVR/GNU Linker : 3.4.2
	$(QUOTE)C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-gcc.exe$(QUOTE) -o$(OUTPUT_FILE_PATH_AS_ARGS) $(OBJS_AS_ARGS) $(USER_OBJS) $(LIBS) -Wl,-Map="UniversalInfraredRemoteControl.map" -Wl,--start-group -Wl,-lm  -Wl,--end-group -Wl,--gc-sections -mrelax -mmcu=at90usb1286 
	@echo Finished building target: $@
	"C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-objcopy.exe" -O ihex -R .eeprom -R .fuse -R .lock -R .signature  "UniversalInfraredRemoteControl.elf" "UniversalInfraredRemoteControl.hex"
	"C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-objcopy.exe" -j .eeprom  --set-section-flags=.eeprom=alloc,load --change-section-lma .eeprom=0  --no-change-warnings -O ihex "UniversalInfraredRemoteControl.elf" "UniversalInfraredRemoteControl.eep" || exit 0
	"C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-objdump.exe" -h -S "UniversalInfraredRemoteControl.elf" > "UniversalInfraredRemoteControl.lss"
	"C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-objcopy.exe" -O srec -R .eeprom -R .fuse -R .lock -R .signature  "UniversalInfraredRemoteControl.elf" "UniversalInfraredRemoteControl.srec"
	"C:\Program Files (x86)\Atmel\Atmel Toolchain\AVR8 GCC\Native\3.4.2.1002\avr8-gnu-toolchain\bin\avr-size.exe" "UniversalInfraredRemoteControl.elf"
	
	





# Other Targets
clean:
	-$(RM) $(OBJS_AS_ARGS) $(EXECUTABLES)  
	-$(RM) $(C_DEPS_AS_ARGS)   
	rm -rf "UniversalInfraredRemoteControl.elf" "UniversalInfraredRemoteControl.a" "UniversalInfraredRemoteControl.hex" "UniversalInfraredRemoteControl.lss" "UniversalInfraredRemoteControl.eep" "UniversalInfraredRemoteControl.map" "UniversalInfraredRemoteControl.srec"
	