#!/bin/bash
source /etc/profile
mkdir -p /var/log/flume
cd /home/sinorail/apache-flume-1.8.0-bin/conf/noCloud
nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-system -n a1 >flume-system.log 2>&1 &
nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-bde -n a1 >flume-bde.log 2>&1 &
nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-cmagent -n a1 >flume-cmagent.log 2>&1 &
nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-cloudera -n a1 >flume-cloudera.log 2>&1 &
nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-flume -n a1 >flume-flume.log 2>&1 &

if [ -d "/var/log/tomcat" ];then
	c1=`ls -A /var/log/tomcat|wc -w`
	if [ $c1 > 0 ];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-tomcat -n a1 >flume-tomcat.log 2>&1 &
	fi
fi

if [ -d "/var/log/elasticsearch" ];then
	c1=`ls -A /var/log/elasticsearch|wc -w`
	if [ $c1 > 0 ];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-elasticsearch -n a1 >flume-elasticsearch.log 2>&1 &
	fi
fi

if [ -d "/var/log/nginx" ];then
	c1=`ls -A /var/log/nginx|wc -w`
	if [ $c1 > 0 ];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-nginx -n a1 >flume-nginx.log 2>&1 &
	fi
fi

if [ -d "/var/log/redis" ];then
	c1=`ls -A /var/log/redis|wc -w`
	if [ $c1 > 0 ];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-redis -n a1 >flume-redis.log 2>&1 &
	fi
fi

if [[ -d "/var/log/postgresql" ]];then
	c1=`ls -A /var/log/postgresql|wc -w`
	if [[ $c1 > 0 ]];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-db -n a1 >flume-db.log 2>&1 &
	fi
fi

if [[ -d "/var/log/spark/monitor" || -d "/var/log/spark/logStatistics" ]];then
	c1=`ls -A /var/log/spark/monitor|wc -w`
	c2=`ls -A /var/log/spark/logStatistics|wc -w`
	if [[ $c1 > 0 || $c2 > 0 ]];then
		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-sparkapp -n a1 >flume-sparkapp.log 2>&1 &
	fi
fi
