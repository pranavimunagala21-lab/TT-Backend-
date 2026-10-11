package com.campussphere.service.impl;

import com.campussphere.dto.AuthRequest;
import com.campussphere.dto.AuthResponse;
import com.campussphere.dto.RegisterRequest;
import com.campussphere.dto.UserDto;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.UserRepository;
import com.campussphere.security.JwtTokenProvider;
import com.campussphere.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Public registration does not accept role assignment.
        user.setRole(Role.STUDENT);
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setBio(request.getBio());
        if (request.getSkills() != null) {
            user.setSkills(request.getSkills());
        }
        if (request.getInterests() != null) {
            user.setInterests(request.getInterests());
        }

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateTokenFromEmail(savedUser.getEmail());

        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getRole());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = tokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    @Override
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserDto(user);
    }
}
