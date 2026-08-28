package com.craete.vault.Application.Fields.DTOs;

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
public class FieldStorageModel {

    private UUID id;
    private String fieldName;
    private List<UUID> projects;

}
