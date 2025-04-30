import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class MatchesService {
  private baseUrl = 'api/matches';

  constructor(private apiService: ApiService) { }

  /**
   * Get all matches for a user
   * @param userId ID of the user
   * @returns Observable of the user's matches
   */
  getAllMatchesForUser(userId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/user/${userId}`);
  }

  /**
   * Get all active matches for a user
   * @param userId ID of the user
   * @returns Observable of the user's active matches
   */
  getActiveMatchesForUser(userId: number): Observable<any> {
    return this.apiService.get(`${this.baseUrl}/user/${userId}/active`);
  }

  /**
   * Check if two users have a match
   * @param user1Id ID of the first user
   * @param user2Id ID of the second user
   * @returns Observable of the match if it exists
   */
  getMatchBetweenUsers(user1Id: number, user2Id: number): Observable<any> {
    const params = new HttpParams()
      .set('user1Id', user1Id.toString())
      .set('user2Id', user2Id.toString());

    return this.apiService.get(`${this.baseUrl}/between`, { params });
  }

  /**
   * Create a match between two users
   * @param user1Id ID of the first user
   * @param user2Id ID of the second user
   * @returns Observable of the created match
   */
  createMatch(user1Id: number, user2Id: number): Observable<any> {
    const params = new HttpParams()
      .set('user1Id', user1Id.toString())
      .set('user2Id', user2Id.toString());

    return this.apiService.post(this.baseUrl, null, { params });
  }

  /**
   * Update the status of a match
   * @param matchId ID of the match
   * @param status New status ('ACTIVE', 'INACTIVE', or 'EXPIRED')
   * @returns Observable of the updated match
   */
  updateMatchStatus(matchId: number, status: string): Observable<any> {
    const params = new HttpParams()
      .set('status', status);

    return this.apiService.put(`${this.baseUrl}/${matchId}/status`, null, { params });
  }

  /**
   * Unmatch two users
   * @param user1Id ID of the first user
   * @param user2Id ID of the second user
   * @returns Observable of the response
   */
  unmatchUsers(user1Id: number, user2Id: number): Observable<any> {
    const params = new HttpParams()
      .set('user1Id', user1Id.toString())
      .set('user2Id', user2Id.toString());

    return this.apiService.put(`${this.baseUrl}/unmatch`, null, { params });
  }

  /**
   * Mark notification as sent for a user in a match
   * @param matchId ID of the match
   * @param userId ID of the user
   * @returns Observable of the updated match
   */
  markNotificationSent(matchId: number, userId: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId.toString());

    return this.apiService.put(`${this.baseUrl}/${matchId}/notification`, null, { params });
  }
}
