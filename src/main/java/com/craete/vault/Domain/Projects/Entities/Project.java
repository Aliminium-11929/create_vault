package com.craete.vault.Domain.Projects.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.craete.vault.Domain.Fields.Entities.Field;
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
import jakarta.persistence.OneToOne;
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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_id", updatable = false, nullable = false)
    private UUID projectId;

    @NotBlank
    private String projectTitle;

    @Column(name = "project_description", nullable = true, columnDefinition = "TEXT")
    private String projectDescription;

    @Column(name = "academic_year", nullable = false)
    @Min(2000)
    @Max(2100)
    private int academicYear;

    @OneToOne(optional = true)
    @JoinColumn(name = "tutor_id", nullable = true, updatable = true)
    private User tutor;

    @OneToOne(optional = false)
    @JoinColumn(name = "supervisor_id", nullable = false, updatable = true)
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Field field;

    @OneToMany(
        mappedBy = "project",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("pictureOrder ASC")
    private List<ProjectPicture> pictures = new ArrayList<>();

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
        return projectId != null && projectId.equals(other.projectId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
