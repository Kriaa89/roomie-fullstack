import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { RoleSelectionComponent } from './user/role-selection/role-selection.component';
import { OwnerDashboardComponent } from './owner/owner-dashboard/owner-dashboard.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Public routes
  { path: '', component: LandingPageComponent },
  { path: 'landing', component: LandingPageComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Protected routes
  { path: 'role-selection', component: RoleSelectionComponent, canActivate: [AuthGuard] },
  { path: 'owner-dashboard', component: OwnerDashboardComponent, canActivate: [AuthGuard] },

  // Redirect dashboard to owner-dashboard for now
  { path: 'dashboard', redirectTo: 'owner-dashboard', pathMatch: 'full' },

  // Fallback route
  { path: '**', redirectTo: '' }
];
