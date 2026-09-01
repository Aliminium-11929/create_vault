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
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureCreateModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPicturePatchModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.ProjectPictures.Entities.ProjectPicture;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectPictureControllerTests {

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
        private ProjectPictureRepository projectPictureRepository;

        private final HttpHeaders headers = new HttpHeaders();

        @BeforeEach
        void setUp() {
                projectPictureRepository.deleteAll();
                projectRepository.deleteAll();
                userRepository.deleteAll();
                fieldRepository.deleteAll();
        }

        @Test
        void createProjectPicture_persistsAndReturnsProjectPictureStorageModel() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("AgriTech").build());
                User supervisor = userRepository
                                .saveAndFlush(User.builder().id(1L).name("Supervisor").email("supervisor@example.com")
                                                .role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Drone crop mapping")
                                .description("Monitor crop health")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());

                ProjectPictureCreateModel request = new ProjectPictureCreateModel();
                request.setProjectId(project.getId());
                request.setStorageKey("projects/project-1/cover.png");
                request.setOrder(1);
                request.setCaption("Cover image");

                ResponseEntity<ProjectPictureStorageModel> response = restTemplate.exchange(
                                createURLWithPort("/projectpictures"),
                                HttpMethod.POST,
                                new HttpEntity<>(request, headers),
                                ProjectPictureStorageModel.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(request.getProjectId(), response.getBody().getProjectId());
                assertEquals(request.getStorageKey(), response.getBody().getStorageKey());
                assertEquals(request.getOrder(), response.getBody().getOrder());
                assertEquals(1L, projectPictureRepository.count());
        }

        @Test
        void getAllProjectPictures_returnsAllProjectPictures() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("AgriTech").build());
                User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Agri Supervisor")
                                .email("agri.sup@example.com").role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Crop monitor")
                                .description("Hope")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());
                ProjectPicture picture = new ProjectPicture();
                picture.setProject(project);
                picture.setStorageKey("projects/monitor.png");
                picture.setOrder(1);
                picture.setCaption("Monitor");
                picture = projectPictureRepository.saveAndFlush(picture);

                ResponseEntity<ProjectPictureStorageModel[]> response = restTemplate.getForEntity(
                                createURLWithPort("/projectpictures"),
                                ProjectPictureStorageModel[].class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(1, response.getBody().length);
                assertEquals(picture.getId(), response.getBody()[0].getId());
        }

        @Test
        void getProjectPictureById_returnsProjectPictureStorageModel() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("Smart Farm").build());
                User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Farm Supervisor")
                                .email("farm.sup@example.com").role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Farm project")
                                .description("Track field output")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());
                ProjectPicture picture = new ProjectPicture();
                picture.setProject(project);
                picture.setStorageKey("projects/farm.png");
                picture.setOrder(2);
                picture.setCaption("Farm overview");
                picture = projectPictureRepository.saveAndFlush(picture);

                ResponseEntity<ProjectPictureStorageModel> response = restTemplate.exchange(
                                createURLWithPort("/projectpictures/" + picture.getId()),
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                ProjectPictureStorageModel.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(picture.getId(), response.getBody().getId());
                assertEquals("projects/farm.png", response.getBody().getStorageKey());
        }

        @Test
        void patchProjectPicture_updatesProjectPicture() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("Forestry").build());
                User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Forest Supervisor")
                                .email("forest@example.com").role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Forest project")
                                .description("Tree cover")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());
                ProjectPicture picture = new ProjectPicture();
                picture.setProject(project);
                picture.setStorageKey("projects/old.png");
                picture.setOrder(1);
                picture.setCaption("Old caption");
                picture = projectPictureRepository.saveAndFlush(picture);

                ProjectPicturePatchModel request = new ProjectPicturePatchModel();
                request.setId(picture.getId());
                request.setProjectId(project.getId());
                request.setStorageKey("projects/new.png");
                request.setOrder(3);
                request.setCaption("New caption");

                ResponseEntity<ProjectPictureStorageModel> response = restTemplate.exchange(
                                createURLWithPort("/projectpictures"),
                                HttpMethod.PATCH,
                                new HttpEntity<>(request, headers),
                                ProjectPictureStorageModel.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("projects/new.png", response.getBody().getStorageKey());
                assertEquals(3, response.getBody().getOrder());
                assertEquals("New caption", response.getBody().getCaption());
        }

        @Test
        void deleteProjectPictureById_removesProjectPicture() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("Crops").build());
                User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Crops Supervisor")
                                .email("crops.sup@example.com").role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Crops test")
                                .description("Data")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());
                ProjectPicture picture = new ProjectPicture();
                picture.setProject(project);
                picture.setStorageKey("projects/delete.png");
                picture.setOrder(1);
                picture.setCaption("Delete me");
                picture = projectPictureRepository.saveAndFlush(picture);

                ResponseEntity<Void> response = restTemplate.exchange(
                                createURLWithPort("/projectpictures/" + picture.getId()),
                                HttpMethod.DELETE,
                                new HttpEntity<>(headers),
                                Void.class);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(0L, projectPictureRepository.count());
        }

        @Test
        void createProjectPicture_withNullStorageKey_returnsBadRequest() {
                Field field = fieldRepository.saveAndFlush(Field.builder().name("Forest").build());
                User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Forest Supervisor")
                                .email("forest.sup@example.com").role(UserRole.SUPERVISOR).field(field).build());
                Project project = projectRepository.saveAndFlush(Project.builder()
                                .title("Forest overview")
                                .description("Overview")
                                .academicYear(2025)
                                .field(field)
                                .supervisor(supervisor)
                                .build());

                ProjectPictureCreateModel request = new ProjectPictureCreateModel();
                request.setProjectId(project.getId());
                request.setStorageKey(null);
                request.setOrder(1);
                request.setCaption("Blank path");

                HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                                () -> restTemplate.exchange(
                                                createURLWithPort("/projectpictures"),
                                                HttpMethod.POST,
                                                new HttpEntity<>(request, headers),
                                                ProjectPictureStorageModel.class));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        }

        @Test
        void createProjectPicture_withUnknownProject_returnsBadRequest() {
                ProjectPictureCreateModel request = new ProjectPictureCreateModel();
                request.setProjectId(UUID.randomUUID());
                request.setStorageKey("projects/missing.png");
                request.setOrder(1);
                request.setCaption("Missing project");

                HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                                () -> restTemplate.exchange(
                                                createURLWithPort("/projectpictures"),
                                                HttpMethod.POST,
                                                new HttpEntity<>(request, headers),
                                                ProjectPictureStorageModel.class));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        }

        @Test
        void getProjectPictureById_whenPictureDoesNotExist_returnsNotFound() {
                UUID missingId = UUID.randomUUID();

                HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                                () -> restTemplate.exchange(
                                                createURLWithPort("/projectpictures/" + missingId),
                                                HttpMethod.GET,
                                                new HttpEntity<>(headers),
                                                ProjectPictureStorageModel.class));

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }

        private String createURLWithPort(String url) {
                return "http://localhost:" + port + url;
        }
}
