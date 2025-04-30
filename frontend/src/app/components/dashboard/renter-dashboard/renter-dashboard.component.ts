import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ApiService } from '../../../services/api.service';
import { PropertyService } from '../../../services/property.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-renter-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './renter-dashboard.component.html',
  styleUrls: ['./renter-dashboard.component.css']
})
export class RenterDashboardComponent implements OnInit {
  user: any = null;
  availableProperties: any[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  activeSection: string = 'overview'; // Default active section

  // Profile form model
  profileModel: any = {
    occupation: '',
    city: '',
    bio: '',
    budgetMin: '',
    budgetMax: '',
    desiredLocations: '',
    moveInDate: '',
    petFriendly: 'No Preference',
    smoking: 'No Preference',
    genderPreference: 'No Preference'
  };

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
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

          // Initialize profile model with current user values
          this.profileModel = {
            occupation: user.occupation || '',
            city: user.city || '',
            bio: user.bio || '',
            budgetMin: user.budgetMin || '',
            budgetMax: user.budgetMax || '',
            desiredLocations: user.desiredLocations || '',
            moveInDate: user.moveInDate || '',
            petFriendly: user.petFriendly || 'No Preference',
            smoking: user.smoking || 'No Preference',
            genderPreference: user.genderPreference || 'No Preference'
          };

          // Check if profile is incomplete and show profile section if needed
          this.checkProfileCompleteness();
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

  // Set active section
  setActiveSection(section: string): void {
    this.activeSection = section;
  }

  // Navigate to property swipe page
  navigateToPropertySwipe(): void {
    // Use navigateByUrl instead of navigate to ensure a full navigation
    this.router.navigateByUrl('/properties/swipe', { skipLocationChange: false });
  }

  // Express interest in a property
  expressInterest(propertyId: number, event: Event): void {
    event.stopPropagation(); // Prevent the card click event from firing
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.expressInterest(propertyId).subscribe({
      next: (response) => {
        this.isLoading = false;
        alert('You have expressed interest in this property. The owner will be notified.');
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to express interest. Please try again.';
        console.error('Error expressing interest:', error);
      }
    });
  }

  // Schedule a visit to a property
  scheduleVisit(propertyId: number, event: Event): void {
    event.stopPropagation(); // Prevent the card click event from firing
    this.isLoading = true;
    this.errorMessage = '';

    // In a real implementation, this would open a date picker modal
    // For now, we'll just use the current date + 3 days
    const visitDate = new Date();
    visitDate.setDate(visitDate.getDate() + 3);
    const formattedDate = visitDate.toISOString().split('T')[0];

    this.propertyService.scheduleVisit(propertyId, formattedDate).subscribe({
      next: (response) => {
        this.isLoading = false;
        alert(`Visit scheduled for ${formattedDate}. The owner will be notified.`);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to schedule visit. Please try again.';
        console.error('Error scheduling visit:', error);
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

  // Check if profile is incomplete
  checkProfileCompleteness(): void {
    // Define required fields for a complete profile
    const requiredFields = [
      { field: 'location', label: 'Location' },
      { field: 'city', label: 'City' },
      { field: 'occupation', label: 'Occupation' },
      { field: 'bio', label: 'Bio' },
      { field: 'budgetMin', label: 'Minimum Budget' },
      { field: 'budgetMax', label: 'Maximum Budget' }
    ];

    // Check which fields are missing
    const missingFields = requiredFields.filter(item =>
      !this.user[item.field] ||
      (typeof this.user[item.field] === 'string' && this.user[item.field].trim() === '')
    );

    // If any required fields are missing, show the profile section
    if (missingFields.length > 0) {
      this.activeSection = 'profile';

      // Create a more informative error message
      const missingFieldLabels = missingFields.map(item => item.label);
      this.errorMessage = `Please complete your profile to fully use the platform. Missing fields: ${missingFieldLabels.join(', ')}.`;

      // Pre-fill the profile model with existing user data
      this.profileModel = {
        occupation: this.user.occupation || '',
        city: this.user.city || '',
        bio: this.user.bio || '',
        budgetMin: this.user.budgetMin || '',
        budgetMax: this.user.budgetMax || '',
        desiredLocations: this.user.desiredLocations || '',
        moveInDate: this.user.moveInDate || '',
        petFriendly: this.user.petFriendly || 'No Preference',
        smoking: this.user.smoking || 'No Preference',
        genderPreference: this.user.genderPreference || 'No Preference'
      };
    }
  }

  // Save profile changes
  saveProfileChanges(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Validate required fields on the client side before sending to server
    const requiredFields = [
      { field: 'occupation', message: 'Occupation is required' },
      { field: 'city', message: 'City is required' },
      { field: 'bio', message: 'Bio is required' },
      { field: 'budgetMin', message: 'Minimum budget is required' },
      { field: 'budgetMax', message: 'Maximum budget is required' }
    ];

    // Check for empty required fields
    const missingFields = requiredFields.filter(item =>
      !this.profileModel[item.field] || this.profileModel[item.field].trim() === ''
    );

    if (missingFields.length > 0) {
      this.isLoading = false;
      this.errorMessage = missingFields.map(item => item.message).join('. ');
      return;
    }

    // Create a copy of the user object
    const updatedUser = { ...this.user };

    // Update all fields from the profile model
    updatedUser.occupation = this.profileModel.occupation.trim();
    updatedUser.city = this.profileModel.city.trim();
    updatedUser.bio = this.profileModel.bio.trim();
    updatedUser.budgetMin = this.profileModel.budgetMin.trim();
    updatedUser.budgetMax = this.profileModel.budgetMax.trim();
    updatedUser.desiredLocations = this.profileModel.desiredLocations?.trim() || '';
    updatedUser.moveInDate = this.profileModel.moveInDate || '';
    updatedUser.petFriendly = this.profileModel.petFriendly || 'No Preference';
    updatedUser.smoking = this.profileModel.smoking || 'No Preference';
    updatedUser.genderPreference = this.profileModel.genderPreference || 'No Preference';

    // Set location to city (as per backend requirement)
    updatedUser.location = updatedUser.city;

    // Call the API service to update the renter profile
    this.apiService.updateRenterProfile(updatedUser).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Update the local user object with the response
        this.user = response;
        // Update auth service current user
        this.authService.updateCurrentUser(response);
        // Show success message
        alert('Profile updated successfully!');
        // Switch to overview section
        this.activeSection = 'overview';
      },
      error: (error) => {
        this.isLoading = false;
        // Extract error message from response if available
        let errorMsg = 'Failed to update profile. Please ensure all required fields are filled.';

        if (error.error) {
          if (typeof error.error === 'string') {
            errorMsg = error.error;
          } else if (error.error.message) {
            errorMsg = error.error.message;
          }
        }

        this.errorMessage = errorMsg;
        console.error('Error updating profile:', error);
      }
    });
  }
}
