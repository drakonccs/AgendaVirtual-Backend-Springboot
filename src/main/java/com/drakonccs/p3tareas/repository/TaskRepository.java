package com.drakonccs.p3tareas.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.drakonccs.p3tareas.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToUsername(String username);
}
