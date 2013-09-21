#!/bin/sh

echo $0 $*
#sleep 30

perl /home/ubuntu/workspace/zoonimbus/pipeline/sam2bed.pl $1 $2
