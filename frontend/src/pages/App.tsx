import { useEffect, useState, type FormEvent } from "react";
import { DataTable } from "../components/DataTable";
import { MetricCard } from "../components/MetricCard";
import {
  decideLeave,
  generatePayroll,
  getAttendance,
  getAuditLogs,
  getDashboardOverview,
  getEmployees,
  getInsights,
  getLeaveRequests,
  getNotifications,
  getPayrollRuns,
  getPerformanceReviews,
  login
} from "../lib/api";
import type {
  AttendanceView,
  AuditLogView,
  AuthResponse,
  DashboardOverview,
  EmployeeSummary,
  InsightCard,
  LeaveRequestView,
  NotificationView,
  PayrollRunView,
  PerformanceReviewView
} from "../lib/types";

type WorkspaceData = {
  overview: DashboardOverview | null;
  employees: EmployeeSummary[];
  attendance: AttendanceView[];
  leaves: LeaveRequestView[];
  payroll: PayrollRunView[];
  reviews: PerformanceReviewView[];
  insights: InsightCard[];
  notifications: NotificationView[];
  auditLogs: AuditLogView[];
};

const emptyData: WorkspaceData = {
  overview: null,
  employees: [],
  attendance: [],
  leaves: [],
  payroll: [],
  reviews: [],
  insights: [],
  notifications: [],
  auditLogs: []
};

