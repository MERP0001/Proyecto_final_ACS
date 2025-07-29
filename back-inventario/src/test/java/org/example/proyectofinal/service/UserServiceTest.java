package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .role(User.Role.ADMINISTRADOR)
                .activo(true)
                .build();
    }

    @Test
    void loadUserByUsername_Exitoso() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        System.out.println("Resultado de loadUserByUsername_Exitoso: " + userDetails);

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_NoEncontrado() {
        // Arrange
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nouser");
        });
        System.out.println("Resultado de loadUserByUsername_NoEncontrado: " + exception.getMessage());

        verify(userRepository, times(1)).findByUsername("nouser");
    }

    @Test
    void createUser_Exitoso() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User newUser = userService.createUser("testuser", "password", "test@example.com", "Test User", User.Role.ADMINISTRADOR);
        System.out.println("Resultado de createUser_Exitoso: " + newUser);
        
        // Assert
        assertNotNull(newUser);
        assertEquals("testuser", newUser.getUsername());
        assertEquals("encodedPassword", newUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }
} 