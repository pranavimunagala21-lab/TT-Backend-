package com.campussphere.service.impl;

import com.campussphere.dto.UserDto;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getDepartment() != null) user.setDepartment(userDto.getDepartment());
        if (userDto.getYear() != null) user.setYear(userDto.getYear());
        if (userDto.getBio() != null) user.setBio(userDto.getBio());
        if (userDto.getAvatarUrl() != null) user.setAvatarUrl(userDto.getAvatarUrl());
        if (userDto.getSkills() != null) user.setSkills(userDto.getSkills());
        if (userDto.getInterests() != null) user.setInterests(userDto.getInterests());

        return new UserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only student accounts can be deactivated through user management");
        }
        // Retain project ownership, knowledge authorship, requests and audit relationships.
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public Set<String> getUserSkills(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getSkills();
    }

    @Override
    @Transactional
    public Set<String> addUserSkill(Long userId, String skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.getSkills().add(skill);
        userRepository.save(user);
        return user.getSkills();
    }

    @Override
    @Transactional
    public Set<String> removeUserSkill(Long userId, String skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.getSkills().remove(skill);
        userRepository.save(user);
        return user.getSkills();
    }

    @Override
    public Page<UserDto> searchUsers(String query, String department, Role role, Pageable pageable, boolean includeInactive) {
        return userRepository.searchUsers(query, department, role, includeInactive, pageable)
                .map(UserDto::new);
    }
}
