package org.example;

import org.example.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionUtils {
    public static void executeInTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = Hibernate.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
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

    public static <R> R executeInTransaction(Function<Session, R> action) {
        Transaction transaction = null;
        try (Session session = Hibernate.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            R result = action.apply(session);
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
