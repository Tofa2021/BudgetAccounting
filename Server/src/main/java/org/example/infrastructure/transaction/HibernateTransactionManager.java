package org.example.infrastructure.transaction;

import org.example.domain.TransactionManager;
import org.example.domain.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Supplier;

public class HibernateTransactionManager implements TransactionManager {
    private final SessionFactory sessionFactory = buildSessionFactory();

    private SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("SessionFactory creation failed" + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void executeInTransaction(Runnable action) {
        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            action.run();
            transaction.commit();
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> R executeInTransaction(Supplier<R> action) {
        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            R result = action.get();
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
