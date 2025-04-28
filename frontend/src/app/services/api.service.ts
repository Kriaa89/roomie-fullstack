import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
//import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080'; // Default API URL

  constructor(private http: HttpClient) {}

  // Set the authorization token for requests
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  // Authentication methods
  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, credentials);
  }

  selectRole(userId: number, roleType: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/auth/select-role/${userId}`,
      { roleType },
      { headers: this.getHeaders() }
    );
  }

  // User methods
  getCurrentUser(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/users/me`,
      { headers: this.getHeaders() }
    );
  }

  updateUser(userId: number, userData: any): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/api/users/${userId}`,
      userData,
      { headers: this.getHeaders() }
    );
  }

  changePassword(userId: number, passwordData: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/users/${userId}/change-password`,
      passwordData,
      { headers: this.getHeaders() }
    );
  }

  // Property methods
  getAllProperties(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/properties`,
      { headers: this.getHeaders() }
    );
  }

  getPropertyById(propertyId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/properties/${propertyId}`,
      { headers: this.getHeaders() }
    );
  }

  getPropertiesByOwner(ownerId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/properties/owner/${ownerId}`,
      { headers: this.getHeaders() }
    );
  }

  getAvailableProperties(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/properties/available`,
      { headers: this.getHeaders() }
    );
  }

  createProperty(propertyData: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/properties`,
      propertyData,
      { headers: this.getHeaders() }
    );
  }

  updateProperty(propertyId: number, propertyData: any): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/api/properties/${propertyId}`,
      propertyData,
      { headers: this.getHeaders() }
    );
  }

  deleteProperty(propertyId: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/api/properties/${propertyId}`,
      { headers: this.getHeaders() }
    );
  }

  // Owner specific methods
  getMyProperties(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/owner/properties`,
      { headers: this.getHeaders() }
    );
  }

  // Admin specific methods
  getAllUsers(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/admin/users`,
      { headers: this.getHeaders() }
    );
  }

  getAdminStats(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/admin/stats`,
      { headers: this.getHeaders() }
    );
  }
}
