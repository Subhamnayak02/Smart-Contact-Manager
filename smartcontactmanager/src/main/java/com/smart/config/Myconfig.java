package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Myconfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the PasswordEncoder bean

    @Bean
    public SecurityFilterChain filterchain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/user/**").authenticated()  // Authenticate requests to URLs starting with /user
                .anyRequest().permitAll()                     // Allow all other requests
            )
            .formLogin(form -> form
                .loginPage("/login")                          // Custom login page URL
                .permitAll()                                  // Allow everyone to access the login page
                .defaultSuccessUrl("/user/index")    // Redirect to dashboard after successful login
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                         // Custom logout URL
                .logoutSuccessUrl("/")                        // Redirect to home page after logout
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")           // Custom access denied page
            );

        return httpSecurity.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
