package com.naviksha.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "careers")
public class Career {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String careerId;
    
    private String careerName;
    private String bucket;
    private String riasecProfile;
    private String primarySubjects;
    private String tags;
    private String minQualification;
    private String top5CollegeCourses;
    private String baseParagraph;
    private String microprojects;
    private String whyFit;
}