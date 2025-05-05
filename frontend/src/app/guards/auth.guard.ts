import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

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
      // Check if route has data.roles and user has one of the required roles
      if (route.data['roles'] && route.data['roles'].length) {
        const hasRequiredRole = route.data['roles'].some((role: string) =>
          currentUser.roles.includes(role)
        );

        if (!hasRequiredRole) {
          // User doesn't have any of the required roles, redirect to home
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
