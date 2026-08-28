package com.craete.vault.Application.Projects.DTOs;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPatchModel {

    @NotNull
    private UUID id;

    @NotBlank
    private String title;

    private String description; // nullable

    @NotNull
    @Min(2000)
    @Max(2100)
    private int academicYear;

    private UUID tutorId; // nullable

    @NotNull
    private UUID supervisorId;

    @NotNull
    private UUID fieldId;

    private List<UUID> pictures; // refers to project pictures
    private List<UUID> members; // refers to project memberships


}
