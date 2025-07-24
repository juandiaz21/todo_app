package com.prueba.todoapp.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Embeddable
@Builder
public class Address {
    private String street;
    private String city;
    private String zipcode;
    private String country;
}
