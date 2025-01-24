package com.icodesoft.auth.service;

import com.icodesoft.auth.entity.User;
import com.icodesoft.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository loginUserRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = this.getUserByName(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return userDetails;
    }

    @Transactional
    public UserDetails getUserByName(String username) {
        User loginUser = this.loginUserRepository.findByName(username);
        if (Objects.isNull(loginUser)) throw new UsernameNotFoundException("User is not exists.");
        List<SimpleGrantedAuthority> authorities = loginUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
        return org.springframework.security.core.userdetails.User.builder().username(loginUser.getName())
                .password(loginUser.getPassword()).authorities(authorities).build();
    }

    public String createUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        this.loginUserRepository.save(user);
        return "user added successfully";
    }
}
