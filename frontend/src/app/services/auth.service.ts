import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { Role } from '../models/role.model';
import { environment } from '../../environments/environment';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_ID_KEY = 'user_id';
  private readonly USER_ROLE_KEY = 'user_role';

  private currentUserSubject: BehaviorSubject<AuthResponse | null>;
  public currentUser: Observable<AuthResponse | null>;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    let currentUser = null;

    if (this.isBrowser()) {
      const storedToken = localStorage.getItem(this.TOKEN_KEY);
      const storedUserId = localStorage.getItem(this.USER_ID_KEY);
      const storedRole = localStorage.getItem(this.USER_ROLE_KEY);

      if (storedToken && storedUserId && storedRole) {
        currentUser = {
          token: storedToken,
          userId: parseInt(storedUserId, 10),
          role: storedRole as Role
        };
      }
    }

    this.currentUserSubject = new BehaviorSubject<AuthResponse | null>(currentUser);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  public get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  public get isLoggedIn(): boolean {
    const user = this.currentUserValue;
    if (!user) {
      return false;
    }

    // Check if token is expired
    if (this.isTokenExpired(user.token)) {
      this.logout();
      return false;
    }

    return true;
  }

  private isTokenExpired(token: string): boolean {
    try {
      const decoded: any = jwtDecode(token);
      if (!decoded.exp) {
        return false;
      }

      // exp is in seconds, Date.now() is in milliseconds
      const expirationDate = new Date(decoded.exp * 1000);
      return expirationDate < new Date();
    } catch (error) {
      console.error('Error decoding token:', error);
      return true; // If there's an error decoding, consider the token expired
    }
  }

  public get hasRole(): (role: Role) => boolean {
    return (role: Role) => {
      if (!this.isLoggedIn) {
        return false;
      }

      const user = this.currentUserValue;
      return !!user && user.role === role;
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


  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_ID_KEY);
      localStorage.removeItem(this.USER_ROLE_KEY);
    }
    this.currentUserSubject.next(null);
  }

  private setSession(authResult: AuthResponse): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.TOKEN_KEY, authResult.token);
      localStorage.setItem(this.USER_ID_KEY, authResult.userId.toString());
      localStorage.setItem(this.USER_ROLE_KEY, authResult.role);
    }
    this.currentUserSubject.next(authResult);

    // Set up token refresh
    this.setupTokenRefresh(authResult.token);
  }

  private setupTokenRefresh(token: string): void {
    try {
      const decoded: any = jwtDecode(token);
      if (!decoded.exp) {
        return;
      }

      // Calculate time until token expires (in milliseconds)
      const expirationTime = decoded.exp * 1000; // Convert to milliseconds
      const currentTime = Date.now();
      const timeUntilExpiration = expirationTime - currentTime;

      // Refresh token 5 minutes before it expires
      const refreshTime = timeUntilExpiration - (5 * 60 * 1000);

      if (refreshTime > 0) {
        setTimeout(() => {
          // If user is still logged in, refresh the token
          if (this.currentUserValue) {
            console.log('Token is about to expire. Refreshing...');
            // You would typically call a refresh token endpoint here
            // For now, we'll just log the user out if no refresh token endpoint exists
            this.logout();
            window.location.href = '/login';
          }
        }, refreshTime);
      }
    } catch (error) {
      console.error('Error setting up token refresh:', error);
    }
  }
}
