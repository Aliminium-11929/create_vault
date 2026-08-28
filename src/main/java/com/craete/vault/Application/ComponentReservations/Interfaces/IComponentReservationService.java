package com.craete.vault.Application.ComponentReservations.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;

public interface IComponentReservationService {

    ComponentReservationStorageModel createComponentReservation(ComponentReservationCreateModel ComponentReservationCreateModel);
    ComponentReservationStorageModel getComponentReservationById(UUID id);
    List<ComponentReservationStorageModel> getComponentReservationsByComponentId(UUID ComponentId);
    List<ComponentReservationStorageModel> getComponentReservationsByUserId(Long UserId);
    List<ComponentReservationStorageModel> getComponentReservationsById(List<UUID> id);
    List<ComponentReservationStorageModel> getAllComponentReservations();
    ComponentReservationStorageModel patchComponentReservation(ComponentReservationPatchModel ComponentReservationPatchModel);
    void deleteComponentReservationById(UUID id);
    void deleteComponentReservationByComponentId(UUID ComponentId);

}
