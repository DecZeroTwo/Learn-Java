# 设计模式—build模式

## 设计模式的分类

总体来说设计模式分为三大类：

创建型模式，共五种：工厂方法模式、抽象工厂模式、单例模式、建造者模式、原型模式。

结构型模式，共七种：适配器模式、装饰器模式、代理模式、外观模式、桥接模式、组合模式、享元模式。

行为型模式，共十一种：策略模式、模板方法模式、观察者模式、迭代子模式、责任链模式、命令模式、备忘录模式、状态模式、访问者模式、中介者模式、解释器模式。

每种设计模式都有其特定的应用场景和解决方案，开发人员可以根据实际需求选择合适的设计模式来解决问题，从而提高代码的质量和可维护性。



## 建造者模式

### 建造者模式的由来

建造者模式是构造方法的一种替代方案，为什么需要建造者模式，我们可以想，假设有一个对象里面有20个属性：

- 属性1
- 属性2
- ...
- 属性20

对开发者来说这不是疯了，也就是说我要去使用这个对象，我得去了解每个属性的含义，然后在构造函数或者Setter中一个一个去指定。更加复杂的场景是，这些属性之间是有关联的，比如属性1=A，那么属性2只能等于B/C/D，这样对于开发者来说更是增加了学习成本，开源产品这样的一个对象相信不会有太多开发者去使用。

为了解决以上的痛点，建造者模式应运而生，对象中属性多，但是通常重要的只有几个，因此建造者模式会**让开发者指定一些比较重要的属性**或者让开发者**指定某几个对象类型**，然后让建造者去实现复杂的构建对象的过程，这就是对象的属性与创建分离。这样对于开发者而言隐藏了复杂的对象构建细节，降低了学习成本，同时提升了代码的可复用性。

### 通过简单的代码加深体会



```java
// 电脑类
public class Computer {
    private String cpu;
    private String memory;
    private String motherboard;
    private String graphicsCard;
    private String hardDisk;

    public Computer(String cpu, String memory, String motherboard, String graphicsCard, String hardDisk) {
        this.cpu = cpu;
        this.memory = memory;
        this.motherboard = motherboard;
        this.graphicsCard = graphicsCard;
        this.hardDisk = hardDisk;
    }

    @Override
    public String toString() {
        return "Computer{" +
                "cpu='" + cpu + '\'' +
                ", memory='" + memory + '\'' +
                ", motherboard='" + motherboard + '\'' +
                ", graphicsCard='" + graphicsCard + '\'' +
                ", hardDisk='" + hardDisk + '\'' +
                '}';
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(String hardDisk) {
        this.hardDisk = hardDisk;
    }
}
```



```java
// 抽象类（或接口） 建造者，规定创建Product实例对象的流程方法
public interface ComputerBuilder {
    void buildCPU();
    void buildMemory();
    void buildMotherboard();
    void buildGraphicsCard();
    void buildHardDisk();
    Computer getComputer();
}
```



```java
// 对抽象建造者的继承实现，对于一种Computer构建的过程
public class ConcreteComputerBuilder implements ComputerBuilder {
    private Computer computer;

    public ConcreteComputerBuilder() {
        this.computer = new Computer("", "", "", "", "");
    }

    @Override
    public void buildCPU() {
        computer.setCpu("Intel Core i7"); // 假设默认CPU为Intel Core i7
    }

    @Override
    public void buildMemory() {
        computer.setMemory("16GB DDR4"); // 假设默认内存为16GB DDR4
    }

    @Override
    public void buildMotherboard() {
        computer.setMotherboard("ASUS ROG Strix Z590"); // 假设默认主板为ASUS ROG Strix Z590
    }

    @Override
    public void buildGraphicsCard() {
        computer.setGraphicsCard("NVIDIA GeForce RTX 3080"); // 假设默认显卡为NVIDIA GeForce RTX 3080
    }

    @Override
    public void buildHardDisk() {
        computer.setHardDisk("1TB NVMe SSD"); // 假设默认硬盘为1TB NVMe SSD
    }

    @Override
    public Computer getComputer() {
        return computer;
    }
}
```





