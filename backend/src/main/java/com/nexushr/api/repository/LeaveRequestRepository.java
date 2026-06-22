package com.nexushr.api.repository;

import com.nexushr.api.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    long countByStatus(String status);
}
