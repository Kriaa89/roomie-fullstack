import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080'; // Adjust based on your backend URL

  constructor(private http: HttpClient) {}

  // Get HTTP headers with authorization token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Register a new user
  register(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, userData);
  }

  // Login a user
  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  // Assign a role to a user
  assignRole(userId: number, roleType: string): Observable<any> {
    const headers = this.getHeaders();
    return this.http.post(
      `${this.baseUrl}/api/user-roles/user/${userId}`,
      { roleType },
      { headers }
    );
  }

  // Get user roles
  getUserRoles(userId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get(
      `${this.baseUrl}/api/user-roles/user/${userId}`,
      { headers }
    );
  }
}
