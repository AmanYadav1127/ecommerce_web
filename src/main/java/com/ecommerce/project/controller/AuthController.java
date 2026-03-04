package com.ecommerce.project.controller;

import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
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
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest signUpRequest) {

    }
}
