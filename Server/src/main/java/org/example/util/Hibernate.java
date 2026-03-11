package org.example.util;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Getter
public class Hibernate implements SessionBuilder {
    private final SessionFactory sessionFactory = buildSessionFactory();

    private SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("SessionFactory creation failed" + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
