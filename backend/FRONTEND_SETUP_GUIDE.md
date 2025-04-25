# Frontend Setup Guide for Roomie Application

This guide provides step-by-step instructions on how to set up the frontend of the Roomie application from scratch. It covers deleting existing components, creating new ones, and implementing the authentication and role selection features.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Deleting Existing Components](#deleting-existing-components)
3. [Setting Up the Project Structure](#setting-up-the-project-structure)
4. [Creating the Components](#creating-the-components)
5. [Implementing the API Service](#implementing-the-api-service)
6. [Configuring Routing](#configuring-routing)
7. [Adding Authentication Guards](#adding-authentication-guards)
8. [Testing the Application](#testing-the-application)

## Prerequisites

Before starting, make sure you have the following installed:
- Node.js (v14 or later)
- npm (v6 or later)
- Angular CLI (v16 or later)

## Deleting Existing Components

If you need to start from scratch, follow these steps to delete all existing components:

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Delete the existing components in the src/app directory:
```bash
# Remove all component files
rm -rf src/app/*.component.*

# Remove any existing component directories
rm -rf src/app/auth
rm -rf src/app/landing
rm -rf src/app/role-selection
rm -rf src/app/dashboard
```

3. Keep the essential Angular files:
```
app.config.ts
app.routes.ts
api.service.ts (if you want to modify it)
```

## Setting Up the Project Structure

1. Create the necessary directories for the components:
```bash
mkdir -p src/app/auth
mkdir -p src/app/landing
mkdir -p src/app/role-selection
mkdir -p src/app/dashboard
```

2. Create a basic app component:
```bash
# Create app.component.ts
cat > src/app/app.component.ts << 'EOF'
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Roomie';
}
EOF

# Create app.component.html
cat > src/app/app.component.html << 'EOF'
<router-outlet></router-outlet>
EOF

# Create app.component.css
cat > src/app/app.component.css << 'EOF'
/* Global styles can be added here */
EOF
```

## Creating the Components

### 1. Landing Component

1. Create the landing component files:
```bash
# Create landing.component.ts
cat > src/app/landing/landing.component.ts << 'EOF'
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
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
EOF

# Create landing.component.html
cat > src/app/landing/landing.component.html << 'EOF'
<div class="landing-container">
  <h1>Welcome to Roomie</h1>
  <p>Find your perfect roommate or property</p>
  <div class="auth-buttons">
    <button (click)="navigateToRegister()">Register</button>
    <button (click)="navigateToLogin()">Login</button>
  </div>
</div>
EOF

# Create landing.component.css
cat > src/app/landing/landing.component.css << 'EOF'
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
EOF
```

### 2. Authentication Component

1. Create the authentication component files:
```bash
# Create auth.component.ts
cat > src/app/auth/auth.component.ts << 'EOF'
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
EOF

# Create auth.component.html
cat > src/app/auth/auth.component.html << 'EOF'
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
EOF

# Create auth.component.css
cat > src/app/auth/auth.component.css << 'EOF'
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
EOF
```

### 3. Role Selection Component

1. Create the role selection component files:
```bash
# Create role-selection.component.ts
cat > src/app/role-selection/role-selection.component.ts << 'EOF'
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-role-selection',
  standalone: true,
  imports: [CommonModule],
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
EOF

# Create role-selection.component.html
cat > src/app/role-selection/role-selection.component.html << 'EOF'
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
EOF

# Create role-selection.component.css
cat > src/app/role-selection/role-selection.component.css << 'EOF'
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
EOF
```

### 4. Dashboard Component (Placeholder)

1. Create a simple dashboard component:
```bash
# Create dashboard.component.ts
cat > src/app/dashboard/dashboard.component.ts << 'EOF'
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  userRoles: any[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    const userId = Number(localStorage.getItem('userId'));

    if (userId) {
      this.apiService.getUserRoles(userId).subscribe(
        roles => {
          this.userRoles = roles;
        },
        error => {
          console.error('Failed to load user roles', error);
        }
      );
    }
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    window.location.href = '/';
  }
}
EOF

# Create dashboard.component.html
cat > src/app/dashboard/dashboard.component.html << 'EOF'
<div class="dashboard-container">
  <header>
    <h1>Roomie Dashboard</h1>
    <button (click)="logout()">Logout</button>
  </header>

  <div class="dashboard-content">
    <h2>Welcome to your dashboard!</h2>

    <div *ngIf="userRoles.length > 0" class="roles-section">
      <h3>Your Roles:</h3>
      <ul>
        <li *ngFor="let role of userRoles">
          {{ role.roleType }}
        </li>
      </ul>
    </div>

    <div *ngIf="userRoles.length === 0" class="no-roles">
      <p>You don't have any roles assigned yet.</p>
    </div>
  </div>
</div>
EOF

# Create dashboard.component.css
cat > src/app/dashboard/dashboard.component.css << 'EOF'
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

header button {
  padding: 8px 16px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.dashboard-content {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.roles-section {
  margin-top: 20px;
}

.roles-section ul {
  list-style-type: none;
  padding: 0;
}

.roles-section li {
  padding: 10px;
  margin-bottom: 5px;
  background-color: #e9f7ef;
  border-radius: 4px;
}

.no-roles {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  text-align: center;
}
EOF
```

## Implementing the API Service

1. Create or update the API service:
```bash
cat > src/app/api.service.ts << 'EOF'
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
EOF
```

## Configuring Routing

1. Update the app.routes.ts file:
```bash
cat > src/app/app.routes.ts << 'EOF'
import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { 
    path: '', 
    loadComponent: () => import('./landing/landing.component').then(m => m.LandingComponent) 
  },
  { 
    path: 'auth', 
    loadComponent: () => import('./auth/auth.component').then(m => m.AuthComponent) 
  },
  { 
    path: 'role-selection', 
    loadComponent: () => import('./role-selection/role-selection.component').then(m => m.RoleSelectionComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'dashboard', 
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  { 
    path: '**', 
    redirectTo: '' 
  }
];
EOF
```

## Adding Authentication Guards

1. Create an authentication guard:
```bash
cat > src/app/auth.guard.ts << 'EOF'
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (token) {
    return true;
  } else {
    router.navigate(['/auth']);
    return false;
  }
};
EOF
```

## Testing the Application

1. Update the app.config.ts file to include HTTP client:
```bash
cat > src/app/app.config.ts << 'EOF'
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi())
  ]
};
EOF
```

2. Start the development server:
```bash
ng serve
```

3. Open your browser and navigate to `http://localhost:4200` to see the application.

## Conclusion

You have now set up the frontend of the Roomie application from scratch. The application includes:

1. A landing page with options to register or login
2. An authentication component for user registration and login
3. A role selection component for new users to select their role
4. A dashboard component to display user information
5. An API service to communicate with the backend
6. Authentication guards to protect routes

For any questions or issues, please refer to the API documentation or contact the development team.
