package com.craete.vault.Application.Users.Interfaces;

import java.util.List;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;

public interface IUserService {

    UserStorageModel createUser(UserCreateModel userCreateModel);
    UserStorageModel getUserById(Long id);
    List<UserStorageModel> getUsersById(List<Long> id);
    List<UserStorageModel> getAllUsers();
    UserStorageModel patchUser(UserPatchModel userPatchModel);
    void deleteUserById(Long id);

}
