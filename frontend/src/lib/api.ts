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

const configuredApiUrl = import.meta.env.VITE_API_URL?.trim();
const API_URL = configuredApiUrl || "http://localhost:8080/api";

function isRemoteFrontendUsingLocalApi(): boolean {
  if (typeof window === "undefined") {
    return false;
  }

  return window.location.hostname !== "localhost" && window.location.hostname !== "127.0.0.1" && API_URL.includes("localhost");
}

function describeApiFailure(defaultMessage: string): string {
  if (isRemoteFrontendUsingLocalApi()) {
    return "This deployment is still pointing to localhost. Set VITE_API_URL in Vercel to your public backend URL and redeploy.";
  }

  if (!configuredApiUrl) {
    return "The backend API URL is missing for this build. Set VITE_API_URL and redeploy.";
  }

  return `${defaultMessage} (${API_URL})`;
}

async function request<T>(path: string, token: string, options: RequestInit = {}): Promise<T> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        ...options.headers
      }
    });
  } catch {
    throw new Error(describeApiFailure("Could not reach the backend API"));
  }

  if (!response.ok) {
    throw new Error(`Request failed for ${path}: ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  let response: Response;

  try {
    response = await fetch(`${API_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email, password })
    });
  } catch {
    throw new Error(describeApiFailure("Could not reach the backend API"));
  }

  if (!response.ok) {
    if (response.status !== 401) {
      throw new Error(`Login failed: ${response.status}`);
    }

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
