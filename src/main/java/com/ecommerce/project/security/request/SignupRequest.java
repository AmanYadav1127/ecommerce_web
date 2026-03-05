package com.ecommerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
@Data
public class SignupRequest {
    @NotBlank
    @Size(min=3, message = "Username must be at least 3 characters long")
    private String Username;
    @Email
    @NotBlank
    @Size(max=50, message = "Email must be at most 50 characters long")
    private String Email;
    @NotBlank
    @Size(min=6, message = "Password must be at least 6 characters long")
    private String Password;
    private Set<String> role;
}
