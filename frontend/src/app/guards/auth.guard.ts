import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const currentUser = this.authService.currentUserValue;

    if (currentUser) {
      // Check if route has data.role and user has the required role
      if (route.data['role'] && route.data['role'].length) {
        const hasRequiredRole = route.data['role'].includes(currentUser.role);

        if (!hasRequiredRole) {
          // User doesn't have the required role, redirect to home
          this.router.navigate(['/']);
          return false;
        }
      }

      // Authorized, return true
      return true;
    }

    // Not logged in, redirect to login page with return url
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
