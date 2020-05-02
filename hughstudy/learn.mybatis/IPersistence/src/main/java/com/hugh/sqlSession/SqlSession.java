package com.hugh.sqlSession;

import java.sql.SQLException;
import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     * @param statementid statementid
     * @param params params
     * @param <E> 泛型
     * @return 对象集合
     * @throws Exception 异常
     */
    public <E> List<E> selectList(String statementid, Object... params) throws Exception;

    /**
     * 根据条件查询单个
     * @param statementid statementid
     * @param params params
     * @param <T> 泛型
     * @return 对象集合
     * @throws Exception 异常
     */
    public <T> T selectOne(String statementid, Object... params) throws Exception;

    /**
     * 更新操作
     * @param statementid statementid
     * @param params 参数
     * @return int
     * @throws SQLException 异常
     */
    int update(String statementid, Object... params) throws SQLException, Exception;

    /**
     * 插入操作
     * @param statementid statementid
     * @param params 参数
     * @return int
     * @throws SQLException 异常
     */
    int insert(String statementid, Object... params) throws SQLException, Exception;

    /**
     * 删除操作
     * @param statementid statementid
     * @param params 参数
     * @return int
     * @throws SQLException 异常
     */
    int delete(String statementid, Object... params) throws SQLException, Exception;
    /**
     * 为Dao接口生成代理实现类
     * @param mapperClass 接口类类
     * @param <T> 泛型
     * @return 泛型
     */
    public <T> T getMapper(Class<?> mapperClass);
}
