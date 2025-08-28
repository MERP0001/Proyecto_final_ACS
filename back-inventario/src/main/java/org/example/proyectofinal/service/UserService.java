package org.example.proyectofinal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.exception.UserAlreadyExistsException;
import org.example.proyectofinal.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
    
    @Transactional
    public User createUser(String username, String password, String email, String nombreCompleto, User.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw UserAlreadyExistsException.porUsername(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException.porEmail(email);
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

    /**
     * Obtener usuario por ID.
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    public User obtenerUsuarioPorId(Long id) {
        log.debug("Buscando usuario con ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Obtener usuario por username.
     * @param username Username del usuario
     * @return Usuario encontrado
     */
    public User obtenerUsuarioPorUsername(String username) {
        log.debug("Buscando usuario con username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.porUsername(username));
    }

    /**
     * Listar usuarios activos.
     * @return Lista de usuarios activos
     */
    public List<User> listarUsuariosActivos() {
        log.debug("Listando usuarios activos");
        return userRepository.findAll().stream()
                .filter(User::getActivo)
                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                .toList();
    }

    /**
     * Buscar usuarios por rol.
     * @param role Rol a buscar
     * @return Lista de usuarios con el rol especificado
     */
    public List<User> buscarUsuariosPorRol(User.Role role) {
        log.debug("Buscando usuarios con rol: {}", role);
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role && user.getActivo())
                .toList();
    }

    /**
     * Verificar si existe usuario por username.
     * @param username Username a verificar
     * @return true si existe, false caso contrario
     */
    public boolean existeUsuarioPorUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Verificar si existe usuario por email.
     * @param email Email a verificar
     * @return true si existe, false caso contrario
     */
    public boolean existeUsuarioPorEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 