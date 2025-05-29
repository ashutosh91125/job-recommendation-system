package com.jobrecommendation.recommendationservice.service;

import com.jobrecommendation.recommendationservice.model.JobPosting;
import com.jobrecommendation.recommendationservice.model.UserProfile;
import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {

    private static final double SKILLS_WEIGHT = 0.4;
    private static final double LOCATION_WEIGHT = 0.2;
    private static final double EXPERIENCE_WEIGHT = 0.2;
    private static final double SALARY_WEIGHT = 0.1;
    private static final double COMPANY_WEIGHT = 0.1;

    public JobRecommendation calculateJobMatch(UserProfile userProfile, JobPosting jobPosting) {
        Map<String, Double> matchFactors = new HashMap<>();
        
        // Calculate skill match
        double skillMatch = calculateSkillMatch(userProfile.getSkills(), jobPosting.getRequiredSkills());
        matchFactors.put("skillMatch", skillMatch);

        // Calculate location match
        double locationMatch = calculateLocationMatch(userProfile.getPreferredLocation(), jobPosting.getLocation());
        matchFactors.put("locationMatch", locationMatch);

        // Calculate experience match
        double experienceMatch = calculateExperienceMatch(userProfile.getExperienceLevel(), jobPosting.getExperienceLevel());
        matchFactors.put("experienceMatch", experienceMatch);

        // Calculate salary match
        double salaryMatch = calculateSalaryMatch(userProfile.getExpectedSalary(), jobPosting.getSalary());
        matchFactors.put("salaryMatch", salaryMatch);

        // Calculate company match
        double companyMatch = calculateCompanyMatch(userProfile.getPreferredCompanies(), jobPosting.getCompany());
        matchFactors.put("companyMatch", companyMatch);

        // Calculate overall match score
        double matchScore = (skillMatch * SKILLS_WEIGHT) +
                          (locationMatch * LOCATION_WEIGHT) +
                          (experienceMatch * EXPERIENCE_WEIGHT) +
                          (salaryMatch * SALARY_WEIGHT) +
                          (companyMatch * COMPANY_WEIGHT);

        JobRecommendation recommendation = new JobRecommendation();
        recommendation.setUserId(userProfile.getId());
        recommendation.setJobId(jobPosting.getId());
        recommendation.setMatchScore(matchScore);
        recommendation.setMatchFactors(matchFactors);

        return recommendation;
    }

    private double calculateSkillMatch(List<String> userSkills, List<String> requiredSkills) {
        if (userSkills == null || requiredSkills == null || requiredSkills.isEmpty()) {
            return 0.0;
        }

        Set<String> userSkillsSet = userSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<String> requiredSkillsSet = requiredSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        int matchingSkills = 0;
        for (String skill : requiredSkillsSet) {
            if (userSkillsSet.contains(skill)) {
                matchingSkills++;
            }
        }

        return (double) matchingSkills / requiredSkillsSet.size();
    }

    private double calculateLocationMatch(String preferredLocation, String jobLocation) {
        if (preferredLocation == null || jobLocation == null) {
            return 0.0;
        }
        return preferredLocation.equalsIgnoreCase(jobLocation) ? 1.0 : 0.0;
    }

    private double calculateExperienceMatch(String userExperience, String requiredExperience) {
        if (userExperience == null || requiredExperience == null) {
            return 0.0;
        }
        
        Map<String, Integer> experienceLevels = Map.of(
            "ENTRY", 1,
            "MID", 2,
            "SENIOR", 3
        );

        Integer userLevel = experienceLevels.get(userExperience.toUpperCase());
        Integer requiredLevel = experienceLevels.get(requiredExperience.toUpperCase());

        if (userLevel == null || requiredLevel == null) {
            return 0.0;
        }

        return userLevel >= requiredLevel ? 1.0 : 0.0;
    }

    private double calculateSalaryMatch(Double expectedSalary, Double offeredSalary) {
        if (expectedSalary == null || offeredSalary == null) {
            return 0.0;
        }

        if (offeredSalary >= expectedSalary) {
            return 1.0;
        }

        double ratio = offeredSalary / expectedSalary;
        return Math.max(0.0, Math.min(1.0, ratio));
    }

    private double calculateCompanyMatch(List<String> preferredCompanies, String jobCompany) {
        if (preferredCompanies == null || jobCompany == null || preferredCompanies.isEmpty()) {
            return 0.5; // Neutral score if no preferences
        }

        return preferredCompanies.stream()
                .map(String::toLowerCase)
                .anyMatch(company -> company.equals(jobCompany.toLowerCase())) ? 1.0 : 0.5;
    }
}
