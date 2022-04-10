package ru.winlocker.utils.sql;

public interface ResponseHandler <V, T extends Throwable> {

    void handle(V value) throws T;
}
