# Spring解决循环依赖的方案

## 什么是循环依赖？

循环依赖其实就是循环引用，也就是两个或则两个以上的bean互相持有对方，最终形成闭环。比如A依赖于B，B依赖于C，C又依赖于A。如下图：



![20200514091417809](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272032324.png)



## 三种循环依赖的情况

1. 构造器的循环依赖：这种依赖spring是处理不了的，直接抛出BeanCurrentlylnCreationException异常。
2. 单例模式下的setter循环依赖：通过“三级缓存”处理循环依赖，能处理。
3. 非单例循环依赖：无法处理。原型(Prototype)的场景是不支持循环依赖的，通常会走到AbstractBeanFactory类中下面的判断，抛出异常。

```java
if (isPrototypeCurrentlyInCreation(beanName)) 
{  throw new BeanCurrentlyInCreationException(beanName);}
```

原因很好理解，创建新的A时，发现要注入原型字段B，又创建新的B发现要注入原型字段A

## spring是如何解决的

**Spring为了解决单例的循环依赖问题，使用了三级缓存。**

### **Spring的三级缓存**

一级缓存（`singletonObjects`）：缓存的是**已经实例化、属性注入、初始化后**的 Bean 对象。

二级缓存（`earlySingletonObjects`）：缓存的是**实例化后，但未属性注入、初始化**的 Bean对象（用于提前暴露 Bean）。

三级缓存（`singletonFactories`）：缓存的是一个 `ObjectFactory`，主要作用是生成原始对象进行 AOP 操作后的**代理对象**



### 循环依赖案例

对象A和对象B 通过属性注入的方式产生了循环依赖，程序代码如下：

```java
@Component
public class ObjectA {

    @Autowired
    private  ObjectB b;

}
@Component
public class ObjectB {

    @Autowired
    private  ObjectA a;
}
```



首先我们Spring初始化，然后三级缓存还是空的



![image-20231127204857867](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272048916.png)



**第一步：尝试从缓存获取A对象**

在每次创建对象之前，Spring首先会尝试从缓存获取对象，但显然A还没有创建过，所以从缓存获取不到，所以会执行下面的对象实例化流程，尝试创建A对象。



**第二步：实例化A对象**

这一步会进行A对象的实例化，首先会调用构造方法进行实例化，再把自己放到一个ObjectFactory工厂里再保存到三级缓存。



![image-20231127204949905](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272049023.png)



**第三步：A对象进行属性注入**

我们发现A需要依赖于B对象，所以完成A要完成创建首先需要获得B，所以这时候会尝试从一级缓存获取B对象，但是此时一级缓存没有；然后我们会看B对象是否正处于创建中，显然现在B也还未开始创建，所以这个时候容器会先去创建完B对象，等拿到B对象的之后，然后再回来完成A的属性注入。



**第四步：实例化B对象**

尝试创建对象B之前，容器还是会尝试先从缓存里面查找，然而没找到；这才真正决定进行B对象的实例化，调用B的构造方法进行实例化，再把自己放到一个ObjectFactory工厂对象里保存到三级缓存里。



![image-20231127205134213](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272051347.png)



**第五步：B对象进行属性注入**

进行属性注入的时候，我们发现B的属性需要依赖于A对象，到这里sping也还是会尝试从缓存获取，先查看一级缓存有没有A对象，但是此时一级缓存没有A对象；

然后再看A对象是否正处于创建中（这时知道了A正处于创建中），所以就继续从二级缓存中去获取A（二级缓存也没有），最后去三级缓存里面找（此时A对象存在于三级缓存），从三级缓存里获得一个ObjectFactory，然后调用ObjectFactory.getObject()方法得到了A对象。拿到A对象后 这里会把A对象从三级缓存移出，然后把A保存到二级缓存。



![image-20231127205159657](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272051802.png)



### **第六步：B对象创建完成**

拿到A对象后，spring把A对象的引用赋值给B对象的属性，然后B就完成了创建，最后会把B对象从三级缓存移出，保存到一级缓存里去



![image-20231127205327335](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272053389.png)



### 第七步：**完成A对象的属性注入**

这个时候，我们的代码流程会返回到第三步，容器已经拿到了B对象了，所以可以继续完成A对象的属性注入工作了。

拿到B对象后，然后把B对象引用赋值给A的属性,最后同样也会把A对象从二级缓存移出，保存到一级缓存里去，同时也会移出创建中的标记。



![image-20231127205353493](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272053619.png)



![image-20231127205954976](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311272059054.png)

