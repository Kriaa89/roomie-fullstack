import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { Property, PropertyType } from '../../../models/property.model';

@Component({
  selector: 'app-property-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './property-form.component.html',
  styleUrls: ['./property-form.component.css']
})
export class PropertyFormComponent implements OnInit {
  propertyForm: FormGroup;
  propertyId: number | null = null;
  isEditMode: boolean = false;
  isLoading: boolean = false;
  errorMessage: string = '';
  propertyTypes = Object.values(PropertyType);

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private authService: AuthService
  ) {
    this.propertyForm = this.createPropertyForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.propertyId = +params['id'];
        this.isEditMode = true;
        this.loadProperty();
      }
    });
  }

  createPropertyForm(): FormGroup {
    return this.formBuilder.group({
      name: ['', [Validators.required]],
      type: [PropertyType.APARTMENT, [Validators.required]],
      location: ['', [Validators.required]],
      price: ['', [Validators.required]],
      description: ['', [Validators.required]],
      images: [''],
      amenities: [''],
      surface: [''],
      numberOfRooms: [null],
      numberOfBathrooms: [null],
      numberOfBedrooms: [null],
      propertyRules: [''],
      availability: [true],
      audiance: ['']
    });
  }

  loadProperty(): void {
    if (!this.propertyId) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getPropertyById(this.propertyId).subscribe({
      next: (property: Property) => {
        this.isLoading = false;
        this.propertyForm.patchValue({
          name: property.name,
          type: property.type,
          location: property.location,
          price: property.price,
          description: property.description,
          images: property.images,
          amenities: property.amenities,
          surface: property.surface,
          numberOfRooms: property.numberOfRooms,
          numberOfBathrooms: property.numberOfBathrooms,
          numberOfBedrooms: property.numberOfBedrooms,
          propertyRules: property.propertyRules,
          availability: property.availability,
          audiance: property.audiance
        });
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load property. Please try again.';
        console.error('Error loading property:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.propertyForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.propertyForm.controls).forEach(key => {
        const control = this.propertyForm.get(key);
        control?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.propertyId) {
      this.updateProperty();
    } else {
      this.createProperty();
    }
  }

  createProperty(): void {
    this.propertyService.createProperty(this.propertyForm.value).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.router.navigate(['/properties', response.id]);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to create property. Please try again.';
        }
        console.error('Error creating property:', error);
      }
    });
  }

  updateProperty(): void {
    if (!this.propertyId) return;

    this.propertyService.updateProperty(this.propertyId, this.propertyForm.value).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.router.navigate(['/properties', this.propertyId]);
      },
      error: (error) => {
        this.isLoading = false;
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Failed to update property. Please try again.';
        }
        console.error('Error updating property:', error);
      }
    });
  }

  cancel(): void {
    if (this.isEditMode && this.propertyId) {
      this.router.navigate(['/properties', this.propertyId]);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }
}
