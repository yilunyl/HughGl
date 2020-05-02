
import com.hugh.dao.IUserDao;
import com.hugh.io.Resource;
import com.hugh.pojo.User;
import com.hugh.sqlSession.SqlSession;
import com.hugh.sqlSession.SqlSessionFactory;
import com.hugh.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IPersistenceTest {

    private IUserDao userDao;

    @Before
    public void initSqlSession()throws Exception {
        InputStream resourceAsSteam = Resource.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        userDao = sqlSession.getMapper(IUserDao.class);

    }
    @Test
    public void selectTest() throws Exception {

        User user = new User();
        user.setId(1);
        user.setUsername("张三");

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }
    }

    @Test
    public void insertTest(){
        List<User> insertUsers = new ArrayList<>(10);
        for(int i=0;i<3;i++){
            insertUsers.add(User.builder()
                    .username(IPersistenceTest.class.getName()+i)
                    .birthday(LocalDateTime.now().toString())
                    .password(UUID.randomUUID().toString())
                    .build());
        }
        int i = userDao.insertUser(insertUsers.get(0));
        int j = userDao.insertUserList(insertUsers);
        System.out.println(i);
        System.out.println(j);
    }

    @Test
    public void updateTest(){
        User build = User.builder()
                .id(1)
                .birthday(LocalDateTime.now().toString())
                .password(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .build();
        int i = userDao.updateUser(build);
        System.out.println(i);
    }

    @Test
    public void deleteTest(){
        User build = User.builder()
                .id(9)
                .birthday(LocalDateTime.now().toString())
                .password(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .build();
        int i = userDao.deleteUser(build);
        System.out.println(i);
    }


}
