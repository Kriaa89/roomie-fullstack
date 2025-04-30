import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { PropertyService } from '../../../services/property.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.css']
})
export class OwnerDashboardComponent implements OnInit {
  user: any = null;
  myProperties: any[] = [];
  visitRequests: any[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  activeSection: string = 'properties'; // Default active section

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
          this.loadVisitRequests();
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

  // Set active section
  setActiveSection(section: string): void {
    this.activeSection = section;
  }

  // Get count of available properties
  getAvailablePropertiesCount(): number {
    return this.myProperties.filter(property => property.availability).length;
  }

  // Get count of pending visit requests
  getPendingVisitRequestsCount(): number {
    return this.visitRequests.filter(request => request.status === 'PENDING').length;
  }

  // Toggle property availability
  toggleAvailability(propertyId: number, event: Event): void {
    event.stopPropagation(); // Prevent the card click event from firing

    // Find the property
    const property = this.myProperties.find(p => p.id === propertyId);
    if (!property) return;

    this.isLoading = true;
    this.errorMessage = '';

    // Call the API to toggle property availability
    this.propertyService.toggleAvailability(propertyId).subscribe({
      next: (updatedProperty) => {
        this.isLoading = false;
        // Update the local property object with the response
        property.availability = updatedProperty.availability;
        // Show a success message
        alert(`Property ${property.name} is now ${property.availability ? 'available' : 'unavailable'}.`);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to update property availability. Please try again.';
        console.error('Error updating property availability:', error);
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

  // Delete property
  deleteProperty(propertyId: number, event: Event): void {
    event.stopPropagation(); // Prevent the card click event from firing

    // Confirm deletion
    if (confirm('Are you sure you want to delete this property? This action cannot be undone.')) {
      this.propertyService.deleteProperty(propertyId).subscribe({
        next: () => {
          // Remove the property from the local array
          this.myProperties = this.myProperties.filter(p => p.id !== propertyId);
          alert('Property deleted successfully.');
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete property. Please try again.';
          console.error('Error deleting property:', error);
        }
      });
    }
  }

  // Load visit requests for all properties
  loadVisitRequests(): void {
    this.isLoading = true;

    // First, get all properties to ensure we have their IDs
    this.propertyService.getMyProperties().subscribe({
      next: (properties) => {
        // If no properties, no need to fetch visit requests
        if (properties.length === 0) {
          this.isLoading = false;
          this.visitRequests = [];
          return;
        }

        // Create an array to hold all visit requests
        let allVisitRequests: any[] = [];
        let completedRequests = 0;

        // For each property, get its visit requests
        properties.forEach((property: { id: number; name: string; location: string }) => {
          this.propertyService.getPropertyVisitRequests(property.id).subscribe({
            next: (requests) => {
              // Add property information to each request for display purposes
              requests.forEach((request: any) => {
                request.propertyName = property.name;
                request.propertyLocation = property.location;
              });

              // Add these requests to our array
              allVisitRequests = [...allVisitRequests, ...requests];

              // Check if we've completed all requests
              completedRequests++;
              if (completedRequests === properties.length) {
                this.visitRequests = allVisitRequests;
                this.isLoading = false;
              }
            },
            error: (error) => {
              console.error(`Error loading visit requests for property ${property.id}:`, error);
              // Still count this as completed even if it failed
              completedRequests++;
              if (completedRequests === properties.length) {
                this.visitRequests = allVisitRequests;
                this.isLoading = false;
              }
            }
          });
        });
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading properties for visit requests:', error);
      }
    });
  }

  // Update visit request status (accept or reject)
  updateVisitRequestStatus(requestId: number, status: string, event: Event): void {
    event.stopPropagation(); // Prevent any parent click events

    this.isLoading = true;

    this.propertyService.updateVisitRequestStatus(requestId, status).subscribe({
      next: (updatedRequest) => {
        // Update the request in our local array
        const index = this.visitRequests.findIndex(r => r.id === requestId);
        if (index !== -1) {
          this.visitRequests[index].status = status;
        }

        this.isLoading = false;
        alert(`Visit request ${status.toLowerCase()}.`);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = `Failed to ${status.toLowerCase()} visit request. Please try again.`;
        console.error(`Error updating visit request status:`, error);
      }
    });
  }
}
