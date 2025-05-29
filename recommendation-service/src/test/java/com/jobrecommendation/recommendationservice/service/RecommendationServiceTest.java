package com.jobrecommendation.recommendationservice.service;

import com.jobrecommendation.recommendationservice.model.JobPosting;
import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import com.jobrecommendation.recommendationservice.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private RecommendationEngine recommendationEngine;

    private UserProfile testUserProfile;
    private JobPosting testJobPosting;
    private JobRecommendation testRecommendation;

    @BeforeEach
    void setUp() {
        testUserProfile = new UserProfile();
        testUserProfile.setId("1");
        testUserProfile.setSkills(Arrays.asList("Java", "Spring"));
        testUserProfile.setPreferredLocation("Remote");
        testUserProfile.setExperienceLevel("SENIOR");
        testUserProfile.setExpectedSalary(100000.0);

        testJobPosting = new JobPosting();
        testJobPosting.setId("1");
        testJobPosting.setTitle("Senior Java Developer");
        testJobPosting.setCompany("TechCorp");
        testJobPosting.setRequiredSkills(Arrays.asList("Java", "Spring", "Microservices"));
        testJobPosting.setLocation("Remote");
        testJobPosting.setSalary(120000.0);

        testRecommendation = new JobRecommendation();
        testRecommendation.setUserId("1");
        testRecommendation.setJobId("1");
        testRecommendation.setMatchScore(0.85);
    }    @Test
    void getRecommendationsForUser_Success() {
        when(restTemplate.getForObject(
            eq("http://user-service/api/users/{userId}/profile"),
            eq(UserProfile.class),
            eq("1")))
            .thenReturn(testUserProfile);

        when(restTemplate.getForObject(
            eq("http://job-posting-service/api/jobs"),
            eq(JobPosting[].class)))
            .thenReturn(new JobPosting[]{testJobPosting});

        when(recommendationEngine.calculateJobMatch(any(UserProfile.class), any(JobPosting.class)))
            .thenReturn(testRecommendation);

        List<JobRecommendation> recommendations = recommendationService.getRecommendationsForUser("1", 10);

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        assertEquals(1, recommendations.size());
        assertEquals(0.85, recommendations.get(0).getMatchScore());
    }

    @Test
    void getRecommendationsForUser_UserNotFound() {
        when(restTemplate.getForObject(
            contains("/users/999/profile"),
            eq(UserProfile.class)))
            .thenReturn(null);

        List<JobRecommendation> recommendations = recommendationService.getRecommendationsForUser("999", 10);

        assertTrue(recommendations.isEmpty());
    }    @Test
    void getMatchingUsersForJob_Success() {
        when(restTemplate.getForObject(
            eq("http://job-posting-service/api/jobs/{jobId}"),
            eq(JobPosting.class),
            eq("1")))
            .thenReturn(testJobPosting);

        when(restTemplate.getForObject(
            eq("http://user-service/api/users/profiles"),
            eq(UserProfile[].class)))
            .thenReturn(new UserProfile[]{testUserProfile});

        when(recommendationEngine.calculateJobMatch(any(UserProfile.class), any(JobPosting.class)))
            .thenReturn(testRecommendation);

        List<JobRecommendation> matchingUsers = recommendationService.getMatchingUsersForJob("1", 10);

        assertNotNull(matchingUsers);
        assertFalse(matchingUsers.isEmpty());
        assertEquals(1, matchingUsers.size());
        assertEquals(0.85, matchingUsers.get(0).getMatchScore());
    }

    @Test
    void getMatchingUsersForJob_JobNotFound() {
        when(restTemplate.getForObject(
            contains("/jobs/999"),
            eq(JobPosting.class)))
            .thenReturn(null);

        List<JobRecommendation> matchingUsers = recommendationService.getMatchingUsersForJob("999", 10);

        assertTrue(matchingUsers.isEmpty());
    }

    @Test
    void refreshRecommendations_Success() {
        when(restTemplate.getForObject(
            contains("/users/1/profile"),
            eq(UserProfile.class)))
            .thenReturn(testUserProfile);

        when(restTemplate.getForObject(
            contains("/jobs"),
            eq(JobPosting[].class)))
            .thenReturn(new JobPosting[]{testJobPosting});

        when(recommendationEngine.calculateJobMatch(any(UserProfile.class), any(JobPosting.class)))
            .thenReturn(testRecommendation);

        recommendationService.refreshRecommendations("1");
        // Verify that the refresh operation completes without errors
    }
}
