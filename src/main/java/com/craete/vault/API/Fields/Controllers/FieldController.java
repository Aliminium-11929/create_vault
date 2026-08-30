package com.craete.vault.API.Fields.Controllers;

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

import com.craete.vault.Application.Fields.DTOs.FieldCreateModel;
import com.craete.vault.Application.Fields.DTOs.FieldPatchModel;
import com.craete.vault.Application.Fields.DTOs.FieldStorageModel;
import com.craete.vault.Application.Fields.Interfaces.IFieldService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fields")
public class FieldController {
    private final IFieldService fieldService;

    public FieldController(IFieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping()
    public List<FieldStorageModel> getAllFields() {
        return fieldService.getAllFields();
    }

    @GetMapping("{id}")
    public FieldStorageModel getFieldById(@PathVariable UUID id) {
        return fieldService.getFieldById(id);
    }

    @GetMapping("{id}/projects")
    public List<UUID> getProjectsInField(@PathVariable UUID id) {
        return fieldService.getProjectsInField(id);
    }

    @GetMapping("{id}/users")
    public List<Long> getUsersInField(@PathVariable UUID id) {
        return fieldService.getUsersInField(id);
    }

    // @GetMapping()
    // public List<FieldStorageModel> getFieldsById(@Valid @RequestBody List<UUID>
    // ids) {
    // return fieldService.getFieldsById(ids);
    // }

    @PostMapping
    public FieldStorageModel createField(@Valid @RequestBody FieldCreateModel projectCreateModel) {
        return fieldService.createField(projectCreateModel);
    }

    @PatchMapping
    public FieldStorageModel patchField(@Valid @RequestBody FieldPatchModel projectPatchModel) {
        return fieldService.patchField(projectPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteFieldById(@PathVariable UUID id) {
        fieldService.deleteFieldById(id);
    }
}
