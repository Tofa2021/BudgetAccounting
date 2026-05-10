package org.example.domain;

import java.util.function.Supplier;

public interface PersistenceManager {
    void executeTransaction(Runnable action);

    <R> R executeTransaction(Supplier<R> action);

    <R> R executeReadOnlyTransaction(Supplier<R> action);

    <R> R executeReadOnly(Supplier<R> action);
}
