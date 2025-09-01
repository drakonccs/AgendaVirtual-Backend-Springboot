package com.drakonccs.p3tareas.dto;

import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String description,
    LocalDate dueDate,
    String priority,
    String status,
    String assignedTo
) {}
