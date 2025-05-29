package com.jobrecommendation.jobpostingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobPostingDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;

    @NotNull(message = "Required skills must not be null")
    @Size(min = 1, message = "At least one required skill must be specified")
    private List<String> requiredSkills;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @PositiveOrZero(message = "Salary must be zero or positive")
    private Double salary;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;

    @Future(message = "Expiry date must be in the future")
    @NotNull(message = "Expiry date is required")
    private LocalDateTime expiryDate;
}
