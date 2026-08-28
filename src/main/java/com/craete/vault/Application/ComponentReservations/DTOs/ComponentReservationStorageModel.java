package com.craete.vault.Application.ComponentReservations.DTOs;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentReservationStorageModel {

    private UUID id;
    private UUID componentId;
    private ZonedDateTime reservedFrom;
    private ZonedDateTime reservedTo;
    private int quantity;
    private Long borrowerId;

}
