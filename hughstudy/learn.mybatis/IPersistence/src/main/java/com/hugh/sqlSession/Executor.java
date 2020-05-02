package com.hugh.sqlSession;


import com.hugh.pojo.Configuration;
import com.hugh.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    /**
     * 通用查询接口
     * @param configuration
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;


    int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, Exception;
}
