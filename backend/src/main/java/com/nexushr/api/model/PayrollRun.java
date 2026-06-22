package com.nexushr.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_runs")
public class PayrollRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cycle;
    private BigDecimal grossPayroll;
    private BigDecimal netPayroll;
    private int processedEmployees;
    private String status;
    private LocalDateTime processedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public BigDecimal getGrossPayroll() {
        return grossPayroll;
    }

    public void setGrossPayroll(BigDecimal grossPayroll) {
        this.grossPayroll = grossPayroll;
    }

    public BigDecimal getNetPayroll() {
        return netPayroll;
    }

    public void setNetPayroll(BigDecimal netPayroll) {
        this.netPayroll = netPayroll;
    }

    public int getProcessedEmployees() {
        return processedEmployees;
    }

    public void setProcessedEmployees(int processedEmployees) {
        this.processedEmployees = processedEmployees;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
