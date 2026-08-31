package com.craete.vault.Application.Users.DTOs;

import java.util.List;
import java.util.UUID;

import com.craete.vault.Domain.Users.Entities.User.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateModel {

	@NotNull
	private Long Id;

	@NotBlank
	private String name;

	@Email
	@NotBlank
	private String email;

	@NotNull
	private UserRole role;

	@NotNull
	private UUID fieldId;

	private List<UUID> projectIds;

}
