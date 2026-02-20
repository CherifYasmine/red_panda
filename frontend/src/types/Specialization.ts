import type { RoomType } from './RoomType';

/**
 * Specialization Type
 * Maps to specializations table in backend
 */
export interface Specialization {
  id: number;
  name: string;
  description: string;
  roomType?: RoomType | null;
  createdAt?: string;
}
