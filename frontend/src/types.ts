export type ServiceStatus = "healthy" | "at-risk" | "ip";
export type DeprecationRisk = "low" | "medium" | "high";

export interface Service {
  id: number;
  name: string;
  team: string;
  tech_stack: string;
  coverage: number;
  goal: number;
  last_updated: string;
  status: ServiceStatus;
  deprecation_risk: DeprecationRisk;
}

