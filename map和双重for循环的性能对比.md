# map优化

## 双重for循环

```java
public static void test1() {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            list1.add(i);
        }
        for (int i = 9999; i < 209999; i++) {
            list2.add(i);
        }
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).equals(list2.get(j))){
                    System.out.println(list1.get(i));
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
```



![251014401-47a9eabe-5c7d-476d-b65a-7896cb3321c5](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310091653251.png)



## map实现1

```java
public static void test2() {
        List<Integer> list = new ArrayList<>();
        Map<Integer,Integer> map = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        for (int i = 9999; i < 209999; i++) {
            map.put(i,1);
        }
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i))!= null){
                System.out.println(list.get(i));
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
```



![251014596-66d2f5d2-a348-4740-9d4a-2289e4c4ccd1](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310091654070.png)



## map实现2

```java
public static void test3() {
        Map<Integer,Integer> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            map.put(i,1);
        }
        for (int i = 9999; i < 209999; i++) {
            list.add(i);
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            if (map.get(list.get(i))!= null){
                System.out.println(list.get(i));
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
```



![251014629-340c071c-e301-40c7-9101-c9314332a02e](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310091654635.png)



## 总结

test3()循环了两万次,test2()循环了一万次

test2()运行时间小于test3()

我们可以总结出将长度更长的那个List换成Map性能会更好,如果将长度短的List换成Map则for循环的遍历次数更多时间也会更长
