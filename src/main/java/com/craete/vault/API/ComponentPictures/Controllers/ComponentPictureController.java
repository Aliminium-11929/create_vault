package com.craete.vault.API.ComponentPictures.Controllers;

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

import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureCreateModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;
import com.craete.vault.Application.ComponentPictures.Interfaces.IComponentPictureService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/componentpictures")
public class ComponentPictureController {
    private final IComponentPictureService componentPictureService;

    public ComponentPictureController(IComponentPictureService componentPictureService) {
        this.componentPictureService = componentPictureService;
    }

    @GetMapping()
    public List<ComponentPictureStorageModel> getAllComponentPictures() {
        return componentPictureService.getAllComponentPictures();
    }

    @GetMapping("{id}")
    public ComponentPictureStorageModel getComponentPictureById(@PathVariable UUID id) {
        return componentPictureService.getComponentPictureById(id);
    }

    // @GetMapping("ids")
    // public List<ComponentPictureStorageModel> getComponentPicturesById(@Valid
    // @RequestBody List<UUID> ids) {
    // return componentPictureService.getComponentPicturesById(ids);
    // }

    @GetMapping("/component/{componentId}/single")
    public ComponentPictureStorageModel getComponentPictureByComponentId(@PathVariable UUID id) {
        return componentPictureService.getComponentPictureByComponentId(id);
    }

    @GetMapping("/component/{componentId}/single/{order}")
    public ComponentPictureStorageModel getComponentPictureByComponentId(@PathVariable UUID id,
            @PathVariable int order) {
        return componentPictureService.getComponentPictureByComponentId(id, order);
    }

    @GetMapping("/component/{componentId}")
    public List<ComponentPictureStorageModel> getComponentPicturesByComponentId(@PathVariable UUID componentId) {
        return componentPictureService.getComponentPicturesByComponentId(componentId);
    }

    @PostMapping
    public ComponentPictureStorageModel createComponentPicture(
            @Valid @RequestBody ComponentPictureCreateModel componentCreateModel) {
        return componentPictureService.createComponentPicture(componentCreateModel);
    }

    @PatchMapping
    public ComponentPictureStorageModel patchComponentPicture(
            @Valid @RequestBody ComponentPicturePatchModel componentPatchModel) {
        return componentPictureService.patchComponentPicture(componentPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteComponentPictureById(@PathVariable UUID id) {
        componentPictureService.deleteComponentPictureById(id);
    }

    @DeleteMapping("/component/{componentId}")
    public void deleteComponentPictureByComponentId(@PathVariable UUID componentId) {
        componentPictureService.deleteComponentPictureByComponentId(componentId);
    }

}
