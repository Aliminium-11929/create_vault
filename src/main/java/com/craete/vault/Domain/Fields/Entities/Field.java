package com.craete.vault.Domain.Fields.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Audited.Table;

import com.craete.vault.Domain.Projects.Entities.Project;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    @Column(name = "field_id")
    private UUID fieldId;

    @OneToMany(
        mappedBy = "field",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("academicYear ASC")
    private List<Project> projects = new ArrayList<>();

    @NotBlank
    @Column(name = "field_name", nullable = false)
    private String fieldName;
}
