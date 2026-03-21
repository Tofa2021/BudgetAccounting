package org.example.util;

import org.example.dto.Status;
import org.example.exception.BusinessException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
public class TransactionUtilsTest {
    @Mock
    private SessionManager sessionManager;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        Mockito.when(sessionManager.openSession()).thenReturn(session);
        Mockito.when(session.beginTransaction()).thenReturn(transaction);
    }

    @Test
    public void executeInTransaction_withConsumerAndSuccess_shouldCommit() {
        Consumer<Session> consumer = Mockito.mock(Consumer.class);

        TransactionUtils.executeInTransaction(sessionManager, consumer);

        Mockito.verify(consumer).accept(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();

    }

    @Test
    public void executeInTransaction_withConsumerAndBusinessException_shouldNotCommitAndNotRollback() {
        Consumer<Session> consumer = Mockito.mock(Consumer.class);

        Mockito.doThrow(new BusinessException(Status.SERVER_ERROR, "")).when(consumer).accept(session);

        Assertions.assertThrows(BusinessException.class, () -> {
            TransactionUtils.executeInTransaction(sessionManager, consumer);
        });

        Mockito.verify(consumer).accept(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
    }

    @Test
    public void executeInTransaction_withConsumerAndRuntimeException_shouldNotCommitAndRollback() {
        Consumer<Session> consumer = Mockito.mock(Consumer.class);

        Mockito.doThrow(new RuntimeException()).when(consumer).accept(session);

        Assertions.assertThrows(RuntimeException.class, () -> {
            TransactionUtils.executeInTransaction(sessionManager, consumer);
        });

        Mockito.verify(consumer).accept(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction).rollback();
    }

    @Test
    public void executeInTransactionReturnFunction() {
        Function<Session, ?> function = Mockito.mock(Function.class);

        TransactionUtils.executeInTransaction(sessionManager, function);

        Mockito.verify(function).apply(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();
    }

    @Test
    public void executeInTransaction_withFunctionAndBusinessException_shouldNotCommitAndNoRollback() {
        Function<Session, ?> function = Mockito.mock(Function.class);

        Mockito.doThrow(new BusinessException(Status.SERVER_ERROR, "")).when(function).apply(session);

        Assertions.assertThrows(BusinessException.class, () -> {
            TransactionUtils.executeInTransaction(sessionManager, function);
        });

        Mockito.verify(function).apply(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
    }

    @Test
    public void executeInTransaction_withFunctionAndRuntimeException_shouldNotCommitAndRollback() {
        Function<Session, ?> function = Mockito.mock(Function.class);

        Mockito.doThrow(new RuntimeException()).when(function).apply(session);

        Assertions.assertThrows(RuntimeException.class, () -> {
            TransactionUtils.executeInTransaction(sessionManager, function);
        });

        Mockito.verify(function).apply(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction).rollback();
    }

    @Test
    public void executeInTransaction_withFunctionAndSuccess_shouldReturnValue() {
        int expectedValue = 10;
        Function<Session, Integer> function = Mockito.mock(Function.class);

        Mockito.when(function.apply(session)).thenReturn(expectedValue);

        int actualValue = TransactionUtils.executeInTransaction(sessionManager, function);

        Assertions.assertEquals(expectedValue, actualValue);
        Mockito.verify(function).apply(session);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
    }
}
