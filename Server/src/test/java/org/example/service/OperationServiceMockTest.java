package org.example.service;

import org.example.application.service.OperationService;
import org.example.domain.SessionManager;
import org.example.domain.exception.not_found.BudgetNotFoundException;
import org.example.domain.exception.not_found.OperationNotFoundException;
import org.example.domain.model.Household;
import org.example.domain.model.Operation;
import org.example.domain.model.User;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.OperationCategory;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.IntegerAuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.UpdateRequest;
import org.example.dto.request.operation.DeleteOperationRequest;
import org.example.dto.request.operation.OperationFilterRequest;
import org.example.infrastructure.dao.HibernateHouseholdDAO;
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
    private HibernateHouseholdDAO hibernateBudgetDAO;
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
        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, OperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findAllByUserId(session, user.getId())).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getAllUserOperations(user.getId());

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
        Mockito.verify(hibernateOperationDAO).findAllByUserId(session, user.getId());
    }

    @Test
    public void getUserRecentOperations() {
        IntegerAuthorizedRequest request = new IntegerAuthorizedRequest(RequestAction.GET_USER_RECENT_OPERATIONS, "token", 7);

        Instant fixedNow = Instant.parse("2026-03-11T10:00:00Z");

        User user = new User(1L, "user", "123", Set.of());
        Operation operation1 = new IncreaseOperation(1L, 100., fixedNow, user, OperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., fixedNow, user, DecreaseOperationCategory.FOOD);

        try (MockedStatic<Instant> instantMock = Mockito.mockStatic(Instant.class)) {
            instantMock.when(Instant::now).thenReturn(fixedNow);
            Instant expectedCutoff = fixedNow.minus(request.getInteger(), ChronoUnit.DAYS);
            Mockito.when(hibernateOperationDAO.findUserRecentOperations(
                    session,
                    user.getId(),
                    expectedCutoff
            )).thenReturn(List.of(operation1, operation2));

            List<Operation> actualOperations = operationService.getUserRecentOperations(user.getId(), request);

            Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
            Mockito.verify(hibernateOperationDAO).findUserRecentOperations(session, user.getId(), expectedCutoff);
        }

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
    }

    @Test
    public void getUserRecentOperations_noOperations_returnEmptyList() {
        IntegerAuthorizedRequest request = new IntegerAuthorizedRequest(RequestAction.GET_USER_RECENT_OPERATIONS, "token", 7);
        Instant fixedNow = Instant.parse("2026-03-11T10:00:00Z");
        User user = new User(1L, "user", "123", Set.of());

        try (MockedStatic<Instant> instantMock = Mockito.mockStatic(Instant.class)) {
            instantMock.when(Instant::now).thenReturn(fixedNow);
            Instant expectedCutoff = fixedNow.minus(request.getInteger(), ChronoUnit.DAYS);
            Mockito.when(hibernateOperationDAO.findUserRecentOperations(
                    session,
                    user.getId(),
                    expectedCutoff
            )).thenReturn(List.of());

            List<Operation> actualOperations = operationService.getUserRecentOperations(user.getId(), request);

            Assertions.assertTrue(actualOperations.isEmpty());
            Mockito.verify(hibernateOperationDAO).findUserRecentOperations(session, user.getId(), expectedCutoff);
        }

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).close();
    }

    @Test
    public void delete_increaseOperation() {
        DeleteOperationRequest request = new DeleteOperationRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        double expectedAmount = 900.;
        Household budget = new Household(1L, 1000., user);
        Operation operation = new IncreaseOperation(1L, 100., Instant.now(), user, OperationCategory.SALARY);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.delete(request);

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
    public void delete_decreaseOperation() {
        DeleteOperationRequest request = new DeleteOperationRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        double expectedAmount = 1100.;
        Household budget = new Household(1L, 1000., user);
        Operation operation = new DecreaseOperation(1L, 100., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.of(budget));

        operationService.delete(request);

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
    public void delete_operationNotFound_throwsException() {
        DeleteOperationRequest request = new DeleteOperationRequest(1L, RequestAction.DELETE_OPERATION, "token");

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.empty());

        Assertions.assertThrows(OperationNotFoundException.class, () -> {
            operationService.delete(request);
        });

        Mockito.verify(sessionManager).openSession();
        Mockito.verify(session).beginTransaction();
        Mockito.verify(session).close();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(hibernateOperationDAO).findById(session, request.getModelId());
    }

    @Test
    public void delete_budgetNotFound_throwsException() {
        DeleteOperationRequest request = new DeleteOperationRequest(1L, RequestAction.DELETE_OPERATION, "token");
        User user = new User(1L, "user", "123", Set.of());
        Operation operation = new DecreaseOperation(1L, 100., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findById(session, request.getModelId())).thenReturn(Optional.of(operation));
        Mockito.when(hibernateBudgetDAO.findByUserId(session, user.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(BudgetNotFoundException.class, () -> {
            operationService.delete(request);
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
                OperationCategory.BONUS
        );
        Household budget = new Household(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                OperationCategory.SALARY
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
                OperationCategory.BONUS
        );
        Household budget = new Household(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                90.,
                Instant.now(),
                user.getId(),
                OperationCategory.SALARY
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
        Household budget = new Household(1L, 100., user);

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
        Household budget = new Household(1L, 100., user);

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
        IncreaseOperation existingOperation = new IncreaseOperation(1L, 100., Instant.now(), user, OperationCategory.BONUS);
        OperationCategory exceptedCategory = existingOperation.getCategory();
        Household budget = new Household(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(1L, 110., Instant.now(), user.getId(), OperationCategory.SALARY);
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
        Household budget = new Household(1L, 100., user);

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
        Household budget = new Household(1L, 100., user);

        IncreaseOperationDTO updateOperation = new IncreaseOperationDTO(
                1L,
                110.,
                Instant.now(),
                user.getId(),
                OperationCategory.SALARY
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
                OperationCategory.SALARY
        );
        OperationCategory expectedCategory = existingOperation.getCategory();
        Household budget = new Household(1L, 100., user);

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

        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, OperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
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
        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
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
                OperationCategory.SALARY
        );

        Operation operation1 = new IncreaseOperation(1L, 200.,
                Instant.parse("2024-03-10T15:30:00Z"), user, OperationCategory.SALARY);
        Operation operation2 = new IncreaseOperation(2L, 300.,
                Instant.parse("2024-07-20T09:15:00Z"), user, OperationCategory.SALARY);

        Operation operation3 = new IncreaseOperation(3L, 250.,
                Instant.parse("2023-12-31T23:59:00Z"), user, OperationCategory.SALARY);
        Operation operation4 = new IncreaseOperation(4L, 350.,
                Instant.parse("2025-01-01T00:00:00Z"), user, OperationCategory.SALARY);

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
                session,
                user.getId(),
                100.,
                500.,
                dateFrom,
                dateTo,
                OperationCategory.SALARY
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);
        Assertions.assertFalse(actualOperations.contains(operation3));
        Assertions.assertFalse(actualOperations.contains(operation4));

        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
                session, user.getId(), 100., 500., dateFrom, dateTo, OperationCategory.SALARY
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

        Operation operation1 = new IncreaseOperation(1L, 200., Instant.now(), user, OperationCategory.SALARY);
        Operation operation2 = new IncreaseOperation(2L, 300., Instant.now(), user, OperationCategory.BONUS);

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
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

        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
                session, user.getId(), 100., 500., dateFrom, dateTo, (OperationCategory) null
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

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
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

        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
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

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
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

        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
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

        Operation operation1 = new IncreaseOperation(1L, 100., Instant.now(), user, OperationCategory.SALARY);
        Operation operation2 = new DecreaseOperation(2L, 50., Instant.now(), user, DecreaseOperationCategory.FOOD);

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(null)
        )).thenReturn(List.of(operation1, operation2));

        List<Operation> actualOperations = operationService.getFilteredOperations(user.getId(), request);

        Assertions.assertEquals(List.of(operation1, operation2), actualOperations);

        Mockito.verify(hibernateOperationDAO).findUserFilteredOperations(
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
                OperationCategory.SALARY
        );

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
                Mockito.eq(session),
                Mockito.eq(user.getId()),
                Mockito.eq(1000.),
                Mockito.eq(2000.),
                Mockito.eq(null),
                Mockito.eq(null),
                Mockito.eq(OperationCategory.SALARY)
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

        Mockito.when(hibernateOperationDAO.findUserFilteredOperations(
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
