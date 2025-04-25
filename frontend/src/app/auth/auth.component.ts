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
  registerForm!: FormGroup;
  loginForm!: FormGroup;

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
