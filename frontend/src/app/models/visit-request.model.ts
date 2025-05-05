import { User } from './user.model';
import { Property } from './property.model';
import { RoommateHostProfile } from './profile.model';

export enum VisitStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export interface VisitRequest {
  id: number;
  requester: User;
  property?: Property;
  roommateHost?: RoommateHostProfile;
  requestDate: string;
  requestTime: string;
  status: VisitStatus;
  message?: string;
  responseMessage?: string;
  createdAt: string;
  updatedAt: string;
}

export interface VisitRequestCreateRequest {
  propertyId?: number;
  roommateHostId?: number;
  requestDate: string;
  requestTime: string;
  message?: string;
}

export interface VisitRequestUpdateRequest {
  status: VisitStatus;
  responseMessage?: string;
}
