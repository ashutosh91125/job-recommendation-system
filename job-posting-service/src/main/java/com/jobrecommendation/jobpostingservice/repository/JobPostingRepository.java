package com.jobrecommendation.jobpostingservice.repository;

import com.jobrecommendation.jobpostingservice.model.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface JobPostingRepository extends MongoRepository<JobPosting, String> {
    List<JobPosting> findByIsActiveTrue();
    List<JobPosting> findByCompany(String company);
    List<JobPosting> findByRequiredSkillsContaining(String skill);
    List<JobPosting> findByLocationAndIsActiveTrue(String location);
    List<JobPosting> findByExpiryDateAfterAndIsActiveTrue(LocalDateTime date);
    
    @Query("{'requiredSkills': {$in: ?0}, 'isActive': true}")
    List<JobPosting> findByMultipleSkills(List<String> skills);
}
