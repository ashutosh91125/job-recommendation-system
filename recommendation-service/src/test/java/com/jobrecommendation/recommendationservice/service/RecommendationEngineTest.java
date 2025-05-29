package com.jobrecommendation.recommendationservice.service;

import com.jobrecommendation.recommendationservice.model.JobPosting;
import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import com.jobrecommendation.recommendationservice.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommendationEngineTest {

    @Autowired
    private RecommendationEngine recommendationEngine;

    private UserProfile userProfile;
    private JobPosting jobPosting;

    @BeforeEach
    void setUp() {
        userProfile = new UserProfile();
        userProfile.setId("1");
        userProfile.setSkills(Arrays.asList("Java", "Spring", "Microservices"));
        userProfile.setPreferredLocation("Remote");
        userProfile.setExperienceLevel("SENIOR");
        userProfile.setExpectedSalary(100000.0);
        userProfile.setPreferredCompanies(Arrays.asList("TechCorp", "InnovativeSoft"));

        jobPosting = new JobPosting();
        jobPosting.setId("1");
        jobPosting.setTitle("Senior Java Developer");
        jobPosting.setCompany("TechCorp");
        jobPosting.setRequiredSkills(Arrays.asList("Java", "Spring", "Microservices"));
        jobPosting.setLocation("Remote");
        jobPosting.setExperienceLevel("SENIOR");
        jobPosting.setSalary(120000.0);
    }

    @Test
    void calculateJobMatch_PerfectMatch() {
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);

        assertNotNull(recommendation);
        assertEquals("1", recommendation.getUserId());
        assertEquals("1", recommendation.getJobId());
        assertEquals(1.0, recommendation.getMatchScore());
        
        var factors = recommendation.getMatchFactors();
        assertEquals(1.0, factors.get("skillMatch"));
        assertEquals(1.0, factors.get("locationMatch"));
        assertEquals(1.0, factors.get("experienceMatch"));
        assertEquals(1.0, factors.get("salaryMatch"));
        assertEquals(1.0, factors.get("companyMatch"));
    }

    @Test
    void calculateJobMatch_PartialSkillMatch() {
        jobPosting.setRequiredSkills(Arrays.asList("Java", "Spring", "Microservices", "Python", "React"));
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        assertTrue(recommendation.getMatchScore() < 1.0);
        assertEquals(0.6, recommendation.getMatchFactors().get("skillMatch"));
    }

    @Test
    void calculateJobMatch_LocationMismatch() {
        jobPosting.setLocation("On-site");
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        assertEquals(0.0, recommendation.getMatchFactors().get("locationMatch"));
    }

    @Test
    void calculateJobMatch_SalaryMismatch() {
        jobPosting.setSalary(80000.0);
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        assertTrue(recommendation.getMatchFactors().get("salaryMatch") < 1.0);
    }

    @Test
    void calculateJobMatch_ExperienceMismatch() {
        jobPosting.setExperienceLevel("MID");
        userProfile.setExperienceLevel("ENTRY");
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        assertEquals(0.0, recommendation.getMatchFactors().get("experienceMatch"));
    }

    @Test
    void calculateJobMatch_CompanyPreferenceMismatch() {
        jobPosting.setCompany("UnknownCorp");
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        assertEquals(0.5, recommendation.getMatchFactors().get("companyMatch")); // Neutral score for non-preferred company
    }

    @Test
    void calculateJobMatch_NullValues() {
        userProfile.setSkills(null);
        userProfile.setPreferredLocation(null);
        userProfile.setExperienceLevel(null);
        userProfile.setExpectedSalary(null);
        userProfile.setPreferredCompanies(null);
        
        JobRecommendation recommendation = recommendationEngine.calculateJobMatch(userProfile, jobPosting);
        
        assertNotNull(recommendation);
        // All match factors should be 0.0 or 0.5 (neutral) for null values
        assertTrue(recommendation.getMatchFactors().get("skillMatch") <= 0.0);
        assertTrue(recommendation.getMatchFactors().get("locationMatch") <= 0.0);
        assertTrue(recommendation.getMatchFactors().get("experienceMatch") <= 0.0);
        assertTrue(recommendation.getMatchFactors().get("salaryMatch") <= 0.0);
        assertEquals(0.5, recommendation.getMatchFactors().get("companyMatch"));
    }
}
