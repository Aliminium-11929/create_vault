package com.craete.vault.Application.Fields.DTOs;

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
public class FieldPatchModel {

    @NotNull
    private UUID id;

    @NotNull
    private String fieldName;

}
