package com.jobrecommendation.userservice.service;

import com.jobrecommendation.userservice.config.TestConfig;
import com.jobrecommendation.userservice.dto.AuthenticationDTO;
import com.jobrecommendation.userservice.dto.UserRegistrationDTO;
import com.jobrecommendation.userservice.model.User;
import com.jobrecommendation.userservice.model.UserProfile;
import com.jobrecommendation.userservice.repository.UserProfileRepository;
import com.jobrecommendation.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private KafkaTemplate<String, UserProfile> kafkaTemplate;

    private UserRegistrationDTO registrationDTO;
    private User testUser;
    private UserProfile testProfile;

    @BeforeEach
    void setUp() {
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("test@test.com");
        registrationDTO.setPassword("testpass");
        registrationDTO.setFirstName("Test");
        registrationDTO.setLastName("User");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setPassword(passwordEncoder.encode("testpass"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Collections.singleton("USER"));
        testUser.setEnabled(true);

        testProfile = new UserProfile();
        testProfile.setId(1L);
        testProfile.setUser(testUser);
        testProfile.setSkills(new ArrayList<>());
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);

        User result = userService.registerUser(registrationDTO);

        assertNotNull(result);
        assertEquals(registrationDTO.getEmail(), result.getEmail());
        assertTrue(result.isEnabled());
        assertTrue(result.getRoles().contains("USER"));
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void registerUser_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.registerUser(registrationDTO)
        );
    }

    @Test
    void authenticateUser_Success() {
        AuthenticationDTO authDTO = new AuthenticationDTO();
        authDTO.setEmail("test@test.com");
        authDTO.setPassword("testpass");

        when(userRepository.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("test.jwt.token");

        String token = userService.authenticateUser(authDTO);

        assertNotNull(token);
        assertEquals("test.jwt.token", token);
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        AuthenticationDTO authDTO = new AuthenticationDTO();
        authDTO.setEmail("test@test.com");
        authDTO.setPassword("wrongpass");

        when(userRepository.findByEmail(authDTO.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(BadCredentialsException.class, () -> 
            userService.authenticateUser(authDTO)
        );
    }

    @Test
    void updateUserProfile_Success() {
        UserProfile profileUpdate = new UserProfile();
        profileUpdate.setSkills(Collections.singletonList("Java"));
        profileUpdate.setPreferredLocation("Remote");

        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        UserProfile result = userService.updateUserProfile(1L, profileUpdate);

        assertNotNull(result);
        assertEquals(profileUpdate.getSkills(), result.getSkills());
        assertEquals(profileUpdate.getPreferredLocation(), result.getPreferredLocation());
        verify(kafkaTemplate).send(eq("user-profiles"), any(UserProfile.class));
    }

    @Test
    void updateUserProfile_NotFound() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            userService.updateUserProfile(1L, new UserProfile())
        );
    }

    @Test
    void getUserProfile_Success() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile));

        Optional<UserProfile> result = userService.getUserProfile(1L);

        assertTrue(result.isPresent());
        assertEquals(testProfile, result.get());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.deleteUser(1L);

        verify(userRepository).save(argThat(user -> !user.isEnabled()));
    }
}
