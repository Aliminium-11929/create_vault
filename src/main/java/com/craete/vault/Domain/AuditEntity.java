package com.craete.vault.Domain;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditEntity {

    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "modified_on", nullable = false)
    private Instant modifiedOn;

    @Version
    @Column(name = "record_version", nullable = false)
    private Long recordVersion = 0L;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @Column(name = "deleted_on")
    private Instant deletedOn;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = Instant.now();
        }
        if (modifiedOn == null) {
            modifiedOn = createdOn;
        }
        if (recordVersion == null) {
            recordVersion = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = Instant.now();
    }
}
