package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;
import com._9.inspect_pro.model.User;
import com._9.inspect_pro.repository.ProfileRepository;
import com._9.inspect_pro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email ya está registrado");
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setDisplayName(name);
        profile.setType(ProfileType.BASIC);
        profileRepository.save(profile);

        return user;
    }

    @Override
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Métodos secundarios (puedes implementar después)
    @Override
    public User updateUser(Long id, String name) {
        // TODO: Implementar si necesitas
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        // TODO: Implementar si necesitas
        throw new UnsupportedOperationException("No implementado");
    }

    @Override
    public void deleteUser(Long id) {
        // TODO: Implementar si necesitas
        throw new UnsupportedOperationException("No implementado");
    }
}