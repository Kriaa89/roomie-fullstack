# Authentication and Role Selection Implementation Guide

This guide provides detailed instructions on how to implement the authentication and role selection features in the Roomie application.

## Table of Contents
1. [Overview](#overview)
2. [Backend Implementation](#backend-implementation)
3. [Frontend Implementation](#frontend-implementation)
4. [API Endpoints](#api-endpoints)
5. [Step-by-Step Implementation Guide](#step-by-step-implementation-guide)

## Overview

The Roomie application requires users to register, log in, and select a role before they can use the full functionality of the application. The authentication flow consists of the following steps:

1. User registers with their personal information
2. User logs in with their credentials
3. New users are prompted to select a role (Renter, Owner, or Roommate Host)
4. After role selection, users are redirected to the appropriate dashboard

## Backend Implementation

The backend is built using Spring Boot and provides RESTful API endpoints for authentication and role management.

### Authentication Service

The authentication service handles user registration and login:

- **Registration**: Creates a new user with the provided information and returns a JWT token
- **Login**: Authenticates the user and returns a JWT token

The `AuthenticationService` class contains the following methods:

```java
// Register a new user
public AuthenticationResponse register(RegisterRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email already in use");
    }

    // Check if password and passwordConfirmation match
    if (!request.getPassword().equals(request.getPasswordConfirmation())) {
        throw new IllegalArgumentException("Password and password confirmation do not match");
    }

    // Create new user with all required fields
    User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .passwordConfirmation(passwordEncoder.encode(request.getPasswordConfirmation()))
            .phoneNumber(request.getPhoneNumber())
            .location(request.getLocation())
            .emailVerified(false)
            .phoneVerified(false)
            .idVerified(false)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();

    // Save user to database
    userRepository.save(user);

    // Generate JWT token
    String token = jwtService.generateToken(user);

    // Return authentication response with token, user ID, and isNewUser flag
    return AuthenticationResponse.builder()
            .token(token)
            .userId(user.getId())
            .isNewUser(true)
            .build();
}

// Authenticate an existing user and generate JWT
public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
        // Use AuthenticationManager to authenticate the user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // If authentication is successful, generate a token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .userId(user.getId())
                .isNewUser(false)
                .build();
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid credentials");
    }
}
```

### User Role Management

The user role controller manages the assignment and retrieval of user roles:

```java
/**
 * Assign a role to a user
 * @param userId ID of the user
 * @param roleRequest the role details
 * @return success message
 */
@PostMapping("/user/{userId}")
public ResponseEntity<?> assignRoleToUser(
        @PathVariable("userId") Long userId,
        @RequestBody Map<String, String> roleRequest) {
    try {
        // Get the role type from the request
        String roleTypeStr = roleRequest.get("roleType");
        if (roleTypeStr == null || roleTypeStr.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role type is required");
        }

        // Convert string to enum
        UserRole.RoleType roleType;
        try {
            roleType = UserRole.RoleType.valueOf(roleTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role type");
        }

        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create a new user role
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRoleType(roleType);
        userRole.setCreatedAt(new Date());
        userRole.setUpdatedAt(new Date());

        // Save the user role
        userRoleRepository.save(userRole);

        // Return success message
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role assigned successfully");
        response.put("userId", userId);
        response.put("roleType", roleType);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
        // Return error message if assignment fails
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

/**
 * Get all roles for a user
 * @param userId ID of the user
 * @return list of roles assigned to the user
 */
@GetMapping("/user/{userId}")
public ResponseEntity<?> getUserRoles(@PathVariable("userId") Long userId) {
    try {
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get the user roles
        return ResponseEntity.ok(userRoleRepository.findByUser(user));
    } catch (Exception e) {
        // Return error message if retrieval fails
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
```

## Frontend Implementation

The frontend is built using Angular and provides a user interface for authentication and role selection.

### Authentication Component

The authentication component handles user registration and login:

```typescript
// auth.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {
  isRegisterMode = true;
  registerForm: FormGroup;
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    // Initialize forms
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(3)]],
      lastName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      passwordConfirmation: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', Validators.required],
      location: ['', Validators.required],
      age: ['', Validators.required]
    });

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    // Check query params for mode
    this.route.queryParams.subscribe(params => {
      if (params['mode'] === 'login') {
        this.isRegisterMode = false;
      } else {
        this.isRegisterMode = true;
      }
    });
  }

  toggleMode() {
    this.isRegisterMode = !this.isRegisterMode;
  }

  onRegister() {
    if (this.registerForm.valid) {
      const registerData = this.registerForm.value;

      this.apiService.register(registerData).subscribe(
        response => {
          // Store token and user ID
          localStorage.setItem('token', response.token);
          localStorage.setItem('userId', response.userId.toString());

          // Check if new user
          if (response.isNewUser) {
            // Navigate to role selection
            this.router.navigate(['/role-selection']);
          } else {
            // Navigate to dashboard
            this.router.navigate(['/dashboard']);
          }
        },
        error => {
          console.error('Registration failed', error);
          // Handle error (show message to user)
        }
      );
    }
  }

  onLogin() {
    if (this.loginForm.valid) {
      const loginData = this.loginForm.value;

      this.apiService.login(loginData).subscribe(
        response => {
          // Store token and user ID
          localStorage.setItem('token', response.token);
          localStorage.setItem('userId', response.userId.toString());

          // Check if new user
          if (response.isNewUser) {
            // Navigate to role selection
            this.router.navigate(['/role-selection']);
          } else {
            // Navigate to dashboard
            this.router.navigate(['/dashboard']);
          }
        },
        error => {
          console.error('Login failed', error);
          // Handle error (show message to user)
        }
      );
    }
  }
}
```

The HTML template for the authentication component:

```html
<!-- auth.component.html -->
<div class="auth-container">
  <div *ngIf="isRegisterMode" class="register-form">
    <h2>Register</h2>
    <form [formGroup]="registerForm" (ngSubmit)="onRegister()">
      <div class="form-group">
        <label for="firstName">First Name</label>
        <input type="text" id="firstName" formControlName="firstName">
        <div *ngIf="registerForm.get('firstName').invalid && registerForm.get('firstName').touched" class="error">
          First name is required
        </div>
      </div>

      <div class="form-group">
        <label for="lastName">Last Name</label>
        <input type="text" id="lastName" formControlName="lastName">
        <div *ngIf="registerForm.get('lastName').invalid && registerForm.get('lastName').touched" class="error">
          Last name is required
        </div>
      </div>

      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" formControlName="email">
        <div *ngIf="registerForm.get('email').invalid && registerForm.get('email').touched" class="error">
          Valid email is required
        </div>
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" formControlName="password">
        <div *ngIf="registerForm.get('password').invalid && registerForm.get('password').touched" class="error">
          Password must be at least 6 characters
        </div>
      </div>

      <div class="form-group">
        <label for="passwordConfirmation">Confirm Password</label>
        <input type="password" id="passwordConfirmation" formControlName="passwordConfirmation">
        <div *ngIf="registerForm.get('passwordConfirmation').invalid && registerForm.get('passwordConfirmation').touched" class="error">
          Passwords must match
        </div>
      </div>

      <div class="form-group">
        <label for="phoneNumber">Phone Number</label>
        <input type="tel" id="phoneNumber" formControlName="phoneNumber">
        <div *ngIf="registerForm.get('phoneNumber').invalid && registerForm.get('phoneNumber').touched" class="error">
          Phone number is required
        </div>
      </div>

      <div class="form-group">
        <label for="location">Location</label>
        <input type="text" id="location" formControlName="location">
        <div *ngIf="registerForm.get('location').invalid && registerForm.get('location').touched" class="error">
          Location is required
        </div>
      </div>

      <div class="form-group">
        <label for="age">Age</label>
        <input type="number" id="age" formControlName="age">
        <div *ngIf="registerForm.get('age').invalid && registerForm.get('age').touched" class="error">
          Age is required
        </div>
      </div>

      <button type="submit" [disabled]="registerForm.invalid">Register</button>
    </form>
    <p>Already have an account? <a (click)="toggleMode()">Login</a></p>
  </div>

  <div *ngIf="!isRegisterMode" class="login-form">
    <h2>Login</h2>
    <form [formGroup]="loginForm" (ngSubmit)="onLogin()">
      <div class="form-group">
        <label for="loginEmail">Email</label>
        <input type="email" id="loginEmail" formControlName="email">
        <div *ngIf="loginForm.get('email').invalid && loginForm.get('email').touched" class="error">
          Valid email is required
        </div>
      </div>

      <div class="form-group">
        <label for="loginPassword">Password</label>
        <input type="password" id="loginPassword" formControlName="password">
        <div *ngIf="loginForm.get('password').invalid && loginForm.get('password').touched" class="error">
          Password is required
        </div>
      </div>

      <button type="submit" [disabled]="loginForm.invalid">Login</button>
    </form>
    <p>Don't have an account? <a (click)="toggleMode()">Register</a></p>
  </div>
</div>
```

### Role Selection Component

The role selection component allows users to select a role after registration:

```typescript
// role-selection.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-role-selection',
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css']
})
export class RoleSelectionComponent {
  selectedRole: string = null;

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  selectRole(role: string) {
    this.selectedRole = role;
  }

  confirmRole() {
    if (this.selectedRole) {
      const userId = Number(localStorage.getItem('userId'));

      this.apiService.assignRole(userId, this.selectedRole).subscribe(
        response => {
          console.log('Role assigned successfully', response);
          // Navigate to dashboard
          this.router.navigate(['/dashboard']);
        },
        error => {
          console.error('Role assignment failed', error);
          // Handle error (show message to user)
        }
      );
    }
  }
}
```

The HTML template for the role selection component:

```html
<!-- role-selection.component.html -->
<div class="role-selection-container">
  <h2>Select Your Role</h2>
  <p>Please select a role to continue:</p>

  <div class="role-options">
    <div class="role-card" [class.selected]="selectedRole === 'RENTER'" (click)="selectRole('RENTER')">
      <h3>Renter</h3>
      <p>Looking for a property to rent</p>
    </div>

    <div class="role-card" [class.selected]="selectedRole === 'OWNER'" (click)="selectRole('OWNER')">
      <h3>Owner</h3>
      <p>Have a property to rent out</p>
    </div>

    <div class="role-card" [class.selected]="selectedRole === 'ROOMMATE_HOST'" (click)="selectRole('ROOMMATE_HOST')">
      <h3>Roommate Host</h3>
      <p>Looking for a roommate</p>
    </div>
  </div>

  <button [disabled]="!selectedRole" (click)="confirmRole()">Continue</button>
</div>
```

### API Service

The API service handles communication with the backend:

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

  // Get HTTP headers with authorization token
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // Register a new user
  register(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, userData);
  }

  // Login a user
  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  // Assign a role to a user
  assignRole(userId: number, roleType: string): Observable<any> {
    const headers = this.getHeaders();
    return this.http.post(
      `${this.baseUrl}/api/user-roles/user/${userId}`,
      { roleType },
      { headers }
    );
  }

  // Get user roles
  getUserRoles(userId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http.get(
      `${this.baseUrl}/api/user-roles/user/${userId}`,
      { headers }
    );
  }
}
```

## API Endpoints

### Authentication Endpoints

#### 1. Register a New User

**Endpoint:** `POST /auth/register`

**Description:** Creates a new user account

**Request:**
- Method: POST
- URL: `http://localhost:8080/auth/register`
- Headers:
  - Content-Type: application/json
- Body (raw JSON):
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "passwordConfirmation": "securePassword123",
  "phoneNumber": "1234567890",
  "location": "New York",
  "age": 25
}
```

**Expected Response:**
- Status: 200 OK
- Body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "isNewUser": true
}
```

#### 2. Login

**Endpoint:** `POST /auth/login`

**Description:** Authenticates a user and returns a JWT token

**Request:**
- Method: POST
- URL: `http://localhost:8080/auth/login`
- Headers:
  - Content-Type: application/json
- Body (raw JSON):
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Expected Response:**
- Status: 200 OK
- Body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "isNewUser": false
}
```

### User Role Endpoints

#### 1. Assign a Role to a User

**Endpoint:** `POST /api/user-roles/user/{userId}`

**Description:** Assigns a role to a user

**Request:**
- Method: POST
- URL: `http://localhost:8080/api/user-roles/user/{userId}`
- Headers:
  - Content-Type: application/json
  - Authorization: Bearer {token}
- Body (raw JSON):
```json
{
  "roleType": "RENTER"
}
```

**Expected Response:**
- Status: 201 Created
- Body:
```json
{
  "message": "Role assigned successfully",
  "userId": 1,
  "roleType": "RENTER"
}
```

#### 2. Get User Roles

**Endpoint:** `GET /api/user-roles/user/{userId}`

**Description:** Gets all roles assigned to a user

**Request:**
- Method: GET
- URL: `http://localhost:8080/api/user-roles/user/{userId}`
- Headers:
  - Authorization: Bearer {token}

**Expected Response:**
- Status: 200 OK
- Body:
```json
[
  {
    "id": 1,
    "createdAt": "2023-06-01T12:00:00.000+00:00",
    "updatedAt": "2023-06-01T12:00:00.000+00:00",
    "roleType": "RENTER"
  }
]
```

## Step-by-Step Implementation Guide

### 1. Setting Up the Landing Page

The landing page should provide options for users to register or log in.

1. Create a new component for the landing page:
```bash
ng generate component landing
```

2. Update the landing component HTML:
```html
<!-- landing.component.html -->
<div class="landing-container">
  <h1>Welcome to Roomie</h1>
  <p>Find your perfect roommate or property</p>
  <div class="auth-buttons">
    <button (click)="navigateToRegister()">Register</button>
    <button (click)="navigateToLogin()">Login</button>
  </div>
</div>
```

3. Update the landing component TypeScript:
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
    this.router.navigate(['/auth'], { queryParams: { mode: 'register' } });
  }

  navigateToLogin() {
    this.router.navigate(['/auth'], { queryParams: { mode: 'login' } });
  }
}
```

4. Add styles to the landing component:
```css
/* landing.component.css */
.landing-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 50px 20px;
  text-align: center;
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

