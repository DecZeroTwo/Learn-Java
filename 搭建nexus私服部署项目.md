# 搭建nexus私服部署项目

## 下载安装

### 下载nexus

> 访问**官网**: https://help.sonatype.com/repomanager3/product-information/download

![image-20231012191625760](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121916904.png)

由于网络不稳定，下载问题，有科学上网工具的可以很快下好，这里我把文件放在附件中，有需要可以去拿

### 上传到linux服务器

先新建一个文件夹

```shell
mkdir nexus
```

![image-20231012192539297](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121925338.png)

然后把文件上传到这个目录

![image-20231012192151465](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121921508.png)

![image-20231012192801641](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121928688.png)

### 进入文件夹解压文件

![image-20231012192908976](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121929020.png)

> tar -zxvf 文件名

```shell
tar -zxvf nexus-3.61.0-02-unix.tar.gz
```

![image-20231012193045951](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121930023.png)







## nexus配置

### 进入nexus-3.40.1/bin文件夹

> cd /usr/local/software/nexus/nexus-3.61.0-02/bin

![image-20231012193422136](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121934238.png)

![image-20231012193612246](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121936305.png)

### 编辑nexus.vmoptions文件

根据自己机器内存大小，适当配置内存。内存太小未来启动nexus会失败。

> **vim** nexus.vmoptions

![image-20231012193745519](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121937574.png)



### 配置端口

默认端口为8081，如果需要在要在文件中配置端口。（如果不需改端口，此处可以忽略）

进入etc文件夹



![image-20231012193914853](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121939916.png)



查看nexus-default.properties文件



![image-20231012194037909](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121940009.png)

![image-20231012194226776](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121942913.png)

开放端口号

```shell
[root@localhost etc]# firewall-cmd --add-port=8081/tcp --permanent 
success
[root@localhost etc]# firewall-cmd --reload
success
```

![image-20231012194440094](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121944154.png)





## 运行nexus

### 启动nexus

运行命令**./nexus start**

![image-20231012195052785](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121950839.png)

出现`Starting nexus`表示启动成功

### 查看nexus的运行状态

nexus-3.40.1 ： 服务器文件夹，启动程序等。

sonatype-work: 工作空间，数据文件。

![image-20231012195500456](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121955504.png)

在浏览器中输入http://192.168.221.129:8081/ (你的Linux服务器地址:nexus配置的端口号)



![image-20231012195934172](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310121959304.png)

![image-20231012200049442](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122000513.png)

### 修改管理员密码

点击Sing in

用户名:admin

密码:第一次登录需要在nexus中获取

查看管理员密码

通过以下路径下的文件获取

![image-202310132317534](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310132317534.png)

![image-20231012210148087](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122101155.png)

第一次登录会要求修改管理员密码

![微信截图_20231012211818](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122119536.png)



![微信截图_20231012211029](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122120231.png)

![202310122012617](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122121938.png)





## 配置私有仓库

### nexus中默认仓库

> **maven-releases** (Version policy=Release)默认只允许上传不带SNAPSHOT版本尾缀的包,默认部署策略是Disable redeploy 不允许重复上传相同版本号信息的jar,避免包版本更新以后使用方无法获取到最新的包。
>
> **maven-snapshots** (Version policy=Snapshot)只允许上传带SNAPSHOT版本尾缀的包,默认部署策略是Allow redeploy,允许重复上传相同版本号信息的jar,每次上传的时候会在jar的版本号上面增加时间后缀信息。
>
> **maven-central** 中央仓库的拷贝,如果环境可以访问中央仓库,则可以获取到相关的包,否则没用
>
> **maven-public** 仓库组,不是实际个一个仓库地址,只是将现有的组合到一次,可以通过它看到所属组内全部仓库的jar信息

![image-20231012201659955](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122017016.png)

### 创建自定义仓库

![image-20231012201808779](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122018842.png)



选择maven2(hosted)



![微信截图_20231012211109](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122121869.png)

![202310122019521](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122125574.png)

![image-20231012141015833](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122019010.png)

![image-20231012202009955](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122020032.png)

**同样添加release仓库和snapshot仓库**

**release**

![image-20231023192616188](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231926306.png)

![image-20231023192653600](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231926672.png)



**snapshot**

![image-20231023193618391](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231936477.png)

![image-20231023193703685](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231937741.png)









### 添加新建仓库到maven-public群组中

![202310122021676](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122126433.png)



![image-20231023193943261](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231939335.png)



![image-20231023194015343](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231940404.png)





## 批量上传本地文件到自定义仓库中

### 上传本地仓库内容到linux服务器

在nexus文件夹中新建文件夹repo

![image-20231012202447312](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122024381.png)

进入repo上传文件

![image-20231012202732000](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122027070.png)

![image-20231012202815617](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122028669.png)

![image-20231012202831887](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122028966.png)

### 编辑批量上传脚本

在本地仓库上传的文件夹(maven-repository)下创建一个shell脚本，命名 localrepository.sh

- 创建脚本

  > touch repo.sh

