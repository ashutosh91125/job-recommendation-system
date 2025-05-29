package com.jobrecommendation.userservice.controller;

import com.jobrecommendation.userservice.dto.AuthenticationDTO;
import com.jobrecommendation.userservice.dto.UserRegistrationDTO;
import com.jobrecommendation.userservice.model.User;
import com.jobrecommendation.userservice.model.UserProfile;
import com.jobrecommendation.userservice.service.UserService;
import com.jobrecommendation.userservice.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({TestSecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRegistrationDTO registrationDTO;
    private AuthenticationDTO authDTO;
    private User testUser;
    private UserProfile testProfile;    @BeforeEach
    void setUp() {
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("test@test.com");
        registrationDTO.setPassword("testpass");
        registrationDTO.setFirstName("Test");
        registrationDTO.setLastName("User");

        authDTO = new AuthenticationDTO();
        authDTO.setEmail("test@test.com");
        authDTO.setPassword("testpass");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.setRoles(Collections.singleton("USER"));

        testProfile = new UserProfile();
        testProfile.setId(1L);
        testProfile.setUser(testUser);
        testProfile.setSkills(Collections.singletonList("Java"));
        testProfile.setPreferredLocation("Remote");
        testProfile.setExperienceLevel("Senior");
    }

    @Test
    void registerUser_Success() throws Exception {
        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void login_Success() throws Exception {
        AuthenticationDTO authDTO = new AuthenticationDTO();
        authDTO.setEmail("test@test.com");
        authDTO.setPassword("testpass");

        when(userService.authenticateUser(any(AuthenticationDTO.class))).thenReturn("test.jwt.token");

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test.jwt.token"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserProfile_Success() throws Exception {
        when(userService.getUserProfile(1L)).thenReturn(Optional.of(testProfile));

        mockMvc.perform(get("/api/users/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserProfile_NotFound() throws Exception {
        when(userService.getUserProfile(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/profile/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserProfile_Success() throws Exception {
        UserProfile profileUpdate = new UserProfile();
        profileUpdate.setPreferredLocation("Remote");

        when(userService.updateUserProfile(eq(1L), any(UserProfile.class))).thenReturn(testProfile);

        mockMvc.perform(put("/api/users/profile/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyLong());
    }
}
