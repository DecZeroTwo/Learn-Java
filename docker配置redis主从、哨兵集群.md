# docker配置redis主从、哨兵集群

## 搭建redis主从

### 准备工作

在/usr/local/software/redis/文件夹下建立如下的文件夹、文件

```shell
root@localhost redis]# mkdir -p  6379/conf 6379/data 6379/log
[root@localhost redis]# mkdir -p  6380/conf 6380/data 6380/log
[root@localhost redis]# mkdir -p  6381/conf 6381/data 6381/log
```

```shell
[root@localhost redis]# tree
.
├── 6379
│   ├── conf
│   │   └── redis.conf
│   ├── data
│   │   └── dump.rdb
│   └── log
│       └── redis.log
├── 6380
│   ├── conf
│   │   └── redis.conf
│   ├── data
│   │   └── dump.rdb
│   └── log
│       └── redis.log
├── 6381
    ├── conf
    │   └── redis.conf
    ├── data
    │   └── dump.rdb
    └── log
        └── redis.log
```

### 配置主（master）服务器

修改redis.conf文件

改为0.0.0.0【所有ip均可访问】

![image-20231203212515966](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032125071.png)

改为no【不开启保护模式】

![image-20231203212534049](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032125129.png)

改为yes【启动时有图标】

![image-20231203212912903](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032129011.png)

设置自己的ip地址和端口

![image-20231203213008476](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032130493.png)

### 创建运行容器

```shell
docker run -it \
--name redis_6379 \
--privileged \
-p 6379:6379 \
--network wn_docker_net \
--ip 172.18.12.10 \
--sysctl net.core.somaxconn=1024 \
-e TIME_ZONE="Asia/Shanghai" -e TZ="Asia/Shanghai" \
-v /usr/local/software/redis/6379/conf/redis.conf:/usr/local/etc/redis/redis.conf \
-v /usr/local/software/redis/6379/data/:/data \
-v /usr/local/software/redis/6379/log/redis.log:/var/log/redis.log \
-d redis \
/usr/local/etc/redis/redis.conf
```

### 配置从（slave）服务器

需配置主的地址和端口

![image-20231203213133785](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032131804.png)

配置自己的地址和端口

![image-20231203213203603](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032132625.png)

设置从服务器是否只读【yes只读，no可以写】

![image-20231203213234600](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032132698.png)

解决脑裂问题（没有就不用配置，否则redis一旦切换代码写入时会经常报错）

![image-20231203213324834](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032133920.png)

从：6380

```sh
docker run -it \
--name redis_6380 \
--privileged \
-p 6380:6379 \
--network wn_docker_net \
--ip 172.18.12.11 \
--sysctl net.core.somaxconn=1024 \
-e TIME_ZONE="Asia/Shanghai" -e TZ="Asia/Shanghai" \
-v /usr/local/software/redis/6380/conf/redis.conf:/usr/local/etc/redis/redis.conf \
-v /usr/local/software/redis/6380/data/:/data \
-v /usr/local/software/redis/6380/log/redis.log:/var/log/redis.log \
-d redis \
/usr/local/etc/redis/redis.conf
```

从：6381【配置文件同6380：改下端口号】

```shell
docker run -it \
--name redis_6381 \
--privileged \
-p 6381:6379 \
--network wn_docker_net \
--ip 172.18.12.12 \
--sysctl net.core.somaxconn=1024 \
-e TIME_ZONE="Asia/Shanghai" -e TZ="Asia/Shanghai" \
-v /usr/local/software/redis/6381/conf/redis.conf:/usr/local/etc/redis/redis.conf \
-v /usr/local/software/redis/6381/data/:/data \
-v /usr/local/software/redis/6381/log/redis.log:/var/log/redis.log \
-d redis \
/usr/local/etc/redis/redis.conf
```

### 检查是否配置成功

进入主服务器查看

```
docker exec -it redis_6379 bash
redis-cli
info replication
```

