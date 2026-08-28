package com.craete.vault.Application.Fields.DTOs;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldCreateModel {

    @NotNull
    private String fieldName;

    @NotNull
    private List<UUID> projects;
}
