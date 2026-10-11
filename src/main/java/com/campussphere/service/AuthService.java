package com.campussphere.service;

import com.campussphere.dto.AuthRequest;
import com.campussphere.dto.AuthResponse;
import com.campussphere.dto.RegisterRequest;
import com.campussphere.dto.UserDto;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    UserDto getCurrentUser(String email);
}
