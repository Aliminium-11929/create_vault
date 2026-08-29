package com.craete.vault.Application.ProjectMemberships.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipCreateModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipPatchModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipStorageModel;

public interface IProjectMembershipService {

    ProjectMembershipStorageModel createProjectMembership(ProjectMembershipCreateModel ProjectMembershipCreateModel);
    ProjectMembershipStorageModel getProjectMembershipById(UUID id);
    List<ProjectMembershipStorageModel> getProjectMembershipsById(List<UUID> ids);
    List<ProjectMembershipStorageModel> getAllProjectMemberships();
    ProjectMembershipStorageModel patchProjectMembership(ProjectMembershipPatchModel ProjectMembershipPatchModel);
    void deleteProjectMembershipById(UUID id);
    void deleteProjectMembershipByUserId(Long UserId);
    void deleteProjectMembershipByProjectId(UUID ProjectId);

}
