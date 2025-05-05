import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SwipeService } from '../../../services/swipe.service';
import { Match } from '../../../models/swipe.model';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.css']
})
export class MatchListComponent implements OnInit {
  matches: Match[] = [];
  loading = true;
  error = '';
  userRole: Role | null = null;

  constructor(
    private swipeService: SwipeService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Determine user role
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userRole = currentUser.role;
    }

    this.loadMatches();
  }

  private loadMatches(): void {
    // Load different matches based on user role
    let matchesObservable;

    if (this.userRole === Role.RENTER) {
      matchesObservable = this.swipeService.getRenterMatches();
    } else if (this.userRole === Role.OWNER) {
      matchesObservable = this.swipeService.getOwnerMatches();
    } else if (this.userRole === Role.ROOMMATE_HOST) {
      matchesObservable = this.swipeService.getHostMatches();
    } else {
      // Fallback to all matches
      matchesObservable = this.swipeService.getMatches();
    }

    matchesObservable.subscribe({
      next: (matches) => {
        this.matches = matches;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load matches';
        this.loading = false;
        console.error('Error loading matches:', error);
      }
    });
  }
}
