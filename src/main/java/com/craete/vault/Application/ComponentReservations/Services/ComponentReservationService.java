package com.craete.vault.Application.ComponentReservations.Services;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationCreateModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationPatchModel;
import com.craete.vault.Application.ComponentReservations.DTOs.ComponentReservationStorageModel;
import com.craete.vault.Application.ComponentReservations.Interfaces.IComponentReservationService;
import com.craete.vault.Domain.ComponentReservations.Entities.ComponentReservation;
import com.craete.vault.Domain.Components.Entities.Component;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Exceptions.ComponentNotFoundException;
import com.craete.vault.Exceptions.ComponentReservationNotFoundException;
import com.craete.vault.Exceptions.UserNotFoundException;
import com.craete.vault.Infrastructure.ComponentReservations.Repository.ComponentReservationRepository;
import com.craete.vault.Infrastructure.Components.Repository.ComponentRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ComponentReservationService implements IComponentReservationService {

    private final ComponentReservationRepository componentReservationRepository;
    private final ComponentRepository componentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ComponentReservationService(
        ComponentReservationRepository componentReservationRepository,
        ComponentRepository componentRepository,
        UserRepository userRepository,
        ModelMapper modelMapper
    ) {
        this.componentReservationRepository = componentReservationRepository;
        this.componentRepository = componentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ComponentReservationStorageModel createComponentReservation(ComponentReservationCreateModel componentReservationCreateModel) {
        if (componentReservationCreateModel == null) {
            throw new IllegalArgumentException("Component reservation must not be null.");
        }

        Component component = componentRepository.findById(componentReservationCreateModel.getComponentId())
            .orElseThrow(() -> new ComponentNotFoundException(
                String.format("Component with ID %s was not found.", componentReservationCreateModel.getComponentId())));

        if (component.getAvailableQuantity() < componentReservationCreateModel.getQuantity()) {
            throw new IllegalArgumentException(
                String.format("Not enough available quantity for component %s. Requested: %s, available: %s.",
                    component.getId(), componentReservationCreateModel.getQuantity(), component.getAvailableQuantity()));
        }

        User borrower = userRepository.findById(componentReservationCreateModel.getBorrowerId())
            .orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s was not found.", componentReservationCreateModel.getBorrowerId())));

        ComponentReservation reservation = modelMapper.map(componentReservationCreateModel, ComponentReservation.class);
        reservation.setId(UUID.randomUUID());
        reservation.setComponent(component);
        reservation.setBorrower(borrower);

        component.setAvailableQuantity(component.getAvailableQuantity() - componentReservationCreateModel.getQuantity());

        ComponentReservation savedReservation = componentReservationRepository.save(reservation);
        componentRepository.save(component);
        return modelMapper.map(savedReservation, ComponentReservationStorageModel.class);
    }

    @Override
    public ComponentReservationStorageModel getComponentReservationById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component reservation ID must not be null.");
        }

        ComponentReservation reservation = componentReservationRepository.findById(id)
            .orElseThrow(() -> new ComponentReservationNotFoundException(
                String.format("Component reservation with ID %s was not found.", id)));

        return modelMapper.map(reservation, ComponentReservationStorageModel.class);
    }

    @Override
    public List<ComponentReservationStorageModel> getComponentReservationsByComponentId(UUID componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        List<ComponentReservation> reservations = componentReservationRepository.findAll().stream()
            .filter(reservation -> reservation.getComponent() != null && reservation.getComponent().getId().equals(componentId))
            .toList();

        if (reservations.isEmpty()) {
            throw new ComponentReservationNotFoundException("Component reservations not found.");
        }

        return reservations.stream()
            .map(reservation -> modelMapper.map(reservation, ComponentReservationStorageModel.class))
            .toList();
    }

    @Override
    public List<ComponentReservationStorageModel> getComponentReservationsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        List<ComponentReservation> reservations = componentReservationRepository.findAll().stream()
            .filter(reservation -> reservation.getBorrower() != null && reservation.getBorrower().getId().equals(userId))
            .toList();

        if (reservations.isEmpty()) {
            throw new ComponentReservationNotFoundException("User reservations not found.");
        }

        return reservations.stream()
            .map(reservation -> modelMapper.map(reservation, ComponentReservationStorageModel.class))
            .toList();
    }

    @Override
    public List<ComponentReservationStorageModel> getComponentReservationsById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Component reservation IDs must not be null or empty.");
        }

        List<ComponentReservation> reservations = componentReservationRepository.findAllById(ids);
        if (reservations.isEmpty()) {
            throw new ComponentReservationNotFoundException("Component reservations not found.");
        }

        return reservations.stream()
            .map(reservation -> modelMapper.map(reservation, ComponentReservationStorageModel.class))
            .toList();
    }

    @Override
    public List<ComponentReservationStorageModel> getAllComponentReservations() {
        return componentReservationRepository.findAll().stream()
            .map(reservation -> modelMapper.map(reservation, ComponentReservationStorageModel.class))
            .toList();
    }

    @Override
    @Transactional
    public ComponentReservationStorageModel patchComponentReservation(ComponentReservationPatchModel componentReservationPatchModel) {
        if (componentReservationPatchModel == null) {
            throw new IllegalArgumentException("Patched component reservation and reservation ID must not be null.");
        }

        ComponentReservation existingReservation = componentReservationRepository.findById(componentReservationPatchModel.getId())
            .orElseThrow(() -> new ComponentReservationNotFoundException(
                String.format("Component reservation with ID %s was not found.", componentReservationPatchModel.getId())));

        Component currentComponent = existingReservation.getComponent();
        Component targetComponent = componentRepository.findById(componentReservationPatchModel.getComponentId())
            .orElseThrow(() -> new ComponentNotFoundException(
                String.format("Component with ID %s was not found.", componentReservationPatchModel.getComponentId())));

        User borrower = userRepository.findById(componentReservationPatchModel.getBorrowerId())
            .orElseThrow(() -> new UserNotFoundException(
                String.format("User with ID %s was not found.", componentReservationPatchModel.getBorrowerId())));

        int previousQuantity = existingReservation.getQuantity();
        int newQuantity = componentReservationPatchModel.getQuantity();

        if (currentComponent != null && currentComponent.getId().equals(targetComponent.getId())) {
            int availableAfterReleasingCurrent = currentComponent.getAvailableQuantity() + previousQuantity;
            if (availableAfterReleasingCurrent < newQuantity) {
                throw new IllegalArgumentException(
                    String.format("Not enough available quantity for component %s. Requested: %s, available: %s.",
                        targetComponent.getId(), newQuantity, availableAfterReleasingCurrent));
            }
            targetComponent.setAvailableQuantity(availableAfterReleasingCurrent - newQuantity);
        } else {
            if (currentComponent != null) {
                currentComponent.setAvailableQuantity(currentComponent.getAvailableQuantity() + previousQuantity);
                componentRepository.save(currentComponent);
            }

            if (targetComponent.getAvailableQuantity() < newQuantity) {
                throw new IllegalArgumentException(
                    String.format("Not enough available quantity for component %s. Requested: %s, available: %s.",
                        targetComponent.getId(), newQuantity, targetComponent.getAvailableQuantity()));
            }

            targetComponent.setAvailableQuantity(targetComponent.getAvailableQuantity() - newQuantity);
        }

        existingReservation.setComponent(targetComponent);
        existingReservation.setBorrower(borrower);
        existingReservation.setReservedFrom(componentReservationPatchModel.getReservedFrom());
        existingReservation.setReservedTo(componentReservationPatchModel.getReservedTo());
        existingReservation.setQuantity(newQuantity);

        componentRepository.save(targetComponent);
        ComponentReservation updatedReservation = componentReservationRepository.save(existingReservation);
        return modelMapper.map(updatedReservation, ComponentReservationStorageModel.class);
    }

    @Override
    public void deleteComponentReservationById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Component reservation ID must not be null.");
        }

        ComponentReservation reservation = componentReservationRepository.findById(id)
            .orElseThrow(() -> new ComponentReservationNotFoundException(
                String.format("Component reservation with ID %s was not found.", id)));

        if (reservation.getComponent() != null) {
            Component component = reservation.getComponent();
            component.setAvailableQuantity(component.getAvailableQuantity() + reservation.getQuantity());
            componentRepository.save(component);
        }

        componentReservationRepository.deleteById(id);
    }

    @Override
    public void deleteComponentReservationByComponentId(UUID componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID must not be null.");
        }

        List<ComponentReservation> reservations = componentReservationRepository.findAll().stream()
            .filter(reservation -> reservation.getComponent() != null && reservation.getComponent().getId().equals(componentId))
            .toList();

        if (reservations.isEmpty()) {
            throw new ComponentReservationNotFoundException("Component reservations not found.");
        }

        for (ComponentReservation reservation : reservations) {
            Component component = reservation.getComponent();
            if (component != null) {
                component.setAvailableQuantity(component.getAvailableQuantity() + reservation.getQuantity());
                componentRepository.save(component);
            }
        }

        componentReservationRepository.deleteAll(reservations);
    }
}
