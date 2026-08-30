package com.craete.vault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.craete.vault.VaultApplication;
import com.craete.vault.Application.Projects.DTOs.ProjectCreateModel;
import com.craete.vault.Application.Projects.DTOs.ProjectStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VaultIntegrationTests {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private FieldRepository fieldRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	void setUp() {
		fieldRepository.deleteAll();
		userRepository.deleteAll();
		projectRepository.deleteAll();
	}

	@Test
	public void createProject_persistsAndReturnsProjectStorageModel() {
		Field testField = Field.builder().name("Sustainability").build();
		Field persistedField = fieldRepository.saveAndFlush(testField);

		User testSupervisor = User.builder().id(1L).name("test-supervisor").email("supervisor@example.com")
				.role(UserRole.SUPERVISOR).field(persistedField).build();
		userRepository.saveAndFlush(testSupervisor);

		ProjectCreateModel requestProject = ProjectCreateModel.builder().title("Smart irrigation system")
				.description("A connected project for resilient farmland")
				.academicYear(2025).supervisorId(testSupervisor.getId()).fieldId(persistedField.getId()).build();

		HttpEntity<ProjectCreateModel> postRequest = new HttpEntity<>(requestProject, headers);

		ResponseEntity<ProjectStorageModel> response = restTemplate.exchange(
				createURLWithPort("/projects"),
				HttpMethod.POST, postRequest, ProjectStorageModel.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(requestProject.getTitle(), response.getBody().getTitle());
		assertEquals(requestProject.getDescription(), response.getBody().getDescription());
		assertEquals(2025, response.getBody().getAcademicYear());
		assertEquals(testSupervisor.getId(), response.getBody().getSupervisorId());
		assertEquals(1L, projectRepository.count());
	}

	private String createURLWithPort(String url) {
		return "http://localhost:" + port + url;
	}
}
