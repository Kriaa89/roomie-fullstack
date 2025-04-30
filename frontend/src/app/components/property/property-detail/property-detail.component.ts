import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { PropertyInsightsComponent } from '../property-insights/property-insights.component';

// Google Maps types
declare const google: any;

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, PropertyInsightsComponent],
  templateUrl: './property-detail.component.html',
  styleUrls: ['./property-detail.component.css']
})
export class PropertyDetailComponent implements OnInit, AfterViewInit {
  propertyId: number = 0;
  property: any | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  isOwner: boolean = false;
  map: any;
  geocoder: any;
  mapInitialized: boolean = false;
  user: any = null;

  // Visit request properties
  visitDate: string = '';
  minDate: string = '';
  visitRequestSuccess: boolean = false;
  visitRequestError: string = '';

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

    // Load Google Maps API script
    //this.loadGoogleMapsScript();

    // Set minimum date for visit request (today)
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];

    // Get current user
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
    });
  }

  ngAfterViewInit(): void {
    // Initialize map if property is already loaded
    if (this.property && !this.mapInitialized) {
      this.initializeMap();
    }
  }

  /**loadGoogleMapsScript(): void {
    if (!document.getElementById('google-maps-script')) {
      const script = document.createElement('script');
      script.id = 'google-maps-script';
      script.src = `https://maps.googleapis.com/maps/api/js?key=AIzaSyBbmjvJ7i_6msXmbAFhygwzb2hAvF4icZc&callback=initMap`;
      script.async = true;
      script.defer = true;

      // Define the callback function
      window['initMap'] = () => {
        if (this.property) {
          this.initializeMap();
        }
      };

      document.head.appendChild(script);
    }
  }**/

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

        // Initialize map if Google Maps API is already loaded
        if (typeof google !== 'undefined' && !this.mapInitialized) {
          this.initializeMap();
        }
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

  requestVisit(): void {
    if (!this.visitDate) {
      this.visitRequestError = 'Please select a date for your visit';
      return;
    }

    this.isLoading = true;
    this.visitRequestSuccess = false;
    this.visitRequestError = '';

    const visitRequest = {
      visitDate: new Date(this.visitDate),
      propertyList: { id: this.propertyId }
    };

    // Call the service to submit the visit request
    this.propertyService.requestVisit(this.propertyId, visitRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.visitRequestSuccess = true;
        this.visitDate = ''; // Reset the form
      },
      error: (error) => {
        this.isLoading = false;
        this.visitRequestError = error.error?.message || 'Failed to submit visit request. Please try again.';
        console.error('Error submitting visit request:', error);
      }
    });
  }

  initializeMap(): void {
    if (!this.property || this.mapInitialized) return;

    try {
      // Create a new map instance
      this.geocoder = new google.maps.Geocoder();
      const mapElement = document.getElementById('map');

      if (mapElement) {
        // Default center (will be updated after geocoding)
        const defaultCenter = { lat: 0, lng: 0 };

        this.map = new google.maps.Map(mapElement, {
          center: defaultCenter,
          zoom: 15,
          mapTypeControl: true,
          streetViewControl: true,
          fullscreenControl: true
        });

        // Geocode the property location
        this.geocodeAddress(this.property.location);
        this.mapInitialized = true;
      }
    } catch (error) {
      console.error('Error initializing map:', error);
    }
  }

  geocodeAddress(address: string): void {
    if (!this.geocoder || !this.map) return;

    this.geocoder.geocode({ 'address': address }, (results: any, status: any) => {
      if (status === 'OK' && results[0]) {
        // Center the map on the geocoded location
        this.map.setCenter(results[0].geometry.location);

        // Add a marker at the geocoded location
        new google.maps.Marker({
          map: this.map,
          position: results[0].geometry.location,
          title: this.property.name,
          animation: google.maps.Animation.DROP
        });
      } else {
        console.error('Geocode was not successful for the following reason:', status);
      }
    });
  }
}
