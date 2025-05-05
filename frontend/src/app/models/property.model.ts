export enum PropertyType {
  APARTMENT = 'APARTMENT',
  HOUSE = 'HOUSE',
  CONDO = 'CONDO',
  STUDIO = 'STUDIO',
  OTHER = 'OTHER'
}

export enum AudienceType {
  SINGLES = 'SINGLES',
  COUPLES = 'COUPLES',
  FAMILIES = 'FAMILIES',
  STUDENTS = 'STUDENTS',
  PROFESSIONALS = 'PROFESSIONALS',
  SENIORS = 'SENIORS',
  ANY = 'ANY'
}

export interface PropertyOwner {
  id: number;
  firstName: string;
  lastName: string;
}

export interface Property {
  id: number;
  name: string;
  type: PropertyType;
  location: string;
  price: string;
  description: string;
  images: string; // Comma-separated list of image URLs
  amenities: string; // Comma-separated list of amenities
  surface: string;
  numberOfRooms: number;
  numberOfBathrooms: number;
  numberOfBedrooms: number;
  propertyRules: string;
  availability: boolean;
  audiance: AudienceType;
  createdAt: string;
  updatedAt: string;
  owner: PropertyOwner;
}

export interface PropertyCreateRequest {
  name: string;
  type: PropertyType;
  location: string;
  price: string;
  description: string;
  images: string;
  amenities: string;
  surface: string;
  numberOfRooms: number;
  numberOfBathrooms: number;
  numberOfBedrooms: number;
  propertyRules: string;
  availability: boolean;
  audiance: AudienceType;
}

export interface PropertyUpdateRequest {
  name?: string;
  type?: PropertyType;
  location?: string;
  price?: string;
  description?: string;
  images?: string;
  amenities?: string;
  surface?: string;
  numberOfRooms?: number;
  numberOfBathrooms?: number;
  numberOfBedrooms?: number;
  propertyRules?: string;
  availability?: boolean;
  audiance?: AudienceType;
}
