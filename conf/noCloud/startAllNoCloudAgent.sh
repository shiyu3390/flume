#!/bin/bash
kafkaAddress=$1
if [ -n "$kafkaAddress" ];then
  sed -i "s/iad66:9092,iad67:9092,iad71:9092/$kafkaAddress/g" /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/flume-*
  for hostname in $(cat /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/hostnames)
  do
      scp -r /home/sinorail/apache-flume-1.8.0-bin $hostname:/home/sinorail/
      ssh -l root $hostname "chmod +x /home/sinorail/apache-flume-1.8.0-bin/conf/stopFlume.sh"
      ssh -l root $hostname "sh /home/sinorail/apache-flume-1.8.0-bin/conf/stopFlume.sh"
      ssh -l root $hostname "rm -rf /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/*.json"
      ssh -l root $hostname "rm -rf /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/*.log"
      ssh -l root $hostname "chmod +x /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng"
      ssh -l root $hostname "chmod +x /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/*.sh"
      ssh -l root $hostname "sh /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud/startNoCloudFlume.sh"
  done
else
  echo "kafkaAddress不能为空,按以下方式运行脚本,举例如下："
  echo "sh startAllNoCloudAgent.sh iad66:9092,iad67:9092,iad71:9092"
  exit 0
fi

