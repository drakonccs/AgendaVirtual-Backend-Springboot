package com.drakonccs.p3tareas.service;


import com.drakonccs.p3tareas.dto.*;
import com.drakonccs.p3tareas.entity.*;
import com.drakonccs.p3tareas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class TaskService {

    @Autowired 
    private TaskRepository taskRepo;

    @Autowired 
    private UserRepository userRepo;

    // Crear tarea
    public TaskResponse create(TaskRequest request) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    User user = userRepo.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


    Task task = new Task();
    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setDueDate(request.dueDate());
    task.setPriority(request.priority());
    task.setStatus(request.status());
    task.setAssignedTo(user);

    taskRepo.save(task);

    return mapToDTO(task);
    }

    // Obtener tareas del usuario actual
    public List<TaskResponse> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return taskRepo.findByAssignedToUsername(username)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Actualizar tarea (solo si es del usuario actual)
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepo.findById(id).orElseThrow();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!task.getAssignedTo().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para modificar esta tarea");
        }

        
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setStatus(request.status());

        taskRepo.save(task);
        return mapToDTO(task);
    }

    // Eliminar tarea (solo si es del usuario actual)
    public void delete(Long id) {
        Task task = taskRepo.findById(id).orElseThrow();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!task.getAssignedTo().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado");
        }
        taskRepo.delete(task);
    }

    private TaskResponse mapToDTO(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority().name(),
                task.getStatus().name(),
                task.getAssignedTo().getUsername()
        );
    }
}