![image-20231203213440070](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032134089.png)

角色：主还是从

role：master

连接的从的数量

connected_slaves:2

## **搭建redis哨兵**

### 准备工作

在/usr/local/software/redis_sentinel/文件夹下建立如下的文件夹、文件

```shell
[root@localhost redis_sentinel]# tree
.
├── 26379
│   └── conf
│       └── sentinel.conf
├── 26380
│   └── conf
│       └── sentinel.conf
└── 26381
    └── conf
        └── sentinel.conf
```

##### 跟踪主（master）服务器

修改redis.conf文件

配置端口

![image-20231203214252916](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032142936.png)

不开启保护模式

![image-20231203214302941](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032143958.png)

配置日志文件位置/var/log/sentinel.log、和自己的ip、端口号

![image-20231203214340005](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032143027.png)

配置需要监控的redis的主的ip和端口，自定义名称，这里是mymaster

![image-20231203214407771](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032144798.png)

##### 运行容器

26379:

```shell
docker run -it \
--name sentinel_26379 \
--privileged \
--network wn_docker_net \
--sysctl net.core.somaxconn=1024 \
--ip 172.18.12.70 \
-p 26379:26379 \
-v /etc/localtime:/etc/localtime \
-v /usr/local/software/redis_sentinel/26379/conf:/usr/local/etc/redis/conf/ \
-v /usr/local/software/redis_sentinel/26379/log/sentinel.log:/var/log/sentinel.log \
-d redis \
redis-sentinel /usr/local/etc/redis/conf/sentinel.conf
```

26380:

```shell
docker run -it \
--name sentinel_26380 \
--privileged \
--network wn_docker_net \
--sysctl net.core.somaxconn=1024 \
--ip 172.18.12.71 \
-p 26380:26380 \
-v /etc/localtime:/etc/localtime \
-v /usr/local/software/redis_sentinel/26380/conf:/user/local/etc/redis/conf/ \
-v /usr/local/software/redis_sentinel/26380/log/sentinel.log:/var/log/sentinel.log \
-d redis \
redis-sentinel /user/local/etc/redis/conf/sentinel.conf
```

26381:

```shell
docker run -it \
--name sentinel_26381 \
--privileged \
--network wn_docker_net \
--sysctl net.core.somaxconn=1024 \
--ip 172.18.12.72 \
-p 26381:26379 \
-v /etc/localtime:/etc/localtime \
-v /usr/local/software/redis_sentinel/26381/conf:/usr/local/etc/redis/conf/ \
-v /usr/local/software/redis_sentinel/26381/log/sentinel.log:/var/log/sentinel.log \
-d redis \
redis-sentinel /usr/local/etc/redis/conf/sentinel.conf
```

##### 检查

```
[root@localhost redis_sentinel]# docker exec -it sentinel_26379 bash
root@d0bd4816a92e:/data# redis-cli -p 26379 -c
127.0.0.1:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.100.128:6379,slaves=2,sentinels=3
```

![image-20231203214650086](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032146109.png)

#### **配置完哨兵之后的代码**

##### 在springboot中的yml文件中配置

```yaml
spring:
  redis:
    sentinel:
      master: mymaster
      nodes: 192.168.100.128:26379,192.168.100.128:26380,192.168.100.128:26381
  application:
    name: smart-replication
```

使用Redisson分布式锁的Config配置
需要导入依赖

```xml
<dependency>
	<groupId>org.redisson</groupId>
	<artifactId>redisson-spring-boot-starter</artifactId>
	<version>3.18.0</version>
</dependency>
```

```java
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSentinelServers().setMasterName("mymaster")
                .addSentinelAddress("redis://192.168.100.128:26379")
                .addSentinelAddress("redis://192.168.100.128:26380")
                .addSentinelAddress("redis://192.168.100.128:26381");
        return Redisson.create(config);
    }
}
```

