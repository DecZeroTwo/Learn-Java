# docker配置redis插件

## 运行容器redis_6390

```shell
docker run -it \
--name redis_6390 \
--privileged \
-p 6390:6379 \
--network wn_docker_net \
--ip 172.18.12.19 \
--sysctl net.core.somaxconn=1024 \
-e TIME_ZONE="Asia/Shanghai" -e TZ="Asia/Shanghai" \
-v /usr/local/software/redis/6390/conf/redis.conf:/usr/local/etc/redis/redis.conf \
-v /usr/local/software/redis/6390/data/:/data \
-v /usr/local/software/redis/6390/log/redis.log:/var/log/redis.log \
-d redis \
/usr/local/etc/redis/redis.conf
```

## 插件安装

下载插件

[Releases · brandur/redis-cell (github.com)](https://github.com/brandur/redis-cell/releases)

在linux中解压插件

```shell
[root@hao /usr/local/software/redis/conf]# tar -zxvf redis-cell-v0.3.1-x86_64-unknown-linux-gnu.tar.gz
libredis_cell.d
libredis_cell.so
```

拷贝libredis_cell.so文件到容器中

```shell
[root@hao /usr/local/software/redis/conf]# docker cp libredis_cell.so redis_6390:/usr/local/etc/redis
Successfully copied 6.77MB to redis_6390:/usr/local/etc/redis
```



### 令牌桶

修改配置文件

![image-20231203212148687](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032121712.png)

检查插件是否安装成功

使用：



```
[root@hao /usr/local/software/redis/6390/log]# docker exec -it redis_6390 bash
root@6066a010f59f:/data# redis-cli
127.0.0.1:6379> CL.THROTTLE older:activity 99 10 100 5
1) (integer) 0
2) (integer) 100
3) (integer) 95
4) (integer) -1
5) (integer) 51

/* 请求：
 * 1.最大的突发请求
 * 2，3.每【3】秒存入【2】个令牌
 * 4.本次申请多少个令牌
 */

/* 结果
 * 1.当前请求是否被允许，0表示允许，1表示不允许；2.
 * 2.令牌桶的最大容量，令牌桶中令牌数的最大值【为最大突发请求数 + 1】
 * 3.令牌桶中当前的令牌数
 * 4.如果被拒绝，需要多长时间后在重试，如果当前被允许则为-1
 * 5.多长时间后令牌桶中的令牌会满
 */
```



### 布隆过滤器

下载[bloom filter](https://github.com/RedisBloom/RedisBloom/archive/v2.2.4.tar.gz) 并上传到linux

bloom filter作者未编译打包，需要自行编译

需提前安装gcc

```shell
[root@localhost bf]# yum -y install gcc
已加载插件：fastestmirror, langpacks
Determining fastest mirrors
 * base: mirrors.ustc.edu.cn
 * extras: mirrors.ustc.edu.cn
 * updates: mirrors.ustc.edu.cn
base
[root@localhost bf]# gcc --version
gcc (GCC) 4.8.5 20150623 (Red Hat 4.8.5-44)
Copyright © 2015 Free Software Foundation, Inc.
本程序是自由软件；请参看源代码的版权声明。本软件没有任何担保；
包括没有适销性和某一专用目的下的适用性担保。
```

解压文件

```shell
tar -zxvf RedisBloom-2.2.4.tar.gz
```

进入文件夹，查看文件，里面有Makefile文件

![分类/redis/redisBloomFilter_1.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032109405.png)

输入make进行编译

![分类/redis/redisBloomFilter_2.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032109968.png)

![分类/redis/redisBloomFilter_3.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032109490.png)

将编译好的插件拷贝到docker的redis容器中

```shell
 docker cp redisbloom.so redis_6390:/usr/local/etc/redis
```

![分类/redis/redisBloomFilter_4.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032110906.png)

修改配置文件

```
  43 # loadmodule /path/to/my_module.so
  44 # loadmodule /path/to/other_module.so
  45 
  46 loadmodule /usr/local/etc/redis/redisbloom.so
```

重启redis

```shell
 docker restart redis_6390
```

查看日志

![分类/redis/redisBloomFilter_5.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032110776.png)

验证是否安装成功

```
root@33918dd9ffd7:/data# redis-cli
127.0.0.1:6379> bf.add who me
(integer) 1
127.0.0.1:6379> bf.exists who me
(integer) 1
127.0.0.1:6379> bf.exists who abc
(integer) 0
```

