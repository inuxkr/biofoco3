#!/bin/sh

echo $0 $*
#sleep 30

/usr/local/bowtie/sam2bed.pl $1 $2
