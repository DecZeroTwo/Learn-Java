# RBAC-基于角色权限的模型

## 权限系统与RBAC模型概述

![Role Based Access Control – RBAC - ARKIT](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211825222.jpeg)

RBAC（Role-Based Access Control ）基于角色的访问控制。

在20世纪90年代期间，大量的专家学者和专门研究单位对RBAC的概念进行了深入研究，先后提出了许多类型的RBAC模型，其中以美国George Mason大学信息安全技术实验室（LIST）提出的RBAC96模型最具有系统性，得到普遍的公认。

RBAC认为权限的过程可以抽象概括为：判断【Who是否可以对What进行How的访问操作（Operator）】这个逻辑表达式的值是否为True的求解过程。

即将权限问题转换为Who、What、How的问题。who、what、how构成了访问权限三元组。

> RBAC支持公认的安全原则：最小特权原则、责任分离原则和数据抽象原则。
>
> - 最小特权原则得到支持，是因为在RBAC模型中可以通过限制分配给角色权限的多少和大小来实现，分配给与某用户对应的角色的权限只要不超过该用户完成其任务的需要就可以了。
> - 责任分离原则的实现，是因为在RBAC模型中可以通过在完成敏感任务过程中分配两个责任上互相约束的两个角色来实现，例如在清查账目时，只需要设置财务管理员和会计两个角色参加就可以了。
> - 数据抽象是借助于抽象许可权这样的概念实现的，如在账目管理活动中，可以使用信用、借方等抽象许可权，而不是使用操作系统提供的读、写、执行等具体的许可权。但RBAC并不强迫实现这些原则，安全管理员可以允许配置RBAC模型使它不支持这些原则。因此，RBAC支持数据抽象的程度与RBAC模型的实现细节有关。

## 案例

### 需求分析

以一个虚拟的互联网公司为背景举例，我们要为该公司建设一个管理系统，这里就要理清相关人员的各种关系。

公司的组织关系(用户、角色、用户角色关系)如下

一把手是项目经理张三，下设两个部门：开发部和人事部。

开发部由高级程序员李四，还有两个初级程序员王五、赵六组成。

人事部为孙七。

此外，李四还是项目经理的心腹下属。
公司的日常工作有这几项(权限)：

接活；

开会，分析项目；

修改数据库；

访问数据库；

招聘员工

很显然，上边这些任务不是人人能做(角色权限关系)：

接活由项目经理负责

数据库很重要只能经理和经理心腹能修改，开会分析项目也是这两个角色；

程序员都能访问数据库；

员工的招聘和开除由人事负责；

因此，以上关系可梳理为下图所示

![image-20231021172142578](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211721656.png)

### 数据库设计

![image-20231021175441809](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211754868.png)

#### 用户表(user)

| id   | name |
| ---- | ---- |
| 1    | 张三 |
| 2    | 李四 |
| 3    | 王五 |
| 4    | 赵六 |
| 5    | 孙七 |



![image-20231021172616283](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211726314.png)



#### 角色表(role)

| id   | name       |
| ---- | ---------- |
| 1    | 项目经理   |
| 2    | 高级程序员 |
| 3    | 初级程序员 |
| 4    | 人事       |
| 5    | 经理心腹   |



![image-20231021173405342](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211734384.png)



#### 权限表(perm)

| id   | name       |
| ---- | ---------- |
| 1    | 接活       |
| 2    | 开会       |
| 3    | 修改数据库 |
| 4    | 访问数据库 |
| 5    | 招聘，开除 |



![image-20231021173433623](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211734657.png)



#### 用户-角色表(rel_user_role)



| id   | uid  | rid  | 解释             |
| ---- | ---- | ---- | ---------------- |
| 1    | 1    | 1    | 张三—>项目经理   |
| 2    | 2    | 2    | 李四—>高级程序员 |
| 3    | 2    | 5    | 李四—>经理心腹   |
| 4    | 3    | 3    | 王五—>初级程序员 |
| 5    | 4    | 3    | 赵六—>初级程序员 |
| 6    | 5    | 4    | 孙七—>人事       |



![image-20231021173922651](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211739693.png)



#### 角色-权限表(rel_role_perm)



| id   | rid  | pid  | 解释                   |
| ---- | ---- | ---- | ---------------------- |
| 1    | 1    | 1    | 项目经理—>接活         |
| 2    | 1    | 2    | 项目经理—>开会         |
| 3    | 1    | 3    | 项目经理—>修改数据库   |
| 4    | 2    | 3    | 高级程序员—>修改数据库 |
| 5    | 2    | 4    | 高级程序员—>访问数据库 |
| 6    | 3    | 4    | 初级程序员—>访问数据库 |
| 7    | 4    | 5    | 人事—>招聘和开除员工   |
| 8    | 5    | 2    | 经理心腹—>开会         |



![image-20231021174547347](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211745385.png)



### 用户分组

用户数量大且可分组授权时，添加 3 个表。

- 用户组
- 用户-用户组
- 用户组-权限



![image-20231021181350407](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211813470.png)

王五和赵六同属开发部的初级程序员，他们具有相同权限，因此将上面数据库设计的表结构更为

#### 用户组表(group)

| id   | name         |
| ---- | ------------ |
| 1    | 开发部初级组 |



![image-20231021182129715](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211821758.png)



#### 用户——组表(rel_user_group)

| id   | uid  | gid  | *解释                |
| ---- | ---- | ---- | -------------------- |
| 1    | 3    | 1    | 王五 ->开发部初级组  |
| 2    | 4    | 1    | 赵六 -> 开发部初级组 |



![image-20231021182232078](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211822115.png)



#### 组-角色表(rel_group_role)

| id   | gid  | rid  | *解释                     |
| ---- | ---- | ---- | ------------------------- |
| 1    | 1    | 3    | 开发部初级组-> 初级程序员 |



![image-20231021182322747](https://raw.githubusercontent.com/DecZeroTwo/blogimage/main/images/202310211823793.png)

