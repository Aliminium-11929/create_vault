package com.craete.vault.Domain.ComponentReservations.Entities;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Domain.Users.Entities.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "component_reservations",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"borrower_id", "component_id"}
    )
)
public class ComponentReservation {

	@Id
	@Column(name = "reservation_id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "component_id", nullable = false)
	@NotNull
	private Component component;

	@NotNull
	private ZonedDateTime reservedFrom;

	@NotNull
	private ZonedDateTime reservedTo;

	@NotNull
	@Positive
	private int quantity;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "borrower_id", nullable = false)
	@NotNull
	private User borrower;
}
