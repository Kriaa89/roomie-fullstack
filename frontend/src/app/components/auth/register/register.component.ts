import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { RegisterRequest } from '../../../models/auth.model';
import { Role } from '../../../models/role.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  roles = [
    { id: Role.RENTER, name: 'Renter', description: 'Looking for a place to rent or a roommate' },
    { id: Role.OWNER, name: 'Owner', description: 'Have properties to rent out' },
    { id: Role.ROOMMATE_HOST, name: 'Roommate-Host', description: 'Have a room to share with a roommate' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    // Redirect to home if already logged in
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/']);
    }

    this.registerForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      location: ['', Validators.required],
      age: ['', [Validators.required, Validators.min(18), Validators.max(120)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      passwordConfirmation: ['', Validators.required],
      role: ['', Validators.required]
    }, {
      validator: this.mustMatch('password', 'passwordConfirmation')
    });
  }

  // Custom validator to check if password and confirmation match
  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors['mustMatch']) {
        // Return if another validator has already found an error
        return;
      }

      // Set error on matchingControl if validation fails
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  // Convenience getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onSubmit() {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    const registerRequest: RegisterRequest = {
      firstName: this.f['firstName'].value,
      lastName: this.f['lastName'].value,
      email: this.f['email'].value,
      phoneNumber: this.f['phoneNumber'].value,
      location: this.f['location'].value,
      age: this.f['age'].value,
      password: this.f['password'].value,
      passwordConfirmation: this.f['passwordConfirmation'].value,
      role: this.f['role'].value
    };

    this.authService.register(registerRequest)
      .subscribe({
        next: (response) => {
          // Navigate based on the selected role
          switch (registerRequest.role) {
            case Role.RENTER:
              this.router.navigate(['/renter']);
              break;
            case Role.OWNER:
              this.router.navigate(['/owner']);
              break;
            case Role.ROOMMATE_HOST:
              this.router.navigate(['/host']);
              break;
            default:
              this.router.navigate(['/']);
          }
        },
        error: error => {
          if (error.error && error.error.message) {
            this.error = error.error.message;
          } else if (error.message) {
            this.error = error.message;
          } else {
            this.error = 'An error occurred during registration. Please try again.';
          }
          console.error('Registration error:', error);
          this.loading = false;
        }
      });
  }
}
