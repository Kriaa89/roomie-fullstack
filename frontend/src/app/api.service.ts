import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// You can keep this for future JSON responses
export interface LandingData {
  // fields here
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private base = 'http://localhost:8080/api/users';
  constructor(private http: HttpClient) { }

  getLanding(): Observable<string> {
    return this.http.get(this.base + '/landing', { responseType: 'text' });
  }
}
