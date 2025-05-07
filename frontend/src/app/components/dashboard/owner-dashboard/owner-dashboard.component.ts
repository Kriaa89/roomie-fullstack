import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { PropertyService } from '../../../services/property.service';
import { VisitRequestService } from '../../../services/visit-request.service';
import { User } from '../../../models/user.model';
import { Property } from '../../../models/property.model';
import { VisitRequest } from '../../../models/visit-request.model';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.css']
})
export class OwnerDashboardComponent implements OnInit {
  currentUser: User | null = null;
  myProperties: Property[] = [];
  visitRequests: VisitRequest[] = [];
  loading = {
    user: true,
    properties: true,
    visits: true
  };
  error = {
    user: '',
    properties: '',
    visits: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private propertyService: PropertyService,
    private visitRequestService: VisitRequestService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    this.loadMyProperties();
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

  private loadMyProperties(): void {
    this.propertyService.getMyProperties().subscribe({
      next: (properties) => {
        this.myProperties = properties;
        this.loading.properties = false;
      },
      error: (error) => {
        this.error.properties = 'Failed to load your properties';
        this.loading.properties = false;
        console.error('Error loading properties:', error);
      }
    });
  }

  private loadVisitRequests(): void {
    this.visitRequestService.getVisitRequestsForOwner().subscribe({
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

  getPropertyById(propertyId: number): Property | undefined {
    return this.myProperties.find(property => property.id === propertyId);
  }

  // Method to handle visit request approval
  approveVisitRequest(visitId: number): void {
    const updateRequest = { status: 'APPROVED' as any, responseMessage: 'Your visit request has been approved.' };
    this.visitRequestService.updateVisitRequest(visitId, updateRequest).subscribe({
      next: () => {
        // Update the local visit request status
        const visitIndex = this.visitRequests.findIndex(visit => visit.id === visitId);
        if (visitIndex !== -1) {
          this.visitRequests[visitIndex].status = 'APPROVED' as any;
          this.visitRequests[visitIndex].responseMessage = 'Your visit request has been approved.';
        }
      },
      error: (error) => {
        console.error('Error approving visit request:', error);
      }
    });
  }

  // Method to handle visit request rejection
  rejectVisitRequest(visitId: number): void {
    const updateRequest = { status: 'REJECTED' as any, responseMessage: 'Sorry, the property is not available at the requested time.' };
    this.visitRequestService.updateVisitRequest(visitId, updateRequest).subscribe({
      next: () => {
        // Update the local visit request status
        const visitIndex = this.visitRequests.findIndex(visit => visit.id === visitId);
        if (visitIndex !== -1) {
          this.visitRequests[visitIndex].status = 'REJECTED' as any;
          this.visitRequests[visitIndex].responseMessage = 'Sorry, the property is not available at the requested time.';
        }
      },
      error: (error) => {
        console.error('Error rejecting visit request:', error);
      }
    });
  }

}
