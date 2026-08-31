package com.craete.vault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.web.client.RestTemplate;

import com.craete.vault.VaultApplication;
import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    @Autowired
    private ProjectPictureRepository projectPictureRepository;

    @Autowired
    private ComponentReservationRepository componentReservationRepository;

    @Autowired
    private UserRepository userRepository;

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
    void createUser_persistsAndReturnsUserStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Engineering").build());

        UserCreateModel request = new UserCreateModel();
        request.setId(101L);
        request.setName("Ada Lovelace");
        request.setEmail("ada@example.com");
        request.setRole(UserRole.STUDENT);
        request.setFieldId(field.getId());

        ResponseEntity<UserStorageModel> response = restTemplate.exchange(
                createURLWithPort("/users"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                UserStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getName(), response.getBody().getName());
        assertEquals(request.getEmail(), response.getBody().getEmail());
        assertEquals(request.getRole(), response.getBody().getRole());
        assertEquals(field.getId(), response.getBody().getFieldId());
        assertEquals(1L, userRepository.count());
    }

    @Test
    void getAllUsers_returnsAllUsers() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Architecture").build());
        User user = userRepository.saveAndFlush(User.builder().id(10L).name("Grace Hopper").email("grace@example.com")
                .role(UserRole.STUDENT).field(field).build());

        ResponseEntity<UserStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/users"),
                UserStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(user.getId(), response.getBody()[0].getId());
    }

    @Test
    void getUserById_returnsUserStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Biology").build());
        User user = userRepository.saveAndFlush(User.builder().id(11L).name("Alan Turing").email("turing@example.com")
                .role(UserRole.SUPERVISOR).field(field).build());

        ResponseEntity<UserStorageModel> response = restTemplate.exchange(
                createURLWithPort("/users/" + user.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals("Alan Turing", response.getBody().getName());
    }

    @Test
    void patchUser_updatesUser() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Physics").build());
        User user = userRepository.saveAndFlush(User.builder().id(12L).name("Old User").email("old@example.com")
                .role(UserRole.STUDENT).field(field).build());

        UserPatchModel request = new UserPatchModel();
        request.setId(user.getId());
        request.setName("Updated User");
        request.setEmail("updated@example.com");
        request.setRole(UserRole.SUPERVISOR);
        request.setFieldId(field.getId());

        ResponseEntity<UserStorageModel> response = restTemplate.exchange(
                createURLWithPort("/users"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                UserStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated User", response.getBody().getName());
        assertEquals("updated@example.com", response.getBody().getEmail());
        assertEquals(UserRole.SUPERVISOR, response.getBody().getRole());
    }

    @Test
    void deleteUserById_removesUser() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Robotics").build());
        User user = userRepository.saveAndFlush(User.builder().id(13L).name("Delete Me").email("delete@example.com")
                .role(UserRole.STUDENT).field(field).build());

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/users/" + user.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, userRepository.count());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
