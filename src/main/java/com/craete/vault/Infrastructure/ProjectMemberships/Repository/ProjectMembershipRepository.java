package com.craete.vault.Infrastructure.ProjectMemberships.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID>{

}
