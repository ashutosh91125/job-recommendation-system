package com.jobrecommendation.userservice.repository;

import com.jobrecommendation.userservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    @Query("SELECT p FROM UserProfile p WHERE p.user.enabled = true")
    List<UserProfile> findAllActiveProfiles();
    
    @Query("SELECT p FROM UserProfile p WHERE :skill MEMBER OF p.skills")
    List<UserProfile> findBySkill(String skill);
    
    List<UserProfile> findByPreferredLocation(String location);
}
