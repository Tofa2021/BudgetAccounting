package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.TransactionManager;
import org.example.domain.dao.CategoryDAO;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.exception.already_exists.CategoryAlreadyExistsException;
import org.example.domain.exception.not_found.CategoryNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.model.Category;
import org.example.domain.model.Household;
import org.example.domain.model.OperationType;
import org.example.dto.request.category.CreateCategoryRequest;
import org.example.dto.request.category.DeleteCategoryRequest;
import org.example.dto.request.category.GetCategoriesRequest;
import org.example.dto.request.category.UpdateCategoryRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CategoryService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final CategoryDAO categoryDAO;
    private final HouseholdDAO householdDAO;

    public Category create(CreateCategoryRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Household household = householdDAO.findById(request.getHouseholdId())
                    .orElseThrow(() -> new HouseholdNotFoundException(request.getHouseholdId()));

            if (categoryDAO.existsByHouseholdIdAndName(request.getHouseholdId(), request.getName())) {
                throw new CategoryAlreadyExistsException(request.getName());
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setType(OperationType.fromString(request.getType()));
            category.setHousehold(household);

            log.info("Category created");
            return categoryDAO.save(category);
        });
    }

    public List<Category> getAll(GetCategoriesRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Long householdId = request.getHouseholdId();

            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdId(householdId);
        });
    }

    public List<Category> getIncomeCategory(GetCategoriesRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Long householdId = request.getHouseholdId();

            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdIdAndType(householdId, OperationType.INCOME);
        });
    }

    public List<Category> getExpenseCategory(GetCategoriesRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Long householdId = request.getHouseholdId();

            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdIdAndType(householdId, OperationType.EXPENSE);
        });
    }

    public void update(UpdateCategoryRequest request) {
        Category category = categoryDAO.findById(request.getId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getId()));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryDAO.existsByHouseholdIdAndName(category.getHousehold().getId(), request.getName())) {
                throw new CategoryAlreadyExistsException(request.getName());
            }
            category.setName(request.getName());
        }

        if (request.getType() != null) {
            category.setType(OperationType.fromString(request.getType()));
        }

        categoryDAO.save(category);
    }

    public void delete(DeleteCategoryRequest request) {
        transactionManager.executeInTransaction(() -> {
            Category category = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

            categoryDAO.delete(category);
        });
    }
}
