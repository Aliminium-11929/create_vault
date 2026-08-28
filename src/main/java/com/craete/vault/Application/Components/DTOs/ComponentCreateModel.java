package com.craete.vault.Application.Components.DTOs;

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
public class ComponentCreateModel {

    @NotNull
    private String name;

    @NotNull
    @Positive
    private int totalQuantity;

    @NotNull
    @PositiveOrZero
    private int availableQuantity;

}
