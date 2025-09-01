package com.drakonccs.p3tareas.unitarias;

import com.drakonccs.p3tareas.dto.UserDTO;
import com.drakonccs.p3tareas.entity.User;
import com.drakonccs.p3tareas.repository.UserRepository;
import com.drakonccs.p3tareas.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldEncodePassword_BeforeSaving() {
        // Preparar DTO de entrada
        UserDTO dto = new UserDTO();
        dto.setUsername("pepe");
        dto.setName("Pepe García");
        dto.setEmail("pepe@mail.com");
        dto.setPassword("1234");

        // Simular que el encoder devuelve hash
        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");

        // Usuario que se guardará en la BD
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("pepe");
        savedUser.setEmail("pepe@mail.com");
        savedUser.setPassword("hashed1234");

        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        // Ejecutar el registro
        UserDTO response = authService.register(dto);

        // Verificaciones
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("pepe", response.getUsername());
        assertEquals("pepe@mail.com", response.getEmail());

        // Verificar que realmente se guardó con contraseña hasheada
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertEquals("hashed1234", captured.getPassword());
    }
}