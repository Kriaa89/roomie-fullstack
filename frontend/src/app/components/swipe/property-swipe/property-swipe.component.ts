import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PropertyService } from '../../../services/property.service';
import { SwipeService } from '../../../services/swipe.service';
import { Property } from '../../../models/property.model';
import { SwipeAction, SwipeType } from '../../../models/swipe.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-property-swipe',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './property-swipe.component.html',
  styleUrls: ['./property-swipe.component.css']
})
export class PropertySwipeComponent implements OnInit {
  properties: Property[] = [];
  currentIndex = 0;
  loading = true;
  error = '';
  swipeInProgress = false;
  noMoreProperties = false;

  @ViewChild('swipeCard') swipeCard!: ElementRef;

  // Touch/mouse event tracking
  startX = 0;
  currentX = 0;

  constructor(
    private propertyService: PropertyService,
    private swipeService: SwipeService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadProperties();
  }

  private loadProperties(): void {
    this.propertyService.getAvailablePropertiesForRenter().subscribe({
      next: (properties) => {
        this.properties = properties;
        this.loading = false;
        this.noMoreProperties = properties.length === 0;
      },
      error: (error) => {
        this.error = 'Failed to load properties';
        this.loading = false;
        console.error('Error loading properties:', error);
      }
    });
  }

  // Touch/mouse event handlers
  onTouchStart(event: TouchEvent): void {
    this.startX = event.touches[0].clientX;
    this.currentX = this.startX;
  }

  onTouchMove(event: TouchEvent): void {
    if (this.swipeInProgress || this.noMoreProperties) return;

    this.currentX = event.touches[0].clientX;
    const deltaX = this.currentX - this.startX;
    this.updateCardPosition(deltaX);
  }

  onTouchEnd(): void {
    if (this.swipeInProgress || this.noMoreProperties) return;

    const deltaX = this.currentX - this.startX;
    this.handleSwipeEnd(deltaX);
  }

  onMouseDown(event: MouseEvent): void {
    this.startX = event.clientX;
    this.currentX = this.startX;
  }

  onMouseMove(event: MouseEvent): void {
    if (!event.buttons || this.swipeInProgress || this.noMoreProperties) return;

    this.currentX = event.clientX;
    const deltaX = this.currentX - this.startX;
    this.updateCardPosition(deltaX);
  }

  onMouseUp(): void {
    if (this.swipeInProgress || this.noMoreProperties) return;

    const deltaX = this.currentX - this.startX;
    this.handleSwipeEnd(deltaX);
  }

  private updateCardPosition(deltaX: number): void {
    if (!this.swipeCard) return;

    const card = this.swipeCard.nativeElement;
    card.style.transform = `translateX(${deltaX}px) rotate(${deltaX * 0.05}deg)`;

    // Update opacity of like/dislike indicators
    if (deltaX > 0) {
      card.querySelector('.like-indicator').style.opacity = Math.min(deltaX / 100, 1);
      card.querySelector('.dislike-indicator').style.opacity = 0;
    } else if (deltaX < 0) {
      card.querySelector('.dislike-indicator').style.opacity = Math.min(Math.abs(deltaX) / 100, 1);
      card.querySelector('.like-indicator').style.opacity = 0;
    } else {
      card.querySelector('.like-indicator').style.opacity = 0;
      card.querySelector('.dislike-indicator').style.opacity = 0;
    }
  }

  private handleSwipeEnd(deltaX: number): void {
    if (!this.swipeCard) return;

    const card = this.swipeCard.nativeElement;

    // Reset card position if swipe wasn't strong enough
    if (Math.abs(deltaX) < 100) {
      card.style.transform = 'translateX(0) rotate(0)';
      card.querySelector('.like-indicator').style.opacity = 0;
      card.querySelector('.dislike-indicator').style.opacity = 0;
      return;
    }

    // Handle like/dislike based on swipe direction
    this.swipeInProgress = true;

    if (deltaX > 0) {
      this.handleLike();
    } else {
      this.handleDislike();
    }
  }

  handleLike(): void {
    if (this.noMoreProperties) return;

    const property = this.properties[this.currentIndex];
    this.animateSwipe('right', () => {
      this.recordSwipe(property.id, SwipeAction.LIKE);
    });
  }

  handleDislike(): void {
    if (this.noMoreProperties) return;

    const property = this.properties[this.currentIndex];
    this.animateSwipe('left', () => {
      this.recordSwipe(property.id, SwipeAction.DISLIKE);
    });
  }

  private animateSwipe(direction: 'left' | 'right', callback: () => void): void {
    if (!this.swipeCard) return;

    const card = this.swipeCard.nativeElement;
    const moveOutWidth = document.body.clientWidth * 1.5;

    card.style.transform = direction === 'right'
      ? `translateX(${moveOutWidth}px) rotate(30deg)`
      : `translateX(-${moveOutWidth}px) rotate(-30deg)`;

    card.style.transition = 'transform 0.5s ease';

    // After animation completes
    setTimeout(() => {
      card.style.transition = 'none';
      card.style.transform = 'translateX(0) rotate(0)';
      card.querySelector('.like-indicator').style.opacity = 0;
      card.querySelector('.dislike-indicator').style.opacity = 0;

      callback();

      // Move to next property
      this.currentIndex++;
      this.noMoreProperties = this.currentIndex >= this.properties.length;
      this.swipeInProgress = false;
    }, 500);
  }

  private recordSwipe(propertyId: number, action: SwipeAction): void {
    const swipeRequest = {
      action: action,
      swipeType: SwipeType.RENTER_TO_PROPERTY,
      targetPropertyId: propertyId
    };

    this.swipeService.createSwipe(swipeRequest).subscribe({
      next: (response) => {
        console.log('Swipe recorded:', response);

        // If it's a match, we could show a notification or navigate to matches
        if (response.isMatch) {
          console.log('It\'s a match!');
          // Show match notification or navigate to match screen
        }
      },
      error: (error) => {
        console.error('Error recording swipe:', error);
      }
    });
  }

  // Button handlers (alternative to swiping)
  onLikeClick(): void {
    if (!this.swipeInProgress && !this.noMoreProperties) {
      this.handleLike();
    }
  }

  onDislikeClick(): void {
    if (!this.swipeInProgress && !this.noMoreProperties) {
      this.handleDislike();
    }
  }
}
