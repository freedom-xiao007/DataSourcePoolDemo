package self.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.logging.Logger;

public class SelfDataSource implements DataSource {

    /**
     * 放置空闲可用的连接
     */
    private final Queue<SelfPoolConnection> idle = new LinkedList<>();
    /**
     * 放置正在使用的连接
     */
    private final Set<SelfPoolConnection> running = new HashSet<>();
    private final String url;
    private final String username;
    private final String password;

    public SelfDataSource(final String url, final String username, final String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 初步将Connection从运行池中异常，放入空闲池
     * 从正在使用连接池中移除，放入空闲连接池中
     * @param selfPoolConnection 自定义Connection
     */
    public void recycle(final SelfPoolConnection selfPoolConnection) {
        running.remove(selfPoolConnection);
        idle.add(selfPoolConnection);
        System.out.println("回收连接");
    }

    /**
     * 自定义的获取数据库物理连接
     * 1.无空闲连接则生成新的物理连接，并且放入正在运行连接池中
     * 2.如果有空闲连接，则获取，并放入正在运行连接池中
     * @return 自定义的数据库物理连接（自定义以能够自定义Close方法）
     * @throws SQLException
     */
    @Override
    synchronized public Connection getConnection() throws SQLException {
        if (idle.isEmpty()) {
            System.out.println("生成新物理连接");
            SelfPoolConnection conn = new SelfPoolConnection(this, url, username, password);
            running.add(conn);
            return conn.getConnection();
        }
        SelfPoolConnection conn = idle.poll();
        running.add(conn);
        return conn.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
