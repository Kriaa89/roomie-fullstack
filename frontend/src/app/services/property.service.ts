import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Property, PropertyCreateRequest, PropertyUpdateRequest, PropertyType } from '../models/property.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private readonly API_URL = `${environment.apiUrl}/properties`;
  private readonly OWNER_API_URL = `${environment.apiUrl}/owner/properties`;

  constructor(private http: HttpClient) { }

  // General property endpoints
  getAllProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(this.API_URL);
  }

  getPropertyById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.API_URL}/${id}`);
  }

  getPropertiesByOwner(ownerId: number): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.API_URL}/owner/${ownerId}`);
  }

  getAvailableProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.API_URL}/available`);
  }

  getPropertiesByType(type: PropertyType): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.API_URL}/type/${type}`);
  }

  getPropertiesByLocation(location: string): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.API_URL}/location/${location}`);
  }

  // Owner-specific endpoints
  getMyProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(this.OWNER_API_URL);
  }

  getMyPropertyById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.OWNER_API_URL}/${id}`);
  }

  createProperty(propertyRequest: PropertyCreateRequest): Observable<Property> {
    return this.http.post<Property>(this.OWNER_API_URL, propertyRequest);
  }

  updateProperty(id: number, propertyRequest: PropertyUpdateRequest): Observable<Property> {
    return this.http.put<Property>(`${this.OWNER_API_URL}/${id}`, propertyRequest);
  }

  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.OWNER_API_URL}/${id}`);
  }

  // Renter-specific endpoints
  getAvailablePropertiesForRenter(): Observable<Property[]> {
    return this.http.get<Property[]>(`${environment.apiUrl}/renter/properties`);
  }

  getPropertyByIdForRenter(id: number): Observable<Property> {
    return this.http.get<Property>(`${environment.apiUrl}/renter/properties/${id}`);
  }
}