export default function App() {
  const [session, setSession] = useState<AuthResponse | null>(() => {
    const stored = localStorage.getItem("nexushr-session");
    return stored ? (JSON.parse(stored) as AuthResponse) : null;
  });
  const [data, setData] = useState<WorkspaceData>(emptyData);
  const [error, setError] = useState("");
  const [cycle, setCycle] = useState("July 2026");
  const [loading, setLoading] = useState(false);

  async function loadData(token = session?.accessToken) {
    if (!token) {
      return;
    }

    try {
      const [overview, employees, attendance, leaves, payroll, reviews, insights, notifications, auditLogs] = await Promise.all([
        getDashboardOverview(token),
        getEmployees(token),
        getAttendance(token),
        getLeaveRequests(token),
        getPayrollRuns(token),
        getPerformanceReviews(token),
        getInsights(token),
        getNotifications(token).catch(() => []),
        getAuditLogs(token).catch(() => [])
      ]);
      setData({ overview, employees, attendance, leaves, payroll, reviews, insights, notifications, auditLogs });
    } catch (loadError) {
      const message = loadError instanceof Error ? loadError.message : "Could not load dashboard";
      if (message.includes("401") || message.includes("403")) {
        localStorage.removeItem("nexushr-session");
        setSession(null);
        setData(emptyData);
        setError("Session expired. Please sign in again.");
        return;
      }
      setError(message);
    }
  }

  useEffect(() => {
    void loadData();
  }, [session?.accessToken]);

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setLoading(true);
    const form = new FormData(event.currentTarget);
    try {
      const nextSession = await login(String(form.get("email")), String(form.get("password")));
      localStorage.setItem("nexushr-session", JSON.stringify(nextSession));
      setSession(nextSession);
      await loadData(nextSession.accessToken);
    } catch (loginError) {
      setError(loginError instanceof Error ? loginError.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleGeneratePayroll() {
    if (!session) {
      return;
    }
    setLoading(true);
    setError("");
    try {
      await generatePayroll(session.accessToken, cycle);
      await loadData(session.accessToken);
    } catch (payrollError) {
      setError(payrollError instanceof Error ? payrollError.message : "Payroll generation failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleLeaveDecision(id: number, status: string) {
    if (!session) {
      return;
    }
    setLoading(true);
    setError("");
    try {
      await decideLeave(session.accessToken, id, status);
      await loadData(session.accessToken);
    } catch (leaveError) {
      setError(leaveError instanceof Error ? leaveError.message : "Leave update failed");
    } finally {
      setLoading(false);
    }
  }

  if (!session) {
    return (
      <main className="login-shell">
        <section className="login-panel">
          <div>
            <p className="eyebrow">NexusHR secure workspace</p>
            <h1>Sign in to workforce operations</h1>
            <p className="hero-copy">Use a demo account to access HR operations, payroll, performance, notifications, and audit data.</p>
          </div>
          <form onSubmit={handleLogin} className="login-form">
            <label>
              Email
              <input name="email" type="email" defaultValue="admin@nexushr.io" />
            </label>
            <label>
              Password
              <input name="password" type="password" defaultValue="password123" />
            </label>
            {error && <p className="form-error">{error}</p>}
            <button type="submit" disabled={loading}>{loading ? "Signing in..." : "Sign in"}</button>
          </form>
        </section>
      </main>
    );
  }

  return (
    <main className="page-shell">
      <header className="app-header">
        <div>
          <p className="eyebrow">NexusHR operations console</p>
          <h1>Workforce command center</h1>
        </div>
        <div className="session-chip">
          <span>{session.displayName}</span>
          <strong>{session.role}</strong>
          <button
            type="button"
            onClick={() => {
              localStorage.removeItem("nexushr-session");
              setSession(null);
              setData(emptyData);
            }}
          >
            Sign out
          </button>
        </div>
      </header>

      {error && <div className="notice">{error}</div>}

      {data.overview && (
        <section className="metrics-grid">
          <MetricCard label="Workforce Size" value={String(data.overview.totalEmployees)} detail="Employees in database" />
          <MetricCard label="Open Positions" value={String(data.overview.openPositions)} detail="Hiring demand this month" />
          <MetricCard label="Attrition Risk" value={`${data.overview.attritionRiskPercent}%`} detail="AI-style risk scoring" />
          <MetricCard label="Payroll Completion" value={`${data.overview.payrollProcessedPercent}%`} detail="Current payroll state" />
        </section>
      )}

      <section className="operations-bar">
        <div>
          <h2>Payroll run</h2>
          <p>Generate payslips, deductions, notification records, and audit logs.</p>
        </div>
        <input value={cycle} onChange={(event) => setCycle(event.target.value)} aria-label="Payroll cycle" />
        <button type="button" onClick={handleGeneratePayroll} disabled={loading || session.role !== "ADMIN"}>Run payroll</button>
      </section>

      <section className="insights-panel">
        <div className="section-intro">
          <p className="eyebrow">AI insights</p>
          <h2>Predictive workforce signals</h2>
        </div>
        <div className="insight-grid">
          {(data.overview?.highlights ?? data.insights).map((insight) => (
            <article className="insight-card" key={insight.title}>
              <p>{insight.title}</p>
              <h3>{insight.value}</h3>
              <span>{insight.trend}</span>
              <small>{insight.recommendation}</small>
            </article>
          ))}
        </div>
      </section>

      <div className="dashboard-grid">
        <DataTable
          title="Employees"
          columns={["Name", "Role", "Department", "Status", "Engagement"]}
          rows={data.employees}
          renderRow={(employee) => (
            <tr key={employee.id}>
              <td>{employee.name}</td>
              <td>{employee.role}</td>
              <td>{employee.department}</td>
              <td>{employee.status}</td>
              <td>{employee.engagementScore}</td>
            </tr>
          )}
        />

        <DataTable
          title="Leave Approvals"
          columns={["Employee", "Type", "Dates", "Status", "Action"]}
          rows={data.leaves}
          renderRow={(leave) => (
            <tr key={leave.id}>
              <td>{leave.employeeName}</td>
              <td>{leave.leaveType}</td>
              <td>{leave.startDate} to {leave.endDate}</td>
              <td>{leave.status}</td>
              <td className="row-actions">
                <button type="button" onClick={() => handleLeaveDecision(leave.id, "Approved")}>Approve</button>
                <button type="button" onClick={() => handleLeaveDecision(leave.id, "Rejected")}>Reject</button>
              </td>
            </tr>
          )}
        />

        <DataTable
          title="Attendance"
          columns={["Employee", "Date", "Status", "Source"]}
          rows={data.attendance}
          renderRow={(record) => (
            <tr key={record.id}>
              <td>{record.employeeName}</td>
              <td>{record.attendanceDate}</td>
              <td>{record.status}</td>
              <td>{record.source}</td>
            </tr>
          )}
        />

        <DataTable
          title="Payroll History"
          columns={["Cycle", "Gross", "Net", "Employees", "Status"]}
          rows={data.payroll}
          renderRow={(run) => (
            <tr key={run.id}>
              <td>{run.cycle}</td>
              <td>{Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(run.grossPayroll)}</td>
              <td>{Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(run.netPayroll)}</td>
              <td>{run.processedEmployees}</td>
              <td>{run.status}</td>
            </tr>
          )}
        />

        <DataTable
          title="Performance Reviews"
          columns={["Employee", "Period", "Manager", "Goals", "Band"]}
          rows={data.reviews}
          renderRow={(review) => (
            <tr key={review.id}>
              <td>{review.employeeName}</td>
              <td>{review.reviewPeriod}</td>
              <td>{review.managerScore}/5</td>
              <td>{review.goalCompletion}%</td>
              <td>{review.performanceBand}</td>
            </tr>
          )}
        />

        <DataTable
          title="Notifications"
          columns={["Channel", "Recipient", "Subject", "Status"]}
          rows={data.notifications}
          renderRow={(notification) => (
            <tr key={notification.id}>
              <td>{notification.channel}</td>
              <td>{notification.recipient}</td>
              <td>{notification.subject}</td>
              <td>{notification.status}</td>
            </tr>
          )}
        />

        <DataTable
          title="Audit Logs"
          columns={["Actor", "Action", "Entity", "When"]}
          rows={data.auditLogs}
          renderRow={(log) => (
            <tr key={log.id}>
              <td>{log.actor}</td>
              <td>{log.action}</td>
              <td>{log.entityType} #{log.entityId}</td>
              <td>{log.createdAt}</td>
            </tr>
          )}
        />
      </div>
    </main>
  );
}
