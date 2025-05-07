import { Role } from './role.model';
import { RenterProfile, RoommateHostProfile, OwnerProfile } from './profile.model';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  profilePicture?: string;
  location: string;
  age: number;
  idVerified: boolean;
  createdAt: string;
  updatedAt: string;
  roles: Role[];
}

// Alias for Role enum for backward compatibility
export import UserRole = Role;

// Union type of all profile types for backward compatibility
export type UserProfile = RenterProfile | RoommateHostProfile | OwnerProfile;

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
