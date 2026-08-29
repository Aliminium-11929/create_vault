package com.craete.vault.Infrastructure.ProjectMemberships.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID>{
    List<ProjectMembership> findByProjectId(UUID projectId);
    List<ProjectMembership> findByUserId(Long userId);
    void deleteAllByProjectId(UUID projectId);
    void deleteAllByUserId(Long userId);
}
