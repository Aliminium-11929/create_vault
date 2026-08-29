package com.craete.vault.Application.Fields.Interfaces;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Application.Fields.DTOs.FieldCreateModel;
import com.craete.vault.Application.Fields.DTOs.FieldPatchModel;
import com.craete.vault.Application.Fields.DTOs.FieldStorageModel;

public interface IFieldService {

    FieldStorageModel createField(FieldCreateModel FieldCreateModel);
    FieldStorageModel getFieldById(UUID id);
    List<FieldStorageModel> getFieldsById(List<UUID> id);
    List<FieldStorageModel> getAllFields();
    List<UUID> getProjectsInField(UUID id);
    List<Long> getUsersInField(UUID id);
    FieldStorageModel patchField(FieldPatchModel FieldPatchModel);
    void deleteFieldById(UUID id);

}
