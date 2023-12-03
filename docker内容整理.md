# docker内容整理

## docker的安装

1. 检查之前是否安装过docker，如果有使用yum remove docker卸载

   ```shell
   [root@woniu ~]# yum remove docker \
   > docker-client \
   > docker-client-latest \
   > docker-common \
   > docker-latest \
   > docker-latest-logrotate \
   > docker-logrotate \
   > docker-engine
   ```

2. 安装yum工具

   ```shell
   [root@woniu ~]# yum -y install yum-utils
   ```

   

3. 配置阿里云镜像，添加docker引擎的yum源

   ```shell
   [root@woniu ~]# yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
   ```

   

4. 更新CentOS

   ```shell
   [root@woniu ~]# yum update
   ```

   

5. 列出docker源

   ```shell
   [root@woniu ~]# yum list docker-ce —showduplicates | sort -r
   ```

6. 安装docker

   ```shell
   [root@woniu ~]# yum list docker-ce —showduplicates | sort -r
   ```

7. 检查docker版本号

   ```shell
   [root@woniu ~]# docker -v
   Docker version 24.0.7, build afdd53b
   ```

8. 启动docker，并设置自启

   ```
   [root@woniu ~]# systemctl start docker
   [root@woniu ~]# systemctl enable docker
   ```

9. 配置本地镜像库

   从默认网络获取docker镜像太慢，为了提高速度，可以配置国内的镜像库，可同时配置多个

   ```shell
   [root@woniu ~]# vim /etc/docker/daemon.json
   ```

   ```json
   {
           "registry-mirrors":[
                   "https://ung2thfc.mirror.aliyuncs.com",
                   "https://docker.mirrors.ustc.edu.cn",
                   "https://registry.docker-cn.com",
                   "http://hub-mirror.c.163.com",
                   "https://mirror.ccs.tencentyun.com"
           ]
   }
   ```

   配置完成之后重启docker

   ```shell
   [root@woniu ~]# systemctl restart docker
   ```

10. 设置docker的静态ip
    目的：解决docker运行过程中ip的固定。
    docker容器的ip地址在每次启动后启动顺序设置ip地址，为解决ip地址变动的问题，我们有必要设置docker内部ip地址固定。

11. 创建自定义网络（network）
    docker network create —driver bridge —subnet=自定义网络ip/16 —gateway=网关值 自定义网络名称

    ```shell
    [root@woniu ~]# docker network create --driver bridge --subnet=172.18.12.0/16 --gateway=172.18.1.1 wn_docker_net
    ```

    查看创建的网络

    ```shell
    [root@woniu ~]# docker network ls
    NETWORK ID     NAME                      DRIVER    SCOPE
    70fe2b91dc9a   bridge                    bridge    local
    dc8dac7cdeb4   host                      host      local
    ab24c1a95ddc   none                      null      local
    88dee123076b   ssc-replication_default   bridge    local
    b948a39c4ea9   test_default              bridge    local
    4bb015285e05   wn_docker_net             bridge    local
    ```

    

    ```json
    [root@woniu ~]# docker inspect wn_docker_net
    [
        {
            "Name": "wn_docker_net",
            "Id": "4bb015285e05f57ab4b59e30f2eb9c0b76818c850680a3910aca6201aac9bdaa",
            "Created": "2023-09-25T15:33:43.398740767+08:00",
            "Scope": "local",
            "Driver": "bridge",
            "EnableIPv6": false,
            "IPAM": {
                "Driver": "default",
                "Options": {},
                "Config": [
                    {
                        "Subnet": "172.18.12.0/16",
                        "Gateway": "172.18.1.1"
                    }
                ]
            },
            "Internal": false,
            "Attachable": false,
            "Ingress": false,
            "ConfigFrom": {
                "Network": ""
            },
            "ConfigOnly": false,
            "Containers": {},
            "Options": {},
            "Labels": {}
        }
    ]
    ```

12. docker常见问题IPv4 forwarding disabled的解决方案
    docker run创建运行容器可能出现警告WARNING: IPv4 forwarding is disabled. Networking will not work.解决方案为开启路由转发功能，方式如下

    ```shell
    [root@woniu ~]# vim /etc/sysctl.conf
    ```

    在末尾追加net.ipv4.ip_forward=1，保存退出（:wq）

    查看是否修改成功

    ```shell
    [root@woniu ~]# sysctl net.ipv4.ip_forward
    net.ipv4.ip_forward = 1
    ```

    重启network和docker服务
    ```shell
    [root@woniu ~]# systemctl restart network
    [root@woniu ~]# systemctl restart docker
    ```





