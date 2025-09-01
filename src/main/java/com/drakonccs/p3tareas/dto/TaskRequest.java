package com.drakonccs.p3tareas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import com.drakonccs.p3tareas.entity.Priority;
import com.drakonccs.p3tareas.entity.Status;

public record TaskRequest(
    @NotBlank String title,
    String description,
    @FutureOrPresent LocalDate dueDate,
    @NotNull Priority priority,
    @NotNull Status status
) {
    
}
