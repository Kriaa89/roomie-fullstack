import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { RoleSelectionComponent } from './components/role-selection/role-selection.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LandingComponent } from './components/landing/landing.component';
import { PropertyDetailComponent } from './components/property/property-detail/property-detail.component';
import { PropertyFormComponent } from './components/property/property-form/property-form.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'select-role', component: RoleSelectionComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'properties/create', component: PropertyFormComponent },
  { path: 'properties/edit/:id', component: PropertyFormComponent },
  { path: 'properties/:id', component: PropertyDetailComponent },
  // Add more routes as needed
  { path: '**', redirectTo: '/' } // Redirect to landing page for any unknown routes
];
