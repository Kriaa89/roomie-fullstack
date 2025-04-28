import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
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
  @ViewChild('fileInput') fileInput!: ElementRef;

  propertyForm: FormGroup;
  propertyId: number | null = null;
  isEditMode: boolean = false;
  isLoading: boolean = false;
  errorMessage: string = '';
  propertyTypes = Object.values(PropertyType);

  selectedFiles: File[] = [];
  previewUrls: string[] = [];
  uploadProgress: number = 0;

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
    // Check if user has OWNER role
    if (!this.authService.hasRole('OWNER')) {
      // Store error message in localStorage to display it on dashboard
      localStorage.setItem('propertyFormError', 'You need to have the OWNER role to create or edit properties.');
      this.router.navigate(['/dashboard']);
      return;
    }

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
      images: ['', [Validators.required]],
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

        // Load existing images for preview
        if (property.images) {
          const imageUrls = property.images.split(',');
          this.previewUrls = imageUrls;

          // For demonstration purposes, we're not actually loading the files
          // In a real application, you would need to fetch the files from the server
          // or create File objects from the URLs if possible
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load property. Please try again.';
        console.error('Error loading property:', error);
      }
    });
  }

  async onSubmit(): Promise<void> {
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

    try {
      // Upload files and get URLs
      const uploadedUrls = await this.uploadFiles();

      // Update the form value with the uploaded image URLs
      if (uploadedUrls.length > 0) {
        this.propertyForm.patchValue({
          images: uploadedUrls.join(',')
        });
      }

      // Submit the form
      if (this.isEditMode && this.propertyId) {
        this.updateProperty();
      } else {
        this.createProperty();
      }
    } catch (error: any) {
      this.isLoading = false;
      if (error.message && error.message.includes('Please select at least one image')) {
        this.errorMessage = error.message;
      } else {
        this.errorMessage = 'Failed to upload images. Please try again.';
      }
      console.error('Error uploading images:', error);
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

  // File handling methods
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      // Clear previous selections
      this.selectedFiles = [];
      this.previewUrls = [];

      // Add new files
      for (let i = 0; i < input.files.length; i++) {
        const file = input.files[i];
        if (this.isValidImageFile(file)) {
          this.selectedFiles.push(file);
          this.createImagePreview(file);
        } else {
          this.errorMessage = 'Please select valid image files (jpg, jpeg, png, gif)';
        }
      }

      // Update the images form control
      if (this.selectedFiles.length > 0) {
        // Set a temporary value to satisfy the validator
        this.propertyForm.get('images')?.setValue('temp');
        this.propertyForm.get('images')?.markAsTouched();
      } else {
        this.propertyForm.get('images')?.setValue('');
        this.propertyForm.get('images')?.markAsTouched();
      }
    }
  }

  isValidImageFile(file: File): boolean {
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    return validTypes.includes(file.type);
  }

  createImagePreview(file: File): void {
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrls.push(reader.result as string);
    };
    reader.readAsDataURL(file);
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.previewUrls.splice(index, 1);

    // Update the images form control
    if (this.selectedFiles.length > 0) {
      // Set a temporary value to satisfy the validator
      this.propertyForm.get('images')?.setValue('temp');
    } else {
      this.propertyForm.get('images')?.setValue('');
    }
    this.propertyForm.get('images')?.markAsTouched();
  }

  triggerFileInput(): void {
    this.fileInput.nativeElement.click();
  }

  // This method will be called before form submission to handle file uploads
  async uploadFiles(): Promise<string[]> {
    if (this.selectedFiles.length === 0) {
      // If no new files selected, check if we have existing images
      const existingImages = this.propertyForm.get('images')?.value;
      if (existingImages) {
        return existingImages.split(',');
      } else {
        // No files selected and no existing images
        throw new Error('Please select at least one image for the property');
      }
    }

    const uploadedUrls: string[] = [];

    // In a real application, you would upload each file to a server
    // For this example, we'll simulate the upload and return data URLs
    for (let i = 0; i < this.selectedFiles.length; i++) {
      // Simulate upload progress
      this.uploadProgress = Math.round(((i + 1) / this.selectedFiles.length) * 100);

      // In a real app, you would call a service to upload the file
      // For now, we'll just use the preview URLs
      uploadedUrls.push(this.previewUrls[i]);

      // Simulate network delay
      await new Promise(resolve => setTimeout(resolve, 500));
    }

    return uploadedUrls;
  }
}
