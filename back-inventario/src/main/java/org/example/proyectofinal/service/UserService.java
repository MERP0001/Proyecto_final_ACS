package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
    }
    
    @Transactional
    public User createUser(String username, String password, String email, String nombreCompleto, User.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nombreCompleto(nombreCompleto)
                .role(role)
                .activo(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("Usuario creado exitosamente: {}", username);
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setNombreCompleto(userDetails.getNombreCompleto());
        user.setRole(userDetails.getRole());
        user.setActivo(userDetails.getActivo());

        // No actualizamos la contraseña aquí, debería ser un método separado y más seguro
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
        user.setActivo(false);
        userRepository.save(user);
    }
    
    @Transactional
    public void updateLastAccess(Long userId) {
        userRepository.updateUltimoAcceso(userId, LocalDateTime.now());
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 