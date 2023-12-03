# docker搭建nginx实现负载均衡



## 安装nginx

查询安装

```shell
[root@localhost ~]# docker search nginx
[root@localhost ~]# docker pull nginx
```

准备
创建一个空的nginx文件夹里面在创建一个nginx.conf文件和conf.d文件夹



运行映射之前创建的文件夹
端口：8075映射80

```shell
docker run -it \
--name nginx \
-p 8075:80 \
-p 8080:8080 \
--privileged \
--network wn_docker_net \
--ip 172.18.12.90 \
-v /etc/localtime:/etc/localtime \
-v /usr/local/software/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
-v /usr/local/software/nginx/html:/usr/share/nginx/html \
-v /usr/local/software/nginx/conf/conf.d:/etc/nginx/conf.d \
-v /usr/local/software/nginx/logs:/var/log/nginx \
-d nginx
```

## 配置实现负载均衡

打开nginx.conf

在http中配置

![image-20231203195107286](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312031951362.png)

完整的文件内容：

```shell

user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    #myCode：
    #配置上有服务器，形成负载
    upstream activityBalance{
       server 192.168.133.100:15348 weight=1;
       server 192.168.200.113:15348 weight=1;
    }
    server{
       keepalive_requests 120;        #单连接请求上限次数
       listen    8080;                  #监听端口号

       location /api/{
        proxy_pass         http://activityBalance/api/; #反向代理服务器的访问地址
        proxy_set_header   Host $host;                                             #主机ip
        proxy_set_header   X-real-ip        $remote_addr;                          #客户端访问的真实ip
        proxy_set_header   X-Fowarded-For   $proxy_add_x_forwarded_for;            #代理转发历史
        proxy_redirect     off;
       }
    }

    include /etc/nginx/conf.d/*.conf;
}
```



此时访问nginx会负载均衡到两个服务器