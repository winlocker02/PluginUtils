package ru.winlocker.utils.sql;

import com.google.common.util.concurrent.*;
import lombok.*;

import java.sql.*;
import java.util.concurrent.*;

public interface SqlDatabase {

    ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setNameFormat("LAST MySQL-Worker #%s")
            .setDaemon(true)
            .build());
    int execute(boolean async, @NonNull String sql, Object...objects);
    <V> V executeQuery(boolean async, @NonNull String sql, @NonNull ResponseHandler<ResultSet, V> handler, Object...objects);

    Connection getConnection();

    void closeConnection() throws SQLException;

    default <V> V handle(boolean async, Callable<V> callable) {
        if(async) {
            Future<V> future = THREAD_POOL.submit(callable);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to execute async query", e);
            }
        } else {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute sync query", e);
            }
        }
    }
}
