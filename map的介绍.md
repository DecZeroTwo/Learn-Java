# map的介绍

## map和list查找的区别


arr={10, 11, 22, 13, 4, 5, 16, 17, 48, 69}

list的查找需要遍历整个list，时间复杂度是:O(N)

> list.get(0) == 17?  --->  list.get(1) == 17?...

## Map的查找方式

> 17%10=7---> arr[7]

查找的时间复杂度是: O(1)

#### map的两种常用操作


map取和放时使用hash算法来获得地址


下面为模拟

> get(17) --> {return arr[17 % 10];}
>
> put(17)--> {arr[17 %10]= 17;}

put的时间复杂度是: O(1)


如果要继续put，数字27 --> arr[27 % 10]=27;

链表27--->17---> 37

#### map的碰撞

当HashMap的HashCode相同时会发生碰撞，此时会把相同的数据链接成一个链表，查找的时间复杂度为O(n)。


为了提高map的性能，降低查询的时间复杂度，源码中给出了解决方案：树化。

#### map退化

当链表长度大于8时map会退化成链表，此时map会用红黑树来减少查询的时间复杂度。

![250949060-49d26361-45be-4b1a-b4c5-8cf1e7a0ba50](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310091703543.png)
