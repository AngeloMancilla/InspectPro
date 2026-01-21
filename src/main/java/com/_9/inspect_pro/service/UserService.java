package com._9.inspect_pro.service;

import com._9.inspect_pro.model.User;

import java.util.Optional;

public interface UserService {

    User registerUser(String email, String password, String name);

    User authenticateUser(String email, String password);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User updateUser(Long id, String name);

    void changePassword(Long id, String oldPassword, String newPassword);

    void deleteUser(Long id);
}