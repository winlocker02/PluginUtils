package ru.winlocker.utils.sql;

import java.sql.*;
import java.util.concurrent.*;

public interface SqlDatabase {

    ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    void execute(boolean async, String sql, Object...objects);

    ResultSet query(String sql, Object...objects);
    void query(boolean async, String sql, ResponseHandler<ResultSet, SQLException> handler, Object...objects);

    Connection getConnection();

    void closeConnection() throws SQLException;
}
