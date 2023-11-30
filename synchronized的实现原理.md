# synchronized的实现原理



## 参考文献



[面试官：请详细说下synchronized的实现原理 - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/377423211)



大佬写的太好了建议看大佬的。



## **Java内存的可见性问题**

在了解`synchronized`关键字的底层原理前，需要先简单了解下Java的内存模型，看看`synchronized`关键字是如何起作用的。

这里的本地内存并不是真实存在的，只是Java内存模型的一个抽象概念，它包含了控制器、运算器、缓存等。同时Java内存模型规定，线程对共享变量的操作必须在自己的本地内存中进行，不能直接在主内存中操作共享变量。这种内存模型会出现什么问题呢？



![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311302048038.webp)



1. 线程A获取到共享变量X的值，此时本地内存A中没有X的值，所以加载主内存中的X值并缓存到本地内存A中，线程A修改X的值为1，并将X的值刷新到主内存中，这时主内存及本地内存中的X的值都为1。
2. 线程B需要获取共享变量X的值，此时本地内存B中没有X的值，加载主内存中的X值并缓存到本地内存B中，此时X的值为1。线程B修改X的值为2，并刷新到主内存中，此时主内存及本地内存B中的X值为2，本地内存A中的X值为1。
3. 线程A再次获取共享变量X的值，此时本地内存中存在X的值，所以直接从本地内存中A获取到了X为1的值，但此时主内存中X的值为2，到此出现了所谓内存不可见的问题。

该问题Java内存模型是通过`synchronized`关键字和`volatile`关键字就可以解决，那么`synchronized`关键字是如何解决的呢，其实进入`synchronized`块就是把在`synchronized`块内使用到的变量从线程的本地内存中擦除，这样在`synchronized`块中再次使用到该变量就不能从本地内存中获取了，需要从主内存中获取，解决了内存不可见问题。



## synchronized关键字三大特性

- 原子性：一个或多个操作要么全部执行成功，要么全部执行失败。`synchronized`关键字可以保证只有一个线程拿到锁，访问共享资源。
- 可见性：`synchronized`在修改了本地内存中的变量后，解锁前会将本地内存修改的内容刷新到主内存中，确保了共享变量的值是最新的，也就保证了可见性。
- 有序性：加了synchronized后程序仍会发生重排，但是因为一次只有一个线程来获取和修改变量所以重排并不影响别的线程，从而保证了有序性

## **synchronized关键字可以实现什么类型的锁？**

- 悲观锁：`synchronized`关键字实现的是悲观锁，每次访问共享资源时都会上锁。
- 非公平锁：`synchronized`关键字实现的是非公平锁，即线程获取锁的顺序并不一定是按照线程阻塞的顺序。
- 可重入锁：`synchronized`关键字实现的是可重入锁，即已经获取锁的线程可以再次获取锁。
- 独占锁或者排他锁：`synchronized`关键字实现的是独占锁，即该锁只能被一个线程所持有，其他线程均被阻塞。

## **synchronized关键字的使用方式**

在Java中，"synchronized"主要有三种使用方式：

1. **同步方法：**

   在方法声明中使用 "synchronized" 关键字，确保在调用该方法时，只有一个线程可以执行该方法。其他线程必须等待当前线程执行完毕才能访问该方法。

   ```java
   class MySync implements Runnable {
   
       private static int i = 0;   //共享资源
   
       private synchronized void add() {
           i++;
       }
   
       @Override
       public void run() {
           for (int j = 0; j < 10000; j++) {
               add();
           }
       }
   
       public static void main(String[] args) throws Exception {
   
           MySync MySync = new MySync();
           Thread t1 = new Thread(MySync);
           Thread t2 = new Thread(MySync);
   
           t1.start();
           t2.start();
   
           t1.join();
           t2.join();
   
           System.out.println(i);
       }
   }
   ```

   这段代码的结果很容易得到

   >  20000

   再试试这段代码

   ```java
   class MySync implements Runnable {
   
       private static int i = 0;   //共享资源
   
       private synchronized void add() {
           i++;
       }
   
       @Override
       public void run() {
           for (int j = 0; j < 10000; j++) {
               add();
           }
       }
   
       public static void main(String[] args) throws Exception {
           Thread t1 = new Thread(new MySync());
           Thread t2 = new Thread(new MySync());
   
           t1.start();
           t2.start();
   
           t1.join();
           t2.join();
   
           System.out.println(i);
       }
   }
   ```

   结果

   > 18659

   第二个示例中的`add()`方法虽然也使用`synchronized`关键字修饰了，但是因为两次`new MySync()`操作建立的是两个不同的对象，也就是说存在两个不同的对象锁，线程t1和t2使用的是不同的对象锁，所以不能保证线程安全。如果synchronized关键字作用于类对象，即用`synchronized`修饰静态方法，问题则迎刃而解。

