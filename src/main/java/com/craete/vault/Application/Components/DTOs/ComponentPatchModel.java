package com.craete.vault.Application.Components.DTOs;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private int quantity;

    @NotNull
    private List<UUID> reservations;
}
