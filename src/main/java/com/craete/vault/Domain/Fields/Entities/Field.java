package com.craete.vault.Domain.Fields.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.craete.vault.Domain.AuditEntity;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fields")
public class Field extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull
    @Column(name = "field_id")
    private UUID Id;

    @NotBlank
    @Column(name = "field_name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("academicYear ASC")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<User> users = new ArrayList<>();
}
