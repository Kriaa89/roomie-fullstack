import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-role-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './role-selection.component.html',
  styleUrls: ['./role-selection.component.css']
})
export class RoleSelectionComponent implements OnInit {
  userId: number | null = null;
  availableRoles = [
    { type: 'OWNER', title: 'Property Owner', description: 'List and manage your properties for rent' },
    { type: 'RENTER', title: 'Renter', description: 'Search and rent properties' },
    { type: 'ROOMMATE_HOST', title: 'Roommate Host', description: 'Host roommates in your property' }
  ];
  allRoles = [
    { type: 'OWNER', title: 'Property Owner', description: 'List and manage your properties for rent' },
    { type: 'RENTER', title: 'Renter', description: 'Search and rent properties' },
    { type: 'ROOMMATE_HOST', title: 'Roommate Host', description: 'Host roommates in your property' }
  ];
  selectedRole: string | null = null;
  errorMessage: string = '';
  isLoading: boolean = false;
  currentRoles: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get current user from auth service
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.userId = user.id;
        this.currentRoles = user.roles || [];

        console.log('Current roles:', this.currentRoles);

        // Instead of filtering out roles the user already has, show all roles
        // This ensures the RENTER role is always displayed
        this.availableRoles = [...this.allRoles];
        console.log('Available roles (showing all):', this.availableRoles);

        // Only redirect to dashboard if there are truly no roles available
        if (this.availableRoles.length === 0) {
          console.log('No available roles, redirecting to dashboard');
          this.router.navigate(['/dashboard']);
        }
      } else {
        // If no user is logged in, redirect to login
        console.log('No user logged in, redirecting to login');
        this.router.navigate(['/login']);
      }
    });
  }

  selectRole(roleType: string): void {
    // Check if user already has this role
    const hasRole = this.currentRoles.includes(roleType);
    if (hasRole) {
      this.errorMessage = `You already have the ${roleType} role. Please select a different role.`;
      return;
    }

    this.errorMessage = ''; // Clear any previous error messages
    this.selectedRole = roleType;
  }

  confirmSelection(): void {
    if (!this.selectedRole || !this.userId) {
      this.errorMessage = 'Please select a role to continue';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.selectRole(this.userId, this.selectedRole).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to select role. Please try again.';
        }
        console.error('Role selection error:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    // The logout method in AuthService already handles navigation to login page
  }
}