```java
//指挥者，指挥创建者Builder以什么样的顺序生产Computer
public class ComputerDirector {
    public Computer buildComputer(ComputerBuilder builder) {
        builder.buildCPU();
        builder.buildMemory();
        builder.buildMotherboard();
        builder.buildGraphicsCard();
        builder.buildHardDisk();
        return builder.getComputer();
    }
}
```



```java
//测试客户类
public class Client {
    public static void main(String[] args) {
        ComputerBuilder builder = new ConcreteComputerBuilder();
        ComputerDirector director = new ComputerDirector();
        Computer computer = director.buildComputer(builder);

        System.out.println(computer);
    }
}
```





### 建造者中的角色

![image-20231123203433550](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202311232115582.png)



Computer :我们具体需要生成的类对象

Builder(抽象建造者类)：为我们需要生成的类对象，构建不同的模块属性，即：公开构建产品类的属性，隐藏产品类的其他功能

ConcreteBuilder(具体建造者类)：实现我们要生成的类对象

Director(导演类)：确定构建我们的类对象具体有哪些模块属性，在实际应用中可以不需要这个角色，直接通过client处理



用户需要产品时，调用Director，由其调用具体的建造者类创建需要的产品。



模式特点：
1.客户端不必知道产品内部组成的细节，将产品本身与产品创建过程解耦，使得相同的创建过程可以创建不同的产品对象
2.每个具体建造者都相对独立，而与其他的具体建造者无关，因此可以很方便地替换具体建造者或增加新的具体建造者，用户使用不同的具体建造者即可得到不同的产品对象
3.可以更加精细地控制产品的创建过程。将复杂产品的创建步骤分解在不同的方法中，使得创建过程更加清晰，也更方便使用程序来控制创建过程
4.增加新的具体建造者无需修改原有类库的代码，指挥者类针对抽象建造者类编程，系统扩展方便，符合开闭原则



## 建造者模式与工厂模式的区别



### 建造者模式（Builder Pattern）：

1. **目的**：
   - 建造者模式的主要目的是将一个复杂对象的构建与其表示分离，使得同样的构建过程可以创建不同的表示。
   - 适用于需要构建复杂对象，对象的构建步骤多且可定制。
2. **结构**：
   - 包含产品类（被构建的复杂对象）、抽象建造者（定义构建过程的接口）、具体建造者（实现构建过程的具体步骤）、导演类（指导建造过程）。
3. **使用场景**：
   - 当对象的构建步骤多且需要灵活配置时，可以使用建造者模式。
   - 适用于构建复杂的对象，对象的内部结构较为复杂或构建过程涉及多个步骤。

### 工厂模式（Factory Pattern）：

1. **目的**：
   - 工厂模式的主要目的是定义一个用于创建对象的接口，但将实例化的步骤延迟到子类中。
   - 适用于一个类无法预先知道需要创建哪个类的实例，将实例化的责任委托给子类来决定。
2. **结构**：
   - 包含工厂接口（定义创建对象的方法）、具体工厂（实现工厂接口来创建具体对象）、产品类（由工厂创建的对象）。
3. **使用场景**：
   - 当一个类无法预先知道需要创建哪个类的实例时，可以使用工厂模式。
   - 适用于对象的创建不依赖于类的具体实现，而是依赖于接口。

### 总结区别：

- **建造者模式**强调的是将一个复杂对象的构建过程分步骤进行，使得构建的过程灵活，可以根据需要定制不同的表示。建造者模式通常涉及一个导演类来指导构建的过程。
- **工厂模式**强调的是定义一个接口用于创建对象，但将实例化的步骤延迟到子类中。工厂模式适用于无法预先知道具体类的情况，通过子类来实现具体对象的创建。