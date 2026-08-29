package com.craete.vault.Application.ProjectPictures.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureCreateModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPicturePatchModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureStorageModel;

public interface IProjectPictureService {

    ProjectPictureStorageModel createProjectPicture(ProjectPictureCreateModel ProjectPictureCreateModel);
    ProjectPictureStorageModel getProjectPictureById(UUID id);
    List<ProjectPictureStorageModel> getProjectPicturesById(List<UUID> ids);
    List<ProjectPictureStorageModel> getProjectPicturesByProjectId(UUID ProjectId);
    List<ProjectPictureStorageModel> getAllProjectPictures();
    ProjectPictureStorageModel patchProjectPicture(ProjectPicturePatchModel ProjectPicturePatchModel);
    void deleteProjectPictureById(UUID id);
    void deleteProjectPicturesByProjectId(UUID ProjectId);

}
