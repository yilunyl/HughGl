import com.lagou.edu.context.GlApplicationContext;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.utils.ConnectionUtils;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author 应癫
 */
public class IoCTest {


    @Test
    public void testIoC() throws Exception {

        GlApplicationContext glApplicationContext = new GlApplicationContext("com.lagou.edu");
        TransferService transferService = (TransferService) glApplicationContext.getBeanByName("transferService");
        if(Objects.nonNull(transferService)){
            transferService.transfer("李大雷 6029621011000","韩梅梅 6029621011001",100);
            System.out.println("success!");
        }

    }

    /**
     * mysql驱动版本 8.0.11
     * @throws SQLException
     */
    @Test
    public void testMysql() throws SQLException {
        ConnectionUtils connectionUtils = new ConnectionUtils();
        Connection con = connectionUtils.getCurrentThreadConn();
        String sql = "update account set money=? where cardNo=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1,12300);
        preparedStatement.setString(2,"李大雷 6029621011000");
        int i = preparedStatement.executeUpdate();
        System.out.println("out" + i);
    }
}
