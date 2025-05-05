import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserUpdateRequest, PasswordChangeRequest } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly API_URL = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/me`);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/${id}`);
  }

  updateUser(id: number, updateRequest: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.API_URL}/${id}`, updateRequest);
  }

  changePassword(id: number, passwordRequest: PasswordChangeRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/${id}/change-password`, passwordRequest);
  }
}
