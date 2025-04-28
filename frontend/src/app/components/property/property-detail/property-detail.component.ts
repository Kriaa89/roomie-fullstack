import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { Property } from '../../../models/property.model';

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css']
})
export class PropertyDetailComponent implements OnInit {
  propertyId: number = 0;
  property: Property | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  isOwner: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.propertyId = +params['id'];
        this.loadProperty();
      } else {
        this.router.navigate(['/dashboard']);
      }
    });
  }

  loadProperty(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getPropertyById(this.propertyId).subscribe({
      next: (data) => {
        this.property = data;
        this.isLoading = false;

        // Check if current user is the owner of this property
        this.authService.currentUser$.subscribe(user => {
          if (user && this.property && this.property.owner) {
            this.isOwner = user.id === this.property.owner.id;
          }
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load property details. Please try again.';
        console.error('Error loading property:', error);
      }
    });
  }

  editProperty(): void {
    this.router.navigate(['/properties/edit', this.propertyId]);
  }

  deleteProperty(): void {
    if (confirm('Are you sure you want to delete this property? This action cannot be undone.')) {
      this.isLoading = true;
      this.errorMessage = '';

      this.propertyService.deleteProperty(this.propertyId).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Failed to delete property. Please try again.';
          console.error('Error deleting property:', error);
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
