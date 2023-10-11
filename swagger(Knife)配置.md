# swagger(Knife4j)

## 前言

在前后端分离开发的过程中，前端和后端需要进行api对接进行交互，就需要一个api规范文档，方便前后端的交互，但api文档不能根据代码的变化发生实时动态的改变，这样后端修改了接口，前端不能及时获取最新的接口，导致调用出错，需要手动维护api文档，加大了开发的工作量和困难，而swagger的出现就是为了解决这一系列的问题。

## 什么是 Swagger

**[Swagger](https://apifox.com/apiskills/what-is-swagger/)** 是一个开源的 API 设计和文档工具，它可以帮助开发人员更快、更简单地设计、构建、文档化和测试 [**RESTful API**](https://apifox.com/apiskills/rest-api/)。Swagger 可以自动生成交互式 API 文档、客户端 SDK、服务器 stub 代码等，从而使开发人员更加容易地开发、测试和部署 API。

![ASP.NET Core Swagger UI Authorization using IdentityServer4](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310111903069.png)



## knife4j(swagger的本地增强解决方案)

[Knife4j · 集Swagger2及OpenAPI3为一体的增强解决方案. | Knife4j (xiaominfo.com)](https://doc.xiaominfo.com/)

![image-20230628112011921](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310111911277.png)

### knife4j与springboot的兼容性

| Spring Boot版本 | Knife4j Swagger2规范  | Knife4j OpenAPI3规范 |
| --------------- | --------------------- | -------------------- |
| 1.5.x~2.0.0     | <Knife4j 2.0.0        | >=Knife4j 4.0.0      |
| 2.0~2.2         | Knife4j 2.0.0 ~ 2.0.6 | >=Knife4j 4.0.0      |
| 2.2.x~2.4.0     | Knife4j 2.0.6 ~ 2.0.9 | >=Knife4j 4.0.0      |
| 2.4.0~2.7.x     | >=Knife4j 4.0.0       | >=Knife4j 4.0.0      |
| >= 3.0          | >=Knife4j 4.0.0       | >=Knife4j 4.0.0      |

### 引入依赖

在[Knife4j](https://doc.xiaominfo.com/)的官网我们可以看到Knife4j的官方start包

![image-20231011191449270](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310111914325.png)

```xml
<!--引入Knife4j的官方start包,该指南选择Spring Boot版本<3.0,开发者需要注意-->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    <version>4.3.0</version>
</dependency>
```

![image-20231011191633579](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310111916615.png)

需要在Controller类上添加注解`@Slf4j`

## 常用注解



#### 1、用在请求的类上

> @Api：
>
> tags="说明这个类的作用"
> value="该参数没有意义，通常不需要配置"


示例：

```java
@Api(tags="图书类型接口")
```



#### 2、用在请求的方法上(无参数)：

>@ApiOperation："用在请求的方法上，说明方法的作用"
>
>value="说明方法的作用"
>notes="方法的备注"

示例：

```kotlin
@ApiOperation(value="findAll",notes="对所有图书类型查询")
```

#### 3、用在请求的方法上(带参数)

> @ApiImplicitParams：用在请求的方法上，包含一组参数说明
> @ApiImplicitParam：用在 @ApiImplicitParams 注解中，指定一个请求参数的信息    
>  name：参数名
>  value：参数的说明
>  required：参数是否必传
>  paramType：参数放在哪个地方
>    · header --> 请求参数的获取：@RequestHeader
>    · query --> 请求参数的获取：@RequestParam
>    · path（用于restful接口）--> 请求参数的获取：@PathVariable
>    · body（不常用）
>    · form（不常用）   
>  dataType：参数类型，默认String，其它值dataType="Integer"    
>  defaultValue：参数的默认值

示列：

```java
@ApiImplicitParams(
	{
		@ApiImplicitParam(name = "name", value = "请输入新图书类型名称", required = true,paramType = "query",dataType = "String")
	}
)
```

#### 4、用于请求的方法上，表示响应

> @ApiResponses：用于请求的方法上，表示一组响应
> @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
> code：数字，例如400
> message：信息，例如"请求参数没填好"
> response：抛出异常的类

示例：

```java
@ApiResponses({
    @ApiResponse(code=400,message="请求参数没填好"),
    @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
})
```

#### 5、用于响应类上，表示一个返回响应数据的信息

> @ApiModel：用于响应类上，表示一个返回响应数据的信息
>
> （这种一般用在post创建的时候，使用@RequestBody这样的场景，请求参数无法使用@ApiImplicitParam注解进行描述的时候）
>
> @ApiModelProperty：用在属性上，描述响应类的属性


示例:

```java
@ApiModel(description= "返回响应数据")
public class RestMessage implements Serializable{
 
    @ApiModelProperty(value = "是否成功")
    private boolean success=true;
    @ApiModelProperty(value = "返回对象")
    private Object data;
    @ApiModelProperty(value = "错误编号")
    private Integer errCode;
    @ApiModelProperty(value = "错误信息")
    private String message;
}
```

##  项目案例

### 封装返回对象类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResp <T>{
    private int code;
    private String msg;
    private T result;
    private LocalDate time;
}
```

### 实体类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Type {
    private Integer id;
    private String name;
    private String createBy;
    private Date createTime;
    private Date updateTime;
}
```



### Controller

```java
@Api(tags = "图书类型接口")
@RestController
@RequestMapping("/api/type")
@Slf4j
public class TypeController {

    @ApiOperation(value = "findAll", notes = "对所有图书类型查询")
    @GetMapping("/findAll")
    public HttpResp<List<Type>> findAll() {
        List<Type> list = new ArrayList<>(20);
        for (int i = 0; i <= 100; i++) {
            list.add(new Type(i, "type_" + i, "admin", new Date(), new Date()));
        }
        log.debug("查询所有图书的类型数量:{}", list.size());
        return new HttpResp<>(200, "success", list, LocalDate.now());
    }

    @ApiOperation(value = "addType", notes = "添加新图书类型接口")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", value = "请输入新图书类型名称", required = true,paramType = "query",dataType = "String")
            }
    )
    @PostMapping("/addType")
    public HttpResp<String> addType(@RequestPart @RequestParam("name") String name) {
        log.debug("添加新的类型:{}", name);
        return new HttpResp<>(200, "success", null, LocalDate.now());
    }

    @ApiOperation(value = "uploadExcel", notes = "上传excel文件进行解析")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "uploadExcel",value = "上传文件", required = true)
    )
    @PostMapping("/uploadExcel")
    public HttpResp<String> uploadExcel(@RequestPart @RequestParam("excel") MultipartFile excel) {
        String originalFilename = excel.getOriginalFilename();
        log.debug("上传文件名称:{}", originalFilename);
        return new HttpResp<>(200, "success", originalFilename + "上传成功", LocalDate.now());
    }

    @ApiOperation(value = "qiang", notes = "抢购图书")
    @PutMapping("/qiang")
    public HttpResult<Integer> qiang() {
        int retValue;
        try {
            retValue = ibs.qiangGou();
            log.debug("retValue:{}", retValue);
        } catch (Exception e) {
            return new HttpResult<>(500, e.getMessage(), -1, LocalDate.now());
        }
        return new HttpResult<>(200, "抢购成功", retValue, LocalDate.now());
    }
}
```



## 测试

> http://localhost:9091/doc.html
>
> (你的spring配置的server.port)

yml文件配置

```yaml
server:
  port: 9091

knife4j:
  enable: true
  
logging:
  level:
    com.wnhz.book.bk: debug
```



### 主页

![image-20231011201315701](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112013810.png)

### Model

![image-20231011201352404](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112013480.png)

![image-20231011201439827](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112014898.png)

### API测试

![image-20231011203123334](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112031408.png)

![image-20231011203331939](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112033016.png)

![image-20231011203357915](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112033979.png)



### 生成离线文档

![image-20231011202043247](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112020315.png)



## Swagger接口测试

### 引入依赖

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 配置swagger

> 配置原因：由于**Spring Boot 2.6.x** 请求路径与 Spring MVC 处理映射匹配的默认策略从`AntPathMatcher`更改为`PathPatternParser`。所以需要设置`spring.mvc.pathmatch.matching-strategy为ant-path-matcher`来改变它。

```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

### swagger配置

```java
@Configuration
public class Swagger3Config {
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(("com.ohb.springboot.controller")))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfo("springboot测试swagger3项目",
                "springboot整合swagger3文档",
                "1.1",
                "https://www.bilibili.com/video/BV1tK411y7Uf/",
                new Contact("ohb","https://blog.csdn.net/qq_36115196/", "ohb@djs.com"),
                "Apache2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<VendorExtension>());
    }
}
```

### 常用注解

注解和knife4j一样。

### 案例

#### 无参数

```java
@ApiOperation(value = "findAll", notes = "对所有图书类型查询")
    @GetMapping("/findAll")
    public HttpResp<List<Type>> findAll() {
        List<Type> list = new ArrayList<>(20);
        for (int i = 0; i <= 100; i++) {
            list.add(new Type(i, "type_" + i, "admin", new Date(), new Date()));
        }
        log.debug("查询所有图书的类型数量:{}", list.size());
        return new HttpResp<>(200, "success", list, LocalDate.now());
    }
```

#### 方法有参数

```java
@ApiOperation(value = "addType", notes = "添加新图书类型接口")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", value = "请输入新图书类型名称", required = true,paramType = "query",dataType = "String")
            }
    )
    @PostMapping("/addType")
    public HttpResp<String> addType(@RequestPart @RequestParam("name") String name) {
        log.debug("添加新的类型:{}", name);
        return new HttpResp<>(200, "success", null, LocalDate.now());
    }
```

#### 带上传参数

```java
@ApiOperation(value = "uploadExcel", notes = "上传excel文件进行解析")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "uploadExcel",value = "上传文件", required = true)
    )
    @PostMapping("/uploadExcel")
    public HttpResp<String> uploadExcel(@RequestPart @RequestParam("excel") MultipartFile excel) {
        String originalFilename = excel.getOriginalFilename();
        log.debug("上传文件名称:{}", originalFilename);
        return new HttpResp<>(200, "success", originalFilename + "上传成功", LocalDate.now());
    }
```



## 压力测试(JMeter)

### 添加线程组

![image-20231011192129596](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112057923.png)

![image-20231011192321281](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112058504.png)



> 上述图表示 1s请求100次 执行1次

### 添加http请求

![image-20231011192401510](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112058624.png)

![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112058385.png)

> http请求填写如图所示，内容编码可不填写

### 添加结果树

![image-20231011192501443](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112055670.png)

### 开始测试

![image-20231011192758364](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112055658.png)

### 查看结果

![image-20231011192828707](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112055824.png)

![在这里插入图片描述](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310112102049.png)
