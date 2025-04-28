import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define interfaces for API responses
interface LoginResponse {
  token: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/auth';
  private userUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {
      headers: headers
      // Removed withCredentials: true as it might be causing CORS issues
    };

    console.log('Sending registration request to:', `${this.baseUrl}/register`);
    console.log('With data:', data);

    return this.http.post(`${this.baseUrl}/register`, data, options);
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {
      headers: headers
      // Removed withCredentials: true for consistency with register method
    };

    const data = { email, password };

    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, data, options);
  }

  getAvailableRoles(): Observable<string[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<string[]>(`${this.userUrl}/roles`, { headers });
  }

  assignRole(userId: string, role: string): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post(`${this.userUrl}/${userId}/role`, { role }, { headers });
  }
}
