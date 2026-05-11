package org.example.infrastructure.dao;

import org.example.domain.dao.HouseholdDAO;
import org.example.domain.model.Account;
import org.example.domain.model.Category;
import org.example.domain.model.Household;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;
import java.util.Optional;

public class HibernateHouseholdDAO extends HibernateDAO<Household, Long> implements HouseholdDAO {
    public HibernateHouseholdDAO(HibernatePersistenceManager transactionManager) {
        super(Household.class, transactionManager);
    }

    @Override
    public List<Household> findByUserId(Long userId) {
        return HqlQueryBuilder.builder(Household.class)
                .select()
                .where("members.user.id", "=", userId)
                .build(getCurrentSession())
                .list();
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
