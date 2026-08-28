package com.craete.vault.Application.ComponentPictures.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureCreateModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;

public interface IComponentPictureService {

    ComponentPictureStorageModel createComponentPicture(ComponentPictureCreateModel ComponentPictureCreateModel);
    ComponentPictureStorageModel getComponentPictureById(UUID id);
    ComponentPictureStorageModel getComponentPictureByComponentId(UUID ComponentId);
    ComponentPictureStorageModel getComponentPictureByComponentId(UUID ComponentId, int order);
    List<ComponentPictureStorageModel> getComponentPicturesById(List<UUID> id);
    List<ComponentPictureStorageModel> getComponentPicturesByComponentId(UUID ComponentId);
    List<ComponentPictureStorageModel> getAllComponentPictures();
    ComponentPictureStorageModel patchComponentPicture(ComponentPicturePatchModel ComponentPicturePatchModel);
    void deleteComponentPictureById(UUID id);
    void deleteComponentPictureByComponentId(UUID ComponentId);

}
