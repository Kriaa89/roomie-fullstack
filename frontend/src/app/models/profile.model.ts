import { User } from './user.model';

export enum Skill {
  COOKING = 'COOKING',
  CLEANING = 'CLEANING',
  GARDENING = 'GARDENING',
  MAINTENANCE = 'MAINTENANCE',
  CHILDCARE = 'CHILDCARE',
  PETCARE = 'PETCARE',
  TUTORING = 'TUTORING',
  DRIVING = 'DRIVING',
  SHOPPING = 'SHOPPING',
  OTHER = 'OTHER'
}

export enum LifestylePreference {
  EARLY_RISER = 'EARLY_RISER',
  NIGHT_OWL = 'NIGHT_OWL',
  QUIET = 'QUIET',
  SOCIAL = 'SOCIAL',
  CLEAN = 'CLEAN',
  RELAXED = 'RELAXED',
  VEGETARIAN = 'VEGETARIAN',
  VEGAN = 'VEGAN',
  PET_FRIENDLY = 'PET_FRIENDLY',
  SMOKE_FREE = 'SMOKE_FREE',
  ALCOHOL_FREE = 'ALCOHOL_FREE',
  STUDENT = 'STUDENT',
  PROFESSIONAL = 'PROFESSIONAL',
  REMOTE_WORKER = 'REMOTE_WORKER'
}

export interface BaseProfile {
  id: number;
  bio: string;
  city: string;
  profilePicture: string;
  visibility: boolean;
  createdAt: string;
  updatedAt: string;
  user: User;
}

export interface RenterProfile extends BaseProfile {
  skillsExpected: Skill[];
  lifestylePreferences: LifestylePreference[];
}

export interface RoommateHostProfile extends BaseProfile {
  skillsOffered: Skill[];
  lifestylePreferences: LifestylePreference[];
}

export interface OwnerProfile extends BaseProfile {
  // Minimal fields as per the readme
}

export interface RenterProfileCreateRequest {
  bio: string;
  city: string;
  profilePicture?: string;
  skillsExpected: Skill[];
  lifestylePreferences: LifestylePreference[];
  visibility: boolean;
}

export interface RoommateHostProfileCreateRequest {
  bio: string;
  city: string;
  profilePicture?: string;
  skillsOffered: Skill[];
  lifestylePreferences: LifestylePreference[];
  visibility: boolean;
}

export interface OwnerProfileCreateRequest {
  bio: string;
  city: string;
  profilePicture?: string;
  visibility: boolean;
}

export interface RenterProfileUpdateRequest {
  bio?: string;
  city?: string;
  profilePicture?: string;
  skillsExpected?: Skill[];
  lifestylePreferences?: LifestylePreference[];
  visibility?: boolean;
}

export interface RoommateHostProfileUpdateRequest {
  bio?: string;
  city?: string;
  profilePicture?: string;
  skillsOffered?: Skill[];
  lifestylePreferences?: LifestylePreference[];
  visibility?: boolean;
}

export interface OwnerProfileUpdateRequest {
  bio?: string;
  city?: string;
  profilePicture?: string;
  visibility?: boolean;
}
