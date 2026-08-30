package com.craete.vault.Application.Projects.DTOs;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateModel {

    @NotBlank
    private String title;

    private String description; // nullable

    @NotNull
    @Min(2000)
    @Max(2100)
    private int academicYear;

    private Long tutorId; // nullable

    @NotNull
    private Long supervisorId;

    @NotNull
    private UUID fieldId;

}
