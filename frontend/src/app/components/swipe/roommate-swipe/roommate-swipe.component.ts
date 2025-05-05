import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProfileService } from '../../../services/profile.service';
import { SwipeService } from '../../../services/swipe.service';
import { RoommateHostProfile } from '../../../models/profile.model';
import { SwipeAction, SwipeType } from '../../../models/swipe.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-roommate-swipe',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './roommate-swipe.component.html',
  styleUrls: ['./roommate-swipe.component.css']
})
export class RoommateSwipeComponent implements OnInit {
  roommateHosts: RoommateHostProfile[] = [];
  currentIndex = 0;
  loading = true;
  error = '';
  swipeInProgress = false;
  noMoreHosts = false;

  @ViewChild('swipeCard') swipeCard!: ElementRef;

  // Touch/mouse event tracking
  startX = 0;
  currentX = 0;

  constructor(
    private profileService: ProfileService,
    private swipeService: SwipeService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadRoommateHosts();
  }

  private loadRoommateHosts(): void {
    this.profileService.getAvailableRoommateHosts().subscribe({
      next: (hosts) => {
        this.roommateHosts = hosts;
        this.loading = false;
        this.noMoreHosts = hosts.length === 0;
      },
      error: (error) => {
        this.error = 'Failed to load roommate hosts';
        this.loading = false;
        console.error('Error loading roommate hosts:', error);
      }
    });
  }

  // Touch/mouse event handlers
  onTouchStart(event: TouchEvent): void {
    this.startX = event.touches[0].clientX;
    this.currentX = this.startX;
  }

  onTouchMove(event: TouchEvent): void {
    if (this.swipeInProgress || this.noMoreHosts) return;

    this.currentX = event.touches[0].clientX;
    const deltaX = this.currentX - this.startX;
    this.updateCardPosition(deltaX);
  }

  onTouchEnd(): void {
    if (this.swipeInProgress || this.noMoreHosts) return;

    const deltaX = this.currentX - this.startX;
    this.handleSwipeEnd(deltaX);
  }

  onMouseDown(event: MouseEvent): void {
    this.startX = event.clientX;
    this.currentX = this.startX;
  }

  onMouseMove(event: MouseEvent): void {
    if (!event.buttons || this.swipeInProgress || this.noMoreHosts) return;

    this.currentX = event.clientX;
    const deltaX = this.currentX - this.startX;
    this.updateCardPosition(deltaX);
  }

  onMouseUp(): void {
    if (this.swipeInProgress || this.noMoreHosts) return;

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
    if (this.noMoreHosts) return;

    const host = this.roommateHosts[this.currentIndex];
    this.animateSwipe('right', () => {
      this.recordSwipe(host.id, SwipeAction.LIKE);
    });
  }

  handleDislike(): void {
    if (this.noMoreHosts) return;

    const host = this.roommateHosts[this.currentIndex];
    this.animateSwipe('left', () => {
      this.recordSwipe(host.id, SwipeAction.DISLIKE);
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

      // Move to next host
      this.currentIndex++;
      this.noMoreHosts = this.currentIndex >= this.roommateHosts.length;
      this.swipeInProgress = false;
    }, 500);
  }

  private recordSwipe(hostProfileId: number, action: SwipeAction): void {
    const swipeRequest = {
      action: action,
      swipeType: SwipeType.RENTER_TO_HOST,
      targetHostProfileId: hostProfileId
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
    if (!this.swipeInProgress && !this.noMoreHosts) {
      this.handleLike();
    }
  }

  onDislikeClick(): void {
    if (!this.swipeInProgress && !this.noMoreHosts) {
      this.handleDislike();
    }
  }
}
