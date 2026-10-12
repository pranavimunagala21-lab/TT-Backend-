package com.campussphere.config;

import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import com.campussphere.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminBootstrapRunnerTest {
    private static final String EMAIL = "admin@campussphere.local";
    private static final String PASSWORD = "CampusAdmin@2026!";

    @Test
    void createsLocalAdminWithEncodedPasswordWhenAccountIsMissing() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(users.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
        when(encoder.encode(PASSWORD)).thenReturn("bcrypt-hash");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        runner(users, encoder).run(mock(ApplicationArguments.class));

        var saved = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals(EMAIL, saved.getValue().getEmail());
        assertEquals(Role.ADMIN, saved.getValue().getRole());
        assertEquals("bcrypt-hash", saved.getValue().getPassword());
    }

    @Test
    void promotesExistingAccountWithoutChangingItsPassword() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        User existing = new User();
        existing.setEmail(EMAIL);
        existing.setPassword("existing-hash");
        existing.setRole(Role.STUDENT);
        when(users.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existing));

        runner(users, encoder).run(mock(ApplicationArguments.class));

        assertEquals(Role.ADMIN, existing.getRole());
        assertEquals("existing-hash", existing.getPassword());
        verify(users).save(existing);
        verify(encoder, never()).encode(any(CharSequence.class));
    }

    @Test
    void repeatedStartupDoesNotRewriteAnExistingAdmin() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        User existing = new User();
        existing.setEmail(EMAIL);
        existing.setPassword("existing-hash");
        existing.setRole(Role.ADMIN);
        when(users.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existing));

        runner(users, encoder).run(mock(ApplicationArguments.class));

        verify(users, never()).save(any(User.class));
        verify(encoder, never()).encode(any(CharSequence.class));
    }

    private AdminBootstrapRunner runner(UserRepository users, PasswordEncoder encoder) {
        return new AdminBootstrapRunner(users, encoder, EMAIL, PASSWORD);
    }
}
