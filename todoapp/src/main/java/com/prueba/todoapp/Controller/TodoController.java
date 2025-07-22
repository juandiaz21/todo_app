package com.prueba.todoapp.Controller;

import com.prueba.todoapp.Model.Todo;
import com.prueba.todoapp.Model.User;
import com.prueba.todoapp.Repository.UserRepo;
import com.prueba.todoapp.Service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public Page<Todo> getTodos(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        return todoService.getTodos(title, username, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody Todo todo) {
        if (todo.getUser() == null || todo.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        Optional<User> user = userRepo.findById(todo.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        todo.setUser(user.get());
        Todo created = todoService.createTodo(todo);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        if (todo.getUser() == null || todo.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }

        Optional<User> user = userRepo.findById(todo.getUser().getId());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        todo.setUser(user.get());
        Todo updated = todoService.updateTodo(id, todo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.ok().build();
    }
}