#!/bin/sh

echo $0 $*
#sleep 30

perl /home/ubuntu/workspace/zoonimbus/pipeline/genome2interval.pl $1 $2 $3
