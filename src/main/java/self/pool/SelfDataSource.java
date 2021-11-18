package self.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class SelfDataSource implements DataSource {

    /**
     * 放置空闲可用的连接
     */
    private final LinkedBlockingDeque<SelfPoolConnection> idle = new LinkedBlockingDeque<>();
    /**
     * 放置正在使用的连接
     */
    private final LinkedBlockingDeque<SelfPoolConnection> active = new LinkedBlockingDeque<>();
    private final AtomicInteger connectCount = new AtomicInteger(0);

    private final String url;
    private final String username;
    private final String password;
    private int maxActive;
    private int timeout = 100;

    public SelfDataSource(final String url, final String username, final String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public SelfDataSource(final Properties properties) {
        this.url = properties.getProperty("url");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.maxActive = Integer.parseInt(properties.getProperty("maxActive"));
        this.timeout = Integer.parseInt(properties.getProperty("timeout", "100"));

        final int initCount = Integer.parseInt(properties.getProperty("initCount", "0"));
        if (initCount > maxActive) {
            throw new RuntimeException("initCount gt maxActive");
        }

        IntStream.range(0, initCount).forEach(i -> createPhysicsConnect());
    }

    private void createPhysicsConnect() {
        System.out.println("生成新物理连接");
        try {
            idle.put(new SelfPoolConnection(this, url, username, password));
            connectCount.addAndGet(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初步将Connection从运行池中异常，放入空闲池
     * 从正在使用连接池中移除，放入空闲连接池中
     * @param selfPoolConnection 自定义Connection
     */
    synchronized public void recycle(final SelfPoolConnection selfPoolConnection) {
        try {
            System.out.println("回收连接,开始");
            while (!active.remove(selfPoolConnection)){
                System.out.println("!active.remove");
            }
            while (idle.offer(selfPoolConnection, timeout, TimeUnit.NANOSECONDS)) {
                System.out.println("idle.offer");
            }
            System.out.println("回收连接,结束\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        SelfPoolConnection connection;
        try {
            System.out.println("获取连接，开始");
            connection = idle.poll(timeout, TimeUnit.NANOSECONDS);
            while (connection == null) {
                connection = idle.poll(timeout, TimeUnit.NANOSECONDS);
            }
            while (!active.offer(connection, timeout, TimeUnit.NANOSECONDS)){
                System.out.println("active.offer");
            }
            System.out.println("获取连接，结束\n");
            return connection;
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

    public void setMaxActiveCount(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getConnectionCount() {
        return 0;
    }
}
