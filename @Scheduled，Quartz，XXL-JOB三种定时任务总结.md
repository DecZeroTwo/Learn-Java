# @Scheduled，Quartz，XXL-JOB三种定时任务总结

## 一、@Scheduled

### 简介

`@Scheduled` 是 Spring 框架中用于声明定时任务的注解。通过使用 `@Scheduled` 注解，你可以指定一个方法应该在何时执行，无需依赖外部的调度器。

这个注解通常与`@EnableScheduling` 注解一起使用，后者用于启用 Spring 的任务调度功能。当在一个类的方法上使用 `@Scheduled` 注解时，Spring 框架会自动创建一个定时任务，并按照指定的规则执行该方法。

### 简单使用

首先建好一个Spring Boot项目，在启动类上加@EnableScheduling开启Spring的任务调度功能，创建一个类，再其方法上加上注解@Scheduled，通过cron表达式来指定什么时候执行该方法。

```java
@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```

```java
@Component
public class ScheduleTest {
    @Scheduled(cron = "0/3 * * * * ?")
    public void testScheduled() {
        //每3秒执行一次
        System.out.println("你好");
    }
}
```



![image-20231204205531391](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042055599.png)



## 二、Quartz

### 简介

Quartz是一个开源的、功能强大且灵活的作业调度框架，它支持复杂的调度需求，作业持久化、集群部署等功能。它允许你按照设定的时间规则执行特定的任务，例如定时执行、循环执行、间隔执行等。Quartz可以与Spring等框架无缝集成，被广泛应用于企业级应用中。

### 简单使用

pom.xml中导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

创建一个定时器任务类，实现QuartzJobBean接口的方法

```java
public class QuartzTest extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("我是Quartz，你好");
    }
}
```

创建Quartz配置类，构建定时任务触发器

```java
@Configuration
public class QuartzConfig {
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(QuartzTest.class)
                .storeDurably(true)
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withSchedule(CronScheduleBuilder.cronSchedule("0/3 * * * * ?"))
                .build();
    }
}
```

项目启动后，你就能看到控制台每3秒就会打执行一次

![image-20231204210647717](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042106844.png)

## 三、XXL-Job

### 简介

XXL-JOB是一个分布式任务调度平台，用于在分布式系统中进行任务调度和管理。XXL-JOB提供了可视化的任务管理界面、任务的动态添加、修改、删除等功能。它支持分布式部署，并提供了任务执行日志、任务运行状态等监控功能。XXL-Job是为了解决分布式系统中任务调度的问题而设计的，适合于大规模分布式系统中的定时任务调度。

### 简单使用

使用docker-compose安装

查找镜像，选择xuxueli/xxl-job-admin

```shell
[root@localhost /]# docker search xxl-job
NAME                                              DESCRIPTION                                      STARS     OFFICIAL   AUTOMATED
vulhub/xxl-job                                                                                     1                    
xuxueli/xxl-job-admin                             A lightweight distributed task scheduling fr…   150       
```

新建一个目录xxl-job，在它下面建一个logs目录和一个docker-compose.yml文件

```shell
[root@localhost xxl-job]# pwd
/usr/local/software/xxl-job
[root@localhost xxl-job]# ls
docker-compose.yml  logs
```

编辑docker-compose.yml（每个人的MySQL地址和ip不一样注意修改）

```yaml
version: '2'
#自定义的docker网络
networks:
  wn_docker_net:
    external: true
services:
  xxl-job-compose:
    #读取Dockerfile
    #build: .
    #镜像名称
    image: xuxueli/xxl-job-admin:2.3.1
    #容器名称
    container_name: xxl-job
    ports:
      - '9898:8080'
    environment:
      PARAMS: '--spring.datasource.url=jdbc:mysql://192.168.100.128:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
               --spring.datasource.username=root
               --spring.datasource.password=123'
    volumes:
      - /usr/local/software/xxl-job/logs:/data/applogs
    networks:
      wn_docker_net:
        ipv4_address: 172.18.12.100
```

进入xxl-job官网https://www.xuxueli.com/xxl-job/的github或gitee里的doc/db目录下找到建库语句tables_xxl_job.sql建数据库

docker-compose up -d后台构建执行

浏览器输入（你自己虚拟机的ip）192.168.100.128:9898/xxl-job-admin，用户为建库后的xxl_job_user表中的数据



![image-20231204211632865](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042116186.png)

![image-20231204211727635](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042117089.png)



Spring Boot整合XXL-Job

导入依赖

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.0</version>
</dependency>
```

配置application-xxljob.yml，进行一些执行器的配置

```yaml
server:
  port: 13000

#xxljob的配置
xxl:
  job:
    admin:
      addresses: http://192.168.100.128:9898/xxl-job-admin/
    executor:
      appname: xxl-job-executor-sample
      port: 9777
    accessToken: default_token

logging:
  level:
    com.wnhz.ssc: debug
```

创建XxlJobConfig配置类，导入刚才在yml文件中的配置

```java
@Configuration
@Slf4j
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses}")
    private String addresses;
    @Value("${xxl.job.executor.appname}")
    private String appName;
    @Value("${xxl.job.executor.port}")
    private int port;
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(addresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        log.debug("xxl-job初始化成功：{}", xxlJobSpringExecutor);
        return xxlJobSpringExecutor;
    }
}
```

创建XxlJob定时任务

```java
@Slf4j
@Component
public class MyXxlJob {
    @XxlJob("hello-xxljob")
    public void job() {
        log.debug("我的第一个xxljob");
    }
}
```



在XXL-Job任务调度中心中的任务管理中添加任务

点击新增

![image-20231204212635364](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042126851.png)

启动spring项目

点击启动

![image-20231204212840897](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202312042128075.png)

可以看到xxl-job启动成功

## 总结

Schedule是计划执行任务的通用术语。Quartz是Java任务调度框架，支持灵活任务管理。XXL-JOB是分布式任务调度平台，注重大规模系统，提供分布式任务调度和管理，包括动态调度、监控、日志记录等功能。选择取决于应用需求，Quartz适用于Java应用，XXL-JOB适用于分布式环境。