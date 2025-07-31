package ru.chepenkov.storage.Entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_files")
public class UserFileEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    public UserFileEntity() {}

    public UserFileEntity(UUID id, String userId, String originalFilename, String storedFilename) {
        this.id = id;
        this.userId = userId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.uploadedAt = Instant.now();
    }

}
