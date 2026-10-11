package com.campussphere.service.impl;

import com.campussphere.dto.AuthResponse;
import com.campussphere.dto.RegisterRequest;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import com.campussphere.repository.UserRepository;
import com.campussphere.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {
    @Test
    void publicRegistrationAlwaysCreatesStudent() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.edu");
        request.setPassword("secret1");
        AtomicReference<User> stored = new AtomicReference<>();
        UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(), new Class<?>[]{UserRepository.class}, (proxy, method, args) -> {
                    if (method.getName().equals("existsByEmail")) return false;
                    if (method.getName().equals("save")) {
                        User user = (User) args[0];
                        user.setId(42L);
                        stored.set(user);
                        return user;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        PasswordEncoder passwordEncoder = new PasswordEncoder() {
            public String encode(CharSequence rawPassword) { return "encoded"; }
            public boolean matches(CharSequence rawPassword, String encodedPassword) { return false; }
        };
        AuthenticationManager authenticationManager = authentication -> authentication;
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        when(tokenProvider.generateTokenFromEmail(request.getEmail())).thenReturn("jwt-token");
        AuthServiceImpl authService = new AuthServiceImpl(userRepository, passwordEncoder, authenticationManager, tokenProvider);

        AuthResponse response = authService.register(request);

        assertEquals(Role.STUDENT, stored.get().getRole());
        assertEquals(Role.STUDENT, response.getRole());
        assertEquals(42L, response.getId());
    }
}
