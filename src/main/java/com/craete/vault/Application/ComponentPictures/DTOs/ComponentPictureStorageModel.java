package com.craete.vault.Application.ComponentPictures.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentPictureStorageModel {

    private UUID id;
    private UUID componentId;
    private String storageKey;
    private int order;
    private String caption;

}
