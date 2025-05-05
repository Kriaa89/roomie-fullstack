import { User } from './user.model';

export enum NotificationType {
  MATCH = 'MATCH',
  VISIT_REQUEST = 'VISIT_REQUEST',
  VISIT_RESPONSE = 'VISIT_RESPONSE',
  SYSTEM = 'SYSTEM',
  MESSAGE = 'MESSAGE'
}

export interface Notification {
  id: number;
  recipient: User;
  sender?: User;
  type: NotificationType;
  message: string;
  read: boolean;
  relatedEntityId?: number;
  relatedEntityType?: string;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationUpdateRequest {
  read: boolean;
}
