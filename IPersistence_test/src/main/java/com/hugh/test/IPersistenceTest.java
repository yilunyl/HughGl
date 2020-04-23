package com.hugh.test;

import com.hugh.io.Resource;
import com.hugh.sqlSession.SqlSession;
import com.hugh.sqlSession.SqlSessionFactory;
import com.hugh.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @Classname IPersistenceTest
 * @Description TODO
 * @Date 2020/4/21 9:46 下午
 * @Created by gule
 */
public class IPersistenceTest {

    public void test() throws PropertyVetoException, DocumentException {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
//        Object selectOne = sqlSession.selectOne("user.selectOne", );
    }
}
