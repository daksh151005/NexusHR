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
} from "./types";

const API_URL = (import.meta.env.VITE_API_URL ?? "http://localhost:8080/api").replace(/\/$/, "");

async function request<T>(path: string, token: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...options.headers
    }
  });

  if (!response.ok) {
    throw new Error(`Request failed for ${path}: ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    throw new Error("Invalid credentials");
  }

  return response.json() as Promise<AuthResponse>;
}

export const getDashboardOverview = (token: string) => request<DashboardOverview>("/dashboard/overview", token);
export const getEmployees = (token: string) => request<EmployeeSummary[]>("/employees", token);
export const getAttendance = (token: string) => request<AttendanceView[]>("/attendance", token);
export const getLeaveRequests = (token: string) => request<LeaveRequestView[]>("/leaves", token);
export const decideLeave = (token: string, id: number, status: string) =>
  request<LeaveRequestView>(`/leaves/${id}/decision`, token, {
    method: "POST",
    body: JSON.stringify({ status, approverComment: `${status} from manager console` })
  });
export const getPayrollRuns = (token: string) => request<PayrollRunView[]>("/payroll", token);
export const generatePayroll = (token: string, cycle: string) =>
  request<PayrollRunView>("/payroll", token, {
    method: "POST",
    body: JSON.stringify({ cycle })
  });
export const getPerformanceReviews = (token: string) => request<PerformanceReviewView[]>("/performance", token);
export const getInsights = (token: string) => request<InsightCard[]>("/insights", token);
export const getNotifications = (token: string) => request<NotificationView[]>("/notifications", token);
export const getAuditLogs = (token: string) => request<AuditLogView[]>("/audit-logs", token);
