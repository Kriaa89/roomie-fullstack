import { Role } from './role.model';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  profilePicture?: string;
  location: string;
  age: number;
  emailVerified: boolean;
  phoneVerified: boolean;
  idVerified: boolean;
  createdAt: string;
  updatedAt: string;
  roles: Role[];
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  profilePicture?: string;
  location?: string;
  age?: number;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
