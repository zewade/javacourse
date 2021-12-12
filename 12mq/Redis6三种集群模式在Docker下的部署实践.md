# Redis6三种集群模式在Docker下的部署实践
## 概述
本文记录了Redis常用的三种集群模式在Dokcer下的部署实践，由于只用来本地环境下的学习研究（作业）使用，只考虑了必需的参数配置。各种参数调优将在后续继续完善。

## Redis主从模式
在该模式下，目标是配置一主两从，主从复制的Redis集群。

为了部署方便，此处使用Docker-Compose作为容器编排的工具，它通过编写docker-compose.yml脚本声明Redis集群的部署配置，此处为了容器网络配置方便，我将所有节点的内部端口都保持为默认的6379，然后都其分别映射为宿主机上的不同端口。这样就可以从容器外部，通过宿主机的IP和端口来访问Redis各节点。

创建如下的docker-compose.yml，然后进入其所在目录运行`docker-compose up -d` ，完成集群部署。

```yaml
version: '3'
services:
  master:
    image: redis:6.2-alpine
    container_name: redis-master
    ports:
      - 6379:6379
    command: redis-server --requirepass master123456
    networks:
      - redis_net

  slave1:
    image: redis:6.2-alpine
    container_name: redis-slave-1
    ports:
      - 6380:6379
    command: redis-server --requirepass master123456 --slaveof redis-master 6379 --masterauth master123456
    networks:
      - redis_net

  slave2:
    image: redis:6.2-alpine
    container_name: redis-slave-2
    ports:
      - 6381:6379
    command: redis-server --requirepass master123456 --slaveof redis-master 6379 --masterauth master123456
    networks:
      - redis_net

networks:
  redis_net:
    ipam:
      driver: default

```
启动成功，查询容器状态

```bash
root@zewade-ubuntu:/home/wade/redis/master-slave# docker ps
CONTAINER ID   IMAGE                    COMMAND                  CREATED         STATUS         PORTS                                                                                  NAMES
a15bfd70a71e   redis:6.2-alpine         "docker-entrypoint.s…"   7 seconds ago   Up 3 seconds   0.0.0.0:6380->6379/tcp, :::6380->6379/tcp                                              redis-slave-1
81f6cc4184dc   redis:6.2-alpine         "docker-entrypoint.s…"   7 seconds ago   Up 3 seconds   0.0.0.0:6379->6379/tcp, :::6379->6379/tcp                                              redis-master
0a66c53f4d77   redis:6.2-alpine         "docker-entrypoint.s…"   7 seconds ago   Up 2 seconds   0.0.0.0:6381->6379/tcp, :::6381->6379/tcp                                              redis-slave-2
5caf604ec197   portainer/portainer-ce   "/portainer"             14 months ago   Up 6 days      0.0.0.0:8000->8000/tcp, :::8000->8000/tcp, 0.0.0.0:9000->9000/tcp, :::9000->9000/tcp   portainer

```
进入redis-master容器，登录redis-cli，通过info命令查看集群状态，可以看到集群启动成功。

```bash
# Replication
role:master
connected_slaves:2
slave0:ip=172.25.0.4,port=6379,state=online,offset=42,lag=1
slave1:ip=172.25.0.2,port=6379,state=online,offset=42,lag=1
master_failover_state:no-failover
master_replid:d5790298f37cc1850f4c370ef4fc12eab9122612
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:42
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:42
```
## Redis哨兵模式
在该模式下，目标是配置一主两从三哨兵，通过哨兵实现Redis集群高可用。实际是在上一节的主从集群基础上增加三个哨兵节点，实现主从选举高可用功能。

首先我们复用上一节的主从集群，新建一个docker-compose.yml部署3个哨兵节点与其进行连接，但是在实践过程中发现新建的哨兵节点和主从集群的IP并不是一个网段的，它们之间网络不能互通。因此我将主从集群节点的启动参数进行一些修改，通过声明replica-announce-ip参数指定宿主机的IP为节点向哨兵注册上报的IP，从而打通网络的访问。

