# SpringMVC的工作流程

## SpringMVC 简介

SpringMVC是一种基于Spring实现了Web MVC设计模式的请求驱动类型的轻量级Web框架，使用了MVC的架构模式思想，将Web层进行指责解耦，并管理应用所需的生命周期，为简化日常开发，提供了很大便利。

1. Model（模型）：数据模型，提供要展示的数据，因此包含数据和行为，可以认为是领域模型或 JavaBean组件，不过现在一般都分离开来：Value Object（数据Dao） 和 服务层（行为Service）。也就是模型提供了模型数据查询和模型数据的状态更新等功能，包括数据和业务。

2. View（视图）：负责进行模型的展示，将逻辑视图解析成物理视图并渲染。

3. Controller（控制器）：接收用户请求，委托给模型进行处理，处理完毕后把返回的模型数据返回给视图，由视图负责展示。起调度作用。

## SpringMVC 工作流程分析

![2023-10-17-1697541112](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310171948933.png)

### 执行流程

1、 用户发送http请求至前端控制器DispatcherServlet。DispatcherServlet执行doService方法经过数据灌入后调用doDispatch方法，得到请求资源标识符（URI）

2、DispatcherServlet收到请求后调用HandlerMapping处理器映射器。

> DispatcherServlet 是 SpringMVC 中的前端控制器，所有的请求都会先经过它进行处理。DispatcherServlet会去遍历所有的HandlerMapping，寻找一个可以处理该HTTP请求的Handler。

3、DispatcherServlet通过HandlerAdapter处理器适配器调用处理器Handler

4、Handler执行完成返回ModelAndView

5、HandlerAdapter将Handler执行结果ModelAndView返回给DispatcherServlet

6， DispatcherServlet将ModelAndView传给ViewReslover视图解析器，并根据View进行渲染视图（即将模型数据填充至视图中）

> SpringMVC中处理视图最终要的两个接口就是ViewResolver和View， ViewResolver的作用是将逻辑视图解析成物理视图
> View的主要作用是调用其render()方法将物理视图进行渲染。
> 根据返回的ModelAndView，通过配置中定义的ViewResolver解析（必须是已经注册到Spring容器中的）结合Model和View，来渲染视图

7，将处理过后的ModelAndView视图交给DispatcherServlet，并由DispatcherServlet响应给客户端（展示画面）。

## SpringMVC核心组件

**前端控制器DispatcherServlet**
接收请求，响应结果，相当于转发器，中央处理器。有了dispatcherServlet减少了其它组件之间的耦合度。dispatcherServlet是整个流程控制的中心，由它调用其它组件处理用户的请求

**处理器映射器HandlerMapping**
根据请求的url查找Handler，HandlerMapping负责根据用户请求找到Handler即处理器，springmvc提供了不同的映射器实现不同的映射方式，例如：配置文件方式，实现接口方式，注解方式等。

**处理器适配器HandlerAdapter**
按照特定规则（HandlerAdapter要求的规则）去执行Handler，通过HandlerAdapter对处理器进行执行，这是适配器模式的应用。

**处理器Handler**
Handler 是继DispatcherServlet前端控制器的后端控制器，在DispatcherServlet的控制下Handler对具体的用户请求进行处理。

**视图解析器ViewResolver**
进行视图解析，根据逻辑视图名解析成真正的视图（view）
ViewResolver负责将处理结果生成View视图，ViewResolver首先根据逻辑视图名解析成物理视图名即具体的页面地址，再生成View视图对象，最后对View进行渲染将处理结果通过页面展示给用户。