package com.craete.vault.Application.ComponentPictures.DTOs;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPictureCreateModel {

    @NotNull
    private UUID componentId;

    @NotNull
    private String storageKey;

    @NotNull
    @PositiveOrZero
    private int order;

    private String caption; // nullable

}
