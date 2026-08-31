package com.craete.vault.Application.ComponentPictures.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureCreateModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;
import com.craete.vault.Application.ComponentPictures.Interfaces.IComponentPictureService;
import com.craete.vault.Domain.ComponentPictures.Entities.ComponentPicture;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Exceptions.ComponentPictureNotFoundException;
import com.craete.vault.Infrastructure.ComponentPictures.Repository.ComponentPictureRepository;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;

import jakarta.transaction.Transactional;

@Service
public class ComponentPictureService implements IComponentPictureService {

    private final ComponentPictureRepository componentPictureRepository;
    private final ComponentRepository componentRepository;
    private final ModelMapper modelMapper;

    public ComponentPictureService(ComponentPictureRepository componentPictureRepository,
            ComponentRepository componentRepository, ModelMapper modelMapper) {
        this.componentPictureRepository = componentPictureRepository;
        this.componentRepository = componentRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ComponentPictureStorageModel createComponentPicture(
            ComponentPictureCreateModel componentPictureCreateModel) {
        if (componentPictureCreateModel == null) {
            throw new IllegalArgumentException("Component picture must not be null.");
        }

        Component component = componentRepository.findById(componentPictureCreateModel.getComponentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Component not found: " + componentPictureCreateModel.getComponentId()));

        ComponentPicture picture = new ComponentPicture();
        picture.setComponent(component);
        picture.setStorageKey(componentPictureCreateModel.getStorageKey());
        picture.setOrder(componentPictureCreateModel.getOrder());
        picture.setCaption(componentPictureCreateModel.getCaption());

        ComponentPicture savedPicture = componentPictureRepository.save(picture);
        return modelMapper.map(savedPicture, ComponentPictureStorageModel.class);
    }

    @Override
    public ComponentPictureStorageModel getComponentPictureById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component picture ID must not be null.");
        }

        ComponentPicture picture = componentPictureRepository.findById(id)
                .orElseThrow(() -> new ComponentPictureNotFoundException(
                        String.format("Component picture with ID %s was not found.", id)));

        return modelMapper.map(picture, ComponentPictureStorageModel.class);
    }

    @Override
    public ComponentPictureStorageModel getComponentPictureByComponentId(UUID componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        return componentPictureRepository.findAll().stream()
                .filter(picture -> picture.getComponent() != null && picture.getComponent().getId().equals(componentId))
                .findFirst()
                .map(picture -> modelMapper.map(picture, ComponentPictureStorageModel.class))
                .orElseThrow(() -> new ComponentPictureNotFoundException(
                        String.format("Component picture for component ID %s was not found.", componentId)));
    }

    @Override
    public ComponentPictureStorageModel getComponentPictureByComponentId(UUID componentId, int order) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        return componentPictureRepository.findAll().stream()
                .filter(picture -> picture.getComponent() != null && picture.getComponent().getId().equals(componentId))
                .filter(picture -> picture.getOrder() == order)
                .findFirst()
                .map(picture -> modelMapper.map(picture, ComponentPictureStorageModel.class))
                .orElseThrow(() -> new ComponentPictureNotFoundException(
                        String.format("Component picture for component ID %s with order %s was not found.", componentId,
                                order)));
    }

    @Override
    public List<ComponentPictureStorageModel> getComponentPicturesById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Component picture IDs must not be null or empty.");
        }

        List<ComponentPicture> pictures = componentPictureRepository.findAllById(ids);
        if (pictures.isEmpty()) {
            throw new ComponentPictureNotFoundException("Component pictures not found.");
        }

        return pictures.stream()
                .map(picture -> modelMapper.map(picture, ComponentPictureStorageModel.class))
                .toList();
    }

    @Override
    public List<ComponentPictureStorageModel> getComponentPicturesByComponentId(UUID componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        List<ComponentPicture> pictures = componentPictureRepository.findAll().stream()
                .filter(picture -> picture.getComponent() != null && picture.getComponent().getId().equals(componentId))
                .toList();

        if (pictures.isEmpty()) {
            throw new ComponentPictureNotFoundException("Component pictures not found.");
        }

        return pictures.stream()
                .map(picture -> modelMapper.map(picture, ComponentPictureStorageModel.class))
                .toList();
    }

    @Override
    public List<ComponentPictureStorageModel> getAllComponentPictures() {
        return componentPictureRepository.findAll().stream()
                .map(picture -> modelMapper.map(picture, ComponentPictureStorageModel.class))
                .toList();
    }

    @Override
    @Transactional
    public ComponentPictureStorageModel patchComponentPicture(ComponentPicturePatchModel componentPicturePatchModel) {
        if (componentPicturePatchModel == null) {
            throw new IllegalArgumentException("Patched component picture and picture ID must not be null.");
        }

        ComponentPicture existingPicture = componentPictureRepository.findById(componentPicturePatchModel.getId())
                .orElseThrow(() -> new ComponentPictureNotFoundException(
                        String.format("Component picture with ID %s was not found.",
                                componentPicturePatchModel.getId())));

        modelMapper.map(componentPicturePatchModel, existingPicture);
        return modelMapper.map(componentPictureRepository.save(existingPicture), ComponentPictureStorageModel.class);
    }

    @Override
    public void deleteComponentPictureById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component picture ID must not be null.");
        }

        componentPictureRepository.findById(id)
                .orElseThrow(() -> new ComponentPictureNotFoundException(
                        String.format("Component picture with ID %s was not found.", id)));

        componentPictureRepository.deleteById(id);
    }

    @Override
    public void deleteComponentPictureByComponentId(UUID componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        List<ComponentPicture> pictures = componentPictureRepository.findAll().stream()
                .filter(picture -> picture.getComponent() != null && picture.getComponent().getId().equals(componentId))
                .toList();

        if (pictures.isEmpty()) {
            throw new ComponentPictureNotFoundException("Component pictures not found.");
        }

        componentPictureRepository.deleteAll(pictures);
    }
}
