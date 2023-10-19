# redis中的五个常用类

## 什么是Redis

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。



![redis多数据库使用防止key冲突 - 运维·速度 | 运维·速度](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310192058704.png)

## 字符串(string)

### 用途

> 存储字符串类型数据，包括文本、数字等。

### 常用命令

| 命令                                        | 描述                                | 案例                    |
| ------------------------------------------- | ----------------------------------- | ----------------------- |
| set key value                               | 设置指定 key 的值                   | set mykey hello         |
| get key                                     | 返回指定 key 的值。                 | get mykey               |
| mset key1 value1 key2 value2 .. keyN valueN | 同时设置一个或多个 key-value 对。   | mset key1 java key2 c++ |
| mget key1 key2 … keyn                       | 返回所有(一个或多个)给定 key 的值。 | mget key1 key2          |
| **setnx key value**                         | 只有在 key 不存在时设置 key 的值。  | setnx lock mylock       |
| strlen key                                  | 返回 key 所储存的字符串值的长度。   | strlen mykey            |
| incr key                                    | 将 key 中储存的数字值增一。         | incr money              |
| decr key                                    | 将 key 中储存的数字值减一。         | decr money              |



![image-20231019192223775](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310191922841.png)

> setnx在key不存在时返回1，key存在时返回0；

## 列表(list)

### 用途

> 存储一组有序的字符串类型数据，可以用于实现消息队列、任务列表等场景。

### 常用命令

| 命令                     | 描述                                                         | 案例                                    |
| ------------------------ | ------------------------------------------------------------ | --------------------------------------- |
| llen key                 | 返回列表长度                                                 | llen language                           |
| lpush key value1… value2 | 将一个或多个值插入到列表头部(最左边)。 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。 | lpush language java c c++ python golang |
| lpop key                 | 移出并返回列表的第一个元素                                   | lpop language                           |
| lrange key start end     | 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。 其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 | lrange language 0 -1                    |
| lrem key count value     | 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。     | lrem mylist -2 hello                    |
| rpush key value1… value2 | 用于将一个或多个值插入到列表的尾部(最右边)。                 | rpush language rust                     |
| rpop key count           | 尾部移除count个元素                                          | rpop language 2                         |



![image-20231019194619469](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310191946528.png)



![image-20231019194756017](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310191947072.png)



![image-20231019195000041](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310191950066.png)

![image-20231019195130370](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310191951402.png)



## 集合(set)

### 用途

> Redis的Set是string类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。

### 常用命令

| 命令                      | 描述                                                         | 案例                       |
| ------------------------- | ------------------------------------------------------------ | -------------------------- |
| sadd key member1… member2 | 将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。 | sadd myset hello foo hello |
| scard key                 | 返回集合中元素的数量。                                       | scard myset                |
| SMEMBERS key              | 返回集合中的所有的成员。                                     | smembers myset             |
| sismember key member      | 判断member元素是否是集合key的成员                            | sismember myset hello      |
| spop key                  | 移除并返回集合中的一个随机元素                               | spop myset                 |

![image-20231019200048591](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310192000628.png)



## 有序集合(sorted set)

### 用途

> 存储一组有序的字符串类型数据，每个元素都有一个对应的分数，可用于排行榜等场景。

### 常用命令

| 命令                                          | 描述                                                         | 案例                          |
| --------------------------------------------- | ------------------------------------------------------------ | ----------------------------- |
| zadd key score1 member1… score2 member2       | 向有序集合添加一个或多个成员，或者更新已存在成员的分数       | zadd stus 3 zs 4 ls 5 ww 6 zl |
| zcard key                                     | 获取集合的成员数                                             | zcard  stus                   |
| zcount key min max                            | 计算在有序集合中指定区间分数的成员数                         | zcount stus 3 4               |
| ZRANGEBYSCORE key min max[limit offset count] | 返回指定成员区间内的成员，按成员字典正序排序, 分数必须相同。 | ZRANGEBYLEX myzset [aaa (g    |
| zrem key member1 member2                      | 移除有序集合中的一个或多个成员                               | zrem stus zs                  |
| ZREMRANGEBYSCORE key min max                  | 移除有序集合中给定的分数区间的所有成员                       | zremrangebyscore stus 5 6     |
| zscore key member                             | 返回有序集中，成员的分数值                                   | zscore stus ls                |



![image-20231019202736115](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310192027159.png)



![image-20231019202854692](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310192028732.png)



## 哈希(hash)

### 用途

> 存储一些具有对应关系的数据

### 常用命令

| 命令                           | 描述                                                | 案例                       |
| ------------------------------ | --------------------------------------------------- | -------------------------- |
| hdel key field1 field2… fieldn | 删除哈希表 key 中的一个或多个指定字段               | hdel user age              |
| hexists key field              | 查看哈希表 key 中，指定的字段是否存在               | hexists user age           |
| hget key field                 | 获取存储在哈希表中指定字段的值                      | hget user name             |
| hgetall key                    | 获取在哈希表中指定 key 的所有字段和值               | hgetall user               |
| hincrby key field increment    | 为哈希表 key 中的指定字段的整数值加上增量 increment | hincrby user age 1         |
| hkeys key                      | 获取所有哈希表中的字段(field)                       | hkeys user                 |
| hlen key                       | 获取哈希表中字段的数量                              | hlen user                  |
| hmget key field1 field2        | 获取所有给定字段的值                                | hmget user name            |
| hset key field value           | 将哈希表 key 中的字段 field 的值设为 value 。       | hset user name lucy age 18 |
| hsetnx key field value         | 只有在字段 field 不存在时，设置哈希表字段的值。     | hsetnx user name jack      |
| hvals key                      | 获取哈希表中所有值                                  | hvals user                 |



![image-20231019204626919](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310192046963.png)