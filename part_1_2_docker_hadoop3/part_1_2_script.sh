#build docker
docker build -t hadoop3 .
docker run -t -i hadoop3 /bin/bash /etc/bootstrap.sh -bash


# test with create an directory on HDFS
export PATH=$PATH:$HADOOP_PREFIX/bin
hdfs dfs -mkdir /input
hdfs dfs -ls /
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/root
hdfs dfs -put etc/hadoop/ input
hdfs dfs -ls /user/root/input
hdfs dfs -rm -r /user/root/input/shellprofile.d

bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.0.0.jar wordcount input/ output/

