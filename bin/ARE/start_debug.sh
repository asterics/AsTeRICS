#!/bin/bash

SCRIPTDIR="$(dirname "$0")"

ARE_CONSOLE_LOGLEVEL="FINE" ARE_DEBUG_STRING="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044" $SCRIPTDIR/start.sh $@
