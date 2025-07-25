package com.prueba.todoapp.service;

import com.prueba.todoapp.model.Todo;
import com.prueba.todoapp.repository.TodoRepo;
import com.prueba.todoapp.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    @Autowired
    private TodoRepo todoRepository;

    @Autowired
    private UserRepo userRepo;

    public Page<Todo> getTodos(String title, String username, Pageable pageable) {
        if (title != null && username != null) {
            return todoRepository.findByTitleContainingAndUserUsername(title, username, pageable);
        } else if (title != null) {
            return todoRepository.findByTitleContaining(title, pageable);
        } else if (username != null) {
            return todoRepository.findByUserUsername(username, pageable);
        } else {
            return todoRepository.findAll(pageable);
        }
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TO-DO not found with id: " + id));
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, Todo todoUpdate, String username) {
        Todo todo = getTodoById(id);
        if (!todo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado para modificar este TODO");
        }

        todo.setTitle(todoUpdate.getTitle());
        todo.setCompleted(todoUpdate.isCompleted());
        todo.setUser(userRepo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username)));

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id, String username) {
        Todo todo = getTodoById(id);
        if (!todo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("No autorizado para eliminar este TODO");
        }
        
        todoRepository.deleteById(id);
    }
}
