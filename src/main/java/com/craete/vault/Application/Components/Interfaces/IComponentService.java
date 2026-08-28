package com.craete.vault.Application.Components.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;

public interface IComponentService {

    ComponentStorageModel createComponent(ComponentCreateModel ComponentCreateModel);
    ComponentStorageModel getComponentById(UUID id);
    List<ComponentStorageModel> getComponentsById(List<UUID> id);
    List<ComponentStorageModel> getAllComponents();
    List<UUID> getComponentReservations(UUID id);
    ComponentStorageModel patchComponent(ComponentPatchModel ComponentPatchModel);
    void deleteComponentById(UUID id);

}
