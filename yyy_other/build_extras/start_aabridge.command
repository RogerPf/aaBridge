#!/bin/bash

#    if Java is not in your path then add it to the command line below 

set -x  #echo on

java -cp $(dirname $0) -Xmx100m com.rogerpf.aabridge.controller.AaBridge

