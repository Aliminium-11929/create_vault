package com.craete.vault.Infrastructure.ComponentPictures.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ComponentPictures.Entities.ComponentPicture;

public interface ComponentPictureRepository extends JpaRepository<ComponentPicture, UUID>{

}
