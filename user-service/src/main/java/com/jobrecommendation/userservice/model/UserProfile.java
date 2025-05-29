package com.jobrecommendation.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id; // Same as user ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    private String preferredLocation;
    private String experienceLevel;
    
    @ElementCollection
    @CollectionTable(name = "preferred_companies", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "company")
    private List<String> preferredCompanies = new ArrayList<>();
    
    private Double expectedSalary;
    private String preferredEmploymentType;
    private String resume; // Could store file path or URL
    private String linkedInProfile;
    private String githubProfile;

    @Column(length = 1000)
    private String summary;
}
