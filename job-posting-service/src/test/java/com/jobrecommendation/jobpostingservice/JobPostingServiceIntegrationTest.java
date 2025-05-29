package com.jobrecommendation.jobpostingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobrecommendation.jobpostingservice.dto.JobPostingDTO;
import com.jobrecommendation.jobpostingservice.model.JobPosting;
import com.jobrecommendation.jobpostingservice.repository.JobPostingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class JobPostingServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    private JobPostingDTO jobPostingDTO;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        jobPostingDTO = new JobPostingDTO();
        jobPostingDTO.setTitle("Software Engineer");
        jobPostingDTO.setCompany("Tech Corp");
        jobPostingDTO.setDescription("Java Developer role");
        jobPostingDTO.setRequiredSkills(Arrays.asList("Java", "Spring"));
        jobPostingDTO.setLocation("New York");
        jobPostingDTO.setExpiryDate(LocalDateTime.now().plusDays(30));
    }

    @AfterEach
    void cleanup() {
        jobPostingRepository.deleteAll();
    }

    @Test
    void createJobPosting_Success() throws Exception {
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPostingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(jobPostingDTO.getTitle())))
                .andExpect(jsonPath("$.company", is(jobPostingDTO.getCompany())))
                .andExpect(jsonPath("$.isActive", is(true)));
    }

    @Test
    void getAllActiveJobs_Success() throws Exception {
        // Create a job posting first
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPostingDTO)));

        // Get all active jobs
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", is(jobPostingDTO.getTitle())));
    }

    @Test
    void searchBySkills_Success() throws Exception {
        // Create a job posting first
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobPostingDTO)));

        // Search by skills
        mockMvc.perform(get("/api/jobs/search/skills")
                .param("skills", "Java", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].requiredSkills", hasItems("Java", "Spring")));
    }
}
