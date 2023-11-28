# Java三种代理模式：静态代理、动态代理和CGLIB代理



## 代理模式

代理模式是23种设计模式种的一种。代理模式是一种结构型设计模式，它允许为其他对象提供一个替代品或占位符，以控制对这个对象的访问。代理模式可以在不修改被代理对象的基础上，通过扩展代理类，进行一些功能的附加与增强。

## 代理模式的构成

适配器模式一般包含三种角色：

- 抽象主题角色（Subject）：通过接口或抽象类声明真实角色实现的业务方法。
- 代理主题角色（Proxy）：实现抽象角色，是真实角色的代理（访问层），通过真实角色的业务逻辑方法来实现抽象方法，并可以附加自己的操作。
- 真实主题角色（RealSubject）：实现抽象角色，定义真实角色所要实现的业务逻辑，供代理角色调用。

代理模式主要有三种形式，分别是静态代理、动态代理（也称JDK代理、接口代理）和CGLIB代理（在内存动态创建对象而不需要实现接口，也可属于动态代理得范畴）



## 静态代理

静态代理是定义父类或者接口，然后被代理对象（即目标对象）与代理对象一起实现相同的接口或者是继承相同父类。**代理对象与目标对象实现相同的接口**，然后通过调用相同的方法来调用目标对象的方法。

- 优点：可不修改目标对象的功能，通过代理对象对目标功能扩展。
- 缺点：因为代理对象需要与目标对象实现一样的接口，所以会有很多代理类，一旦接口增加方法，目标对象与代理对象都要维护。



```java
public interface Animal {
	void eat();
}
```



```java
public class Dog implements Animal {
    @Override
    public void eat() {
        System.out.println("吃吃吃");
    }
}
```



```java
public class DogProxy implements Animal {
    private Animal target; //通过接口聚合目标对象
    public DogProxy(Animal target) {
        this.target = target;
    }
    @Override
    public void eat() {
        System.out.println("静态代理开始");
        target.eat();
        System.out.println("静态代理结束");
    }
}
```



```java
public class Main {
    public static void main(String[] args) {
        //创建被代理对象
        Dog dog = new Dog();
        //创建代理对象, 同时将被代理对象传递给代理对象
        DogProxy dogProxy = new DogProxy(dog);
        //通过代理对象，调用到被代理对象的方法
        dogProxy.eat();
    }
}
```



![image-20231128203835440](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311282040642.png)



## 动态代理

动态代理是在运行时动态生成代理类，不需要手动编写代理类。Java种的动态代理主要是使用java.lang.reflect.Proxy和java.lang.reflect.InvocationHandler接口实现。

优点：可以代理多个真实类，同时也可以在代理类中实现通用的逻辑，比如日志记录、异常处理等。
缺点：基于反射机制，性能相对较低；无法代理目标类中的final方法。

动态代理最主要的就是Proxy.newProxyInstance方法，它是用于创建动态代理对象的静态方法。它接受三个参数：

ClassLoader：用于加载动态代理类的类加载器。
interfaces：要代理的接口数组。
InvocationHandler：实现了InvocationHandler接口的对象，用于处理代理对象的方法调用。



```java
public interface Animal {
	void eat();
}
```



```java
public class Dog implements Animal {
    @Override
    public void eat() {
        System.out.println("吃吃吃");
    }
}
```



```java
public class AnimalInvocationHandler implements InvocationHandler {
    private Object target;
    public AnimalInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("吃前加热");
        Object result = method.invoke(target, args);
        System.out.println("吃后清理");
        return result;
    }
}
```

```java
public class AnimalProxy {
    public static Animal createProxy(Animal animal) {
        return (Animal) Proxy.newProxyInstance(
                animal.getClass().getClassLoader(),
                animal.getClass().getInterfaces(),
                new AnimalInvocationHandler(animal));
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Dog dog = new Dog();
        Animal dogProxy = AnimalProxy.createProxy(dog);
        dogProxy.eat();
    }
}
```



![image-20231128205554639](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311282055661.png)

