package com.craete.vault.Application.Users.Services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Application.Users.Interfaces.IUserService;
import com.craete.vault.Application.Users.Mappers.UserMapper;
import com.craete.vault.Domain.Users.Entities.User;
import com.craete.vault.Exceptions.UserNotFoundException;
import com.craete.vault.Infrastructure.Users.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional // Ensures rollback on db failure
    public UserStorageModel createUser(UserCreateModel userToCreate){
        if (userToCreate == null){
            throw new IllegalArgumentException("User must not be null.");
        }

        User savedUser = userRepository.save(userMapper.toEntity(userToCreate));
        return userMapper.toStorageModel(savedUser);
    }

    @Override
    public UserStorageModel getUserById(Long id){
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s was not found.", id)));
        return userMapper.toStorageModel(existingUser);
    }

    @Override
    public List<UserStorageModel> getUsersById(List<Long> ids){
        List<User> existingUsers = userRepository.findAllById(ids);
        if (existingUsers.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        return userMapper.toStorageModels(existingUsers);
    }

    @Override
    @Transactional
    public List<UserStorageModel> getAllUsers(){
        return userMapper.toStorageModels(userRepository.findAll());
    }

    @Override
    @Transactional // Ensures rollback on db failure
    public UserStorageModel patchUser(UserPatchModel patchedUser){
        if (patchedUser == null) {
            throw new IllegalArgumentException("Patched user and user ID must not be null.");
        }

        User existingUser = userRepository.findById(patchedUser.getId())
            .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s was not found.", patchedUser.getId())));

        userMapper.applyPatch(patchedUser, existingUser);

        return userMapper.toStorageModel(userRepository.save(existingUser));
    }

    @Override
    public void deleteUserById(Long id){
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null.");
        }

        userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s was not found.", id)));

        userRepository.deleteById(id);
    }
}
