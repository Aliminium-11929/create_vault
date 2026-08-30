package com.craete.vault.API.ComponentReservations.Controllers;

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

import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;
import com.craete.vault.Application.ComponentReservations.Interfaces.IComponentReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/componentReservations")
public class ComponentReservationController {
    private final IComponentReservationService componentReservationService;

    public ComponentReservationController(IComponentReservationService componentReservationService) {
        this.componentReservationService = componentReservationService;
    }

    @GetMapping()
    public List<ComponentReservationStorageModel> getAllComponentReservations() {
        return componentReservationService.getAllComponentReservations();
    }

    @GetMapping("{id}")
    public ComponentReservationStorageModel getComponentReservationById(@PathVariable UUID id) {
        return componentReservationService.getComponentReservationById(id);
    }

    @GetMapping("/component/{componentId}")
    public List<ComponentReservationStorageModel> getComponentReservationsByComponentId(
            @PathVariable UUID componentId) {
        return componentReservationService.getComponentReservationsByComponentId(componentId);
    }

    @GetMapping("/user/{userId}")
    public List<ComponentReservationStorageModel> getComponentReservationsByUserId(@PathVariable Long userId) {
        return componentReservationService.getComponentReservationsByUserId(userId);
    }

    // @GetMapping()
    // public List<ComponentReservationStorageModel>
    // getComponentReservationsById(@Valid @RequestBody List<UUID> ids) {
    // return componentReservationService.getComponentReservationsById(ids);
    // }

    @PostMapping
    public ComponentReservationStorageModel createComponentReservation(
            @Valid @RequestBody ComponentReservationCreateModel projectCreateModel) {
        return componentReservationService.createComponentReservation(projectCreateModel);
    }

    @PatchMapping
    public ComponentReservationStorageModel patchComponentReservation(
            @Valid @RequestBody ComponentReservationPatchModel projectPatchModel) {
        return componentReservationService.patchComponentReservation(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteComponentReservationById(@PathVariable UUID id) {
        componentReservationService.deleteComponentReservationById(id);
    }

    @DeleteMapping("/component/{componentId}")
    public void deleteComponentReservationByComponentId(@PathVariable UUID componentId) {
        componentReservationService.deleteComponentReservationByComponentId(componentId);
    }
}
