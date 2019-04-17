#!/bin/bash
source /etc/profile
pids=`ps aux|grep flume|grep Application|grep flume-1.8|awk -F " " '{print $2}'`
for pid in $pids
do
	echo "running kill -9 $pid"
	kill -9 $pid
done