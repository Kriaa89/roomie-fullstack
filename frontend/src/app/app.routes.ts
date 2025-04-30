import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { RoleSelectionComponent } from './components/role-selection/role-selection.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { OwnerDashboardComponent } from './components/dashboard/owner-dashboard/owner-dashboard.component';
import { RenterDashboardComponent } from './components/dashboard/renter-dashboard/renter-dashboard.component';
import { RoommateHostDashboardComponent } from './components/dashboard/roommate-host-dashboard/roommate-host-dashboard.component';
import { LandingComponent } from './components/landing/landing.component';
import { PropertyDetailComponent } from './components/property/property-detail/property-detail.component';
import { PropertyFormComponent } from './components/property/property-form/property-form.component';
import { PropertySwipeComponent } from './components/property/property-swipe/property-swipe.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'select-role', component: RoleSelectionComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'owner-dashboard', component: OwnerDashboardComponent },
  { path: 'renter-dashboard', component: RenterDashboardComponent },
  { path: 'roommate-host-dashboard', component: RoommateHostDashboardComponent },

  // Property routes
  { path: 'properties/create', component: PropertyFormComponent },
  { path: 'properties/edit/:id', component: PropertyFormComponent },
  { path: 'properties/available', component: PropertySwipeComponent }, // Now using the swipe component
  { path: 'properties/swipe', component: PropertySwipeComponent }, // Dedicated route for swiping
  { path: 'properties/:id', component: PropertyDetailComponent },
  { path: 'my-properties', component: OwnerDashboardComponent }, // Temporary redirect to owner dashboard

  // Additional routes mentioned in dashboard components
  { path: 'roommates', component: RoommateHostDashboardComponent }, // Temporary redirect to roommate host dashboard
  { path: 'profile', component: DashboardComponent }, // Temporary redirect to dashboard
  { path: 'browse-renters', component: OwnerDashboardComponent }, // Temporary redirect to owner dashboard
  { path: 'matches', component: DashboardComponent }, // Temporary redirect to dashboard
  { path: 'messages', component: DashboardComponent }, // Temporary redirect to dashboard
  { path: 'calendar', component: OwnerDashboardComponent }, // Temporary redirect to owner dashboard
  { path: 'analytics', component: OwnerDashboardComponent }, // Temporary redirect to owner dashboard
  { path: 'appointments', component: RenterDashboardComponent }, // Temporary redirect to renter dashboard
  { path: 'room-listings', component: RoommateHostDashboardComponent }, // Temporary redirect to roommate host dashboard
  { path: 'visits', component: RoommateHostDashboardComponent }, // Temporary redirect to roommate host dashboard

  // Catch-all route
  { path: '**', redirectTo: '/' } // Redirect to landing page for any unknown routes
];
