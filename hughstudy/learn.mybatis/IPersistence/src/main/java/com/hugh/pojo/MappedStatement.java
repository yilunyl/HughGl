package com.hugh.pojo;

import com.hugh.utils.SqlCommandType;

/**
 * @Classname MappedStatement
 * @Description TODO
 * @Date 2020/4/21 11:12 下午
 * @Created by gule
 */
public class MappedStatement {

    //id标识
    private String id;
    //返回值类型
    private String resultType;
    //参数值类型
    private String paramterType;
    //sql语句
    private String sql;
    //是否是动态sql
    private boolean isDynamicSql = false;
    //
    private String dynamicSql = "";

    public boolean isDynamicSql() {
        return isDynamicSql;
    }

    public void setDynamicSql(boolean dynamicSql) {
        isDynamicSql = dynamicSql;
    }

    public String getDynamicSql() {
        return dynamicSql;
    }

    public void setDynamicSql(String dynamicSql) {
        this.dynamicSql = dynamicSql;
    }

    //处理sql的操作类型
    private SqlCommandType commandType;
    public SqlCommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(SqlCommandType commandType) {
        this.commandType = commandType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamterType() {
        return paramterType;
    }

    public void setParamterType(String paramterType) {
        this.paramterType = paramterType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
