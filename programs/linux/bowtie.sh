#!/bin/bash

echo $0 $*
#sleep 30

tmp=`echo $$`

#bowtie-build $1 index-$tmp
#bowtie -f -p 8 --sam-nohead -k 2 index-$tmp $2 $3
#rm -rf index-$tmp.*
/home/ubuntu/bowtie-0.12.9/bowtie -f -p 8 --sam-nohead -k 2 "/home/ubuntu/workspace/zoonimbus/pipeline/chr1Index" $2 $3


