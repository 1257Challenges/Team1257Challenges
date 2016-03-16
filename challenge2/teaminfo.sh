#!/bin/bash

#frc tool
#dependencies:
#curl / wget
#jq
#this

name="https://www.thebluealliance.com"
apiVersion="v2"

authString="frc1257:challengeapp:1c"

outputFile=$1$2

if [ ! -f $outputFile ]
then
        rm $outputFile
fi

curl -s -o $outputFile $name/api/$apiVersion/team/$1$2?X-TBA-App-Id=$authString

cat $outputFile |  jq .
