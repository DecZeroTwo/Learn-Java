# docker搭建rabbitmq、配置延迟队列插件

消息队列的作用：消峰、解耦、异步

## rabbitmq安装

查询

```shell
[root@localhost ~]# docker search rabbitmq
```

安装

```shell
[root@localhost ~]# docker pull rabbitmq
```

准备工作
创建文件夹：/usr/local/software/rabbitmq/data

运行容器

```shell
docker run -it \
--name rabbitmq \
--network wn_docker_net \
--ip 172.18.12.20 \
-v /etc/localtime:/etc/localtime \
-v /usr/local/software/rabbitmq/data:/var/lib/rabbitmq \
-e RABBITMQ_DEFAULT_USER=admin \
-e RABBITMQ_DEFAULT_PASS=123 \
-p 15672:15672 \
-p 5672:5672 \
-d rabbitmq
```

访问网页

![202312032014761.png](https://github.com/DecZeroTwo/blogimage/blob/main/images/202312032014761.png?raw=true)

![分类/rabbitmq/rabbitmq_2.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032016157.png)

## 安装延迟插件

下载支持3.9.x的插件

[https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases)

上传文件到linux

在/usr/local/software/下创建文件夹rabbitmq/plugins

拷贝插件到容器中

```shell
[root@localhost plugins]# docker cp ./rabbitmq_delayed_message_exchange-3.9.0.ez rabbitmq:/plugins
```

进入容器安装插件

```shell
[root@localhost plugins]# docker  exec -it rabbitmq bash
root@6d2342d51b11:/# rabbitmq-plugins enable rabbitmq_delayed_message_exchange
```

检查是否安装成功
打开管理页面，点击Exchange，点开Type下拉菜单查看是否含有x-delayed-message选项

![分类/rabbitmq/rabbitmq_3.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032021790.png)