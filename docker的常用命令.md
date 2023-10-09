## docker的常用命令

### docker镜像



![image-20231008195744365](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008194928202.png)

##### 镜像查询

```dockerfile
docker search mysql #(你要的软件)
```



![image-20231008194928202](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008194928202.png)

##### 拉取镜像

```dockerfile
dokcer pull mariadb #（搜索到的名称）
```



![image-20231008195330139](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008195330139.png)

##### 查询已下载镜像

```dockerfile
docker image ls
```



![image-20231008195435301](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008195435301.png)

##### 删除镜像

> docker image rm 镜像id 
>
> docker rmi 镜像名:版本号

```dockerfile
docker image rm 镜像id
```



![image-20231008195927366](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008195927366.png)

```dockerfile
docker rmi 镜像名
```



![image-20231008200331921](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008200331921.png)



### docker容器

##### 创建docker容器

> docker run -it  --name xxx(自定义容器名称)  -e xxxxx  -d 镜像名称

```dockerfile
docker run -it \
> --name mysqlx \
> -e MYSQL_ROOT_PASSWORD=123 \
> -d mysql
```



![image-20231008201102416](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008201102416.png)

##### 查看容器

> docker ps   ——查看当前运行的容器
>
> docker ps -a  ——查看所有容器

```dockerfile
dokcer ps
```



![image-20231008201619457](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008201619457.png)

```dockerfile
docker ps -a
```



![image-20231008201757852](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008201757852.png)

##### 查看容器日志

> docker logs+容器名——查看容器日志，可用于排错

```dockerfile
docker logs mysqlx
```



![image-20231008201904196](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008201904196.png)

##### 关闭容器

> docker stop+容器名称    ——关闭容器

```dockerfile
docker stop mysqlx
```



![image-20231008202335505](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008202335505.png)

##### 启动容器

> docker start+容器名称    ——启动容器

```dockerfile
docker start mysqlx
```



![image-20231008202523922](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008202523922.png)

##### 重启容器

>  docker restart+容器名称——重启容器

```dockerfile
docker restart mysqlx
```



![image-20231008202635188](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008202635188.png)

##### 删除容器

> docker rm+容器名称或容器id    ——删除容器（删除前要先关闭容器）

```dockerfile
docker rm mysqlx
```



![image-20231008202843327](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008202843327.png)

##### 进入容器

> docker exec -it+容器名+bash   ——进入容器

```dockerfile
docker exec -it mysqlx bash
```



![image-20231008203348594](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008203348594.png)

##### 查看容器的元数据

> docker inspect+容器名   ——查看容器的元数据

```dockerfile
docker inspect mysqlx
```



![image-20231008203601279](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008203601279.png)

##### 关键字查找

> grep IPA——查看含字符串"IPA"的所有行

```dockerfile
docker inspect mysqlx |grep IPA
```



![image-20231008203959202](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008203959202.png)

##### 输出重定向

> \>>+路径+文件名——将查询的数据以文件的形式输出到指定路径的文件

```dockerfile
docker inspect mysqlx >> /var/mysqlx.txt
```



![image-20231008204436240](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008204436240.png)

##### 创建容器时指定容器ip

> --ip 指定的ip地址

```dockerfile
docker run -it \
--name mysqlx \
--network wn_docker_net \
--ip 172.18.12.10 \
-p 3320:3306 \
-v /var/mysqlx:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123 \
-d mysql
```



![image-20231008204648527](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008204648527.png)



##### 文件映射

> docker run -v Linux文件/文件夹路径:容器的文件/文件夹位路径——在创建运行容器时将容器内部文件映射到linux指定文件上，保证数据持久化

```dockerfile
-v /var/mysqlx:/var/lib/mysql \
```

![image-20231008205215632](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008205215632.png)

![image-20231008205233224](https://raw.githubusercontent.com/DecZeroTwo/learn_java/main/images/image-20231008205233224.png)