package com.prueba.todoapp.controller;

import com.prueba.todoapp.model.Todo;
import com.prueba.todoapp.service.TodoService;
import com.prueba.todoapp.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public String listTodos(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);
        title = (title != null && title.isBlank()) ? null : title;
        username = (username != null && username.isBlank()) ? null : username;

        Page<Todo> todos = todoService.getTodos(title, username, pageable);

        model.addAttribute("todos", todos);
        model.addAttribute("title", title);
        model.addAttribute("username", username);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", todos.getTotalPages());

        String loggedUsername = userDetails != null ? userDetails.getUsername() : null;
        model.addAttribute("loggedUsername", loggedUsername);

        return "list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("todo", new Todo());
        model.addAttribute("users", userRepo.findAll());
        return "form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                    @AuthenticationPrincipal UserDetails userDetails,
                    RedirectAttributes redirectAttributes) {
        Todo todo = todoService.getTodoById(id);
        if (!todo.getUser().getUsername().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Este TODO no es tuyo.");
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        model.addAttribute("users", userRepo.findAll());
        return "form";
    }

    @PostMapping("/save")
    public String saveTodo(@ModelAttribute Todo todo,
                        RedirectAttributes redirectAttributes,
                        @AuthenticationPrincipal UserDetails userDetails) {

        if (todo.getUser() == null || todo.getUser().getId() == null || !userRepo.existsById(todo.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/todos/create?error=user";
        }

        if (todo.getTitle() == null || todo.getTitle().length() > 200) {
            redirectAttributes.addFlashAttribute("errorMessage", "Título inválido.");
            return "redirect:/todos/create?error=title";
        }

        if (todo.getId() != null) {

            Todo existingTodo = todoService.getTodoById(todo.getId());
            if (!existingTodo.getUser().getUsername().equals(userDetails.getUsername())) {
                redirectAttributes.addFlashAttribute("errorMessage", "No puedes modificar un TODO que no es tuyo.");
                return "redirect:/todos";
            }
            todoService.updateTodo(todo.getId(), todo, userDetails.getUsername());
        } else {
            todoService.createTodo(todo);
        }

        return "redirect:/todos";
    }


    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id,
                @AuthenticationPrincipal UserDetails userDetails,
                RedirectAttributes redirectAttributes) {
        Todo todo = todoService.getTodoById(id);
        if (!todo.getUser().getUsername().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes eliminar un TODO que no es tuyo.");
            return "redirect:/todos";
        }
        todoService.deleteTodo(id, userDetails.getUsername());
        return "redirect:/todos";
    }
}
