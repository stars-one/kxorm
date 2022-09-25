# Kxorm

<meta name="referrer" content="no-referrer">

Kotlin编写的ORM框架,侧重自动创表(实体类自动创表)功能

<img src="https://jitpack.io/v/stars-one/kxorm.svg" />

> 目前处于测试阶段,具体结构还在思索,所以下述的相关API在之后可能会有所变动!!

**补充:**
> github工作流只是用来自动发布release,在推送了tag到github后,jitpack会自动进行版本的发布操作!
## 起因
原本也不想重复造轮子,但发现目前现有的ORM框架,只有JPA这个框架支持自动创表,且需要在Spring
框架中才能使用,实在有些不方便,于是便是整个单独的简单的ORM框架出来

这里提及的自动创表功能,解释一下:
> 直接写对应的的实体类,之后由ORM自动创建对应的表,而不是由表再生成实体类

**为什么需要这个功能?**

主要是之前使用了Android的一个框架,[Litepal](https://github.com/guolindev/LitePal),使用可以不用关心表的创建,比较方便

在使用TornadoFx(JavaFx)开发桌面程序,也是也需要数据库的使用,发现创表比较麻烦,便是有了造轮子的想法

**实现原理?**

实现的思路很简单,使用反射技术,将实体类转为对应的Sql语句,之后通过JDBC执行创表语句,之后相关的数据库的查询,删除,更新等也是同理

## 支持数据库
- H2DataBase

目前暂定支持H2DataBase
## 使用

导入依赖

```
<dependency>
    <groupId>com.github.stars-one</groupId>
    <artifactId>kxorm</artifactId>
    <version>0.1</version>
</dependency>
```
或

```
implementation 'com.github.stars-one:kxorm:Tag'
```

下面介绍具体的使用步骤,详情也可以参考测试文件[KxOrmTest](https://github.com/stars-one/kxorm/blob/main/src/test/kotlin/site/starsone/kxorm/KxOrmTest.kt)

### 1.定义数据类

> 注意:**参数需要使用`var`关键字**,因为用查询是用反射初始化的,定义为val会导致实例初始化失败!!

```kotlin
@Table("my_data")
data class ItemData(
    @TableColumnPk
    @TableColumn("data_id")
    var dataId:String,
    var file: File,
    var dirName: String,
    var myCount:Int
)
```

目前测试是支持String,Int和File类型(其实File类型入库也是String类型)

### 2.初始化及数据类注册
```kotlin
val kclass =ItemData::class

val dbUrl = "jdbc:h2:D:/temp/h2db/test"
val user = ""
val pwd = ""

val kxDbConnConfig = KxDbConnConfig(dbUrl, user, pwd).registerClass(kclass)
KxDb.init(kxDbConnConfig)
```

这一步主要构建一个数据库连接配置`kxDbConnConfig`,之后还需要使用KxDbConnConfig对象的`registerClass`方法进行数据类的注册(**不注册之后无法使用!!**)

之后调用`KxDb.init()`,将此配置作为参数传入,完成初始化操作,此步里已经包含了创表的操作(如果库中表不存在)

> 由于是使用的H2DataBase,数据库不存在会自动进行创建

### 3.插入

使用`KxDb.insert(bean)`方法插入数据

```kotlin
val data = ItemData(
    "122",
    File("D:\\temp\\myd.png"),
    "D:\\temp",
    12
)
//返回的结果是sql执行的影响行数
val result = KxDb.insert(data) 
```

> PS: 批量插入还未实现,之后抽空实现

### 4.查询

- `getQueryList()` 查询表的所有数据
- `getQueryListByCondition()` 条件查询表的数据

```kotlin
//查询全部
val queryList = KxDb.getQueryList(ItemData::class)
println(queryList.toString())

//条件查询(Kotlin特有DSL语法)
//类似sql语句 select * from ITEMDATA where MYCOUNT > 10
val list = KxDb.getQueryListByCondition(ItemData::class){
    ItemData::myCount gt 10
}

//传where语句(不需要写where关键字)
val list = KxDb.getQueryListByCondition(ItemData::class,"MYCOUNT > 10 and DATAID LIKE '%j'")
```

目前条件查询的DSL语法暂且支持单条件,具体如何支持多条件,还在研究探索中,欢迎有经验的小伙伴可以一起交流 :taga:

如果你想用order by等语句,也可以在上述传where语句的方法里进行传值(因为还没有设计好,所以暂且这么用吧:joy:),如下代码所示
```kotlin
//传where语句(不需要写where关键字)
val list = KxDb.getQueryListByCondition(ItemData::class,"MYCOUNT > 10 and DATAID LIKE '%j' order by MYCOUNT")
```

### 5.更新

`updateForce()` 更新数据(以新数据类对象直接覆盖数据库中的旧数据)

```kotlin
val dataId = UUID.randomUUID().toString()
val data = ItemData(dataId,File("D:\\temp"),"mydirName11",20)
KxDb.insert(data)

data.myCount = 45
val row = KxDb.updateForce(data)
```

### 6.删除

```kotlin
val data = ItemData("232",File("D:\\temp"),"mydirName11",20)
KxDb.insert(data)
val row = KxDb.delete(data)
```