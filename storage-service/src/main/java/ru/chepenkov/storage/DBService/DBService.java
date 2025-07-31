package ru.chepenkov.storage.DBService;

import jakarta.persistence.EntityManager;
import ru.chepenkov.storage.Entity.UserFileEntity;
import ru.chepenkov.storage.Hibernate.HibernateUtil;
import ru.chepenkov.storage.Repository.UserFileRepository;

import java.util.Optional;
import java.util.UUID;

public class DBService {
    public void saveFileMeta(String userId, String originalFilename, String storedFilename) {
        EntityManager em = HibernateUtil.getEntityManager();
        UserFileRepository repo = new UserFileRepository(em);
        UserFileEntity entity = new UserFileEntity(
                UUID.randomUUID(),
                userId,
                originalFilename,
                storedFilename
        );
        repo.save(entity);
    }

    public Optional<UserFileEntity> getStoredFilename(String userId, String originalFilename) {
        EntityManager em = HibernateUtil.getEntityManager();
        UserFileRepository repo = new UserFileRepository(em);
        return repo.findByUserIdAndOriginalFilename(userId, originalFilename);
    }
}
