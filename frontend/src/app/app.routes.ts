import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { RoleSelectionComponent } from './components/role-selection/role-selection.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'select-role', component: RoleSelectionComponent },
  { path: 'dashboard', component: DashboardComponent },
  // Add more routes as needed
  { path: '**', redirectTo: '/login' } // Redirect to login for any unknown routes
];
