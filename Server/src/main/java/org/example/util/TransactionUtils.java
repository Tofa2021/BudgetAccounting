package org.example.util;

import org.example.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionUtils {
    public static void executeInTransaction(SessionManager sessionManager, Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionManager.openSession()) {
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

    public static <R> R executeInTransaction(SessionManager sessionManager, Function<Session, R> action) {
        Transaction transaction = null;
        try (Session session = sessionManager.openSession()) {
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
