package com.example.contify.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
    * 인증/인가 정책 정의 JWT Filter 연결 비즈니스 흐름에 직접 영향 👉 즉, “설정”이지만 “보안 로직의 일부” - 패키지는 security에 넣는 것이 좋음
    * */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtProvider jwtProvider
    )throws Exception{
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm-> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth.requestMatchers("/health","/error","/auth/**","/h2-console","/swagger-ui.html","/v3/**").permitAll().anyRequest().authenticated())
                .addFilterBefore( new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
