package com.jobrecommendation.jobpostingservice.controller;

import com.jobrecommendation.jobpostingservice.dto.JobPostingDTO;
import com.jobrecommendation.jobpostingservice.model.JobPosting;
import com.jobrecommendation.jobpostingservice.service.JobPostingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Validated
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @PostMapping
    public ResponseEntity<JobPosting> createJobPosting(@Valid @RequestBody JobPostingDTO jobPostingDTO) {
        JobPosting createdJob = jobPostingService.createJobPosting(jobPostingDTO);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobPosting>> getAllActiveJobs() {
        List<JobPosting> jobs = jobPostingService.getAllActiveJobPostings();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPosting> getJobById(@NotEmpty(message = "ID cannot be empty") @PathVariable String id) {
        return jobPostingService.getJobPosting(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/skills")
    public ResponseEntity<List<JobPosting>> searchBySkills(
            @NotEmpty(message = "At least one skill must be provided")
            @RequestParam List<String> skills) {
        List<JobPosting> jobs = jobPostingService.searchJobsBySkills(skills);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search/location/{location}")
    public ResponseEntity<List<JobPosting>> searchByLocation(
            @NotEmpty(message = "Location cannot be empty")
            @PathVariable String location) {
        List<JobPosting> jobs = jobPostingService.searchJobsByLocation(location);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<JobPosting>> getJobsByCompany(
            @NotEmpty(message = "Company name cannot be empty")
            @PathVariable String company) {
        List<JobPosting> jobs = jobPostingService.getJobsByCompany(company);
        return ResponseEntity.ok(jobs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPosting> updateJobPosting(
            @NotEmpty(message = "ID cannot be empty") @PathVariable String id,
            @Valid @RequestBody JobPostingDTO jobPostingDTO) {
        JobPosting updatedJob = jobPostingService.updateJobPosting(id, jobPostingDTO);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateJobPosting(
            @NotEmpty(message = "ID cannot be empty")
            @PathVariable String id) {
        jobPostingService.deactivateJobPosting(id);
        return ResponseEntity.noContent().build();
    }
}
