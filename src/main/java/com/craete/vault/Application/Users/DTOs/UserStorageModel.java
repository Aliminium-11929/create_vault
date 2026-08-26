package com.craete.vault.Application.Users.DTOs;

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
public class UserStorageModel {

	private Long id;
	private String name;
	private String email;
	private String role;
	private UUID fieldId;
	private List<UUID> projectIds;

}
