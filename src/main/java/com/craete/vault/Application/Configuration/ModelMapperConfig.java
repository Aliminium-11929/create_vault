package com.craete.vault.Application.Configuration;

import java.util.List;
import java.util.UUID;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<UUID, Field> fieldFromIdConverter = context -> {
            UUID fieldId = context.getSource();
            if (fieldId == null) {
                return null;
            }

            Field field = new Field();
            field.setFieldId(fieldId);
            return field;
        };

        Converter<List<UUID>, List<ProjectMembership>> projectMembershipsFromIdsConverter = context -> {
            List<UUID> projectIds = context.getSource();
            if (projectIds == null) {
                return List.of();
            }

            User destinationUser = context.getParent() == null ? null : (User) context.getParent().getDestination();
            return projectIds.stream()
                .map(projectId -> {
                    Project project = new Project();
                    project.setId(projectId);

                    ProjectMembership membership = new ProjectMembership();
                    membership.setProject(project);
                    membership.setMember(destinationUser);
                    return membership;
                })
                .toList();
        };

        Converter<Field, UUID> fieldIdConverter = context -> {
            Field field = context.getSource();
            return field == null ? null : field.getFieldId();
        };

        Converter<List<ProjectMembership>, List<UUID>> projectIdsConverter = context -> {
            List<ProjectMembership> memberships = context.getSource();
            if (memberships == null) {
                return List.of();
            }

            return memberships.stream()
                .map(ProjectMembership::getProject)
                .filter(project -> project != null && project.getId() != null)
                .map(Project::getId)
                .toList();
        };

        modelMapper.createTypeMap(UserCreateModel.class, User.class)
            .addMappings(mapper -> {
                mapper.using(fieldFromIdConverter).map(UserCreateModel::getFieldId, User::setField);
                mapper.using(projectMembershipsFromIdsConverter).map(UserCreateModel::getProjectIds, User::setProjectMemberships);
            });

        modelMapper.createTypeMap(UserPatchModel.class, User.class)
            .addMappings(mapper -> {
                mapper.skip(User::setId);
                mapper.using(fieldFromIdConverter).map(UserPatchModel::getFieldId, User::setField);
                mapper.using(projectMembershipsFromIdsConverter).map(UserPatchModel::getProjectIds, User::setProjectMemberships);
            });

        modelMapper.createTypeMap(User.class, UserStorageModel.class)
            .addMappings(mapper -> {
                mapper.using(fieldIdConverter).map(User::getField, UserStorageModel::setFieldId);
                mapper.using(projectIdsConverter).map(User::getProjectMemberships, UserStorageModel::setProjectIds);
            });

        return modelMapper;
    }
}
