package com.jobrecommendation.recommendationservice.controller;

import com.jobrecommendation.recommendationservice.model.JobRecommendation;
import com.jobrecommendation.recommendationservice.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    private JobRecommendation testRecommendation;

    @BeforeEach
    void setUp() {
        testRecommendation = new JobRecommendation();
        testRecommendation.setUserId("1");
        testRecommendation.setJobId("1");
        testRecommendation.setMatchScore(0.85);
        testRecommendation.setMatchFactors(new HashMap<>() {{
            put("skillMatch", 0.9);
            put("locationMatch", 1.0);
            put("experienceMatch", 0.8);
            put("salaryMatch", 1.0);
            put("companyMatch", 0.5);
        }});
    }

    @Test
    void getRecommendationsForUser_Success() throws Exception {
        when(recommendationService.getRecommendationsForUser(anyString(), anyInt()))
            .thenReturn(Arrays.asList(testRecommendation));

        mockMvc.perform(get("/api/recommendations/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].jobId").value("1"))
                .andExpect(jsonPath("$[0].matchScore").value(0.85));
    }

    @Test
    void getRecommendationsForUser_NoResults() throws Exception {
        when(recommendationService.getRecommendationsForUser(anyString(), anyInt()))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recommendations/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMatchingUsersForJob_Success() throws Exception {
        when(recommendationService.getMatchingUsersForJob(anyString(), anyInt()))
            .thenReturn(Arrays.asList(testRecommendation));

        mockMvc.perform(get("/api/recommendations/jobs/1/matching-users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].jobId").value("1"))
                .andExpect(jsonPath("$[0].matchScore").value(0.85));
    }

    @Test
    void getMatchingUsersForJob_NoResults() throws Exception {
        when(recommendationService.getMatchingUsersForJob(anyString(), anyInt()))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recommendations/jobs/999/matching-users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void refreshRecommendations_Success() throws Exception {
        mockMvc.perform(post("/api/recommendations/refresh/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
