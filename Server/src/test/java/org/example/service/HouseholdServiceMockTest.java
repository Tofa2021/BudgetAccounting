package org.example.service;

import org.example.application.service.HouseholdService;
import org.example.domain.SessionManager;
import org.example.domain.exception.not_found.BudgetNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.DecreaseOperation;
import org.example.domain.model.Household;
import org.example.domain.model.IncreaseOperation;
import org.example.domain.model.User;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.OperationCategory;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.infrastructure.dao.HibernateHouseholdDAO;
import org.example.infrastructure.dao.HibernateUserDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class HouseholdServiceMockTest {
    @InjectMocks
    private HouseholdService householdService;

    @Mock
    private HibernateHouseholdDAO hibernateBudgetDAO;
    @Mock
    private HibernateUserDAO hibernateUserDAO;
    @Mock
    private SessionManager sessionManager;

    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    @BeforeEach
    public void SetUp() {
        Mockito.when(sessionManager.openSession()).thenReturn(session);
    }

    @Test
    public void getAmount() {
        User user = new User(1L, "user", "123", Set.of());
        double expectedAmount = 100.;
        Household budget = new Household(1L, expectedAmount, user);

        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        double actualAmount = householdService.getAmount(user.getId());

        Assertions.assertEquals(expectedAmount, actualAmount);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
    }

    @Test
    public void getAmount_budgetNotFound_throwsException() {
        User user = new User(1L, "user", "123", Set.of());

        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            householdService.getAmount(user.getId());
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
    }

    @Test
    public void processIncreaseOperation() {
        IncreaseOperationRequest request = new IncreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                OperationCategory.SALARY
        );
        User user = new User(1L, "user", "123", Set.of());
        Household budget = new Household(1L, 1000., user);

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.of(user));
        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        householdService.processIncreaseOperation(request, user.getId());

        Assertions.assertEquals(1100., budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
        Mockito.verify(session).persist(Mockito.any(IncreaseOperation.class));
    }

    @Test
    public void processIncreaseOperation_budgetNotFound_throwsException() {
        IncreaseOperationRequest request = new IncreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                OperationCategory.SALARY
        );
        User user = new User(1L, "user", "123", Set.of());
        Household budget = new Household(1L, 1000., user);

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.of(user));
        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            householdService.processIncreaseOperation(request, user.getId());
        });

        Assertions.assertEquals(1000., budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
        Mockito.verify(session, Mockito.never()).persist(Mockito.any(IncreaseOperation.class));
    }

    @Test
    public void processIncreaseOperation_userNotFound_throwsException() {
        IncreaseOperationRequest request = new IncreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                OperationCategory.SALARY
        );
        User user = new User(1L, "user", "123", Set.of());

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            householdService.processIncreaseOperation(request, user.getId());
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(session, Mockito.never()).persist(Mockito.any(IncreaseOperation.class));
    }

    @Test
    public void processDecreaseOperation() {
        DecreaseOperationRequest request = new DecreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                DecreaseOperationCategory.TRANSPORT
        );
        User user = new User(1L, "user", "123", Set.of());
        Household budget = new Household(1L, 1000., user);

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.of(user));
        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        householdService.processDecreaseOperation(request, user.getId());

        Assertions.assertEquals(900., budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
        Mockito.verify(session).persist(Mockito.any(DecreaseOperation.class));
    }

    @Test
    public void processDecreaseOperation_budgetNotFound_throwsException() {
        DecreaseOperationRequest request = new DecreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                DecreaseOperationCategory.TRANSPORT
        );
        User user = new User(1L, "user", "123", Set.of());
        Household budget = new Household(1L, 1000., user);

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.of(user));
        Mockito.when(hibernateBudgetDAO.getAllByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            householdService.processDecreaseOperation(request, user.getId());
        });

        Assertions.assertEquals(1000., budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(hibernateBudgetDAO).getAllByUserId(session, user.getId());
        Mockito.verify(session, Mockito.never()).persist(Mockito.any(DecreaseOperation.class));
    }

    @Test
    public void processDecreaseOperation_userNotFound_throwsException() {
        DecreaseOperationRequest request = new DecreaseOperationRequest(
                "token",
                100.,
                Instant.now(),
                DecreaseOperationCategory.TRANSPORT
        );
        User user = new User(1L, "user", "123", Set.of());

        Mockito.when(session.beginTransaction()).thenReturn(transaction);
        Mockito.when(hibernateUserDAO.findById(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            householdService.processDecreaseOperation(request, user.getId());
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateUserDAO).findById(session, user.getId());
        Mockito.verify(session, Mockito.never()).persist(Mockito.any(DecreaseOperation.class));
    }
}
