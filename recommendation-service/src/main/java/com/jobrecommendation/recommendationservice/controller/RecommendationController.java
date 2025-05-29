package com.jobrecommendation.recommendationservice.controller;

import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import com.jobrecommendation.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<JobRecommendation>> getRecommendationsForUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<JobRecommendation> recommendations = recommendationService.getRecommendationsForUser(userId, limit);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/jobs/{jobId}/matching-users")
    public ResponseEntity<List<JobRecommendation>> getMatchingUsersForJob(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "10") int limit) {
        List<JobRecommendation> matchingUsers = recommendationService.getMatchingUsersForJob(jobId, limit);
        return ResponseEntity.ok(matchingUsers);
    }

    @PostMapping("/refresh/{userId}")
    public ResponseEntity<Void> refreshRecommendations(@PathVariable String userId) {
        recommendationService.refreshRecommendations(userId);
        return ResponseEntity.ok().build();
    }
}