2. **静态同步方法：**

   类级别的同步，确保在同一时刻只有一个线程可以访问该类的静态同步方法。使用 `static synchronized` 关键字来实现。

   ```java
   class MySync2 implements Runnable {
   
        private static int i = 0;   //共享资源
   
        private static synchronized void add() {
            i++;
        }
   
        @Override
        public void run() {
            for (int j = 0; j < 10000; j++) {
                add();
            }
        }
   
        public static void main(String[] args) throws Exception {
            Thread t1 = new Thread(new MySync2());
            Thread t2 = new Thread(new MySync2());
   
            t1.start();
            t2.start();
   
            t1.join();
            t2.join();
   
            System.out.println(i);
        }
    }
   ```

   在这个例子中，`lockObject` 是一个共享的对象，用于在多个线程之间建立同步。

3. **同步块：**

   使用同步块可以在方法内的特定代码块上应用同步。这对于只需要在某个代码片段上进行同步的情况很有用，而不是整个方法。

   ```java
   class MySync3 implements Runnable {
   
        static int i = 0;   //共享资源
   
        @Override
        public void run() {
            synchronized (this){       //this表示当前对象实例，这里还可以使用syncTest.class，表示class对象锁
                for (int j = 0; j < 10000; j++) {
                    i++;
                }
            }
   
        }
   
        public static void main(String[] args) throws Exception {
   
            MySync3 mySync = new MySync3();
   
            Thread t1 = new Thread(mySync);
            Thread t2 = new Thread(mySync);
   
            t1.start();
            t2.start();
   
            t1.join();
            t2.join();
   
            System.out.println(i);
        }
    }
   ```



## synchronized关键字的底层原理



在jdk1.6之前，`synchronized`被称为重量级锁，在jdk1.6中，为了减少获得锁和释放锁带来的性能开销，引入了偏向锁和轻量级锁。

### **对象头**

在HotSpot虚拟机中，Java对象在内存中的布局大致可以分为三部分：**对象头**、**实例数据**和**填充对齐**。因为`synchronized`用的锁是存在对象头里的，这里我们需要重点了解对象头。如果对象头是数组类型，则对象头由**Mark Word**、**Class MetadataAddress**和**Array length**组成，如果对象头非数组类型，对象头则由**Mark Word**和**Class MetadataAddress**组成。在32位虚拟机中，数组类型的Java对象头的组成如下表：

| 内容                  | 说明                                   | 长度  |
| --------------------- | -------------------------------------- | ----- |
| Mark Word             | 存储对象的hashCode、分代年龄和锁标记位 | 32bit |
| Class MetadataAddress | 存储到对象类型数据的指针               | 32bit |
| Array length          | 数组的长度                             | 32bit |

### **Mark Word**

在运行期间，Mark Word中存储的数据会随着锁标志位的变化而变化，在32位虚拟机中，不同状态下的组成如下：

![img](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311302152016.png)

其中线程ID表示持有偏向锁线程的ID，Epoch表示偏向锁的时间戳，偏向锁和轻量级锁是在jdk1.6中引入的。



### **重量级锁的底部实现原理：Monitor**

在jdk1.6之前，`synchronized`只能实现重量级锁，Java虚拟机是基于Monitor对象来实现重量级锁的，所以首先来了解下Monitor，在Hotspot虚拟机中，Monitor是由ObjectMonitor实现的，

简单介绍下其数据结构(不全)

```cpp
ObjectMonitor() {
    _header       = NULL;
    _count        = 0; //锁的计数器，获取锁时count数值加1，释放锁时count值减1，直到
    _waiters      = 0, //等待线程数
    _recursions   = 0; //锁的重入次数

    _owner        = NULL; //指向持有ObjectMonitor对象的线程地址
    _WaitSet      = NULL; //处于wait状态的线程，会被加入到_WaitSet

    _cxq          = NULL ; //阻塞在EntryList上的单向线程列表

    _EntryList    = NULL ; //处于等待锁block状态的线程，会被加入到该列表

  }
```



其中 _owner、_WaitSet和_EntryList 字段比较重要，它们之间的转换关系如下图

![image-20231130213008487](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311302130744.png)

从上图可以总结获取Monitor和释放Monitor的流程如下：

1. 当多个线程同时访问同步代码块时，首先会进入到EntryList中，然后通过CAS的方式尝试将Monitor中的owner字段设置为当前线程，同时count加1，若发现之前的owner的值就是指向当前线程的，recursions也需要加1。如果CAS尝试获取锁失败，则进入到EntryList中。
2. 当获取锁的线程调用`wait()`方法，则会将owner设置为null，同时count减1，recursions减1，当前线程加入到WaitSet中，等待被唤醒。
3. 当前线程执行完同步代码块时，则会释放锁，count减1，recursions减1。当recursions的值为0时，说明线程已经释放了锁。



### **synchronized作用于同步代码块的实现原理**

前面已经了解Monitor的实现细节，而Java虚拟机则是通过进入和退出Monitor对象来实现方法同步和代码块同步的。

```java
monitorenter     //进入同步代码块的指令
{
    //同步代码块
}
monitorexit     //结束同步代码块的指令
```

