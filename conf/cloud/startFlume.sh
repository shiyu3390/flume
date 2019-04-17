#!/bin/bash
source /etc/profile
mkdir -p /var/log/flume
chmod +x /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng
cd /home/sinorail/apache-flume-1.8.0-bin/conf/cloud
region=""
if [ -n "$1" ];then
	region=$1
elif [ -f "/root/.admin-openrc.sh" ];then
	region=`cat /root/.admin-openrc.sh |awk -F "OS_REGION_NAME=" '{if ($2 != "") print $2}'`
	echo "~/.admin-openrc.sh中的region为$region"
elif [ -f "/root/.admin-openrc" ];then
	region=`cat /root/.admin-openrc |awk -F "OS_REGION_NAME=" '{if ($2 != "") print $2}'`
	echo "~/.admin-openrc中的region为$region"
fi

if [ -n "$region" ];then
  regionName=$(echo $region)
  echo "regionName is $regionName"
  sed -i "s/BJPOC-REGION1/$regionName/g" flume-*
  nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-msg -n a1 >flume-msg.log 2>&1 &
  if [[ -d "/var/log/nova" || -d "/var/log/kolla/nova" ]];then
  	if [ -d "/var/log/kolla/nova" ];then
  		sed -i 's/\/var\/log\/nova/\/var\/log\/kolla\/nova/g' flume-nova
  	fi
  	c1=`ls -A /var/log/nova|wc -w`
  	c2=`ls -A /var/log/kolla/nova|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-nova -n a1 >flume-nova.log 2>&1 &
  	else
  	  echo "nova的日志目录为空"
  	fi
  else
    echo "nova的日志目录不存在"
  fi
  
  if [[ -d "/var/log/neutron" || -d "/var/log/kolla/neutron" ]];then
  	if [ -d "/var/log/kolla/neutron" ];then
  		sed -i 's/\/var\/log\/neutron/\/var\/log\/kolla\/neutron/g' flume-neutron
                if [ ! -f "/var/log/neutron/.*dhcp-agent.log" ];then
                    sed -i 's/kolla\/neutron\/.*dhcp-agent.log/neutron\/.*dhcp-agent.log/g' flume-neutron
                fi                
  	fi
  	c1=`ls -A /var/log/neutron|wc -w`
  	c2=`ls -A /var/log/kolla/neutron|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-neutron -n a1 >flume-neutron.log 2>&1 &
  	else
  	  echo "neutron的日志目录为空"
  	fi
  else
    echo "neutron的日志目录不存在"
  fi
  
  if [[ -d "/var/log/cinder" || -d "/var/log/kolla/cinder" ]];then
  	if [ -d "/var/log/kolla/cinder" ];then
  		sed -i 's/\/var\/log\/cinder/\/var\/log\/kolla\/cinder/g' flume-cinder
  	fi
  	c1=`ls -A /var/log/cinder|wc -w`
  	c2=`ls -A /var/log/kolla/cinder|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-cinder -n a1 >flume-cinder.log 2>&1 &
  	else
  	  echo "cinder的日志目录为空"
  	fi
  else
    echo "cinder的日志目录不存在"
  fi
  
  if [[ -d "/var/log/glance" || -d "/var/log/kolla/glance" ]];then
  	if [ -d "/var/log/kolla/glance" ];then
  		sed -i 's/\/var\/log\/glance/\/var\/log\/kolla\/glance/g' flume-glance
  	fi
  	c1=`ls -A /var/log/glance|wc -w`
  	c2=`ls -A /var/log/kolla/glance|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-glance -n a1 >flume-glance.log 2>&1 &
  	else
  	  echo "glance的日志目录为空"
  	fi
  else
    echo "glance的日志目录不存在"
  fi
  
  if [[ -d "/var/log/keystone" || -d "/var/log/kolla/keystone" ]];then
  	if [ -d "/var/log/kolla/keystone" ];then
  		sed -i 's/\/var\/log\/keystone/\/var\/log\/kolla\/keystone/g' flume-keystone
  	fi
  	c1=`ls -A /var/log/keystone|wc -w`
  	c2=`ls -A /var/log/kolla/keystone|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-keystone -n a1 >flume-keystone.log 2>&1 &
  	else
  	  echo "keystone的日志目录为空"
  	fi
  else
    echo "keystone的日志目录不存在"
  fi
  
  if [[ -d "/var/log/httpd" || -d "/var/log/kolla/httpd" ]];then
  	if [ -d "/var/log/kolla/httpd" ];then
  		sed -i 's/\/var\/log\/httpd/\/var\/log\/kolla\/httpd/g' flume-httpd
  	fi
  	c1=`ls -A /var/log/httpd|wc -w`
  	c2=`ls -A /var/log/kolla/httpd|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-httpd -n a1 >flume-httpd.log 2>&1 &
  	else
  	  echo "httpd的日志目录为空"
  	fi
  else
    echo "httpd的日志目录不存在"
  fi
  
  if [[ -d "/var/log/ceph" || -d "/var/log/kolla/ceph" ]];then
  	if [ -d "/var/log/kolla/ceph" ];then
  		sed -i 's/\/var\/log\/ceph/\/var\/log\/kolla\/ceph/g' flume-ceph
  	fi
  	c1=`ls -A /var/log/ceph|wc -w`
  	c2=`ls -A /var/log/kolla/ceph|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-ceph -n a1 >flume-ceph.log 2>&1 &
  	else
  	  echo "ceph的日志目录为空"
  	fi
  else
    echo "ceph的日志目录不存在"
  fi
  
  if [[ -d "/var/log/haproxy" || -d "/var/log/kolla/haproxy" ]];then
  	if [ -d "/var/log/kolla/haproxy" ];then
  		sed -i 's/\/var\/log\/haproxy/\/var\/log\/kolla\/haproxy/g' flume-haproxy
  	fi
  	c1=`ls -A /var/log/haproxy|wc -w`
  	c2=`ls -A /var/log/kolla/haproxy|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-haproxy -n a1 >flume-haproxy.log 2>&1 &
  	else
  	  echo "haproxy的日志目录为空"
  	fi
  else
    echo "haproxy的日志目录不存在"
  fi
  
  if [[ -d "/var/log/keepalived" || -d "/var/log/kolla/keepalived" ]];then
  	if [ -d "/var/log/kolla/keepalived" ];then
  		sed -i 's/\/var\/log\/keepalived/\/var\/log\/kolla\/keepalived/g' flume-keepalived
  	fi
  	c1=`ls -A /var/log/keepalived|wc -w`
  	c2=`ls -A /var/log/kolla/keepalived|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-keepalived -n a1 >flume-keepalived.log 2>&1 &
  	else
  	  echo "keepalived的日志目录为空"
  	fi
  else
    echo "keepalived的日志目录不存在"
  fi
  
  if [[ -d "/var/log/rabbitmq" || -d "/var/log/kolla/rabbitmq" ]];then
  	if [ -d "/var/log/kolla/rabbitmq" ];then
  		sed -i 's/\/var\/log\/rabbitmq/\/var\/log\/kolla\/rabbitmq/g' flume-rabbitmq
  	fi
  	c1=`ls -A /var/log/rabbitmq|wc -w`
  	c2=`ls -A /var/log/kolla/rabbitmq|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-rabbitmq -n a1 >flume-rabbitmq.log 2>&1 &
  	else
  	  echo "rabbitmq的日志目录为空"
  	fi
  else
    echo "rabbitmq的日志目录不存在"
  fi
  
  if [[ -d "/var/log/libvirt" || -d "/var/log/kolla/libvirt" ]];then
  	if [ -d "/var/log/kolla/libvirt" ];then
  		sed -i 's/\/var\/log\/libvirt/\/var\/log\/kolla\/libvirt/g' flume-libvirt
  	fi
  	c1=`ls -A /var/log/libvirt|wc -w`
  	c2=`ls -A /var/log/kolla/libvirt|wc -w`
  	if [[ $c1 > 0 || $c2 > 0 ]];then
  		nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-libvirt -n a1 >flume-libvirt.log 2>&1 &
  	else
  	  echo "libvirt的日志目录为空"
  	fi
  else
    echo "libvirt的日志目录不存在"
  fi

  if [[ -d "/var/log/clog" || -d "/var/log/kolla/clog" ]];then
        if [ -d "/var/log/kolla/clog" ];then
                sed -i 's/\/var\/log\/clog/\/var\/log\/kolla\/clog/g' flume-clog
        fi
        c1=`ls -A /var/log/clog|wc -w`
        c2=`ls -A /var/log/kolla/clog|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-clog -n a1 >flume-clog.log 2>&1 &
        else
          echo "clog的日志目录为空"
        fi
  else
    echo "clog的日志目录不存在"
  fi
  
  if [[ -d "/var/log/fault_migration" || -d "/var/log/kolla/fault_migration" ]];then
        if [ -d "/var/log/kolla/fault_migration" ];then
                sed -i 's/\/var\/log\/fault_migration/\/var\/log\/kolla\/fault_migration/g' flume-fault_migration
        fi
        c1=`ls -A /var/log/fault_migration|wc -w`
        c2=`ls -A /var/log/kolla/fault_migration|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-fault_migration -n a1 >flume-fault_migration.log 2>&1 &
        else
          echo "fault_migration的日志目录为空"
        fi
  else
    echo "fault_migration的日志目录不存在"
  fi
  
  if [[ -d "/var/log/heat" || -d "/var/log/kolla/heat" ]];then
        if [ -d "/var/log/kolla/heat" ];then
                sed -i 's/\/var\/log\/heat/\/var\/log\/kolla\/heat/g' flume-heat
        fi
        c1=`ls -A /var/log/heat|wc -w`
        c2=`ls -A /var/log/kolla/heat|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-heat -n a1 >flume-heat.log 2>&1 &
        else
          echo "heat的日志目录为空"
        fi
  else
    echo "heat的日志目录不存在"
  fi
  
  if [[ -d "/var/log/portal" || -d "/var/log/kolla/portal" ]];then
        if [ -d "/var/log/kolla/portal" ];then
                sed -i 's/\/var\/log\/portal/\/var\/log\/kolla\/portal/g' flume-portal
        fi
        c1=`ls -A /var/log/portal|wc -w`
        c2=`ls -A /var/log/kolla/portal|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-portal -n a1 >flume-portal.log 2>&1 &
        else
          echo "portal的日志目录为空"
        fi
  else
    echo "portal的日志目录不存在"
  fi
  
  if [[ -d "/var/log/sahara" || -d "/var/log/kolla/sahara" ]];then
        if [ -d "/var/log/kolla/sahara" ];then
                sed -i 's/\/var\/log\/sahara/\/var\/log\/kolla\/sahara/g' flume-sahara
        fi
        c1=`ls -A /var/log/sahara|wc -w`
        c2=`ls -A /var/log/kolla/sahara|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-sahara -n a1 >flume-sahara.log 2>&1 &
        else
          echo "sahara的日志目录为空"
        fi
  else
    echo "sahara的日志目录不存在"
  fi
  
  if [[ -d "/var/log/trove" || -d "/var/log/kolla/trove" ]];then
        if [ -d "/var/log/kolla/trove" ];then
                sed -i 's/\/var\/log\/trove/\/var\/log\/kolla\/trove/g' flume-trove
        fi
        c1=`ls -A /var/log/trove|wc -w`
        c2=`ls -A /var/log/kolla/trove|wc -w`
        if [[ $c1 > 0 || $c2 > 0 ]];then
                nohup /home/sinorail/apache-flume-1.8.0-bin/bin/flume-ng agent -c conf -f flume-trove -n a1 >flume-trove.log 2>&1 &
        else
          echo "trove的日志目录为空"
        fi
  else
    echo "trove的日志目录不存在"
  fi

else
  echo '''region为空，请指定region名，举例如下：
          如果OS_REGION_NAME为default，则运行如下脚本启动flume：
          sh startFlume.sh default'''
fi
