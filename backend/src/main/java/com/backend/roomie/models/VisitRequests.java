package com.backend.roomie.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "visit_requests")
public class VisitRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // status of the visit request
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
    // visit date
    @Column(name = "visit_date")
    private Date visitDate;

    @Column(updatable=false)
    private Date createdAt;
    private Date updatedAt;

    // relation between the VisitRequest class and the User class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // relation between the VisitRequest class and the PropretyList class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private PropretyList propertyList;

}
