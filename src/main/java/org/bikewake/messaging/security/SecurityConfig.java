package org.bikewake.messaging.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
//                .logout( lo -> lo.logoutSuccessUrl("https://buben.bikewake.org/realms/chat/protocol/openid-connect/logout?redirect_uri=http://localhost:8080/"))
        ;

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/unauthenticated", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().fullyAuthenticated()
                );

        return http.build();
    }
}
