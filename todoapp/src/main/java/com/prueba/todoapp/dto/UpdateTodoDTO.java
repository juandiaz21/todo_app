package com.prueba.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateTodoDTO {
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    private String title;

    private boolean completed;

    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
}
