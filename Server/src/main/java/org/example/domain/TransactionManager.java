package org.example.domain;

import java.util.function.Supplier;

public interface TransactionManager {
    void executeInTransaction(Runnable action);

    <R> R executeInTransaction(Supplier<R> action);
}
