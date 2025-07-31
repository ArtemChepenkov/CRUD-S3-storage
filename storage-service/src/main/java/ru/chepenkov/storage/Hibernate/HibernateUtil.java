package ru.chepenkov.storage.Hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("default");

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
