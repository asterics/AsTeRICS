#!/bin/bash
for I in *
do
	adb push $I /sdcard/yaak
done
