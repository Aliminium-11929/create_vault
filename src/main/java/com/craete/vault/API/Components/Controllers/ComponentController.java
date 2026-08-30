package com.craete.vault.API.Components.Controllers;

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

import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;
import com.craete.vault.Application.Components.Interfaces.IComponentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/components")
public class ComponentController {
    private final IComponentService componentService;

    public ComponentController(IComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping()
    public List<ComponentStorageModel> getAllComponents() {
        return componentService.getAllComponents();
    }

    @GetMapping("{id}")
    public ComponentStorageModel getComponentById(@PathVariable UUID id) {
        return componentService.getComponentById(id);
    }

    // @GetMapping()
    // public List<ComponentStorageModel> getComponentsById(@Valid @RequestBody
    // List<UUID> ids) {
    // return componentService.getComponentsById(ids);
    // }

    @PostMapping
    public ComponentStorageModel createComponent(@Valid @RequestBody ComponentCreateModel projectCreateModel) {
        return componentService.createComponent(projectCreateModel);
    }

    @PatchMapping
    public ComponentStorageModel patchComponent(@Valid @RequestBody ComponentPatchModel projectPatchModel) {
        return componentService.patchComponent(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteComponentById(@PathVariable UUID id) {
        componentService.deleteComponentById(id);
    }
}
