package com.craete.vault.Domain.Components.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.craete.vault.Domain.ComoponentReservations.Entities.ComponentReservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "components"
)
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "component_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "component_name", updatable = false, nullable = false)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(name = "component_quantity", updatable = false, nullable = false)
    private Long quantity;

    @OneToMany(
        mappedBy = "component",
        orphanRemoval = true,
        cascade = CascadeType.ALL
    )
    private List<ComponentReservation> reservations = new ArrayList<>();
}
