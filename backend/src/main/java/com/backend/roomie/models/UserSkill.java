package com.backend.roomie.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "user_skills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Skill name
    @NotEmpty(message = "Skill name is required")
    private String name;

    // Description of the skill
    private String description;

    // The user can choose to offer a skill or indicate they want to learn it
    @Enumerated(EnumType.STRING)
    private SkillType type;

    // Skill type enum
    public enum SkillType {
        OFFER,  // Skills the user can offer to others
        LEARN   // Skills the user wants to learn from others
    }

    // Proficiency level (for OFFER type)
    private Integer proficiencyLevel;

    // Timestamps
    @Column(updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Relation between the UserSkill class and the User class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
