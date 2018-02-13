#!/bin/bash

/etc/init.d/ssh start

# START HADOOP
##############
cd $HADOOP_HOME

bin/hdfs namenode -format

#start cluster
sbin/start-dfs.sh
sbin/start-yarn.sh
sbin/mr-jobhistory-daemon.sh start historyserver

/bin/bash
# bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.0.0.jar pi 2 5