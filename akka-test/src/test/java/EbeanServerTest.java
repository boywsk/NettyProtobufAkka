import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;

/**
 * Created by wangshikai on 2016/7/13.
 */
public class EbeanServerTest {
    public static void main(String[] args) {
        EbeanServer ebeanServer = Ebean.getServer(null);
        SqlUpdate sqlUpdate = Ebean.createSqlUpdate("update tbl_test set id =1 where id =1");
        int result = ebeanServer.execute(sqlUpdate);
        System.out.println(result);
    }
}
