import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
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
import { UserProfile } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly API_URL = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  // Renter profile endpoints
  getRenterProfile(userId: number): Observable<RenterProfile> {
    return this.http.get<RenterProfile>(`${this.API_URL}/renter/profile/user/${userId}`);
  }

  createRenterProfile(profileRequest: RenterProfileCreateRequest): Observable<RenterProfile> {
    return this.http.post<RenterProfile>(`${this.API_URL}/renter/profile`, profileRequest);
  }

  updateRenterProfile(userId: number, profileRequest: RenterProfileUpdateRequest): Observable<RenterProfile> {
    return this.http.put<RenterProfile>(`${this.API_URL}/renter/profile/${userId}`, profileRequest);
  }

  // Roommate host profile endpoints
  getRoommateHostProfile(userId: number): Observable<RoommateHostProfile> {
    return this.http.get<RoommateHostProfile>(`${this.API_URL}/roommate-host/profile/user/${userId}`);
  }

  createRoommateHostProfile(profileRequest: RoommateHostProfileCreateRequest): Observable<RoommateHostProfile> {
    return this.http.post<RoommateHostProfile>(`${this.API_URL}/roommate-host/profile`, profileRequest);
  }

  updateRoommateHostProfile(userId: number, profileRequest: RoommateHostProfileUpdateRequest): Observable<RoommateHostProfile> {
    return this.http.put<RoommateHostProfile>(`${this.API_URL}/roommate-host/profile/${userId}`, profileRequest);
  }

  getRoommateHostProfileByUserId(userId: number): Observable<RoommateHostProfile> {
    return this.http.get<RoommateHostProfile>(`${this.API_URL}/roommate-host/profile/user/${userId}`);
  }

  // Owner profile endpoints
  getOwnerProfile(userId: number): Observable<OwnerProfile> {
    return this.http.get<OwnerProfile>(`${this.API_URL}/owner/profile/user/${userId}`);
  }

  createOwnerProfile(profileRequest: OwnerProfileCreateRequest): Observable<OwnerProfile> {
    return this.http.post<OwnerProfile>(`${this.API_URL}/owner/profile`, profileRequest);
  }

  updateOwnerProfile(userId: number, profileRequest: OwnerProfileUpdateRequest): Observable<OwnerProfile> {
    return this.http.put<OwnerProfile>(`${this.API_URL}/owner/profile/${userId}`, profileRequest);
  }

  // Get available roommate hosts for renters to swipe on
  getAvailableRoommateHosts(): Observable<RoommateHostProfile[]> {
    return this.http.get<RoommateHostProfile[]>(`${this.API_URL}/roommate-host/profiles/visible`);
  }

  // Get available renters for hosts to swipe on
  getAvailableRenters(): Observable<RenterProfile[]> {
    return this.http.get<RenterProfile[]>(`${this.API_URL}/renter/profiles/visible`);
  }

  // Generic profile methods for backward compatibility
  getUserProfile(userId: number, role?: string): Observable<UserProfile> {
    // Determine which profile to get based on the user's role
    if (role === 'ROOMMATE_HOST') {
      return this.getRoommateHostProfile(userId) as Observable<UserProfile>;
    } else if (role === 'RENTER') {
      return this.getRenterProfile(userId) as Observable<UserProfile>;
    } else if (role === 'OWNER') {
      return this.getOwnerProfile(userId) as Observable<UserProfile>;
    } else {
      // Try all profile types if role is not specified
      return this.getOwnerProfile(userId) as Observable<UserProfile>;
    }
  }

  updateUserProfile(userId: number, profileData: any, role?: string): Observable<UserProfile> {
    // Determine which profile to update based on the user's role
    if (role === 'ROOMMATE_HOST') {
      return this.updateRoommateHostProfile(userId, profileData) as Observable<UserProfile>;
    } else if (role === 'RENTER') {
      return this.updateRenterProfile(userId, profileData) as Observable<UserProfile>;
    } else if (role === 'OWNER') {
      return this.updateOwnerProfile(userId, profileData) as Observable<UserProfile>;
    } else {
      // Default to owner profile if role is not specified
      return this.updateOwnerProfile(userId, profileData) as Observable<UserProfile>;
    }
  }

  getRoommateProfiles(): Observable<RoommateHostProfile[]> {
    // Alias for getAvailableRoommateHosts
    return this.getAvailableRoommateHosts();
  }
}
