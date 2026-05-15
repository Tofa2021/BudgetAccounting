package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.CategoryDAO;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.PersistenceManager;
import org.example.domain.exception.already_exists.CategoryAlreadyExistsException;
import org.example.domain.exception.not_found.CategoryNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.model.Category;
import org.example.domain.model.Household;
import org.example.domain.model.HouseholdMemberRole;
import org.example.domain.model.OperationType;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final PersistenceManager persistenceManager;
    private final HouseholdPermissionChecker householdPermissionChecker;
    private final CategoryDAO categoryDAO;
    private final HouseholdDAO householdDAO;

    public Category create(Long householdId, String name, String type, Long userId) {
        log.debug("Creating category with household = {} name = {} type = {}", householdId, name, type);

        return persistenceManager.executeTransaction(() -> {
            householdPermissionChecker.checkRole(householdId, userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

            if (categoryDAO.existsByHouseholdIdAndName(householdId, name)) {
                throw new CategoryAlreadyExistsException(name);
            }

            Category category = new Category();
            category.setName(name);
            category.setType(OperationType.fromString(type));
            category.setHousehold(household);
            categoryDAO.save(category);

            log.info("Category created with id = {} householdId = {} name = {} type = {}", category.getId(), householdId, name, type);
            return category;
        });
    }

    public List<Category> getAllByHouseholdId(Long householdId, Long userId) {
        log.debug("Getting categories with householdId = {}", householdId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkMembership(householdId, userId);

            List<Category> categories = categoryDAO.findAllByHouseholdIdWithRelations(householdId);
            log.debug("Categories gotten householdId = {} count = {}", householdId, categories.size());
            return categories;
        });
    }

    public List<Category> getIncomeCategories(Long householdId, Long userId) {
        log.debug("Getting categories with householdId = {} type = INCOME", householdId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkMembership(householdId, userId);

            List<Category> categories = categoryDAO.findAllByHouseholdIdAndTypeWithRelations(householdId, OperationType.INCOME);
            log.debug("Categories gotten with householdId = {} type = INCOME count = {}", householdId, categories.size());
            return categories;
        });
    }

    public List<Category> getExpenseCategories(Long householdId, Long userId) {
        log.debug("Getting categories with householdId = {} type = EXPENSE", householdId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkMembership(householdId, userId);

            List<Category> categories = categoryDAO.findAllByHouseholdIdAndTypeWithRelations(householdId, OperationType.EXPENSE);
            log.debug("Categories gotten with householdId = {} type = EXPENSE count = {}", householdId, categories.size());
            return categories;
        });
    }

    public void update(Long id, String newName, String newType, Long userId) {
        log.debug("Updating category with id = {} newName = {} newType = {}", id, newName, newType);

        persistenceManager.executeTransaction(() -> {
            Category category = categoryDAO.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));

            householdPermissionChecker.checkRole(category.getHousehold().getId(), userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            String oldName = category.getName();
            OperationType oldType = category.getType();

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
            log.info("Category updated with id = {} oldName = {} newName = {} oldType = {} newType = {}", id, oldName, newName, oldType, newType);
        });
    }

    public void delete(Long id, Long userId) {
        log.debug("Deleting category with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            Category category = categoryDAO.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));

            householdPermissionChecker.checkRole(category.getHousehold().getId(), userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            categoryDAO.deleteById(id);
            log.info("Category deleted with id = {}", id);
        });
    }
}