## docker常用命令及参数

1. 容器管理：

   - docker run: 创建、运行一个新的容器

   - - -d: 以守护进程（后台）模式运行容器
     - -it: 分配一个交互式的终端
     - –name: 指定容器的名称
     - -p: 端口映射
     - –network：指定容器连接的网络。比如指定为我们之前自定义的网络–network wn_docker_net
     - –ip：指定容器内部ip，配合–network使用，如–ip 172.18.12.2
     - -v：用于挂载数据卷，如-v /usr/local/softwares/mysql/3306/conf/my.cnf:/etc/mysql/my.cnf，冒号左边的是外面宿主机的文件路径，冒号右边的是容器内的文件路径，这样我们可以修改外面的文件，容器内部的文件会被自动修改，非常方便
     - -e：用于设置环境变量
     - –restart：设置容器的重启策略。可以使用该参数来指定容器在退出时的重启策略，如 --restart=always 表示容器退出时总是重启。能够使我们在启动docker时，自动启动docker内的各种容器。
     - –rm：容器退出时自动删除。使用该参数可以在容器退出后自动删除容器，适用于临时性任务的容器。
   - docker start: 启动已经停止的容器
   - docker stop: 停止正在运行的容器
   - docker restart: 重启容器
   - docker rm: 删除容器
   - docker ps: 查看正在运行的容器
   - docker ps -a: 查看所有容器（包括已停止的）
   - docker logs: 查看容器的日志，如果在配置文件设置了自定义的日志路径则失效，此时应该把日志挂载到外面查看

2. 镜像管理：

   - docker pull: 下载镜像

   - docker build: 构建镜像
   - docker push: 推送镜像到仓库
   - docker images: 查看本地的镜像列表
   - docker rmi: 删除镜像

3. 网络管理：

   docker network ls: 列出 Docker 网络

   - docker network create: 创建一个新的 Docker 网络
   - docker network connect: 将容器连接到网络
   - docker network disconnect: 将容器从网络断开

4. 仓库管理：

   - docker login: 登录到 Docker 仓库

   - docker logout: 退出 Docker 仓库

   - docker search: 在 Docker 仓库中搜索镜像

5. 其他常用命令:

   - docker exec: 在运行的容器中执行命令，一般使用docker exec -it 容器名 bash进入容器内部

   - docker info: 显示 Docker 系统信息

   - docker version: 显示 Docker 版本信息

   - docker-compose: 使用 Docker Compose 来定义和运行多个容器的应用程序

   - docker inspect：用于获取有关 Docker 对象（如容器、镜像、网络等）的详细信息，可以配合管道|grep "x"进行过滤输出结果搜索包含x的指定行

以上是常用命令别的可以通过docker --help/-h或者 docker [command] --help来查看

## mysql

相关链接：

