package com.ecommerce.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class  Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank
    @Size(min=3, message = "Product name must be at least 3 characters long")
    private String productName;
    @NotBlank
    @Size(min=5, message = "Product description must be at least 5 characters long")
    private String description;
    private String image;
    private double price;
    private double specialPrice;
    private double discount;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;
    // Getters and Setters
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;
    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.EAGER)
    private List<CartItem>products=new ArrayList<>();
}
