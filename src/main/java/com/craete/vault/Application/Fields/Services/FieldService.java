package com.craete.vault.Application.Fields.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.Fields.DTOs.FieldCreateModel;
import com.craete.vault.Application.Fields.DTOs.FieldPatchModel;
import com.craete.vault.Application.Fields.DTOs.FieldStorageModel;
import com.craete.vault.Application.Fields.Interfaces.IFieldService;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Exceptions.FieldNotFoundException;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;

import jakarta.transaction.Transactional;

@Service
public class FieldService implements IFieldService {

    private final FieldRepository fieldRepository;
    private final ModelMapper modelMapper;

    public FieldService(FieldRepository fieldRepository, ModelMapper modelMapper) {
        this.fieldRepository = fieldRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public FieldStorageModel createField(FieldCreateModel fieldCreateModel) {
        if (fieldCreateModel == null) {
            throw new IllegalArgumentException("Field must not be null.");
        }

        Field field = modelMapper.map(fieldCreateModel, Field.class);
        Field savedField = fieldRepository.save(field);
        return toStorageModel(savedField);
    }

    @Override
    public FieldStorageModel getFieldById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Field ID must not be null.");
        }

        Field existingField = fieldRepository.findById(id)
            .orElseThrow(() -> new FieldNotFoundException(String.format("Field with ID %s was not found.", id)));

        return toStorageModel(existingField);
    }

    @Override
    public List<FieldStorageModel> getFieldsById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Field IDs must not be null or empty.");
        }

        List<Field> existingFields = fieldRepository.findAllById(ids);
        if (existingFields.isEmpty()) {
            throw new FieldNotFoundException("Fields not found.");
        }

        return existingFields.stream()
            .map(this::toStorageModel)
            .toList();
    }

    @Override
    public List<FieldStorageModel> getAllFields() {
        return fieldRepository.findAll().stream()
            .map(this::toStorageModel)
            .toList();
    }

    @Override
    public List<UUID> getProjectsInField(UUID id) {
        Field field = getFieldEntityById(id);
        return field.getProjects().stream()
            .map(Project::getId)
            .toList();
    }

    @Override
    public List<Long> getUsersInField(UUID id) {
        Field field = getFieldEntityById(id);
        return field.getUsers().stream()
            .map(User::getId)
            .toList();
    }

    @Override
    @Transactional
    public FieldStorageModel patchField(FieldPatchModel fieldPatchModel) {
        if (fieldPatchModel == null) {
            throw new IllegalArgumentException("Patched field and field ID must not be null.");
        }

        Field existingField = fieldRepository.findById(fieldPatchModel.getId())
            .orElseThrow(() -> new FieldNotFoundException(
                String.format("Field with ID %s was not found.", fieldPatchModel.getId())));

        existingField.setFieldName(fieldPatchModel.getFieldName());
        existingField.setFieldId(fieldPatchModel.getId());

        return toStorageModel(fieldRepository.save(existingField));
    }

    @Override
    public void deleteFieldById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Field ID must not be null.");
        }

        fieldRepository.findById(id)
            .orElseThrow(() -> new FieldNotFoundException(String.format("Field with ID %s was not found.", id)));

        fieldRepository.deleteById(id);
    }

    private Field getFieldEntityById(UUID id) {
        return fieldRepository.findById(id)
            .orElseThrow(() -> new FieldNotFoundException(String.format("Field with ID %s was not found.", id)));
    }

    private FieldStorageModel toStorageModel(Field field) {
        FieldStorageModel storageModel = modelMapper.map(field, FieldStorageModel.class);
        storageModel.setId(field.getFieldId());

        if (field.getProjects() != null) {
            storageModel.setProjects(field.getProjects().stream()
                .map(Project::getId)
                .toList());
        }

        return storageModel;
    }
}
