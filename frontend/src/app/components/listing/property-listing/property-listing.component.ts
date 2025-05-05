import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { Property } from '../../../models/property.model';

@Component({
  selector: 'app-property-listing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-listing.component.html',
  styleUrls: ['./property-listing.component.css']
})
export class PropertyListingComponent implements OnInit {
  properties: Property[] = [];
  loading = true;
  error = '';

  constructor(private propertyService: PropertyService) { }

  ngOnInit(): void {
    this.loadProperties();
  }

  private loadProperties(): void {
    this.propertyService.getAllProperties().subscribe({
      next: (properties) => {
        this.properties = properties;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load properties';
        this.loading = false;
        console.error('Error loading properties:', error);
      }
    });
  }

  // Method to filter properties (can be expanded later)
  filterProperties(searchTerm: string): void {
    if (!searchTerm) {
      this.loadProperties();
      return;
    }

    // Simple client-side filtering
    this.propertyService.getAllProperties().subscribe({
      next: (properties) => {
        this.properties = properties.filter(property =>
          property.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          property.location.toLowerCase().includes(searchTerm.toLowerCase()) ||
          property.description.toLowerCase().includes(searchTerm.toLowerCase())
        );
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to filter properties';
        this.loading = false;
        console.error('Error filtering properties:', error);
      }
    });
  }
}
