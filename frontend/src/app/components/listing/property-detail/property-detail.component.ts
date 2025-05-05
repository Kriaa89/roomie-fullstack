import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { VisitRequestService } from '../../../services/visit-request.service';
import { Property } from '../../../models/property.model';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css']
})
export class PropertyDetailComponent implements OnInit {
  property: Property | null = null;
  loading = true;
  error = '';
  currentImageIndex = 0;
  imageUrls: string[] = [];
  isOwner = false;
  isRenter = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private visitRequestService: VisitRequestService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const propertyId = +params['id']; // Convert to number
      if (propertyId) {
        this.loadProperty(propertyId);
      } else {
        this.error = 'Invalid property ID';
        this.loading = false;
      }
    });

    // Check user role
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.isOwner = currentUser.roles.includes(Role.OWNER);
      this.isRenter = currentUser.roles.includes(Role.RENTER);
    }
  }

  private loadProperty(propertyId: number): void {
    this.propertyService.getPropertyById(propertyId).subscribe({
      next: (property) => {
        this.property = property;
        this.loading = false;

        // Process images
        if (property.images) {
          this.imageUrls = property.images.split(',').map(url => url.trim());
        }

        // Check if current user is the owner of this property
        if (this.isOwner && this.authService.currentUserValue) {
          const userId = this.authService.currentUserValue.userId;
          this.isOwner = property.owner.id === userId;
        }
      },
      error: (error) => {
        this.error = 'Failed to load property details';
        this.loading = false;
        console.error('Error loading property:', error);
      }
    });
  }

  // Image navigation methods
  nextImage(): void {
    if (this.imageUrls.length > 1) {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.imageUrls.length;
    }
  }

  prevImage(): void {
    if (this.imageUrls.length > 1) {
      this.currentImageIndex = (this.currentImageIndex - 1 + this.imageUrls.length) % this.imageUrls.length;
    }
  }

  // Navigate to edit page (for owners)
  editProperty(): void {
    if (this.property) {
      this.router.navigate(['/owner/properties/edit', this.property.id]);
    }
  }

  // Navigate to visit request form (for renters)
  requestVisit(): void {
    if (this.property) {
      this.router.navigate(['/renter/visits/new'], {
        queryParams: { propertyId: this.property.id }
      });
    }
  }
}
