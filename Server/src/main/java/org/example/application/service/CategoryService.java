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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CategoryService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final CategoryDAO categoryDAO;
    private final HouseholdDAO householdDAO;

    public Category create(Long householdId, String name, String type) {
        return transactionManager.executeInTransaction(() -> {
            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

            if (categoryDAO.existsByHouseholdIdAndName(householdId, name)) {
                throw new CategoryAlreadyExistsException(name);
            }

            Category category = new Category();
            category.setName(name);
            category.setType(OperationType.fromString(type));
            category.setHousehold(household);

            log.info("Category created with name = {}", name);
            return categoryDAO.save(category);
        });
    }

    public List<Category> getAll(Long householdId) {
        return transactionManager.executeInTransaction(() -> {
            householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdId(householdId);
        });
    }

    public List<Category> getIncomeCategory(Long householdId) {
        return transactionManager.executeInTransaction(() -> {
            householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdIdAndType(householdId, OperationType.INCOME);
        });
    }

    public List<Category> getExpenseCategory(Long householdId) {
        return transactionManager.executeInTransaction(() -> {
            householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));
            return categoryDAO.findByHouseholdIdAndType(householdId, OperationType.EXPENSE);
        });
    }

    public void update(Long id, String newName, String newType) {
        Category category = categoryDAO.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (newName != null && !newName.equals(category.getName())) {
            if (categoryDAO.existsByHouseholdIdAndName(category.getHousehold().getId(), newName)) {
                throw new CategoryAlreadyExistsException(newName);
            }
            category.setName(newName);
        }

        if (newType != null) {
            category.setType(OperationType.fromString(newType));
        }

        categoryDAO.save(category);
    }

    public void delete(Long id) {
        transactionManager.executeInTransaction(() -> {
            Category category = categoryDAO.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));

            categoryDAO.delete(category);
        });
    }
}
