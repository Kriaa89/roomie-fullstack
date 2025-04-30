package com.backend.roomie.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Entity
@Table(name = "property_list")
public class PropertyList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // property name
    @NotEmpty(message = "Property name is required")
    private String name;

    // property type
    @NotEmpty(message = "Property type is required")
    private String type;

    // property location
    @NotEmpty(message = "Property location is required")
    private String location;

    // property price
    @NotEmpty(message = "Property price is required")
    private String price;

    // property description
    @NotEmpty(message = "Property description is required")
    private String description;

    // property amenities
    @Column(name = "amenities")
    //@NotEmpty(message = "Property amenities are required")
    private String amenities;

    // property surface
    @Column(name = "surface")
    //@NotEmpty(message = "Property surface is required")
    private String surface;

    // property number of rooms
    @Column(name = "number_of_rooms")
    //@NotEmpty(message = "Property number of rooms is required")
    private Integer numberOfRooms;

    // property number of bathrooms
    @Column(name = "number_of_bathrooms")
    private Integer numberOfBathrooms;

    // property number of bedrooms
    @Column(name = "number_of_bedrooms")
    private Integer numberOfBedrooms;

    // rules
    @Lob
    @Column(name = "rules")
    private String propertyRules;

    // availability
    @Column(name = "availability")
    private boolean availability;

    // audiance
    @Column(name = "audiance")
    private String audiance;

    @Column(updatable=false)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updatedAt;

    // relation between the property list and the user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User owner; // Direct relationship to user

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // No-args constructor
    public PropertyList() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public String getPropertyRules() {
        return propertyRules;
    }

    public void setPropertyRules(String propertyRules) {
        this.propertyRules = propertyRules;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getAudiance() {
        return audiance;
    }

    public void setAudiance(String audiance) {
        this.audiance = audiance;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}