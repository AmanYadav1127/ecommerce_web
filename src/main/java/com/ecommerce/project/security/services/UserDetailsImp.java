package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImp implements UserDetails {
    private static final Long serialVersionUID = 1L;
    @Getter
    private Long id;
    private String email;
    private String username;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImp(Long id, String email, String username, String password,
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
    public static UserDetailsImp build(User user) {
       List<GrantedAuthority>authorities=user.getRoles().stream().
               map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
         return new UserDetailsImp(user.getUserId(),
                 user.getEmail(),
                 user.getUserName(),
                 user.getPassword(),
                 authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImp user = (UserDetailsImp) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public boolean isEnabled() {
        return true;

    }

}
