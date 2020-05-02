# 一、简单题
1、Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？
    (1)动态sql更多的为了支持业务当中参数变量的动态变化性，来满足不同情况下的sql；
    (2)if,choose,when,otherwise,trim,where,set,foreach
    (3)从sql参数对象中计算表达式的值，根据表达式的值动态拼接 SQL 
2、Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？
    (1)支持
    (2)使用CGLIB创建目标对象的代理对象，当调用目标方法时，进入拦截器方法，
当拦截器invoke()方法发现是null值，就会先创建需要的对象，然后再进行调用
3、Mybatis都有哪些Executor执行器？它们之间的区别是什么？
    org.apache.ibatis.executor.Executor#update更新
    org.apache.ibatis.executor.Executor#query查询
    clearLocalCache 清缓存
    commit 提交事务
    rollback 回滚事务
4、简述下Mybatis的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？
    一级：Map<Object, Object> cache = new HashMap()，其中key是CacheKey的一个对象，
    属于sql所在的包名、类名、staementid和offset、limit、sql语句等共同构成属于sqlsession界别的缓存，当发生sql的修改、增加、删除会导致该缓存失效
    二级缓存是PerpetualCache，也是Map<Object, Object> cache = new HashMap()的结构，二级缓存是多个sqlsession共享的缓存，属于namespace的级别
    同一级一样，当发生insert/update/delte会触发刷新操作，在多表联查的时候会发生脏读，在分布式下基本会出现脏读
5、简述Mybatis的插件运行原理，以及如何编写一个插件？
    每个创建的对象并不是直接执行/直接返回的，而是会经过InterceptorChain，方法，进行调用前后的增加，例如dubbo的fifter和netty的ChannelPipeline
    例如，之前写过一个统计满sql的功能：需要实现Interceptor，需要通过@Signature注解过滤update和query方法，
    在returnValue = invocation.proceed()前后增加计时，最后算时间，打印不同的日志级别，进行不同的处理
# 二、编程题

请完善自定义持久层框架IPersistence，在现有代码基础上添加、修改及删除功能。【需要采用getMapper方式】
## 添加的功能点 
    int insertUserList(List<User> userList); 
    int insertUser(User user);
    int updateUser(User user);
    int deleteUser(User user);
## 实际执行结果
2020-05-02T11:46:31.847+0800 [INFO] mybatis com.hugh.sqlSession.SimpleExecutor sql:insert into user (username,password,birthday) values ( ?, ?, ? )
2020-05-02T11:46:31.874+0800 [INFO] mybatis com.hugh.sqlSession.SimpleExecutor sql:insert into user (username,password,birthday) values(?, ?, ?),(?, ?, ?),(?, ?, ?)
2020-05-02T11:47:44.992+0800 [INFO] mybatis com.hugh.sqlSession.SimpleExecutor sql:update user set username = ?, password = ?, birthday = ? where id = ?
2020-05-02T11:48:16.125+0800 [INFO] mybatis com.hugh.sqlSession.SimpleExecutor sql:delete from user where id = ?
##修改的内容
###    1、添加了SqlCommandType，用来标记从xml读取到的操作类型，方便在getMapper中进行处理
###    2、Executor接口中定义了update方法，用来执行非select的操作
###    3、SqlSession中增加了批量插入，修改和删除的操作
###    4、MappedStatement增加了动态sql的一些标记字段
###    5、SimpleExecutor中增加了updateForEach和updateFor方法，updateForEach用来处理ForEach的动态sql的处理(简化版)，目前代码修改仅支持ForEach的动态sql
###    6、其他(增加打印日志，lombok等)，代码优化和调整




