package org.example.infrastructure.dao;

import org.hibernate.Session;
import org.hibernate.query.CommonQueryContract;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.Map;

public class HqlQueryBuilder<T> {
    private final Class<T> resultClass;
    private final Map<String, Object> parameters = new HashMap<>();
    private final StringBuilder hql = new StringBuilder();

    private HqlQueryBuilder(Class<T> resultClass) {
        this.resultClass = resultClass;
    }

    public static <T> HqlQueryBuilder<T> builder(Class<T> resultClass) {
        return new HqlQueryBuilder<>(resultClass);
    }

    public HqlQueryBuilder<T> select(String tableName) {
        hql.append("SELECT o FROM ").append(tableName).append(" o");
        return this;
    }

    public HqlQueryBuilder<T> select() {
        hql.append("SELECT o FROM ").append(resultClass.getSimpleName()).append(" o");
        return this;
    }

    public HqlQueryBuilder<T> selectCount() {
        hql.append("SELECT COUNT(o) FROM ").append(resultClass.getSimpleName()).append(" o");
        return this;
    }

    public HqlQueryBuilder<T> delete() {
        hql.append("DELETE FROM ").append(resultClass.getSimpleName()).append(" o");
        return this;
    }

    public HqlQueryBuilder<T> where(String columnName, String operation, Object value) {
        if (value == null) {
            return this;
        }

        String placeholder = generatePlaceholder(columnName);

        hql.append(" WHERE")
                .append(" o.").append(columnName).append(" ")
                .append(operation)
                .append(" :").append(placeholder);
        parameters.put(placeholder, value);
        return this;
    }

    private String generatePlaceholder(String columnName) {
        return columnName.replace('.', '_') + parameters.size();
    }

    public HqlQueryBuilder<T> and(String columnName, String operation, Object value) {
        if (value == null) {
            return this;
        }

        String placeholder = generatePlaceholder(columnName);

        hql.append(" AND")
                .append(" o.").append(columnName).append(" ")
                .append(operation)
                .append(" :").append(placeholder);
        parameters.put(placeholder, value);
        return this;
    }

    public Query<T> build(Session session) {
        System.out.println(hql);
        var query = session.createQuery(hql.toString(), resultClass);
        applyParameters(query);

        return query;
    }

    public Query<Long> buildCount(Session session) {
        System.out.println(hql);
        String queryString = hql.toString();

        var query = session.createQuery(queryString, Long.class);
        applyParameters(query);
        return query;
    }

    private void applyParameters(CommonQueryContract query) {
        for (var parameter : parameters.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
        }
    }

    public MutationQuery buildMutation(Session session) {
        System.out.println(hql);
        var query = session.createMutationQuery(hql.toString());
        for (var parameter : parameters.entrySet()) {
            System.out.println(parameter.getKey() + " " + parameter.getValue());
            query.setParameter(parameter.getKey(), parameter.getValue());
        }

        return query;
    }
}
