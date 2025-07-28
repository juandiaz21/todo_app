package com.prueba.todoapp.repository;

import com.prueba.todoapp.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<Todo, Long> {

    Page<Todo> findByTitleContainingAndUserUsername(String title, String username, Pageable pageable); // hacer un LIKE del titulo y un equals del username
    Page<Todo> findByTitleContaining(String title, Pageable pageable); // hacer un LIKE del titulo y que el username sea null
    Page<Todo> findByUserUsername(String username, Pageable pageable); // que el titulo sea null y hacer un equals del username

}
