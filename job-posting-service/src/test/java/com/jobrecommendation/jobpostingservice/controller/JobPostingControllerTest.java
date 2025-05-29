package com.jobrecommendation.jobpostingservice.controller;

import com.jobrecommendation.jobpostingservice.dto.JobPostingDTO;
import com.jobrecommendation.jobpostingservice.model.JobPosting;
import com.jobrecommendation.jobpostingservice.service.JobPostingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JobPostingControllerTest {

    @Mock
    private JobPostingService jobPostingService;

    @InjectMocks
    private JobPostingController jobPostingController;

    private JobPostingDTO jobPostingDTO;
    private JobPosting jobPosting;

    @BeforeEach
    void setUp() {
        jobPostingDTO = new JobPostingDTO();
        jobPostingDTO.setTitle("Software Engineer");
        jobPostingDTO.setCompany("Tech Corp");
        jobPostingDTO.setDescription("Java Developer role");
        jobPostingDTO.setRequiredSkills(Arrays.asList("Java", "Spring"));
        jobPostingDTO.setLocation("New York");
        jobPostingDTO.setExpiryDate(LocalDateTime.now().plusDays(30));

        jobPosting = new JobPosting();
        jobPosting.setId("1");
        jobPosting.setTitle(jobPostingDTO.getTitle());
        jobPosting.setCompany(jobPostingDTO.getCompany());
        jobPosting.setDescription(jobPostingDTO.getDescription());
        jobPosting.setRequiredSkills(jobPostingDTO.getRequiredSkills());
        jobPosting.setLocation(jobPostingDTO.getLocation());
        jobPosting.setExpiryDate(jobPostingDTO.getExpiryDate());
        jobPosting.setIsActive(true);
    }

    @Test
    void createJobPosting_Success() {
        when(jobPostingService.createJobPosting(any(JobPostingDTO.class))).thenReturn(jobPosting);

        ResponseEntity<JobPosting> response = jobPostingController.createJobPosting(jobPostingDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobPosting.getId(), response.getBody().getId());
    }

    @Test
    void getAllActiveJobs_Success() {
        List<JobPosting> activeJobs = Arrays.asList(jobPosting);
        when(jobPostingService.getAllActiveJobPostings()).thenReturn(activeJobs);

        ResponseEntity<List<JobPosting>> response = jobPostingController.getAllActiveJobs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getJobById_Success() {
        when(jobPostingService.getJobPosting(anyString())).thenReturn(Optional.of(jobPosting));

        ResponseEntity<JobPosting> response = jobPostingController.getJobById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobPosting.getId(), response.getBody().getId());
    }

    @Test
    void getJobById_NotFound() {
        when(jobPostingService.getJobPosting(anyString())).thenReturn(Optional.empty());

        ResponseEntity<JobPosting> response = jobPostingController.getJobById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void searchBySkills_Success() {
        List<String> skills = Arrays.asList("Java", "Spring");
        List<JobPosting> matchingJobs = Arrays.asList(jobPosting);
        when(jobPostingService.searchJobsBySkills(skills)).thenReturn(matchingJobs);

        ResponseEntity<List<JobPosting>> response = jobPostingController.searchBySkills(skills);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void searchByLocation_Success() {
        List<JobPosting> matchingJobs = Arrays.asList(jobPosting);
        when(jobPostingService.searchJobsByLocation("New York")).thenReturn(matchingJobs);

        ResponseEntity<List<JobPosting>> response = jobPostingController.searchByLocation("New York");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
