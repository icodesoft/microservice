package com.icodesoft.auth.config;

import com.icodesoft.auth.filter.JwtAuthenticationFilter;
import com.icodesoft.auth.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
public class SecrityConfig {



    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(Customizer.withDefaults()) // Apply CORS
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/health", "/login").permitAll()
                            .anyRequest().authenticated();

                })
                .formLogin(formLogin -> formLogin.loginPage("/login"))
//                .formLogin(Customizer.withDefaults())
                .sessionManagement(session -> session.disable())
                .addFilterBefore(jwtAuthenticationFilter, AuthenticationFilter.class);

        // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token
        httpSecurity.oauth2ResourceServer((resourceServer) -> resourceServer
                .jwt(Customizer.withDefaults())
                .accessDeniedHandler(SecurityUtil::exceptionHandler)
                .authenticationEntryPoint(SecurityUtil::exceptionHandler)
        );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements AuthenticationProvider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //配置用于数据库认证的UserDetailsService
        provider.setUserDetailsService(userDetailsService);

        //ProviderManager implements AuthenticationManager
        //ProviderManager(AuthenticationProvider... providers)
        return new ProviderManager(provider);
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
