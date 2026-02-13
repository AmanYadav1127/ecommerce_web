package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String image;
    private double price;
    private double specialPrice;
    private double discount;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

        // Getters and Setters
}
