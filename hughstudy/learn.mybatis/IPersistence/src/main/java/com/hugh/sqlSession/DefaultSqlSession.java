package com.hugh.sqlSession;


import com.hugh.pojo.Configuration;
import com.hugh.pojo.MappedStatement;
import com.hugh.utils.SqlCommandType;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @SuppressWarnings("unchecked")
    public  <E> List<E> selectList(String statementid, Object... params) throws Exception {

        //将要去完成对simpleExecutor里的query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);

        return (List<E>) list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T selectOne(String statementid, Object... params) throws Exception {
        List<Object> objects = selectList(statementid, params);
        if(objects.size()==1){
            return (T) objects.get(0);
        }else {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }


    }

    @Override
    public int update(String statementid, Object... params) throws Exception {
        //here can do something
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementid);
        return simpleExecutor.update(configuration, mappedStatement, params);
    }

    @Override
    public int insert(String statementid, Object... params) throws Exception {
        //here can do something
        return update(statementid, params);
    }

    @Override
    public int delete(String statementid, Object... params) throws Exception {
        //here can do something
        return update(statementid, params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码 //根据不同情况，来调用selctList或者selectOne
                // 准备参数 1：statmentid :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className+"."+methodName;
                SqlCommandType commandType = configuration.getMappedStatementMap().get(statementId).getCommandType();
                switch (commandType){
                    case SELECT:{
                        // 准备参数2：params:args
                        // 获取被调用方法的返回值类型
                        Type genericReturnType = method.getGenericReturnType();
                        // 判断是否进行了 泛型类型参数化,true代表一个集合，false代表一个对象
                        if (genericReturnType instanceof ParameterizedType) {
                            return selectList(statementId, args);
                        }
                        return selectOne(statementId, args);
                    }
                    case INSERT:
                        return insert(statementId, args);
                    case DELETE:
                        return delete(statementId, args);
                    case UPDATE:
                        return update(statementId, args);
                    default:
                        throw new RuntimeException("暂不支持的处理类型");
                }
            }
        });
        return (T) proxyInstance;
    }


}
