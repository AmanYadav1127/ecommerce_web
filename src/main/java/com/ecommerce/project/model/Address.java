package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    @NotBlank
    @Size(min = 3, message = "Street must be at least 3 characters long")
    private String street;
    @NotBlank
    @Size(min = 2, message = "City must be at least 2 characters long")
    private String city;
    @NotBlank
    @Size(min = 2, message = "Building name must be at least 2 characters long")
    private String buildingName;
    @NotBlank
    @Size(min = 2, message = "State must be at least 2 characters long")
    private String state;
    @NotBlank
    @Size(min = 6, max = 10, message = "Pin code must be between 4 and 10 characters long")
    private String pinCode;
    @NotBlank
    @Size(min = 2, message = "Country must be at least 2 characters long")
    private String country;
    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();

    public Address(String street, String city, String buildingName, String state, String pinCode, String country) {
        this.street = street;
        this.city = city;
        this.buildingName = buildingName;
        this.state = state;
        this.pinCode = pinCode;
        this.country = country;
    }
}
