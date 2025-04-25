package com.backend.roomie.models;


import jakarta.persistence.*;

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

    // Status field
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    // No-args constructor
    public VisitRequests() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PropretyList getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(PropretyList propertyList) {
        this.propertyList = propertyList;
    }
}
