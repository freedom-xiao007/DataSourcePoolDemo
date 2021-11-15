package jdbcRaw;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import self.pool.SelfDatasource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class Main {

    static final String DB_URL = "jdbc:h2:file:./demo-db";
    static final String USER = "sa";
    static final String PASS = "sa";
    static final String QUERY = "SELECT id, name FROM user_example";

    private static void initData() {
        final String drop = "drop table `user_example` if exists;";
        final String createTable = "CREATE TABLE IF NOT EXISTS `user_example` (" +
                "`id` bigint NOT NULL AUTO_INCREMENT, " +
                "`name` varchar(100) NOT NULL" +
                ");";
        final String addUser = "insert into user_example (name) values(%s)";
        // Open a connection
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

    private static void select() {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("ID: " + rs.getInt("id"));
                System.out.print(", name: " + rs.getString("name"));
                System.out.print(";");
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        initData();

        long current = System.currentTimeMillis();
        for (int i=0; i<10; i++) {
            select();
        }
        System.out.printf("原生查询耗时：%d 毫秒\n", System.currentTimeMillis() - current);

        current = System.currentTimeMillis();
        druidExample();
        System.out.printf("连接池查询耗时：%d 毫秒\n", System.currentTimeMillis() - current);

        current = System.currentTimeMillis();
        selfExample();
        System.out.printf("自写连接池查询耗时：%d 毫秒\n", System.currentTimeMillis() - current);

        Thread.sleep(3000);
    }

    private static void selfExample() {
        final SelfDatasource dataSource = new SelfDatasource(DB_URL, USER, PASS);
        for (int i=0; i<10; i++) {
            // Open a connection
            try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(QUERY)) {
                // Extract data from result set
                while (rs.next()) {
                    // Retrieve by column name
                    System.out.print("ID: " + rs.getInt("id"));
                    System.out.print(", name: " + rs.getString("name"));
                    System.out.print(";");
                }
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void druidExample() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("url", DB_URL);
        properties.setProperty("username", USER);
        properties.setProperty("password", PASS);
        properties.setProperty("driverClass", "org.h2.Driver");

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(1);
        dataSource.setMinIdle(1);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);

        for (int i=0; i<10; i++) {
            // Open a connection
            try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(QUERY)) {
                // Extract data from result set
                while (rs.next()) {
                    // Retrieve by column name
                    System.out.print("ID: " + rs.getInt("id"));
                    System.out.print(", name: " + rs.getString("name"));
                    System.out.print(";");
                }
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
