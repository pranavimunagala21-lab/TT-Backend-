package com.campussphere.config;

import com.campussphere.entity.enums.Role;
import com.campussphere.entity.User;
import com.campussphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Bootstraps the development administrator only when the local profile is active. */
@Component
@Profile("local")
public class AdminBootstrapRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);
    private final String adminEmail;
    private final String adminPassword;

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapRunner(UserRepository users, PasswordEncoder passwordEncoder,
                                @Value("${ADMIN_BOOTSTRAP_EMAIL:admin@campussphere.local}") String adminEmail,
                                @Value("${ADMIN_BOOTSTRAP_PASSWORD:CampusAdmin@2026!}") String adminPassword) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() && adminPassword.isBlank()) return;
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("Set both ADMIN_BOOTSTRAP_EMAIL and ADMIN_BOOTSTRAP_PASSWORD, or leave both empty");
        }
        if (adminPassword.length() < 12) {
            throw new IllegalStateException("ADMIN_BOOTSTRAP_PASSWORD must contain at least 12 characters");
        }
        var existing = users.findByEmailIgnoreCase(adminEmail);
        if (existing.isPresent()) {
            User admin = existing.get();
            boolean roleChanged = admin.getRole() != Role.ADMIN;
            if (roleChanged) {
                admin.setRole(Role.ADMIN);
                users.save(admin);
            }
            log.info("Configured local administrator is ready (role {})", admin.getRole());
            return;
        }

        User admin = new User();
        admin.setName("CampusSphere Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        users.save(admin);
        log.info("Created configured local administrator account");
    }
}
