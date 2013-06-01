#!/bin/sh

echo $0 $*
#sleep 30

/usr/local/bowtie/genome2interval.pl $1 $2 $3