> JDK 动态代理有一个最致命的问题是其只能代理实现了接口的类。
>
> 为了解决这个问题，我们可以用 CGLIB 动态代理机制来避免。



## CGLIB代理

CGLIB代理也叫作子类代理，它使目标对象不需要实现接口，是在内存中构建一个子类对象从而实现对目标对象功能扩展，有的也将CGLIB代理归属到动态代理。

CGLIB是一个高性能的代码生成包，它可以在运行期扩展java类与实现java接口。被许多AOP的框架使用（如Spring AOP）。Cglib包的底层是通过使用字节码处理框架ASM来转换字节码并生成新的类。



- 优点：可以为没有实现接口的类提供代理；性能比动态代理更高
- 缺点：不能代理代理final类和final方法；生成的代理类可能会占用较多的内存，因为它生成的代理类通常比目标类更庞大



```java
public class Dog {
    public void eat() {
        System.out.println("吃吃吃");
    }
}
```



```java
public class AnimalMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("吃前加热");
        Object result = methodProxy.invokeSuper(o, objects);
        System.out.println("吃后清理");
        return result;
    }
}
```



```java
public class DogCglibProxy {
    public static Dog createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Dog.class);
        enhancer.setCallback(new AnimalMethodInterceptor());
        return (Dog) enhancer.create();
    }
}
```



```java
public class Main {
    public static void main(String[] args) {
        Dog dogProxy = DogCglibProxy.createProxy();
        dogProxy.eat();
    }
}
```



![image-20231128211037905](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311282110957.png)

CGLIB与java动态代理的区别

1. **实现方式**:
   - **Java动态代理**：使用`java.lang.reflect.Proxy`和`InvocationHandler`接口。Java动态代理只能为接口创建代理对象，它是基于接口的代理。通过`Proxy.newProxyInstance()`方法可以动态地生成实现了指定接口的代理类。
   - **CGLIB**：通过继承目标类的方式创建代理对象。CGLIB可以为类创建代理，而不仅仅是接口。它通过生成目标类的子类，在子类中增加代理逻辑来实现动态代理。
2. **代理对象类型**:
   - **Java动态代理**：只能代理实现了接口的类。它要求目标对象实现一个或多个接口，然后通过代理对象来实现这些接口。
   - **CGLIB**：可以代理没有实现任何接口的类。它通过继承目标类来创建代理对象，因此目标类不需要实现任何接口。
3. **性能**:
   - **Java动态代理**：由于生成的代理对象是基于接口的，因此在调用代理方法时，会通过接口的方法调用`InvocationHandler`的`invoke`方法，再由`invoke`方法调用实际的目标方法。这一层额外的调用可能会引入一些性能开销。
   - **CGLIB**：生成的代理对象是目标类的子类，因此调用代理方法时，直接调用子类中的方法，避免了通过接口的中间层，可能会在一些情况下具有更好的性能。
4. **构造方式**:
   - **Java动态代理**：通过`Proxy.newProxyInstance()`方法动态生成代理对象，需要提供一个实现`InvocationHandler`接口的对象。
   - **CGLIB**：通过CGLIB库动态生成代理对象，无需提供`InvocationHandler`。CGLIB通过继承目标类并重写其中的方法来实现代理逻辑。



## 应用场景

代理模式可以在多种场景下使用，包括但不限于以下几个方面：

1. 访问控制：代理模式可以用来控制对实际对象的访问权限。比如，只有特定用户或角色才能访问某些敏感数据。
2. 远程访问：代理模式可以用来处理远程对象的访问。比如，通过代理对象来访问远程Web服务。
3. 延迟加载：代理模式可以用来实现延迟加载。比如，通过代理对象来加载某些资源或数据，以避免在程序启动时就加载所有数据。
4. 虚拟代理：当需要延迟加载或预加载大量数据时，可以使用虚拟代理来提高程序的性能和效率。
5. 缓存代理：当需要对经常使用的数据进行缓存时，可以使用缓存代理来管理和优化数据的访问效率。