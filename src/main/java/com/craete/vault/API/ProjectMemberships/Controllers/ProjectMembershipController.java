package com.craete.vault.API.ProjectMemberships.Controllers;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.craete.vault.Application.ProjectMemberships.Interfaces.IProjectMembershipService;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipCreateModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipPatchModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipStorageModel;

@RestController
@RequestMapping("/memberships")
public class ProjectMembershipController {
    private final IProjectMembershipService projectMembershipService;

    public ProjectMembershipController(IProjectMembershipService projectMembershipService){
        this.projectMembershipService = projectMembershipService;
    }

    @GetMapping()
    public List<ProjectMembershipStorageModel> getAllProjectMemberships() {
        return projectMembershipService.getAllProjectMemberships();
    }

    @GetMapping("{id}")
    public ProjectMembershipStorageModel getProjectMembershipById(@PathVariable UUID id) {
        return projectMembershipService.getProjectMembershipById(id);
    }

    @PostMapping
    public ProjectMembershipStorageModel createProjectmembership(@Valid @RequestBody ProjectMembershipCreateModel projectCreateModel) {
        return projectMembershipService.createProjectMembership(projectCreateModel);
    }

    @PatchMapping
    public ProjectMembershipStorageModel patchProjectMembership(@Valid @RequestBody ProjectMembershipPatchModel projectPatchModel) {
        return projectMembershipService.patchProjectMembership(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteProjectMembershipById(@PathVariable UUID id) {
        projectMembershipService.deleteProjectMembershipById(id);
    }

    @DeleteMapping("/project/{projectId}")
    public void deleteProjectMembershipsByProjectId(@PathVariable UUID projectId) {
        projectMembershipService.deleteProjectMembershipByProjectId(projectId);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteProjectMembershipsByUserId(@PathVariable Long userId) {
        projectMembershipService.deleteProjectMembershipByUserId(userId);
    }

}
