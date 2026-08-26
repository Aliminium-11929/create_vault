package com.craete.vault.Application.Users.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;

@Component
public class UserMapper {

    public User toEntity(UserCreateModel userCreateModel) {
        if (userCreateModel == null) {
            return null;
        }

        User user = new User();
        user.setName(userCreateModel.getName());
        user.setEmail(userCreateModel.getEmail());
        user.setRole(userCreateModel.getRole());
        user.setField(toField(userCreateModel.getFieldId()));
        user.setProjects(toProjects(userCreateModel.getProjectIds()));
        return user;
    }

    public User applyPatch(UserPatchModel userPatchModel, User user) {
        if (userPatchModel == null || user == null) {
            return user;
        }

        user.setName(userPatchModel.getName());
        user.setEmail(userPatchModel.getEmail());
        user.setRole(userPatchModel.getRole());
        user.setField(toField(userPatchModel.getFieldId()));
        user.setProjects(toProjects(userPatchModel.getProjectIds()));
        return user;
    }

    public UserStorageModel toStorageModel(User user) {
        if (user == null) {
            return null;
        }

        List<UUID> projectIds = user.getProjects() == null
            ? List.of()
            : user.getProjects().stream()
                .map(project -> project.getProjectId())
                .toList();

        return new UserStorageModel(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getField() != null ? user.getField().getFieldId() : null,
            projectIds
        );
    }

    public List<UserStorageModel> toStorageModels(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
            .map(this::toStorageModel)
            .toList();
    }

    private Field toField(UUID fieldId) {
        if (fieldId == null) {
            return null;
        }

        Field field = new Field();
        field.setFieldId(fieldId);
        return field;
    }

    private List<Project> toProjects(List<UUID> projectIds) {
        if (projectIds == null) {
            return List.of();
        }

        return IntStream.range(0, projectIds.size())
            .mapToObj(index -> {
                Project project = new Project();
                project.setProjectId(projectIds.get(index));
                return project;
            })
            .toList();
    }
}