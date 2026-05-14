package org.example.infrastructure.transaction;

import org.example.domain.dao.PersistenceManager;
import org.example.domain.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Supplier;

public class HibernatePersistenceManager implements PersistenceManager {
    private final SessionFactory sessionFactory = buildSessionFactory();
    private final ThreadLocal<Session> currentSession = new ThreadLocal<>();

    private SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("SessionFactory creation failed" + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public Session getCurrentSession() {
        Session session = currentSession.get();
        if (session == null) {
            throw new IllegalStateException("No active session");
        }
        return session;
    }

    private <R> R executeInSession(SessionOperation<R> operation) {
        try (Session session = sessionFactory.openSession()) {
            currentSession.set(session);
            return operation.execute(session);
        } catch (Exception e) {
            if (e instanceof BusinessException exception) {
                throw exception;
            }
            throw new RuntimeException(e);
        } finally {
            currentSession.remove();
        }
    }

    @Override
    public void executeTransaction(Runnable action) {
        executeInSession(session -> {
            Transaction transaction = session.beginTransaction();
            try {
                action.run();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
            return null;
        });
    }

    @Override
    public <R> R executeTransaction(Supplier<R> action) {
        return executeInSession(session -> {
            Transaction transaction = session.beginTransaction();
            try {
                R result = action.get();
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        });
    }

    @Override
    public <R> R executeReadOnlyTransaction(Supplier<R> action) {
        return executeInSession(session -> {
            Transaction transaction = session.beginTransaction();
            R result = action.get();
            transaction.rollback();
            return result;
        });
    }

    @Override
    public <R> R executeReadOnly(Supplier<R> action) {
        return executeInSession(session -> action.get());
    }

    public void close() {
        sessionFactory.close();
    }

    @FunctionalInterface
    private interface SessionOperation<R> {
        R execute(Session session) throws Exception;
    }
}
