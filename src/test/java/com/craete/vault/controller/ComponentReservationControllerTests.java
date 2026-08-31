package com.craete.vault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZonedDateTime;

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
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;
import com.craete.vault.Domain.ComponentReservations.Entities.ComponentReservation;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.ComponentPictures.Repository.ComponentPictureRepository;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.ProjectPictures.Repository.ProjectPictureRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComponentReservationControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentReservationRepository componentReservationRepository;

    @Autowired
    private ComponentPictureRepository componentPictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    @Autowired
    private ProjectPictureRepository projectPictureRepository;

    private final HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
        componentReservationRepository.deleteAll();
        componentPictureRepository.deleteAll();
        componentRepository.deleteAll();
        projectMembershipRepository.deleteAll();
        projectPictureRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        fieldRepository.deleteAll();
    }

    @Test
    void createComponentReservation_persistsAndReturnsComponentReservationStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Field Lab").build());
        User borrower = userRepository.saveAndFlush(User.builder().id(1L).name("Borrower").email("borrower@example.com")
                .role(UserRole.STUDENT).field(field).build());
        Component component = new Component();
        component.setName("Water Tank");
        component.setTotalQuantity(15);
        component.setAvailableQuantity(12);
        component = componentRepository.saveAndFlush(component);

        ZonedDateTime from = ZonedDateTime.now().plusDays(1);
        ZonedDateTime to = from.plusDays(3);

        ComponentReservationCreateModel request = new ComponentReservationCreateModel();
        request.setComponentId(component.getId());
        request.setReservedFrom(from);
        request.setReservedTo(to);
        request.setQuantity(2);
        request.setBorrowerId(borrower.getId());

        ResponseEntity<ComponentReservationStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentReservations"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ComponentReservationStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getComponentId(), response.getBody().getComponentId());
        assertEquals(request.getQuantity(), response.getBody().getQuantity());
        assertEquals(request.getBorrowerId(), response.getBody().getBorrowerId());
        assertEquals(1L, componentReservationRepository.count());
    }

    @Test
    void getAllComponentReservations_returnsAllComponentReservations() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Field Lab").build());
        User borrower = userRepository
                .saveAndFlush(User.builder().id(2L).name("Borrower Two").email("borrower2@example.com")
                        .role(UserRole.STUDENT).field(field).build());
        Component component = new Component();
        component.setName("Valve");
        component.setTotalQuantity(8);
        component.setAvailableQuantity(6);
        component = componentRepository.saveAndFlush(component);
        ComponentReservation reservation = new ComponentReservation();
        reservation.setId(java.util.UUID.randomUUID());
        reservation.setComponent(component);
        reservation.setBorrower(borrower);
        reservation.setReservedFrom(ZonedDateTime.now().plusDays(3));
        reservation.setReservedTo(ZonedDateTime.now().plusDays(5));
        reservation.setQuantity(2);
        reservation = componentReservationRepository.saveAndFlush(reservation);

        ResponseEntity<ComponentReservationStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/componentReservations"),
                ComponentReservationStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(reservation.getId(), response.getBody()[0].getId());
    }

    @Test
    void getComponentReservationById_returnsComponentReservationStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Science Lab").build());
        User borrower = userRepository
                .saveAndFlush(User.builder().id(3L).name("Borrower Three").email("borrower3@example.com")
                        .role(UserRole.STUDENT).field(field).build());
        Component component = new Component();
        component.setName("Sensor Pack");
        component.setTotalQuantity(11);
        component.setAvailableQuantity(9);
        component = componentRepository.saveAndFlush(component);
        ComponentReservation reservation = new ComponentReservation();
        reservation.setId(java.util.UUID.randomUUID());
        reservation.setComponent(component);
        reservation.setBorrower(borrower);
        reservation.setReservedFrom(ZonedDateTime.now().plusDays(1));
        reservation.setReservedTo(ZonedDateTime.now().plusDays(3));
        reservation.setQuantity(3);
        reservation = componentReservationRepository.saveAndFlush(reservation);

        ResponseEntity<ComponentReservationStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentReservations/" + reservation.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ComponentReservationStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservation.getId(), response.getBody().getId());
        assertEquals(component.getId(), response.getBody().getComponentId());
    }

    @Test
    void patchComponentReservation_updatesComponentReservation() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Workshop").build());
        User borrower = userRepository
                .saveAndFlush(User.builder().id(4L).name("Borrower Four").email("borrower4@example.com")
                        .role(UserRole.STUDENT).field(field).build());
        Component component = new Component();
        component.setName("Power Cell");
        component.setTotalQuantity(12);
        component.setAvailableQuantity(10);
        component = componentRepository.saveAndFlush(component);
        ComponentReservation reservation = new ComponentReservation();
        reservation.setId(java.util.UUID.randomUUID());
        reservation.setComponent(component);
        reservation.setBorrower(borrower);
        reservation.setReservedFrom(ZonedDateTime.now().plusDays(2));
        reservation.setReservedTo(ZonedDateTime.now().plusDays(4));
        reservation.setQuantity(1);
        reservation = componentReservationRepository.saveAndFlush(reservation);

        ZonedDateTime updatedFrom = ZonedDateTime.now().plusDays(7);
        ZonedDateTime updatedTo = updatedFrom.plusDays(2);

        ComponentReservationPatchModel request = new ComponentReservationPatchModel();
        request.setId(reservation.getId());
        request.setComponentId(component.getId());
        request.setReservedFrom(updatedFrom);
        request.setReservedTo(updatedTo);
        request.setQuantity(4);
        request.setBorrowerId(borrower.getId());

        ResponseEntity<ComponentReservationStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentReservations"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                ComponentReservationStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().getQuantity());
        assertEquals(borrower.getId(), response.getBody().getBorrowerId());
    }

    @Test
    void deleteComponentReservationById_removesComponentReservation() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Battery Lab").build());
        User borrower = userRepository
                .saveAndFlush(User.builder().id(5L).name("Borrower Five").email("borrower5@example.com")
                        .role(UserRole.STUDENT).field(field).build());
        Component component = new Component();
        component.setName("Battery");
        component.setTotalQuantity(20);
        component.setAvailableQuantity(18);
        component = componentRepository.saveAndFlush(component);
        ComponentReservation reservation = new ComponentReservation();
        reservation.setId(java.util.UUID.randomUUID());
        reservation.setComponent(component);
        reservation.setBorrower(borrower);
        reservation.setReservedFrom(ZonedDateTime.now().plusDays(5));
        reservation.setReservedTo(ZonedDateTime.now().plusDays(7));
        reservation.setQuantity(2);
        reservation = componentReservationRepository.saveAndFlush(reservation);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/componentReservations/" + reservation.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, componentReservationRepository.count());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
