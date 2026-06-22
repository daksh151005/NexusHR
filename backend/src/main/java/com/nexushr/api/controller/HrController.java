package com.nexushr.api.controller;

import com.nexushr.api.dto.AttendanceView;
import com.nexushr.api.dto.AuditLogView;
import com.nexushr.api.dto.DashboardOverview;
import com.nexushr.api.dto.EmployeeRequest;
import com.nexushr.api.dto.EmployeeSummary;
import com.nexushr.api.dto.InsightCard;
import com.nexushr.api.dto.LeaveDecisionRequest;
import com.nexushr.api.dto.LeaveRequestView;
import com.nexushr.api.dto.NotificationView;
import com.nexushr.api.dto.PayrollRequest;
import com.nexushr.api.dto.PayrollRunView;
import com.nexushr.api.dto.PayslipView;
import com.nexushr.api.dto.PerformanceReviewView;
import com.nexushr.api.service.HrDataService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HrController {

    private final HrDataService hrDataService;

    public HrController(HrDataService hrDataService) {
        this.hrDataService = hrDataService;
    }

    @GetMapping("/dashboard/overview")
    public DashboardOverview dashboardOverview() {
        return hrDataService.dashboardOverview();
    }

    @GetMapping("/employees")
    public List<EmployeeSummary> employees() {
        return hrDataService.employees();
    }

    @PostMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public EmployeeSummary createEmployee(@Valid @RequestBody EmployeeRequest request, Authentication authentication) {
        return hrDataService.createEmployee(request, authentication.getName());
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public EmployeeSummary updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request, Authentication authentication) {
        return hrDataService.updateEmployee(id, request, authentication.getName());
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void offboardEmployee(@PathVariable Long id, Authentication authentication) {
        hrDataService.offboardEmployee(id, authentication.getName());
    }

    @GetMapping("/attendance")
    public List<AttendanceView> attendance() {
        return hrDataService.attendance();
    }

    @GetMapping("/leaves")
    public List<LeaveRequestView> leaves() {
        return hrDataService.leaveRequests();
    }

    @PostMapping("/leaves/{id}/decision")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public LeaveRequestView decideLeave(@PathVariable Long id, @Valid @RequestBody LeaveDecisionRequest request, Authentication authentication) {
        return hrDataService.decideLeave(id, request.status(), request.approverComment(), authentication.getName());
    }

    @GetMapping("/payroll")
    public List<PayrollRunView> payrollRuns() {
        return hrDataService.payrollRuns();
    }

    @PostMapping("/payroll")
    @PreAuthorize("hasRole('ADMIN')")
    public PayrollRunView generatePayroll(@Valid @RequestBody PayrollRequest request, Authentication authentication) {
        return hrDataService.generatePayroll(request.cycle(), authentication.getName());
    }

    @GetMapping("/payroll/{id}/payslips")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PayslipView> payslips(@PathVariable Long id) {
        return hrDataService.payslips(id);
    }

    @GetMapping("/performance")
    public List<PerformanceReviewView> performanceReviews() {
        return hrDataService.performanceReviews();
    }

    @GetMapping("/insights")
    public List<InsightCard> insights() {
        return hrDataService.aiInsights();
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<NotificationView> notifications() {
        return hrDataService.notifications();
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogView> auditLogs() {
        return hrDataService.auditLogs();
    }
}
