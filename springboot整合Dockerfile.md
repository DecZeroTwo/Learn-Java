# springboot整合Dockerfile

## Dockerfile

![Docker Images | DockerFile | Kubernetes | Docker & Kubernetes](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112046872.png)

> docker一般使用Dockerfile文本文件来构建镜像，当然也可以直接从容器来构建镜像。
>
> Dockerfile由一条条构建镜像所需的指令和说明构成。



### Dockerfile文件常用指令

#### FROM 指定基础镜像

FROM指令初始化一个新的构建阶段，并为后续指令设置**基础镜像**。因此，有效的 Dockerfile 必须以 FROM 指令开头。该镜像可以是任何有效的镜像。

##### 格式

> FROM 镜像名称
> FROM 镜像名称:<tag>
> FROM 镜像名称@<digest>
>
> 该`tag`或`digest`值是可选的。如果您省略其中任何一个，构建器默认使用一个`latest`标签。如果找不到该`tag`值，构建器将返回错误。

##### 案例

```dockerfile
FROM mysql:5.6
```



#### MAINTAINER指定维护者信息

MAINTAINER指令用于指定维护者的信息，用于为Dockerfile署名。

##### 格式

> MAINTAINER 名字

##### 案例

```dockerfile
MAINTAINER wzy
```



#### ADD 复制文件

`ADD`指令从`<src>`复制文件、目录、远程URL资源，并加入到镜像文件系统的`<dest>`路径下。

##### 格式

> ADD <src>... <dest>
> ADD ["<src>",... "<dest>"]

##### 案例 

```dockerfile
ADD target/springboot-dockerfile-1.0-SNAPSHOT.jar /app.jar
```



#### ENTRYPOINT 入口点

`ENTRYPOINT` 允许您配置将作为可执行文件运行的容器。用ENTRYPOINT的exec形式来设置相当稳定的默认命令和参数，然后使用**CMD**的任何一种形式来设置更可能更改的其他默认值。

##### 格式

> **ENTRYPOINT** [“executable”, “param1”, “param2”] (可执行文件, 优先)
> ENTRYPOINT command param1 param2 (shell内部命令)

ENTRYPOINT与CMD非常类似，不同的是通过docker run执行的命令不会覆盖ENTRYPOINT，而docker run命令中指定的任何参数，都会被当做参数再次传递给ENTRYPOINT。Dockerfile中只允许有一个ENTRYPOINT命令，多指定时会覆盖前面的设置，而只执行最后的ENTRYPOINT指令。

##### 案例

```dockerfile
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### EXPOSE

`EXPOSE`指令通知 Docker , 容器在运行时侦听指定的网络端口。可以指定端口是监听TCP还是UDP，如果不指定协议，默认为TCP。

该`EXPOSE`指令实际上并未暴露端口。它充当构建镜像的人和运行容器的人之间的一种文档，关于打算暴露哪些端口。要在运行容器时实际暴露端口，请在`docker run`中使用`-p`标志 来暴露和映射一个或多个端口，或者使用`-P`标志来发布所有暴露的端口并将它们映射到高阶端口。EXPOSE默认使用TCP，也能手动指定UDP。

##### 格式

> **EXPOSE** <port> [<port>…]

##### 案例

```dockerfile
EXPOSE 9091
```

#### VOLUME 指定挂载点

`VOLUME`指令创建一个具有指定名称的挂载点，并将其标记为来自本机或其他容器的“外部挂载卷”。

##### 格式

> VOLUME 文件路径

##### 案例

```dockerfile
VOLUME /tmp
```

### 创建Dockerfile

vim hello

在里面编辑

> 1. `FROM redis`
> 2. `MAINTAINER wzy`

![image-20231010195249531](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310101952640.png)

### 运行Dockerfile

> `docker build -t 镜像名称:版本(tag) -f 文件名称 .`

![image-20231010200923400](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102009510.png)



## 整合docker

### linux中配置远程访问docker

#### 打开文件

```shell
vim /lib/systemd/system/docker.service
```



![image-20231010203126397](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102031486.png)

修改

![image-20231010203204995](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102032108.png)

### 重启docker

```shell
[root@localhost df]# systemctl daemon-reload
[root@localhost df]# systemctl restart docker.service
```

### 测试2375端口是否有效

```shell
[root@localhost df]# netstat -nplt|grep 2375
tcp6       0      0 :::2375                 :::*                    LISTEN      110686/dockerd      
[root@localhost df]#
```

### curl测试服务是否启动

> curl http:/linux的ip地址:2375/info

![image-20231010203656987](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102036091.png)



### 开放2375端口

```shell
[root@localhost df]# firewall-cmd --add-port=2375/tcp --permanent 
success
[root@localhost df]# firewall-cmd --reload
success
[root@localhost df]# firewall-cmd --list-ports 
3306/tcp 2375/tcp
```

### idea安装配置docker插件

![image-20231010104702112](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102038356.png)

![image-20231010104849791](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102039871.png)

![image-20231010105023159](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102039512.png)

### 新建一个maven项目

#### 配置 pom

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.wnhz.springboot</groupId>
    <artifactId>hello-docker</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>
    <!-- dependencemanagement :maven的继承特性-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.6.13</version>
    </parent>
    <!-- 依赖-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
    <!-- springboot打包插件--->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 将项目打包

![image-20231010204654408](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102046462.png)

jar包在target目录中

![image-20231010204737962](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102047010.png)

### linux安装JDK

> 创建目录
>
> /usr/local/software/jdk



### 上传jdk

![image-20231010204137435](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102041479.png)



### 解压

```
tar -zxvf   jdk-8u341-linux-x64.tar.gz
```

### 配置

> JAVA_HOME, PATH, CLASS_PATH

#### 编辑/etc/profile文件

> vim /etc/profile

```
JAVA_HOME=/usr/local/software/jdk/jdk1.8.0_341
CLASSPATH=.:$JAVA_HOME/lib
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME CLASSPATH PATH
```

#### source /etc/profile

```shell
source /etc/profile
```

#### 测试jdk

```shell
java -version
```

![image-20231010204307498](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102043570.png)

#### 上传jar

上传你的jar包

![image-20231010204443732](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102044773.png)

#### 开启指定端口

```shell
[root@localhost jdk1.8.0_341]# firewall-cmd --add-port=9090/tcp --permanent 
success
[root@localhost jdk1.8.0_341]# firewall-cmd --reload
```

#### 运行

```shell
 java -jar hello-docker-1.0-SNAPSHOT.jar
```

> window与linux环境变量配置区别

| 序列 | 符号       | Windows | Linux |
| ---- | ---------- | ------- | ----- |
| 1    | 分隔符     | ;       | :     |
| 2    | 文件夹分割 | \       | /     |
| 3    | 取值符号   | %变量%  | $变量 |



### 在项目下创建Dockerfile

项目根目录新建一个Dockerfile

```dockerfile
FROM anapsix/alpine-java   
MAINTAINER wzy
EXPOSE 9090
ADD target/hello-docker-1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 创建容器

![image-20231010204936344](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102049395.png)



![image-20231010205207832](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102052891.png)

![image-20231010151853938](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102053571.png)



![image-20231010151950028](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102053209.png)

linux中查看容器是否生成

![image-20231010205658921](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310102056004.png)
