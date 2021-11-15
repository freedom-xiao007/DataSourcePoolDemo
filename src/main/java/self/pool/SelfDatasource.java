package self.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.logging.Logger;

public class SelfDatasource implements DataSource {

    private final Queue<SelfPoolConnection> idle = new LinkedList<>();
    private final Set<SelfPoolConnection> running = new HashSet<>();
    private final String url;
    private final String username;
    private final String password;

    public SelfDatasource(final String url, final String username, final String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void recycle(final SelfPoolConnection selfPoolConnection) {
        running.remove(selfPoolConnection);
        idle.add(selfPoolConnection);
        System.out.println("回收连接");
    }

    @Override
    synchronized public Connection getConnection() throws SQLException {
        if (idle.isEmpty()) {
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
