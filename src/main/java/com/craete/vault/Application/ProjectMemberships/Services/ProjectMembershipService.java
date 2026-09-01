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
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Exceptions.ProjectMembershipNotFoundException;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

import jakarta.transaction.Transactional;

/**
 * ProjectMembershipService
 */
@Service
public class ProjectMembershipService implements IProjectMembershipService {

    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProjectMembershipService(ProjectMembershipRepository projectMembershipRepository,
            ProjectRepository projectRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.projectMembershipRepository = projectMembershipRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectMembershipStorageModel createProjectMembership(
            ProjectMembershipCreateModel ProjectMembershipCreateModel) {
        if (ProjectMembershipCreateModel == null)
            throw new IllegalArgumentException("Project membership can't be null.");

        Project project = projectRepository.findById(ProjectMembershipCreateModel.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found: " + ProjectMembershipCreateModel.getProjectId()));

        User member = userRepository.findById(ProjectMembershipCreateModel.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + ProjectMembershipCreateModel.getMemberId()));

        boolean alreadyExists = projectMembershipRepository.findByProject_Id(project.getId()).stream()
                .anyMatch(existingMembership -> existingMembership.getMember() != null
                        && existingMembership.getMember().getId().equals(member.getId()));
        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "Membership already exists for project " + project.getId() + " and member " + member.getId());
        }

        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setMember(member);

        ProjectMembership savedProjectMembership = projectMembershipRepository.save(membership);
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

        Project project = projectRepository.findById(ProjectMembershipPatchModel.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found: " + ProjectMembershipPatchModel.getProjectId()));

        User member = userRepository.findById(ProjectMembershipPatchModel.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + ProjectMembershipPatchModel.getMemberId()));

        existingMembership.setProject(project);
        existingMembership.setMember(member);

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
