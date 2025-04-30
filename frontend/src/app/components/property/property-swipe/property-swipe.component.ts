import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SwipeService } from '../../../services/swipe.service';
import { PropertyService } from '../../../services/property.service';
import { AuthService } from '../../../services/auth.service';
import { MatchesService } from '../../../services/matches.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-property-swipe',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './property-swipe.component.html',
  styleUrls: ['./property-swipe.component.css']
})
export class PropertySwipeComponent implements OnInit {
  properties: any[] = [];
  currentPropertyIndex = 0;
  loading = true;
  error = '';
  swipeAnimation = '';
  userId: number | null = null;

  // New properties for enhanced functionality
  swipingInProgress = false;
  matchFound = false;
  matchDetails: any = null;
  propertiesLoaded = false;
  noMoreProperties = false;

  // Property owner info for potential match
  currentPropertyOwner: any = null;

  constructor(
    private propertyService: PropertyService,
    private swipeService: SwipeService,
    private authService: AuthService,
    private matchesService: MatchesService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadProperties();
  }

  loadUserInfo(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user && user.id) {
          this.userId = user.id;
        } else {
          this.error = 'User not authenticated';
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        this.error = 'Error loading user info: ' + err.message;
        this.router.navigate(['/login']);
      }
    });
  }

  loadProperties(): void {
    this.loading = true;
    this.error = '';
    this.propertiesLoaded = false;
    this.noMoreProperties = false;

    this.propertyService.getAvailableProperties().subscribe({
      next: (data) => {
        this.properties = data;
        this.propertiesLoaded = true;
        this.loading = false;

        if (this.properties.length === 0) {
          this.noMoreProperties = true;
          this.error = 'No properties available for swiping';
        } else {
          // Load the owner info for the first property
          this.loadPropertyOwnerInfo();
        }
      },
      error: (err) => {
        this.error = 'Error loading properties: ' + (err.message || 'Unknown error');
        this.loading = false;
        this.propertiesLoaded = false;
      }
    });
  }

  /**
   * Load the owner information for the current property
   */
  loadPropertyOwnerInfo(): void {
    if (!this.currentProperty || !this.currentProperty.owner || !this.currentProperty.owner.id) {
      this.currentPropertyOwner = null;
      return;
    }

    const ownerId = this.currentProperty.owner.id;
    // This would typically be a call to get user details, but we'll use the owner info from the property
    this.currentPropertyOwner = this.currentProperty.owner;
  }

  get currentProperty(): any {
    return this.properties[this.currentPropertyIndex] || null;
  }

  swipeLeft(): void {
    if (!this.userId || !this.currentProperty) return;
    if (this.swipingInProgress) return; // Prevent multiple swipes at once

    this.swipingInProgress = true;
    this.swipeAnimation = 'swipe-left';
    this.error = '';

    this.swipeService.createPropertySwipe(this.userId, this.currentProperty.id, 'LEFT')
      .pipe(
        finalize(() => {
          this.swipingInProgress = false;
        })
      )
      .subscribe({
        next: () => {
          setTimeout(() => {
            this.swipeAnimation = '';
            this.moveToNextProperty();
          }, 300);
        },
        error: (err) => {
          this.error = 'Error recording swipe: ' + (err.message || 'Unknown error');
          this.swipeAnimation = '';
        }
      });
  }

  swipeRight(): void {
    if (!this.userId || !this.currentProperty) return;
    if (this.swipingInProgress) return; // Prevent multiple swipes at once

    this.swipingInProgress = true;
    this.swipeAnimation = 'swipe-right';
    this.error = '';
    this.matchFound = false;
    this.matchDetails = null;

    this.swipeService.createPropertySwipe(this.userId, this.currentProperty.id, 'RIGHT')
      .pipe(
        finalize(() => {
          this.swipingInProgress = false;
        })
      )
      .subscribe({
        next: (response) => {
          // Check if the property owner has also swiped right on this user
          if (this.currentPropertyOwner && this.currentPropertyOwner.id) {
            this.checkForMatch(this.currentPropertyOwner.id);
          } else {
            // No owner info, just move to next property
            setTimeout(() => {
              this.swipeAnimation = '';
              this.moveToNextProperty();
            }, 300);
          }
        },
        error: (err) => {
          this.error = 'Error recording swipe: ' + (err.message || 'Unknown error');
          this.swipeAnimation = '';
        }
      });
  }

  /**
   * Check if there's a match between the current user and the property owner
   */
  checkForMatch(ownerId: number): void {
    if (!this.userId) return;

    this.matchesService.getMatchBetweenUsers(this.userId, ownerId)
      .subscribe({
        next: (match) => {
          if (match && !match.error) {
            // Match found!
            this.matchFound = true;
            this.matchDetails = match;

            // Show match notification for a moment before moving to next property
            setTimeout(() => {
              this.matchFound = false;
              this.matchDetails = null;
              this.swipeAnimation = '';
              this.moveToNextProperty();
            }, 3000); // Show match for 3 seconds
          } else {
            // No match yet, just move to next property
            setTimeout(() => {
              this.swipeAnimation = '';
              this.moveToNextProperty();
            }, 300);
          }
        },
        error: (err) => {
          // Error checking for match, just move to next property
          console.error('Error checking for match:', err);
          setTimeout(() => {
            this.swipeAnimation = '';
            this.moveToNextProperty();
          }, 300);
        }
      });
  }

  moveToNextProperty(): void {
    if (this.currentPropertyIndex < this.properties.length - 1) {
      this.currentPropertyIndex++;
      // Load owner info for the next property
      this.loadPropertyOwnerInfo();
    } else {
      // No more properties to swipe
      this.noMoreProperties = true;
      this.error = 'You have viewed all available properties';

      // Provide a way to refresh or go back to dashboard
      setTimeout(() => {
        // Clear error after 5 seconds to show the refresh button
        if (this.error === 'You have viewed all available properties') {
          this.error = '';
        }
      }, 5000);
    }
  }

  /**
   * Refresh the property list
   */
  refreshProperties(): void {
    this.currentPropertyIndex = 0;
    this.loadProperties();
  }

  viewPropertyDetails(): void {
    if (this.currentProperty) {
      this.router.navigate(['/properties', this.currentProperty.id]);
    }
  }
}