p {
  font-size: 1.2rem;
  margin-bottom: 40px;
}

.auth-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

button {
  padding: 12px 30px;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:first-child {
  background-color: #4CAF50;
  color: white;
}

button:last-child {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

button:hover {
  opacity: 0.9;
}
```

### 2. Implementing the Authentication Component

1. Create a new component for authentication:
```bash
ng generate component auth
```

2. Update the auth component HTML as shown in the [Authentication Component](#authentication-component) section.

3. Update the auth component TypeScript as shown in the [Authentication Component](#authentication-component) section.

4. Add styles to the auth component:
```css
/* auth.component.css */
.auth-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

a {
  color: #4CAF50;
  cursor: pointer;
}
```

### 3. Implementing the Role Selection Component

1. Create a new component for role selection:
```bash
ng generate component role-selection
```

2. Update the role selection component HTML as shown in the [Role Selection Component](#role-selection-component) section.

3. Update the role selection component TypeScript as shown in the [Role Selection Component](#role-selection-component) section.

4. Add styles to the role selection component:
```css
/* role-selection.component.css */
.role-selection-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.role-options {
  display: flex;
  justify-content: space-between;
  margin: 30px 0;
}

.role-card {
  flex: 1;
  margin: 0 10px;
  padding: 20px;
  border: 2px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.role-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.role-card.selected {
  border-color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.1);
}

button {
  padding: 10px 30px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
```

### 4. Implementing the API Service

1. Create a new service for API communication:
```bash
ng generate service api
```

2. Update the API service as shown in the [API Service](#api-service) section.

### 5. Updating the App Module

Update the app module to include the new components and services:

```typescript
// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LandingComponent } from './landing/landing.component';
import { AuthComponent } from './auth/auth.component';
import { RoleSelectionComponent } from './role-selection/role-selection.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ApiService } from './api.service';
import { AuthGuard } from './auth.guard';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'auth', component: AuthComponent },
  { path: 'role-selection', component: RoleSelectionComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' }
];

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
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [ApiService, AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### 6. Adding Authentication Guards

Create an authentication guard to protect routes that require authentication:

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
    const token = localStorage.getItem('token');

    if (token) {
      return true;
    } else {
      this.router.navigate(['/auth']);
      return false;
    }
  }
}
```

## Conclusion

This guide provides a comprehensive overview of how to implement authentication and role selection in the Roomie application. By following these steps, you can create a seamless user experience for registration, login, and role selection.

Remember to:
1. Implement proper validation for all forms
2. Handle errors gracefully and provide feedback to users
3. Secure routes that require authentication
4. Store tokens securely and include them in API requests
5. Redirect users based on their authentication status and role selection

For any questions or issues, please refer to the API documentation or contact the development team.
