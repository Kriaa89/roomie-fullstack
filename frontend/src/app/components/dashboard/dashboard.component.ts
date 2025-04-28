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
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private propertyService: PropertyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadProperties();
  }

  loadUserData(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.user = user;
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  loadProperties(): void {
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

  // Navigate to my properties page (for owners)
  viewMyProperties(): void {
    this.router.navigate(['/my-properties']);
  }
}
