import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ProfileService } from '../../../services/profile.service';
import { SwipeService } from '../../../services/swipe.service';
import { VisitRequestService } from '../../../services/visit-request.service';
import { RoomService } from '../../../services/room.service';
import { User } from '../../../models/user.model';
import { RoommateHostProfile, RenterProfile } from '../../../models/profile.model';
import { Match } from '../../../models/swipe.model';
import { VisitRequest } from '../../../models/visit-request.model';
import { Room } from '../../../models/room.model';

@Component({
  selector: 'app-roommate-host-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './roommate-host-dashboard.component.html',
  styleUrls: ['./roommate-host-dashboard.component.css']
})
export class RoommateHostDashboardComponent implements OnInit {
  currentUser: User | null = null;
  hostProfile: RoommateHostProfile | null = null;
  availableRenters: RenterProfile[] = [];
  matches: Match[] = [];
  visitRequests: VisitRequest[] = [];
  rooms: Room[] = [];
  loading = {
    user: true,
    profile: true,
    renters: true,
    matches: true,
    visits: true,
    rooms: true
  };
  error = {
    user: '',
    profile: '',
    renters: '',
    matches: '',
    visits: '',
    rooms: ''
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private profileService: ProfileService,
    private swipeService: SwipeService,
    private visitRequestService: VisitRequestService,
    private roomService: RoomService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    this.loadAvailableRenters();
    this.loadMatches();
    this.loadVisitRequests();
  }

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.loading.user = false;

        // After loading user, load the host profile
        if (user && user.id) {
          this.loadHostProfile(user.id);
        }
      },
      error: (error) => {
        this.error.user = 'Failed to load user data';
        this.loading.user = false;
        console.error('Error loading user data:', error);
      }
    });
  }

  private loadHostProfile(userId: number): void {
    this.profileService.getRoommateHostProfile(userId).subscribe({
      next: (profile) => {
        this.hostProfile = profile;
        this.loading.profile = false;
        // Load rooms after host profile is loaded
        this.loadRooms();
      },
      error: (error) => {
        this.error.profile = 'Failed to load host profile';
        this.loading.profile = false;
        console.error('Error loading host profile:', error);
      }
    });
  }

  private loadAvailableRenters(): void {
    this.profileService.getAvailableRenters().subscribe({
      next: (renters) => {
        this.availableRenters = renters;
        this.loading.renters = false;
      },
      error: (error) => {
        this.error.renters = 'Failed to load available renters';
        this.loading.renters = false;
        console.error('Error loading renters:', error);
      }
    });
  }

  private loadMatches(): void {
    this.swipeService.getHostMatches().subscribe({
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
    this.visitRequestService.getVisitRequestsForRoommateHost().subscribe({
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

  private loadRooms(): void {
    if (this.hostProfile && this.hostProfile.id) {
      this.roomService.getRoomsByHostId(this.hostProfile.id).subscribe({
        next: (rooms) => {
          this.rooms = rooms;
          this.loading.rooms = false;
        },
        error: (error) => {
          this.error.rooms = 'Failed to load rooms';
          this.loading.rooms = false;
          console.error('Error loading rooms:', error);
        }
      });
    } else {
      // If host profile is not loaded yet, wait for it
      this.loading.rooms = true;
    }
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
    const updateRequest = { status: 'REJECTED' as any, responseMessage: 'Sorry, the room is not available at the requested time.' };
    this.visitRequestService.updateVisitRequest(visitId, updateRequest).subscribe({
      next: () => {
        // Update the local visit request status
        const visitIndex = this.visitRequests.findIndex(visit => visit.id === visitId);
        if (visitIndex !== -1) {
          this.visitRequests[visitIndex].status = 'REJECTED' as any;
          this.visitRequests[visitIndex].responseMessage = 'Sorry, the room is not available at the requested time.';
        }
      },
      error: (error) => {
        console.error('Error rejecting visit request:', error);
      }
    });
  }
}
