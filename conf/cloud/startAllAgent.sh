#!/bin/bash
PASSWD=$1
region=$2
kafkaAddress=$3
function expect_run_cmd {
  expect -c "set timeout -1;
        spawn $1;
        expect {
            *(yes/no)* {send -- yes\r;exp_continue;}
            *assword:* {send -- $PASSWD\r;exp_continue;}
            *id_rsa):* {send -- \r;exp_continue}
            *y/n)?* {send -- y\r;exp_continue}
            *passphrase* {send -- \r;exp_continue}
            eof        {exit 0;}
        }";
}
if [ -n "$kafkaAddress" ];then
  sed -i "s/iad66:9092,iad67:9092,iad71:9092/$kafkaAddress/g" /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/flume-*
  for hostname in $(cat /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/hostnames)
  do
      expect_run_cmd "scp -r /home/sinorail/apache-flume-1.8.0-bin $hostname:/home/sinorail/"
      expect_run_cmd "ssh -l root $hostname \"chmod +x /home/sinorail/apache-flume-1.8.0-bin/conf/stopFlume.sh\""
      expect_run_cmd "ssh -l root $hostname \"sh /home/sinorail/apache-flume-1.8.0-bin/conf/stopFlume.sh\""
      expect_run_cmd "ssh -l root $hostname \"rm -rf /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/*.json\""
      expect_run_cmd "ssh -l root $hostname \"rm -rf /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/*.log\""
      expect_run_cmd "scp /etc/hosts $hostname:/etc"
      expect_run_cmd "ssh -l root $hostname \"chmod +x /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/*.sh\""
      expect_run_cmd "ssh -l root $hostname \"sh /home/sinorail/apache-flume-1.8.0-bin/conf/cloud/startFlume.sh $region\""
  done
else
  echo "kafkaAddress不能为空,按以下方式运行脚本,第一个参数是当前用户的密码,第二个参数是云的region名称,第三个是kafka集群的地址,举例如下："
  echo "sh startAllAgent.sh passw0rd default iad66:9092,iad67:9092,iad71:9092"
  exit 0
fi
