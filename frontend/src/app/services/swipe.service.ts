import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class SwipeService {
  private baseUrl = 'api/swipes';

  constructor(private http: HttpClient, private apiService: ApiService) { }

  /**
   * Create a user-to-user swipe
   * @param swiperId ID of the user who is swiping
   * @param targetUserId ID of the user being swiped on
   * @param direction Swipe direction ('LEFT' or 'RIGHT')
   * @returns Observable of the created swipe
   */
  createUserSwipe(swiperId: number, targetUserId: number, direction: string): Observable<any> {
    const params = new HttpParams()
      .set('swiperId', swiperId.toString())
      .set('targetUserId', targetUserId.toString())
      .set('direction', direction);

    return this.apiService.post(`${this.baseUrl}/user`, null, { params });
  }

  /**
   * Create a user-to-property swipe
   * @param swiperId ID of the user who is swiping
   * @param propertyId ID of the property being swiped on
   * @param direction Swipe direction ('LEFT' or 'RIGHT')
   * @returns Observable of the created swipe
   */
  createPropertySwipe(swiperId: number, propertyId: number, direction: string): Observable<any> {
    // Convert parameters to ensure they're sent as expected by the backend
    const params = new HttpParams()
      .set('swiperId', swiperId.toString())
      .set('propertyId', propertyId.toString())
      .set('direction', direction);

    // Use the ApiService to ensure consistent API calls
    return this.apiService.post(`${this.baseUrl}/property`, null, { params });
  }

  /**
   * Get all swipes by a user
   * @param userId ID of the user
   * @returns Observable of the user's swipes
   */
  getSwipesByUser(userId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/user/${userId}`);
  }

  /**
   * Get all right swipes by a user
   * @param userId ID of the user
   * @returns Observable of the user's right swipes
   */
  getRightSwipesByUser(userId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/user/${userId}/right`);
  }

  /**
   * Get all swipes on a property
   * @param propertyId ID of the property
   * @returns Observable of the property's swipes
   */
  getSwipesOnProperty(propertyId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/property/${propertyId}`);
  }

  /**
   * Get all right swipes on a property
   * @param propertyId ID of the property
   * @returns Observable of the property's right swipes
   */
  getRightSwipesOnProperty(propertyId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/property/${propertyId}/right`);
  }
}
