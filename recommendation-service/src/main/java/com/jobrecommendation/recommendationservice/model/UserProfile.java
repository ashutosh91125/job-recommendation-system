package com.jobrecommendation.recommendationservice.model;

import lombok.Data;
import java.util.List;

@Data
public class UserProfile {
    private String id;
    private List<String> skills;
    private String preferredLocation;
    private String experienceLevel;
    private List<String> preferredCompanies;
    private Double expectedSalary;
    private String preferredEmploymentType;
}
