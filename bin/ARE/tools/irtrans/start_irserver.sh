echo "killing already running irserver processes..."
sudo pkill irserver64
sudo pkill irserver_arm
sudo pkill irserver
sudo fuser -k /dev/ttyIRTrans

DEV_NAME=$(readlink /dev/ttyIRTrans)
if [ -z "$DEV_NAME" ]; then
  echo "did not find link 'ttyIRTrans', using fallback 'ttyUSB0'."
  DEV_NAME="ttyUSB0"
else
  echo "found link 'ttyIRTrans', using real device '$DEV_NAME'."
fi

if [ "$(dpkg --print-architecture)" == "amd64" ]; then
  sudo /opt/asterics-ergo/app/tools/irserver64 /dev/$DEV_NAME
elif [ "$(dpkg --print-architecture)" == "armhf" ]; then
  sudo /opt/asterics-ergo/app/tools/irserver_arm /dev/$DEV_NAME
else
  sudo /opt/asterics-ergo/app/tools/irserver /dev/$DEV_NAME
fi
echo "press any key to close..."
read -n 1 -s