首先在主从集群的docker-compose.yml目录，使用`docker-compose down`关闭集群，然后使用如下的yml重新启动集群。

```yaml
version: '3'
services:
  master:
    image: redis:6.2-alpine
    container_name: redis-master
    ports:
      - 6379:6379
    command: redis-server --requirepass master123456 --replica-announce-ip 192.168.2.136 --replica-announce-port 6379
    networks:
      - redis_net

  slave1:
    image: redis:6.2-alpine
    container_name: redis-slave-1
    ports:
      - 6380:6379
    command: redis-server --requirepass master123456 --slaveof redis-master 6379 --masterauth master123456 --replica-announce-ip 192.168.2.136 --replica-announce-port 6380
    networks:
      - redis_net

  slave2:
    image: redis:6.2-alpine
    container_name: redis-slave-2
    ports:
      - 6381:6379
    command: redis-server --requirepass master123456 --slaveof redis-master 6379 --masterauth master123456 --replica-announce-ip 192.168.2.136 --replica-announce-port 6381
    networks:
      - redis_net

networks:
  redis_net:
    ipam:
      driver: default

```
然后我们新建一个目录放置哨兵节点的yml，如下所示，然后启动。

```yaml
version: '3'
services:
  sentinel1:
    image: redis:6.2-alpine
    container_name: redis-sentinel-1
    ports:
      - 26379:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf --requirepass master123456
    volumes:
      - ./sentinel1.conf:/usr/local/etc/redis/sentinel.conf
    networks:
      - master-slave_redis_net

  sentinel2:
    image: redis:6.2-alpine
    container_name: redis-sentinel-2
    ports:
      - 26380:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf --requirepass master123456
    volumes:
      - ./sentinel2.conf:/usr/local/etc/redis/sentinel.conf
    networks:
      - master-slave_redis_net

  sentinel3:
    image: redis:6.2-alpine
    container_name: redis-sentinel-3
    ports:
      - 26381:26379
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf --requirepass master123456
    volumes:
      - ./sentinel3.conf:/usr/local/etc/redis/sentinel.conf
    networks:
      - master-slave_redis_net

networks:
  master-slave_redis_net:
    external: true

```
挂载的哨兵配置文件，放在当前目录下，sentinel1.conf/sentinel2.conf/sentinel3.conf三个文件的内容一样。其中因为前面已经进行了映射，配置监控的master节点ip采用宿主机的ip。

```bash
root@zewade-ubuntu:/home/wade/redis/sentinel# cat sentinel1.conf
port 26379
dir /tmp
sentinel monitor mymaster 192.168.2.136 6379 2
sentinel auth-pass mymaster master123456
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 180000
sentinel deny-scripts-reconfig yes
```
这样我们就得到了一个一主两从三哨兵的Redis高可用集群。我们可以进入Redis-cli，利用info命令进行状态查看。

```bash
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.2.136:6379,slaves=2,sentinels=3
```
Redis Cluster集群模式

在该模式下，集群中的数据被分片存放于各个节点，单个节点没有完整的数据。在实践过程中，在设置1个数据副本的情况下，Redis提示至少需要启动6个节点，因此yml文件如下。在这个模式下，我们先启动6个开启集群功能但不相关的节点，随后通过一句命令将6个节点创建为一个集群。暂时未能找到直接启动一个集群的方法，待后续更新。

注意需要额外声明和映射集群通信的端口`cluster-announce-bus-port` ，同时节点的集群功能为启用`cluster-enabled yes` 。

