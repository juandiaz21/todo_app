package com.prueba.todoapp.controller;

import com.prueba.todoapp.model.Todo;
import com.prueba.todoapp.model.User;
import com.prueba.todoapp.service.TodoService;
import com.prueba.todoapp.repository.UserRepo;
import com.prueba.todoapp.config.jwt.JwtUtils;
import com.prueba.todoapp.config.jwt.CustomUserDetailsService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoRESTControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Obtener un Todo por ID válido")
    @WithMockUser(username = "juan")
    void shouldReturnTodoIfOwner() throws Exception {
        User user = User.builder().username("juan").build();
        Todo todo = Todo.builder().id(1L).title("Prueba").user(user).build();

        Mockito.when(todoService.getTodoById(1L)).thenReturn(todo);

        mockMvc.perform(get("/api/todos/getTODO/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Prueba"));
    }

    @Test
    @DisplayName("Retorna 404 si el ID no existe")
    @WithMockUser(username = "juan")
    void shouldReturn404IfTodoNotFound() throws Exception {
        Mockito.when(todoService.getTodoById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/todos/getTODO/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Retorna 403 si el usuario no es el dueño del Todo")
    @WithMockUser(username = "otroUsuario")
    void shouldReturn403IfUserIsNotOwner() throws Exception {
        User user = User.builder().username("juan").build();
        Todo todo = Todo.builder().id(2L).title("Secreto").user(user).build();

        Mockito.when(todoService.getTodoById(2L)).thenReturn(todo);

        mockMvc.perform(get("/api/todos/getTODO/2"))
                .andExpect(status().isForbidden());
    }

}