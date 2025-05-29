package com.jobrecommendation.userservice.controller;

import com.jobrecommendation.userservice.dto.AuthenticationDTO;
import com.jobrecommendation.userservice.dto.UserRegistrationDTO;
import com.jobrecommendation.userservice.model.User;
import com.jobrecommendation.userservice.model.UserProfile;
import com.jobrecommendation.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        User registeredUser = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthenticationDTO authDTO) {
        String token = userService.authenticateUser(authDTO);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
    public ResponseEntity<UserProfile> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfile profileUpdate) {
        UserProfile updatedProfile = userService.updateUserProfile(userId, profileUpdate);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
