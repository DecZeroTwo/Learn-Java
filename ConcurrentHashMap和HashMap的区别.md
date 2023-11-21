# ConcurrentHashMap和HashMap的区别

**Doug Lea**

![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311212114524.jpeg)

java.util.concurrent包就是出自这位大师之手

## ConcurrentHashMap介绍

> 引入ConcurrentHashMap是为了同步集合Hashtable之间有更好的选择，Hashtable与HashMap,ConcurrentHashMap主要区别于HashMap不是同步的，线程不安全的和不适合应用于多线程并发环境下，而ConcurrentHashMap是线程安全的集合容器，特别是在多线程和并发环境中，通常作为Map的主要实现。
>
> **ConcurrentHashMap避免了对全局加锁改成了局部加锁操作**，这样就极大地提高了并发环境下的操作速度，由于ConcurrentHashMap在JDK1.7和1.8中的实现非常不同，接下来我们谈谈JDK在1.7和1.8中的区别。



## JDK1.7版本的CurrentHashMap的实现原理

在JDK1.7中ConcurrentHashMap采用了**数组+Segment+分段锁**的方式实现。

### 1.Segment(分段锁)

ConcurrentHashMap中的**分段锁称为Segment**，它即类似于HashMap的结构，即内部拥有一个Entry数组，数组中的每个元素又是一个链表,同时又是一个ReentrantLock（Segment继承了ReentrantLock）。

### 2.内部结构

ConcurrentHashMap使用分段锁技术，将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问，能够实现真正的并发访问。如下图是ConcurrentHashMap的内部结构图：



![分类/线程安全/ConcurrentHashMap1.7底层结构.png](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311212059609.png)



从上面的结构我们可以了解到，ConcurrentHashMap定位一个元素的过程需要进行两次Hash操作。

**第一次Hash定位到Segment，第二次Hash定位到元素所在的链表的头部。**



![java7_concurrenthashmap](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311212038318.png)



## JDK1.8版本的CurrentHashMap的实现原理



JDK8中ConcurrentHashMap参考了JDK8 HashMap的实现，采用了**数组+链表+红黑树**的实现方式来设计，内部大量采用CAS操作

CAS是compare and swap的缩写，即我们所说的比较交换。cas是一种基于锁的操作，而且是乐观锁。

**JDK8中彻底放弃了Segment转而采用的是Node，其设计思想也不再是JDK1.7中的分段锁思想。**

**Node：保存key，value及key的hash值的数据结构。其中value和next都用volatile修饰，保证并发的可见性。**

![java8_concurrenthashmap](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311212039275.png)



## **总结**

其实可以看出JDK1.8版本的ConcurrentHashMap的数据结构已经接近HashMap，相对而言，ConcurrentHashMap只是增加了同步的操作来控制并发，从JDK1.7版本的ReentrantLock+Segment+HashEntry，到JDK1.8版本中synchronized+CAS+HashEntry+红黑树。

**1.数据结构：**取消了Segment分段锁的数据结构，取而代之的是数组+链表+红黑树的结构。

**2.保证线程安全机制：**JDK1.7采用segment的分段锁机制实现线程安全，其中segment继承自ReentrantLock。JDK1.8采用CAS+Synchronized保证线程安全。

**3.锁的粒度：**原来是对需要进行数据操作的Segment加锁，现调整为对每个数组元素加锁（Node）。

**4.链表转化为红黑树:**定位结点的hash算法简化会带来弊端,Hash冲突加剧,因此在链表节点数量大于8时，会将链表转化为红黑树进行存储。

**5.查询时间复杂度：**从原来的遍历链表O(n)，变成遍历红黑树O(logN)。



## 参考：

[ConcurrentHashMap的实现原理(JDK1.7和JDK1.8) - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/94874100)



[HashMap与ConcurrentHashMap工作原理、区别和总结-CSDN博客](https://blog.csdn.net/chenwendangding/article/details/99065623)

