package com.jobrecommendation.jobpostingservice.service;

import com.jobrecommendation.jobpostingservice.dto.JobPostingDTO;
import com.jobrecommendation.jobpostingservice.model.JobPosting;
import com.jobrecommendation.jobpostingservice.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final KafkaTemplate<String, JobPosting> kafkaTemplate;
    private static final String JOB_POSTING_TOPIC = "job-postings";

    @Transactional
    public JobPosting createJobPosting(JobPostingDTO jobPostingDTO) {
        JobPosting jobPosting = new JobPosting();
        mapDTOToEntity(jobPostingDTO, jobPosting);
        jobPosting.setPostedDate(LocalDateTime.now());
        jobPosting.setIsActive(true);

        JobPosting savedJob = jobPostingRepository.save(jobPosting);
        // Publish event to Kafka for recommendation service
        kafkaTemplate.send(JOB_POSTING_TOPIC, savedJob);
        return savedJob;
    }

    public List<JobPosting> getAllActiveJobPostings() {
        return jobPostingRepository.findByIsActiveTrue();
    }

    public Optional<JobPosting> getJobPosting(String id) {
        return jobPostingRepository.findById(id);
    }

    public List<JobPosting> searchJobsBySkills(List<String> skills) {
        return jobPostingRepository.findByMultipleSkills(skills);
    }

    public List<JobPosting> searchJobsByLocation(String location) {
        return jobPostingRepository.findByLocationAndIsActiveTrue(location);
    }

    public List<JobPosting> getJobsByCompany(String company) {
        return jobPostingRepository.findByCompany(company);
    }

    @Transactional
    public JobPosting updateJobPosting(String id, JobPostingDTO jobPostingDTO) {
        JobPosting existingJob = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with id: " + id));

        mapDTOToEntity(jobPostingDTO, existingJob);
        JobPosting updatedJob = jobPostingRepository.save(existingJob);
        // Publish update event to Kafka
        kafkaTemplate.send(JOB_POSTING_TOPIC, updatedJob);
        return updatedJob;
    }

    @Transactional
    public void deactivateJobPosting(String id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with id: " + id));
        jobPosting.setIsActive(false);
        JobPosting deactivatedJob = jobPostingRepository.save(jobPosting);
        // Publish deactivation event to Kafka
        kafkaTemplate.send(JOB_POSTING_TOPIC, deactivatedJob);
    }

    private void mapDTOToEntity(JobPostingDTO dto, JobPosting entity) {
        entity.setTitle(dto.getTitle());
        entity.setCompany(dto.getCompany());
        entity.setDescription(dto.getDescription());
        entity.setRequiredSkills(dto.getRequiredSkills());
        entity.setLocation(dto.getLocation());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setSalary(dto.getSalary());
        entity.setExperienceLevel(dto.getExperienceLevel());
        entity.setExpiryDate(dto.getExpiryDate());
    }
}
