package com.drakonccs.p3tareas.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.drakonccs.p3tareas.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
