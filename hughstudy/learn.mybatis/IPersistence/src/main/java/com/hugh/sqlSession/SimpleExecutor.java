package com.hugh.sqlSession;


import com.hugh.config.BoundSql;
import com.hugh.pojo.Configuration;
import com.hugh.pojo.MappedStatement;
import com.hugh.utils.GenericTokenParser;
import com.hugh.utils.ParameterMapping;
import com.hugh.utils.ParameterMappingTokenHandler;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleExecutor implements  Executor {

    @SuppressWarnings("unchecked")
    @Override                                                                                //user
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // 1. 注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2. 获取sql语句 : select * from user where id = #{id} and username = #{username}
            //转换sql语句： select * from user where id = ? and username = ? ，转换的过程中，还需要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        // 3.获取预处理对象：preparedStatement
        //将含有'？'的占位符的sql封装进PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4. 设置参数
            //获取到了参数的全路径
         String paramterType = mappedStatement.getParamterType();
         //获取参数的类对象
         Class<?> paramtertypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        //通过遍历参数list(应该必须保证有序)，一次给对应的'?'占位符进行赋值
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //反射 获取属性对象
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //o就是获取到的属性值
            Object o = declaredField.get(params[0]);
            //preparedStatement的序号是从1开始的
            preparedStatement.setObject(i+1,o);

        }


        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        ArrayList<Object> objects = new ArrayList<>();

        // 6. 封装返回结果集
        while (resultSet.next()){
            //将类对象转成具体的类的对象
            Object o =resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //i为什么从1开始？--> metaData.getColumnCount()代表查询的总列数
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                //PropertyDescriptor是内省库中一个类，作用是根据resultTypeClass对象来给对应的属性字段生成读写方法
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);


            }
            objects.add(o);

        }
        return (List<E>) objects;

    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        //处理动态sql中foreach
        if(mappedStatement.isDynamicSql() && params[0] instanceof List){
            return updateForEach(configuration, mappedStatement, (List)params[0]);
        }

        //注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        log.info("sql:{}", boundSql.getSqlText());
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        // 4. 设置参数
        //获取到了参数的全路径
        String paramterType = mappedStatement.getParamterType();
        //获取参数的类对象
        Class<?> paramtertypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        //通过遍历参数list(应该必须保证有序)，一次给对应的'?'占位符进行赋值
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //反射 获取属性对象
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //o就是获取到的属性值
            Object o = declaredField.get(params[0]);
            //preparedStatement的序号是从1开始的
            preparedStatement.setObject(i+1,o);

        }
        return preparedStatement.executeUpdate();
    }

    private int updateForEach(Configuration configuration, MappedStatement mappedStatement, List list) throws Exception {
        //注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();
        StringBuilder sb = new StringBuilder(mappedStatement.getSql());
        for(int index=0;index<list.size();index++) {
            sb.append(mappedStatement.getDynamicSql());
            if(index +1 < list.size()){
                sb.append(",");
            }
        }
        String sql = sb.toString();
        BoundSql dynamicBoundSql = getBoundSql(sql);
        log.info("sql:{}", dynamicBoundSql.getSqlText());
        PreparedStatement preparedStatement = connection.prepareStatement(dynamicBoundSql.getSqlText());
        // 4. 设置参数
        //获取到了参数的全路径
        String paramterType = list.get(0).getClass().getName();
        //获取参数的类对象
        Class<?> paramtertypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappingList = dynamicBoundSql.getParameterMappingList();
        int size = parameterMappingList.size();
        int eachObj = size / list.size();
        //通过遍历参数list(应该必须保证有序)，一次给对应的'?'占位符进行赋值
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //反射 获取属性对象
            Field declaredField = paramtertypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            //o就是获取到的属性值
            Object o = declaredField.get(list.get(i/eachObj));
            //preparedStatement的序号是从1开始的
            preparedStatement.setObject(i + 1, o);
        }

        return preparedStatement.executeUpdate();
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if(paramterType!=null){
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
         return null;
    }


    /**
     * 完成对#{}的解析工作：1.将#{}使用？进行代替，2.解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return boundSql;

    }


}
