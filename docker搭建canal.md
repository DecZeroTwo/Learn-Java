# docker搭建canal

canal是监听主mysql的binlog日志，实现和从一样能够监听到数据的变化，进而写进[rabbitmq](https://so.csdn.net/so/search?q=rabbitmq&spm=1001.2101.3001.7020)进行设置

## canal安装

docker search canal

![202312032026433.png](https://github.com/DecZeroTwo/blogimage/blob/main/images/202312032026433.png?raw=true)

pull canal/canal-server

![分类/redis/canal_2.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032051613.png)

![分类/redis/canal_3.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032051725.png)

上传挂载文件

![分类/redis/canal_4.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032051205.png)

## 配置

canal.properties

![分类/redis/canal_5.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032051096.png)

![分类/redis/canal_6.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032052927.png)

![分类/redis/canal_7.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032052457.png)

instance.properties

查看mysql的master status

![分类/redis/canal_8.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053893.png)

![分类/redis/canal_9.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053313.png)

![分类/redis/canal_10.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053239.png)

![分类/redis/canal_11.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053653.png)

![分类/redis/canal_12.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053359.png)

![分类/redis/canal_13.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032053940.png)

## 创建容器

```shell
docker run \
--name canal \
--privileged \
-p 11111:11111 \
--network wn_docker_net \
--ip 172.18.12.66 \
-v /usr/local/software/canal/conf/canal.properties:/home/admin/canal-server/conf/canal.properties \
-v /usr/local/software/canal/conf/instance.properties:/home/admin/canal-server/conf/example/instance.properties \
-v /usr/local/software/canal/logs:/home/admin/canal-server/logs/example \
-d canal/canal-server
```

docker logs canal

![分类/redis/canal_15.png  0 → 100644](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312032054123.png)