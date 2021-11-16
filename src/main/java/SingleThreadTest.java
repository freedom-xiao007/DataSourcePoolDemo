import com.alibaba.druid.pool.DruidDataSource;
import self.pool.SelfDataSource;

import java.sql.*;

public class SingleThreadTest {

    static final String DB_URL = "jdbc:h2:file:./demo-db";
    static final String USER = "sa";
    static final String PASS = "sa";
    static final String QUERY = "SELECT id, name FROM user_example";
    static final int queryAmount = 5;

    /**
     * 生成数据
     */
    private static void initData() {
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

    /**
     * 原生JDBC查询
     */
    private static void rawExample() {
        for (int i=0; i<queryAmount; i++) {
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
    }

    /**
     * 原生JDBC查询 单连接查询
     */
    private static void rawSingleExample() {
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            for (int i=0; i<queryAmount; i++) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(QUERY);) {
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
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);

        for (int i=0; i<queryAmount; i++) {
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

    /**
     * 自定义数据库连接池查询
     */
    private static void selfExample() {
        final SelfDataSource dataSource = new SelfDataSource(DB_URL, USER, PASS);
        for (int i=0; i<queryAmount; i++) {
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

    /**
     * 单线程测试代码
     * 注意：不要一起运行测试，感觉有缓存，导致在后面运行的查询速度很快
     *      需要运行那个单独放开，其他注释掉
     * @param args args
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {
//        initData();

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
