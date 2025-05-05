import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  RenterProfile,
  RoommateHostProfile,
  OwnerProfile,
  RenterProfileCreateRequest,
  RoommateHostProfileCreateRequest,
  OwnerProfileCreateRequest,
  RenterProfileUpdateRequest,
  RoommateHostProfileUpdateRequest,
  OwnerProfileUpdateRequest
} from '../models/profile.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly API_URL = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  // Renter profile endpoints
  getRenterProfile(userId: number): Observable<RenterProfile> {
    return this.http.get<RenterProfile>(`${this.API_URL}/renter-profiles/${userId}`);
  }

  createRenterProfile(profileRequest: RenterProfileCreateRequest): Observable<RenterProfile> {
    return this.http.post<RenterProfile>(`${this.API_URL}/renter-profiles`, profileRequest);
  }

  updateRenterProfile(userId: number, profileRequest: RenterProfileUpdateRequest): Observable<RenterProfile> {
    return this.http.put<RenterProfile>(`${this.API_URL}/renter-profiles/${userId}`, profileRequest);
  }

  // Roommate host profile endpoints
  getRoommateHostProfile(userId: number): Observable<RoommateHostProfile> {
    return this.http.get<RoommateHostProfile>(`${this.API_URL}/roommate-host-profiles/${userId}`);
  }

  createRoommateHostProfile(profileRequest: RoommateHostProfileCreateRequest): Observable<RoommateHostProfile> {
    return this.http.post<RoommateHostProfile>(`${this.API_URL}/roommate-host-profiles`, profileRequest);
  }

  updateRoommateHostProfile(userId: number, profileRequest: RoommateHostProfileUpdateRequest): Observable<RoommateHostProfile> {
    return this.http.put<RoommateHostProfile>(`${this.API_URL}/roommate-host-profiles/${userId}`, profileRequest);
  }

  // Owner profile endpoints
  getOwnerProfile(userId: number): Observable<OwnerProfile> {
    return this.http.get<OwnerProfile>(`${this.API_URL}/owner-profiles/${userId}`);
  }

  createOwnerProfile(profileRequest: OwnerProfileCreateRequest): Observable<OwnerProfile> {
    return this.http.post<OwnerProfile>(`${this.API_URL}/owner-profiles`, profileRequest);
  }

  updateOwnerProfile(userId: number, profileRequest: OwnerProfileUpdateRequest): Observable<OwnerProfile> {
    return this.http.put<OwnerProfile>(`${this.API_URL}/owner-profiles/${userId}`, profileRequest);
  }

  // Get available roommate hosts for renters to swipe on
  getAvailableRoommateHosts(): Observable<RoommateHostProfile[]> {
    return this.http.get<RoommateHostProfile[]>(`${this.API_URL}/roommate-host-profiles/available`);
  }

  // Get available renters for hosts to swipe on
  getAvailableRenters(): Observable<RenterProfile[]> {
    return this.http.get<RenterProfile[]>(`${this.API_URL}/renter-profiles/available`);
  }
}
