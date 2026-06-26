package com.careerportal.entity;

import jakarta.persistence.*;

/**
 * Student entity - stores student profile and academic details
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Personal Info
    private String dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String profilePhoto;

    // Academic Info
    private String currentClass;      // e.g., "12th Grade"
    private String school;
    private String board;             // CBSE, ICSE, State Board
    private Double mathMarks;
    private Double scienceMarks;
    private Double englishMarks;
    private Double overallPercentage;

    // Career Interests
    @Column(length = 500)
    private String skills;

    @Column(length = 500)
    private String interests;

    @Column(length = 500)
    private String hobbies;

    @Column(length = 500)
    private String careerGoal;

    // Parent link
    private Long parentId;

    // Counselor link
    private Long counselorId;

    // Profile completion %
    private Integer profileCompletion = 0;

    // Constructors
    public Student() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getCurrentClass() { return currentClass; }
    public void setCurrentClass(String currentClass) { this.currentClass = currentClass; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getBoard() { return board; }
    public void setBoard(String board) { this.board = board; }

    public Double getMathMarks() { return mathMarks; }
    public void setMathMarks(Double mathMarks) { this.mathMarks = mathMarks; }

    public Double getScienceMarks() { return scienceMarks; }
    public void setScienceMarks(Double scienceMarks) { this.scienceMarks = scienceMarks; }

    public Double getEnglishMarks() { return englishMarks; }
    public void setEnglishMarks(Double englishMarks) { this.englishMarks = englishMarks; }

    public Double getOverallPercentage() { return overallPercentage; }
    public void setOverallPercentage(Double overallPercentage) { this.overallPercentage = overallPercentage; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }

    public String getCareerGoal() { return careerGoal; }
    public void setCareerGoal(String careerGoal) { this.careerGoal = careerGoal; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getCounselorId() { return counselorId; }
    public void setCounselorId(Long counselorId) { this.counselorId = counselorId; }

    public Integer getProfileCompletion() { return profileCompletion; }
    public void setProfileCompletion(Integer profileCompletion) { this.profileCompletion = profileCompletion; }
}
