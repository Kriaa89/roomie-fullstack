import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { VisitRequestService } from '../../../services/visit-request.service';
import { AuthService } from '../../../services/auth.service';
import { VisitRequest, VisitStatus } from '../../../models/visit-request.model';
import { Role } from '../../../models/role.model';

@Component({
  selector: 'app-visit-request-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './visit-request-list.component.html',
  styleUrls: ['./visit-request-list.component.css']
})
export class VisitRequestListComponent implements OnInit {
  visitRequests: VisitRequest[] = [];
  loading = true;
  error = '';
  userRole: Role | null = null;
  VisitStatus = VisitStatus; // Make enum available in template

  // For filtering
  statusFilter: string = 'all';
  filteredRequests: VisitRequest[] = [];

  constructor(
    private visitRequestService: VisitRequestService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    // Determine user role
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.userRole = currentUser.role;
    }

    this.loadVisitRequests();
  }

  private loadVisitRequests(): void {
    // Load different requests based on user role
    let requestObservable;

    if (this.userRole === Role.RENTER) {
      requestObservable = this.visitRequestService.getVisitRequestsForRenter();
    } else if (this.userRole === Role.OWNER) {
      requestObservable = this.visitRequestService.getVisitRequestsForOwner();
    } else if (this.userRole === Role.ROOMMATE_HOST) {
      requestObservable = this.visitRequestService.getVisitRequestsForRoommateHost();
    } else {
      // Fallback to all requests
      requestObservable = this.visitRequestService.getAllVisitRequests();
    }

    requestObservable.subscribe({
      next: (requests) => {
        this.visitRequests = requests;
        this.applyFilter(); // Apply initial filter
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load visit requests';
        this.loading = false;
        console.error('Error loading visit requests:', error);
      }
    });
  }

  // Filter requests by status
  applyFilter(status: string = 'all'): void {
    this.statusFilter = status;

    if (status === 'all') {
      this.filteredRequests = [...this.visitRequests];
    } else {
      this.filteredRequests = this.visitRequests.filter(
        request => request.status.toLowerCase() === status.toLowerCase()
      );
    }
  }

  // Update request status (approve/reject)
  updateRequestStatus(requestId: number, status: VisitStatus, responseMessage: string = ''): void {
    const updateRequest = {
      status: status,
      responseMessage: responseMessage
    };

    this.visitRequestService.updateVisitRequest(requestId, updateRequest).subscribe({
      next: (updatedRequest) => {
        // Update the request in the local array
        const index = this.visitRequests.findIndex(r => r.id === requestId);
        if (index !== -1) {
          this.visitRequests[index] = updatedRequest;
          this.applyFilter(this.statusFilter); // Reapply current filter
        }
      },
      error: (error) => {
        console.error('Error updating visit request:', error);
        // Show error message to user
      }
    });
  }

  // Cancel a request (for renters)
  cancelRequest(requestId: number): void {
    this.visitRequestService.cancelVisitRequest(requestId).subscribe({
      next: (updatedRequest) => {
        // Update the request in the local array
        const index = this.visitRequests.findIndex(r => r.id === requestId);
        if (index !== -1) {
          this.visitRequests[index] = updatedRequest;
          this.applyFilter(this.statusFilter); // Reapply current filter
        }
      },
      error: (error) => {
        console.error('Error cancelling visit request:', error);
        // Show error message to user
      }
    });
  }

  // Helper method to format date
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}
