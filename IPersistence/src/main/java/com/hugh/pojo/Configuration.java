package com.hugh.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname Configuration
 * @Description TODO
 * @Date 2020/4/21 11:15 下午
 * @Created by gule
 */
public class Configuration {
    private DataSource dataSource;

    /*
     *   key: statementid  value:封装好的mappedStatement对象
     * */
     Map<String,MappedStatement> mappedStatementMap = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
