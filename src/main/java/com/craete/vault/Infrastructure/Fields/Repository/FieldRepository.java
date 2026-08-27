package com.craete.vault.Infrastructure.Fields.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.Fields.Entities.Field;

public interface FieldRepository extends JpaRepository<Field, UUID>{

}
