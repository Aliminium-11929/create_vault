package com.craete.vault.API.Projects.Controllers;

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

import com.craete.vault.Application.Projects.Interfaces.IProjectService;
import com.craete.vault.Application.Projects.DTOs.ProjectCreateModel;
import com.craete.vault.Application.Projects.DTOs.ProjectPatchModel;
import com.craete.vault.Application.Projects.DTOs.ProjectStorageModel;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final IProjectService projectService;

    public ProjectController(IProjectService projectService){
        this.projectService = projectService;
    }

    @GetMapping()
    public List<ProjectStorageModel> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("{id}")
    public ProjectStorageModel getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("{id}/members")
    public List<Long> getMemberIdsByProjectId(@PathVariable UUID id) {
        return projectService.getMemberIdsByProjectId(id);
    }

    @PostMapping
    public ProjectStorageModel createProject(@Valid @RequestBody ProjectCreateModel projectCreateModel) {
        return projectService.createProject(projectCreateModel);
    }

    @PatchMapping
    public ProjectStorageModel patchProject(@Valid @RequestBody ProjectPatchModel projectPatchModel) {
        return projectService.patchProject(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteProjectById(@PathVariable UUID id) {
        projectService.deleteProjectById(id);
    }

}
