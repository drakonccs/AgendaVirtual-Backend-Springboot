package com.drakonccs.p3tareas.unitarias;

import com.drakonccs.p3tareas.dto.TaskRequest;
import com.drakonccs.p3tareas.entity.*;
import com.drakonccs.p3tareas.repository.TaskRepository;
import com.drakonccs.p3tareas.repository.UserRepository;
import com.drakonccs.p3tareas.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private UserRepository userRepo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateTask_ShouldThrowException_IfNotOwner() {
        // Usuario dueño
        User owner = new User();
        owner.setUsername("ownerUser");

        // Usuario intruso 
        User intruder = new User();
        intruder.setUsername("intruderUser");

        // Tarea asignada al dueño
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Tarea de prueba");
        task.setAssignedTo(owner);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        // Simular que el intruso está autenticado
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(intruder.getUsername(), null, null);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Crear request de actualización
        TaskRequest request = new TaskRequest(
                "Nuevo título",
                "Nueva desc",
                LocalDate.of(2025, 9, 1),
                Priority.MEDIUM,
                Status.DOING
        );

        // Ejecutar y verificar excepción
        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.update(1L, request);
        });

        assertEquals("No tienes permiso para modificar esta tarea", exception.getMessage());
    }
}
