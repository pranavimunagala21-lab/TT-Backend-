package com.campussphere.service;

import com.campussphere.dto.UserDto;
import com.campussphere.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    Set<String> getUserSkills(Long userId);
    Set<String> addUserSkill(Long userId, String skill);
    Set<String> removeUserSkill(Long userId, String skill);
    Page<UserDto> searchUsers(String query, String department, Role role, Pageable pageable, boolean includeInactive);
}
