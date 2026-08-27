package com.craete.vault.Infrastructure.ProjectPictures.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ProjectPictures.Entities.ProjectPicture;

public interface ProjectPictureRepository extends JpaRepository<ProjectPicture, UUID>{

}
