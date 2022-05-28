package ru.winlocker.utils.sql.database;

import com.mysql.jdbc.jdbc2.optional.*;
import lombok.*;
import ru.winlocker.utils.sql.*;

import java.sql.*;

public class MysqlDatabase implements SqlDatabase {

    private final MysqlDataSource dataSource = new MysqlDataSource();
    private Connection connection;

    @Builder(buildMethodName = "create")
    public MysqlDatabase(String host, int port, String user, String password, String database) throws SQLException {
        this.dataSource.setServerName(host);
        this.dataSource.setPort(port);
        this.dataSource.setUser(user);
        this.dataSource.setPassword(password);
        this.dataSource.setDatabaseName(database);
        this.dataSource.setEncoding("UTF-8");

        this.refreshConnection();
    }

    @Override
    public int execute(boolean async, @NonNull String sql, Object... objects) {
        return handle(async, () -> {
            try (SqlStatement statement = new SqlStatement(getConnection(), sql, objects)) {
                return statement.execute();
            }
        });
    }

    @Override
    public <V> V executeQuery(boolean async, @NonNull String sql, @NonNull ResponseHandler<ResultSet, V> handler, Object... objects) {
        return handle(async, () -> {
            try (SqlStatement statement = new SqlStatement(getConnection(), sql, objects)) {
                return handler.handleResponse(statement.executeQuery());
            }
        });
    }

    @Override
    public Connection getConnection() {
        try {
            return this.refreshConnection();
        } catch (SQLException e) {
            throw new RuntimeException("[MysqlDatabase] Error connecting to database", e);
        }
    }

    private Connection refreshConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed() || !this.connection.isValid(1000)) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
