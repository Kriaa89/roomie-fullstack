import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SwipeLike, SwipeLikeRequest, Match } from '../models/swipe.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SwipeService {
  private readonly API_URL = `${environment.apiUrl}/swipes`;
  private readonly MATCHES_URL = `${environment.apiUrl}/matches`;

  constructor(private http: HttpClient) { }

  // Swipe actions
  createSwipe(swipeRequest: SwipeLikeRequest): Observable<SwipeLike> {
    return this.http.post<SwipeLike>(this.API_URL, swipeRequest);
  }

  getSwipesByUser(userId: number): Observable<SwipeLike[]> {
    return this.http.get<SwipeLike[]>(`${this.API_URL}/user/${userId}`);
  }

  // Match related endpoints
  getMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(this.MATCHES_URL);
  }

  getMatchById(matchId: number): Observable<Match> {
    return this.http.get<Match>(`${this.MATCHES_URL}/${matchId}`);
  }

  getMatchesByUser(userId: number): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.MATCHES_URL}/user/${userId}`);
  }

  // Renter specific endpoints
  getRenterMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${environment.apiUrl}/renter/matches`);
  }

  // Host specific endpoints
  getHostMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${environment.apiUrl}/roommate-host/matches`);
  }

  // Owner specific endpoints
  getOwnerMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${environment.apiUrl}/owner/matches`);
  }
}
