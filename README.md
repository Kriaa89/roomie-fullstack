# Roomie - User Authentication and Role Selection Guide

This guide explains how to handle user authentication (registration and login) and role selection in the Roomie application. It's designed for developers who are new to Angular and need to understand how the frontend interacts with the backend for these core functionalities.

## Table of Contents
1. [Overview](#overview)
2. [Landing Page](#landing-page)
3. [User Registration](#user-registration)
4. [User Login](#user-login)
5. [Role Selection](#role-selection)
6. [Authentication Flow](#authentication-flow)
7. [API Endpoints](#api-endpoints)

## Overview

Roomie is a full-stack application with:
- **Backend**: Spring Boot Java application providing REST APIs
- **Frontend**: Angular application consuming these APIs

The authentication flow consists of three main steps:
1. User lands on the landing page
2. User registers or logs in
3. User selects a role (RENTER, OWNER, or ROOMMATE_HOST)

## Landing Page

The landing page is the entry point of the application. It provides options for users to register or login.

### How to Implement in Angular

1. **Create a Landing Component**:
   ```typescript
   // landing.component.ts
   import { Component } from '@angular/core';
   import { Router } from '@angular/router';

   @Component({
     selector: 'app-landing',
     templateUrl: './landing.component.html',
     styleUrls: ['./landing.component.css']
   })
   export class LandingComponent {
     constructor(private router: Router) {}

     navigateToRegister() {
       this.router.navigate(['/auth/register']);
     }

     navigateToLogin() {
       this.router.navigate(['/auth/login']);
     }
   }
   ```

2. **Create the HTML Template**:
   ```html
   <!-- landing.component.html -->
   <div class="landing-container">
     <h1>Welcome to Roomie!</h1>
     <p>Find your perfect roommate or property</p>

     <div class="buttons">
       <button (click)="navigateToRegister()" class="register-btn">Register</button>
       <button (click)="navigateToLogin()" class="login-btn">Login</button>
     </div>
   </div>
   ```

## User Registration

The registration process collects user information and creates a new account.

### How to Implement in Angular

1. **Create Registration Form**:
   ```typescript
   // auth.component.ts (registration part)
   import { Component } from '@angular/core';
   import { FormBuilder, FormGroup, Validators } from '@angular/forms';
   import { ApiService } from '../api.service';
   import { Router } from '@angular/router';

   @Component({
     selector: 'app-auth',
     templateUrl: './auth.component.html',
     styleUrls: ['./auth.component.css']
   })
   export class AuthComponent {
     registerForm: FormGroup;
     isLogin = false; // Toggle between login and register forms
     errorMessage = '';

     constructor(
       private fb: FormBuilder,
       private apiService: ApiService,
       private router: Router
     ) {
       this.registerForm = this.fb.group({
         firstName: ['', [Validators.required, Validators.minLength(3)]],
         lastName: ['', [Validators.required, Validators.minLength(3)]],
         email: ['', [Validators.required, Validators.email]],
         password: ['', [Validators.required, Validators.minLength(6)]],
         passwordConfirmation: ['', [Validators.required, Validators.minLength(6)]],
         phoneNumber: ['', Validators.required],
         location: ['', Validators.required],
         age: ['', [Validators.required, Validators.min(18), Validators.max(120)]]
       });
     }

     onRegisterSubmit() {
       if (this.registerForm.valid) {
         this.apiService.register(this.registerForm.value).subscribe(
           (response) => {
             // Store token in localStorage
             localStorage.setItem('token', response.token);

             // Navigate to role selection
             this.router.navigate(['/role-selection']);
           },
           (error) => {
             this.errorMessage = error.error || 'Registration failed';
           }
         );
       }
     }
   }
   ```

2. **Create the Registration Form HTML**:
   ```html
   <!-- auth.component.html (registration part) -->
   <div *ngIf="!isLogin" class="register-container">
     <h2>Create an Account</h2>

     <form [formGroup]="registerForm" (ngSubmit)="onRegisterSubmit()">
       <div class="form-group">
         <label for="firstName">First Name</label>
         <input type="text" id="firstName" formControlName="firstName">
         <div *ngIf="registerForm.get('firstName')?.invalid && registerForm.get('firstName')?.touched" class="error">
           First name is required and must be at least 3 characters
         </div>
       </div>

       <div class="form-group">
         <label for="lastName">Last Name</label>
         <input type="text" id="lastName" formControlName="lastName">
         <div *ngIf="registerForm.get('lastName')?.invalid && registerForm.get('lastName')?.touched" class="error">
           Last name is required and must be at least 3 characters
         </div>
       </div>

       <div class="form-group">
         <label for="email">Email</label>
         <input type="email" id="email" formControlName="email">
         <div *ngIf="registerForm.get('email')?.invalid && registerForm.get('email')?.touched" class="error">
           Valid email is required
         </div>
       </div>

       <div class="form-group">
         <label for="password">Password</label>
         <input type="password" id="password" formControlName="password">
         <div *ngIf="registerForm.get('password')?.invalid && registerForm.get('password')?.touched" class="error">
           Password is required and must be at least 6 characters
         </div>
       </div>

       <div class="form-group">
         <label for="passwordConfirmation">Confirm Password</label>
         <input type="password" id="passwordConfirmation" formControlName="passwordConfirmation">
         <div *ngIf="registerForm.get('passwordConfirmation')?.invalid && registerForm.get('passwordConfirmation')?.touched" class="error">
           Password confirmation is required
         </div>
       </div>

       <div class="form-group">
         <label for="phoneNumber">Phone Number</label>
         <input type="text" id="phoneNumber" formControlName="phoneNumber">
         <div *ngIf="registerForm.get('phoneNumber')?.invalid && registerForm.get('phoneNumber')?.touched" class="error">
           Phone number is required
         </div>
       </div>

       <div class="form-group">
         <label for="location">Location</label>
         <input type="text" id="location" formControlName="location">
         <div *ngIf="registerForm.get('location')?.invalid && registerForm.get('location')?.touched" class="error">
           Location is required
         </div>
       </div>

       <div class="form-group">
         <label for="age">Age</label>
         <input type="number" id="age" formControlName="age">
         <div *ngIf="registerForm.get('age')?.invalid && registerForm.get('age')?.touched" class="error">
           Age is required and must be between 18 and 120
         </div>
       </div>

       <button type="submit" [disabled]="registerForm.invalid">Register</button>

       <div *ngIf="errorMessage" class="error-message">
         {{ errorMessage }}
       </div>

       <p>Already have an account? <a (click)="isLogin = true">Login</a></p>
     </form>
   </div>
   ```

## User Login

The login process authenticates existing users.

### How to Implement in Angular

1. **Create Login Form**:
   ```typescript
   // auth.component.ts (login part)
   // Add this to the existing AuthComponent class

   loginForm: FormGroup;

   constructor(
     private fb: FormBuilder,
     private apiService: ApiService,
     private router: Router
   ) {
     // ... existing register form initialization

     this.loginForm = this.fb.group({
       email: ['', [Validators.required, Validators.email]],
       password: ['', [Validators.required]]
     });
   }

   onLoginSubmit() {
     if (this.loginForm.valid) {
       this.apiService.login(this.loginForm.value).subscribe(
         (response) => {
           // Store token in localStorage
           localStorage.setItem('token', response.token);

           // Navigate to role selection or dashboard based on user state
           this.router.navigate(['/role-selection']);
         },
         (error) => {
           this.errorMessage = error.error || 'Login failed';
         }
       );
     }
   }
   ```

2. **Create the Login Form HTML**:
   ```html
   <!-- auth.component.html (login part) -->
   <div *ngIf="isLogin" class="login-container">
     <h2>Login to Your Account</h2>

     <form [formGroup]="loginForm" (ngSubmit)="onLoginSubmit()">
       <div class="form-group">
         <label for="loginEmail">Email</label>
         <input type="email" id="loginEmail" formControlName="email">
         <div *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched" class="error">
           Valid email is required
         </div>
       </div>

       <div class="form-group">
         <label for="loginPassword">Password</label>
         <input type="password" id="loginPassword" formControlName="password">
         <div *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched" class="error">
           Password is required
         </div>
       </div>

       <button type="submit" [disabled]="loginForm.invalid">Login</button>

       <div *ngIf="errorMessage" class="error-message">
         {{ errorMessage }}
       </div>

       <p>Don't have an account? <a (click)="isLogin = false">Register</a></p>
     </form>
   </div>
   ```

## Role Selection

After registration or login, users need to select a role (RENTER, OWNER, or ROOMMATE_HOST).

### How to Implement in Angular

1. **Create Role Selection Component**:
   ```typescript
   // role-selection.component.ts
   import { Component, OnInit } from '@angular/core';
   import { ApiService } from '../api.service';
   import { Router } from '@angular/router';

   @Component({
     selector: 'app-role-selection',
     templateUrl: './role-selection.component.html',
     styleUrls: ['./role-selection.component.css']
   })
   export class RoleSelectionComponent implements OnInit {
     availableRoles: string[] = [];
     selectedRole: string = '';
     errorMessage: string = '';
     userId: number = 0;

     constructor(
       private apiService: ApiService,
       private router: Router
     ) {}

     ngOnInit() {
       // Get user ID from token or API call
       this.getUserId();

       // Get available roles
       this.apiService.getAvailableRoles().subscribe(
         (roles) => {
           this.availableRoles = roles;
         },
         (error) => {
           this.errorMessage = 'Failed to load roles';
         }
       );
     }

     getUserId() {
       // In a real app, you would decode the JWT token or make an API call
       // For this example, we'll assume you have a method to get the user ID
       this.apiService.getCurrentUser().subscribe(
         (user) => {
           this.userId = user.id;
         },
         (error) => {
           this.errorMessage = 'Failed to get user information';
         }
       );
     }

     selectRole() {
       if (this.selectedRole && this.userId) {
         this.apiService.assignRole(this.userId, this.selectedRole).subscribe(
           () => {
             // Navigate to dashboard or home page
             this.router.navigate(['/dashboard']);
           },
           (error) => {
             this.errorMessage = error.error || 'Failed to assign role';
           }
         );
       }
     }
   }
   ```

2. **Create the Role Selection HTML**:
   ```html
   <!-- role-selection.component.html -->
   <div class="role-selection-container">
     <h2>Select Your Role</h2>

     <div class="role-options">
       <div *ngFor="let role of availableRoles" 
            class="role-option" 
            [class.selected]="selectedRole === role"
            (click)="selectedRole = role">
         <h3>{{ role }}</h3>
         <p *ngIf="role === 'RENTER'">Looking for a place to rent</p>
         <p *ngIf="role === 'OWNER'">Have a property to rent out</p>
         <p *ngIf="role === 'ROOMMATE_HOST'">Have a room to share</p>
       </div>
     </div>

     <button (click)="selectRole()" [disabled]="!selectedRole">Continue</button>

     <div *ngIf="errorMessage" class="error-message">
       {{ errorMessage }}
     </div>
   </div>
   ```

## Authentication Flow

The complete authentication flow is as follows:

1. **User visits the landing page**
   - User clicks on Register or Login

2. **If Register:**
   - User fills out registration form
   - Frontend sends data to `/auth/register` endpoint
   - Backend validates data, creates user, and returns JWT token
   - Frontend stores token and redirects to role selection

3. **If Login:**
   - User fills out login form
   - Frontend sends data to `/auth/login` endpoint
   - Backend validates credentials and returns JWT token
   - Frontend stores token and redirects to role selection

4. **Role Selection:**
   - Frontend fetches available roles from `/api/users/roles` endpoint
   - User selects a role
   - Frontend sends selected role to `/api/users/{userId}/role` endpoint
   - Backend assigns the role to the user
   - Frontend redirects to dashboard or home page

## API Endpoints

### Authentication Endpoints

- **Register User**
  - URL: `/auth/register`
  - Method: POST
  - Body: 
    ```json
    {
      "firstName": "string",
      "lastName": "string",
      "email": "string",
      "password": "string",
      "passwordConfirmation": "string",
      "phoneNumber": "string",
      "location": "string",
      "age": number
    }
    ```
  - Response: 
    ```json
    {
      "token": "JWT_TOKEN_STRING"
    }
    ```

- **Login User**
  - URL: `/auth/login`
  - Method: POST
  - Body: 
    ```json
    {
      "email": "string",
      "password": "string"
    }
    ```
  - Response: 
    ```json
    {
      "token": "JWT_TOKEN_STRING"
    }
    ```

### Role Management Endpoints

- **Get Available Roles**
  - URL: `/api/users/roles`
  - Method: GET
  - Response: 
    ```json
    ["RENTER", "OWNER", "ROOMMATE_HOST"]
    ```

- **Assign Role to User**
  - URL: `/api/users/{userId}/role`
  - Method: POST
  - Query Parameter: `role` (one of: RENTER, OWNER, ROOMMATE_HOST)
  - Response: 
    ```
    "Role assigned successfully"
    ```

## Implementing API Service in Angular

Create an API service to handle all HTTP requests:

```typescript
// api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080'; // Adjust based on your backend URL

  constructor(private http: HttpClient) {}

  // Get auth token from localStorage
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Authentication methods
  register(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  // Role management methods
  getAvailableRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/api/users/roles`, { headers: this.getAuthHeaders() });
  }

  assignRole(userId: number, role: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/users/${userId}/role?role=${role}`, {}, { headers: this.getAuthHeaders() });
  }

  // User information methods
  getCurrentUser(): Observable<any> {
    // This endpoint would need to be implemented in your backend
    return this.http.get(`${this.baseUrl}/api/users/me`, { headers: this.getAuthHeaders() });
  }
}
```

## Angular Routing Configuration

The Angular routing configuration is defined in the `app.routes.ts` file. This file maps URLs to components, enabling navigation between different parts of the application.

### Basic Structure of app.routes.ts

```typescript
// app.routes.ts
import { Routes } from '@angular/router';
import { LandingComponent } from './landing/landing.component';
import { AuthComponent } from './auth/auth.component';
import { RoleSelectionComponent } from './role-selection/role-selection.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  // Public routes (accessible without authentication)
  { path: '', component: LandingComponent },
  { path: 'auth', component: AuthComponent },

  // Protected routes (require authentication)
  { 
    path: 'role-selection', 
    component: RoleSelectionComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'dashboard', 
    component: DashboardComponent, 
    canActivate: [AuthGuard] 
  },

  // Redirect any unknown paths to landing page
  { path: '**', redirectTo: '' }
];
```

### Route Guards for Authentication

To protect routes that require authentication, we use an AuthGuard:

```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    // Check if user is logged in by verifying token exists
    const token = localStorage.getItem('token');

    if (token) {
      // User is authenticated
      return true;
    } else {
      // User is not authenticated, redirect to login
      this.router.navigate(['/auth']);
      return false;
    }
  }
}
```

### Registering Routes in the Application

To use these routes in your Angular application, you need to import them in your main module:

```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { LandingComponent } from './landing/landing.component';
import { AuthComponent } from './auth/auth.component';
import { RoleSelectionComponent } from './role-selection/role-selection.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { routes } from './app.routes';

@NgModule({
  declarations: [
    AppComponent,
    LandingComponent,
    AuthComponent,
    RoleSelectionComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### Using Router in Templates

To create navigation links in your templates, use the RouterLink directive:

```html
<!-- Example navigation menu -->
<nav>
  <a [routerLink]="['/']">Home</a>
  <a [routerLink]="['/auth']">Login/Register</a>
  <a [routerLink]="['/dashboard']" *ngIf="isLoggedIn">Dashboard</a>
</nav>

<!-- Router outlet where components will be rendered -->
<router-outlet></router-outlet>
```

### Programmatic Navigation

To navigate programmatically from your components, inject the Router service:

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-example',
  template: '<button (click)="goToDashboard()">Go to Dashboard</button>'
})
export class ExampleComponent {
  constructor(private router: Router) {}

  goToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
```

This routing configuration ensures that:
1. Unauthenticated users can only access the landing page and auth component
2. Authenticated users can access protected routes like role selection and dashboard
3. Any unknown routes redirect to the landing page

---

This guide provides a comprehensive overview of how to implement user authentication and role selection in the Roomie application. By following these instructions, you should be able to create a seamless user experience for registration, login, and role selection.
