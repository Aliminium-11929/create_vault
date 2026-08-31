package com.craete.vault.Application.Configuration;

import java.util.List;
import java.util.UUID;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPicturePatchModel;
import com.craete.vault.Application.ComponentPictures.DTOs.ComponentPictureStorageModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;
import com.craete.vault.Application.Components.DTOs.ComponentCreateModel;
import com.craete.vault.Application.Components.DTOs.ComponentPatchModel;
import com.craete.vault.Application.Components.DTOs.ComponentStorageModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipPatchModel;
import com.craete.vault.Application.ProjectMemberships.DTOs.ProjectMembershipStorageModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPicturePatchModel;
import com.craete.vault.Application.ProjectPictures.DTOs.ProjectPictureStorageModel;
import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Domain.ComponentPictures.Entities.ComponentPicture;
import com.craete.vault.Domain.ComponentReservations.Entities.ComponentReservation;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.ProjectMemberships.Entities.ProjectMembership;
import com.craete.vault.Domain.ProjectPictures.Entities.ProjectPicture;
import com.craete.vault.Domain.Projects.Entities.Project;
import com.craete.vault.Domain.Users.Entities.User;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

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
            return field == null ? null : field.getId();
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

        modelMapper.createTypeMap(UserCreateModel.class, User.class)
                .addMappings(mapper -> {
                    mapper.using(projectMembershipsFromIdsConverter).map(UserCreateModel::getProjectIds,
                            User::setProjectMemberships);
                });

        modelMapper.createTypeMap(UserPatchModel.class, User.class)
                .addMappings(mapper -> {
                    mapper.skip(User::setId);
                    mapper.using(projectMembershipsFromIdsConverter).map(UserPatchModel::getProjectIds,
                            User::setProjectMemberships);
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
                    mapper.using(reservationIdsConverter).map(Component::getReservations,
                            ComponentStorageModel::setReservations);
                });

        modelMapper.createTypeMap(ComponentReservationCreateModel.class, ComponentReservation.class)
                .addMappings(mapper -> {
                });

        modelMapper.createTypeMap(ComponentReservationPatchModel.class, ComponentReservation.class)
                .addMappings(mapper -> {
                    mapper.skip(ComponentReservation::setId);
                });

        modelMapper.createTypeMap(ComponentReservation.class, ComponentReservationStorageModel.class)
                .addMappings(mapper -> {
                });

        modelMapper.createTypeMap(ProjectMembershipPatchModel.class, ProjectMembership.class)
                .addMappings(mapper -> {
                    mapper.skip(ProjectMembership::setId);
                });

        modelMapper.createTypeMap(ProjectMembership.class, ProjectMembershipStorageModel.class)
                .addMappings(mapper -> {
                });

        modelMapper.createTypeMap(ProjectPicturePatchModel.class, ProjectPicture.class)
                .addMappings(mapper -> {
                    mapper.skip(ProjectPicture::setId);
                });

        modelMapper.createTypeMap(ProjectPicture.class, ProjectPictureStorageModel.class)
                .addMappings(mapper -> {
                });

        modelMapper.createTypeMap(ComponentPicturePatchModel.class, ComponentPicture.class)
                .addMappings(mapper -> {
                    mapper.skip(ComponentPicture::setId);
                });

        modelMapper.createTypeMap(ComponentPicture.class, ComponentPictureStorageModel.class)
                .addMappings(mapper -> {
                });

        return modelMapper;
    }
}
