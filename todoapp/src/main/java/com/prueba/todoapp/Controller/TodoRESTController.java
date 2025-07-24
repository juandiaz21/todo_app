package com.prueba.todoapp.Controller;

import com.prueba.todoapp.Model.Todo;
import com.prueba.todoapp.Model.User;
import com.prueba.todoapp.Repository.UserRepo;
import com.prueba.todoapp.Service.TodoService;
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
    public ResponseEntity<?> createTodo(@RequestBody Todo todo,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        if (todo.getTitle() == null || todo.getTitle().isBlank() || todo.getTitle().length() > 200) {
            return ResponseEntity.badRequest().body("Title must be non-empty and under 200 characters");
        }

        todo.setUser(user);
        Todo created = todoService.createTodo(todo);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/modifyTODO/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id,
            @RequestBody Todo todo,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Todo updated = todoService.updateTodo(id, todo, userDetails.getUsername());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
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
}