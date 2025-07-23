package com.prueba.todoapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prueba.todoapp.Model.Todo;
import com.prueba.todoapp.Repository.TodoRepo;
import com.prueba.todoapp.Service.TodoService;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepo todoRepo;

    @InjectMocks
    private TodoService todoService;

    @Test
    void shouldCreateTodo() {

        Todo todo = new Todo();
        todo.setTitle("Saludar");
        todo.setCompleted(false);

        Todo savedTodo = new Todo();
        savedTodo.setTitle("Saludar");
        savedTodo.setCompleted(false);

        when(todoRepo.save(todo)).thenReturn(savedTodo);

        Todo result = todoService.createTodo(todo);

        assertThat(result.getTitle()).isEqualTo("Saludar");
    }
}
