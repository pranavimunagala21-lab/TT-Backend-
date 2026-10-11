package com.campussphere.controller;

import com.campussphere.dto.*;
import com.campussphere.entity.enums.Role;
import com.campussphere.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestControllerRouteTest {
    private final AtomicReference<String> invoked = new AtomicReference<>();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthService auth = proxy(AuthService.class, (method, args) -> {
            invoked.set("auth." + method);
            if (method.equals("register")) return new AuthResponse("token", 1L, "a@b.edu", "A", Role.STUDENT);
            return new UserDto();
        });
        UserService users = proxy(UserService.class, (method, args) -> {
            invoked.set("user." + method);
            return new UserDto();
        });
        ClubService clubs = proxy(ClubService.class, (method, args) -> {
            invoked.set("club." + method);
            return List.of();
        });
        ProjectService projects = proxy(ProjectService.class, (method, args) -> {
            invoked.set("project." + method);
            return List.of();
        });
        SkillService skills = proxy(SkillService.class, (method, args) -> {
            invoked.set("skill." + method);
            return List.of();
        });
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(auth), new UserController(users, auth),
                new ClubController(clubs), new ProjectController(projects), new SkillController(skills)).build();
    }

    @Test void registrationEndpointDelegatesAndReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"email\":\"a@b.edu\",\"password\":\"secret1\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated());
        assertEquals("auth.register", invoked.get());
    }

    @Test void userProfileEndpointDelegates() throws Exception {
        mockMvc.perform(get("/api/users/1")).andExpect(status().isOk());
        assertEquals("user.getUserById", invoked.get());
    }

    @Test void clubCollectionEndpointDelegates() throws Exception {
        mockMvc.perform(get("/api/clubs")).andExpect(status().isOk());
        assertEquals("club.getAllClubs", invoked.get());
    }

    @Test void projectCollectionEndpointDelegates() throws Exception {
        mockMvc.perform(get("/api/projects")).andExpect(status().isOk());
        assertEquals("project.getAllProjects", invoked.get());
    }

    @Test void skillCollectionEndpointDelegates() throws Exception {
        mockMvc.perform(get("/api/skills")).andExpect(status().isOk());
        assertEquals("skill.getAllSkills", invoked.get());
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, ServiceCall call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) return method.getName().equals("toString") ? type.getSimpleName() : null;
            return call.invoke(method.getName(), args);
        });
    }

    @FunctionalInterface private interface ServiceCall {
        Object invoke(String method, Object[] args);
    }
}
