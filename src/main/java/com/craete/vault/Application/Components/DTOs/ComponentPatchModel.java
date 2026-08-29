package com.craete.vault.Application.Components.DTOs;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPatchModel {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    @Positive
    private int totalQuantity;

    @NotNull
    @PositiveOrZero
    private int availableQuantity;

}
