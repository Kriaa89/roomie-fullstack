export interface Property {
  id?: number;
  name: string;
  type: PropertyType;
  location: string;
  price: string;
  description: string;
  images?: string;
  amenities?: string;
  surface?: string;
  numberOfRooms?: number;
  numberOfBathrooms?: number;
  numberOfBedrooms?: number;
  propertyRules?: string;
  availability: boolean;
  audiance?: string;
  createdAt?: Date;
  updatedAt?: Date;
  owner?: {
    id: number;
    firstName: string;
    lastName: string;
  };
}

export enum PropertyType {
  APARTMENT = 'APARTMENT',
  HOUSE = 'HOUSE',
  CONDO = 'CONDO',
  TOWNHOUSE = 'TOWNHOUSE',
  OTHER = 'OTHER'
}

export interface PropertyCreateRequest {
  name: string;
  type: PropertyType;
  location: string;
  price: string;
  description: string;
  images?: string;
  amenities?: string;
  surface?: string;
  numberOfRooms?: number;
  numberOfBathrooms?: number;
  numberOfBedrooms?: number;
  propertyRules?: string;
  availability: boolean;
  audiance?: string;
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
  audiance?: string;
}
