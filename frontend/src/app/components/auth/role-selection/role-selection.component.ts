import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Role, RoleRequest } from '../../../models/role.model';

@Component({
  selector: 'app-role-selection',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css']
})
export class RoleSelectionComponent implements OnInit {
  roleForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  roles = [
    { id: Role.RENTER, name: 'Renter', description: 'Looking for a place to rent or a roommate' },
    { id: Role.OWNER, name: 'Property Owner', description: 'Have properties to rent out' },
    { id: Role.ROOMMATE_HOST, name: 'Roommate Host', description: 'Have a room to share with a roommate' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.roleForm = this.formBuilder.group({
      role: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Check if user is already logged in
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/login']);
    }
  }

  // Convenience getter for easy access to form fields
  get f() { return this.roleForm.controls; }

  onSubmit() {
    this.submitted = true;

    // Stop here if form is invalid
    if (this.roleForm.invalid) {
      return;
    }

    this.loading = true;
    const userId = this.authService.currentUserValue?.userId;

    if (!userId) {
      this.error = 'User ID not found. Please login again.';
      this.loading = false;
      return;
    }

    const roleRequest: RoleRequest = {
      roleType: this.f['role'].value
    };

    this.authService.selectRole(userId, roleRequest)
      .subscribe({
        next: (response) => {
          // Navigate based on the selected role
          switch (roleRequest.roleType) {
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
          this.error = error;
          this.loading = false;
        }
      });
  }
}
