package com.jobrecommendation.jobpostingservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "job_postings")
@CompoundIndexes({
    @CompoundIndex(name = "location_active", def = "{'location': 1, 'isActive': 1}"),
    @CompoundIndex(name = "expiry_active", def = "{'expiryDate': 1, 'isActive': 1}")
})
public class JobPosting {
    @Id
    private String id;
      @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 100, message = "Job title must be between 3 and 100 characters")
    @Indexed
    @Field("title")
    @TextIndexed
    private String title;
    
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    @Indexed
    @Field("company")
    private String company;
    
    @NotBlank(message = "Job description is required")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    @TextIndexed
    @Field("description")
    private String description;
    
    @NotEmpty(message = "Required skills list cannot be empty")
    @Size(min = 1, max = 20, message = "Required skills must have between 1 and 20 items")
    @TextIndexed
    @Field("required_skills")
    private List<String> requiredSkills;
    
    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    @Indexed
    @Field("location")
    private String location;
    
    @NotBlank(message = "Employment type is required")
    @Pattern(regexp = "^(FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP)$", message = "Invalid employment type")
    @Field("employment_type")
    private String employmentType;
    
    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be greater than or equal to 0")
    @Field("salary")
    private Double salary;
    
    @Field("experience_level")
    private String experienceLevel;
    
    @Field("posted_date")
    private LocalDateTime postedDate;
    
    @Indexed(expireAfterSeconds = 0)
    @Field("expiry_date")
    private LocalDateTime expiryDate;
    
    @Indexed
    @Field("is_active")
    private Boolean isActive;
}
