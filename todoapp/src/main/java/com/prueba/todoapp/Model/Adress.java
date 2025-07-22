package com.prueba.todoapp.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Embeddable
public class Adress {
    private String street;
    private String city;
    private String zipcode;
    private String country;
}
