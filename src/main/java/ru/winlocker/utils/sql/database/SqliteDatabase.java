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
    public void execute(boolean async, String sql, Object... objects) {

        Runnable runnable = () -> {
            try (SqlStatement statement = new SqlStatement(getConnection(), sql, objects)) {
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if(async) {
            THREAD_POOL.submit(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public ResultSet query(String sql, Object... objects) {
        try {
            SqlStatement statement = new SqlStatement(getConnection(), sql, objects);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("[SqliteDatabase] Error executing query to database", e);
        }
    }

    @Override
    public void query(boolean async, String sql, ResponseHandler<ResultSet, SQLException> handler, Object... objects) {

        Runnable runnable = () -> {
            try (SqlStatement statement = new SqlStatement(getConnection(), sql, objects)) {
                handler.handle(statement.executeQuery());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if(async) {
            THREAD_POOL.submit(runnable);
        } else {
            runnable.run();
        }
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
