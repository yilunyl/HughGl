# HughGl
## 作业1
 作业完成模块：transfer-customize-anno
 
 自定义了如下的三个注解：@GlTransactional， @GlService， @GlAutowired
 实现的功能如下
 @GlTransactional:支持定义在类上和方法上，可以正常实现方法的回滚，同时也是一个bean
 @GlService:支持定义在类上@GlAutowired属性注入
 
 核心实现类的说明：GlApplicationContext，该类中的singletonBean管理了所有的bean
 核心实现思路：1、先获取@GlService注解的bean
            2、然后根据已经获取好的bean注入到@GlAutowired声明的地方
            3、单独处理剩下的@GlTransactional的类，先生成该bean然后运用动态代理，实现事务的功能(initTransactionalBean方法)
 测试方法：1、启动服务
          2、跑test:IoCTest.testIoC
 实际验证结果：通过
 

## 作业2
 processon画图： https://www.processon.com/diagraming/5eb121e65653bb072154a195 
 图片在当前目录下以: spring解决循环依赖.png
 思维导图是: spring.xmind
### 备注
    1、比较复杂的循环依赖等等，并未实现，接下来有空我会继续完善
    2、自己对spring的aop实现还是有点绕，接下来会在进行研究一下
    3、starUml是用了，但是感觉很难用，希望老师可以抽空讲解下




