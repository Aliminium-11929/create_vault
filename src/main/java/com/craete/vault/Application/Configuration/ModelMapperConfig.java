package com.craete.vault.Application.Configuration;

import java.util.List;
import java.util.UUID;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureCreateModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;
import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;
import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Domain.ComponentPictures.Entities.ComponentPicture;
import com.craete.vault.Domain.ComponentReservations.Entities.ComponentReservation;
import com.craete.vault.Domain.Components.Entities.Component;
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

        Converter<UUID, Component> componentFromIdConverter = context -> {
            UUID componentId = context.getSource();
            if (componentId == null) {
                return null;
            }

            Component component = new Component();
            component.setId(componentId);
            return component;
        };

        Converter<Component, UUID> componentIdConverter = context -> {
            Component component = context.getSource();
            return component == null ? null : component.getId();
        };

        Converter<List<ComponentReservation>, List<UUID>> reservationIdsConverter = context -> {
            List<ComponentReservation> reservations = context.getSource();
            if (reservations == null) {
                return List.of();
            }

            return reservations.stream()
                .map(ComponentReservation::getId)
                .filter(java.util.Objects::nonNull)
                .toList();
        };

        Converter<Long, User> userFromIdConverter = context -> {
            Long userId = context.getSource();
            if (userId == null) {
                return null;
            }

            User user = new User();
            user.setId(userId);
            return user;
        };

        Converter<User, Long> userIdConverter = context -> {
            User user = context.getSource();
            return user == null ? null : user.getId();
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

        modelMapper.createTypeMap(ComponentCreateModel.class, Component.class)
            .addMappings(mapper -> {
                mapper.map(ComponentCreateModel::getTotalQuantity, Component::setTotalQuantity);
                mapper.map(ComponentCreateModel::getAvailableQuantity, Component::setAvailableQuantity);
            });

        modelMapper.createTypeMap(ComponentPatchModel.class, Component.class)
            .addMappings(mapper -> {
                mapper.skip(Component::setId);
                mapper.map(ComponentPatchModel::getTotalQuantity, Component::setTotalQuantity);
                mapper.map(ComponentPatchModel::getAvailableQuantity, Component::setAvailableQuantity);
            });

        modelMapper.createTypeMap(Component.class, ComponentStorageModel.class)
            .addMappings(mapper -> {
                mapper.map(Component::getTotalQuantity, ComponentStorageModel::setTotalQuantity);
                mapper.map(Component::getAvailableQuantity, ComponentStorageModel::setAvailableQuantity);
                mapper.using(reservationIdsConverter).map(Component::getReservations, ComponentStorageModel::setReservations);
            });

        modelMapper.createTypeMap(ComponentReservationCreateModel.class, ComponentReservation.class)
            .addMappings(mapper -> {
                mapper.using(componentFromIdConverter).map(ComponentReservationCreateModel::getComponentId, ComponentReservation::setComponent);
                mapper.using(userFromIdConverter).map(ComponentReservationCreateModel::getBorrowerId, ComponentReservation::setBorrower);
            });

        modelMapper.createTypeMap(ComponentReservationPatchModel.class, ComponentReservation.class)
            .addMappings(mapper -> {
                mapper.skip(ComponentReservation::setId);
                mapper.using(componentFromIdConverter).map(ComponentReservationPatchModel::getComponentId, ComponentReservation::setComponent);
                mapper.using(userFromIdConverter).map(ComponentReservationPatchModel::getBorrowerId, ComponentReservation::setBorrower);
            });

        modelMapper.createTypeMap(ComponentReservation.class, ComponentReservationStorageModel.class)
            .addMappings(mapper -> {
                mapper.using(componentIdConverter).map(ComponentReservation::getComponent, ComponentReservationStorageModel::setComponentId);
                mapper.using(userIdConverter).map(ComponentReservation::getBorrower, ComponentReservationStorageModel::setBorrowerId);
            });

        modelMapper.createTypeMap(ComponentPictureCreateModel.class, ComponentPicture.class)
            .addMappings(mapper -> {
                mapper.using(componentFromIdConverter).map(ComponentPictureCreateModel::getComponentId, ComponentPicture::setComponent);
            });

        modelMapper.createTypeMap(ComponentPicturePatchModel.class, ComponentPicture.class)
            .addMappings(mapper -> {
                mapper.skip(ComponentPicture::setId);
                mapper.using(componentFromIdConverter).map(ComponentPicturePatchModel::getComponentId, ComponentPicture::setComponent);
            });

        modelMapper.createTypeMap(ComponentPicture.class, ComponentPictureStorageModel.class)
            .addMappings(mapper -> {
                mapper.using(componentIdConverter).map(ComponentPicture::getComponent, ComponentPictureStorageModel::setComponentId);
            });

        return modelMapper;
    }
}
