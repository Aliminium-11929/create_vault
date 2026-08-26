package com.craete.vault.API.Users.Controllers;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.craete.vault.Application.Users.DTOs.UserCreateModel;
import com.craete.vault.Application.Users.DTOs.UserPatchModel;
import com.craete.vault.Application.Users.DTOs.UserStorageModel;
import com.craete.vault.Application.Users.Interfaces.IUserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public List<UserStorageModel> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("{id}")
    public UserStorageModel getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserStorageModel createUser(@Valid @RequestBody UserCreateModel userCreateModel) {
        return userService.createUser(userCreateModel);
    }

    @PatchMapping
    public UserStorageModel patchUser(@Valid @RequestBody UserPatchModel userPatchModel) {
        return userService.patchUser(userPatchModel);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
    
}