```yaml
version: '3'
services:
  master:
    image: redis:6.2-alpine
    container_name: redis-cluster-master
    ports:
      - 6379:6379
      - 16379:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6379 --cluster-announce-bus-port 16379 --cluster-enabled yes
    networks:
      - redis_net

  slave1:
    image: redis:6.2-alpine
    container_name: redis-cluster-slave-1
    ports:
      - 6380:6379
      - 16380:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6380 --cluster-announce-bus-port 16380 --cluster-enabled yes
    networks:
      - redis_net

  slave2:
    image: redis:6.2-alpine
    container_name: redis-cluster-slave-2
    ports:
      - 6381:6379
      - 16381:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6381 --cluster-announce-bus-port 16381 --cluster-enabled yes
    networks:
      - redis_net

  slave3:
    image: redis:6.2-alpine
    container_name: redis-cluster-slave-3
    ports:
      - 6382:6379
      - 16382:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6382 --cluster-announce-bus-port 16382 --cluster-enabled yes
    networks:
      - redis_net
  
  slave4:
    image: redis:6.2-alpine
    container_name: redis-cluster-slave-4
    ports:
      - 6383:6379
      - 16383:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6383 --cluster-announce-bus-port 16383 --cluster-enabled yes
    networks:
      - redis_net
  
  slave5:
    image: redis:6.2-alpine
    container_name: redis-cluster-slave-5
    ports:
      - 6384:6379
      - 16384:16379
    command: redis-server --requirepass master123456 --cluster-announce-ip 192.168.2.136 --cluster-announce-port 6384 --cluster-announce-bus-port 16384 --cluster-enabled yes
    networks:
      - redis_net


networks:
  redis_net:
    ipam:
      driver: default
```
随后可以进入redis-cluster-master容器运行创建集群的命令（应该任意节点都可以执行）。

```shell
root@zewade-ubuntu:/home/wade/redis/cluster# docker exec -it redis-cluster-master sh
/data # redis-cli --cluster create 192.168.2.136:6379 192.168.2.136:6380 192.168.2.136:6381 192.168.2.136:6382 192.168.2.136:6383 192.168.2.136:6384  --cluster-replicas 1 -a master123456
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 192.168.2.136:6383 to 192.168.2.136:6379
Adding replica 192.168.2.136:6384 to 192.168.2.136:6380
Adding replica 192.168.2.136:6382 to 192.168.2.136:6381
>>> Trying to optimize slaves allocation for anti-affinity
[WARNING] Some slaves are in the same host as their master
M: cc690bb714a0b8aec1f358a109996a57a1e8a94d 192.168.2.136:6379
   slots:[0-5460] (5461 slots) master
M: a6922eac990d11d5785b092afe55442e83d3e3fd 192.168.2.136:6380
   slots:[5461-10922] (5462 slots) master
M: 79a60a5f361041a761f1b76394d33a45a1142794 192.168.2.136:6381
   slots:[10923-16383] (5461 slots) master
S: 43a2bafae1d1017fef4f632757ebe51590c90fee 192.168.2.136:6382
   replicates a6922eac990d11d5785b092afe55442e83d3e3fd
S: 5237851aa8adf2f4c35e2e01e473685aae44c60b 192.168.2.136:6383
   replicates 79a60a5f361041a761f1b76394d33a45a1142794
S: 12fbc782eeeac50f7ef96f12ad5199f6fa6c182b 192.168.2.136:6384
   replicates cc690bb714a0b8aec1f358a109996a57a1e8a94d
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join
.
>>> Performing Cluster Check (using node 192.168.2.136:6379)
M: cc690bb714a0b8aec1f358a109996a57a1e8a94d 192.168.2.136:6379
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
M: 79a60a5f361041a761f1b76394d33a45a1142794 192.168.2.136:6381
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: 12fbc782eeeac50f7ef96f12ad5199f6fa6c182b 192.168.2.136:6384
   slots: (0 slots) slave
   replicates cc690bb714a0b8aec1f358a109996a57a1e8a94d
S: 5237851aa8adf2f4c35e2e01e473685aae44c60b 192.168.2.136:6383
   slots: (0 slots) slave
   replicates 79a60a5f361041a761f1b76394d33a45a1142794
M: a6922eac990d11d5785b092afe55442e83d3e3fd 192.168.2.136:6380
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 43a2bafae1d1017fef4f632757ebe51590c90fee 192.168.2.136:6382
   slots: (0 slots) slave
   replicates a6922eac990d11d5785b092afe55442e83d3e3fd
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
## 总结
至此三种集群模式在Docker下都搭建成功，后续计划在Kubernetes环境进行更加深入的部署实践。













