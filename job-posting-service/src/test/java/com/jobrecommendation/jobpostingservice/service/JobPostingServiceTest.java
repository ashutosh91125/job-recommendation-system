package com.jobrecommendation.jobpostingservice.service;

import com.jobrecommendation.jobpostingservice.dto.JobPostingDTO;
import com.jobrecommendation.jobpostingservice.model.JobPosting;
import com.jobrecommendation.jobpostingservice.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private KafkaTemplate<String, JobPosting> kafkaTemplate;

    @InjectMocks
    private JobPostingService jobPostingService;

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
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
        when(kafkaTemplate.send(anyString(), any(JobPosting.class))).thenReturn(null);

        JobPosting result = jobPostingService.createJobPosting(jobPostingDTO);

        assertNotNull(result);
        assertEquals(jobPostingDTO.getTitle(), result.getTitle());
        assertEquals(jobPostingDTO.getCompany(), result.getCompany());
        assertTrue(result.getIsActive());
        verify(kafkaTemplate).send(eq("job-postings"), any(JobPosting.class));
    }

    @Test
    void getAllActiveJobPostings_Success() {
        List<JobPosting> activeJobs = Arrays.asList(jobPosting);
        when(jobPostingRepository.findByIsActiveTrue()).thenReturn(activeJobs);

        List<JobPosting> result = jobPostingService.getAllActiveJobPostings();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(jobPosting.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getJobPosting_Success() {
        when(jobPostingRepository.findById("1")).thenReturn(Optional.of(jobPosting));

        Optional<JobPosting> result = jobPostingService.getJobPosting("1");

        assertTrue(result.isPresent());
        assertEquals(jobPosting.getId(), result.get().getId());
    }

    @Test
    void searchJobsBySkills_Success() {
        List<String> skills = Arrays.asList("Java", "Spring");
        List<JobPosting> matchingJobs = Arrays.asList(jobPosting);
        when(jobPostingRepository.findByMultipleSkills(skills)).thenReturn(matchingJobs);

        List<JobPosting> result = jobPostingService.searchJobsBySkills(skills);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRequiredSkills().containsAll(skills));
    }

    @Test
    void deactivateJobPosting_Success() {
        when(jobPostingRepository.findById("1")).thenReturn(Optional.of(jobPosting));
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(jobPosting);
        when(kafkaTemplate.send(anyString(), any(JobPosting.class))).thenReturn(null);

        jobPostingService.deactivateJobPosting("1");

        verify(jobPostingRepository).findById("1");
        verify(jobPostingRepository).save(any(JobPosting.class));
        verify(kafkaTemplate).send(eq("job-postings"), any(JobPosting.class));
    }
}
