import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { Role, RoleRequest } from '../models/role.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_ID_KEY = 'user_id';
  private readonly USER_ROLES_KEY = 'user_roles';

  private currentUserSubject: BehaviorSubject<AuthResponse | null>;
  public currentUser: Observable<AuthResponse | null>;

  constructor(private http: HttpClient) {
    const storedToken = localStorage.getItem(this.TOKEN_KEY);
    const storedUserId = localStorage.getItem(this.USER_ID_KEY);
    const storedRoles = localStorage.getItem(this.USER_ROLES_KEY);

    let currentUser = null;
    if (storedToken && storedUserId && storedRoles) {
      currentUser = {
        token: storedToken,
        userId: parseInt(storedUserId, 10),
        roles: JSON.parse(storedRoles) as Role[]
      };
    }

    this.currentUserSubject = new BehaviorSubject<AuthResponse | null>(currentUser);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  public get isLoggedIn(): boolean {
    return !!this.currentUserValue;
  }

  public get hasRole(): (role: Role) => boolean {
    return (role: Role) => {
      const user = this.currentUserValue;
      return !!user && user.roles.includes(role);
    };
  }

  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, registerRequest)
      .pipe(
        tap(response => this.setSession(response))
      );
  }

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, loginRequest)
      .pipe(
        tap(response => this.setSession(response))
      );
  }

  selectRole(userId: number, roleRequest: RoleRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/select-role/${userId}`, roleRequest)
      .pipe(
        tap(response => this.setSession(response))
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.USER_ROLES_KEY);
    this.currentUserSubject.next(null);
  }

  private setSession(authResult: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, authResult.token);
    localStorage.setItem(this.USER_ID_KEY, authResult.userId.toString());
    localStorage.setItem(this.USER_ROLES_KEY, JSON.stringify(authResult.roles));
    this.currentUserSubject.next(authResult);
  }
}
