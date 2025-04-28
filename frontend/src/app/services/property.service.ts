import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  constructor(private apiService: ApiService) {}

  // Get all properties
  getAllProperties(): Observable<any> {
    return this.apiService.getAllProperties();
  }

  // Get property by ID
  getPropertyById(propertyId: number): Observable<any> {
    return this.apiService.getPropertyById(propertyId);
  }

  // Get properties by owner
  getPropertiesByOwner(ownerId: number): Observable<any> {
    return this.apiService.getPropertiesByOwner(ownerId);
  }

  // Get available properties
  getAvailableProperties(): Observable<any> {
    return this.apiService.getAvailableProperties();
  }

  // Create a new property
  createProperty(propertyData: any): Observable<any> {
    return this.apiService.createProperty(propertyData);
  }

  // Update an existing property
  updateProperty(propertyId: number, propertyData: any): Observable<any> {
    return this.apiService.updateProperty(propertyId, propertyData);
  }

  // Delete a property
  deleteProperty(propertyId: number): Observable<any> {
    return this.apiService.deleteProperty(propertyId);
  }

  // Get my properties (for owners)
  getMyProperties(): Observable<any> {
    return this.apiService.getMyProperties();
  }
}
