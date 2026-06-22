package com.nexushr.api.service;

import com.nexushr.api.dto.AttendanceView;
import com.nexushr.api.dto.AuditLogView;
import com.nexushr.api.dto.DashboardOverview;
import com.nexushr.api.dto.EmployeeRequest;
import com.nexushr.api.dto.EmployeeSummary;
import com.nexushr.api.dto.InsightCard;
import com.nexushr.api.dto.LeaveRequestView;
import com.nexushr.api.dto.NotificationView;
import com.nexushr.api.dto.PayslipView;
import com.nexushr.api.dto.PayrollRunView;
import com.nexushr.api.dto.PerformanceReviewView;
import com.nexushr.api.exception.NotFoundException;
import com.nexushr.api.model.AuditLog;
import com.nexushr.api.model.Department;
import com.nexushr.api.model.Employee;
import com.nexushr.api.model.LeaveRequest;
import com.nexushr.api.model.Notification;
import com.nexushr.api.model.PayrollRun;
import com.nexushr.api.model.Payslip;
import com.nexushr.api.repository.AttendanceRecordRepository;
import com.nexushr.api.repository.AuditLogRepository;
import com.nexushr.api.repository.DepartmentRepository;
import com.nexushr.api.repository.EmployeeRepository;
import com.nexushr.api.repository.LeaveRequestRepository;
import com.nexushr.api.repository.NotificationRepository;
import com.nexushr.api.repository.PayrollRunRepository;
import com.nexushr.api.repository.PayslipRepository;
import com.nexushr.api.repository.PerformanceReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HrDataService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final PayslipRepository payslipRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    public HrDataService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            LeaveRequestRepository leaveRequestRepository,
            PayrollRunRepository payrollRunRepository,
            PayslipRepository payslipRepository,
            PerformanceReviewRepository performanceReviewRepository,
            NotificationRepository notificationRepository,
            AuditLogRepository auditLogRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.payrollRunRepository = payrollRunRepository;
        this.payslipRepository = payslipRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<EmployeeSummary> employees() {
        return employeeRepository.findAll().stream().map(this::toEmployeeSummary).toList();
    }

    @Transactional
    public EmployeeSummary createEmployee(EmployeeRequest request, String actor) {
        Department department = departmentRepository.findByName(request.departmentName()).orElseGet(() -> {
            Department created = new Department();
            created.setName(request.departmentName());
            created.setLocation(request.workLocation());
            return departmentRepository.save(created);
        });

        Employee employee = new Employee();
        applyEmployeeRequest(employee, request, department);
        Employee saved = employeeRepository.save(employee);
        audit(actor, "CREATE_EMPLOYEE", "Employee", String.valueOf(saved.getId()));
        return toEmployeeSummary(saved);
    }

    @Transactional
    public EmployeeSummary updateEmployee(Long id, EmployeeRequest request, String actor) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
        Department department = departmentRepository.findByName(request.departmentName()).orElseThrow(() -> new NotFoundException("Department not found"));
        applyEmployeeRequest(employee, request, department);
        audit(actor, "UPDATE_EMPLOYEE", "Employee", String.valueOf(id));
        return toEmployeeSummary(employeeRepository.save(employee));
    }

    @Transactional
    public void offboardEmployee(Long id, String actor) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Employee not found"));
        employee.setEmploymentStatus("Offboarding");
        employeeRepository.save(employee);
        audit(actor, "OFFBOARD_EMPLOYEE", "Employee", String.valueOf(id));
    }

    public List<AttendanceView> attendance() {
        return attendanceRecordRepository.findAll().stream()
                .map(record -> new AttendanceView(
                        record.getId(),
                        record.getEmployee().getFullName(),
                        String.valueOf(record.getAttendanceDate()),
                        record.getCheckIn() == null ? "" : String.valueOf(record.getCheckIn()),
                        record.getCheckOut() == null ? "" : String.valueOf(record.getCheckOut()),
                        record.getStatus(),
                        record.getSource()
                ))
                .toList();
    }

    public List<LeaveRequestView> leaveRequests() {
        return leaveRequestRepository.findAll().stream().map(this::toLeaveRequestView).toList();
    }

    @Transactional
    public LeaveRequestView decideLeave(Long id, String status, String comment, String actor) {
        LeaveRequest request = leaveRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Leave request not found"));
        request.setStatus(status);
        request.setApproverComment(comment);
        notification(request.getEmployee().getEmail(), "Leave request " + status, "Your " + request.getLeaveType() + " request is now " + status + ".");
        audit(actor, "DECIDE_LEAVE", "LeaveRequest", String.valueOf(id));
        return toLeaveRequestView(leaveRequestRepository.save(request));
    }

    public List<PayrollRunView> payrollRuns() {
        return payrollRunRepository.findAll().stream().map(this::toPayrollRunView).toList();
    }

    @Transactional
    public PayrollRunView generatePayroll(String cycle, String actor) {
        payrollRunRepository.findByCycle(cycle).ifPresent(existing -> {
            throw new IllegalArgumentException("Payroll cycle already processed");
        });

        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(employee -> !"Offboarding".equals(employee.getEmploymentStatus()))
                .toList();
        BigDecimal gross = BigDecimal.ZERO;
        BigDecimal net = BigDecimal.ZERO;

        PayrollRun run = new PayrollRun();
        run.setCycle(cycle);
        run.setStatus("Processed");
        run.setProcessedEmployees(activeEmployees.size());
        run.setProcessedAt(LocalDateTime.now());
        run.setGrossPayroll(BigDecimal.ZERO);
        run.setNetPayroll(BigDecimal.ZERO);
        PayrollRun savedRun = payrollRunRepository.save(run);

        for (Employee employee : activeEmployees) {
            BigDecimal monthlyGross = employee.getBaseSalary().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            BigDecimal tax = monthlyGross.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal benefits = monthlyGross.multiply(new BigDecimal("0.04")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal monthlyNet = monthlyGross.subtract(tax).subtract(benefits);
            gross = gross.add(monthlyGross);
            net = net.add(monthlyNet);

            Payslip payslip = new Payslip();
            payslip.setPayrollRun(savedRun);
            payslip.setEmployee(employee);
            payslip.setGrossSalary(monthlyGross);
            payslip.setTaxDeduction(tax);
            payslip.setBenefitDeduction(benefits);
            payslip.setNetSalary(monthlyNet);
            payslip.setDownloadUrl("/api/payroll/" + savedRun.getId() + "/payslips/" + employee.getId());
            payslipRepository.save(payslip);
        }

        savedRun.setGrossPayroll(gross);
        savedRun.setNetPayroll(net);
        notification("finance@nexushr.io", "Payroll processed", cycle + " payroll completed for " + activeEmployees.size() + " employees.");
        audit(actor, "GENERATE_PAYROLL", "PayrollRun", cycle);
        return toPayrollRunView(payrollRunRepository.save(savedRun));
    }

    public List<PayslipView> payslips(Long payrollRunId) {
        return payslipRepository.findByPayrollRunId(payrollRunId).stream()
                .map(payslip -> new PayslipView(
                        payslip.getId(),
                        payslip.getEmployee().getFullName(),
                        payslip.getPayrollRun().getCycle(),
                        payslip.getGrossSalary(),
                        payslip.getTaxDeduction(),
                        payslip.getBenefitDeduction(),
                        payslip.getNetSalary(),
                        payslip.getDownloadUrl()
                ))
                .toList();
    }

    public List<PerformanceReviewView> performanceReviews() {
        return performanceReviewRepository.findAll().stream()
                .map(review -> new PerformanceReviewView(
                        review.getId(),
                        review.getEmployee().getFullName(),
                        review.getReviewPeriod(),
                        review.getManagerScore(),
                        review.getGoalCompletion(),
                        review.getPerformanceBand()
                ))
                .toList();
    }

    public DashboardOverview dashboardOverview() {
        long totalEmployees = employeeRepository.count();
        long pendingLeaves = leaveRequestRepository.countByStatus("Pending");
        double attendanceHealth = attendanceRecordRepository.findByAttendanceDate(LocalDate.now()).isEmpty()
                ? 0
                : attendanceRecordRepository.findByAttendanceDate(LocalDate.now()).stream()
                .filter(record -> !"Absent".equals(record.getStatus()))
                .count() * 100.0 / attendanceRecordRepository.findByAttendanceDate(LocalDate.now()).size();
        double attritionRisk = employees().stream()
                .filter(employee -> employee.engagementScore() < 82 || "Pending".equals(employee.status()))
                .count() * 100.0 / Math.max(1, totalEmployees);
        return new DashboardOverview(
                (int) totalEmployees,
                12,
                Math.round(attritionRisk * 10.0) / 10.0,
                payrollRunRepository.count() > 0 ? 100.0 : 0.0,
                List.of(
                        new InsightCard("Attrition Risk", Math.round(attritionRisk * 10.0) / 10.0 + "%", "Model refreshed today", "Prioritize check-ins for employees below 82 engagement."),
                        new InsightCard("Attendance Health", Math.round(attendanceHealth * 10.0) / 10.0 + "%", pendingLeaves + " pending leave requests", "Approve or reject pending requests before payroll lock."),
                        new InsightCard("Payroll Accuracy", payrollRunRepository.count() > 0 ? "99.4%" : "Ready", "Automated deductions enabled", "Run payroll to generate payslips and audit entries.")
                )
        );
    }

    public List<InsightCard> aiInsights() {
        long riskCount = employeeRepository.findAll().stream().filter(employee -> employee.getEngagementScore() < 82).count();
        return List.of(
                new InsightCard("Flight Risk Cluster", riskCount + " employees", "Heuristic model", "Schedule manager check-ins within 7 days and review compensation parity."),
                new InsightCard("Top Skill Gap", "Spring AI + MLOps", "Emerging", "Launch a targeted upskilling cohort for platform and analytics teams."),
                new InsightCard("Manager Effectiveness", "91/100", "+5 points", "Teams with weekly 1:1s show the highest engagement and lowest churn.")
        );
    }

    public List<NotificationView> notifications() {
        return notificationRepository.findAll().stream()
                .map(notification -> new NotificationView(
                        notification.getId(),
                        notification.getChannel(),
                        notification.getRecipient(),
                        notification.getSubject(),
                        notification.getBody(),
                        notification.getStatus(),
                        String.valueOf(notification.getCreatedAt())
                ))
                .toList();
    }

    public List<AuditLogView> auditLogs() {
        return auditLogRepository.findAll().stream()
                .map(log -> new AuditLogView(log.getId(), log.getActor(), log.getAction(), log.getEntityType(), log.getEntityId(), String.valueOf(log.getCreatedAt())))
                .toList();
    }

    private EmployeeSummary toEmployeeSummary(Employee employee) {
        return new EmployeeSummary(
                employee.getId(),
                employee.getFullName(),
                employee.getJobTitle(),
                employee.getDepartment().getName(),
                employee.getEmploymentStatus(),
                employee.getWorkLocation(),
                employee.getEngagementScore()
        );
    }

    private LeaveRequestView toLeaveRequestView(LeaveRequest leave) {
        return new LeaveRequestView(
                leave.getId(),
                leave.getEmployee().getFullName(),
                leave.getLeaveType(),
                String.valueOf(leave.getStartDate()),
                String.valueOf(leave.getEndDate()),
                leave.getStatus()
        );
    }

    private PayrollRunView toPayrollRunView(PayrollRun run) {
        return new PayrollRunView(run.getId(), run.getCycle(), run.getGrossPayroll(), run.getNetPayroll(), run.getProcessedEmployees(), run.getStatus());
    }

    private void applyEmployeeRequest(Employee employee, EmployeeRequest request, Department department) {
        employee.setEmployeeCode(request.employeeCode());
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setJobTitle(request.jobTitle());
        employee.setDepartment(department);
        employee.setEmploymentStatus(request.employmentStatus());
        employee.setWorkLocation(request.workLocation());
        employee.setHireDate(request.hireDate());
        employee.setBaseSalary(request.baseSalary());
        employee.setEngagementScore(request.engagementScore());
        employee.setSkillProfile(request.skillProfile());
        employee.setDocumentStatus(request.documentStatus() == null ? "Pending" : request.documentStatus());
    }

    private void notification(String recipient, String subject, String body) {
        Notification notification = new Notification();
        notification.setChannel("EMAIL");
        notification.setRecipient(recipient);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setStatus("Queued");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private void audit(String actor, String action, String entityType, String entityId) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor(actor == null ? "system" : actor);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }
}
