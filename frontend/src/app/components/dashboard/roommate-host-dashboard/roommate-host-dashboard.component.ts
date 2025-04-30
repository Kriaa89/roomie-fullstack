import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ApiService } from '../../../services/api.service';
import { PropertyService } from '../../../services/property.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-roommate-host-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './roommate-host-dashboard.component.html',
  styleUrls: ['./roommate-host-dashboard.component.css']
})
export class RoommateHostDashboardComponent implements OnInit {
  user: any = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  activeSection: string = 'overview'; // Default active section
  roomListings: any[] = [];

  // Profile form model
  profileModel: any = {
    city: '',
    occupation: '',
    bio: '',
    genderPreference: 'No Preference',
    smoking: 'No Preference',
    pets: 'No Preference'
  };

  // Room listing form model
  roomListingModel: any = {
    name: '',
    description: '',
    price: '',
    securityDeposit: '',
    location: '',
    availableFrom: '',
    propertyRules: '',
    amenities: {
      wifi: false,
      kitchen: false,
      laundry: false,
      parking: false,
      privateBathroom: false,
      airConditioning: false
    },
    surface: '',
    numberOfRooms: 1,
    numberOfBathrooms: 1,
    numberOfBedrooms: 1,
    audiance: 'SINGLES',
    availability: true
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
        if (this.authService.hasRole('ROOMMATE_HOST')) {
          // Load room listings if user is a roommate host
          this.loadRoomListings();
        } else {
          // Redirect to appropriate dashboard if not a roommate host
          this.router.navigate(['/dashboard']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  loadRoomListings(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.propertyService.getRoomListings().subscribe({
      next: (data) => {
        this.roomListings = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load room listings. Please try again.';
        console.error('Error loading room listings:', error);
      }
    });
  }

  // Set active section
  setActiveSection(section: string): void {
    this.activeSection = section;
  }

  // Create a new room listing
  createRoomListing(formData: any): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Convert amenities object to string format
    const amenitiesArray = [];
    if (formData.amenities) {
      if (formData.amenities.wifi) amenitiesArray.push('WiFi');
      if (formData.amenities.kitchen) amenitiesArray.push('Kitchen');
      if (formData.amenities.laundry) amenitiesArray.push('Laundry');
      if (formData.amenities.parking) amenitiesArray.push('Parking');
      if (formData.amenities.privateBathroom) amenitiesArray.push('Private Bathroom');
      if (formData.amenities.airConditioning) amenitiesArray.push('Air Conditioning');
    }
    const amenitiesString = amenitiesArray.length > 0 ? amenitiesArray.join(', ') : 'Basic amenities';

    // Prepare room listing data
    const roomData = {
      name: formData.name || 'Room for Rent',
      type: 'ROOM',
      location: formData.location || this.user.location,
      price: formData.price || '0',
      description: formData.description || 'Room available for rent',
      amenities: amenitiesString,
      surface: formData.surface || 'Not specified',
      numberOfRooms: formData.numberOfRooms || 1,
      numberOfBathrooms: formData.numberOfBathrooms || 1,
      numberOfBedrooms: formData.numberOfBedrooms || 1,
      propertyRules: formData.propertyRules || 'No specific rules',
      availability: true,
      audiance: formData.audiance || 'SINGLES'
    };

    this.propertyService.createRoomListing(roomData).subscribe({
      next: (response) => {
        this.isLoading = false;
        alert('Room listing created successfully!');
        // Reload the page or navigate to the room listing detail page
        window.location.reload();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to create room listing. Please try again.';
        console.error('Error creating room listing:', error);
      }
    });
  }

  // Save room listing as draft
  saveAsDraft(formData: any): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Convert amenities object to string format
    const amenitiesArray = [];
    if (formData.amenities) {
      if (formData.amenities.wifi) amenitiesArray.push('WiFi');
      if (formData.amenities.kitchen) amenitiesArray.push('Kitchen');
      if (formData.amenities.laundry) amenitiesArray.push('Laundry');
      if (formData.amenities.parking) amenitiesArray.push('Parking');
      if (formData.amenities.privateBathroom) amenitiesArray.push('Private Bathroom');
      if (formData.amenities.airConditioning) amenitiesArray.push('Air Conditioning');
    }
    const amenitiesString = amenitiesArray.length > 0 ? amenitiesArray.join(', ') : '';

    // Prepare room listing data with draft status
    const roomData = {
      name: formData.name || 'Draft Room Listing',
      type: 'ROOM',
      location: formData.location || this.user.location,
      price: formData.price || '0',
      description: formData.description || 'Draft room listing',
      amenities: amenitiesString,
      surface: formData.surface || '',
      numberOfRooms: formData.numberOfRooms || 1,
      numberOfBathrooms: formData.numberOfBathrooms || 1,
      numberOfBedrooms: formData.numberOfBedrooms || 1,
      propertyRules: formData.propertyRules || '',
      availability: false, // Draft listings are not available
      audiance: formData.audiance || 'SINGLES'
    };

    this.propertyService.createRoomListing(roomData).subscribe({
      next: (response) => {
        this.isLoading = false;
        alert('Room listing saved as draft!');
        // Reload the page or navigate to the room listing detail page
        window.location.reload();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to save room listing as draft. Please try again.';
        console.error('Error saving room listing as draft:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  // Save profile changes
  saveProfileChanges(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Create a copy of the user object with updated profile information
    const updatedUser = {
      ...this.user,
      city: this.profileModel.city,
      occupation: this.profileModel.occupation,
      bio: this.profileModel.bio,
      genderPreference: this.profileModel.genderPreference,
      smoking: this.profileModel.smoking,
      petFriendly: this.profileModel.pets // Map 'pets' to 'petFriendly' to match backend field name
    };

    // Call the API service to update the user profile
    this.apiService.updateUser(this.user.id, updatedUser).subscribe({
      next: (response) => {
        this.isLoading = false;
        // Update the local user object with the response
        this.user = response;
        // Show success message
        alert('Profile updated successfully!');
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to update profile. Please try again.';
        console.error('Error updating profile:', error);
      }
    });
  }
}
