package com.craete.vault.Domain.ProjectPictures.Entities;

import java.util.UUID;

import com.craete.vault.Domain.Projects.Entities.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "project_pictures",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_project_picture_order",
            columnNames = {"project_id", "picture_order"}
        )
    }
)
public class ProjectPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "picture_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    @Column(name = "storage_key", nullable = false, length = 512)
    @NotNull
    private String storageKey;

    @Column(name = "picture_order", nullable = false)
    @PositiveOrZero
    private int order;

    @Column(name = "picture_caption", nullable = true)
    @NotEmpty
    private String caption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectPicture other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
