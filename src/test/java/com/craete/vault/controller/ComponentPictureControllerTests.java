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
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureCreateModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;
import com.craete.vault.Domain.ComponentPictures.Entities.ComponentPicture;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Infrastructure.ComponentPictures.Repository.ComponentPictureRepository;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ComponentPictureControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private ComponentPictureRepository componentPictureRepository;

    private final HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
        componentPictureRepository.deleteAll();
        componentRepository.deleteAll();
    }

    @Test
    void createComponentPicture_persistsAndReturnsComponentPictureStorageModel() {
        Component component = new Component();
        component.setName("Hydro Sensor");
        component.setTotalQuantity(10);
        component.setAvailableQuantity(8);
        component = componentRepository.saveAndFlush(component);

        ComponentPictureCreateModel request = new ComponentPictureCreateModel();
        request.setComponentId(component.getId());
        request.setStorageKey("components/component-1/overview.png");
        request.setOrder(1);
        request.setCaption("Overview");

        ResponseEntity<ComponentPictureStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentpictures"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ComponentPictureStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getComponentId(), response.getBody().getComponentId());
        assertEquals(request.getStorageKey(), response.getBody().getStorageKey());
        assertEquals(request.getOrder(), response.getBody().getOrder());
        assertEquals(1L, componentPictureRepository.count());
    }

    @Test
    void getAllComponentPictures_returnsAllComponentPictures() {
        Component component = new Component();
        component.setName("Valve");
        component.setTotalQuantity(6);
        component.setAvailableQuantity(4);
        component = componentRepository.saveAndFlush(component);
        ComponentPicture picture = new ComponentPicture();
        picture.setComponent(component);
        picture.setStorageKey("components/valve.png");
        picture.setOrder(1);
        picture.setCaption("Valve overview");
        picture = componentPictureRepository.saveAndFlush(picture);

        ResponseEntity<ComponentPictureStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/componentpictures"),
                ComponentPictureStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(picture.getId(), response.getBody()[0].getId());
    }

    @Test
    void getComponentPictureById_returnsComponentPictureStorageModel() {
        Component component = new Component();
        component.setName("Pump");
        component.setTotalQuantity(8);
        component.setAvailableQuantity(6);
        component = componentRepository.saveAndFlush(component);
        ComponentPicture picture = new ComponentPicture();
        picture.setComponent(component);
        picture.setStorageKey("components/pump.png");
        picture.setOrder(2);
        picture.setCaption("Pump detail");
        picture = componentPictureRepository.saveAndFlush(picture);

        ResponseEntity<ComponentPictureStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentpictures/" + picture.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ComponentPictureStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(picture.getId(), response.getBody().getId());
        assertEquals("components/pump.png", response.getBody().getStorageKey());
    }

    @Test
    void patchComponentPicture_updatesComponentPicture() {
        Component component = new Component();
        component.setName("Sensor");
        component.setTotalQuantity(12);
        component.setAvailableQuantity(10);
        component = componentRepository.saveAndFlush(component);
        ComponentPicture picture = new ComponentPicture();
        picture.setComponent(component);
        picture.setStorageKey("components/old.png");
        picture.setOrder(1);
        picture.setCaption("Old caption");
        picture = componentPictureRepository.saveAndFlush(picture);

        ComponentPicturePatchModel request = new ComponentPicturePatchModel();
        request.setId(picture.getId());
        request.setComponentId(component.getId());
        request.setStorageKey("components/new.png");
        request.setOrder(3);
        request.setCaption("New caption");

        ResponseEntity<ComponentPictureStorageModel> response = restTemplate.exchange(
                createURLWithPort("/componentpictures"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                ComponentPictureStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("components/new.png", response.getBody().getStorageKey());
        assertEquals(3, response.getBody().getOrder());
        assertEquals("New caption", response.getBody().getCaption());
    }

    @Test
    void deleteComponentPictureById_removesComponentPicture() {
        Component component = new Component();
        component.setName("Filter");
        component.setTotalQuantity(3);
        component.setAvailableQuantity(1);
        component = componentRepository.saveAndFlush(component);
        ComponentPicture picture = new ComponentPicture();
        picture.setComponent(component);
        picture.setStorageKey("components/delete.png");
        picture.setOrder(1);
        picture.setCaption("Delete me");
        picture = componentPictureRepository.saveAndFlush(picture);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/componentpictures/" + picture.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, componentPictureRepository.count());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
