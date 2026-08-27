package com.craete.vault.Infrastructure.Projects.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.Projects.Entities.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID>{

}
