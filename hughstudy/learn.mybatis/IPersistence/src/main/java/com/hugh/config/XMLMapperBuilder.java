package com.hugh.config;

import com.hugh.pojo.Configuration;
import com.hugh.pojo.MappedStatement;
import com.hugh.utils.SqlCommandType;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
@Slf4j
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration =configuration;
    }

    @SuppressWarnings("unchecked")
    public void parse(InputStream inputStream) throws DocumentException {

        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");

        List<Element> list = rootElement.selectNodes("//*");
        for (int i=0;i< list.size();i++) {
            Element element = list.get(i);
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sqlText = element.getTextTrim();
            // 获得 SQL 对应的 SqlCommandType 枚举值
            String qName = element.getQName().getName();
            SqlCommandType sqlCommandType = SqlCommandType.UNKNOWN;
            try {
                sqlCommandType = SqlCommandType.valueOf(qName.toUpperCase(Locale.ENGLISH));
            }catch (Exception e){
                log.error("sql id {} is unknow type", namespace+ id);
            }
            if(SqlCommandType.UNKNOWN.equals(sqlCommandType)){
                //有可能是动态sql，目前仅针对foreach进行处理
                //取上一条的Element
                if(i-1 >= 0){
                    Element lastElement = list.get(i - 1);
                    //说明是动态sql
                    String lasetId = lastElement.attributeValue("id");
                    String lastKey = namespace+"."+lasetId;
                    MappedStatement lastMappedStatement = configuration.getMappedStatementMap().get(lastKey);
                    lastMappedStatement.setDynamicSql(true);
                    lastMappedStatement.setDynamicSql(sqlText);
                    continue;
                }
            }
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(sqlText);
            mappedStatement.setCommandType(sqlCommandType);
            String key = namespace+"."+id;
            configuration.getMappedStatementMap().put(key,mappedStatement);

        }

    }


}
