package com.hugh.sqlSession;


import com.hugh.pojo.Configuration;
import com.hugh.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

}
