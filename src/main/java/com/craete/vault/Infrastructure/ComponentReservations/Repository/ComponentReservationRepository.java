package com.craete.vault.Infrastructure.ComponentReservations.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.ComponentReservations.Entities.ComponentReservation;

public interface ComponentReservationRepository extends JpaRepository<ComponentReservation, UUID>{

}
