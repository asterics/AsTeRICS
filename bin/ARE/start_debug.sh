#!/bin/sh

SCRIPTDIR="$(dirname "$0")"

ARE_LOG_STRING="error_level:FINE" ARE_DEBUG_STRING="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044" $SCRIPTDIR/start.sh $@
