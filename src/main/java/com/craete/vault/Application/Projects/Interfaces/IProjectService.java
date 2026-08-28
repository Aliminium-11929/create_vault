package com.craete.vault.Application.Projects.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.Projects.DTOs.ProjectCreateModel;
import com.craete.vault.Application.Projects.DTOs.ProjectPatchModel;
import com.craete.vault.Application.Projects.DTOs.ProjectStorageModel;

public interface IProjectService {

    ProjectStorageModel createProject(ProjectCreateModel ProjectCreateModel);
    ProjectStorageModel getProjectById(UUID id);
    List<ProjectStorageModel> getProjectsById(List<UUID> ids);
    List<ProjectStorageModel> getAllProjects();
    List<Long> getMemberIdsByProjectId(UUID id);
    ProjectStorageModel patchProject(ProjectPatchModel ProjectPatchModel);
    void deleteProjectById(UUID id);

}
