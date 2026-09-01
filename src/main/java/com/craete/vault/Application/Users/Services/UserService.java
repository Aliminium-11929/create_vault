package com.craete.vault.Application.Users.Services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Application.Users.Interfaces.IUserService;
import com.craete.vault.Domain.Fields.Entities.Field;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Exceptions.UserNotFoundException;
import com.craete.vault.Infrastructure.Fields.Repository.FieldRepository;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, FieldRepository fieldRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional // Ensures rollback on db failure
    public UserStorageModel createUser(UserCreateModel userToCreate) {
        if (userToCreate == null) {
            throw new IllegalArgumentException("User must not be null.");
        }

        if (userToCreate.getFieldId() == null) {
            throw new IllegalArgumentException("Field ID must not be null.");
        }

        Field field = fieldRepository.findById(userToCreate.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Field not found: " + userToCreate.getFieldId()));

        User user = modelMapper.map(userToCreate, User.class);
        user.setField(field);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserStorageModel.class);
    }

    @Override
    public UserStorageModel getUserById(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s was not found.", id)));
        return modelMapper.map(existingUser, UserStorageModel.class);
    }

    @Override
    public List<UserStorageModel> getUsersById(List<Long> ids) {
        List<User> existingUsers = userRepository.findAllById(ids);
        if (existingUsers.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        return existingUsers.stream()
                .map(user -> modelMapper.map(user, UserStorageModel.class))
                .toList();
    }

    @Override
    @Transactional
    public List<UserStorageModel> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserStorageModel.class))
                .toList();
    }

    @Override
    @Transactional // Ensures rollback on db failure
    public UserStorageModel patchUser(UserPatchModel patchedUser) {
        if (patchedUser == null) {
            throw new IllegalArgumentException("Patched user and user ID must not be null.");
        }

        User existingUser = userRepository.findById(patchedUser.getId())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with ID %s was not found.", patchedUser.getId())));

        if (patchedUser.getName() != null && !patchedUser.getName().isBlank()) {
            existingUser.setName(patchedUser.getName());
        }

        if (patchedUser.getEmail() != null && !patchedUser.getEmail().isBlank()) {
            existingUser.setEmail(patchedUser.getEmail());
        }

        if (patchedUser.getRole() != null) {
            existingUser.setRole(patchedUser.getRole());
        }

        if (patchedUser.getFieldId() != null) {
            Field field = fieldRepository.findById(patchedUser.getFieldId())
                    .orElseThrow(() -> new UserNotFoundException(
                            String.format("Field with ID %s was not found.", patchedUser.getFieldId())));
            existingUser.setField(field);
        }

        return modelMapper.map(userRepository.save(existingUser), UserStorageModel.class);
    }

    @Override
    public void deleteUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s was not found.", id)));

        userRepository.deleteById(id);
    }
}
