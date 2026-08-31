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
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipCreateModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipPatchModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Domain.Users.Entities.User.UserRole;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.ProjectMemberships.Repository.ProjectMembershipRepository;
import com.craete.vault.Infrastructure.Projects.Repository.ProjectRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

@SpringBootTest(classes = VaultApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectMembershipControllerTests {

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

    private final HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    void setUp() {
        projectMembershipRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
        fieldRepository.deleteAll();
    }

    @Test
    void createProjectMembership_persistsAndReturnsProjectMembershipStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Climate Tech").build());
        User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("The Supervisor")
                .email("supervisor@example.com").role(UserRole.SUPERVISOR).field(field).build());
        User member = userRepository.saveAndFlush(User.builder().id(2L).name("The Member").email("member@example.com")
                .role(UserRole.STUDENT).field(field).build());
        Project project = projectRepository.saveAndFlush(Project.builder()
                .title("Smart irrigation")
                .description("Optimized irrigation")
                .academicYear(2025)
                .field(field)
                .supervisor(supervisor)
                .build());

        ProjectMembershipCreateModel request = new ProjectMembershipCreateModel();
        request.setProjectId(project.getId());
        request.setMemberId(member.getId());

        ResponseEntity<ProjectMembershipStorageModel> response = restTemplate.exchange(
                createURLWithPort("/memberships"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                ProjectMembershipStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getProjectId(), response.getBody().getProjectId());
        assertEquals(request.getMemberId(), response.getBody().getMemberId());
        assertEquals(1L, projectMembershipRepository.count());
    }

    @Test
    void getAllProjectMemberships_returnsAllProjectMemberships() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Sustainability").build());
        User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Supervisor").email("sup@example.com")
                .role(UserRole.SUPERVISOR).field(field).build());
        User member = userRepository.saveAndFlush(User.builder().id(2L).name("Member").email("member@example.com")
                .role(UserRole.STUDENT).field(field).build());
        Project project = projectRepository.saveAndFlush(Project.builder()
                .title("Project A")
                .description("Description")
                .academicYear(2025)
                .field(field)
                .supervisor(supervisor)
                .build());
        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setMember(member);
        membership = projectMembershipRepository.saveAndFlush(membership);

        ResponseEntity<ProjectMembershipStorageModel[]> response = restTemplate.getForEntity(
                createURLWithPort("/memberships"),
                ProjectMembershipStorageModel[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(membership.getId(), response.getBody()[0].getId());
    }

    @Test
    void getProjectMembershipById_returnsProjectMembershipStorageModel() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Water").build());
        User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Supervisor Owner")
                .email("owner@example.com").role(UserRole.SUPERVISOR).field(field).build());
        User member = userRepository.saveAndFlush(User.builder().id(2L).name("Project Member")
                .email("projectmember@example.com").role(UserRole.STUDENT).field(field).build());
        Project project = projectRepository.saveAndFlush(Project.builder()
                .title("Project B")
                .description("Another project")
                .academicYear(2025)
                .field(field)
                .supervisor(supervisor)
                .build());
        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setMember(member);
        membership = projectMembershipRepository.saveAndFlush(membership);

        ResponseEntity<ProjectMembershipStorageModel> response = restTemplate.exchange(
                createURLWithPort("/memberships/" + membership.getId()),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectMembershipStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(membership.getId(), response.getBody().getId());
        assertEquals(project.getId(), response.getBody().getProjectId());
    }

    @Test
    void patchProjectMembership_updatesProjectMembership() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Renewables").build());
        User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Sup")
                .email("suprenew@example.com").role(UserRole.SUPERVISOR).field(field).build());
        User memberOne = userRepository.saveAndFlush(User.builder().id(2L).name("Member One")
                .email("memberone@example.com").role(UserRole.STUDENT).field(field).build());
        User memberTwo = userRepository.saveAndFlush(User.builder().id(3L).name("Member Two")
                .email("membertwo@example.com").role(UserRole.STUDENT).field(field).build());
        Project project = projectRepository.saveAndFlush(Project.builder()
                .title("Renewable project")
                .description("Test renewable project")
                .academicYear(2025)
                .field(field)
                .supervisor(supervisor)
                .build());
        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setMember(memberOne);
        membership = projectMembershipRepository.saveAndFlush(membership);

        ProjectMembershipPatchModel request = new ProjectMembershipPatchModel();
        request.setId(membership.getId());
        request.setProjectId(project.getId());
        request.setMemberId(memberTwo.getId());

        ResponseEntity<ProjectMembershipStorageModel> response = restTemplate.exchange(
                createURLWithPort("/memberships"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                ProjectMembershipStorageModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(memberTwo.getId(), response.getBody().getMemberId());
    }

    @Test
    void deleteProjectMembershipById_removesProjectMembership() {
        Field field = fieldRepository.saveAndFlush(Field.builder().name("Food").build());
        User supervisor = userRepository.saveAndFlush(User.builder().id(1L).name("Food Supervisor")
                .email("foodsup@example.com").role(UserRole.SUPERVISOR).field(field).build());
        User member = userRepository.saveAndFlush(User.builder().id(2L).name("Food Member")
                .email("foodmember@example.com").role(UserRole.STUDENT).field(field).build());
        Project project = projectRepository.saveAndFlush(Project.builder()
                .title("Food project")
                .description("Food production")
                .academicYear(2025)
                .field(field)
                .supervisor(supervisor)
                .build());
        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setMember(member);
        membership = projectMembershipRepository.saveAndFlush(membership);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/memberships/" + membership.getId()),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, projectMembershipRepository.count());
    }

    private String createURLWithPort(String url) {
        return "http://localhost:" + port + url;
    }
}
