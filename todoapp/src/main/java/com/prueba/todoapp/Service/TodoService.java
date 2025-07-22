package com.prueba.todoapp.Service;

import com.prueba.todoapp.Model.Todo;
import com.prueba.todoapp.Repository.TodoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    @Autowired
    private TodoRepo todoRepository;

    public Page<Todo> getTodos(String title, String username, Pageable pageable) {
        if (title != null && username != null) {
            return todoRepository.findByTitleAndUserUsername(title, username, pageable);
        } else if (title != null) {
            return todoRepository.findByTitle(title, pageable);
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

    public Todo updateTodo(Long id, Todo todoUpdateDetails) {
        Todo todo = getTodoById(id);

        todo.setTitle(todoUpdateDetails.getTitle());
        todo.setCompleted(todoUpdateDetails.isCompleted());
        todo.setUser(todoUpdateDetails.getUser());

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }
}
