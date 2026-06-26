package com.careerportal.entity;

import jakarta.persistence.*;

/** Course entity */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String provider;  // Coursera, Udemy, etc.
    private String category;

    @Column(length = 500)
    private String description;

    private String duration;
    private String price;
    private String url;
    private String level;     // Beginner, Intermediate, Advanced
    private boolean active = true;

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
