package com.jobrecommendation.userservice;

import com.jobrecommendation.userservice.dto.UserRegistrationDTO;
import com.jobrecommendation.userservice.model.User;
import com.jobrecommendation.userservice.model.UserProfile;
import com.jobrecommendation.userservice.repository.UserProfileRepository;
import com.jobrecommendation.userservice.repository.UserRepository;
import com.jobrecommendation.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @MockBean
    private KafkaTemplate<String, UserProfile> kafkaTemplate;

    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userProfileRepository.deleteAll();

        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("integration@test.com");
        registrationDTO.setPassword("testpass");
        registrationDTO.setFirstName("Integration");
        registrationDTO.setLastName("Test");
    }

    @Test
    void fullUserLifecycle() {
        // Register user
        User registeredUser = userService.registerUser(registrationDTO);
        assertNotNull(registeredUser);
        assertEquals(registrationDTO.getEmail(), registeredUser.getEmail());
        assertTrue(registeredUser.getRoles().contains("USER"));

        // Verify user profile was created
        Optional<UserProfile> profile = userProfileRepository.findById(registeredUser.getId());
        assertTrue(profile.isPresent());

        // Update profile
        UserProfile profileUpdate = new UserProfile();
        profileUpdate.setSkills(Collections.singletonList("Java"));
        profileUpdate.setPreferredLocation("Remote");
        profileUpdate.setExperienceLevel("Senior");

        UserProfile updatedProfile = userService.updateUserProfile(registeredUser.getId(), profileUpdate);
        assertNotNull(updatedProfile);
        assertEquals(profileUpdate.getSkills(), updatedProfile.getSkills());
        assertEquals(profileUpdate.getPreferredLocation(), updatedProfile.getPreferredLocation());
        assertEquals(profileUpdate.getExperienceLevel(), updatedProfile.getExperienceLevel());

        // Delete user
        userService.deleteUser(registeredUser.getId());
        Optional<User> deletedUser = userRepository.findById(registeredUser.getId());
        assertTrue(deletedUser.isPresent());
        assertFalse(deletedUser.get().isEnabled());
    }
}
