package ru.chepenkov.storage.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import ru.chepenkov.storage.Entity.UserFileEntity;

import java.util.Optional;

public class UserFileRepository {

    private final EntityManager entityManager;

    public UserFileRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(UserFileEntity entity) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.persist(entity);
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction.isActive()) entityTransaction.rollback();
            throw e;
        }
    }

    public Optional<UserFileEntity> findByUserIdAndOriginalFilename(String userId, String filename) {
        TypedQuery<UserFileEntity> query = entityManager.createQuery(
                "SELECT u FROM UserFileEntity u WHERE u.userId = :userId AND u.originalFilename = :filename",
                UserFileEntity.class
        );
        query.setParameter("userId", userId);
        query.setParameter("filename", filename);
        return query.getResultStream().findFirst();
    }
}