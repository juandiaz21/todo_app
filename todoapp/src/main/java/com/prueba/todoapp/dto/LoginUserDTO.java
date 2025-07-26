package com.prueba.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDTO {

    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    public String getUsername() { return username; }
    public String getPassword() { return password; }

}
