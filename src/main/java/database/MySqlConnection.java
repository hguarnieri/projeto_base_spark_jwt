package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConnection {
    private static String dbUrl = "jdbc:mysql://localhost:3306/server";
    private static String dbUsername = "server";
    private static String dbPassword = "123";

    static HikariConfig config;

    static HikariDataSource ds;

    public static Connection getConnection() throws SQLException {
        if (config == null) {
            config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useSSL", "false");

            ds = new HikariDataSource(config);
        }

        return ds.getConnection();
    }
}