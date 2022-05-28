package ru.winlocker.utils.sql.database;

import lombok.*;
import org.sqlite.*;
import ru.winlocker.utils.sql.*;

import java.io.*;
import java.sql.*;

public class SqliteDatabase implements SqlDatabase {

    private final SQLiteDataSource dataSource = new SQLiteDataSource();
    private Connection connection;

    @Builder(buildMethodName = "create")
    public SqliteDatabase(File databaseFile, String database) throws SQLException {
        this("jdbc:sqlite:" + databaseFile, database);
    }

    public SqliteDatabase(String url, String database) throws SQLException {
        this.dataSource.setDatabaseName(database);
        this.dataSource.setUrl(url);
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
            throw new RuntimeException("[SqliteDatabase] Error connecting to database", e);
        }
    }

    private Connection refreshConnection() throws SQLException {
        if(this.connection == null || this.connection.isClosed() || !this.connection.isValid(1000)) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
