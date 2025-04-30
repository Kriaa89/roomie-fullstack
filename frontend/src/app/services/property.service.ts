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

  // Toggle property availability
  toggleAvailability(propertyId: number): Observable<any> {
    return this.apiService.togglePropertyAvailability(propertyId);
  }

  // Get my properties (for owners)
  getMyProperties(): Observable<any> {
    return this.apiService.getMyProperties();
  }

  // Visit request methods
  requestVisit(propertyId: number, visitRequestData: any): Observable<any> {
    return this.apiService.createVisitRequest(propertyId, visitRequestData);
  }

  getMyVisitRequests(): Observable<any> {
    return this.apiService.getMyVisitRequests();
  }

  getPropertyVisitRequests(propertyId: number): Observable<any> {
    return this.apiService.getPropertyVisitRequests(propertyId);
  }

  updateVisitRequestStatus(requestId: number, status: string): Observable<any> {
    return this.apiService.updateVisitRequestStatus(requestId, status);
  }

  // Express interest in a property
  expressInterest(propertyId: number): Observable<any> {
    return this.apiService.submitRentalRequest(propertyId, { requestType: 'interest' });
  }

  // Schedule a visit to a property
  scheduleVisit(propertyId: number, visitDate: string): Observable<any> {
    return this.apiService.submitRentalRequest(propertyId, {
      requestType: 'visit',
      visitDate: visitDate
    });
  }

  // Roommate host specific methods
  createRoomListing(roomData: any): Observable<any> {
    return this.apiService.createRoomListing(roomData);
  }

  getRoomListings(): Observable<any> {
    return this.apiService.getRoomListings();
  }
}
