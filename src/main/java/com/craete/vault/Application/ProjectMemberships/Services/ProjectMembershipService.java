package com.craete.vault.Application.ProjectMemberships.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipCreateModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipPatchModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipStorageModel;
import com.craete.vault.Application.ProjectMemberships.Interfaces.IProjectMembershipService;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Exceptions.ProjectMembershipNotFoundException;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;

import jakarta.transaction.Transactional;

/**
 * ProjectMembershipService
 */
@Service
public class ProjectMembershipService implements IProjectMembershipService {

    private ProjectMembershipRepository projectMembershipRepository;
    private ModelMapper modelMapper;

    public ProjectMembershipService(ProjectMembershipRepository projectMembershipRepository, ModelMapper modelMapper) {
        this.projectMembershipRepository = projectMembershipRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectMembershipStorageModel createProjectMembership(
            ProjectMembershipCreateModel ProjectMembershipCreateModel) {
        if (ProjectMembershipCreateModel == null)
            throw new IllegalArgumentException("Project membership can't be null.");
        ProjectMembership savedProjectMembership = projectMembershipRepository.save(
                modelMapper.map(ProjectMembershipCreateModel, ProjectMembership.class));
        return modelMapper.map(savedProjectMembership, ProjectMembershipStorageModel.class);
    }

    @Override
    public ProjectMembershipStorageModel getProjectMembershipById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("id can't be null");
        return modelMapper.map(
                projectMembershipRepository.findById(id)
                        .orElseThrow(() -> new ProjectMembershipNotFoundException(
                                String.format("Membership with id %s not found", id))),
                ProjectMembershipStorageModel.class);
    }

    @Override
    public List<ProjectMembershipStorageModel> getProjectMembershipsById(List<UUID> ids) {
        List<ProjectMembership> savedMemberships = projectMembershipRepository.findAllById(ids);
        if (savedMemberships.isEmpty())
            throw new ProjectMembershipNotFoundException("Memberships not found.");
        return savedMemberships.stream()
                .map(membership -> modelMapper.map(membership, ProjectMembershipStorageModel.class))
                .toList();
    }

    @Override
    public List<ProjectMembershipStorageModel> getAllProjectMemberships() {
        return projectMembershipRepository.findAll()
                .stream()
                .map(membership -> modelMapper.map(membership, ProjectMembershipStorageModel.class))
                .toList();
    }

    @Override
    @Transactional
    public ProjectMembershipStorageModel patchProjectMembership(
            ProjectMembershipPatchModel ProjectMembershipPatchModel) {
        if (ProjectMembershipPatchModel == null)
            throw new IllegalArgumentException("Project membership can't be null");
        ProjectMembership existingMembership = projectMembershipRepository.findById(ProjectMembershipPatchModel.getId())
                .orElseThrow(
                        () -> new ProjectMembershipNotFoundException(
                                String.format("Project membership with id %s not found",
                                        ProjectMembershipPatchModel.getId())));
        modelMapper.map(ProjectMembershipPatchModel, existingMembership);
        return modelMapper.map(projectMembershipRepository.save(existingMembership),
                ProjectMembershipStorageModel.class);
    }

    @Override
    @Transactional
    public void deleteProjectMembershipById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("id can't be null");
        projectMembershipRepository.findById(id)
                .orElseThrow(
                        () -> new ProjectMembershipNotFoundException(
                                String.format("Membership with id %s not found", id)));
        projectMembershipRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteProjectMembershipByUserId(Long UserId) {
        if (UserId == null)
            throw new IllegalArgumentException("id can't be null");
        if (projectMembershipRepository.findByMember_Id(UserId).isEmpty())
            throw new ProjectMembershipNotFoundException(
                    String.format("Memberships of user with id %s not found", UserId));
        projectMembershipRepository.deleteAllByMember_Id(UserId);
    }

    @Override
    @Transactional
    public void deleteProjectMembershipByProjectId(UUID ProjectId) {
        if (ProjectId == null)
            throw new IllegalArgumentException("id can't be null");
        if (projectMembershipRepository.findByProject_Id(ProjectId).isEmpty())
            throw new ProjectMembershipNotFoundException(
                    String.format("Memberships of project with id %s not found", ProjectId));
        projectMembershipRepository.deleteAllByProject_Id(ProjectId);
    }
}
