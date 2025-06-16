package org.example.popspace.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.filter.AccessTokenCheckFilter;
import org.example.popspace.filter.LoginFilter;
import org.example.popspace.filter.RefreshTokenFilter;
import org.example.popspace.handler.LoginSuccessHandler;
import org.example.popspace.mapper.redis.AuthRedisRepository;
import org.example.popspace.service.auth.UserDetailService;
import org.example.popspace.util.auth.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Slf4j
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;
    private final UserDetailService userDetailService;
    private final AuthRedisRepository authRedisRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           LoginSuccessHandler loginSuccessHandler,
                                           AccessTokenCheckFilter accessTokenCheckFilter,
                                           RefreshTokenFilter refreshTokenFilter,
                                           PasswordEncoder passwordEncoder) throws Exception {

        log.info("-----------------------configuration---------------------");

        // 사용자 인증을 위한 AuthenticationManager 구성
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
        AuthenticationManager authenticationManager = builder.build();

        // 사용자 로그인 시 사용되는 커스텀 필터
        LoginFilter loginFilter = new LoginFilter("/api/auth/login");
        loginFilter.setAuthenticationManager(authenticationManager);
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

        http
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getPublicAuthEndpoints())
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//세션을 생성하거나 유지하지 않음
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 필터 순서: 로그인 → 토큰검사 → 리프레시토큰 처리
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(accessTokenCheckFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(refreshTokenFilter, AccessTokenCheckFilter.class)
        ;
        return http.build();
    }

    private String[] getPublicAuthEndpoints() {
        return new String[] {
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/logout",
                "/api/auth/refresh",
                "/api/auth/reset-password/verify-email",
                "/api/auth/reset-password/verify-code",
                "/api/auth/nickname/check-duplication",
                "/api/auth/email/check-duplication",
                "/api/home/most-liked",
                "/api/popup/list",
                "/api/popup/detail",
                "/api/popup/detail/*",
                "/api/popup/review",
                "/api/popup/review/*",
                "/run/mock"
        };
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtUtil);
    }

    @Bean
    public AccessTokenCheckFilter tokenCheckFilter() {
        return new AccessTokenCheckFilter(getPublicAuthEndpoints(),jwtUtil, authRedisRepository);
    }

    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
        return new RefreshTokenFilter("/api/auth/refresh", jwtUtil, userDetailService, authRedisRepository);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
