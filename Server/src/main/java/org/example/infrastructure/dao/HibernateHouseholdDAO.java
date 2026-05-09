package org.example.infrastructure.dao;

import org.example.domain.dao.HouseholdDAO;
import org.example.domain.model.Household;
import org.example.infrastructure.transaction.HibernateTransactionManager;

import java.util.List;

public class HibernateHouseholdDAO extends HibernateDAO<Household, Long> implements HouseholdDAO {
    public HibernateHouseholdDAO(HibernateTransactionManager transactionManager) {
        super(Household.class, transactionManager);
    }

    @Override
    public List<Household> findByUserId(Long userId) {
        return HqlQueryBuilder
                .builder(Household.class)
                .select()
                .where("members.user.id", "=", userId)
                .build(getCurrentSession())
                .list();
    }
}
