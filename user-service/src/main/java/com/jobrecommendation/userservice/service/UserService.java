package com.jobrecommendation.userservice.service;

import com.jobrecommendation.userservice.dto.UserRegistrationDTO;
import com.jobrecommendation.userservice.dto.AuthenticationDTO;
import com.jobrecommendation.userservice.model.User;
import com.jobrecommendation.userservice.model.UserProfile;
import com.jobrecommendation.userservice.repository.UserRepository;
import com.jobrecommendation.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, UserProfile> kafkaTemplate;
    private static final String USER_PROFILE_TOPIC = "user-profiles";

    @Transactional
    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setFirstName(registrationDTO.getFirstName());
        user.setLastName(registrationDTO.getLastName());
        user.setRoles(Collections.singleton("USER"));
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Create empty profile for new user
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        userProfileRepository.save(profile);

        return savedUser;
    }

    public String authenticateUser(AuthenticationDTO authDTO) {
        User user = userRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }

    @Transactional
    public UserProfile updateUserProfile(Long userId, UserProfile profileUpdate) {
        UserProfile existingProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        // Update profile fields
        existingProfile.setSkills(profileUpdate.getSkills());
        existingProfile.setPreferredLocation(profileUpdate.getPreferredLocation());
        existingProfile.setExperienceLevel(profileUpdate.getExperienceLevel());
        existingProfile.setPreferredCompanies(profileUpdate.getPreferredCompanies());
        existingProfile.setExpectedSalary(profileUpdate.getExpectedSalary());
        existingProfile.setPreferredEmploymentType(profileUpdate.getPreferredEmploymentType());
        existingProfile.setResume(profileUpdate.getResume());
        existingProfile.setLinkedInProfile(profileUpdate.getLinkedInProfile());
        existingProfile.setGithubProfile(profileUpdate.getGithubProfile());
        existingProfile.setSummary(profileUpdate.getSummary());

        UserProfile updatedProfile = userProfileRepository.save(existingProfile);
        
        // Publish profile update event to Kafka
        kafkaTemplate.send(USER_PROFILE_TOPIC, updatedProfile);
        
        return updatedProfile;
    }

    public Optional<UserProfile> getUserProfile(Long userId) {
        return userProfileRepository.findById(userId);
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });
    }
}
