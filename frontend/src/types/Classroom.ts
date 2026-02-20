import type { RoomType } from './RoomType';

/**
 * Classroom Entity
 * Maps to classrooms table in backend
 */
export interface Classroom {
  id: number;
  name: string;
  roomType: RoomType;
  capacity: number;
  equipment?: string;
  floor?: number;
  createdAt: string;
}
