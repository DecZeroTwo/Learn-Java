# 拦截器、过滤器、AOP的区别和联系



## 过滤器（Filter）

过滤器，顾名思义就是起到过滤筛选作用的一种事物，只不过相较于现实生活中的过滤器，这里的过滤器过滤的对象是客户端访问的web资源，

也可以理解为一种预处理手段，对资源进行拦截后，将其中我们认为的杂质（用户自己定义的）过滤，符合条件的放行，不符合的则拦截下来。

过滤器常见的使用场景

- 统一设置编码
- 过滤敏感字符
- 登录校验
- URL级别的访问权限控制
- 数据压缩



![](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311292034879.png)

### 过滤器的使用

首先需要实现 Filter接口然后重写它的三个方法
init 方法：在容器中创建当前过滤器的时候自动调用
destory 方法：在容器中销毁当前过滤器的时候自动调用
doFilter 方法：过滤的具体操作





```java
public interface Filter {
 
    public void init(FilterConfig filterConfig) throws ServletException;
 
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException;
 
    public void destroy();
}
```



## 拦截器（Interceptor）



拦截器（Interceptor）是一种用于拦截处理器执行的组件，它提供了一种机制，允许在请求到达目标处理器之前和响应返回客户端之前执行一些操作。与过滤器（Filter）类似，拦截器也用于对请求和响应进行预处理和后处理。



1. **身份验证和授权：** 拦截器可用于对请求进行身份验证，检查用户是否已登录或是否有足够的权限访问特定资源。这是一个常见的用例，可以确保只有授权用户能够执行敏感操作。
2. **数据预处理：** 在请求到达控制器之前，拦截器可以对请求进行预处理，例如解析请求参数、修改请求数据等。这有助于在请求到达处理器之前对数据进行初步处理。
3. **请求重定向：** 拦截器可以对请求进行重定向，例如阻止特定的请求或修改请求的路径。



![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311292042017.png)

### 拦截器的使用

```java
public interface HandlerInterceptor {
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
 
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
 
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
```





## AOP

相对于拦截器更加细致，而且非常灵活，拦截器只能针对URL做拦截，而AOP针对具体的代码，能够实现更加复杂的业务逻辑。

它通过将横切关注点（cross-cutting concerns）从主要的业务逻辑中分离出来，以便更容易管理和维护。

在传统的面向对象编程中，程序的功能通常被分解为一个个的类，而横切关注点则分散在这些类的多个方法中。



### AOP的主要概念包括：

1. **切面（Aspect）：** 切面是横切关注点的模块化单元。它定义了在何处（连接点）以及何时（通知）执行横切逻辑。
2. **连接点（Join Point）：** 连接点是在应用程序执行过程中能够插入切面的点，例如方法的调用、对象的创建、异常的处理等。
3. **通知（Advice）：** 通知定义了在切面的连接点上执行的行为。有不同类型的通知，包括前置通知（before）、后置通知（after）、返回通知（after-returning）、异常通知（after-throwing）和环绕通知（around）。
4. **切入点（Pointcut）：** 切入点定义了一组连接点的集合，通知将在这些连接点上执行。它是切面选择何处执行的逻辑。
5. **引入（Introduction）：** 引入允许在现有类上添加新方法或属性，而不需要修改类的源代码。
6. **目标对象（Target Object）：** 目标对象是在其上执行横切逻辑的对象。这是原始的业务逻辑对象。
7. **织入（Weaving）：** 织入是将切面应用到目标对象并创建新的代理对象的过程。织入可以发生在编译时、类加载时或运行时。



### 通知类型（Advice Types）：

1. **前置通知（Before Advice）：** 在目标方法执行之前执行。用于执行一些前置操作，例如权限检查、日志记录等。
2. **后置通知（After Advice）：** 在目标方法执行之后执行，不考虑方法的结果。常用于资源清理等操作。
3. **返回通知（After-Returning Advice）：** 在目标方法成功执行并返回结果后执行。可以访问目标方法的返回值。
4. **异常通知（After-Throwing Advice）：** 在目标方法抛出异常后执行。用于处理异常情况，例如日志记录、异常转换等。
5. **环绕通知（Around Advice）：** 包围目标方法的执行。可以在目标方法执行前后自定义处理，甚至完全替代目标方法的执行。具有最大的灵活性。

## 三者对比

三者功能类似，但各有优势，从过滤器--》拦截器--》AOP，拦截规则越来越细致，执行顺序依次是过滤器、拦截器、切面。一般情况下数据被过滤的时机越早对服务的性能影响越小，因此我们在编写相对比较公用的代码时，优先考虑过滤器，然后是拦截器，最后是aop。

![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311292057389.png)

Spring的拦截器与Servlet的Filter有相似之处，比如二者都是AOP编程思想的体现，都能实现权限检查、日志记录等。不同的是：

1. 使用范围不同：Filter是Servlet规范规定的，只能用于Web程序中。而拦截器既可以用于Web程序，也可以用于Application、Swing程序中。
2. 规范不同：Filter是在Servlet规范中定义的，是Servlet容器支持的。而拦截器是在Spring容器内的，是Spring框架支持的。
3. 使用的资源不同：同其他的代码块一样，拦截器也是一个Spring的组件，归Spring管理，配置在Spring文件中，因此能使用Spring里的任何资源、对象，例如Service对象、数据源、事务管理等，通过IOC注入到拦截器即可；而Filter则不能。
4. 深度不同：
   - Filter在只在Servlet前后起作用。实际上Filter和Servlet极其相似，区别只是Filter不能直接对用户生成响应。实际上Filter里doFilter()方法里的代码就是从多个Servlet的service()方法里抽取的通用代码，通过使用Filter可以实现更好的复用。Filter是一个可以复用的代码片段，可以用来转换Http请求、响应和头信息。Filter不像Servlet，它不能产生一个请求或者响应，它只是修改对某一资源的请求，或者修改从某一资源的响应。
   - 而拦截器能够深入到方法前后、异常抛出前后等，因此拦截器的使用具有更大的弹性。所以在Spring构架的程序中，要优先使用拦截器。
   - AOP相对于拦截器更加细致，而且非常灵活，拦截器只能针对URL做拦截，而AOP针对具体的代码，能够实现更加复杂的业务逻辑。



# 参考



[过滤器、拦截器和AOP的分析与对比 - 🐫沙漠骆驼 - 博客园 (cnblogs.com)](https://www.cnblogs.com/goSilver/p/11773972.html)



[拦截器、过滤器、AOP的区别和联系 - 掘金 (juejin.cn)](https://juejin.cn/post/7127450757502074888#heading-11)