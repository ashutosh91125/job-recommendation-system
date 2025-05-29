package com.jobrecommendation.recommendationservice.service;

import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import com.jobrecommendation.recommendationservice.model.UserProfile;
import com.jobrecommendation.recommendationservice.model.JobPosting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationEngine recommendationEngine;
    private final RestTemplate restTemplate;

    public List<JobRecommendation> getRecommendationsForUser(String userId, int limit) {
        // 1. Get user profile from User Service
        UserProfile userProfile = getUserProfile(userId);
        if (userProfile == null) {
            return Collections.emptyList();
        }

        // 2. Get active job postings from Job Posting Service
        List<JobPosting> activeJobs = getActiveJobPostings();

        // 3. Calculate recommendations
        return activeJobs.stream()
                .map(job -> recommendationEngine.calculateJobMatch(userProfile, job))
                .sorted(Comparator.comparingDouble(JobRecommendation::getMatchScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<JobRecommendation> getMatchingUsersForJob(String jobId, int limit) {
        // 1. Get job posting details
        JobPosting jobPosting = getJobPosting(jobId);
        if (jobPosting == null) {
            return Collections.emptyList();
        }

        // 2. Get user profiles from User Service
        List<UserProfile> userProfiles = getAllUserProfiles();

        // 3. Calculate matches
        return userProfiles.stream()
                .map(user -> recommendationEngine.calculateJobMatch(user, jobPosting))
                .sorted(Comparator.comparingDouble(JobRecommendation::getMatchScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void refreshRecommendations(String userId) {
        // Trigger a refresh of recommendations for a specific user
        // This could involve recalculating scores, updating cache, etc.
        log.info("Refreshing recommendations for user: {}", userId);
        getRecommendationsForUser(userId, 10); // Default to 10 recommendations
    }

    private UserProfile getUserProfile(String userId) {
        try {
            return restTemplate.getForObject(
                "http://user-service/api/users/{userId}/profile",
                UserProfile.class,
                userId
            );
        } catch (Exception e) {
            log.error("Error fetching user profile for userId: {}", userId, e);
            return null;
        }
    }

    private List<UserProfile> getAllUserProfiles() {
        try {
            return Arrays.asList(restTemplate.getForObject(
                "http://user-service/api/users/profiles",
                UserProfile[].class
            ));
        } catch (Exception e) {
            log.error("Error fetching user profiles", e);
            return Collections.emptyList();
        }
    }

    private JobPosting getJobPosting(String jobId) {
        try {
            return restTemplate.getForObject(
                "http://job-posting-service/api/jobs/{jobId}",
                JobPosting.class,
                jobId
            );
        } catch (Exception e) {
            log.error("Error fetching job posting for jobId: {}", jobId, e);
            return null;
        }
    }

    private List<JobPosting> getActiveJobPostings() {
        try {
            return Arrays.asList(restTemplate.getForObject(
                "http://job-posting-service/api/jobs",
                JobPosting[].class
            ));
        } catch (Exception e) {
            log.error("Error fetching active job postings", e);
            return Collections.emptyList();
        }
    }
}
