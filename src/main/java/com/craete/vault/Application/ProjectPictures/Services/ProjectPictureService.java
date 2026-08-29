package com.craete.vault.Application.ProjectPictures.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;

import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureCreateModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPicturePatchModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureStorageModel;
import com.craete.vault.Application.ProjectPictures.Interfaces.IProjectPictureService;
import com.craete.vault.Domain.ProjectPictures.Entities.ProjectPicture;
import com.craete.vault.Exceptions.ProjectPictureNotFoundException;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;

import jakarta.transaction.Transactional;

/**
 * ProjectPictureService
 */
public class ProjectPictureService implements IProjectPictureService{

    private final ProjectPictureRepository projectPictureRepository;
    private final ModelMapper modelMapper;

    public ProjectPictureService(ProjectPictureRepository projectPictureRepository, ModelMapper modelMapper) {
        this.projectPictureRepository = projectPictureRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ProjectPictureStorageModel createProjectPicture(ProjectPictureCreateModel ProjectPictureCreateModel) {
        if (ProjectPictureCreateModel == null) throw new IllegalArgumentException("Picture can't be null");
        ProjectPicture savedProjectPicture = projectPictureRepository.save(modelMapper.map(ProjectPictureCreateModel, ProjectPicture.class));
        return modelMapper.map(savedProjectPicture, ProjectPictureStorageModel.class);
    }

    @Override
    public ProjectPictureStorageModel getProjectPictureById(UUID id) {
        return modelMapper.map(
            projectPictureRepository.findById(id)
                .orElseThrow(() -> new ProjectPictureNotFoundException(String.format("No picture with ID %s was found.", id))),
            ProjectPictureStorageModel.class
        );
    }

    @Override
    public List<ProjectPictureStorageModel> getProjectPicturesById(List<UUID> ids) {
        List<ProjectPicture> projectPictures = projectPictureRepository.findAllById(ids);
        if (projectPictures.isEmpty()) throw new ProjectPictureNotFoundException("Project pictures not found.");
        return projectPictures.stream()
            .map(picture -> modelMapper.map(picture, ProjectPictureStorageModel.class))
            .toList();
    }

    @Override
    public List<ProjectPictureStorageModel> getProjectPicturesByProjectId(UUID ProjectId) {
        List<ProjectPicture> savedPictures = projectPictureRepository.findByProjectId(ProjectId);
        if (savedPictures.isEmpty()) throw new ProjectPictureNotFoundException(String.format("No pictures with project id %s were found.", ProjectId));
        return savedPictures.stream()
            .map(picture -> modelMapper.map(picture, ProjectPictureStorageModel.class))
            .toList();
    }

    @Override
    public List<ProjectPictureStorageModel> getAllProjectPictures() {
        return projectPictureRepository.findAll()
            .stream()
            .map(picture -> modelMapper.map(picture, ProjectPictureStorageModel.class))
            .toList();
    }

    @Override
    public ProjectPictureStorageModel getProjectPictureByProjectId(UUID projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID must not be null.");
        }

        return projectPictureRepository.findAll().stream()
            .filter(picture -> picture.getProject() != null && picture.getProject().getId().equals(projectId))
            .findFirst()
            .map(picture -> modelMapper.map(picture, ProjectPictureStorageModel.class))
            .orElseThrow(() -> new ProjectPictureNotFoundException(
                String.format("Project picture for project ID %s was not found.", projectId)));
    }

    @Override
    public ProjectPictureStorageModel getProjectPictureByProjectId(UUID projectId, int order) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID must not be null.");
        }

        return projectPictureRepository.findAll().stream()
            .filter(picture -> picture.getProject() != null && picture.getProject().getId().equals(projectId))
            .filter(picture -> picture.getOrder() == order)
            .findFirst()
            .map(picture -> modelMapper.map(picture, ProjectPictureStorageModel.class))
            .orElseThrow(() -> new ProjectPictureNotFoundException(
                String.format("Project picture for project ID %s with order %s was not found.", projectId, order)));
    }

    @Override
    @Transactional
    public ProjectPictureStorageModel patchProjectPicture(ProjectPicturePatchModel ProjectPicturePatchModel) {
        if (ProjectPicturePatchModel == null) throw new IllegalArgumentException("Picture can't be null.");
        ProjectPicture existingPicture = projectPictureRepository.findById(ProjectPicturePatchModel.getId())
            .orElseThrow(() -> new ProjectPictureNotFoundException("Picture not found."));
        modelMapper.map(ProjectPicturePatchModel, existingPicture);
        return modelMapper.map(projectPictureRepository.save(existingPicture), ProjectPictureStorageModel.class);
    }

    @Override
    @Transactional
    public void deleteProjectPictureById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id can't be null.");
        projectPictureRepository.findById(id)
            .orElseThrow(() -> new ProjectPictureNotFoundException(String.format("Picture with id %s not found.", id)));
        projectPictureRepository.deleteById(id);
    }

    public void deleteProjectPicturesByProjectId(UUID ProjectId) {
        if (ProjectId == null) throw new IllegalArgumentException("id can't be null.");
        if (projectPictureRepository.findByProjectId(ProjectId).isEmpty())
            throw new ProjectPictureNotFoundException(String.format("No pictures belong to project with id %s", ProjectId));
        projectPictureRepository.deleteAllByProjectId(ProjectId);
    }

}
