import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { PropertyService } from '../../../services/property.service';
import { Property } from '../../../models/property.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.css']
})
export class OwnerDashboardComponent implements OnInit {
  user: any = null;
  myProperties: Property[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check for error message from property form
    const propertyFormError = localStorage.getItem('propertyFormError');
    if (propertyFormError) {
      this.errorMessage = propertyFormError;
      localStorage.removeItem('propertyFormError'); // Clear the error message
    }

    this.loadUserData();
  }

  loadUserData(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.user = user;
        if (this.authService.hasRole('OWNER')) {
          this.loadMyProperties();
        } else {
          // Redirect to appropriate dashboard if not an owner
          this.router.navigate(['/dashboard']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  loadMyProperties(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getMyProperties().subscribe({
      next: (data) => {
        this.myProperties = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load your properties. Please try again.';
        console.error('Error loading properties:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  // Navigate to property details
  viewProperty(propertyId: number): void {
    this.router.navigate(['/properties', propertyId]);
  }

  // Navigate to create property page
  createProperty(): void {
    this.router.navigate(['/properties/create']);
  }

  // Navigate to edit property page
  editProperty(propertyId: number, event: Event): void {
    event.stopPropagation(); // Prevent the card click event from firing
    this.router.navigate(['/properties/edit', propertyId]);
  }
}
