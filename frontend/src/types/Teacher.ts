import type { Specialization } from './Specialization';

/**
 * Teacher Type
 * Maps to teachers table in backend
 */
export interface Teacher {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  specialization: Specialization;
  maxDailyHours?: number;
  createdAt?: string;
}
