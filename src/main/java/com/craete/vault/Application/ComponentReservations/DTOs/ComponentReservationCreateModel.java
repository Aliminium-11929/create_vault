package com.craete.vault.Application.ComponentReservations.DTOs;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentReservationCreateModel {

    @NotNull
    private UUID componentId;

    @NotNull
    private ZonedDateTime reservedFrom;

    @NotNull
    private ZonedDateTime reservedTo;

    @NotNull
    @Positive
    private int quantity;

    @NotNull
    private Long borrowerId;

}
