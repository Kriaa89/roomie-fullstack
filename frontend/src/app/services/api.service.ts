import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Generic HTTP methods
  get(url: string, options: any = {}): Observable<any> {
    options.headers = options.headers || this.getHeaders();
    return this.http.get(`${this.apiUrl}/${url}`, options);
  }

  post(url: string, body: any = null, options: any = {}): Observable<any> {
    options.headers = options.headers || this.getHeaders();
    return this.http.post(`${this.apiUrl}/${url}`, body, options);
  }

  put(url: string, body: any, options: any = {}): Observable<any> {
    options.headers = options.headers || this.getHeaders();
    return this.http.put(`${this.apiUrl}/${url}`, body, options);
  }

  delete(url: string, options: any = {}): Observable<any> {
    options.headers = options.headers || this.getHeaders();
    return this.http.delete(`${this.apiUrl}/${url}`, options);
  }

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

  togglePropertyAvailability(propertyId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/api/properties/${propertyId}/toggle-availability`,
      {},
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

  // Visit request methods
  createVisitRequest(propertyId: number, visitRequestData: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/visit-requests/property/${propertyId}`,
      visitRequestData,
      { headers: this.getHeaders() }
    );
  }

  getMyVisitRequests(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/visit-requests/my-requests`,
      { headers: this.getHeaders() }
    );
  }

  getPropertyVisitRequests(propertyId: number): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/visit-requests/property/${propertyId}`,
      { headers: this.getHeaders() }
    );
  }

  updateVisitRequestStatus(requestId: number, status: string): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/api/visit-requests/${requestId}/status?status=${status}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  // Renter specific methods
  updateRenterProfile(userData: any): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/api/renter/profile`,
      userData,
      { headers: this.getHeaders() }
    );
  }

  submitRentalRequest(propertyId: number, requestDetails: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/renter/properties/${propertyId}/request`,
      requestDetails,
      { headers: this.getHeaders() }
    );
  }

  // Roommate host specific methods
  createRoomListing(roomData: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/api/roommate-host/rooms`,
      roomData,
      { headers: this.getHeaders() }
    );
  }

  getRoomListings(): Observable<any> {
    return this.http.get(
      `${this.apiUrl}/api/roommate-host/rooms`,
      { headers: this.getHeaders() }
    );
  }
}
