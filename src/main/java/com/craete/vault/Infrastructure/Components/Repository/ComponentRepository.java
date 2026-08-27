package com.craete.vault.Infrastructure.Components.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.Components.Entities.Component;

public interface ComponentRepository extends JpaRepository<Component, UUID>{

}
