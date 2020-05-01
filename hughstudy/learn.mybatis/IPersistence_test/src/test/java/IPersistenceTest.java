
import com.hugh.dao.IUserDao;
import com.hugh.io.Resource;
import com.hugh.pojo.User;
import com.hugh.sqlSession.SqlSession;
import com.hugh.sqlSession.SqlSessionFactory;
import com.hugh.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream resourceAsSteam = Resource.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        User user = new User();
        user.setId(1);
        user.setUsername("张三");

       /* List<User> users = sqlSession.selectList("user.selectList");
        for (User user1 : users) {
            System.out.println(user1);
        }*/

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }


    }



}
