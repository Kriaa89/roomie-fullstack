import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { PropertyService } from '../../../services/property.service';
import { Property } from '../../../models/property.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-renter-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renter-dashboard.component.html',
  styleUrls: ['./renter-dashboard.component.css']
})
export class RenterDashboardComponent implements OnInit {
  user: any = null;
  availableProperties: Property[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.user = user;
        if (this.authService.hasRole('RENTER')) {
          this.loadAvailableProperties();
        } else {
          // Redirect to appropriate dashboard if not a renter
          this.router.navigate(['/dashboard']);
        }
      } else {
        this.router.navigate(['/login']);
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

  logout(): void {
    this.authService.logout();
  }

  // Navigate to property details
  viewProperty(propertyId: number): void {
    this.router.navigate(['/properties', propertyId]);
  }
}
