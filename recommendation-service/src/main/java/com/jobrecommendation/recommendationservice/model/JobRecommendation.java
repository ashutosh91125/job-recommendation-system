package com.jobrecommendation.recommendationservice.model;

import lombok.Data;
import java.util.Map;

@Data
public class JobRecommendation {
    private String userId;
    private String jobId;
    private double matchScore;
    private Map<String, Double> matchFactors;
}
