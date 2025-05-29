package com.jobrecommendation.jobpostingservice.config;

import com.jobrecommendation.jobpostingservice.model.JobPosting;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.domain.Sort;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        // Create text index for search functionality
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("title")
                .onField("description")
                .onField("requiredSkills")
                .onField("company")
                .build();
        
        mongoTemplate.indexOps(JobPosting.class).ensureIndex(textIndex);

        // Create index on location and isActive
        mongoTemplate.indexOps(JobPosting.class)
                .ensureIndex(new Index()
                        .on("location", Sort.Direction.ASC)
                        .on("isActive", Sort.Direction.ASC));

        // Create index on expiryDate and isActive
        mongoTemplate.indexOps(JobPosting.class)
                .ensureIndex(new Index()
                        .on("expiryDate", Sort.Direction.DESC)
                        .on("isActive", Sort.Direction.ASC));

        // Create index on company
        mongoTemplate.indexOps(JobPosting.class)
                .ensureIndex(new Index().on("company", Sort.Direction.ASC));
    }
}
