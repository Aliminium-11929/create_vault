package com.craete.vault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.craete.vault.VaultApplication;
import com.craete.vault.Application.Projects.DTOs.ProjectCreateModel;
import com.craete.vault.Application.Projects.DTOs.ProjectPatchModel;
import com.craete.vault.Application.Projects.DTOs.ProjectStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerTests {

	@LocalServerPort
	private int port;

	private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	@Autowired
	private FieldRepository fieldRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private ProjectMembershipRepository projectMembershipRepository;

	@Autowired
	private ProjectPictureRepository projectPictureRepository;

	@Autowired
	private ComponentReservationRepository componentReservationRepository;

	private final HttpHeaders headers = new HttpHeaders();

	@BeforeEach
	void setUp() {
		componentReservationRepository.deleteAll();
		projectMembershipRepository.deleteAll();
		projectPictureRepository.deleteAll();
		projectRepository.deleteAll();
		userRepository.deleteAll();
		fieldRepository.deleteAll();
	}

	@Test
	void createProject_persistsAndReturnsProjectStorageModel() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Sustainability").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("test-supervisor")
				.email("supervisor@example.com").role(UserRole.SUPERVISOR).field(field).build());

		ProjectCreateModel request = ProjectCreateModel.builder()
				.title("Smart irrigation system")
				.description("A connected project for resilient farmland")
				.academicYear(2025)
				.supervisorId(supervisor.getId())
				.fieldId(field.getId())
				.build();

		ResponseEntity<ProjectStorageModel> response = restTemplate.exchange(
				createURLWithPort("/projects"),
				HttpMethod.POST,
				new HttpEntity<>(request, headers),
				ProjectStorageModel.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(request.getTitle(), response.getBody().getTitle());
		assertEquals(request.getDescription(), response.getBody().getDescription());
		assertEquals(2025, response.getBody().getAcademicYear());
		assertEquals(supervisor.getId(), response.getBody().getSupervisorId());
		assertEquals(1L, projectRepository.count());
	}

	@Test
	void getAllProjects_returnsAllProjects() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Hydrology").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("supervisor")
				.email("hydro@example.com").role(UserRole.SUPERVISOR).field(field).build());
		Project project = projectRepository.saveAndFlush(Project.builder()
				.title("Flood forecast")
				.description("Predictions")
				.academicYear(2025)
				.field(field)
				.supervisor(supervisor)
				.build());

		ResponseEntity<ProjectStorageModel[]> response = restTemplate.getForEntity(
				createURLWithPort("/projects"),
				ProjectStorageModel[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().length);
		assertEquals(project.getId(), response.getBody()[0].getId());
	}

	@Test
	void getProjectById_returnsProjectStorageModel() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("AgriTech").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("lead")
				.email("agri@example.com").role(UserRole.SUPERVISOR).field(field).build());
		Project project = projectRepository.saveAndFlush(Project.builder()
				.title("Soil analysis")
				.description("Analyze soil moisture")
				.academicYear(2026)
				.field(field)
				.supervisor(supervisor)
				.build());

		ResponseEntity<ProjectStorageModel> response = restTemplate.exchange(
				createURLWithPort("/projects/" + project.getId()),
				HttpMethod.GET,
				new HttpEntity<>(headers),
				ProjectStorageModel.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(project.getId(), response.getBody().getId());
		assertEquals(project.getTitle(), response.getBody().getTitle());
	}

	@Test
	void patchProject_updatesProject() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Energy").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("energy-supervisor")
				.email("energy@example.com").role(UserRole.SUPERVISOR).field(field).build());
		Project project = projectRepository.saveAndFlush(Project.builder()
				.title("Old title")
				.description("Old description")
				.academicYear(2024)
				.field(field)
				.supervisor(supervisor)
				.build());

		ProjectPatchModel request = new ProjectPatchModel();
		request.setId(project.getId());
		request.setTitle("Updated title");
		request.setDescription("Updated description");
		request.setAcademicYear(2027);
		request.setSupervisorId(supervisor.getId());
		request.setFieldId(field.getId());

		ResponseEntity<ProjectStorageModel> response = restTemplate.exchange(
				createURLWithPort("/projects"),
				HttpMethod.PATCH,
				new HttpEntity<>(request, headers),
				ProjectStorageModel.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Updated title", response.getBody().getTitle());
		assertEquals("Updated description", response.getBody().getDescription());
		assertEquals(2027, response.getBody().getAcademicYear());
	}

	@Test
	void deleteProjectById_removesProject() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Ocean").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("ocean-supervisor")
				.email("ocean@example.com").role(UserRole.SUPERVISOR).field(field).build());
		Project project = projectRepository.saveAndFlush(Project.builder()
				.title("Wave monitor")
				.description("Track wave height")
				.academicYear(2025)
				.field(field)
				.supervisor(supervisor)
				.build());

		ResponseEntity<Void> response = restTemplate.exchange(
				createURLWithPort("/projects/" + project.getId()),
				HttpMethod.DELETE,
				new HttpEntity<>(headers),
				Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(0L, projectRepository.count());
	}

	@Test
	void createProject_withBlankTitle_returnsBadRequest() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Urban Planning").build());
		User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("planner")
				.email("planner@example.com").role(UserRole.SUPERVISOR).field(field).build());

		ProjectCreateModel request = ProjectCreateModel.builder()
				.title(" ")
				.description("Missing title")
				.academicYear(2025)
				.supervisorId(supervisor.getId())
				.fieldId(field.getId())
				.build();

		HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
				createURLWithPort("/projects"),
				HttpMethod.POST,
				new HttpEntity<>(request, headers),
				ProjectStorageModel.class));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void createProject_withUnknownSupervisor_returnsBadRequest() {
		Field field = fieldRepository.saveAndFlush(Field.builder().name("Renewables").build());

		ProjectCreateModel request = ProjectCreateModel.builder()
				.title("Solar monitor")
				.description("Monitoring")
				.academicYear(2025)
				.supervisorId(999_999L)
				.fieldId(field.getId())
				.build();

		HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
				createURLWithPort("/projects"),
				HttpMethod.POST,
				new HttpEntity<>(request, headers),
				ProjectStorageModel.class));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
	}

	@Test
	void getProjectById_whenProjectDoesNotExist_returnsNotFound() {
		UUID missingId = UUID.randomUUID();

		HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
				createURLWithPort("/projects/" + missingId),
				HttpMethod.GET,
				new HttpEntity<>(headers),
				ProjectStorageModel.class));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
	}

	private String createURLWithPort(String url) {
		return "http://localhost:" + port + url;
	}
}