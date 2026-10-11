package com.campussphere.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;

@Configuration
public class JwtSigningKeyConfiguration {
    private static final int KEY_BYTES = 32;

    /** IntelliJ's default local profile uses a persistent private key outside the repository. */
    @Bean
    @Profile("local")
    SecretKey localJwtSigningKey(Environment environment,
                                 @Value("${user.home}") String userHome) throws IOException {
        String configured = environment.getProperty("JWT_SECRET");
        if (configured != null && !configured.isBlank()) return fromConfiguredSecret(configured);

        Path keyFile = Path.of(userHome, ".campussphere", "jwt-signing-key");
        Files.createDirectories(keyFile.getParent());
        if (Files.notExists(keyFile)) {
            byte[] generated = new byte[KEY_BYTES];
            new SecureRandom().nextBytes(generated);
            try {
                createPrivateFile(keyFile, Base64.getEncoder().encode(generated));
            } catch (FileAlreadyExistsException ignored) {
                // Another local application process created the shared key at the same time.
            }
        }
        byte[] encoded = Files.readAllBytes(keyFile);
        try {
            byte[] key = Base64.getDecoder().decode(encoded);
            if (key.length < KEY_BYTES) throw new IllegalStateException("Local JWT key file must contain at least 32 random bytes");
            return Keys.hmacShaKeyFor(key);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Local JWT key file is invalid; remove " + keyFile + " to generate a new local key", ex);
        }
    }

    /** Any non-local profile requires an explicitly managed secret; there is no production fallback. */
    @Bean
    @Profile("!local")
    SecretKey configuredJwtSigningKey(Environment environment) {
        String configured = environment.getProperty("JWT_SECRET");
        if (configured == null || configured.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured when the local Spring profile is not active");
        }
        return fromConfiguredSecret(configured);
    }

    private static SecretKey fromConfiguredSecret(String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < KEY_BYTES) throw new IllegalStateException("JWT_SECRET must contain at least 32 UTF-8 bytes");
        return Keys.hmacShaKeyFor(key);
    }

    private static void createPrivateFile(Path path, byte[] contents) throws IOException {
        try {
            Set<PosixFilePermission> permissions = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
            Files.createFile(path, PosixFilePermissions.asFileAttribute(permissions));
        } catch (UnsupportedOperationException ex) {
            Files.createFile(path);
        }
        Files.write(path, contents);
    }
}
