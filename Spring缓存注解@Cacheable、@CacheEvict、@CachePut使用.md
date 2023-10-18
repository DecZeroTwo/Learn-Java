# Spring缓存注解@Cacheable、@CacheEvict、@CachePut使用

> 从3.1开始，Spring引入了对Cache的支持。其使用方法和原理都类似于Spring对事务管理的支持。Spring
> Cache是作用在方法上的，其核心思想是这样的：当我们在调用一个缓存方法时会把该方法参数和返回结果作为一个键值对存放在缓存中，等到下次利用同样的参数来调用该方法时将不再执行该方法，而是直接从缓存中获取结果进行返回。所以在使用Spring
> Cache的时候我们要保证我们缓存的方法对于相同的方法参数要有相同的返回结果。

    使用Spring Cache需要我们做两方面的事：
    
    \- 声明某些方法使用缓存
    
    \- 配置Spring对Cache的支持
    
    和Spring对事务管理的支持一样，Spring对Cache的支持也有基于注解和基于XML配置两种方式。

## @EnableCaching

@EnableCaching是开启缓存功能，作用于缓存配置类上或者作用于springboot启动类上。

## @Cacheable

    @Cacheable可以标记在一个方法上，也可以标记在一个类上。当标记在一个方法上时表示该方法是支持缓存的，当标记在一个类上时则表示该类所有的方法都是支持缓存的。对于一个支持缓存的方法，Spring会在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法。

在`@Cacheable`注解中，有一些常用参数可以进行配置：

- `value`与`cacheNames` - 表示绑定的缓存名称。这里的缓存指的是单个的缓存存储器，并不是最终的键值对缓存对象。
- `key` - 表示缓存对象的 **key**，这个才是最终的缓存键值对的 **key**。这里的参数需要使用 **SpEL**表达式。
- `keyGenerator` - 表示用于生成此方法 **缓存key**的类。与`key`参数只能选择一个添加，否则会抛出`IllegalStateException`异常。
- `cacheManager` - 指定缓存管理器。这个后面再细说。
- `condition` - 缓存的条件。支持SpEL，当缓存条件满足时，才会进入缓存取值模式。
- `unless` - 排除的条件。支持SpEL，当排除的条件满足时，会直接调用方法取值。
- `sync` - 异步缓存模式。是否采用异步的方式，在方法取值时异步缓存。默认`false`，在缓存完成后才返回值。

```java
@Cacheable("allUser")
    @Override
    public List<User> findAll() {
        return iUserDao.findAll();
    }
```

## @CachePut

@CachePut可以声明一个方法支持缓存功能。与@Cacheable不同的是使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。@CachePut适用于更新了表中的内容，使缓存也要更新

@CachePut也可以标注在类上和方法上。使用@CachePut时我们可以指定的属性跟@Cacheable是一样的。

```java
@CachePut(cacheNames = "allUser")
@Override
public List<User> updateUsers() {
    iUserDao.deleteById(2L);
    return iUserDao.findAll();
}
```

## @CacheEvict

 @CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。

在CacheEvict注解中，多了两个参数：

- `allEntries` - 清除当前`value`下的所有缓存。

- `beforeInvocation` - 在方法**执行前**清除缓存。

  

  

 ### allEntries属性

      allEntries是[boolean类型](https://so.csdn.net/so/search?q=boolean类型&spm=1001.2101.3001.7020)，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。  

  


  ```java
  @CacheEvict(cacheNames = "allUser",allEntries = true)
  @Override
  public void deleteUser(long id) {
      iUserDao.deleteById(id);
  }
  ```

  

  ### beforeInvocation属性

      清除操作默认是在对应方法成功执行之后触发的，即方法如果因为抛出异常而未能成功返回时也不会触发清除操作。使用beforeInvocation可以改变触发清除操作的时间，当我们指定该属性值为true时，Spring会在调用该方法之前清除缓存中的指定元素。

  


```java
@CacheEvict(cacheNames = "allUser",beforeInvocation = true)
@Override
public void deleteUser(long id) {
    iUserDao.deleteById(id);
}
```