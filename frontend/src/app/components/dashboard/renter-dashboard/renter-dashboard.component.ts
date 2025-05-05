import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { PropertyService } from '../../../services/property.service';
import { SwipeService } from '../../../services/swipe.service';
import { VisitRequestService } from '../../../services/visit-request.service';
import { User } from '../../../models/user.model';
import { Property } from '../../../models/property.model';
import { Match } from '../../../models/swipe.model';
import { VisitRequest } from '../../../models/visit-request.model';

@Component({
  selector: 'app-renter-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './renter-dashboard.component.html',
  styleUrls: ['./renter-dashboard.component.css']
})
export class RenterDashboardComponent implements OnInit {
  currentUser: User | null = null;
  availableProperties: Property[] = [];
  matches: Match[] = [];
  visitRequests: VisitRequest[] = [];
  loading = {
    user: true,
    properties: true,
    matches: true,
    visits: true
  };
  error = {
    user: '',
    properties: '',
    matches: '',
    visits: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private propertyService: PropertyService,
    private swipeService: SwipeService,
    private visitRequestService: VisitRequestService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    this.loadAvailableProperties();
    this.loadMatches();
    this.loadVisitRequests();
  }

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.loading.user = false;
      },
      error: (error) => {
        this.error.user = 'Failed to load user data';
        this.loading.user = false;
        console.error('Error loading user data:', error);
      }
    });
  }

  private loadAvailableProperties(): void {
    this.propertyService.getAvailablePropertiesForRenter().subscribe({
      next: (properties) => {
        this.availableProperties = properties;
        this.loading.properties = false;
      },
      error: (error) => {
        this.error.properties = 'Failed to load available properties';
        this.loading.properties = false;
        console.error('Error loading properties:', error);
      }
    });
  }

  private loadMatches(): void {
    this.swipeService.getRenterMatches().subscribe({
      next: (matches) => {
        this.matches = matches;
        this.loading.matches = false;
      },
      error: (error) => {
        this.error.matches = 'Failed to load matches';
        this.loading.matches = false;
        console.error('Error loading matches:', error);
      }
    });
  }

  private loadVisitRequests(): void {
    this.visitRequestService.getVisitRequestsForRenter().subscribe({
      next: (requests) => {
        this.visitRequests = requests;
        this.loading.visits = false;
      },
      error: (error) => {
        this.error.visits = 'Failed to load visit requests';
        this.loading.visits = false;
        console.error('Error loading visit requests:', error);
      }
    });
  }
}
