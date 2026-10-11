package com.campussphere.repository;

import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByName(String name);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE " +
           "(:query IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:department IS NULL OR u.department = :department) AND " +
           "(:role IS NULL OR u.role = :role) AND (:includeInactive = true OR u.active = true)")
    Page<User> searchUsers(@Param("query") String query,
                           @Param("department") String department,
                           @Param("role") Role role,
                           @Param("includeInactive") boolean includeInactive,
                           Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u JOIN u.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<User> findBySkillName(@Param("skill") String skill);
}