- 编辑脚本

  > vim repo.sh

  ```sh
  #!/bin/bash
  while getopts ":r:u:p:" opt; do
      case $opt in
          r) REPO_URL="$OPTARG"
          ;;
          u) USERNAME="$OPTARG"
          ;;
          p) PASSWORD="$OPTARG"
          ;;
      esac
  done
  find . -type f -not -path './mavenimport\.sh*' -not -path '*/\.*' -not -path '*/\^archetype\-catalog\.xml*' -not -path '*/\^maven\-metadata\-local*\.xml' -not -path '*/\^maven\-metadata\-deployment*\.xml' | sed "s|^\./||" | xargs -I '{}' curl -u "$USERNAME:$PASSWORD" -X PUT -v -T {} ${REPO_URL}/{} ;
  ```



![image-20231012203028015](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122030278.png)



### 添加权限

给脚本repo.sh添加执行权限



![image-20231012203613104](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122036145.png)

> chmod +x repo.sh

![image-20231012203632969](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122036013.png)



### 导入本地仓库到nexus私有仓库

执行以下命令

> ./repo.sh -u nexus用户名 -p nexus密码 -r 仓库地址

```
./repo.sh -u admin -p 123 -r http://192.168.221.129:8081/repository/wnhz-repository/
```



![image-20231012203822352](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122038433.png)

![image-20231012203944461](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122039501.png)



![image-20231012204035967](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122040301.png)



![image-20231012204157888](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122041947.png)



## 项目中引用nexus库

### 在maven的conf/settings.xml中配置server



id可以随意但是pom.xml中要保持一致

```xml
<server>
  <id>wnhz</id>
  <username>admin</username>
  <password>123</password>
</server>
<server>
  <id>wnhz-releases</id>
  <username>admin</username>
  <password>123</password>
</server>
<server>
  <id>wnhz-snapshots</id>
  <username>admin</username>
  <password>123</password>
</server>
```





![image-20231023194759146](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231947217.png)





### 在maven中配置镜像

中央仓库的资源从阿里云访问，其它资源来自nexus私服。

![image-20231012205304070](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122053151.png)



```xml
  <mirrors>
    <!-- mirror
     | Specifies a repository mirror site to use instead of a given repository. The repository that
     | this mirror serves has an ID that matches the mirrorOf element of this mirror. IDs are used
     | for inheritance and direct lookup purposes, and must be unique across the set of mirrors.   
    <mirror>
      <id>mirrorId</id>
      <mirrorOf>repositoryId</mirrorOf>
      <name>Human Readable Name for this Mirror.</name>
      <url>http://my.repository.com/repo/path</url>
    </mirror>
     -->
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>central</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
    <mirror>
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <name>nexus djs</name>
      <url>http://192.168.0.101:8081/repository/maven-public/</url>
    </mirror>
  </mirrors>
```

私服访问地址从下图获取：

![image-20231012205443917](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310122054007.png)



### profile

**nexus仓库配置**



```xml
<profile>
  <!--profile 的 id-->
  <id>nexus</id>
  <repositories>
      <repository>
          <!--仓库 id，repositories 可以配置多个仓库，保证 id 不重复-->
          <id>nexus</id>
          <!--仓库地址，即 nexus 仓库组的地址-->
          <url>http://192.168.221.129:8081/repository/maven-public/</url>
          <!--是否下载 releases 构件-->
          <releases>
              <enabled>true</enabled>
          </releases>
          <!--是否下载 snapshots 构件-->
          <snapshots>
              <enabled>true</enabled>
          </snapshots>
      </repository>
  </repositories>
  <pluginRepositories>
      <!-- 插件仓库，maven 的运行依赖插件，也需要从私服下载插件 -->
      <pluginRepository>
          <!-- 插件仓库的 id 不允许重复，如果重复后边配置会覆盖前边 -->
          <id>public</id>
          <name>Public Repositories</name>
          <url>http://192.168.221.129:8081/repository/maven-public/</url>
          <!--是否下载 releases 构件-->
          <releases>
              <enabled>true</enabled>
          </releases>
          <!--是否下载 snapshots 构件-->
          <snapshots>
              <enabled>true</enabled>
          </snapshots>
      </pluginRepository>
  </pluginRepositories>
</profile>
```



![image-20231023195707673](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231957815.png)

### activeprofile

```xml
<activeProfiles>
	<activeProfile>nexus</activeProfile>
</activeProfiles>
```





![image-20231023195855344](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310231958409.png)



### 开发使用maven部署



> pom中部署maven



```xml
<distributionManagement>
    <snapshotRepository>
        <id>wnhz-snapshots</id>
        <url>http://192.168.221.129:8081/repository/wnhz-snapshots/</url>
    </snapshotRepository>
    <repository>
        <id>wnhz-releases</id>
        <url>http://192.168.221.129:8081/repository/wnhz-releases/</url>
    </repository>
</distributionManagement>
<repositories>
    <repository>
        <id>nexus</id>
        <url>http://192.168.221.129:8081/repository/maven-public/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```



![image-20231023200333631](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310232003718.png)



![image-20231023200557301](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310232005387.png)



![image-20231023200705295](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310232007379.png)



![image-20231023200814629](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310232008695.png)



这样我们就可以在之后的项目的pom.xml直接引入部署的jar包依赖了

```xml
<dependency>
    <groupId>com.wnhz.bms.common</groupId>
    <artifactId>bms-common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

