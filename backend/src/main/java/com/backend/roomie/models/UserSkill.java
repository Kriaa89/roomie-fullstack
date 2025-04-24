package com.backend.roomie.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

@Entity
@Table(name = "user_skills")
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // skill name
    @NotEmpty(message = "Skill name is required")
    private String name;

    //  the the user can choose gonna offer a skill or he want to lean it
    public enum SkillType {
        OFFER,
        LEARN
    }

    @Column(updatable=false)
    private Date createdAt;
    private Date updatedAt;

    // relation betwenn the userSkill class and the User class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
