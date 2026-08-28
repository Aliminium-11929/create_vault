package com.craete.vault.Application.Components.DTOs;

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
public class ComponentStorageModel {

    private UUID id;
    private String name;
    private int totalQuantity;
    private int availableQuantity;
    private List<UUID> reservations;
}
