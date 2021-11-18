import com.alibaba.druid.pool.DruidDataSource;
import self.pool.SelfDataSource;

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

        final Properties properties = new Properties();
        properties.setProperty("url", "jdbc:h2:file:./demo-db");
        properties.setProperty("username", "sa");
        properties.setProperty("password", "sa");
        properties.setProperty("maxActive", "10");
        properties.setProperty("initCount", "5");

        SelfDataSource dataSource = new SelfDataSource(properties);

        FutureTask[] fs = new FutureTask[10];
        for (int i=0; i<10; i++) {
            fs[i] = new FutureTask(() -> druidQuery(dataSource));
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

        long cost = 0;
        for (int i = 0; i < 10; i++) {
            cost += (Long)(fs[i].get());
        }
        System.out.printf("一共花费：%d \n", cost);

        Thread.sleep(3000);
        fs = new FutureTask[5];
        for (int i=0; i<5; i++) {
            fs[i] = new FutureTask(() -> druidQuery(dataSource));
            new Thread(fs[i]).start();
        }

        Thread.sleep(3000);
        System.out.printf("当前数据库连接数：%d\n", dataSource.getConnectionCount());
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

    private static long druidQuery(SelfDataSource dataSource) {
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
            System.out.printf("当前数据库连接数：%d\n", dataSource.getConnectionCount());
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        final long cost = System.currentTimeMillis() - cur;
        System.out.println(cost);
        return cost;
    }
}
