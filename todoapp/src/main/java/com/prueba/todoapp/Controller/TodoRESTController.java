package com.prueba.todoapp.controller;

import com.prueba.todoapp.dto.CreateTodoDTO;
import com.prueba.todoapp.model.Todo;
import com.prueba.todoapp.model.User;
import com.prueba.todoapp.service.TodoService;

import jakarta.validation.Valid;

import com.prueba.todoapp.repository.UserRepo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoRESTController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/getTODOs")
    public Page<Todo> getTodos(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        return todoService.getTodos(title, username, pageable);
    }

    @GetMapping("/getTODO/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Todo todo = todoService.getTodoById(id);

        if (todo == null) {
            return ResponseEntity.status(404).body("Todo not found");
        }

        if (!todo.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(todo);
    }

    @PostMapping("/createTODO")
    public ResponseEntity<?> createTodo(@RequestBody @Valid CreateTodoDTO dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
                                            
        if (!dto.getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("No autorizado");
        }

        User user = userRepo.findByUsername(dto.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());
        todo.setUser(user);

        Todo created = todoService.createTodo(todo);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/modifyTODO/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id,
            @Valid @RequestBody TodoUpdateRequest body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String title = body.getTitle();
            boolean completed = body.isCompleted();

            Todo updated = todoService.updateTodo(id, title, completed, userDetails.getUsername());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato inválido del body");
        }
    }

    @DeleteMapping("/deleteTODO/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            todoService.deleteTodo(id, userDetails.getUsername());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    public class TodoUpdateRequest {
        private String title;
        private boolean completed;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

}