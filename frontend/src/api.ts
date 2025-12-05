import { Service } from './types';

const API_BASE_URL = 'http://localhost:8000';

export async function fetchServices(): Promise<Service[]> {
  const response = await fetch(`${API_BASE_URL}/api/services`);
  if (!response.ok) {
    throw new Error('Failed to fetch services');
  }
  return response.json();
}

export async function generateTests(serviceId: number): Promise<Service> {
  const response = await fetch(`${API_BASE_URL}/api/services/${serviceId}/generate_tests`, {
    method: 'POST',
  });
  if (!response.ok) {
    throw new Error('Failed to generate tests');
  }
  return response.json();
}

export async function markComplete(serviceId: number): Promise<Service> {
  const response = await fetch(`${API_BASE_URL}/api/services/${serviceId}/mark_complete`, {
    method: 'POST',
  });
  if (!response.ok) {
    throw new Error('Failed to mark service as complete');
  }
  return response.json();
}

