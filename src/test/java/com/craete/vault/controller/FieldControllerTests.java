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
import com.craete.vault.Application.Fields.DTOs.FieldCreateModel;
import com.craete.vault.Application.Fields.DTOs.FieldPatchModel;
import com.craete.vault.Application.Fields.DTOs.FieldStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FieldControllerTests {

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
    void createField_persistsAndReturnsFieldStorageModel() {
        FieldCreateModel request = new FieldCreateModel();
        request.setFieldName("Sustainability");

        ResponseEntity<FieldStorageModel> response = restTemplate.exchange(
                createURLWithPort("/fields"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                FieldStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getFieldName(), response.getBody().getFieldName());
        assertEquals(1L, fieldRepository.count());
    }

    @Test
    void getAllFields_returnsAllFields() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Engineering").build());

        ResponseEntity<FieldStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/fields"),
                FieldStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(field.getId(), response.getBody()[0].getId());
    }

    @Test
    void getFieldById_returnsFieldStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Design").build());

        ResponseEntity<FieldStorageModel> response = restTemplate.exchange(
                createURLWithPort("/fields/" + field.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                FieldStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(field.getId(), response.getBody().getId());
        assertEquals("Design", response.getBody().getFieldName());
    }

    @Test
    void patchField_updatesField() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Old Field").build());

        FieldPatchModel request = new FieldPatchModel();
        request.setId(field.getId());
        request.setFieldName("Updated Field");

        ResponseEntity<FieldStorageModel> response = restTemplate.exchange(
                createURLWithPort("/fields"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                FieldStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Field", response.getBody().getFieldName());
    }

    @Test
    void deleteFieldById_removesField() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("To delete").build());

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/fields/" + field.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, fieldRepository.count());
    }

    @Test
    void createField_withNullName_returnsBadRequest() {
        FieldCreateModel request = new FieldCreateModel();
        request.setFieldName(null);

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/fields"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                FieldStorageModel.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getFieldById_whenFieldDoesNotExist_returnsNotFound() {
        UUID missingId = UUID.randomUUID();

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/fields/" + missingId),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                FieldStorageModel.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteFieldById_whenFieldDoesNotExist_returnsNotFound() {
        UUID missingId = UUID.randomUUID();

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/fields/" + missingId),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
