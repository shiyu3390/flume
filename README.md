<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

## Compiling Flume
Compiling Flume requires the following tools:

* Oracle Java JDK 1.8
* Apache Maven 3.x
Note: The Apache Flume build requires more memory than the default configuration. We recommend you set the following Maven options:

export MAVEN_OPTS="-Xms512m -Xmx1024m"

To compile Flume and build a distribution tarball, run `mvn clean install -DskipTests` from the top level directory.

The artifacts will be placed under flume-ng-dist/target/.

## Use Flume
上传flume-ng-dist/target/apache-flume-1.8.0-bin.tar.gz包到/home/sinorail目录下

解压 `tar zxvf apache-flume-1.8.0-bin.tar.gz`

## 配置文件说明
### flume-ng
agent启动脚本,需要赋予可执行权限,`chmod +x bin/flume-ng`

编辑flume-ng文件,加入JAVA_HOME的配置
### openstack log config
在conf/cloud目录下

* 替换kafka集群的地址