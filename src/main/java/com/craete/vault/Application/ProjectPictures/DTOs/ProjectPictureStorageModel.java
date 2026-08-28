package com.craete.vault.Application.ProjectPictures.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPictureStorageModel {

    private UUID id;
    private UUID projectId;
    private String storageKey;
    private int order;
    private String caption;
}
