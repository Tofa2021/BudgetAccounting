package org.example.service;

import org.example.application.service.OperationService;
import org.example.domain.SessionManager;
import org.example.domain.exception.BudgetNotFoundException;
import org.example.domain.exception.OperationNotFoundException;
import org.example.domain.model.*;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.RequestAction;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;
import org.example.infrastructure.dao.HibernateBudgetDAO;
import org.example.infrastructure.dao.HibernateOperationDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class OperationServiceMockTest {
    @InjectMocks
    private OperationService operationService;

    @Mock
    private HibernateOperationDAO hibernateOperationDAO;
    @Mock
    private HibernateBudgetDAO hibernateBudgetDAO;
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
    public void getAllByUser() {
        User user = new User(1L, "user", "123", Set.of());
        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findAllByUserId(session, user.getId())).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getAllByUserId(user.getId());

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
        Mockito.verify(hibernateOperationDAO).findAllByUserId(session, user.getId());
    }

    @Test
    public void getRecentOperations() {
        IntegerAuthorizedRequest request = new IntegerAuthorizedRequest(RequestAction.GET_RECENT_OPERATIONS, "token", 7);

        Instant fixedNow = Instant.parse("2026-03-11T10:00:00Z");

        User user = new User(1L, "user", "123", Set.of());
        Operation operation1 = new IncreaseOperation(1L, 100., fixedNow, user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., fixedNow, user, DecreaseOperationCategory.FOOD);

        try (MockedStatic<Instant> instantMock = Mockito.mockStatic(Instant.class)) {
            instantMock.when(Instant::now).thenReturn(fixedNow);
            Instant expectedCutoff = fixedNow.minus(request.getInteger(), ChronoUnit.DAYS);
            Mockito.when(hibernateOperationDAO.findRecentOperations(
                    session,
                    user.getId(),
                    expectedCutoff
            )).thenReturn(List.of(operation1, operation2));

            List<Operation> actualOperations = operationService.getRecentOperations(user.getId(), request);

            Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
            Mockito.verify(hibernateOperationDAO).findRecentOperations(session, user.getId(), expectedCutoff);
        }

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
    }

    @Test
    public void getRecentOperations_noOperations_returnEmptyList() {
        IntegerAuthorizedRequest request = new IntegerAuthorizedRequest(RequestAction.GET_RECENT_OPERATIONS, "token", 7);
        Instant fixedNow = Instant.parse("2026-03-11T10:00:00Z");
        User user = new User(1L, "user", "123", Set.of());

        try (MockedStatic<Instant> instantMock = Mockito.mockStatic(Instant.class)) {
            instantMock.when(Instant::now).thenReturn(fixedNow);
            Instant expectedCutoff = fixedNow.minus(request.getInteger(), ChronoUnit.DAYS);
            Mockito.when(hibernateOperationDAO.findRecentOperations(
                    session,
                    user.getId(),
                    expectedCutoff
            )).thenReturn(List.of());

            List<Operation> actualOperations = operationService.getRecentOperations(user.getId(), request);

            Assertions.assertTrue(actualOperations.isEmpty());
            Mockito.verify(hibernateOperationDAO).findRecentOperations(session, user.getId(), expectedCutoff);
        }

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
    }

    @Test
    public void deleteById_increaseOperation() {
        AuthorizedModelIdRequest request = new AuthorizedModelIdRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        double expectedAmount = 900.;
        Budget budget = new Budget(1L, 1000., user);
        Operation operation = new IncreaseOperation(1L, 100., Instant.now(), user, IncreaseOperationCategory.SALARY);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.deleteById(request);

        Assertions.assertEquals(expectedAmount, budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();
        Mockito.verify(hibernateOperationDAO).findById(session, request.getModelId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
        Mockito.verify(hibernateOperationDAO).deleteById(session, request.getModelId());
    }

    @Test
    public void deleteById_decreaseOperation() {
        AuthorizedModelIdRequest request = new AuthorizedModelIdRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        double expectedAmount = 1100.;
        Budget budget = new Budget(1L, 1000., user);
        Operation operation = new DecreaseOperation(1L, 100., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.deleteById(request);

        Assertions.assertEquals(expectedAmount, budget.getAmount());
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();
        Mockito.verify(hibernateOperationDAO).findById(session, request.getModelId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
        Mockito.verify(hibernateOperationDAO).deleteById(session, request.getModelId());
    }

    @Test
    public void deleteById_operationNotFound_throwsException() {
        AuthorizedModelIdRequest request = new AuthorizedModelIdRequest(1L, RequestAction.DELETE_OPERATION, "token");

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.empty());

        Assertions.assertThrows(OperationNotFoundException.class, () -> {
            operationService.deleteById(request);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(hibernateOperationDAO).findById(session, request.getModelId());
    }

    @Test
    public void deleteById_budgetNotFound_throwsException() {
        AuthorizedModelIdRequest request = new AuthorizedModelIdRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        Operation operation = new DecreaseOperation(1L, 100., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            operationService.deleteById(request);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(hibernateOperationDAO).findById(session, request.getModelId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
    }

    @Test
    public void update_increaseOperation_increaseBudget() {
        User user = new User(1L, "user", "123", Set.of());
        IncreaseOperation existingOperation = new IncreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                IncreaseOperationCategory.BONUS
        );
        Budget budget = new Budget(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                IncreaseOperationCategory.SALARY
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.update(request);

        Assertions.assertEquals(110., existingOperation.getAmount());
        Assertions.assertEquals(updateOperation.getCategory(), existingOperation.getCategory());
        Assertions.assertEquals(110., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());

        Mockito.verify(hibernateOperationDAO).update(session, existingOperation);
    }

    @Test
    public void update_increaseOperation_decreaseBudget() {
        User user = new User(1L, "user", "123", Set.of());
        IncreaseOperation existingOperation = new IncreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                IncreaseOperationCategory.BONUS
        );
        Budget budget = new Budget(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                90.,
                Instant.now(),
                user.getId(),
                IncreaseOperationCategory.SALARY
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.update(request);

        Assertions.assertEquals(90., existingOperation.getAmount());
        Assertions.assertEquals(updateOperation.getCategory(), existingOperation.getCategory());
        Assertions.assertEquals(90., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());

        Mockito.verify(hibernateOperationDAO).update(session, existingOperation);
    }

    @Test
    public void update_decreaseOperation_decreaseBudget() {
        User user = new User(1L, "user", "123", Set.of());
        DecreaseOperation existingOperation = new DecreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                DecreaseOperationCategory.FOOD
        );
        Budget budget = new Budget(1L, 100., user);

        DecreaseOperationDTO updateOperation = new DecreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                DecreaseOperationCategory.TRANSPORT
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.update(request);

        Assertions.assertEquals(110., existingOperation.getAmount());
        Assertions.assertEquals(updateOperation.getCategory(), existingOperation.getCategory());
        Assertions.assertEquals(90., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());

        Mockito.verify(hibernateOperationDAO).update(session, existingOperation);
    }

    @Test
    public void update_decreaseOperation_increaseBudget() {
        User user = new User(1L, "user", "123", Set.of());
        DecreaseOperation existingOperation = new DecreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                DecreaseOperationCategory.FOOD
        );
        Budget budget = new Budget(1L, 100., user);

        DecreaseOperationDTO updateOperation = new DecreaseOperationDTO(
                1L,
                90.,
                Instant.now(),
                user.getId(),
                DecreaseOperationCategory.TRANSPORT
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.update(request);

        Assertions.assertEquals(90., existingOperation.getAmount());
        Assertions.assertEquals(updateOperation.getCategory(), existingOperation.getCategory());
        Assertions.assertEquals(110., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());

        Mockito.verify(hibernateOperationDAO).update(session, existingOperation);
    }

    @Test
    public void update_operationNotFound_throwsException() {
        User user = new User(1L, "user", "123", Set.of());
        IncreaseOperation existingOperation = new IncreaseOperation(1L, 100., Instant.now(), user, IncreaseOperationCategory.BONUS);
        IncreaseOperationCategory exceptedCategory = existingOperation.getCategory();
        Budget budget = new Budget(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(1L, 110., Instant.now(), user.getId(), IncreaseOperationCategory.SALARY);
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId()))
                .thenThrow(new OperationNotFoundException(updateOperation.getId()));

        Assertions.assertThrows(OperationNotFoundException.class, () -> {
            operationService.update(request);
        });

        Assertions.assertEquals(100., existingOperation.getAmount());
        Assertions.assertEquals(exceptedCategory, existingOperation.getCategory());
        Assertions.assertEquals(100., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
    }

    @Test
    public void update_budgetNotFound_throwsException() {
        User user = new User(1L, "user", "123", Set.of());
        DecreaseOperation existingOperation = new DecreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                DecreaseOperationCategory.FOOD
        );
        DecreaseOperationCategory expectedCategory = existingOperation.getCategory();
        Budget budget = new Budget(1L, 100., user);

        DecreaseOperationDTO updateOperation = new DecreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                DecreaseOperationCategory.TRANSPORT
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            operationService.update(request);
        });

        Assertions.assertEquals(100., existingOperation.getAmount());
        Assertions.assertEquals(expectedCategory, existingOperation.getCategory());
        Assertions.assertEquals(100., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
    }

    @Test
    public void update_changeOperationTypeToIncrease_throwsException() {
        User user = new User(1L, "user", "123", Set.of());
        DecreaseOperation existingOperation = new DecreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                DecreaseOperationCategory.FOOD
        );
        DecreaseOperationCategory expectedCategory = existingOperation.getCategory();
        Budget budget = new Budget(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                IncreaseOperationCategory.SALARY
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        Assertions.assertThrows(RuntimeException.class, () -> {
            operationService.update(request);
        });

        Assertions.assertEquals(100., existingOperation.getAmount());
        Assertions.assertEquals(expectedCategory, existingOperation.getCategory());
        Assertions.assertEquals(100., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction).rollback();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
    }

    @Test
    public void update_changeOperationTypeToDecrease_throwsException() {
        User user = new User(1L, "user", "123", Set.of());
        IncreaseOperation existingOperation = new IncreaseOperation(
                1L,
                100.,
                Instant.now(),
                user,
                IncreaseOperationCategory.SALARY
        );
        IncreaseOperationCategory expectedCategory = existingOperation.getCategory();
        Budget budget = new Budget(1L, 100., user);

        DecreaseOperationDTO updateOperation = new DecreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                DecreaseOperationCategory.TRANSPORT
        );
        UpdateRequest<OperationDTO> request = new UpdateRequest<>(RequestAction.UPDATE_OPERATION, "token", updateOperation);

        Mockito.when(hibernateOperationDAO.findById(session, updateOperation.getId())).thenReturn(Optional.of(existingOperation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        Assertions.assertThrows(RuntimeException.class, () -> {
            operationService.update(request);
        });

        Assertions.assertEquals(100., existingOperation.getAmount());
        Assertions.assertEquals(expectedCategory, existingOperation.getCategory());
        Assertions.assertEquals(100., budget.getAmount());

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction).rollback();

        Mockito.verify(hibernateOperationDAO).findById(session, updateOperation.getId());
        Mockito.verify(hibernateBudgetDAO).findByUserId(session, user.getId());
    }

    @Test
    public void getFilteredOperations_allOperations() {
        User user = new User(1L, "user", "123", Set.of());

        OperationFilterRequest request = new OperationFilterRequest(
                "token",
                null,
                null,
                null,
                null
        );

        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                session, user.getId(),
                null,
                null,
                null,
                null
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), null, null, null, null
        );
    }

    @Test
    public void getFilteredOperations_increaseOperations_withCategory() {
        User user = new User(1L, "user", "123", Set.of());

        Instant dateFrom = Instant.parse("2024-01-01T00:00:00Z");
        Instant dateTo = Instant.parse("2024-12-31T23:59:59Z");

        IncreaseOperationFilterRequest request = new IncreaseOperationFilterRequest(
                "token",
                500.,
                100.,
                dateFrom,
                dateTo,
                IncreaseOperationCategory.SALARY
        );

        Operation operation1 = new IncreaseOperation(1L, 200.,
                Instant.parse("2024-03-10T15:30:00Z"), user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new IncreaseOperation(2L, 300.,
                Instant.parse("2024-07-20T09:15:00Z"), user, IncreaseOperationCategory.SALARY);

        Operation operation3 = new IncreaseOperation(3L, 250.,
                Instant.parse("2023-12-31T23:59:00Z"), user, IncreaseOperationCategory.SALARY);
        Operation operation4 = new IncreaseOperation(4L, 350.,
                Instant.parse("2025-01-01T00:00:00Z"), user, IncreaseOperationCategory.SALARY);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                session,
                user.getId(),
                100.,
                500.,
                dateFrom,
                dateTo,
                IncreaseOperationCategory.SALARY
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
        Assertions.assertFalse(actualOperations.contains(operation3));
        Assertions.assertFalse(actualOperations.contains(operation4));

        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), 100., 500., dateFrom, dateTo, IncreaseOperationCategory.SALARY
        );
    }

    @Test
    public void getFilteredOperations_increaseOperations_withoutCategory() {
        User user = new User(1L, "user", "123", Set.of());

        Instant dateFrom = Instant.parse("2024-01-01T00:00:00Z");
        Instant dateTo = Instant.parse("2024-12-31T23:59:59Z");

        IncreaseOperationFilterRequest request = new IncreaseOperationFilterRequest(
                "token",
                500.,
                100.,
                dateFrom,
                dateTo,
                null
        );

        Operation operation1 = new IncreaseOperation(1L, 200., Instant.now(), user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new IncreaseOperation(2L, 300., Instant.now(), user, IncreaseOperationCategory.BONUS);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(100.),
                Mockito.eq(500.),
                Mockito.eq(dateFrom),
                Mockito.eq(dateTo),
                Mockito.eq(null)
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), 100., 500., dateFrom, dateTo, (IncreaseOperationCategory) null
        );
    }

    @Test
    public void getFilteredOperations_decreaseOperations_withCategory() {
        User user = new User(1L, "user", "123", Set.of());

        Instant dateFrom = Instant.parse("2024-01-01T00:00:00Z");
        Instant dateTo = Instant.parse("2024-12-31T23:59:59Z");

        DecreaseOperationFilterRequest request = new DecreaseOperationFilterRequest(
                "token",
                100.,
                10.,
                dateFrom,
                dateTo,
                DecreaseOperationCategory.FOOD
        );

        Operation operation1 = new DecreaseOperation(1L, 20., Instant.now(), user, DecreaseOperationCategory.FOOD);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                session,
                user.getId(),
                10.,
                100.,
                dateFrom,
                dateTo,
                DecreaseOperationCategory.FOOD
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), 10., 100., dateFrom, dateTo, DecreaseOperationCategory.FOOD
        );
    }

    @Test
    public void getFilteredOperations_decreaseOperations_withoutCategory() {
        User user = new User(1L, "user", "123", Set.of());

        Instant dateFrom = Instant.parse("2024-01-01T00:00:00Z");
        Instant dateTo = Instant.parse("2024-12-31T23:59:59Z");

        DecreaseOperationFilterRequest request = new DecreaseOperationFilterRequest(
                "token",
                100.,
                10.,
                dateFrom,
                dateTo,
                null
        );

        Operation operation1 = new DecreaseOperation(1L, 20., Instant.now(), user, DecreaseOperationCategory.FOOD);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.TRANSPORT);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(10.),
                Mockito.eq(100.),
                Mockito.eq(dateFrom),
                Mockito.eq(dateTo),
                Mockito.eq(null)
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), 10., 100., dateFrom, dateTo, (DecreaseOperationCategory) null
        );
    }

    @Test
    public void getFilteredOperations_withNullParameters() {
        User user = new User(1L, "user", "123", Set.of());

        OperationFilterRequest request = new OperationFilterRequest(
                "token",
                null,
                null,
                null,
                null
        );

        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, IncreaseOperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null)
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(hibernateOperationDAO).findFilteredOperations(
                session, user.getId(), null, null, null, null
        );
    }

    @Test
    public void getFilteredOperations_emptyResult() {
        User user = new User(1L, "user", "123", Set.of());

        IncreaseOperationFilterRequest request = new IncreaseOperationFilterRequest(
                "token",
                2000., 1000.,
                null,
                null,
                IncreaseOperationCategory.SALARY
        );

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(1000.),
                Mockito.eq(2000.),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(IncreaseOperationCategory.SALARY)
        )).thenReturn(List.of());

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertTrue(actualOperations.isEmpty());
    }

    @Test
    public void getFilteredOperations_databaseError_throwsException() {
        User user = new User(1L, "user", "123", Set.of());

        OperationFilterRequest request = new OperationFilterRequest(
                "token",
                null,
                null,
                null,
                null
        );

        Mockito.when(hibernateOperationDAO.findFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null)
        )).thenThrow(new RuntimeException("Database error"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            operationService.getFilteredOperations(user.getId(), request);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(transaction).rollback();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
    }
}
