#!/bin/sh

ARQ_DIR=/home/ubuntu/server
tmp=`echo $$`

cd $ARQ_DIR
tar -vzxf $1
