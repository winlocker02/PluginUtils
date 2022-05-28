package ru.winlocker.utils.sql;

import java.sql.*;

@FunctionalInterface
public interface ResponseHandler <V, R> {

    R handleResponse(V value) throws SQLException;
}
