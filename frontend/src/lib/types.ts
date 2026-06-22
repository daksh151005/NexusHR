export type InsightCard = {
  title: string;
  value: string;
  trend: string;
  recommendation: string;
};

export type DashboardOverview = {
  totalEmployees: number;
  openPositions: number;
  attritionRiskPercent: number;
  payrollProcessedPercent: number;
  highlights: InsightCard[];
};

export type EmployeeSummary = {
  id: number;
  name: string;
  role: string;
  department: string;
  status: string;
  location: string;
  engagementScore: number;
};

export type LeaveRequestView = {
  id: number;
  employeeName: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  status: string;
};

export type PayrollRunView = {
  id: number;
  cycle: string;
  grossPayroll: number;
  netPayroll: number;
  processedEmployees: number;
  status: string;
};

export type PerformanceReviewView = {
  id: number;
  employeeName: string;
  reviewPeriod: string;
  managerScore: number;
  goalCompletion: number;
  performanceBand: string;
};

export type AuthResponse = {
  accessToken: string;
  role: string;
  displayName: string;
};

export type AttendanceView = {
  id: number;
  employeeName: string;
  attendanceDate: string;
  checkIn: string;
  checkOut: string;
  status: string;
  source: string;
};

export type NotificationView = {
  id: number;
  channel: string;
  recipient: string;
  subject: string;
  body: string;
  status: string;
  createdAt: string;
};

export type AuditLogView = {
  id: number;
  actor: string;
  action: string;
  entityType: string;
  entityId: string;
  createdAt: string;
};
