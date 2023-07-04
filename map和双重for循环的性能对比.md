map优化
双重for循环

```
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

![test1](https://github.com/DecZeroTwo/Learn-Java/assets/138491961/2ea91a25-43e1-4dc9-9383-d635c9aaa393)

map实现1

```
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

![test2](https://github.com/DecZeroTwo/Learn-Java/assets/138491961/5bb42263-8cdc-4747-92f5-5591858a7517)

map实现2

```
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

![test3](https://github.com/DecZeroTwo/Learn-Java/assets/138491961/250bd550-dd45-4e94-a33e-e2635d2ef125)

我们可以总结出将长度更长的那个List换成Map性能会更好,如果将长度短的List换成Map则for循环的遍历次数更多时间也会更长
