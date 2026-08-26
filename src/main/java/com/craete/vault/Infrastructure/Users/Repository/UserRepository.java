package com.craete.vault.Infrastructure.Users.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.craete.vault.Domain.Users.Entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
