import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PropertyService } from '../../services/property.service';
import { Property } from '../../models/property.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: any = null;
  properties: Property[] = [];
  myProperties: Property[] = [];
  availableProperties: Property[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  activeTab: string = 'all'; // Default tab

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

        // Redirect to role-specific dashboard
        if (this.hasRole('OWNER')) {
          this.router.navigate(['/owner-dashboard']);
          return;
        } else if (this.hasRole('RENTER')) {
          this.router.navigate(['/renter-dashboard']);
          return;
        } else if (this.hasRole('ROOMMATE_HOST')) {
          this.router.navigate(['/roommate-host-dashboard']);
          return;
        }

        // If no specific role or multiple roles, load all properties
        this.loadAllProperties();
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  loadAllProperties(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getAllProperties().subscribe({
      next: (data) => {
        this.properties = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load properties. Please try again.';
        console.error('Error loading properties:', error);
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

  loadAvailableProperties(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getAvailableProperties().subscribe({
      next: (data) => {
        this.availableProperties = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load available properties. Please try again.';
        console.error('Error loading properties:', error);
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;

    // Load data for the selected tab if not already loaded
    if (tab === 'all' && this.properties.length === 0) {
      this.loadAllProperties();
    } else if (tab === 'my' && this.myProperties.length === 0 && this.hasRole('OWNER')) {
      this.loadMyProperties();
    } else if (tab === 'available' && this.availableProperties.length === 0) {
      this.loadAvailableProperties();
    }
  }

  logout(): void {
    this.authService.logout();
  }

  // Check if user has a specific role
  hasRole(role: string): boolean {
    return this.authService.hasRole(role);
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

  // Navigate to my properties page (for owners)
  viewMyProperties(): void {
    this.router.navigate(['/my-properties']);
  }
}
