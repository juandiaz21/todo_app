package com.prueba.todoapp.service;

import com.prueba.todoapp.model.Address;
import com.prueba.todoapp.model.Todo;
import com.prueba.todoapp.model.User;
import com.prueba.todoapp.service.TodoService;
import com.prueba.todoapp.repository.TodoRepo;
import com.prueba.todoapp.repository.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @MockBean
    private TodoRepo todoRepo;

    @MockBean
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {

        Address address = Address.builder()
            .street("CRR")
            .city("BCN")
            .zipcode("12345")
            .country("SPN")
            .build();

        User user = User.builder()
            .name("juan")
            .username("juan")
            .password("123")
            .address(address)
            .build();

        Todo todo = Todo.builder()
                .title("Acariciar")
                .completed(true)
                .user(user)
                .build();

        Mockito.when(todoRepo.findByTitleContaining("Acar", Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(todo)));
    }

    @Test
    @DisplayName("Prueba de obtención de información de un todo enviando un titulo acortado")
    public void findByTitleContainingShouldWork() {
        String todoTitleCont = "Acar";
        String todoTitle = "Acariciar";

        Page<Todo> result = todoRepo.findByTitleContaining(todoTitleCont, Pageable.unpaged());

        assertFalse(result.isEmpty(), "No se encontró ningún Todo");

        Todo todo = result.getContent().get(0);

        assertEquals(todoTitle, todo.getTitle());
        System.out.println("todo = " + todo);
    }

    @Test
    @DisplayName("Buscar todos los Todo por nombre de usuario")
    public void findByUsernameShouldWork() {
        String username = "juan";
        Todo todo = Todo.builder().title("Tarea de prueba").completed(false).user(User.builder().username(username).build()).build();

        Mockito.when(todoRepo.findByUserUsername(Mockito.eq(username), Mockito.any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(todo)));

        Page<Todo> result = todoService.getTodos(null, username, Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(username, result.getContent().get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("Buscar todos por título y username")
    public void findByTitleAndUsernameShouldWork() {
        String title = "Tarea";
        String username = "juan";
        Todo todo = Todo.builder().title(title).completed(false).user(User.builder().username(username).build()).build();

        Mockito.when(todoRepo.findByTitleContainingAndUserUsername(Mockito.eq(title), Mockito.eq(username), Mockito.any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(todo)));

        Page<Todo> result = todoService.getTodos(title, username, Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals(title, result.getContent().get(0).getTitle());
        assertEquals(username, result.getContent().get(0).getUser().getUsername());
    }

    @Test
    @DisplayName("Buscar todos sin filtros (todos los elementos)")
    public void findAllTodosShouldWork() {
        Todo todo = Todo.builder().title("Tarea cualquiera").completed(false).build();

        Mockito.when(todoRepo.findAll(Mockito.any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(todo)));

        Page<Todo> result = todoService.getTodos(null, null, Pageable.unpaged());

        assertFalse(result.isEmpty());
        assertEquals("Tarea cualquiera", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Obtener un Todo por ID válido")
    public void getTodoByIdShouldWork() {
        Long todoId = 1L;
        Todo todo = Todo.builder().title("Existente").completed(false).build();

        Mockito.when(todoRepo.findById(todoId)).thenReturn(java.util.Optional.of(todo));

        Todo result = todoService.getTodoById(todoId);

        assertNotNull(result);
        assertEquals("Existente", result.getTitle());
    }
    
}
