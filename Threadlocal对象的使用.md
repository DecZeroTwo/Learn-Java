# Threadlocal对象的使用

## ThreadLocal简介

从名字我们就可以看到ThreadLocal 叫做线程本地变量，意思是说，ThreadLocal 中填充的的是当前线程的变量，该变量对其他线程而言是封闭且隔离的，ThreadLocal 为变量在每个线程中创建了一个副本，这样每个线程都可以访问自己内部的副本变量。

- ThreadLocal 的主要作用如下：

  

  线程隔离：ThreadLocal 可以实现线程间的数据隔离。每个线程可以独立地使用和修改自己的线程局部变量，而不会与其他线程的变量产生冲突。这对于多线程环境下的共享数据访问和线程安全性非常有用。

  

  线程上下文传递：ThreadLocal 可以用于在线程之间传递上下文信息。例如，在一个使用线程池的应用中，可以通过 ThreadLocal 在任务提交和执行之间传递一些上下文信息，而不需要显式地将上下文作为参数传递。

  

  线程封闭性：ThreadLocal 可以实现数据的线程封闭，即将某些数据限定在特定的线程中使用。这对于一些需要临时保存线程私有数据的场景非常有用，避免了数据被其他线程访问和修改的风险。



![image-20231025202311085](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310252023152.png)



## ThreadLocal与Synchronized的区别

ThreadLocal<T>其实是与线程绑定的一个变量。ThreadLocal和Synchonized都用于解决多线程并发访问。

但是ThreadLocal与synchronized有本质的区别：

1、Synchronized用于线程间的数据共享，而ThreadLocal则用于线程间的数据隔离。

2、Synchronized是利用锁的机制，使变量或代码块在某一时该只能被一个线程访问。而ThreadLocal为每一个线程都提供了变量的副本

，使得每个线程在某一时间访问到的并不是同一个对象，这样就隔离了多个线程对数据的数据共享。

而Synchronized却正好相反，它用于在多个线程间通信时能够获得数据共享。




## ThreadLocal的原理

我们来看ThreadLocal的源码
```java
public T get() {
	//获取当前线程
    Thread t = Thread.currentThread();
    //获取当前线程的ThreadLocalMap
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}


ThreadLocalMap getMap(Thread t) {
	//获取线程的threadLocals
    return t.threadLocals;
}
```

```java
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        map.set(this, value);
    } else {
        createMap(t, value);
    }
}
```


```java
static class ThreadLocalMap {

        /**
         * The entries in this hash map extend WeakReference, using
         * its main ref field as the key (which is always a
         * ThreadLocal object).  Note that null keys (i.e. entry.get()
         * == null) mean that the key is no longer referenced, so the
         * entry can be expunged from table.  Such entries are referred to
         * as "stale entries" in the code that follows.
         */
        static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
```

从源码中我们可以知道ThreadLocal的get()方法是先拿到当前线程，然后在拿到当前线程的threadLocals，threadLocals是什么呢？它是ThreadLocal的内部静态类ThreadLocalMap而它的构成主要是用Entry来保存数据 ，而且还是继承的弱引用。在Entry内部使用ThreadLocal作为key，使用我们设置的value作为value。
也就是说ThreadLocal本身并不存储任何数据，它只是作为线程中的threadLocals(ThreadLocalMap)的key，每个线程通过它来去自己的成员变量threadLocals中来获取value，于就是实现了不同的线程在ThreadLocal中get()和set()是完全隔离的。
执行流程：
get()——>获取当前线程——>拿到当前线程的threalLocalMap——>如果threalLocalMap不为null并且数据不为空，获取threalLocalMap中存储的值，如果是数据为null，则初始化，初始化的结果，TheralLocalMap中存放key值为threadLocal，值为null
set()——>获取当前线程——>拿到当前线程的threalLocalMap——>如果threalLocalMap不为null，则直接更新要保存的变量值key为ThreadLocal，value为参数，否则创建threadLocalMap，并赋值

## ThreadLocal的简单使用

```java
public class ThreadLocalDemo {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();


    public static void main(String[] args) throws InterruptedException {

        System.out.println("A线程");

        new Thread(new Runnable() {
            public void run() {
                //打印线程本地变量
                System.out.println("当前线程本地变量:" + threadLocal.get());
                threadLocal.set("A");
                System.out.println("当前线程本地变量:" + threadLocal.get());
            }
        },"A").start();

        Thread.sleep(1000);

        System.out.println("B线程");

        new Thread(new Runnable() {
            public void run() {
                System.out.println("当前线程本地变量:" + threadLocal.get());
                threadLocal.set("B");
                System.out.println("当前线程本地变量:" + threadLocal.get());
            }
        },"B").start();
    }
}



A线程
当前线程本地变量:null
当前线程本地变量:A
B线程
当前线程本地变量:null
当前线程本地变量:B
```



从这个示例中我们可以看到，两个线程分表获取了自己线程存放的变量，他们之间变量的获取并不会错乱。(这里只是演示，每次用完ThreadLocal最好都要remove)



## ThreadLocal的方法



1.使用 `ThreadLocal` 类的 set() 方法设置值：

```java
threadLocal.set(value);
```


2.使用 ThreadLocal 类的 get() 方法获取值：

```java
T value = threadLocal.get();
```

这将返回当前线程的 `ThreadLocal` 实例中存储的值。



3.使用 `ThreadLocal` 类的 `remove()` 方法清除值：

```java
threadLocal.remove();
```

这将从当前线程的 `ThreadLocal` 实例中移除值。



4.在不再需要 `ThreadLocal` 对象时，应调用 `remove()` 方法来清理资源：

```java
threadLocal.remove();
```

## 参考

[史上最全ThreadLocal 详解（一）-CSDN博客](https://blog.csdn.net/u010445301/article/details/111322569)
