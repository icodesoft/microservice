package com.icodesoft.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.icodesoft.auth.model.LoginUserVO;
import com.icodesoft.auth.model.ResponseModel;
import com.icodesoft.auth.model.Token;
import com.icodesoft.auth.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ResponseModel checkLogin(LoginUserVO loginUserVO) {
        String name = loginUserVO.getName();
        String password = loginUserVO.getPassword();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(name, password);
        Authentication authenticate = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (authenticate.isAuthenticated()) {
            // save user info to security context
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            // generate token
            String userID = "user: " +((UserDetails)authenticate.getPrincipal()).getUsername();
            String token = JwtUtil.generateToken(userID, 5);

            // save user info to redis
            this.redisTemplate.opsForValue().set(userID, authenticate.getPrincipal(), 1000 * 60 * 5, TimeUnit.MILLISECONDS);

            return ResponseModel.loginSuccess(new Token("Bearer", 60 * 5, token, UUID.randomUUID().toString()));
        }
        return ResponseModel.loginFailure("login failure");
    }
}
