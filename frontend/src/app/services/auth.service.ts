import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  // Check if token exists in local storage
  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  // Load user data from local storage if available
  private loadUserFromStorage(): void {
    const userData = localStorage.getItem('user');
    if (userData) {
      try {
        const user = JSON.parse(userData);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
      } catch (error) {
        console.error('Error parsing user data from localStorage', error);
        this.logout();
      }
    }
  }

  // Register a new user
  register(userData: any): Observable<any> {
    return this.apiService.register(userData).pipe(
      tap(response => {
        this.setSession(response);
      })
    );
  }

  // Login user
  login(credentials: any): Observable<any> {
    return this.apiService.login(credentials).pipe(
      tap(response => {
        this.setSession(response);
      })
    );
  }

  // Select a role for the user
  selectRole(userId: number, roleType: string): Observable<any> {
    return this.apiService.selectRole(userId, roleType).pipe(
      tap(response => {
        this.setSession(response);
      })
    );
  }

  // Set session data after successful authentication
  private setSession(authResult: any): void {
    localStorage.setItem('token', authResult.token);

    console.log('Auth result:', authResult);
    console.log('Auth result roles:', authResult.roles);

    const user = {
      id: authResult.userId,
      email: authResult.email,
      firstName: authResult.firstName,
      lastName: authResult.lastName,
      roles: authResult.roles
    };

    console.log('User object to be stored:', user);
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }

  // Logout user
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  // Get current user
  getCurrentUser(): Observable<any> {
    return this.apiService.getCurrentUser().pipe(
      tap(user => {
        console.log('getCurrentUser response:', user);
        const currentUser = this.currentUserSubject.value;
        console.log('Current user before update:', currentUser);

        if (currentUser) {
          console.log('User roles from API:', user.roles);

          // Check if roles is an array of objects or strings
          let mappedRoles;
          if (user.roles && user.roles.length > 0 && typeof user.roles[0] === 'object') {
            console.log('Mapping roles from objects to strings');
            mappedRoles = user.roles.map((role: any) => role.roleType);
          } else {
            console.log('Roles are already strings');
            mappedRoles = user.roles;
          }

          console.log('Mapped roles:', mappedRoles);

          // Update only user data, not auth info
          const updatedUser = {
            ...currentUser,
            firstName: user.firstName,
            lastName: user.lastName,
            roles: mappedRoles
          };

          console.log('Updated user:', updatedUser);
          this.currentUserSubject.next(updatedUser);
          localStorage.setItem('user', JSON.stringify(updatedUser));
        }
      })
    );
  }

  // Check if user has a specific role
  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    console.log('Checking if user has role:', role);
    console.log('User:', user);
    if (user && user.roles) {
      console.log('User roles:', user.roles);
      const hasRole = user.roles.includes(role);
      console.log(`User has role ${role}: ${hasRole}`);
      return hasRole;
    }
    console.log('User has no roles');
    return false;
  }

  // Update current user with new data
  updateCurrentUser(userData: any): void {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      // Preserve roles from current user if not provided in userData
      if (!userData.roles && currentUser.roles) {
        userData.roles = currentUser.roles;
      }

      // Update the current user
      this.currentUserSubject.next(userData);
      localStorage.setItem('user', JSON.stringify(userData));
    }
  }
}
