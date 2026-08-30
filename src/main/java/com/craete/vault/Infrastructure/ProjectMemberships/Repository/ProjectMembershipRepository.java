package com.craete.vault.Infrastructure.ProjectMemberships.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {
    List<ProjectMembership> findByProject_Id(UUID projectId);

    List<ProjectMembership> findByMember_Id(Long memberId);

    void deleteAllByProject_Id(UUID projectId);

    void deleteAllByMember_Id(Long memberId);
}
