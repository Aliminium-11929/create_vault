package com.craete.vault.Domain.ComponentPictures.Entities;

import java.util.UUID;

import com.craete.vault.Domain.AuditEntity;
import com.craete.vault.Domain.Components.Entities.Component;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "component_pictures",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_component_picture_order",
            columnNames = {"component_id", "picture_order"}
        )
    }
)
public class ComponentPicture extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "picture_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    @NotNull
    private Component component;

    @Column(name = "storage_key", nullable = false, length = 512)
    @NotNull
    private String storageKey;

    @Column(name = "picture_order", nullable = false)
    @PositiveOrZero
    private int order;

    @Column(name = "picture_caption", nullable = true)
    private String caption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentPicture other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