同步代码块的实现是由`monitorenter` 和`monitorexit`指令完成的，其中`monitorenter`指令所在的位置是同步代码块开始的位置，第一个`monitorexit`指令是用于正常结束同步代码块的指令，第二个`monitorexit`指令是用于异常结束时所执行的释放Monitor指令。



### **synchronized作用于同步方法原理**

同步方法是通过`Access flags`后面的标识来确定该方法是否为同步方法。`Access flags`后边的synchronized标识，该标识表明了该方法是一个同步方法。Java虚拟机通过该标识可以来辨别一个方法是否为同步方法，如果有该标识，线程将持有Monitor，在执行方法，最后释放Monitor。





## **jDK1.6对synchronized做了哪些优化？**

### **锁的升级**

在JDK1.6中，为了减少获得锁和释放锁带来的性能消耗，引入了偏向锁和轻量级锁，锁的状态变成了四种，无锁，偏向锁，轻量级锁，重量级锁。锁的状态会随着竞争激烈逐渐升级，但通常情况下，锁的状态只能升级不能降级。这种只能升级不能降级的策略是为了提高获得锁和释放锁的效率。



### **偏向锁**

偏向锁（Biased Locking）是为了优化无竞争情况下的锁性能而引入的一种锁机制。偏向锁假定在对象的竞争情况下，总是由同一线程多次获得锁。

偏向锁的获取流程：

1. **初始状态：**

   刚创建的对象处于无锁状态，没有任何线程持有该对象的锁。

2. **偏向锁标记：**

   当一个线程第一次访问一个对象并获取锁时，JVM会将对象的Mark Word 设置为偏向锁标记，同时记录持有锁的线程ID。这表示该对象偏向于第一次获得它的线程。

3. **再次获取锁：**

   当同一个线程再次尝试获取该对象的锁时，JVM会检查对象的Mark Word，发现是偏向锁，并且线程ID与当前线程ID一致。此时，无需进行任何同步操作，直接认为当前线程已经持有了该对象的锁，可以继续执行。

4. **获取失败，撤销偏向锁：**

   如果有其他线程尝试获取同一个对象的锁，偏向锁就会失效，JVM会撤销偏向锁，将对象的Mark Word 恢复为无锁状态。这时，转而使用轻量级锁或重量级锁来确保多线程间的同步访问。



### **轻量级锁**

轻量级锁（Lightweight Lock）是Java中用于优化多线程同步的一种机制。轻量级锁的目标是在无竞争的情况下，减少传统的重量级锁的性能开销。

轻量级锁的获取流程：

- **Mark Word 检查：**

  每个对象的头部都包含一个Mark Word，用于存储对象的状态信息。当线程尝试获取轻量级锁时，首先检查对象的Mark Word。如果对象的Mark Word 表示没有被锁定，那么当前线程尝试使用CAS（Compare And Swap）操作来将对象的Mark Word 设置为锁定状态。

- **CAS 尝试：**

  如果对象的Mark Word 表示没有被锁定，当前线程会尝试使用CAS原子操作，将对象的Mark Word 替换为指向当前线程的锁记录（Lock Record）。这个锁记录包含了指向对象Monitor的指针。

- **成功获取锁：**

  如果CAS操作成功，表示当前线程成功获取了轻量级锁，可以继续执行同步块中的代码。在这个阶段，轻量级锁的状态仍然处于无锁状态，只是Mark Word 中包含了指向锁记录的指针。

- **获取失败，膨胀为重量级锁：**

  如果CAS操作失败，表示有其他线程持有了锁，那么当前线程就需要尝试其他手段。此时，轻量级锁会膨胀为重量级锁，这时候JVM会尝试使用互斥量来确保数据的同步访问。膨胀为重量级锁的过程通常涉及到线程的阻塞和唤醒，增加了同步的开销。

### 重量级锁

重量级锁是Java中用于多线程同步的一种较为激进的锁机制，用于解决多个线程之间的竞争问题。

重量级锁的获取流程：

- **初始状态：**

  刚创建的对象处于无锁状态，没有任何线程持有该对象的锁。

- **锁竞争：**

  当一个线程尝试获取一个对象的锁时，发现该对象的锁已经被其他线程持有。此时，发生锁竞争。

- **阻塞等待：**

  当锁竞争发生时，请求锁的线程会被阻塞，进入等待状态。这时，JVM会将该线程放到一个等待队列中。

- **争夺锁：**

  当持有锁的线程释放锁时，JVM会从等待队列中选择一个线程唤醒。这个被唤醒的线程会尝试再次获取锁。

- **竞争成功：**

  如果竞争锁的线程在被唤醒后成功获取了锁，那么它就可以继续执行同步块中的代码。此时，其他线程仍然处于阻塞状态。

- **竞争失败：**

  如果竞争锁的线程再次失败，可能因为其他线程抢先一步获取了锁，那么线程将重新被阻塞，并再次进入等待队列。
