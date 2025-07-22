package com.prueba.todoapp.Repository;

import com.prueba.todoapp.Model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<Todo, Long> {

    Page<Todo> findByTitleAndUserUsername(String title, String username, Pageable pageable);
    Page<Todo> findByTitle(String title, Pageable pageable);
    Page<Todo> findByUserUsername(String username, Pageable pageable);

}
