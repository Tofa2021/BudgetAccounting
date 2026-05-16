package org.example.infrastructure.dao;

import org.example.domain.dao.HouseholdDAO;
import org.example.domain.model.Account;
import org.example.domain.model.Category;
import org.example.domain.model.Household;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.*;
import java.util.stream.Collectors;

public class HibernateHouseholdDAO extends HibernateDAO<Household, Long> implements HouseholdDAO {
    public HibernateHouseholdDAO(HibernatePersistenceManager transactionManager) {
        super(Household.class, transactionManager);
    }

    @Override
    public List<Household> getAllByUserId(Long userId) {
        // 1. Загружаем Household без коллекций
        String householdHql = "SELECT DISTINCT h FROM Household h " +
                "JOIN h.members m " +
                "WHERE m.user.id = :userId";

        List<Household> households = getCurrentSession()
                .createQuery(householdHql, Household.class)
                .setParameter("userId", userId)
                .list();

        if (households.isEmpty()) {
            return households;
        }

        // Получаем ID всех household
        List<Long> householdIds = households.stream()
                .map(Household::getId)
                .collect(Collectors.toList());

        // 2. Загружаем members для всех household
        String membersHql = "SELECT DISTINCT h FROM Household h " +
                "JOIN FETCH h.members m " +
                "WHERE h.id IN (:ids)";

        List<Household> householdsWithMembers = getCurrentSession()
                .createQuery(membersHql, Household.class)
                .setParameter("ids", householdIds)
                .list();

        // 3. Загружаем accounts для всех household
        String accountsHql = "SELECT DISTINCT h FROM Household h " +
                "LEFT JOIN FETCH h.accounts a " +
                "WHERE h.id IN (:ids)";

        List<Household> householdsWithAccounts = getCurrentSession()
                .createQuery(accountsHql, Household.class)
                .setParameter("ids", householdIds)
                .list();

        // 4. Загружаем categories для всех household
        String categoriesHql = "SELECT DISTINCT h FROM Household h " +
                "LEFT JOIN FETCH h.categories c " +
                "WHERE h.id IN (:ids)";

        List<Household> householdsWithCategories = getCurrentSession()
                .createQuery(categoriesHql, Household.class)
                .setParameter("ids", householdIds)
                .list();

        // Объединяем результаты (используем Map)
        Map<Long, Household> householdMap = new HashMap<>();

        // Добавляем households с members
        for (Household h : householdsWithMembers) {
            householdMap.put(h.getId(), h);
        }

        // Добавляем accounts
        for (Household h : householdsWithAccounts) {
            Household existing = householdMap.get(h.getId());
            if (existing != null) {
                existing.setAccounts(h.getAccounts());
            } else {
                householdMap.put(h.getId(), h);
            }
        }

        // Добавляем categories
        for (Household h : householdsWithCategories) {
            Household existing = householdMap.get(h.getId());
            if (existing != null) {
                existing.setCategories(h.getCategories());
            } else {
                householdMap.put(h.getId(), h);
            }
        }

        return new ArrayList<>(householdMap.values());
    }

    @Override
    public Optional<Household> findByIdWithRelations(Long id) {
        Household household = HqlQueryBuilder.builder(Household.class)
                .selectDistinct()
                .leftJoinFetch("members")
                .where("id", "=", id)
                .build(getCurrentSession())
                .uniqueResult();

        if (household == null) {
            return Optional.empty();
        }

        List<Account> accounts = HqlQueryBuilder
                .builder(Account.class)
                .select()
                .where("household.id", "=", id)
                .build(getCurrentSession())
                .list();
        household.setAccounts(accounts);

        List<Category> categories = HqlQueryBuilder
                .builder(Category.class)
                .select()
                .where("household.id", "=", id)
                .build(getCurrentSession())
                .list();
        household.setCategories(categories);

        return Optional.of(household);
    }
}
