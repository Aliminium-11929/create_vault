package com.craete.vault.Domain.Projects.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Audited.Table;

import com.craete.vault.Domain.AuditEntity;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Domain.ProjectPictures.Entities.ProjectPicture;
import com.craete.vault.Domain.Users.Entities.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "project_title", nullable = false, length = 300)
    private String title;

    @Column(name = "project_description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(name = "academic_year", nullable = false)
    @Min(2000)
    @Max(2100)
    private int academicYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tutor_id", nullable = true)
    private User tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    @NotNull
    private Field field;

    @OneToMany(
        mappedBy = "project",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("pictureOrder ASC")
    private List<ProjectPicture> pictures = new ArrayList<>();

    @OneToMany(
        mappedBy = "project",
        orphanRemoval = true,
        cascade = CascadeType.ALL
    )
    private List<ProjectMembership> projectMemberships = new ArrayList<>();

    public void addPicture(ProjectPicture picture) {
            pictures.add(picture);
            picture.setProject(this);
    }

    public void removePicture(ProjectPicture picture) {
        pictures.remove(picture);
        picture.setProject(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
