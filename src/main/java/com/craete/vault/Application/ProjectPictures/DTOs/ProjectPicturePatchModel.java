package com.craete.vault.Application.ProjectPictures.DTOs;

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
public class ProjectPicturePatchModel {

    @NotNull
    private UUID id;

    @NotNull
    private UUID projectId;

    @NotNull
    private String storageKey;

    @PositiveOrZero
    private int order;

    private String caption;
}
