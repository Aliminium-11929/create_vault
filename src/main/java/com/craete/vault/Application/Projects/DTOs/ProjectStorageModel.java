package com.craete.vault.Application.Projects.DTOs;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStorageModel {

    private UUID id;
    private String title;
    private String description;
    private int academicYear;
    private Long tutorId;
    private Long supervisorId;
    private UUID fieldId;
    private List<UUID> pictures;
    private List<UUID> members;


}