[Linux docker搭建mysql主从数据库](https://blog.csdn.net/m0_72075879/article/details/133715830)

### 配置主从

**主从配置原理：**

MySQL master 将数据变更写入二进制日志( binary log, 其中记录叫做二进制日志事件 binary log events，可以通过 show binlog events 进行查看)；
MySQL slave 将 master 的 binary log events 拷贝到它的中继日志(relay log)；
MySQL slave 重做 relay log 中事件，将数据变更反映它自己的数据；

### canal

描述：

canal 翻译为渠道，主要用途是基于 MySQL 数据库的增量日志 Binlog 解析，提供增量数据订阅和消费。

基于日志增量订阅和消费的业务包括：

数据库镜像；
数据库实时备份；
索引构建和实时维护（拆分异构索引、倒排索引等）；
业务 cache 刷新；
带业务逻辑的增量数据处理；
工作原理：

canal 模拟 MySQL slave 的交互协议，伪装自己为 MySQL slave ，向 MySQL master 发送 dump 协议；
MySQL master 收到 dump 请求，开始推送 binary log 给 slave (即 canal )；
canal 解析 binary log 对象(原始为 byte 流)；



## maven私服

相关链接：

[搭建maven私服](https://blog.csdn.net/m0_72075879/article/details/133799769)

**nexus配置maven私服**
描述：

使用Nexus搭建私服有很多好处，其中最主要的原因是可以控制构件的访问和部署。如果没有Nexus私服，我们所需的所有构件都需要通过Maven的中央仓库和第三方的Maven仓库下载到本地，而一个团队中的所有人都重复的从Maven仓库下载构件无疑加大了仓库的负载和浪费了外网带宽，如果网速慢的话，还会影响项目的进程。而使用Nexus搭建私服后，只需要在私服上进行一次下载，就可以在整个团队中使用，大大提高了效率 。

此外，Nexus还具有很多其他优点，例如占用内存小、具有基于ExtJs得操作界面、使用基于Restlet的完全REST API支持代理仓库、宿主仓库和仓库组、基于文件系统，不需要依赖数据库、支持仓库管理、支持构件搜索、支持在界面上上传构件等等 。



## redis

### redis主从

主从复制的作用

读写分离：主节点写，从节点读，提高服务器的读写负载能力
数据冗余︰主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。
故障恢复︰当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复 ; 实际上是一种服务的冗余。
负载均衡︰在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务（即写Redis数据时应用连接主节点，读Redis数据时应用连接从节点），分担服务器负载 ; 尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量。
高可用（集群）基石︰除了上述作用以外，主从复制还是哨兵和集群能够实施的基础，因此说主从复制是Redis高可用的基础。

相关链接：

https://blog.csdn.net/m0_72075879/article/details/134771328

### redis哨兵

哨兵的作用

在主从模式下，主从复制机制使得slave成为与master完全一致的副本，一旦master宕机，我们可以选择一个正常的slave成为新的主节点，实现手动的故障恢复。

哨兵专注于对Redis实例（主节点、从节点）运行状态的监控，并能够在主节点发生故障时通过一系列的机制实现选主及主从切换，实现故障转移，确保整个Redis系统的可用性。

相关链接：

https://blog.csdn.net/m0_72075879/article/details/134771328

### 插件

bloomfilter（布隆过滤器）

布隆过滤器（Bloom Filter）是一种用于快速检查一个元素是否属于一个集合的概率型数据结构。它通过使用多个哈希函数和一个位数组来实现。

redis-cell

使用redis-cell实现令牌桶限流

`redis-cell` 是一个 Redis 模块，它提供了一个基于 Redis 的实时限流器（Rate Limiter）。这个模块的主要目的是让你能够限制在一定时间内请求某个资源的频率，以防止滥用或过度使用。`redis-cell` 模块可以用于 Web 应用、API、或其他需要控制访问速率的场景。

相关链接：

https://blog.csdn.net/m0_72075879/article/details/134770632

## rabbitmq

相关链接：https://blog.csdn.net/m0_72075879/article/details/134769833

消息队列的作用：消峰、解耦、异步

延迟队列：
延迟队列是一种特殊的队列，用于延迟消息的投递。在RabbitMQ中，延迟队列不是一个内置的功能，但可以通过插件（如rabbitmq-delayed-message-exchange）或者使用TTL（Time-To-Live）和死信交换机（DLX）的组合来实现。当消息发送到延迟队列时，它不会立即被消费者接收，而是在队列中等待一定的时间（延迟时间），时间到了之后，消息才会被发送到另一个队列，然后被消费者处理。

应用场景
定时任务：可以将需要在未来某个时间点执行的任务放入延迟队列，当时间到达时，任务被发送到正常队列并执行。
消息重试：在处理消息时，如果遇到暂时性的错误（如网络问题或服务不可用），可以将消息重新发送到延迟队列，等待一段时间后再次尝试处理。
订单超时处理：例如，电商平台中未支付订单的超时关闭，可以在订单创建时发送一个延迟消息，如果用户在规定时间内未支付，消息到期后触发订单关闭流程。
提醒通知：比如预约提醒、会议开始前的通知等，可以在预定时间前将提醒消息发送到延迟队列，到时间后自动通知用户。
死信队列：
死信队列用于存放无法正常投递的消息，这些消息可能因为以下几种情况被投递到死信队列：

消息被拒绝（basic.reject 或 basic.nack）并且设置了requeue参数为false。
消息过期（TTL过期）。
队列达到最大长度。
在RabbitMQ中，可以通过设置队列的x-dead-letter-exchange和x-dead-letter-routing-key参数来指定死信交换机和路由键，从而将死信消息重定向到特定的死信队列。

应用场景
消息审计和排错：当消息无法被正常消费时，将其发送到死信队列，开发人员可以从死信队列中检查和分析这些消息，找出问题原因。
消息保护：避免因为消费者的错误处理导致消息丢失，通过死信队列可以保留这些消息，进行后续的处理。
流量削峰：当系统处理能力达到上限时，可以将超出能力的消息暂时存放在死信队列中，等系统负载降低后再进行处理。
消息重试策略：结合延迟队列，可以实现消息的延迟重试机制。当消息处理失败后，先发送到死信队列，然后再根据需要将其发送到延迟队列等待重试。
延迟队列和死信队列可以单独使用，也可以结合起来使用，以满足复杂的业务需求。例如，可以将消息从延迟队列发送到正常队列，如果消费失败，则进入死信队列，之后可以从死信队列中取出消息进行分析或者再次发送到延迟队列进行重试。

## nginx

相关链接：

[docker搭建nginx实现负载均衡](http://t.csdnimg.cn/tDRez)

## gitlab

相关链接：

[Docker 部署 GitLab-CSDN博客](https://blog.csdn.net/m0_72075879/article/details/134494974)

## Dockerfile

简介

Dockerfile 是一个用来构建镜像的文本文件，文本内容包含了一条条构建镜像所需的指令和说明。

常用指令

- FROM：指定基础镜像
- MAINTAINER：指定维护者信息
- ADD：复制文件
- ENTRYPOINT：入口点
- EXPOSE：指定暴露出去的端口号
- VOLUME：指定挂载点

相关链接：

[springboot整合Dockerfile-CSDN博客](https://blog.csdn.net/m0_72075879/article/details/133756100)

## docker-compose

简介

docker compose是一个编排多容器分布式部署的工具，提供命令集管理容器化应用的完整开发周期，包括服务构建、启动和停止。简单来说，它可以帮你一次性同时创建并启动多个docker容器。

下载（https://github.com/docker/compose/releases?page=3）

![image-20231203221502754](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032218942.png)

下载完成后上传到虚拟机里的/usr/local/bin/目录下，放到这个目录下可以全局使用，对它添加可执行权限，修改名字简化使用

```shell
[root@woniu abc]# cd /usr/local/bin
[root@woniu bin]# chmod u+x docker-compose-linux-x86_64
[root@woniu bin]# mv docker-compose-linux-x86_64 docker-compose
```

查看版本

```shell
[root@woniu ~]# docker-compose --version
Docker Compose version v2.14.2
```



### 简单使用

通过使用命令docker-compose up就可以按照当前目录下docker-compose.yml的配置内容进行构建

![image-20231203190431316](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032220191.png)

创建一个空文件夹

再创建一个docker-compose.yml文件，编辑里面的内容

```yaml
version: '2'
networks:
  wn_docker_net:
    external: true
services:
  replication-01:
    build: .
    image: ssc-replication
    ports:
      - "12006:12006"
    container_name: ssc-appointment_01
    networks:
      wn_docker_net:
        ipv4_address: 172.18.12.110
  replication-02:
    build: .
    image: ssc-replication
    ports:
      - "12007:12006"
    container_name: ssc-appointment_02
    networks:
      wn_docker_net:
        ipv4_address: 172.18.12.111
```

build: .指的是会在当前目录下找一个Dockerfile文件来构建镜像，这个就和之前docker run配合参数构建运行容器一样，只不过可以一次性构建多个，之后可以配合Dockerfile来构建分布式项目。

```shell
[root@woniu test]# docker-compose up
[+] Running 2/2
 ⠿ Container mysql_3320  Created                                                    0.1s
 ⠿ Container redis_6399  Created                                                    0.1s
Attaching to mysql_3320, redis_6399
mysql_3306  | 2023-12-03 22:14:26+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 8.1.0-1.el8 started.
redis_6379  | 1:C 03 Dec 2023 22:14:26.959 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
...
```

Ctrl+C终止

```shell
^CGracefully stopping... (press Ctrl+C again to force)
[+] Running 2/2
 ⠿ Container mysql_3306  Stopped                                                    1.8s
 ⠿ Container redis_6379  Stopped                                                    0.3s
canceled
```



### Spring Boot整合docker compose

编写Dockerfile

![image-20231203222425881](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032224004.png)

![image-20231203222442769](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032224809.png)

创建镜像

![image-20231203222511467](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032225488.png)



编写docker-compose.yml

![image-20231203222541886](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032225070.png)

点击运行

![image-20231203222613266](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032226294.png)

若出现

```
Failed to deploy ‘Compose: docker-compose.yml’: com.intellij.execution.process.ProcessNotCreatedException: Cannot run program “docker-compose” (in directory “d:\develop\test”): CreateProcess error=2, 系统找不到指定的文件。
```

则需要下载[Docker Desktop](https://www.docker.com/products/docker-desktop/)

然后指定文件目录

![image-20231203222937764](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032229810.png)