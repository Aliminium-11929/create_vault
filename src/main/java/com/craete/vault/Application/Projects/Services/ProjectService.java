package com.craete.vault.Application.Projects.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.Projects.DTOs.ProjectCreateModel;
import com.craete.vault.Application.Projects.DTOs.ProjectPatchModel;
import com.craete.vault.Application.Projects.DTOs.ProjectStorageModel;
import com.craete.vault.Application.Projects.Interfaces.IProjectService;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Exceptions.ProjectNotFoundException;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public ProjectService(ProjectRepository projectRepository, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectStorageModel createProject(ProjectCreateModel ProjectCreateModel) {
        if (ProjectCreateModel == null) { throw new IllegalArgumentException("Project can't be null"); }

        Project savedProject = projectRepository.save(modelMapper.map(ProjectCreateModel, Project.class));

        return modelMapper.map(savedProject, ProjectStorageModel.class);
    }

    @Override
    public ProjectStorageModel getProjectById(UUID id) {
        Project existingProject = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(String.format("Project with ID %s was not found.", id)));
        return modelMapper.map(existingProject, ProjectStorageModel.class);
    }

    @Override
    public List<ProjectStorageModel> getProjectsById(List<UUID> ids) {
        List<Project> projects = projectRepository.findAllById(ids);
        if (projects.isEmpty()) { throw new ProjectNotFoundException("Projects not found"); }
        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectStorageModel.class))
                .toList();
    }

    @Override
    public List<ProjectStorageModel> getAllProjects() {
        List<Project> allProjects = projectRepository.findAll();
        return allProjects.stream()
                .map(project -> modelMapper.map(project, ProjectStorageModel.class))
                .toList();
    }

    @Override
    public List<Long> getMemberIdsByProjectId(UUID id) {
        List<ProjectMembership> memberships = projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(String.format("Project with ID %s was not found", id)))
            .getProjectMemberships();
        return memberships.stream()
                .map(membership -> membership.getMember().getId())
                .toList();
    }

    @Override
    @Transactional
    public ProjectStorageModel patchProject(ProjectPatchModel ProjectPatchModel) {
        if (ProjectPatchModel == null) { throw new IllegalArgumentException("Project can't be null."); }

        Project existingProject = projectRepository.findById(ProjectPatchModel.getId())
            .orElseThrow(() -> new ProjectNotFoundException(String.format("Project with ID %s was not found", ProjectPatchModel.getId())));

        modelMapper.map(ProjectPatchModel, existingProject);

        return modelMapper.map(projectRepository.save(existingProject), ProjectStorageModel.class);
    }

    @Override
    @Transactional
    public void deleteProjectById(UUID id) {

        if (id == null) throw new IllegalAccessError("Project can't be null");

        projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(String.format("Project with ID %s was not found", id)));

        projectRepository.deleteById(id);
    }

}
