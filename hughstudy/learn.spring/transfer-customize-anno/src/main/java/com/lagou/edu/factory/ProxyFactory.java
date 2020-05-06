package com.lagou.edu.factory;

import com.lagou.edu.anno.GlAutowired;
import com.lagou.edu.anno.GlService;
import com.lagou.edu.anno.GlTransactional;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author 应癫
 *
 *
 * 代理对象工厂：生成代理对象的
 */


@GlService("proxyFactory")
public class ProxyFactory {


    @GlAutowired
    private TransactionManager transactionManager;

    private Boolean isAll;

    public Object getAopProxy(Object obj, Boolean isAll){
        this.isAll = isAll;
        if(obj.getClass().isInterface()){
            return getJdkProxy(obj);
        }
        return getCglibProxy(obj);
    }

    /**
     * Jdk动态代理
     * @param obj  委托对象
     * @return   代理对象
     */
    private Object getJdkProxy(Object obj) {

        // 获取代理对象
        return  Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        if(!judgeIsGlTransactionalMethod(method)){
                            return method.invoke(obj,args);
                        }
                        try{
                            // 开启事务(关闭事务的自动提交)
                            transactionManager.beginTransaction();
                            result = method.invoke(obj,args);
                            // 提交事务
                            transactionManager.commit();
                        }catch (Exception e) {
                            e.printStackTrace();
                            // 回滚事务
                            transactionManager.rollback();
                            // 抛出异常便于上层servlet捕获
                            throw e;
                        }
                        return result;
                    }
                });
    }
    /**
     * 使用cglib动态代理生成代理对象
     * @param obj 委托对象
     * @return
     */
    private Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                if(!judgeIsGlTransactionalMethod(method)){
                    return method.invoke(obj,objects);
                }
                try{
                    // 开启事务(关闭事务的自动提交)
                    transactionManager.beginTransaction();
                    result = method.invoke(obj,objects);
                    // 提交事务
                    transactionManager.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback();
                    // 抛出异常便于上层servlet捕获
                    throw e;
                }
                return result;
            }
        });
    }

    /**
     * 判断是否需要走事务处理
     * @param method 方法
     * @return Boolean
     */
    private Boolean judgeIsGlTransactionalMethod(Method method){
        GlTransactional methodAnnotation = method.getAnnotation(GlTransactional.class);
        if(isAll || Objects.nonNull(methodAnnotation)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
