package com.craete.vault.API.ProjectPictures.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureCreateModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPicturePatchModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureStorageModel;
import com.craete.vault.Application.ProjectPictures.Interfaces.IProjectPictureService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projectpictures")
public class ProjectPictureController {
    private final IProjectPictureService projectPictureService;

    public ProjectPictureController(IProjectPictureService projectPictureService) {
        this.projectPictureService = projectPictureService;
    }

    @GetMapping()
    public List<ProjectPictureStorageModel> getAllProjectPictures() {
        return projectPictureService.getAllProjectPictures();
    }

    @GetMapping("{id}")
    public ProjectPictureStorageModel getProjectPictureById(@PathVariable UUID id) {
        return projectPictureService.getProjectPictureById(id);
    }

    @GetMapping("/project/{projectId}/single")
    public ProjectPictureStorageModel getProjectPictureByProjectId(@PathVariable("projectId") UUID projectId) {
        return projectPictureService.getProjectPictureByProjectId(projectId);
    }

    @GetMapping("/project/{projectId}/single/{order}")
    public ProjectPictureStorageModel getProjectPictureByProjectId(@PathVariable("projectId") UUID projectId,
            @PathVariable int order) {
        return projectPictureService.getProjectPictureByProjectId(projectId, order);
    }

    @GetMapping("/project/{projectId}")
    public List<ProjectPictureStorageModel> getProjectPicturesByProjectId(@PathVariable UUID projectId) {
        return projectPictureService.getProjectPicturesByProjectId(projectId);
    }

    @PostMapping
    public ProjectPictureStorageModel createProjectPicture(
            @Valid @RequestBody ProjectPictureCreateModel projectCreateModel) {
        return projectPictureService.createProjectPicture(projectCreateModel);
    }

    @PatchMapping
    public ProjectPictureStorageModel patchProjectPicture(
            @Valid @RequestBody ProjectPicturePatchModel projectPatchModel) {
        return projectPictureService.patchProjectPicture(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteProjectPictureById(@PathVariable UUID id) {
        projectPictureService.deleteProjectPictureById(id);
    }

    @DeleteMapping("/project/{projectId}")
    public void deleteProjectPicturesByProjectId(@PathVariable UUID projectId) {
        projectPictureService.deleteProjectPicturesByProjectId(projectId);
    }

}
