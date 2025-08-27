package org.example.proyectofinal.service;

import org.example.proyectofinal.entity.User;
import org.example.proyectofinal.exception.UserAlreadyExistsException;
import org.example.proyectofinal.exception.UserNotFoundException;
import org.example.proyectofinal.exception.BusinessValidationException;
import org.example.proyectofinal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UserService.
 * Utiliza Mockito para simular dependencias y validar comportamientos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Pruebas Unitarias")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User userTest;
    private User userTest2;

    @BeforeEach
    void setUp() {
        userTest = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .nombreCompleto("Test User")
                .role(User.Role.ADMINISTRADOR)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();

        userTest2 = User.builder()
                .id(2L)
                .username("operador")
                .password("encodedPassword2")
                .email("operador@example.com")
                .nombreCompleto("Operador Test")
                .role(User.Role.OPERADOR)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("Spring Security UserDetailsService")
    class SpringSecurityIntegration {

        @Test
        @DisplayName("Load user by username - Éxito")
        void testLoadUserByUsername_Exitoso() {
            // Arrange
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userTest));

            // Act
            UserDetails userDetails = userService.loadUserByUsername("testuser");

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo("testuser");
            assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
            assertThat(userDetails.isEnabled()).isTrue();

            verify(userRepository, times(1)).findByUsername("testuser");
        }

        @Test
        @DisplayName("Load user by username - Usuario no encontrado")
        void testLoadUserByUsername_NoEncontrado() {
            // Arrange
            when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.loadUserByUsername("nouser"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Usuario no encontrado: nouser");

            verify(userRepository, times(1)).findByUsername("nouser");
        }

        @Test
        @DisplayName("Load user by username - Usuario inactivo")
        void testLoadUserByUsername_UsuarioInactivo() {
            // Arrange
            userTest.setActivo(false);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userTest));

            // Act
            UserDetails userDetails = userService.loadUserByUsername("testuser");

            // Assert
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.isEnabled()).isFalse();

            verify(userRepository, times(1)).findByUsername("testuser");
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD Básicas")
    class OperacionesCRUD {

        @Test
        @DisplayName("Crear usuario - Éxito")
        void testCreateUser_Exitoso() {
            // Arrange
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
            when(userRepository.save(any(User.class))).thenReturn(userTest);

            // Act
            User resultado = userService.createUser("newuser", "password123", "newuser@example.com", "New User", User.Role.OPERADOR);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getUsername()).isEqualTo("testuser");
            assertThat(resultado.getEmail()).isEqualTo("test@example.com");

            verify(userRepository, times(1)).existsByUsername("newuser");
            verify(userRepository, times(1)).existsByEmail("newuser@example.com");
            verify(passwordEncoder, times(1)).encode("password123");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Crear usuario - Username duplicado")
        void testCreateUser_UsernameDuplicado() {
            // Arrange
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser("testuser", "password", "test@example.com", "Test User", User.Role.ADMINISTRADOR))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe un usuario con el username: testuser");

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Crear usuario - Email duplicado")
        void testCreateUser_EmailDuplicado() {
            // Arrange
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.createUser("newuser", "password", "test@example.com", "Test User", User.Role.ADMINISTRADOR))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe un usuario con el email: test@example.com");

            verify(userRepository, times(1)).existsByUsername("newuser");
            verify(userRepository, times(1)).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Obtener usuario por ID - Éxito")
        void testObtenerUsuarioPorId_Exitoso() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(userTest));

            // Act
            User resultado = userService.obtenerUsuarioPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getUsername()).isEqualTo("testuser");

            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Obtener usuario por ID - No encontrado")
        void testObtenerUsuarioPorId_NoEncontrado() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.obtenerUsuarioPorId(999L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("No se encontró el usuario con ID: 999");

            verify(userRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Obtener usuario por username - Éxito")
        void testObtenerUsuarioPorUsername_Exitoso() {
            // Arrange
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userTest));

            // Act
            User resultado = userService.obtenerUsuarioPorUsername("testuser");

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getUsername()).isEqualTo("testuser");

            verify(userRepository, times(1)).findByUsername("testuser");
        }
    }

    @Nested
    @DisplayName("Operaciones de Consulta")
    class OperacionesConsulta {

        @Test
        @DisplayName("Listar usuarios activos")
        void testListarUsuariosActivos() {
            // Arrange
            List<User> usuarios = Arrays.asList(userTest, userTest2);
            when(userRepository.findByActivoTrueOrderByUsername()).thenReturn(usuarios);

            // Act
            List<User> resultado = userService.listarUsuariosActivos();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado).contains(userTest, userTest2);

            verify(userRepository, times(1)).findByActivoTrueOrderByUsername();
        }

        @Test
        @DisplayName("Buscar usuarios por rol")
        void testBuscarUsuariosPorRol() {
            // Arrange
            List<User> administradores = Arrays.asList(userTest);
            when(userRepository.findByRoleAndActivoTrue(User.Role.ADMINISTRADOR)).thenReturn(administradores);

            // Act
            List<User> resultado = userService.buscarUsuariosPorRol(User.Role.ADMINISTRADOR);

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getRole()).isEqualTo(User.Role.ADMINISTRADOR);

            verify(userRepository, times(1)).findByRoleAndActivoTrue(User.Role.ADMINISTRADOR);
        }

        @Test
        @DisplayName("Verificar si usuario existe por username")
        void testExisteUsuarioPorUsername() {
            // Arrange
            when(userRepository.existsByUsername("testuser")).thenReturn(true);
            when(userRepository.existsByUsername("inexistente")).thenReturn(false);

            // Act & Assert
            assertThat(userService.existeUsuarioPorUsername("testuser")).isTrue();
            assertThat(userService.existeUsuarioPorUsername("inexistente")).isFalse();

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, times(1)).existsByUsername("inexistente");
        }

        @Test
        @DisplayName("Verificar si usuario existe por email")
        void testExisteUsuarioPorEmail() {
            // Arrange
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            when(userRepository.existsByEmail("inexistente@example.com")).thenReturn(false);

            // Act & Assert
            assertThat(userService.existeUsuarioPorEmail("test@example.com")).isTrue();
            assertThat(userService.existeUsuarioPorEmail("inexistente@example.com")).isFalse();

            verify(userRepository, times(1)).existsByEmail("test@example.com");
            verify(userRepository, times(1)).existsByEmail("inexistente@example.com");
        }
    }

    @Nested
    @DisplayName("Casos Edge y Errores")
    class CasosEdgeYErrores {

        @Test
        @DisplayName("Crear usuario - Username nulo")
        void testCreateUser_UsernameNulo() {
            // Act & Assert
            assertThatThrownBy(() -> userService.createUser(null, "password", "test@example.com", "Test User", User.Role.ADMINISTRADOR))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("El username no puede ser nulo o vacío");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Crear usuario - Email nulo")
        void testCreateUser_EmailNulo() {
            // Act & Assert
            assertThatThrownBy(() -> userService.createUser("testuser", "password", null, "Test User", User.Role.ADMINISTRADOR))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("El email no puede ser nulo o vacío");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Crear usuario - Password nulo")
        void testCreateUser_PasswordNulo() {
            // Act & Assert
            assertThatThrownBy(() -> userService.createUser("testuser", null, "test@example.com", "Test User", User.Role.ADMINISTRADOR))
                    .isInstanceOf(BusinessValidationException.class)
                    .hasMessageContaining("La contraseña no puede ser nula o vacía");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Load user by username - Username nulo")
        void testLoadUserByUsername_UsernameNulo() {
            // Act & Assert
            assertThatThrownBy(() -> userService.loadUserByUsername(null))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("Username no puede ser nulo");

            verify(userRepository, never()).findByUsername(any());
        }
    }
} 