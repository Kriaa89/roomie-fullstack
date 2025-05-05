import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VisitRequest, VisitRequestCreateRequest, VisitRequestUpdateRequest } from '../models/visit-request.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VisitRequestService {
  private readonly API_URL = `${environment.apiUrl}/visit-requests`;

  constructor(private http: HttpClient) { }

  // General visit request endpoints
  getAllVisitRequests(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(this.API_URL);
  }

  getVisitRequestById(id: number): Observable<VisitRequest> {
    return this.http.get<VisitRequest>(`${this.API_URL}/${id}`);
  }

  createVisitRequest(visitRequest: VisitRequestCreateRequest): Observable<VisitRequest> {
    return this.http.post<VisitRequest>(this.API_URL, visitRequest);
  }

  updateVisitRequest(id: number, visitRequest: VisitRequestUpdateRequest): Observable<VisitRequest> {
    return this.http.put<VisitRequest>(`${this.API_URL}/${id}`, visitRequest);
  }

  cancelVisitRequest(id: number): Observable<VisitRequest> {
    return this.http.put<VisitRequest>(`${this.API_URL}/${id}/cancel`, {});
  }

  // Requester (Renter) specific endpoints
  getMyRequestedVisits(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(`${this.API_URL}/my-requests`);
  }

  // Host/Owner specific endpoints
  getVisitRequestsForMyProperties(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(`${this.API_URL}/my-properties`);
  }

  // Roommate host specific endpoints
  getVisitRequestsForRoommateHost(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(`${environment.apiUrl}/roommate-host/visit-requests`);
  }

  // Property owner specific endpoints
  getVisitRequestsForOwner(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(`${environment.apiUrl}/owner/visit-requests`);
  }

  // Renter specific endpoints
  getVisitRequestsForRenter(): Observable<VisitRequest[]> {
    return this.http.get<VisitRequest[]>(`${environment.apiUrl}/renter/visit-requests`);
  }
}
