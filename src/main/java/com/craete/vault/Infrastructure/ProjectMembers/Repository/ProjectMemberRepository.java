package com.craete.vault.Infrastructure.ProjectMembers.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ProjectMembers.Entities.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID>{

}
