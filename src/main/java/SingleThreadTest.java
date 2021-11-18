import com.alibaba.druid.pool.DruidDataSource;
import self.pool.SelfDataSource;

import java.sql.*;
import java.util.Properties;

public class SingleThreadTest {

    private static int queryAmount = 5;

    /**
     * 原生JDBC查询
     */
    private static void rawExample() {
        for (int i=0; i<queryAmount; i++) {
            // Open a connection
            try(Connection conn = DriverManager.getConnection(TestCommon.DB_URL, TestCommon.USER, TestCommon.PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(TestCommon.QUERY);) {
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

    /**
     * 原生JDBC查询 单连接查询
     */
    private static void rawSingleExample() {
        try(Connection conn = DriverManager.getConnection(TestCommon.DB_URL, TestCommon.USER, TestCommon.PASS)) {
            for (int i=0; i<queryAmount; i++) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(TestCommon.QUERY);) {
                    while (rs.next()) {
                        System.out.print("ID: " + rs.getInt("id"));
                        System.out.print(", name: " + rs.getString("name"));
                        System.out.print(";");
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alibaba Druid查询
     */
    private static void druidExample() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(1);
        dataSource.setMinIdle(1);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(TestCommon.DB_URL);
        dataSource.setUsername(TestCommon.USER);
        dataSource.setPassword(TestCommon.PASS);

        for (int i=0; i<queryAmount; i++) {
            // Open a connection
            try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(TestCommon.QUERY)) {
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

    /**
     * 自定义数据库连接池查询
     */
    private static void selfExample() {
        final Properties properties = new Properties();
        properties.setProperty("url", "jdbc:h2:file:./demo-db");
        properties.setProperty("username", "sa");
        properties.setProperty("password", "sa");
        properties.setProperty("maxActive", "10");
        properties.setProperty("initCount", "5");
        SelfDataSource dataSource = new SelfDataSource(properties);

        for (int i=0; i<queryAmount; i++) {
            // Open a connection
            try(Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(TestCommon.QUERY)) {
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

    /**
     * 单线程测试代码
     * 注意：不要一起运行测试，感觉有缓存，导致在后面运行的查询速度很快
     *      需要运行那个单独放开，其他注释掉
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
        // 如果数据库数据未初始化，则先初始化数据
//        TestCommon.initData();

        final StringBuilder result = new StringBuilder();
        long current = System.currentTimeMillis();

//        rawExample();
//        result.append(String.format("原生查询耗时：%d 毫秒\n", System.currentTimeMillis() - current));

//        rawSingleExample();
//        result.append(String.format("原生Jdbc单连接查询耗时：%d 毫秒\n", System.currentTimeMillis() - current));

//        druidExample();
//        result.append(String.format("Druid连接池查询耗时：%d 毫秒\n", System.currentTimeMillis() - current));

        selfExample();
        result.append(String.format("自写连接池查询耗时：%d 毫秒\n", System.currentTimeMillis() - current));

        System.out.println(result);
    }
}
