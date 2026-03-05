package com.ecommerce.project.repositories;

import com.ecommerce.project.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User,Long>{

    Optional<User> findByUserName(String username);

    boolean existsByUsername(@NotBlank @Size(min=3, message = "Username must be at least 3 characters long") String username);

    boolean existsByEmail(@NotBlank @Size(min=3, message = "Username must be at least 3 characters long") String username);
}
