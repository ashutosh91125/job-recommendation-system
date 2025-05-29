package com.jobrecommendation.recommendationservice.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobPosting {
    private String id;
    private String title;
    private String company;
    private String description;
    private List<String> requiredSkills;
    private String location;
    private String employmentType;
    private Double salary;
    private String experienceLevel;
    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
}
