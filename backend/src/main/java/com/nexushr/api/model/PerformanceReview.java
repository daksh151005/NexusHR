package com.nexushr.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private String reviewPeriod;
    private int managerScore;
    private int peerScore;
    private int goalCompletion;
    private String performanceBand;
    private String feedback;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public int getManagerScore() {
        return managerScore;
    }

    public void setManagerScore(int managerScore) {
        this.managerScore = managerScore;
    }

    public int getPeerScore() {
        return peerScore;
    }

    public void setPeerScore(int peerScore) {
        this.peerScore = peerScore;
    }

    public int getGoalCompletion() {
        return goalCompletion;
    }

    public void setGoalCompletion(int goalCompletion) {
        this.goalCompletion = goalCompletion;
    }

    public String getPerformanceBand() {
        return performanceBand;
    }

    public void setPerformanceBand(String performanceBand) {
        this.performanceBand = performanceBand;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
