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
import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Infrastructure.ComponentPictures.Repository.ComponentPictureRepository;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComponentControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentPictureRepository componentPictureRepository;

    @Autowired
    private ComponentReservationRepository componentReservationRepository;

    private final HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
        componentReservationRepository.deleteAll();
        componentPictureRepository.deleteAll();
        componentRepository.deleteAll();
    }

    @Test
    void createComponent_persistsAndReturnsComponentStorageModel() {
        ComponentCreateModel request = new ComponentCreateModel();
        request.setName("Soil Sensor");
        request.setTotalQuantity(20);
        request.setAvailableQuantity(18);

        ResponseEntity<ComponentStorageModel> response = restTemplate.exchange(
                createURLWithPort("/components"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ComponentStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getName(), response.getBody().getName());
        assertEquals(request.getTotalQuantity(), response.getBody().getTotalQuantity());
        assertEquals(request.getAvailableQuantity(), response.getBody().getAvailableQuantity());
        assertEquals(1L, componentRepository.count());
    }

    @Test
    void getAllComponents_returnsAllComponents() {
        Component component = new Component();
        component.setName("Water Meter");
        component.setTotalQuantity(5);
        component.setAvailableQuantity(4);
        component = componentRepository.saveAndFlush(component);

        ResponseEntity<ComponentStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/components"),
                ComponentStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(component.getId(), response.getBody()[0].getId());
    }

    @Test
    void getComponentById_returnsComponentStorageModel() {
        Component component = new Component();
        component.setName("Air Filter");
        component.setTotalQuantity(7);
        component.setAvailableQuantity(6);
        component = componentRepository.saveAndFlush(component);

        ResponseEntity<ComponentStorageModel> response = restTemplate.exchange(
                createURLWithPort("/components/" + component.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ComponentStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(component.getId(), response.getBody().getId());
        assertEquals("Air Filter", response.getBody().getName());
    }

    @Test
    void patchComponent_updatesComponent() {
        Component component = new Component();
        component.setName("Old Sensor");
        component.setTotalQuantity(10);
        component.setAvailableQuantity(9);
        component = componentRepository.saveAndFlush(component);

        ComponentPatchModel request = new ComponentPatchModel();
        request.setId(component.getId());
        request.setName("Updated Sensor");
        request.setTotalQuantity(15);
        request.setAvailableQuantity(14);

        ResponseEntity<ComponentStorageModel> response = restTemplate.exchange(
                createURLWithPort("/components"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                ComponentStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Sensor", response.getBody().getName());
        assertEquals(15, response.getBody().getTotalQuantity());
        assertEquals(14, response.getBody().getAvailableQuantity());
    }

    @Test
    void deleteComponentById_removesComponent() {
        Component component = new Component();
        component.setName("Pump");
        component.setTotalQuantity(4);
        component.setAvailableQuantity(2);
        component = componentRepository.saveAndFlush(component);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/components/" + component.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, componentRepository.count());
    }

    @Test
    void createComponent_withInvalidAvailableQuantity_returnsBadRequest() {
        ComponentCreateModel request = new ComponentCreateModel();
        request.setName("Broken Sensor");
        request.setTotalQuantity(10);
        request.setAvailableQuantity(-1);

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/components"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ComponentStorageModel.class));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void getComponentById_whenComponentDoesNotExist_returnsNotFound() {
        UUID missingId = UUID.randomUUID();

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/components/" + missingId),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ComponentStorageModel.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteComponentById_whenComponentDoesNotExist_returnsNotFound() {
        UUID missingId = UUID.randomUUID();

        HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class, () -> restTemplate.exchange(
                createURLWithPort("/components/" + missingId),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
