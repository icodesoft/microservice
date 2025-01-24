package com.icodesoft.auth.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails, Serializable {

    @Getter
    private final User loginUser;
    List<SimpleGrantedAuthority> authorities;

    public UserPrincipal(User loginUser) {
        this.loginUser = loginUser;
        authorities = loginUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.loginUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.loginUser.getName();
    }
}
