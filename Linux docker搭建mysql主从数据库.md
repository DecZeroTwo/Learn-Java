# Linux docker搭建mhysql主从数据库

## 一、为什么搭建主从服务

多服务器(mysql)协同工作解决大量请求问题。--分布式

CAP理论：CAP

A： 高可用性  ----主从，集群

C：数据一致性

P：分区容错性

## 二、原理(1主2从)

> 采用创建两个docker容器扮演主(master)从(slave)
>
> - 请求mysql在book_tab中添加一条数据 insert
> - master:执行sql，写入到binlog--二进制文件，记录master的数据库操作
> - slave：开启IO线程读取binlog文件
> - slave： 开启写线程写入slave服务器的Relaylog文件中
> - slave： 开启SQL线程，读取relaylog中的数据，更新slave数据库中的内容
> - slave：也添加了一条数据。



![](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092000584.png)



## 三、搭建集群

### 1.从容器中拷贝原始配置文件my.cnf

> 先创建一个MySQL容器

```shell
docker run -it --name mytest -e MYSQL_ROOT-PASSWORD=123 -d mysql
```



![image-20231009200613264](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092006329.png)



> 创建映射文件夹并将刚才的配置文件拷贝进conf文件夹
>
> cp my.cnf 你创建的conf文件路径

```shell
mkdir -p 3306/conf 3306/data 3306 3306/mysql-files
```



![image-20231009202001490](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092020539.png)



> 拷贝my.cnf配置文件
>
> docker cp 容器名:容器内文件地址 自定义路径



```shell
docker cp mytest:/etc/mysql/my.cnf ./
```



![image-20231009200925940](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092009992.png)

### 2.搭建主（master）服务器(docker容器) 

```shell
docker run \
-it \
--name mysql_3306 \
--privileged \
--network wn_docker_net \
--ip 172.18.12.2 \
-p 3306:3306 \
-v  /usr/local/software/mysql/3306/conf/my.cnf:/etc/mysql/my.cnf \
-v  /usr/local/software/mysql/3306/data:/var/lib/mysql \
-v /usr/local/software/mysql/3306/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=123 \
-d mysql
```



> master服务器配置:my.cnf
>
> vim my.cnf

在最后添加

```shell
server-id=200
log_bin=wnhz-master-logbin
binlog_format=row
```



![image-20231009202925638](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092029684.png)

> 重启容器使配置文件生效
>
> docker restart mysql_3306



### 3.创建从(slave)服务器



```shell
docker run \
-it \
--name mysql_3310 \
--privileged \
--network wn_docker_net \
--ip 172.18.12.3 \
-p 3310:3306 \
-v  /usr/local/software/mysql/3310/conf/my.cnf:/etc/mysql/my.cnf \
-v  /usr/local/software/mysql/3310/data:/var/lib/mysql \
-v /usr/local/software/mysql/3310/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=123 \
-d mysql



docker run \
-it \
--name mysql_3311 \
--privileged \
--network wn_docker_net \
--ip 172.18.12.4 \
-p 3311:3306 \
-v  /usr/local/software/mysql/3311/conf/my.cnf:/etc/mysql/my.cnf \
-v  /usr/local/software/mysql/3311/data:/var/lib/mysql \
-v /usr/local/software/mysql/3311/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=123 \
-d mysql
```

#### 修改从服务器配置

```
server-id=201    #slave‘id
log_bin=wnhz-slave-01-logbin    #logbin name
relay_log=wnhz-slave-01-relay
read-only=1
```
```
server-id=202    #slave‘id
log_bin=wnhz-slave-02-logbin    #logbin name
relay_log=wnhz-slave-02-relay
read-only=1
```

#### master创建用户slave进行主从关联

> 进入主服务器容器
>
> docker exec -it mysql_3306 bash
>
> mysql -uroot -p123



```sql
create user 'slave'@'%' IDENTIFIED WITH mysql_native_password BY '123';
GRANT REPLICATION SLAVE,REPLICATION CLIENT ON *.* TO 'slave'@'%';
flush privileges;
```

#### 进入slave容器，连接mysql



```sql
change master to master_host='172.18.12.2',
master_user='slave',master_password='123',
MASTER_LOG_FILE='wnhz-master-logbin.000001',
MASTER_LOG_POS=156;
```



> 注意主服务器的MASTER_LOG_POS可能会发生变动可以进入主服务器容器
>
> 使用 show master status; 来查看



#### 启动slave

```shell
start slave;
```



### 4.查看是否配置正确

> 在slave中输入 show slave status \G;

这两项为yes则成功



![image-20231009204513424](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092045534.png)

#### 创建book前

![image-20231009205847945](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092058989.png)

#### 创建book后

![image-20231009205901218](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310092059257.png)



#### 从数据库依然能够写数据

> 从服务器使用root用户登录

解决方案：

创建一个用户做从处理，权限智能是读操作。

```sql
create user 'sd'@'%' IDENTIFIED WITH mysql_native_password BY '123';
GRANT SELECT ON *.* TO 'sd'@'%';
flush privileges;
```

#### 主从配置出现非两个YES

- [x] 关闭slave

  ```sql
  stop slave;
  ```

- [x] 重置slave: replaylog

  ```sql
  reset slave;
  ```

- [x] 重新配置 change to

  ```sql
  show master status;    #maseter
  
  change master to master_host='172.18.12.2', master_user='slave',master_password='123',MASTER_LOG_FILE='wnhz-master-logbin.000001',MASTER_LOG_POS=156;  #slave
  ```

- [x]  重新运行slave

  ```
  start slave;
  ```

  
