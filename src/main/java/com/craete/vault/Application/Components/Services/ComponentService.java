package com.craete.vault.Application.Components.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;
import com.craete.vault.Application.Components.Interfaces.IComponentService;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Exceptions.ComponentNotFoundException;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;

import jakarta.transaction.Transactional;

@Service
public class ComponentService implements IComponentService {

    private final ComponentRepository componentRepository;
    private final ModelMapper modelMapper;

    public ComponentService(ComponentRepository componentRepository, ModelMapper modelMapper) {
        this.componentRepository = componentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ComponentStorageModel createComponent(ComponentCreateModel componentCreateModel) {
        if (componentCreateModel == null) {
            throw new IllegalArgumentException("Component must not be null.");
        }

        Component savedComponent = componentRepository.save(modelMapper.map(componentCreateModel, Component.class));
        return modelMapper.map(savedComponent, ComponentStorageModel.class);
    }

    @Override
    public ComponentStorageModel getComponentById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        Component existingComponent = componentRepository.findById(id)
            .orElseThrow(() -> new ComponentNotFoundException(String.format("Component with ID %s was not found.", id)));

        return modelMapper.map(existingComponent, ComponentStorageModel.class);
    }

    @Override
    public List<ComponentStorageModel> getComponentsById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Component IDs must not be null or empty.");
        }

        List<Component> existingComponents = componentRepository.findAllById(ids);
        if (existingComponents.isEmpty()) {
            throw new ComponentNotFoundException("Components not found.");
        }

        return existingComponents.stream()
            .map(component -> modelMapper.map(component, ComponentStorageModel.class))
            .toList();
    }

    @Override
    public List<ComponentStorageModel> getAllComponents() {
        return componentRepository.findAll().stream()
            .map(component -> modelMapper.map(component, ComponentStorageModel.class))
            .toList();
    }

    @Override
    @Transactional
    public ComponentStorageModel patchComponent(ComponentPatchModel componentPatchModel) {
        if (componentPatchModel == null) {
            throw new IllegalArgumentException("Patched component and component ID must not be null.");
        }

        Component existingComponent = componentRepository.findById(componentPatchModel.getId())
            .orElseThrow(() -> new ComponentNotFoundException(
                String.format("Component with ID %s was not found.", componentPatchModel.getId())));

        modelMapper.map(componentPatchModel, existingComponent);

        return modelMapper.map(componentRepository.save(existingComponent), ComponentStorageModel.class);
    }

    @Override
    public void deleteComponentById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        componentRepository.findById(id)
            .orElseThrow(() -> new ComponentNotFoundException(String.format("Component with ID %s was not found.", id)));

        componentRepository.deleteById(id);
    }
}
