package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword())); //AuthenticationManager tumhare UserDetailsService aur password encoder
            // ka use karke username/password verify karta hai.Agar OK → authentication object milta hai
            //Agar wrong → catch me jaake “Invalid credentials".

        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("error", "Invalid credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImp userDetails=(UserDetailsImp) authentication.getPrincipal();// getPrincipal() method se currently authenticated user ke details milte hai,
        // jisme username, password, authorities (roles) etc. hote hai.
        String jwtToken=jwtUtils.generateTokenFromUsername(userDetails);//generate JWT token using username from userDetails
        List<String> roles=userDetails.getAuthorities().stream().map(item->item.getAuthority()).collect(Collectors.toList());
        UserInfoResponse response=new UserInfoResponse(userDetails.getId(),jwtToken,userDetails.getUsername(), roles);
        return ResponseEntity.ok(response); //return the response with JWT token and user details
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.
                    badRequest().
                    body(new MessageResponse("Error: Username is already taken!"));
        }

        if(userRepository.existsByEmail(signupRequest.getUsername())){
            return ResponseEntity.
                    badRequest().
                    body(new MessageResponse("Error: Email is already taken!"));
        }
        User user=new User(signupRequest.getUsername(),signupRequest.getEmail(),encoder.encode(signupRequest.getPassword()));
        Set<String> strRoles=signupRequest.getRole();
        Set<Role> roles=new HashSet<>();
        if (strRoles==null){
            Role useeRole=roleRepository.findByRoleName(AppRole.ROLE_USER).
                    orElseThrow(()->new RuntimeException("Error: Role is not found."));
            roles.add(useeRole);
        }
        else {
            // Agar user ne role specify kiya hai, toh unko database se fetch karke set me add karenge
            // For example, agar user ne "admin" role specify kiya hai, toh hum database se "ROLE_ADMIN" role fetch karenge
            // seller ---> ROLE_SELLER
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).
                                orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);

                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
