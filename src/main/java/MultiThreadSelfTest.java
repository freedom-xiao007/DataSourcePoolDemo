import com.alibaba.druid.pool.DruidDataSource;
import self.pool.SelfDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MultiThreadSelfTest {

    public static final String DB_URL = "jdbc:h2:file:./demo-db";
    public static final String USER = "sa";
    public static final String PASS = "sa";
    public static final String QUERY = "SELECT id, name FROM user_example";


    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 第一次运行可以初始化数据库数据，后面可以取消
//        initData();

//        final Properties properties = new Properties();
//        properties.setProperty("url", "jdbc:h2:file:./demo-db");
//        properties.setProperty("username", "sa");
//        properties.setProperty("password", "sa");
//        properties.setProperty("maxActive", "5");
//        properties.setProperty("initCount", "5");
//
//        SelfDataSource dataSource = new SelfDataSource(properties);
//        System.out.println("测试自写线程池");
//        multiQuery(dataSource);

        System.out.println("测试Druid线程池");
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setInitialSize(0);
        druidDataSource.setMaxActive(3);
        druidDataSource.setMinIdle(2);
        druidDataSource.setDriverClassName("org.h2.Driver");
        druidDataSource.setUrl(DB_URL);
        druidDataSource.setUsername(USER);
        druidDataSource.setPassword(PASS);
        multiQuery(druidDataSource);
    }

    private static void multiQuery(DataSource dataSource) {
        System.out.println("初始化线程，开始启动测试");
        long current = System.currentTimeMillis();
        FutureTask[] fs = new FutureTask[10];
        for (int i=0; i<10; i++) {
            fs[i] = new FutureTask(() -> query(dataSource));
            new Thread(fs[i]).start();
        }

        while (true) {
            for (int i = 0; i < 10; i++) {
                if (!fs[i].isDone()) {
                    continue;
                }
            }
            break;
        }
        System.out.println("所有线程已查询完毕，消耗时间为：" + (System.currentTimeMillis() - current));
    }

    private static long query(DataSource dataSource) {
        System.out.println("开始执行查询");
        final long cur = System.currentTimeMillis();
        try(Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY)) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
//                System.out.print("ID: " + rs.getInt("id"));
//                System.out.print(", name: " + rs.getString("name"));
//                System.out.print(";");
            }
//            System.out.println();
            Thread.sleep(1000);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        final long cost = System.currentTimeMillis() - cur;
        System.out.println("查询结束，耗时：" + cost);
        return cost;
    }

    /**
     * 生成数据
     */
    public static void initData() {
        final String drop = "drop table `user_example` if exists;";
        final String createTable = "CREATE TABLE IF NOT EXISTS `user_example` (" +
                "`id` bigint NOT NULL AUTO_INCREMENT, " +
                "`name` varchar(100) NOT NULL" +
                ");";
        final String addUser = "insert into user_example (name) values(%s)";
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement()) {
            stmt.execute(drop);
            stmt.execute(createTable);
            for (int i=0; i<10; i++) {
                stmt.execute(String.format(addUser, i));
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
